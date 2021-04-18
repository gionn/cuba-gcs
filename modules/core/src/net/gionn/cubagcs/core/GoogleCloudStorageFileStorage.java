/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gionn.cubagcs.core;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.sys.events.AppContextStartedEvent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static com.haulmont.bali.util.Preconditions.checkNotNullArgument;

public class GoogleCloudStorageFileStorage implements FileStorageAPI {

    private static final Logger log = LoggerFactory.getLogger( GoogleCloudStorageFileStorage.class );

    @Inject
    protected GoogleCloudStorageConfig googleCloudStorageConfig;

    protected AtomicReference<Storage> storageAtomicReference = new AtomicReference<>();

    @EventListener
    public void getClient( AppContextStartedEvent event )
    {
        getClient();
    }

    public void getClient()
    {
        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setProjectId( googleCloudStorageConfig.getProjectId() )
                .build();
        storageAtomicReference.set( storageOptions.getService() );
    }

    @Override
    public long saveStream(FileDescriptor file, InputStream inputStream) throws FileStorageException {
        Preconditions.checkNotNullArgument(file.getSize());
        try {
            saveFile(file, IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            String message = String.format("Could not save file %s.",
                    getFileName(file));
            throw new FileStorageException(FileStorageException.Type.IO_EXCEPTION, message);
        }
        return file.getSize();
    }

    @Override
    public void saveFile( FileDescriptor file, byte[] data ) throws FileStorageException
    {
        checkNotNullArgument( data, "File content is null" );
        Storage storage = storageAtomicReference.get();
        try
        {
            String objectName = resolveFileName( file );

            BlobId blobId = BlobId.of( googleCloudStorageConfig.getBucket(), objectName );
            BlobInfo blobInfo = BlobInfo.newBuilder( blobId )
                    .build();
            storage.create( blobInfo, data );
            log.info( "Created file {}", objectName );
        }
        catch ( Exception e )
        {
            String message = String.format( "Could not save file %s.", getFileName( file ) );
            throw new FileStorageException( FileStorageException.Type.IO_EXCEPTION, message, e );
        }
    }

    @Override
    public void removeFile( FileDescriptor file ) throws FileStorageException
    {
        Storage storage = storageAtomicReference.get();
        try
        {
            String objectName = resolveFileName( file );
            storage.delete( googleCloudStorageConfig.getBucket(), objectName );
            log.info( "Deleted file {}", objectName );
        }
        catch ( Exception e )
        {
            String message = String.format( "Could not delete file %s.", getFileName( file ) );
            throw new FileStorageException( FileStorageException.Type.IO_EXCEPTION, message, e );
        }
    }

    @Override
    public InputStream openStream( FileDescriptor file ) throws FileStorageException
    {
        Storage storage = storageAtomicReference.get();

        try
        {
            ReadChannel reader = storage.reader( googleCloudStorageConfig.getBucket(), resolveFileName( file ) );
            return Channels.newInputStream( reader );
        }
        catch ( Exception e )
        {
            String message = String.format( "Could not load file %s.", getFileName( file ) );
            throw new FileStorageException( FileStorageException.Type.IO_EXCEPTION, message );
        }
    }

    @Override
    public byte[] loadFile(FileDescriptor file) throws FileStorageException {
        try (InputStream inputStream = openStream(file)) {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new FileStorageException(FileStorageException.Type.IO_EXCEPTION, file.getId().toString(), e);
        }
    }

    @Override
    public boolean fileExists(FileDescriptor file)
    {
        Storage storage = storageAtomicReference.get();
        return storage.get( googleCloudStorageConfig.getBucket(), resolveFileName( file ) ) != null;
    }

    private String resolveFileName(FileDescriptor file) {
        return getStorageDir(file.getCreateDate()) + "/" + getFileName(file);
    }

    private String getStorageDir(Date createDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return String.format("%d/%s/%s", year,
                StringUtils.leftPad(String.valueOf(month), 2, '0'),
                StringUtils.leftPad(String.valueOf(day), 2, '0'));
    }

    private String getFileName( FileDescriptor fileDescriptor )
    {
        if ( StringUtils.isNotBlank( fileDescriptor.getExtension() ) )
        {
            return fileDescriptor.getId()
                    .toString() + "." + fileDescriptor.getExtension();
        }
        else
        {
            return fileDescriptor.getId()
                    .toString();
        }
    }
}

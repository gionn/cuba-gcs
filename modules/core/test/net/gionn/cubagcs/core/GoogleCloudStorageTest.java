package net.gionn.cubagcs.core;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import net.gionn.cubagcs.CubagcsTestContainer;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class GoogleCloudStorageTest
{
    private static final String FILE_CONTENT = "This is a test payload for automated tests of cuba GCS addon.";

    @RegisterExtension
    public static CubagcsTestContainer container = CubagcsTestContainer.Common.INSTANCE;

    protected GoogleCloudStorageFileStorage fileStorageAPI;

    protected FileDescriptor fileDescr;

    @BeforeEach
    public void setUp() throws Exception {

        fileDescr = new FileDescriptor();
        fileDescr.setCreateDate(new Date());
        fileDescr.setSize((long) FILE_CONTENT.length());
        fileDescr.setName("AmazonFileStorageTest");
        fileDescr.setExtension("txt");

        fileStorageAPI = new GoogleCloudStorageFileStorage();
        fileStorageAPI.googleCloudStorageConfig = AppBeans.get( Configuration.class).getConfig(GoogleCloudStorageConfig.class);
        fileStorageAPI.initClient();

        assertNotNull(fileStorageAPI.googleCloudStorageConfig.getBucket());
        assertNotNull(fileStorageAPI.googleCloudStorageConfig.getProjectId());
    }

    @Test
    public void testWithExtension() throws Exception {
        fileStorageAPI.saveFile(fileDescr, FILE_CONTENT.getBytes());

        assertTrue(fileStorageAPI.fileExists(fileDescr));

        InputStream inputStream = fileStorageAPI.openStream(fileDescr);
        assertEquals(FILE_CONTENT, IOUtils.toString(inputStream, StandardCharsets.UTF_8));

        fileStorageAPI.removeFile(fileDescr);

        assertFalse(fileStorageAPI.fileExists(fileDescr));
    }
}

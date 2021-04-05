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

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;

/**
 * Configuration parameters interface used by cubaaws add-on.
 */
@Source(type = SourceType.DATABASE)
public interface GoogleCloudStorageConfig extends Config {

    /**
     * @return Google Cloud Storage project id.
     */
    @Property("cuba.gcs.projectId")
    String getProjectId();

    /**
     * @return Google Cloud Storage bucket name.
     */
    @Property("cuba.gcs.bucket")
    String getBucket();
}

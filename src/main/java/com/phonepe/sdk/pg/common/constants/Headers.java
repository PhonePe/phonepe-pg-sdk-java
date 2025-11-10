/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.phonepe.sdk.pg.common.constants;

import java.io.InputStream;
import java.util.Properties;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class Headers {

    private final Properties properties = new Properties();
    private final String PROPERTIES_FILE_NAME = "/sdk.properties";

    static {
        try (InputStream input = Headers.class.getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (input == null) {
                log.error("Could not find {}", PROPERTIES_FILE_NAME);
            } else {
                properties.load(input);
            }
        } catch (Exception e) {
            log.error("Failed to load SDK properties: {}", e.getMessage());
        }
    }

    public static final String API_VERSION = "V2";
    public static final String SUBSCRIPTION_API_VERSION = properties.getProperty("sdk.version");
    public static final String INTEGRATION = "API";
    public static final String SDK_VERSION = properties.getProperty("sdk.version");
    public static final String SDK_TYPE = "BACKEND_JAVA_SDK";
    public static final String SOURCE = "x-source";
    public static final String SOURCE_VERSION = "x-source-version";
    public static final String SOURCE_PLATFORM = "x-source-platform";
    public static final String SOURCE_PLATFORM_VERSION = "x-source-platform-version";
    public static final String OAUTH_AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
}

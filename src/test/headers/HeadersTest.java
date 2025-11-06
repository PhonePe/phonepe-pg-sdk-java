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
package headers;

import org.junit.jupiter.api.Assertions;

import com.phonepe.sdk.pg.common.constants.Headers;
import java.io.InputStream;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class HeadersTest {

    /**
     * Test that SDK_VERSION is loaded from properties file and not null
     */
    @Test
    void testSDKVersionIsLoadedFromProperties() {
        Assertions.assertNotNull(Headers.SDK_VERSION, "SDK_VERSION should not be null");
        Assertions.assertFalse(Headers.SDK_VERSION.isEmpty(), "SDK_VERSION should not be empty");
    }

    /**
     * Test that SDK_VERSION follows semantic versioning format (e.g., 2.1.8)
     */
    @Test
    void testSDKVersionFormat() {
        Assertions.assertTrue(
                Headers.SDK_VERSION.matches("\\d+\\.\\d+\\.\\d+"),
                "SDK_VERSION should match semantic version format (X.Y.Z). Got: "
                        + Headers.SDK_VERSION);
    }

    /**
     * Test that SUBSCRIPTION_API_VERSION is loaded from properties file and not null
     */
    @Test
    void testSubscriptionAPIVersionIsLoadedFromProperties() {
        Assertions.assertNotNull(
                Headers.SUBSCRIPTION_API_VERSION, "SUBSCRIPTION_API_VERSION should not be null");
        Assertions.assertFalse(
                Headers.SUBSCRIPTION_API_VERSION.isEmpty(),
                "SUBSCRIPTION_API_VERSION should not be empty");
    }

    /**
     * Test that both SDK_VERSION and SUBSCRIPTION_API_VERSION have the same value
     * since they both load from the same property
     */
    @Test
    void testSDKVersionMatchesSubscriptionAPIVersion() {
        Assertions.assertEquals(
                Headers.SDK_VERSION,
                Headers.SUBSCRIPTION_API_VERSION,
                "SDK_VERSION and SUBSCRIPTION_API_VERSION should have the same value");
    }

    /**
     * Test that properties file exists and is accessible in classpath
     */
    @Test
    void testPropertiesFileExists() {
        InputStream input = getClass().getResourceAsStream("/sdk.properties");
        Assertions.assertNotNull(input, "sdk.properties file should exist in classpath");
    }

    /**
     * Test that properties file has been filtered by Maven
     * (i.e., ${project.version} has been replaced with actual version)
     */
    @Test
    void testPropertiesFileIsFiltered() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/sdk.properties")) {
            Assertions.assertNotNull(input, "sdk.properties should be available");
            properties.load(input);

            String version = properties.getProperty("sdk.version");
            Assertions.assertNotNull(version, "sdk.version property should exist");
            Assertions.assertFalse(
                    version.contains("${"),
                    "sdk.version should not contain unresolved Maven placeholders like ${project.version}. Got: "
                            + version);
            Assertions.assertTrue(
                    version.matches("\\d+\\.\\d+\\.\\d+"),
                    "sdk.version should be in semantic version format. Got: " + version);
        }
    }

    /**
     * Test that all header constants are not null
     */
    @Test
    void testAllConstantsAreNotNull() {
        Assertions.assertNotNull(Headers.API_VERSION, "API_VERSION should not be null");
        Assertions.assertNotNull(
                Headers.SUBSCRIPTION_API_VERSION, "SUBSCRIPTION_API_VERSION should not be null");
        Assertions.assertNotNull(Headers.INTEGRATION, "INTEGRATION should not be null");
        Assertions.assertNotNull(Headers.SDK_VERSION, "SDK_VERSION should not be null");
        Assertions.assertNotNull(Headers.SDK_TYPE, "SDK_TYPE should not be null");
        Assertions.assertNotNull(Headers.SOURCE, "SOURCE should not be null");
        Assertions.assertNotNull(Headers.SOURCE_VERSION, "SOURCE_VERSION should not be null");
        Assertions.assertNotNull(Headers.SOURCE_PLATFORM, "SOURCE_PLATFORM should not be null");
        Assertions.assertNotNull(
                Headers.SOURCE_PLATFORM_VERSION, "SOURCE_PLATFORM_VERSION should not be null");
        Assertions.assertNotNull(Headers.OAUTH_AUTHORIZATION, "OAUTH_AUTHORIZATION should not be null");
        Assertions.assertNotNull(Headers.CONTENT_TYPE, "CONTENT_TYPE should not be null");
        Assertions.assertNotNull(Headers.ACCEPT, "ACCEPT should not be null");
    }

    /**
     * Test that SDK_VERSION matches the expected format and contains valid version parts
     */
    @Test
    void testSDKVersionComponents() {
        String[] versionParts = Headers.SDK_VERSION.split("\\.");
        Assertions.assertEquals(
                3,
                versionParts.length,
                "SDK_VERSION should have 3 parts (major.minor.patch). Got: "
                        + Headers.SDK_VERSION);

        // Each part should be a number
        for (int i = 0; i < versionParts.length; i++) {
            Assertions.assertTrue(
                    versionParts[i].matches("\\d+"),
                    "Version part " + i + " should be numeric. Got: " + versionParts[i]);
        }
    }

    /**
     * Test that Header values don't contain common problematic characters
     */
    @Test
    void testHeaderValuesDoNotContainProblematicCharacters() {
        // Version values should not contain whitespace or special characters
        Assertions.assertFalse(
                Headers.SDK_VERSION.contains(" "),
                "SDK_VERSION should not contain whitespace");
        Assertions.assertFalse(
                Headers.SUBSCRIPTION_API_VERSION.contains(" "),
                "SUBSCRIPTION_API_VERSION should not contain whitespace");
        Assertions.assertFalse(
                Headers.API_VERSION.contains(" "), "API_VERSION should not contain whitespace");
    }

    /**
     * Integration test: Verify that properties loaded in static block
     * match what can be read directly from the file
     */
    @Test
    void testStaticBlockPropertiesMatchFileContent() throws Exception {
        Properties fileProperties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/sdk.properties")) {
            fileProperties.load(input);
        }

        String fileVersion = fileProperties.getProperty("sdk.version");
        Assertions.assertEquals(
                Headers.SDK_VERSION,
                fileVersion,
                "SDK_VERSION from Headers should match sdk.version from file");
        Assertions.assertEquals(
                Headers.SUBSCRIPTION_API_VERSION,
                fileVersion,
                "SUBSCRIPTION_API_VERSION from Headers should match sdk.version from file");
    }
}

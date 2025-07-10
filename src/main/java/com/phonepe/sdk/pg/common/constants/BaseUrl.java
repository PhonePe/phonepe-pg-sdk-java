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

import lombok.experimental.UtilityClass;

@UtilityClass
public class BaseUrl {

    public static final String PRODUCTION_PG_HOST_URL = "https://api.phonepe.com/apis/pg";
    public static final String SANDBOX_PG_HOST_URL =
            "https://api-preprod.phonepe.com/apis/pg-sandbox";

    public static final String PRODUCTION_OAUTH_HOST_URL =
            "https://api.phonepe.com/apis/identity-manager";
    public static final String SANDBOX_OAUTH_HOST_URL =
            "https://api-preprod.phonepe.com/apis/pg-sandbox";

    public static final String PRODUCTION_EVENTS_HOST_URL =
            "https://api.phonepe.com/apis/pg-ingestion";
    public static final String SANDBOX_EVENTS_HOST_URL = "http://localhost";

    public static final String TESTING_URL = "http://localhost:30419";
}

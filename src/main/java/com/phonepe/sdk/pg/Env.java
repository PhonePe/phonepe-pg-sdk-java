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
package com.phonepe.sdk.pg;

import com.phonepe.sdk.pg.common.constants.BaseUrl;
import lombok.Getter;

@Getter
public enum Env {
    SANDBOX(
            BaseUrl.SANDBOX_PG_HOST_URL,
            BaseUrl.SANDBOX_OAUTH_HOST_URL,
            BaseUrl.SANDBOX_EVENTS_HOST_URL),
    PRODUCTION(
            BaseUrl.PRODUCTION_PG_HOST_URL,
            BaseUrl.PRODUCTION_OAUTH_HOST_URL,
            BaseUrl.PRODUCTION_EVENTS_HOST_URL),
    TEST(BaseUrl.TESTING_URL, BaseUrl.TESTING_URL, BaseUrl.TESTING_URL);

    private final String pgHostUrl;
    private final String oAuthHostUrl;
    private final String eventsHostUrl;

    Env(String pgHostUrl, String oAuthHostUrl, String eventsHostUrl) {
        this.pgHostUrl = pgHostUrl;
        this.oAuthHostUrl = oAuthHostUrl;
        this.eventsHostUrl = eventsHostUrl;
    }
}

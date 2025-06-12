package com.phonepe.sdk.pg;

import com.phonepe.sdk.pg.common.constants.BaseUrl;
import lombok.Getter;


@Getter
public enum Env {
    SANDBOX(BaseUrl.SANDBOX_PG_HOST_URL, BaseUrl.SANDBOX_OAUTH_HOST_URL, BaseUrl.SANDBOX_EVENTS_HOST_URL),
    PRODUCTION(BaseUrl.PRODUCTION_PG_HOST_URL, BaseUrl.PRODUCTION_OAUTH_HOST_URL, BaseUrl.PRODUCTION_EVENTS_HOST_URL),
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

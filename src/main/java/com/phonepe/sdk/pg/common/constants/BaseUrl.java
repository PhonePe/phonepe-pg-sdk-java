package com.phonepe.sdk.pg.common.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BaseUrl {

    public static final String PRODUCTION_PG_HOST_URL = "https://api.phonepe.com/apis/pg";
    public static final String SANDBOX_PG_HOST_URL = "https://api-preprod.phonepe.com/apis/pg-sandbox";

    public static final String PRODUCTION_OAUTH_HOST_URL = "https://api.phonepe.com/apis/identity-manager";
    public static final String SANDBOX_OAUTH_HOST_URL = "https://api-preprod.phonepe.com/apis/pg-sandbox";

    public static final String PRODUCTION_EVENTS_HOST_URL = "https://api.phonepe.com/apis/pg-ingestion";
    public static final String SANDBOX_EVENTS_HOST_URL = "http://locahost";

    public static final String TESTING_URL = "http://localhost:30419";

}

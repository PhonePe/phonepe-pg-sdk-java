package com.phonepe.sdk.pg.common.events;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    public static final int MAX_EVENTS_IN_BATCH = 10;
    public static final String SOURCE = "BACKEND_SDK";
    public static final String CLIENT_VERSION = "v2";
    public static final String AUTHORIZATION = "Authorization";

    public static final String EVENTS_ENDPOINT = "/client/v1/backend/events/batch";
    public static final int QUEUE_MAX_SIZE = 20000;  // Should be greater than MAX_EVENTS_IN_BATCH
    public static final int INITIAL_DELAY = 1;
    public static final int DELAY = 1;
}

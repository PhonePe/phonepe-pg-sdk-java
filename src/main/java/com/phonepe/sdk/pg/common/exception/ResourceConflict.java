package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;

public class ResourceConflict extends ClientError {

    /**
     * 409 Conflict
     */
    public ResourceConflict(Integer responseCode, String message,
            PhonePeResponse phonePeResponse) {
        super(responseCode, message, phonePeResponse);
    }
}

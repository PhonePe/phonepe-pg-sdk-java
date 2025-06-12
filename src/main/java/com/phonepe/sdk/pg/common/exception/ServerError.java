package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;

public class ServerError extends PhonePeException {

    /**
     * 5xx Server Error
     */
    public ServerError(Integer responseCode, String message, PhonePeResponse phonePeResponse) {
        super(responseCode, message, phonePeResponse);
    }
}

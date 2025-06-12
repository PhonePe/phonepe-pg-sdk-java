package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;


public class BadRequest extends ClientError {

    /**
     * 400 Bad Request
     */
    public BadRequest(Integer responseCode, String message, PhonePeResponse phonePeResponse) {
        super(responseCode, message, phonePeResponse);
    }
}

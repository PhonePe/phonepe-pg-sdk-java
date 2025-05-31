package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;

public class ResourceNotFound extends ClientError {

    /**
     * 404 Not Found
     */
    public ResourceNotFound(Integer responseCode, String message,
            PhonePeResponse phonePeResponse) {

        super(responseCode, message, phonePeResponse);
    }
}

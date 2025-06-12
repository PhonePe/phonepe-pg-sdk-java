package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;

public class ResourceInvalid extends ClientError {

    /**
     * 422 Invalid
     */
    public ResourceInvalid(Integer responseCode, String message, PhonePeResponse phonePeResponse) {
        super(responseCode, message, phonePeResponse);
    }
}

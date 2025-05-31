package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;

public class ForbiddenAccess extends ClientError {

    /**
     * 403 Forbidden
     */
    public ForbiddenAccess(Integer responseCode, String message, PhonePeResponse phonePeResponse) {
        super(responseCode, message, phonePeResponse);
    }
}

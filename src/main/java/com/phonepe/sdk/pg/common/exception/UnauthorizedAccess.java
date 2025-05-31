package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;

public class UnauthorizedAccess extends ClientError {

    /**
     * 401 Unauthorized
     */
    public UnauthorizedAccess(Integer responseCode, String message,
            PhonePeResponse phonePeResponse) {
        super(responseCode, message, phonePeResponse);
    }

}

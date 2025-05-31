package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;

public class ExpectationFailed extends ClientError {

    /**
     * 417 Expectation failed
     */
    public ExpectationFailed(Integer responseCode, String message,
            PhonePeResponse phonePeResponse) {
        super(responseCode, message, phonePeResponse);
    }
}

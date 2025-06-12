package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;

public class TooManyRequest extends ClientError {

    //Too Many Request
    public TooManyRequest(Integer responseCode, String message,
            PhonePeResponse phonePeResponse) {
        super(responseCode, message, phonePeResponse);
    }
}

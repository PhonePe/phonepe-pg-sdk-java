package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;

public class ResourceGone extends ClientError {

    /**
     * 410 Gone
     */
    public ResourceGone(Integer responseCode, String message, PhonePeResponse phonePeResponse) {
        super(responseCode, message, phonePeResponse);
    }
}

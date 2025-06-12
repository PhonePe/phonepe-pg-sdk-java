package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhonePeException extends RuntimeException {

    private Integer httpStatusCode;
    private String message;
    private transient Map<String, Object> data;
    private String code;

    public PhonePeException(String message) {
        this.message = message;
    }

    public PhonePeException(Integer httpStatusCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    public PhonePeException(Integer httpStatusCode, String message,
            PhonePeResponse phonePeResponse) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
        if (phonePeResponse != null) {
            this.message = phonePeResponse.getMessage() != null ? phonePeResponse.getMessage() : message;
            this.data = phonePeResponse.getData();
            this.code =
                    phonePeResponse.getErrorCode() != null ? phonePeResponse.getErrorCode() : phonePeResponse.getCode();
            // keeping only the `errorCode` field for auth exception (ignoring code)
        }
    }

    public String toString() {
        return getClass() + "\n"
                + "httpStatusCode: " + httpStatusCode + "\n"
                + "message: " + message + "\n"
                + "data: " + data + "\n"
                + "code : " + code + "\n";
    }

}
/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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

    public PhonePeException(
            Integer httpStatusCode, String message, PhonePeResponse phonePeResponse) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
        if (phonePeResponse != null) {
            this.message =
                    phonePeResponse.getMessage() != null ? phonePeResponse.getMessage() : message;
            this.data = phonePeResponse.getData();
            this.code =
                    phonePeResponse.getErrorCode() != null
                            ? phonePeResponse.getErrorCode()
                            : phonePeResponse.getCode();
            // keeping only the `errorCode` field for auth exception (ignoring code)
        }
    }

    public String toString() {
        return getClass()
                + "\n"
                + "httpStatusCode: "
                + httpStatusCode
                + "\n"
                + "message: "
                + message
                + "\n"
                + "data: "
                + data
                + "\n"
                + "code : "
                + code
                + "\n";
    }
}

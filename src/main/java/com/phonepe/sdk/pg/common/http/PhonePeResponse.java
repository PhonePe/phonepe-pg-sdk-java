package com.phonepe.sdk.pg.common.http;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhonePeResponse {

    private boolean success;
    private String code;
    private String message;
    @JsonAlias({"data", "context"})
    private Map<String, Object> data;
    private String errorCode;
}

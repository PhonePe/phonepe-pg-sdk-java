package com.phonepe.sdk.pg.payments.v2.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardCheckoutPayResponse {

    private String orderId;
    private String state;
    private long expireAt;
    private String redirectUrl;
}

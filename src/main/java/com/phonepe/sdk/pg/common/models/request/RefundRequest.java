package com.phonepe.sdk.pg.common.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class RefundRequest {

    private String merchantRefundId;
    private String originalMerchantOrderId;
    private long amount;
}

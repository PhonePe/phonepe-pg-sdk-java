package com.phonepe.sdk.pg.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.phonepe.sdk.pg.common.models.MetaInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderStatusResponse {

    private String merchantId;
    private String merchantOrderId;
    private String orderId;
    private String state;
    private long amount;
    private PaymentFlowResponse paymentFlow;
    private long payableAmount;
    private long feeAmount;
    private long expireAt;
    private String errorCode;
    private String detailedErrorCode;
    private MetaInfo metaInfo;
    private List<PaymentDetail> paymentDetails;
}

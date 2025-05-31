package com.phonepe.sdk.pg.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class RefundStatusResponse {

    private String merchantId;
    private String merchantRefundId;
    private String originalMerchantOrderId;
    private long amount;
    private String state;
    private List<PaymentRefundDetail> paymentDetails;
}

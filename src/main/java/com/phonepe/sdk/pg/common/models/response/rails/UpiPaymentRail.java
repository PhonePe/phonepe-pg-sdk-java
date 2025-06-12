package com.phonepe.sdk.pg.common.models.response.rails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpiPaymentRail extends PaymentRail {

    private String utr;
    private String upiTransactionId;
    private String vpa;
    private String umn;

    public UpiPaymentRail() {
        super(PaymentRailType.UPI);
    }

    @Builder
    public UpiPaymentRail(String utr, String upiTransactionId, String vpa, String umn) {
        super(PaymentRailType.UPI);
        this.vpa = vpa;
        this.utr = utr;
        this.upiTransactionId = upiTransactionId;
        this.umn = umn;
    }
}

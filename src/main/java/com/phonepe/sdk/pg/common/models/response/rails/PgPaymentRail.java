package com.phonepe.sdk.pg.common.models.response.rails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PgPaymentRail extends PaymentRail {

    private String transactionId;
    private String authorizationCode;
    private String serviceTransactionId;

    public PgPaymentRail() {
        super(PaymentRailType.PG);
    }

    @Builder
    public PgPaymentRail(String transactionId, String authorizationCode, String serviceTransactionId) {
        super(PaymentRailType.PG);
        this.transactionId = transactionId;
        this.authorizationCode = authorizationCode;
        this.serviceTransactionId = serviceTransactionId;
    }
}

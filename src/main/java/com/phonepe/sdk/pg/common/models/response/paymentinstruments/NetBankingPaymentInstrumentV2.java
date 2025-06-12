package com.phonepe.sdk.pg.common.models.response.paymentinstruments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetBankingPaymentInstrumentV2 extends PaymentInstrumentV2 {

    private String bankTransactionId;
    private String bankId;
    private String brn;
    private String arn;

    public NetBankingPaymentInstrumentV2() {
        super(PaymentInstrumentType.NET_BANKING);
    }

    @Builder
    public NetBankingPaymentInstrumentV2(String bankTransactionId, String bankId, String brn, String arn) {
        super(PaymentInstrumentType.NET_BANKING);
        this.bankTransactionId = bankTransactionId;
        this.bankId = bankId;
        this.brn = brn;
        this.arn = arn;
    }
}

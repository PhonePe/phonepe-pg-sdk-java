package com.phonepe.sdk.pg.common.models.response.paymentinstruments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletPaymentInstrumentV2 extends PaymentInstrumentV2 {

    private String walletId;

    public WalletPaymentInstrumentV2() {
        super(PaymentInstrumentType.WALLET);
    }

    @Builder
    public WalletPaymentInstrumentV2(String walletId) {
        super(PaymentInstrumentType.WALLET);
        this.walletId = walletId;
    }

}

package com.phonepe.sdk.pg.common.models.response.paymentinstruments;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "WALLET", value = WalletPaymentInstrumentV2.class),
        @JsonSubTypes.Type(name = "EGV", value = EGVPaymentInstrumentV2.class),
        @JsonSubTypes.Type(name = "NET_BANKING", value = NetBankingPaymentInstrumentV2.class),
        @JsonSubTypes.Type(name = "ACCOUNT", value = AccountPaymentInstrumentV2.class),
        @JsonSubTypes.Type(name = "CREDIT_CARD", value = CreditCardPaymentInstrumentV2.class),
        @JsonSubTypes.Type(name = "DEBIT_CARD", value = DebitCardPaymentInstrumentV2.class)})
@NoArgsConstructor
@Data
public abstract class PaymentInstrumentV2 {

    private PaymentInstrumentType type;

    public PaymentInstrumentV2(PaymentInstrumentType type) {
        this.type = type;
    }

    public enum PaymentInstrumentType {
        WALLET,
        EGV,
        ACCOUNT,
        CREDIT_CARD,
        DEBIT_CARD,
        NET_BANKING
    }
}

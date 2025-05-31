package com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CardPaymentModeConstraint.class, name = "CARD"),
        @JsonSubTypes.Type(value = NetBankingPaymentModeConstraint.class, name = "NET_BANKING"),
        @JsonSubTypes.Type(value = UpiIntentPaymentModeConstraint.class, name = "UPI_INTENT"),
        @JsonSubTypes.Type(value = UpiQrPaymentModeConstraint.class, name = "UPI_QR"),
        @JsonSubTypes.Type(value = UpiCollectPaymentModeConstraint.class, name = "UPI_COLLECT")
})
public abstract class PaymentModeConstraint {

    protected PgV2InstrumentType type;
}
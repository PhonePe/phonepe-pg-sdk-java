package com.phonepe.sdk.pg.common.models.response.rails;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "UPI", value = UpiPaymentRail.class),
        @JsonSubTypes.Type(name = "PG", value = PgPaymentRail.class),
        @JsonSubTypes.Type(name = "PPI_WALLET", value = PpiWalletPaymentRail.class),
        @JsonSubTypes.Type(name = "PPI_EGV", value = PpiEgvPaymentRail.class)
})
@NoArgsConstructor
@Data
public abstract class PaymentRail {

    private PaymentRailType type;

    public PaymentRail(PaymentRailType type) {
        this.type = type;
    }

    public enum PaymentRailType {
        UPI,
        PG,
        PPI_WALLET,
        PPI_EGV
    }
}

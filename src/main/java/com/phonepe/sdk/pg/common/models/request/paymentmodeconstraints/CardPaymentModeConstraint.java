package com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class CardPaymentModeConstraint extends PaymentModeConstraint {

    private final Set<CardType> cardTypes;

    @Builder
    public CardPaymentModeConstraint(Set<CardType> cardTypes) {
        super(PgV2InstrumentType.CARD);
        this.cardTypes = cardTypes;
    }

}
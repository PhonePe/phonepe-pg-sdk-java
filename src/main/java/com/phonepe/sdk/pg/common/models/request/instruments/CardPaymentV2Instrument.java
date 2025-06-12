package com.phonepe.sdk.pg.common.models.request.instruments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class CardPaymentV2Instrument extends PaymentV2Instrument {

    private String authMode;
    private NewCardDetails cardDetails;
    private boolean savedCard;
    private String merchantUserId;

    @Builder
    public CardPaymentV2Instrument(String authMode, String merchantUserId, NewCardDetails cardDetails,
            boolean savedCard) {
        super(PgV2InstrumentType.CARD);
        this.authMode = authMode;
        this.cardDetails = cardDetails;
        this.savedCard = savedCard;
        this.merchantUserId = merchantUserId;
    }
}

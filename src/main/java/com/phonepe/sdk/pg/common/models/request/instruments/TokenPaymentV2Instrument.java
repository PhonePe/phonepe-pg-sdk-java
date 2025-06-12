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
public class TokenPaymentV2Instrument extends PaymentV2Instrument {

    private String authMode;
    private TokenDetails tokenDetails;
    private String merchantUserId;

    @Builder
    public TokenPaymentV2Instrument(String authMode, TokenDetails tokenDetails, String merchantUserId) {
        super(PgV2InstrumentType.TOKEN);
        this.authMode = authMode;
        this.tokenDetails = tokenDetails;
        this.merchantUserId = merchantUserId;
    }
}

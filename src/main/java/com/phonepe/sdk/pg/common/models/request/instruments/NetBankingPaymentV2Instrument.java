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
public class NetBankingPaymentV2Instrument extends PaymentV2Instrument {

    private String bankId;
    private String merchantUserId;

    @Builder
    public NetBankingPaymentV2Instrument(String bankId, String merchantUserId) {
        super(PgV2InstrumentType.NET_BANKING);
        this.bankId = bankId;
        this.merchantUserId = merchantUserId;
    }
}

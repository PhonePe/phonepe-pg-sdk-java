package com.phonepe.sdk.pg.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.PaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.rails.PaymentRail;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRefundDetail {

    private String transactionId;
    private PgV2InstrumentType paymentMode;
    private long timestamp;
    private long amount;
    private String state;
    private String errorCode;
    private String detailedErrorCode;
    private PaymentInstrumentV2 instrument;
    private PaymentRail rail;
    private List<InstrumentCombo> splitInstruments;
}

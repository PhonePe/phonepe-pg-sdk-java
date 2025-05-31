package com.phonepe.sdk.pg.common.models.request.instruments;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NewCardDetails {

    private String encryptedCardNumber;
    private Long encryptionKeyId;
    private String cardHolderName;
    private Expiry expiry;
    private String encryptedCvv;
    private BillingAddress billingAddress;
}

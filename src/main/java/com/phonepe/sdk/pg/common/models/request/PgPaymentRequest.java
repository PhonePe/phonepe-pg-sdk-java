/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.phonepe.sdk.pg.common.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.phonepe.sdk.pg.common.models.MetaInfo;
import com.phonepe.sdk.pg.common.models.request.instruments.CardPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.CollectPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.Expiry;
import com.phonepe.sdk.pg.common.models.request.instruments.IntentPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.NetBankingPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.NewCardDetails;
import com.phonepe.sdk.pg.common.models.request.instruments.PhoneNumberCollectPaymentDetails;
import com.phonepe.sdk.pg.common.models.request.instruments.TokenDetails;
import com.phonepe.sdk.pg.common.models.request.instruments.TokenPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.UpiQrPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.VpaCollectPaymentDetails;
import com.phonepe.sdk.pg.payments.v2.models.request.MerchantUrls;
import com.phonepe.sdk.pg.payments.v2.models.request.PgPaymentFlow;
import com.phonepe.sdk.pg.subscription.v2.models.request.AmountType;
import com.phonepe.sdk.pg.subscription.v2.models.request.AuthWorkflowType;
import com.phonepe.sdk.pg.subscription.v2.models.request.Frequency;
import com.phonepe.sdk.pg.subscription.v2.models.request.RedemptionRetryStrategy;
import com.phonepe.sdk.pg.subscription.v2.models.request.SubscriptionRedemptionPaymentFlow;
import com.phonepe.sdk.pg.subscription.v2.models.request.SubscriptionSetupPaymentFlow;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class PgPaymentRequest {

    private String merchantOrderId;

    private Long amount;

    private MetaInfo metaInfo;

    private PaymentFlow paymentFlow;

    private List<InstrumentConstraint> constraints;

    private DeviceContext deviceContext;
    private Long expireAfter;
    private Long expireAt;

    private PgPaymentRequest(
            String merchantOrderId,
            Long amount,
            MetaInfo metaInfo,
            List<InstrumentConstraint> constraints,
            Long expireAfter) {
        this.merchantOrderId = merchantOrderId;
        this.amount = amount;
        this.metaInfo = metaInfo;
        this.constraints = constraints;
        this.expireAfter = expireAfter;
    }

    @Builder(
            builderClassName = "UpiIntentPayRequestBuilder",
            builderMethodName = "UpiIntentPayRequestBuilder")
    public PgPaymentRequest(
            String merchantOrderId,
            long amount,
            MetaInfo metaInfo,
            List<InstrumentConstraint> constraints,
            String deviceOS,
            String merchantCallBackScheme,
            String targetApp,
            Long expireAfter) {
        this(merchantOrderId, amount, metaInfo, constraints, expireAfter);
        this.deviceContext =
                DeviceContext.builder()
                        .deviceOS(deviceOS)
                        .merchantCallBackScheme(merchantCallBackScheme)
                        .build();
        this.paymentFlow =
                PgPaymentFlow.builder()
                        .paymentMode(
                                IntentPaymentV2Instrument.builder().targetApp(targetApp).build())
                        .build();
    }

    @Builder(
            builderClassName = "UpiCollectPayViaVpaRequestBuilder",
            builderMethodName = "UpiCollectPayViaVpaRequestBuilder")
    public PgPaymentRequest(
            long amount,
            String merchantOrderId,
            MetaInfo metaInfo,
            List<InstrumentConstraint> constraints,
            String vpa,
            String message,
            Long expireAfter) {
        this(merchantOrderId, amount, metaInfo, constraints, expireAfter);
        this.paymentFlow =
                PgPaymentFlow.builder()
                        .paymentMode(
                                CollectPaymentV2Instrument.builder()
                                        .details(
                                                VpaCollectPaymentDetails.builder().vpa(vpa).build())
                                        .message(message)
                                        .build())
                        .build();
    }

    @Builder(
            builderClassName = "UpiCollectPayViaPhoneNumberRequestBuilder",
            builderMethodName = "UpiCollectPayViaPhoneNumberRequestBuilder")
    public PgPaymentRequest(
            long amount,
            MetaInfo metaInfo,
            String merchantOrderId,
            String phoneNumber,
            List<InstrumentConstraint> constraints,
            String message,
            Long expireAfter) {
        this(merchantOrderId, amount, metaInfo, constraints, expireAfter);
        this.paymentFlow =
                PgPaymentFlow.builder()
                        .paymentMode(
                                CollectPaymentV2Instrument.builder()
                                        .details(
                                                PhoneNumberCollectPaymentDetails.builder()
                                                        .phoneNumber(phoneNumber)
                                                        .build())
                                        .message(message)
                                        .build())
                        .build();
    }

    @Builder(builderClassName = "UpiQrRequestBuilder", builderMethodName = "UpiQrRequestBuilder")
    public PgPaymentRequest(
            long amount,
            MetaInfo metaInfo,
            String merchantOrderId,
            List<InstrumentConstraint> constraints,
            Long expireAfter) {
        this(merchantOrderId, amount, metaInfo, constraints, expireAfter);
        this.paymentFlow =
                PgPaymentFlow.builder()
                        .paymentMode(UpiQrPaymentV2Instrument.builder().build())
                        .build();
    }

    @Builder(
            builderClassName = "NetBankingPayRequestBuilder",
            builderMethodName = "NetBankingPayRequestBuilder")
    public PgPaymentRequest(
            long amount,
            MetaInfo metaInfo,
            List<InstrumentConstraint> constraints,
            String merchantOrderId,
            String bankId,
            String merchantUserId,
            String redirectUrl,
            Long expireAfter) {
        this(merchantOrderId, amount, metaInfo, constraints, expireAfter);
        this.paymentFlow =
                PgPaymentFlow.builder()
                        .paymentMode(
                                NetBankingPaymentV2Instrument.builder()
                                        .bankId(bankId)
                                        .merchantUserId(merchantUserId)
                                        .build())
                        .merchantUrls(MerchantUrls.builder().redirectUrl(redirectUrl).build())
                        .build();
    }

    @Builder(
            builderClassName = "TokenPayRequestBuilder",
            builderMethodName = "TokenPayRequestBuilder")
    public PgPaymentRequest(
            String merchantOrderId,
            long amount,
            long encryptionKeyId,
            String authMode,
            String encryptedToken,
            String encryptedCvv,
            String cryptogram,
            String panSuffix,
            String expiryMonth,
            String expiryYear,
            String redirectUrl,
            String cardHolderName,
            String merchantUserId,
            MetaInfo metaInfo,
            List<InstrumentConstraint> constraints,
            Long expireAfter) {
        this(merchantOrderId, amount, metaInfo, constraints, expireAfter);
        this.paymentFlow =
                PgPaymentFlow.builder()
                        .paymentMode(
                                TokenPaymentV2Instrument.builder()
                                        .merchantUserId(merchantUserId)
                                        .authMode(authMode)
                                        .tokenDetails(
                                                TokenDetails.builder()
                                                        .cardHolderName(cardHolderName)
                                                        .cryptogram(cryptogram)
                                                        .encryptedCvv(encryptedCvv)
                                                        .encryptedToken(encryptedToken)
                                                        .encryptionKeyId(encryptionKeyId)
                                                        .panSuffix(panSuffix)
                                                        .expiry(
                                                                Expiry.builder()
                                                                        .month(expiryMonth)
                                                                        .year(expiryYear)
                                                                        .build())
                                                        .build())
                                        .build())
                        .merchantUrls(MerchantUrls.builder().redirectUrl(redirectUrl).build())
                        .build();
    }

    @Builder(
            builderClassName = "CardPayRequestBuilder",
            builderMethodName = "CardPayRequestBuilder")
    public PgPaymentRequest(
            String merchantOrderId,
            long amount,
            long encryptionKeyId,
            String authMode,
            String encryptedCardNumber,
            String encryptedCvv,
            String expiryMonth,
            String expiryYear,
            String cardHolderName,
            String merchantUserId,
            MetaInfo metaInfo,
            List<InstrumentConstraint> constraints,
            String redirectUrl,
            Long expireAfter) {
        this(merchantOrderId, amount, metaInfo, constraints, expireAfter);
        this.paymentFlow =
                PgPaymentFlow.builder()
                        .paymentMode(
                                CardPaymentV2Instrument.builder()
                                        .merchantUserId(merchantUserId)
                                        .authMode(authMode)
                                        .cardDetails(
                                                NewCardDetails.builder()
                                                        .cardHolderName(cardHolderName)
                                                        .encryptedCvv(encryptedCvv)
                                                        .encryptionKeyId(encryptionKeyId)
                                                        .encryptedCardNumber(encryptedCardNumber)
                                                        .expiry(
                                                                Expiry.builder()
                                                                        .month(expiryMonth)
                                                                        .year(expiryYear)
                                                                        .build())
                                                        .build())
                                        .build())
                        .merchantUrls(MerchantUrls.builder().redirectUrl(redirectUrl).build())
                        .build();
    }

    @Builder(
            builderClassName = "SubscriptionSetupUpiIntentBuilder",
            builderMethodName = "SubscriptionSetupUpiIntentBuilder")
    public PgPaymentRequest(
            String merchantOrderId,
            String merchantSubscriptionId,
            Long amount,
            String deviceOS,
            String merchantCallbackScheme,
            String targetApp,
            AuthWorkflowType authWorkflowType,
            Long subscriptionExpireAt,
            Long orderExpireAt,
            AmountType amountType,
            Frequency frequency,
            MetaInfo metaInfo,
            Long maxAmount,
            List<InstrumentConstraint> constraints) {
        this(merchantOrderId, amount, metaInfo, constraints, null);
        this.expireAt = orderExpireAt;
        this.deviceContext =
                DeviceContext.builder()
                        .deviceOS(deviceOS)
                        .merchantCallBackScheme(merchantCallbackScheme)
                        .build();
        this.paymentFlow =
                SubscriptionSetupPaymentFlow.builder()
                        .merchantSubscriptionId(merchantSubscriptionId)
                        .amountType(amountType)
                        .authWorkflowType(authWorkflowType)
                        .expireAt(subscriptionExpireAt)
                        .frequency(frequency)
                        .maxAmount(maxAmount)
                        .paymentMode(
                                IntentPaymentV2Instrument.builder().targetApp(targetApp).build())
                        .build();
    }

    @Builder(
            builderClassName = "SubscriptionSetupUpiCollectBuilder",
            builderMethodName = "SubscriptionSetupUpiCollectBuilder")
    public PgPaymentRequest(
            String merchantOrderId,
            String merchantSubscriptionId,
            Long amount,
            AuthWorkflowType authWorkflowType,
            Long subscriptionExpireAt,
            Long orderExpireAt,
            AmountType amountType,
            Frequency frequency,
            MetaInfo metaInfo,
            Long maxAmount,
            String vpa,
            String message,
            List<InstrumentConstraint> constraints) {
        this(merchantOrderId, amount, metaInfo, constraints, null);
        this.expireAt = orderExpireAt;
        this.paymentFlow =
                SubscriptionSetupPaymentFlow.builder()
                        .merchantSubscriptionId(merchantSubscriptionId)
                        .amountType(amountType)
                        .authWorkflowType(authWorkflowType)
                        .expireAt(subscriptionExpireAt)
                        .frequency(frequency)
                        .maxAmount(maxAmount)
                        .paymentMode(
                                CollectPaymentV2Instrument.builder()
                                        .details(
                                                VpaCollectPaymentDetails.builder().vpa(vpa).build())
                                        .message(message)
                                        .build())
                        .build();
    }

    @Builder(
            builderClassName = "SubscriptionNotifyRequestBuilder",
            builderMethodName = "SubscriptionNotifyRequestBuilder")
    public PgPaymentRequest(
            String merchantOrderId,
            Long amount,
            Long expireAt,
            MetaInfo metaInfo,
            String merchantSubscriptionId,
            boolean autoDebit,
            RedemptionRetryStrategy redemptionRetryStrategy,
            List<InstrumentConstraint> constraints) {
        this(merchantOrderId, amount, metaInfo, constraints, null);
        this.expireAt = expireAt;
        this.paymentFlow =
                SubscriptionRedemptionPaymentFlow.builder()
                        .redemptionRetryStrategy(redemptionRetryStrategy)
                        .autoDebit(autoDebit)
                        .merchantSubscriptionId(merchantSubscriptionId)
                        .build();
    }
}

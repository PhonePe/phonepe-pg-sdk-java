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
package com.phonepe.sdk.pg.common.events.models;

import com.phonepe.sdk.pg.common.events.models.enums.EventState;
import com.phonepe.sdk.pg.common.events.models.enums.EventType;
import com.phonepe.sdk.pg.common.events.models.enums.FlowType;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.request.RefundRequest;
import com.phonepe.sdk.pg.common.models.request.instruments.IntentPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.PaymentV2Instrument;
import com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest;
import com.phonepe.sdk.pg.payments.v2.models.request.PgPaymentFlow;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.subscription.v2.models.request.SubscriptionRedemptionPaymentFlow;
import com.phonepe.sdk.pg.subscription.v2.models.request.SubscriptionSetupPaymentFlow;
import java.time.Instant;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseEvent {

    private String merchantOrderId;
    private EventType eventName;
    private long eventTime;
    private EventData data;

    /** Client initialization Event */
    public static BaseEvent buildInitClientEvent(FlowType flowType, EventType eventName) {
        BaseEvent baseEvent = buildInitClientEvent(eventName);
        baseEvent.data.setFlowType(flowType);
        return baseEvent;
    }

    /** Client initialization Event */
    public static BaseEvent buildInitClientEvent(EventType eventName) {
        return BaseEvent.builder()
                .eventName(eventName)
                .eventTime(Instant.now().getEpochSecond())
                .data(EventData.builder().eventState(EventState.INITIATED).build())
                .build();
    }

    /** Pay Event Builder For Standard Checkout */
    public static BaseEvent buildStandardCheckoutPayEvent(
            EventState eventState,
            StandardCheckoutPayRequest standardCheckoutPayRequest,
            String apiPath,
            EventType eventName) {
        return BaseEvent.builder()
                .eventName(eventName)
                .eventTime(Instant.now().getEpochSecond())
                .merchantOrderId(standardCheckoutPayRequest.getMerchantOrderId())
                .data(
                        EventData.builder()
                                .eventState(eventState)
                                .amount(standardCheckoutPayRequest.getAmount())
                                .apiPath(apiPath)
                                .expireAfter(standardCheckoutPayRequest.getExpireAfter())
                                .flowType(FlowType.PG_CHECKOUT)
                                .build())
                .build();
    }

    /** Pay Failure Event for Standard Checkout --> Exception is PhonePeException */
    public static BaseEvent buildStandardCheckoutPayEvent(
            EventState eventState,
            StandardCheckoutPayRequest standardCheckoutPayRequest,
            String apiPath,
            EventType eventName,
            Exception exception) {
        BaseEvent baseEvent =
                buildStandardCheckoutPayEvent(
                        eventState, standardCheckoutPayRequest, apiPath, eventName);
        return populateExceptionFields(baseEvent, exception);
    }

    /** Pay Event Builder For Custom Checkout */
    public static BaseEvent buildCustomCheckoutPayEvent(
            EventState eventState,
            PgPaymentRequest pgPaymentRequest,
            String apiPath,
            EventType eventName) {

        // Extracting the instrument from the PgPaymentFlow
        PgPaymentFlow pgPaymentFlow = (PgPaymentFlow) pgPaymentRequest.getPaymentFlow();

        // Extracting the targetApp from the paymentMode in PgPaymentFlow
        String targetApp =
                Optional.ofNullable(pgPaymentFlow)
                        .map(PgPaymentFlow::getPaymentMode)
                        .filter(
                                paymentMode ->
                                        paymentMode.getType().equals(PgV2InstrumentType.UPI_INTENT))
                        .map(IntentPaymentV2Instrument.class::cast)
                        .map(IntentPaymentV2Instrument::getTargetApp)
                        .orElse(null);

        return BaseEvent.builder()
                .eventName(eventName)
                .eventTime(Instant.now().getEpochSecond())
                .merchantOrderId(pgPaymentRequest.getMerchantOrderId())
                .data(
                        EventData.builder()
                                .eventState(eventState)
                                .amount(pgPaymentRequest.getAmount())
                                .apiPath(apiPath)
                                .deviceContext(pgPaymentRequest.getDeviceContext())
                                .expireAfter(pgPaymentRequest.getExpireAfter())
                                .targetApp(targetApp)
                                .paymentInstrument(
                                        Optional.ofNullable(pgPaymentFlow)
                                                .map(PgPaymentFlow::getPaymentMode)
                                                .map(PaymentV2Instrument::getType)
                                                .orElse(null))
                                .flowType(FlowType.PG)
                                .build())
                .build();
    }

    /** Pay Failure Event for Custom Checkout --> Exception is PhonePeException */
    public static BaseEvent buildCustomCheckoutPayEvent(
            EventState eventState,
            PgPaymentRequest pgPaymentRequest,
            String url,
            EventType eventName,
            Exception exception) {
        BaseEvent baseEvent =
                buildCustomCheckoutPayEvent(eventState, pgPaymentRequest, url, eventName);
        return populateExceptionFields(baseEvent, exception);
    }

    /** Order Status Event Builder */
    public static BaseEvent buildOrderStatusEvent(
            EventState eventState,
            String merchantOrderId,
            String apiPath,
            FlowType flowType,
            EventType eventName) {
        return BaseEvent.builder()
                .eventName(eventName)
                .eventTime(Instant.now().getEpochSecond())
                .merchantOrderId(merchantOrderId)
                .data(
                        EventData.builder()
                                .eventState(eventState)
                                .flowType(flowType)
                                .apiPath(apiPath)
                                .build())
                .build();
    }

    /** Order Status Failure Event */
    public static BaseEvent buildOrderStatusEvent(
            EventState eventState,
            String merchantOrderId,
            String apiPath,
            FlowType flowType,
            EventType eventName,
            Exception exception) {
        BaseEvent baseEvent =
                buildOrderStatusEvent(eventState, merchantOrderId, apiPath, flowType, eventName);
        return populateExceptionFields(baseEvent, exception);
    }

    /** Refund Event Builder */
    public static BaseEvent buildRefundEvent(
            EventState eventState,
            RefundRequest refundRequest,
            String apiPath,
            FlowType flowType,
            EventType eventName) {
        return BaseEvent.builder()
                .eventName(eventName)
                .eventTime(Instant.now().getEpochSecond())
                .merchantOrderId(refundRequest.getOriginalMerchantOrderId())
                .data(
                        EventData.builder()
                                .merchantRefundId(refundRequest.getMerchantRefundId())
                                .eventState(eventState)
                                .apiPath(apiPath)
                                .amount(refundRequest.getAmount())
                                .originalMerchantOrderId(refundRequest.getOriginalMerchantOrderId())
                                .flowType(flowType)
                                .build())
                .build();
    }

    /** Failure Event for Refund --> Exception is PhonePeException */
    public static BaseEvent buildRefundEvent(
            EventState eventState,
            RefundRequest refundRequest,
            String apiPath,
            FlowType flowType,
            EventType eventName,
            Exception exception) {
        BaseEvent baseEvent =
                buildRefundEvent(eventState, refundRequest, apiPath, flowType, eventName);
        return populateExceptionFields(baseEvent, exception);
    }

    /** Refund Status Event Builder */
    public static BaseEvent buildRefundStatusEvent(
            EventState eventState,
            String refundId,
            String apiPath,
            FlowType flowType,
            EventType eventName) {
        return BaseEvent.builder()
                .eventName(eventName)
                .eventTime(Instant.now().getEpochSecond())
                .data(
                        EventData.builder()
                                .merchantRefundId(refundId)
                                .eventState(eventState)
                                .apiPath(apiPath)
                                .flowType(flowType)
                                .build())
                .build();
    }

    /** Failure Event for Refund Status --> Exception is PhonePeException */
    public static BaseEvent buildRefundStatusEvent(
            EventState eventState,
            String refundId,
            String apiPath,
            FlowType flowType,
            EventType eventName,
            Exception exception) {
        BaseEvent baseEvent =
                buildRefundStatusEvent(eventState, refundId, apiPath, flowType, eventName);
        return populateExceptionFields(baseEvent, exception);
    }

    /** CreateSdkOrder Event Builder */
    public static BaseEvent buildCreateSdkOrderEvent(
            EventState eventState,
            CreateSdkOrderRequest createSdkOrderRequest,
            String apiPath,
            FlowType flowType,
            EventType eventName) {
        return BaseEvent.builder()
                .merchantOrderId(createSdkOrderRequest.getMerchantOrderId())
                .eventName(eventName)
                .eventTime(Instant.now().getEpochSecond())
                .data(
                        EventData.builder()
                                .amount(createSdkOrderRequest.getAmount())
                                .eventState(eventState)
                                .apiPath(apiPath)
                                .expireAfter(createSdkOrderRequest.getExpireAfter())
                                .flowType(flowType)
                                .build())
                .build();
    }

    /** Failure Event for CreateSdkOrder --> Exception is PhonePeException */
    public static BaseEvent buildCreateSdkOrderEvent(
            EventState eventState,
            CreateSdkOrderRequest createSdkOrderRequest,
            String apiPath,
            FlowType flowType,
            EventType eventName,
            Exception exception) {
        BaseEvent baseEvent =
                buildCreateSdkOrderEvent(
                        eventState, createSdkOrderRequest, apiPath, flowType, eventName);
        return populateExceptionFields(baseEvent, exception);
    }

    /** Transaction Status Event Builder */
    public static BaseEvent buildTransactionStatusEvent(
            EventState eventState,
            String transactionId,
            String apiPath,
            FlowType flowType,
            EventType eventName) {
        return BaseEvent.builder()
                .eventName(eventName)
                .eventTime(Instant.now().getEpochSecond())
                .data(
                        EventData.builder()
                                .eventState(eventState)
                                .transactionId(transactionId)
                                .apiPath(apiPath)
                                .flowType(flowType)
                                .build())
                .build();
    }

    /** Failure Event for Transaction Status --> Exception is PhonePeException */
    public static BaseEvent buildTransactionStatusEvent(
            EventState eventState,
            String transactionId,
            String apiPath,
            FlowType flowType,
            EventType eventName,
            Exception exception) {
        BaseEvent baseEvent =
                buildTransactionStatusEvent(
                        eventState, transactionId, apiPath, flowType, eventName);
        return populateExceptionFields(baseEvent, exception);
    }

    /** Subscription Setup Event Builder */
    public static BaseEvent buildSubscriptionSetupEvent(
            EventState eventState,
            PgPaymentRequest setupRequest,
            String apiPath,
            EventType eventName) {
        // Extracting SubscriptionSetupPaymentFlow from the paymentFlow
        SubscriptionSetupPaymentFlow setupPaymentFlow =
                (SubscriptionSetupPaymentFlow) setupRequest.getPaymentFlow();

        // Extracting targetApp if the paymentMode is UPI_INTENT
        String targetApp =
                Optional.ofNullable(setupPaymentFlow)
                        .map(SubscriptionSetupPaymentFlow::getPaymentMode)
                        .filter(
                                paymentMode ->
                                        paymentMode.getType().equals(PgV2InstrumentType.UPI_INTENT))
                        .map(IntentPaymentV2Instrument.class::cast)
                        .map(IntentPaymentV2Instrument::getTargetApp)
                        .orElse(null);

        return BaseEvent.builder()
                .merchantOrderId(setupRequest.getMerchantOrderId())
                .eventTime(Instant.now().getEpochSecond())
                .eventName(eventName)
                .data(
                        EventData.builder()
                                .eventState(eventState)
                                .apiPath(apiPath)
                                .flowType(FlowType.SUBSCRIPTION)
                                .paymentInstrument(
                                        Optional.ofNullable(setupPaymentFlow)
                                                .map(SubscriptionSetupPaymentFlow::getPaymentMode)
                                                .map(PaymentV2Instrument::getType)
                                                .orElse(null))
                                .targetApp(targetApp)
                                .deviceContext(setupRequest.getDeviceContext())
                                .amount(setupRequest.getAmount())
                                .subscriptionEventData(
                                        SubscriptionEventData.builder()
                                                .orderExpireAt(setupRequest.getExpireAt())
                                                .subscriptionExpireAt(
                                                        Optional.ofNullable(setupPaymentFlow)
                                                                .map(
                                                                        SubscriptionSetupPaymentFlow
                                                                                ::getExpireAt)
                                                                .orElse(null))
                                                .merchantSubscriptionId(
                                                        Optional.ofNullable(setupPaymentFlow)
                                                                .map(
                                                                        SubscriptionSetupPaymentFlow
                                                                                ::getMerchantSubscriptionId)
                                                                .orElse(null))
                                                .build())
                                .build())
                .build();
    }

    /** Failure Event for Subscription Setup */
    public static BaseEvent buildSubscriptionSetupEvent(
            EventState eventState,
            PgPaymentRequest setupRequest,
            String apiPath,
            EventType eventName,
            Exception exception) {
        BaseEvent event = buildSubscriptionSetupEvent(eventState, setupRequest, apiPath, eventName);
        return populateExceptionFields(event, exception);
    }

    /** Subscription Notify Event Builder */
    public static BaseEvent buildSubscriptionNotifyEvent(
            EventState eventState,
            PgPaymentRequest notifyRequest,
            String apiPath,
            EventType eventName) {
        SubscriptionRedemptionPaymentFlow paymentFlow =
                (SubscriptionRedemptionPaymentFlow) notifyRequest.getPaymentFlow();
        return BaseEvent.builder()
                .eventName(eventName)
                .eventTime(Instant.now().getEpochSecond())
                .merchantOrderId(notifyRequest.getMerchantOrderId())
                .data(
                        EventData.builder()
                                .eventState(eventState)
                                .apiPath(apiPath)
                                .amount(notifyRequest.getAmount())
                                .flowType(FlowType.SUBSCRIPTION)
                                .subscriptionEventData(
                                        SubscriptionEventData.builder()
                                                .orderExpireAt(notifyRequest.getExpireAt())
                                                .merchantSubscriptionId(
                                                        paymentFlow.getMerchantSubscriptionId())
                                                .build())
                                .build())
                .build();
    }

    /** Failure Event for Subscription Notify */
    public static BaseEvent buildSubscriptionNotifyEvent(
            EventState eventState,
            PgPaymentRequest notifyRequest,
            String apiPath,
            EventType eventName,
            Exception exception) {
        BaseEvent event =
                buildSubscriptionNotifyEvent(eventState, notifyRequest, apiPath, eventName);
        return populateExceptionFields(event, exception);
    }

    public static BaseEvent buildSubscriptionRedeemEvent(
            EventState eventState, String merchantOrderId, String apiPath, EventType eventName) {
        return BaseEvent.builder()
                .eventTime(Instant.now().getEpochSecond())
                .eventName(eventName)
                .merchantOrderId(merchantOrderId)
                .data(
                        EventData.builder()
                                .flowType(FlowType.SUBSCRIPTION)
                                .eventState(eventState)
                                .apiPath(apiPath)
                                .build())
                .build();
    }

    public static BaseEvent buildSubscriptionRedeemEvent(
            EventState eventState,
            String merchantOrderId,
            String apiPath,
            EventType eventName,
            Exception exception) {
        BaseEvent event =
                buildSubscriptionRedeemEvent(eventState, merchantOrderId, apiPath, eventName);
        return populateExceptionFields(event, exception);
    }

    /** Order Status Event Builder */
    public static BaseEvent buildSubscriptionStatusEvent(
            EventState eventState,
            String merchantSubscriptionId,
            String apiPath,
            EventType eventName) {
        return BaseEvent.builder()
                .eventName(eventName)
                .eventTime(Instant.now().getEpochSecond())
                .data(
                        EventData.builder()
                                .eventState(eventState)
                                .apiPath(apiPath)
                                .flowType(FlowType.SUBSCRIPTION)
                                .subscriptionEventData(
                                        SubscriptionEventData.builder()
                                                .merchantSubscriptionId(merchantSubscriptionId)
                                                .build())
                                .build())
                .build();
    }

    public static BaseEvent buildSubscriptionStatusEvent(
            EventState eventState,
            String merchantSubscriptionId,
            String apiPath,
            EventType eventName,
            Exception exception) {
        BaseEvent event =
                buildSubscriptionStatusEvent(
                        eventState, merchantSubscriptionId, apiPath, eventName);
        return populateExceptionFields(event, exception);
    }

    public static BaseEvent buildSubscriptionCancelEvent(
            EventState eventState,
            String merchantSubscriptionId,
            String apiPath,
            EventType eventName) {
        return BaseEvent.builder()
                .eventTime(Instant.now().getEpochSecond())
                .eventName(eventName)
                .data(
                        EventData.builder()
                                .eventState(eventState)
                                .apiPath(apiPath)
                                .flowType(FlowType.SUBSCRIPTION)
                                .subscriptionEventData(
                                        SubscriptionEventData.builder()
                                                .merchantSubscriptionId(merchantSubscriptionId)
                                                .build())
                                .build())
                .build();
    }

    public static BaseEvent buildSubscriptionCancelEvent(
            EventState eventState,
            String merchantSubscriptionId,
            String apiPath,
            EventType eventName,
            Exception exception) {
        BaseEvent event =
                buildSubscriptionCancelEvent(
                        eventState, merchantSubscriptionId, apiPath, eventName);
        return populateExceptionFields(event, exception);
    }

    /** OAuth Event Builder for Cached Token Present */
    public static BaseEvent buildOAuthEvent(
            long fetchAttemptTime,
            String apiPath,
            EventType eventName,
            Exception exception,
            long cachedTokenIssuedAt,
            long cachedTokenExpiresAt) {
        BaseEvent baseEvent =
                BaseEvent.builder()
                        .eventName(eventName)
                        .eventTime(Instant.now().getEpochSecond())
                        .data(
                                EventData.builder()
                                        .tokenFetchAttemptTimestamp(fetchAttemptTime)
                                        .apiPath(apiPath)
                                        .eventState(EventState.FAILED)
                                        .cachedTokenExpiresAt(cachedTokenExpiresAt)
                                        .cachedTokenIssuedAt(cachedTokenIssuedAt)
                                        .build())
                        .build();
        return populateExceptionFields(baseEvent, exception);
    }

    public static BaseEvent buildCallbackSerializationFailedEvent(
            EventState eventState, FlowType flowType, EventType eventName, Exception exception) {
        BaseEvent baseEvent =
                BaseEvent.builder()
                        .eventName(eventName)
                        .eventTime(Instant.now().getEpochSecond())
                        .data(EventData.builder().eventState(eventState).flowType(flowType).build())
                        .build();
        return populateExceptionFields(baseEvent, exception);
    }

    private static BaseEvent populateExceptionFields(BaseEvent baseEvent, Exception exception) {
        baseEvent.data.setExceptionMessage(exception.getMessage());
        baseEvent.data.setExceptionClass(exception.getClass().getSimpleName());
        if (exception instanceof PhonePeException) {
            PhonePeException phonePeException = (PhonePeException) exception;
            baseEvent.data.setExceptionHttpStatusCode(phonePeException.getHttpStatusCode());
            baseEvent.data.setExceptionCode(phonePeException.getCode());
            baseEvent.data.setExceptionData(phonePeException.getData());
        }
        return baseEvent;
    }
}

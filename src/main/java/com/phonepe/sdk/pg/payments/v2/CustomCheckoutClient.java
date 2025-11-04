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
package com.phonepe.sdk.pg.payments.v2;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.type.TypeReference;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.BaseClient;
import com.phonepe.sdk.pg.common.CommonUtils;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.events.models.BaseEvent;
import com.phonepe.sdk.pg.common.events.models.enums.EventState;
import com.phonepe.sdk.pg.common.events.models.enums.EventType;
import com.phonepe.sdk.pg.common.events.models.enums.FlowType;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.http.HttpHeaderPair;
import com.phonepe.sdk.pg.common.http.HttpMethodType;
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.request.RefundRequest;
import com.phonepe.sdk.pg.common.models.response.CallbackResponse;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import com.phonepe.sdk.pg.common.models.response.RefundResponse;
import com.phonepe.sdk.pg.common.models.response.RefundStatusResponse;
import com.phonepe.sdk.pg.payments.v2.customcheckout.CustomCheckoutConstants;
import com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.SneakyThrows;

public class CustomCheckoutClient extends BaseClient {

    private static final ConcurrentHashMap<String, CustomCheckoutClient> cachedInstances =
            new ConcurrentHashMap<>();
    private List<HttpHeaderPair> headers;

    private CustomCheckoutClient(
            final String clientId,
            final String clientSecret,
            final Integer clientVersion,
            final Env env,
            boolean shouldPublishEvents) {
        super(clientId, clientSecret, clientVersion, env, shouldPublishEvents);
        this.eventPublisher.send(
                BaseEvent.buildInitClientEvent(
                        FlowType.PG, EventType.CUSTOM_CHECKOUT_CLIENT_INITIALIZED));
        this.prepareHeaders();
    }

    /**
     * Generates a CustomCheckout Client for interacting with the PhonePe APIs
     *
     * @param clientId Unique client-id assigned to merchant by PhonePe
     * @param clientSecret Secret provided by PhonePe
     * @param clientVersion The client version used for secure transactions
     * @param env Set to `Env.SANDBOX` for the SANDBOX environment or `Env.PRODUCTION` for the
     *     production environment.
     * @return CustomCheckoutClient object for interacting with the PhonePe APIs
     */
    public static CustomCheckoutClient getInstance(
            final String clientId,
            final String clientSecret,
            final Integer clientVersion,
            final Env env)
            throws PhonePeException {
        return getInstance(clientId, clientSecret, clientVersion, env, true);
    }

    /**
     * Generates a CustomCheckout Client for interacting with the PhonePe APIs
     *
     * @param clientId received at the time of onboarding
     * @param clientSecret received at the time of onboarding
     * @param clientVersion received at the time of onboarding
     * @param env environment to be used by the merchant
     * @param shouldPublishEvents When true, events are sent to PhonePe providing smoother
     *     experience
     * @return CustomCheckoutClient object for interacting with the PhonePe APIs
     */
    public static CustomCheckoutClient getInstance(
            final String clientId,
            final String clientSecret,
            final Integer clientVersion,
            final Env env,
            boolean shouldPublishEvents)
            throws PhonePeException {
        final boolean shouldPublishInProd = shouldPublishEvents && env == Env.PRODUCTION;
        final String requestedClientSHA =
                CommonUtils.calculateSha256(
                        clientId,
                        clientSecret,
                        clientVersion,
                        env,
                        shouldPublishInProd,
                        FlowType.PG);

        return new CustomCheckoutClient(
                clientId, clientSecret, clientVersion, env, shouldPublishInProd);
    }

    /**
     * Initiate a Pay Order
     *
     * @param pgPaymentRequest Request Object Build using CustomCheckoutPayRequest Builder's
     * @return CustomCheckoutPayResponse contains checkout page url and related details
     */
    @SneakyThrows
    public PgPaymentResponse pay(PgPaymentRequest pgPaymentRequest) {
        String url = CustomCheckoutConstants.PAY_API;
        try {
            PgPaymentResponse pgPaymentResponse =
                    requestViaAuthRefresh(
                            HttpMethodType.POST,
                            pgPaymentRequest,
                            url,
                            null,
                            new TypeReference<PgPaymentResponse>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildCustomCheckoutPayEvent(
                            EventState.SUCCESS, pgPaymentRequest, url, EventType.PAY_SUCCESS));
            return pgPaymentResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildCustomCheckoutPayEvent(
                            EventState.FAILED,
                            pgPaymentRequest,
                            url,
                            EventType.PAY_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Gets the status of the order
     *
     * @param merchantOrderId Order id generated by merchant
     * @return OrderStatusResponse Response with order and transaction details
     */
    @SneakyThrows
    public OrderStatusResponse getOrderStatus(String merchantOrderId) {
        return getOrderStatus(merchantOrderId, false);
    }

    /**
     * Gets status of an order
     *
     * @param merchantOrderId Order id generated by merchant
     * @param details true -> order status has all attempt details under paymentDetails list false
     *     -> order status has only latest attempt details under paymentDetails list
     * @return OrderStatusResponse Response with order and transaction details
     */
    @SneakyThrows
    public OrderStatusResponse getOrderStatus(String merchantOrderId, boolean details) {
        String url = String.format(CustomCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        try {
            OrderStatusResponse orderStatusResponse =
                    requestViaAuthRefresh(
                            HttpMethodType.GET,
                            null,
                            url,
                            Collections.singletonMap(
                                    CustomCheckoutConstants.ORDER_DETAILS,
                                    Boolean.toString(details)),
                            new TypeReference<OrderStatusResponse>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildOrderStatusEvent(
                            EventState.SUCCESS,
                            merchantOrderId,
                            url,
                            FlowType.PG,
                            EventType.ORDER_STATUS_SUCCESS));
            return orderStatusResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildOrderStatusEvent(
                            EventState.FAILED,
                            merchantOrderId,
                            url,
                            FlowType.PG,
                            EventType.ORDER_STATUS_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Initiate refund of an order
     *
     * @param refundRequest Request object build using RefundRequest builder
     * @return RefundResponse contains refund details for an order
     */
    @SneakyThrows
    public RefundResponse refund(RefundRequest refundRequest) {
        String url = CustomCheckoutConstants.REFUND_API;
        try {
            RefundResponse refundResponse =
                    requestViaAuthRefresh(
                            HttpMethodType.POST,
                            refundRequest,
                            url,
                            null,
                            new TypeReference<RefundResponse>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildRefundEvent(
                            EventState.SUCCESS,
                            refundRequest,
                            url,
                            FlowType.PG,
                            EventType.REFUND_SUCCESS));
            return refundResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildRefundEvent(
                            EventState.FAILED,
                            refundRequest,
                            url,
                            FlowType.PG,
                            EventType.REFUND_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Create order token for SDK integrated order requests
     *
     * @param createSdkOrderRequest Request object build using CreateSdkOrderRequest builder
     * @return CreateSdkOrderResponse contains token details to be consumed by the UI
     */
    @SneakyThrows
    public CreateSdkOrderResponse createSdkOrder(CreateSdkOrderRequest createSdkOrderRequest) {
        String url = CustomCheckoutConstants.CREATE_ORDER_API;
        try {
            CreateSdkOrderResponse createSdkOrderResponse =
                    requestViaAuthRefresh(
                            HttpMethodType.POST,
                            createSdkOrderRequest,
                            url,
                            null,
                            new TypeReference<CreateSdkOrderResponse>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildCreateSdkOrderEvent(
                            EventState.SUCCESS,
                            createSdkOrderRequest,
                            url,
                            FlowType.PG,
                            EventType.CREATE_SDK_ORDER_SUCCESS));
            return createSdkOrderResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildCreateSdkOrderEvent(
                            EventState.FAILED,
                            createSdkOrderRequest,
                            url,
                            FlowType.PG,
                            EventType.CREATE_SDK_ORDER_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Get status of a transaction attempt
     *
     * @param transactionId Transaction attempt id generated by PhonePe
     * @return OrderStatusResponse Response with order and transaction details
     * @throws PhonePeException if any error occurs during the process
     */
    @SneakyThrows
    public OrderStatusResponse getTransactionStatus(String transactionId) {
        final String url =
                String.format(CustomCheckoutConstants.TRANSACTION_STATUS_API, transactionId);
        try {
            OrderStatusResponse orderStatusResponse =
                    requestViaAuthRefresh(
                            HttpMethodType.GET,
                            null,
                            url,
                            null,
                            new TypeReference<OrderStatusResponse>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildTransactionStatusEvent(
                            EventState.SUCCESS,
                            transactionId,
                            url,
                            FlowType.PG,
                            EventType.TRANSACTION_STATUS_SUCCESS));
            return orderStatusResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildTransactionStatusEvent(
                            EventState.FAILED,
                            transactionId,
                            url,
                            FlowType.PG,
                            EventType.TRANSACTION_STATUS_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Get status of refund
     *
     * @param refundId Merchant Refund id for which you need the status
     * @return RefundStatusResponse Refund status details
     * @throws PhonePeException if any error occurs during the process
     */
    @SneakyThrows
    public RefundStatusResponse getRefundStatus(String refundId) {
        final String url = String.format(CustomCheckoutConstants.REFUND_STATUS_API, refundId);
        try {
            RefundStatusResponse refundStatusResponse =
                    requestViaAuthRefresh(
                            HttpMethodType.GET,
                            null,
                            url,
                            null,
                            new TypeReference<RefundStatusResponse>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildRefundStatusEvent(
                            EventState.SUCCESS,
                            refundId,
                            url,
                            FlowType.PG,
                            EventType.REFUND_STATUS_SUCCESS));
            return refundStatusResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildRefundStatusEvent(
                            EventState.FAILED,
                            refundId,
                            url,
                            FlowType.PG,
                            EventType.REFUND_STATUS_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Validate if the callback is valid
     *
     * @param username username set by the merchant on the dashboard
     * @param password password set by the merchant on the dashboard
     * @param authorization String data under `authorization` key of response headers
     * @param responseBody Callback response body
     * @return CallbackResponse Deserialized callback body
     * @throws PhonePeException when callback is not valid
     */
    @SneakyThrows
    public CallbackResponse validateCallback(
            String username, String password, String authorization, String responseBody) {
        if (!CommonUtils.isCallbackValid(username, password, authorization)) {
            throw new PhonePeException(417, "Invalid Callback");
        }
        try {
            return getObjectMapper().readValue(responseBody, CallbackResponse.class);
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildCallbackSerializationFailedEvent(
                            EventState.FAILED,
                            FlowType.PG,
                            EventType.CALLBACK_SERIALIZATION_FAILED,
                            exception));
            throw exception;
        }
    }

    /** Prepares the headers for CustomCheckout Client */
    private void prepareHeaders() {
        this.headers = new ArrayList<>();
        headers.add(
                HttpHeaderPair.builder().key(Headers.CONTENT_TYPE).value(APPLICATION_JSON).build());
        headers.add(
                HttpHeaderPair.builder().key(Headers.SOURCE).value(Headers.INTEGRATION).build());
        headers.add(
                HttpHeaderPair.builder()
                        .key(Headers.SOURCE_VERSION)
                        .value(Headers.API_VERSION)
                        .build());
        headers.add(
                HttpHeaderPair.builder()
                        .key(Headers.SOURCE_PLATFORM)
                        .value(Headers.SDK_TYPE)
                        .build());
        headers.add(
                HttpHeaderPair.builder()
                        .key(Headers.SOURCE_PLATFORM_VERSION)
                        .value(Headers.SDK_VERSION)
                        .build());
    }
}

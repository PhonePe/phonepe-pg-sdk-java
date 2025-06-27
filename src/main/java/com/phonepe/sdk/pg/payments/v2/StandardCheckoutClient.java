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
import com.phonepe.sdk.pg.common.models.request.RefundRequest;
import com.phonepe.sdk.pg.common.models.response.CallbackResponse;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.common.models.response.RefundResponse;
import com.phonepe.sdk.pg.common.models.response.RefundStatusResponse;
import com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;

/** The StandardCheckout client class provides methods for interacting with the PhonePe APIs. */
public class StandardCheckoutClient extends BaseClient {

    private static StandardCheckoutClient client;
    private List<HttpHeaderPair> headers;

    private StandardCheckoutClient(
            final String clientId,
            final String clientSecret,
            final Integer clientVersion,
            final Env env,
            boolean shouldPublishEvents) {
        super(clientId, clientSecret, clientVersion, env, shouldPublishEvents);
        this.eventPublisher.send(
                BaseEvent.buildInitClientEvent(
                        FlowType.PG_CHECKOUT, EventType.STANDARD_CHECKOUT_CLIENT_INITIALIZED));
        this.prepareHeaders();
    }

    /**
     * Generates a StandardCheckout Client for interacting with the PhonePe APIs
     *
     * @param clientId Unique client-id assigned to merchant by PhonePe
     * @param clientSecret Secret provided by PhonePe
     * @param clientVersion The client version used for secure transactions
     * @param env Set to `Env.SANDBOX` for the SANDBOX environment or `Env.PRODUCTION` for the
     *     production environment.
     * @return StandardCheckoutClient object for interacting with the PhonePe APIs
     */
    public static synchronized StandardCheckoutClient getInstance(
            final String clientId,
            final String clientSecret,
            final Integer clientVersion,
            final Env env)
            throws PhonePeException {
        return getInstance(clientId, clientSecret, clientVersion, env, true);
    }

    /**
     * Generates a StandardCheckout Client for interacting with the PhonePe APIs
     *
     * @param clientId received at the time of onboarding
     * @param clientSecret received at the time of onboarding
     * @param clientVersion received at the time of onboarding
     * @param env environment to be used by the merchant
     * @param shouldPublishEvents When true, events are sent to PhonePe providing smoother
     *     experience
     * @return StandardCheckoutClient object for interacting with the PhonePe APIs
     */
    public static synchronized StandardCheckoutClient getInstance(
            final String clientId,
            final String clientSecret,
            final Integer clientVersion,
            final Env env,
            boolean shouldPublishEvents)
            throws PhonePeException {
        shouldPublishEvents = shouldPublishEvents && env == Env.PRODUCTION;
        if (Objects.isNull(client)) {
            client =
                    new StandardCheckoutClient(
                            clientId, clientSecret, clientVersion, env, shouldPublishEvents);
            return client;
        }

        String requestedClientSHA =
                CommonUtils.calculateSha256(
                        clientId,
                        clientSecret,
                        clientVersion,
                        env,
                        shouldPublishEvents,
                        FlowType.PG_CHECKOUT);
        String cachedClientSHA =
                CommonUtils.calculateSha256(
                        client.getCredentialConfig().getClientId(),
                        client.getCredentialConfig().getClientSecret(),
                        client.getCredentialConfig().getClientVersion(),
                        client.getEnv(),
                        client.isShouldPublishEvents(),
                        FlowType.PG_CHECKOUT);

        if (Objects.equals(requestedClientSHA, cachedClientSHA)) {
            return client;
        }
        throw new PhonePeException(
                "Cannot re-initialize StandardCheckoutClient. Please utilize the existing Client"
                        + " object with required credentials");
    }

    /**
     * Initiate a Pay Order
     *
     * @param standardCheckoutPayRequest Request Object Build using StandardCheckoutPayRequest
     *     Builder
     * @return StandardCheckoutPayResponse contains checkout page url and related details
     */
    @SneakyThrows
    public StandardCheckoutPayResponse pay(StandardCheckoutPayRequest standardCheckoutPayRequest) {
        String url = StandardCheckoutConstants.PAY_API;
        try {
            StandardCheckoutPayResponse standardCheckoutPayResponse =
                    requestViaAuthRefresh(
                            HttpMethodType.POST,
                            standardCheckoutPayRequest,
                            url,
                            null,
                            new TypeReference<StandardCheckoutPayResponse>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildStandardCheckoutPayEvent(
                            EventState.SUCCESS,
                            standardCheckoutPayRequest,
                            url,
                            EventType.PAY_SUCCESS));
            return standardCheckoutPayResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildStandardCheckoutPayEvent(
                            EventState.FAILED,
                            standardCheckoutPayRequest,
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
     * Gets the status of an order
     *
     * @param merchantOrderId Order id generated by merchant
     * @param details true -> order status has all attempt details under paymentDetails list false
     *     -> order status has only latest attempt details under paymentDetails list
     * @return OrderStatusResponse Response with order and transaction details
     */
    @SneakyThrows
    public OrderStatusResponse getOrderStatus(String merchantOrderId, boolean details) {
        final String url =
                String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        try {
            OrderStatusResponse orderStatusResponse =
                    requestViaAuthRefresh(
                            HttpMethodType.GET,
                            null,
                            url,
                            Collections.singletonMap(
                                    StandardCheckoutConstants.ORDER_DETAILS,
                                    Boolean.toString(details)),
                            new TypeReference<OrderStatusResponse>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildOrderStatusEvent(
                            EventState.SUCCESS,
                            merchantOrderId,
                            url,
                            FlowType.PG_CHECKOUT,
                            EventType.ORDER_STATUS_SUCCESS));
            return orderStatusResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildOrderStatusEvent(
                            EventState.FAILED,
                            merchantOrderId,
                            url,
                            FlowType.PG_CHECKOUT,
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
        final String url = StandardCheckoutConstants.REFUND_API;
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
                            FlowType.PG_CHECKOUT,
                            EventType.REFUND_SUCCESS));
            return refundResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildRefundEvent(
                            EventState.FAILED,
                            refundRequest,
                            url,
                            FlowType.PG_CHECKOUT,
                            EventType.REFUND_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Create order token for SDK integrated order requests
     *
     * @param createSdkOrderRequest Request object build using SdkOrderRequest builder
     * @return CreateSdkOrderResponse contains token details to be consumed by the UI
     */
    @SneakyThrows
    public CreateSdkOrderResponse createSdkOrder(CreateSdkOrderRequest createSdkOrderRequest) {
        final String url = StandardCheckoutConstants.CREATE_ORDER_API;
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
                            FlowType.PG_CHECKOUT,
                            EventType.CREATE_SDK_ORDER_SUCCESS));
            return createSdkOrderResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildCreateSdkOrderEvent(
                            EventState.FAILED,
                            createSdkOrderRequest,
                            url,
                            FlowType.PG_CHECKOUT,
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
     */
    @SneakyThrows
    public OrderStatusResponse getTransactionStatus(String transactionId) {
        final String url =
                String.format(StandardCheckoutConstants.TRANSACTION_STATUS_API, transactionId);
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
                            FlowType.PG_CHECKOUT,
                            EventType.TRANSACTION_STATUS_SUCCESS));
            return orderStatusResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildTransactionStatusEvent(
                            EventState.FAILED,
                            transactionId,
                            url,
                            FlowType.PG_CHECKOUT,
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
        final String url = String.format(StandardCheckoutConstants.REFUND_STATUS_API, refundId);
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
                            FlowType.PG_CHECKOUT,
                            EventType.REFUND_STATUS_SUCCESS));
            return refundStatusResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildRefundStatusEvent(
                            EventState.FAILED,
                            refundId,
                            url,
                            FlowType.PG_CHECKOUT,
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
            String username, String password, String authorization, String responseBody)
            throws PhonePeException {
        if (!CommonUtils.isCallbackValid(username, password, authorization)) {
            throw new PhonePeException(417, "Invalid Callback");
        }
        try {
            return getObjectMapper().readValue(responseBody, CallbackResponse.class);
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildCallbackSerializationFailedEvent(
                            EventState.FAILED,
                            FlowType.PG_CHECKOUT,
                            EventType.CALLBACK_SERIALIZATION_FAILED,
                            exception));
            throw exception;
        }
    }

    /** Prepares the headers for StandardCheckout Client */
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

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
package com.phonepe.sdk.pg.subscription.v2;

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
import com.phonepe.sdk.pg.subscription.v2.models.request.SubscriptionRedeemRequestV2;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionRedeemResponseV2;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionStatusResponseV2;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;

public class SubscriptionClient extends BaseClient {

    private static SubscriptionClient client;
    private List<HttpHeaderPair> headers;

    private SubscriptionClient(
            String clientId,
            String clientSecret,
            Integer clientVersion,
            Env env,
            boolean shouldPublishEvents) {
        super(clientId, clientSecret, clientVersion, env, shouldPublishEvents);
        this.eventPublisher.send(
                BaseEvent.buildInitClientEvent(
                        FlowType.SUBSCRIPTION, EventType.SUBSCRIPTION_CLIENT_INITIALIZED));
        this.prepareHeaders();
    }

    /**
     * Generates a Subscription Client for interacting with the PhonePe APIs
     *
     * @param clientId Unique client-id assigned to merchant by PhonePe
     * @param clientSecret Secret provided by PhonePe
     * @param clientVersion The client version used for secure transactions
     * @param env Set to `Env.SANDBOX` for the SANDBOX environment or `Env.PRODUCTION` for the
     *     production environment.
     * @return SubscriptionClient object for interacting with the PhonePe APIs
     */
    public static synchronized SubscriptionClient getInstance(
            final String clientId,
            final String clientSecret,
            final Integer clientVersion,
            final Env env)
            throws PhonePeException {
        return getInstance(clientId, clientSecret, clientVersion, env, true);
    }

    /**
     * Generates a Subscription Client for interacting with the PhonePe Subscription APIs
     *
     * @param clientId Unique client-id assigned to merchant by PhonePe
     * @param clientSecret Secret provided by PhonePe
     * @param clientVersion The client version used for secure transactions
     * @param env Set to `Env.SANDBOX` for the SANDBOX environment or `Env.PRODUCTION` for the
     *     production environment.
     * @param shouldPublishEvents When true, events are sent to PhonePe providing smoother
     *     experience
     * @return SubscriptionClient object for interacting with the PhonePe APIs
     */
    public static synchronized SubscriptionClient getInstance(
            final String clientId,
            final String clientSecret,
            final Integer clientVersion,
            final Env env,
            boolean shouldPublishEvents)
            throws PhonePeException {
        shouldPublishEvents = shouldPublishEvents && env == Env.PRODUCTION;
        if (Objects.isNull(client)) {
            client =
                    new SubscriptionClient(
                            clientId, clientSecret, clientVersion, env, shouldPublishEvents);
            return client;
        }

        String requestedSha256 =
                CommonUtils.calculateSha256(
                        clientId,
                        clientSecret,
                        clientVersion,
                        env,
                        shouldPublishEvents,
                        FlowType.SUBSCRIPTION);
        String existingSha =
                CommonUtils.calculateSha256(
                        client.getCredentialConfig().getClientId(),
                        client.getCredentialConfig().getClientSecret(),
                        client.getCredentialConfig().getClientVersion(),
                        client.getEnv(),
                        client.isShouldPublishEvents(),
                        FlowType.SUBSCRIPTION);

        if (Objects.equals(requestedSha256, existingSha)) {
            return client;
        }
        throw new PhonePeException(
                "Cannot re-initialize SubscriptionClient. Please utilize the existing client object"
                        + " with required credentials");
    }

    /**
     * Setup the subscription based on the given merchant requirements
     *
     * @param request Request built using PgPaymentRequest Builder's. 1. Payment via UPI_INTENT ->
     *     use PgPaymentRequest.SubscriptionSetupUpiIntentBuilder() 2. Payment via UPI_COLLECT ->
     *     use PgPaymentRequest.SubscriptionSetupUpiCollectBuilder()
     * @return PgPaymentResponse which contains the state of the requested setup
     */
    @SneakyThrows
    public PgPaymentResponse setup(PgPaymentRequest request) {
        final String url = SubscriptionConstants.SETUP_API;
        try {
            PgPaymentResponse response =
                    requestViaAuthRefresh(
                            HttpMethodType.POST,
                            request,
                            url,
                            null,
                            new TypeReference<PgPaymentResponse>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildSubscriptionSetupEvent(
                            EventState.SUCCESS, request, url, EventType.SETUP_SUCCESS));
            return response;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildSubscriptionSetupEvent(
                            EventState.FAILED, request, url, EventType.SETUP_FAILED, exception));
            throw exception;
        }
    }

    /**
     * Used to send the notify information from the PhonePe
     *
     * @param request Request built using PgPaymentRequest.SubscriptionNotifyRequestBuilder()
     * @return PgPaymentResponse which contains the state of the requested notify
     */
    @SneakyThrows
    public PgPaymentResponse notify(PgPaymentRequest request) {
        final String url = SubscriptionConstants.NOTIFY_API;
        try {
            PgPaymentResponse response =
                    requestViaAuthRefresh(
                            HttpMethodType.POST,
                            request,
                            url,
                            null,
                            new TypeReference<PgPaymentResponse>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildSubscriptionNotifyEvent(
                            EventState.SUCCESS, request, url, EventType.NOTIFY_SUCCESS));
            return response;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildSubscriptionNotifyEvent(
                            EventState.FAILED, request, url, EventType.NOTIFY_FAILED, exception));
            throw exception;
        }
    }

    /**
     * Used to redeem the subscription for the given ID
     *
     * @param merchantOrderId Same ID used at the time of making a notify request
     * @return SubscriptionRedeemResponseV2 which contains the state for the request made
     */
    @SneakyThrows
    public SubscriptionRedeemResponseV2 redeem(String merchantOrderId) {
        final String url = SubscriptionConstants.REDEEM_API;
        try {
            SubscriptionRedeemRequestV2 request = new SubscriptionRedeemRequestV2(merchantOrderId);
            SubscriptionRedeemResponseV2 response =
                    requestViaAuthRefresh(
                            HttpMethodType.POST,
                            request,
                            url,
                            null,
                            new TypeReference<SubscriptionRedeemResponseV2>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildSubscriptionRedeemEvent(
                            EventState.SUCCESS, merchantOrderId, url, EventType.REDEEM_SUCCESS));
            return response;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildSubscriptionRedeemEvent(
                            EventState.FAILED,
                            merchantOrderId,
                            url,
                            EventType.REDEEM_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Gets the status of the subscription
     *
     * @param merchantSubscriptionId Subscription ID generated by the merchant
     * @return SubscriptionStatusResponseV2 which contains the entire status of the given
     *     subscription ID
     */
    @SneakyThrows
    public SubscriptionStatusResponseV2 getSubscriptionStatus(String merchantSubscriptionId) {
        final String url =
                String.format(
                        SubscriptionConstants.SUBSCRIPTION_STATUS_API, merchantSubscriptionId);
        try {
            SubscriptionStatusResponseV2 response =
                    requestViaAuthRefresh(
                            HttpMethodType.GET,
                            null,
                            url,
                            null,
                            new TypeReference<SubscriptionStatusResponseV2>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildSubscriptionStatusEvent(
                            EventState.SUCCESS,
                            merchantSubscriptionId,
                            url,
                            EventType.SUBSCRIPTION_STATUS_SUCCESS));
            return response;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildSubscriptionStatusEvent(
                            EventState.FAILED,
                            merchantSubscriptionId,
                            url,
                            EventType.SUBSCRIPTION_STATUS_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Gets the status for the order
     *
     * @param merchantOrderId Order ID generated by the merchant
     * @return OrderStatusResponse which contains the entire status of the given order ID
     */
    @SneakyThrows
    public OrderStatusResponse getOrderStatus(String merchantOrderId) {
        final String url = String.format(SubscriptionConstants.ORDER_STATUS_API, merchantOrderId);
        try {
            OrderStatusResponse response =
                    requestViaAuthRefresh(
                            HttpMethodType.GET,
                            null,
                            url,
                            null,
                            new TypeReference<OrderStatusResponse>() {},
                            headers);
            this.eventPublisher.send(
                    BaseEvent.buildOrderStatusEvent(
                            EventState.SUCCESS,
                            merchantOrderId,
                            url,
                            FlowType.SUBSCRIPTION,
                            EventType.ORDER_STATUS_SUCCESS));
            return response;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildOrderStatusEvent(
                            EventState.FAILED,
                            merchantOrderId,
                            url,
                            FlowType.SUBSCRIPTION,
                            EventType.ORDER_STATUS_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Cancels/Stops the subscription for the given subscription ID
     *
     * @param merchantSubscriptionId Subscription ID generated by the merchant
     */
    @SneakyThrows
    public void cancelSubscription(String merchantSubscriptionId) {
        final String url = String.format(SubscriptionConstants.CANCEL_API, merchantSubscriptionId);
        try {
            requestViaAuthRefresh(
                    HttpMethodType.POST, null, url, null, new TypeReference<Void>() {}, headers);
            this.eventPublisher.send(
                    BaseEvent.buildSubscriptionCancelEvent(
                            EventState.SUCCESS,
                            merchantSubscriptionId,
                            url,
                            EventType.CANCEL_SUCCESS));
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildSubscriptionCancelEvent(
                            EventState.FAILED,
                            merchantSubscriptionId,
                            url,
                            EventType.CANCEL_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Gets status of the given particular transaction ID
     *
     * @param transactionId Generated by the PhonePe Side
     * @return OrderStatusResponse which contains the status of the particular transaction in the
     *     paymentDetails attribute
     */
    @SneakyThrows
    public OrderStatusResponse getTransactionStatus(String transactionId) {
        final String url =
                String.format(SubscriptionConstants.TRANSACTION_STATUS_API, transactionId);
        try {
            OrderStatusResponse response =
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
                            FlowType.SUBSCRIPTION,
                            EventType.TRANSACTION_STATUS_SUCCESS));
            return response;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildTransactionStatusEvent(
                            EventState.FAILED,
                            transactionId,
                            url,
                            FlowType.SUBSCRIPTION,
                            EventType.TRANSACTION_STATUS_FAILED,
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
        final String url = SubscriptionConstants.REFUND_API;
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
                            FlowType.SUBSCRIPTION,
                            EventType.REFUND_SUCCESS));
            return refundResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildRefundEvent(
                            EventState.FAILED,
                            refundRequest,
                            url,
                            FlowType.SUBSCRIPTION,
                            EventType.REFUND_FAILED,
                            exception));
            throw exception;
        }
    }

    /**
     * Get status of the refund initiated
     *
     * @param refundId Merchant Refund id for which you need the status
     * @return RefundStatusResponse Refund status details
     * @throws PhonePeException if any error occurs during the process
     */
    @SneakyThrows
    public RefundStatusResponse getRefundStatus(String refundId) {
        final String url = String.format(SubscriptionConstants.REFUND_STATUS_API, refundId);
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
                            FlowType.SUBSCRIPTION,
                            EventType.REFUND_STATUS_SUCCESS));
            return refundStatusResponse;
        } catch (Exception exception) {
            this.eventPublisher.send(
                    BaseEvent.buildRefundStatusEvent(
                            EventState.FAILED,
                            refundId,
                            url,
                            FlowType.SUBSCRIPTION,
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
                            FlowType.SUBSCRIPTION,
                            EventType.CALLBACK_SERIALIZATION_FAILED,
                            exception));
            throw exception;
        }
    }

    /** Prepares the headers for Subscription Client */
    private void prepareHeaders() {
        this.headers = new ArrayList<>();
        headers.add(
                HttpHeaderPair.builder().key(Headers.CONTENT_TYPE).value(APPLICATION_JSON).build());
        headers.add(
                HttpHeaderPair.builder().key(Headers.SOURCE).value(Headers.INTEGRATION).build());
        headers.add(
                HttpHeaderPair.builder()
                        .key(Headers.SOURCE_VERSION)
                        .value(Headers.SUBSCRIPTION_API_VERSION)
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

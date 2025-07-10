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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.common.models.response.PaymentDetail;
import com.phonepe.sdk.pg.payments.v2.customcheckout.CustomCheckoutConstants;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionConstants;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

class TransactionTest extends BaseSetupWithOAuth {

    String transactionId = "transactionId";

    @Test
    void testTransactionStatus() throws JsonProcessingException {

        String url = String.format(StandardCheckoutConstants.TRANSACTION_STATUS_API, transactionId);
        OrderStatusResponse transactionCheckStatusResponse =
                OrderStatusResponse.builder()
                        .state("FAILED")
                        .orderId("orderId")
                        .expireAt(34324)
                        .paymentDetails(
                                Arrays.asList(
                                        PaymentDetail.builder()
                                                .transactionId("transactionId")
                                                .amount(100)
                                                .build()))
                        .build();

        addStubForGetRequest(
                url,
                ImmutableMap.of(),
                getHeaders(),
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                transactionCheckStatusResponse);

        OrderStatusResponse actual = standardCheckoutClient.getTransactionStatus(transactionId);
        Assertions.assertEquals(actual, transactionCheckStatusResponse);
    }

    @Test
    void testTransactionTestCustomCheckout() {
        String url = String.format(CustomCheckoutConstants.TRANSACTION_STATUS_API, transactionId);
        OrderStatusResponse transactionCheckStatusResponse =
                OrderStatusResponse.builder()
                        .state("FAILED")
                        .orderId("orderId")
                        .expireAt(34324)
                        .paymentDetails(
                                Arrays.asList(
                                        PaymentDetail.builder()
                                                .transactionId("transactionId")
                                                .amount(100)
                                                .build()))
                        .build();

        addStubForGetRequest(
                url,
                ImmutableMap.of(),
                getHeaders(),
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                transactionCheckStatusResponse);

        OrderStatusResponse actual = customCheckoutClient.getTransactionStatus(transactionId);
        Assertions.assertEquals(actual, transactionCheckStatusResponse);
    }

    @Test
    void testTransactionSubscriptionClient() {
        String url = String.format(SubscriptionConstants.TRANSACTION_STATUS_API, transactionId);
        OrderStatusResponse transactionCheckStatusResponse =
                OrderStatusResponse.builder()
                        .state("FAILED")
                        .orderId("orderId")
                        .expireAt(34324)
                        .paymentDetails(
                                Arrays.asList(
                                        PaymentDetail.builder()
                                                .transactionId("transactionId")
                                                .amount(100)
                                                .build()))
                        .build();

        addStubForGetRequest(
                url,
                ImmutableMap.of(),
                getHeadersForSubscription(),
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                transactionCheckStatusResponse);

        OrderStatusResponse actual = subscriptionClient.getTransactionStatus(transactionId);
        Assertions.assertEquals(actual, transactionCheckStatusResponse);
    }
}

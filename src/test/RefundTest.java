import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.common.models.request.RefundRequest;
import com.phonepe.sdk.pg.common.models.response.RefundResponse;
import com.phonepe.sdk.pg.common.models.response.RefundStatusResponse;
import com.phonepe.sdk.pg.payments.v2.customcheckout.CustomCheckoutConstants;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionConstants;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class RefundTest extends BaseSetupWithOAuth {


    @Test
    void testRefundSuccess() {
        String refundId = "234234";
        String url = StandardCheckoutConstants.REFUND_API;
        RefundRequest refundRequest = RefundRequest.builder()
                .merchantRefundId(refundId)
                .originalMerchantOrderId(merchantOrderId)
                .amount(amount)
                .build();
        RefundResponse refundResponse = RefundResponse.builder()
                .refundId("34534534")
                .state("CREATED")
                .amount(100)
                .build();
        Map<String, String> headers = getHeaders();

        addStubForPostRequest(url, headers, refundRequest, HttpStatus.SC_OK, Maps.newHashMap(), refundResponse);
        RefundResponse actual = standardCheckoutClient.refund(refundRequest);
        Assertions.assertEquals(actual, refundResponse);
    }

    @Test
    void testRefundStatus() {
        String refundId = "RefundId";
        RefundStatusResponse refundStatusResponse = RefundStatusResponse.builder()
                .merchantId("merchantId")
                .amount(100)
                .state("COMPLETED")
                .merchantRefundId("RefundId")
                .paymentDetails(Arrays.asList())
                .originalMerchantOrderId("merchantOrderId")
                .build();
        String url = String.format(StandardCheckoutConstants.REFUND_STATUS_API, refundId);
        addStubForGetRequest(url, ImmutableMap.of(), getHeaders(), HttpStatus.SC_OK,
                ImmutableMap.of(), refundStatusResponse);

        RefundStatusResponse actual = standardCheckoutClient.getRefundStatus(refundId);
        Assertions.assertEquals(actual, refundStatusResponse);
    }

    @Test
    void testRefundThrows404() {
        String refundId = "234234";
        long amount = 100;
        String originalMerchantOrderId = "435435634";
        String url = StandardCheckoutConstants.REFUND_API;
        RefundRequest refundRequest = RefundRequest.builder()
                .merchantRefundId(refundId)
                .originalMerchantOrderId(originalMerchantOrderId)
                .amount(amount)
                .build();
        PhonePeResponse phonePeResponse = PhonePeResponse.<Map<String, String>>builder()
                .code("code")
                .message("Not Found")
                .data(Collections.singletonMap("a", "b"))
                .build();
        addStubForPostRequest(url, getHeaders(), refundRequest, HttpStatus.SC_NOT_FOUND, Maps.newHashMap(),
                phonePeResponse);

        final PhonePeException phonePeException = assertThrows(PhonePeException.class,
                () -> standardCheckoutClient.refund(refundRequest));
        Assertions.assertEquals(404, phonePeException.getHttpStatusCode());
        Assertions.assertEquals("Not Found", phonePeException.getMessage());
    }

    @Test
    void refundCustom() {
        String refundId = "234234";
        String url = CustomCheckoutConstants.REFUND_API;
        RefundRequest refundRequest = RefundRequest.builder()
                .merchantRefundId(refundId)
                .originalMerchantOrderId(merchantOrderId)
                .amount(amount)
                .build();
        RefundResponse refundResponse = RefundResponse.builder()
                .refundId("34534534")
                .state("CREATED")
                .amount(100)
                .build();
        Map<String, String> headers = getHeaders();

        addStubForPostRequest(url, headers, refundRequest, HttpStatus.SC_OK, Maps.newHashMap(), refundResponse);
        RefundResponse actual = customCheckoutClient.refund(refundRequest);
        Assertions.assertEquals(actual, refundResponse);
    }

    @Test
    void testRefundStatusCustom() {
        String refundId = "RefundId";
        RefundStatusResponse refundStatusResponse = RefundStatusResponse.builder()
                .merchantId("merchantId")
                .amount(100)
                .state("COMPLETED")
                .merchantRefundId("RefundId")
                .paymentDetails(Arrays.asList())
                .originalMerchantOrderId("merchantOrderId")
                .build();
        String url = String.format(CustomCheckoutConstants.REFUND_STATUS_API, refundId);
        addStubForGetRequest(url, ImmutableMap.of(), getHeaders(), HttpStatus.SC_OK,
                ImmutableMap.of(), refundStatusResponse);

        RefundStatusResponse actual = customCheckoutClient.getRefundStatus(refundId);
        Assertions.assertEquals(actual, refundStatusResponse);
    }

    @Test
    void testRefundSubscription() {
        String refundId = "234234";
        String url = SubscriptionConstants.REFUND_API;
        RefundRequest refundRequest = RefundRequest.builder()
                .merchantRefundId(refundId)
                .originalMerchantOrderId(merchantOrderId)
                .amount(amount)
                .build();
        RefundResponse refundResponse = RefundResponse.builder()
                .refundId("34534534")
                .state("CREATED")
                .amount(100)
                .build();
        Map<String, String> headers = getHeadersForSubscription();

        addStubForPostRequest(url, headers, refundRequest, HttpStatus.SC_OK, Maps.newHashMap(), refundResponse);
        RefundResponse actual = subscriptionClient.refund(refundRequest);
        Assertions.assertEquals(actual, refundResponse);
    }

    @Test
    void testRefundStatusSubscription() {
        String refundId = "RefundId";
        RefundStatusResponse refundStatusResponse = RefundStatusResponse.builder()
                .merchantId("merchantId")
                .amount(100)
                .state("COMPLETED")
                .merchantRefundId("RefundId")
                .paymentDetails(Arrays.asList())
                .originalMerchantOrderId("merchantOrderId")
                .build();
        String url = String.format(SubscriptionConstants.REFUND_STATUS_API, refundId);
        addStubForGetRequest(url, ImmutableMap.of(), getHeadersForSubscription(), HttpStatus.SC_OK,
                ImmutableMap.of(), refundStatusResponse);

        RefundStatusResponse actual = subscriptionClient.getRefundStatus(refundId);
        Assertions.assertEquals(actual, refundStatusResponse);
    }

    @Test
    void testRefundSubscriptionThrows404() {
        String refundId = "234234";
        long amount = 100;
        String originalMerchantOrderId = "435435634";
        String url = SubscriptionConstants.REFUND_API;
        RefundRequest refundRequest = RefundRequest.builder()
                .merchantRefundId(refundId)
                .originalMerchantOrderId(originalMerchantOrderId)
                .amount(amount)
                .build();
        PhonePeResponse phonePeResponse = PhonePeResponse.<Map<String, String>>builder()
                .code("code")
                .message("Not Found")
                .data(Collections.singletonMap("a", "b"))
                .build();
        addStubForPostRequest(url, getHeadersForSubscription(), refundRequest, HttpStatus.SC_NOT_FOUND,
                Maps.newHashMap(),
                phonePeResponse);

        final PhonePeException phonePeException = assertThrows(PhonePeException.class,
                () -> subscriptionClient.refund(refundRequest));
        Assertions.assertEquals(404, phonePeException.getHttpStatusCode());
        Assertions.assertEquals("Not Found", phonePeException.getMessage());
    }


}

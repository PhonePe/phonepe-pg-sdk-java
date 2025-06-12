import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.common.exception.BadRequest;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionConstants;
import com.phonepe.sdk.pg.subscription.v2.models.request.SubscriptionRedeemRequestV2;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionRedeemResponseV2;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class SubscriptionRedeemTest extends BaseSetupWithOAuth {

    @Test
    void testRedeemSuccess() {
        String url = SubscriptionConstants.REDEEM_API;

        String merchantOrderId = "<MERCHANT_ORDER_ID>";
        String responseJSON = "{\"transactionId\":\"OMxxx\",\"state\":\"PENDING\"}";

        SubscriptionRedeemResponseV2 responseObject = SubscriptionRedeemResponseV2.builder()
                .transactionId("OMxxx")
                .state("PENDING")
                .build();

        SubscriptionRedeemRequestV2 request = new SubscriptionRedeemRequestV2(merchantOrderId);
        Map<String, String> headers = getHeadersForSubscription();

        addStubForPostRequest(url, headers, request, HttpStatus.SC_OK, Maps.newHashMap(), responseJSON);

        SubscriptionRedeemResponseV2 actual = subscriptionClient.redeem(merchantOrderId);

        Assertions.assertEquals(responseObject, actual);
    }

    @Test
    void testRedeemFailure() {
        String url = SubscriptionConstants.REDEEM_API;

        String merchantOrderId = "<MERCHANT_ORDER_ID>";

        PhonePeResponse response = PhonePeResponse.<Map<String, String>>builder()
                .code("Bad Request")
                .message("message")
                .data(Collections.singletonMap("a", "b"))
                .build();

        SubscriptionRedeemRequestV2 request = new SubscriptionRedeemRequestV2(merchantOrderId);
        Map<String, String> headers = getHeadersForSubscription();

        addStubForPostRequest(url, headers, request, HttpStatus.SC_BAD_REQUEST, Maps.newHashMap(), response);

        final PhonePeException actual = assertThrows(PhonePeException.class,
                () -> subscriptionClient.redeem(merchantOrderId));

        Assertions.assertEquals(400, actual.getHttpStatusCode());
        Assertions.assertEquals("Bad Request", actual.getCode());
        Assertions.assertTrue(actual instanceof BadRequest);
    }
}

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableMap;
import com.phonepe.sdk.pg.common.exception.BadRequest;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionConstants;
import com.phonepe.sdk.pg.subscription.v2.models.request.AuthWorkflowType;
import com.phonepe.sdk.pg.subscription.v2.models.request.Frequency;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionStatusResponseV2;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class SubscriptionStatusResponse extends BaseSetupWithOAuth {

    @Test
    void testSubscriptionStatusSuccess() {

        String merchantSubscriptionId = "c61d7378-a081-44ab-9559-ad8563a24b49";

        String response = "{\"merchantSubscriptionId\": \"123456\", \"state\": \"ACTIVE\", \"authWorkflowType\": "
                + "\"PENNY_DROP\", \"frequency\": \"ON_DEMAND\"}";

        String url = String.format(SubscriptionConstants.SUBSCRIPTION_STATUS_API, merchantSubscriptionId);

        SubscriptionStatusResponseV2 responseObject = SubscriptionStatusResponseV2.builder()
                .merchantSubscriptionId("123456")
                .state("ACTIVE")
                .authWorkflowType(AuthWorkflowType.PENNY_DROP)
                .frequency(Frequency.ON_DEMAND)
                .build();

        Map<String, String> headers = getHeadersForSubscription();

        addStubForGetRequest(url, ImmutableMap.of(), headers, HttpStatus.SC_OK, ImmutableMap.of(), response);
        SubscriptionStatusResponseV2 actual = subscriptionClient.getSubscriptionStatus(merchantSubscriptionId);

        Assertions.assertEquals(responseObject, actual);
    }

    @Test
    void testSubscriptionStatusFailure() {

        String merchantSubscriptionId = "c61d7378-a081-44ab-9559-ad8563a24b49";

        String url = String.format(SubscriptionConstants.SUBSCRIPTION_STATUS_API, merchantSubscriptionId);

        PhonePeResponse response = PhonePeResponse.<Map<String, String>>builder()
                .code("Bad Request")
                .message("message")
                .data(Collections.singletonMap("a", "b"))
                .build();

        Map<String, String> headers = getHeadersForSubscription();

        addStubForGetRequest(url, ImmutableMap.of(), headers, HttpStatus.SC_BAD_REQUEST, ImmutableMap.of(), response);

        final PhonePeException actual = assertThrows(PhonePeException.class,
                () -> subscriptionClient.getSubscriptionStatus(merchantSubscriptionId));

        Assertions.assertEquals(400, actual.getHttpStatusCode());
        Assertions.assertEquals("Bad Request", actual.getCode());
        Assertions.assertTrue(actual instanceof BadRequest);
    }
}

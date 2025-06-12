import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionConstants;
import java.util.Map;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class CancelSubscriptionTest extends BaseSetupWithOAuth {

    @Test
    void testCancelSubscription() {
        wireMockServer.resetRequests();
        String merchantSubscriptionId = "c61d7378-a081-44ab-9559-ad8563a24b49";
        String url = String.format(SubscriptionConstants.CANCEL_API, merchantSubscriptionId);

        Map<String, String> headers = getHeadersForSubscription();

        addStubForPostRequest(url, headers, null, HttpStatus.SC_OK, Maps.newHashMap(), null);

        subscriptionClient.cancelSubscription(merchantSubscriptionId);

        wireMockServer.verify(exactly(1),
                postRequestedFor(urlPathMatching(url)));
    }

    @Test
    void testCancelSubscriptionIfResponseReceived() {
        wireMockServer.resetRequests();
        String merchantSubscriptionId = "c61d7378-a081-44ab-9559-ad8563a24b49";
        String url = String.format(SubscriptionConstants.CANCEL_API, merchantSubscriptionId);

        Map<String, String> headers = getHeadersForSubscription();

        String sampleJson = "{\"state\":\"PENDING\"}";

        addStubForPostRequest(url, headers, null, HttpStatus.SC_OK, Maps.newHashMap(), sampleJson);

        subscriptionClient.cancelSubscription(merchantSubscriptionId);

        wireMockServer.verify(exactly(1),
                postRequestedFor(urlPathMatching(url)));
    }
}

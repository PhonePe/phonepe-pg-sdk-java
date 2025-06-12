import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.tokenhandler.OAuthResponse;
import com.phonepe.sdk.pg.common.tokenhandler.TokenService;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import java.util.Map;
import okhttp3.FormBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class SingletonStandardCheckoutTest extends BaseSetup {

    FormBody formBody = new FormBody.Builder().add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("grant_type", "client_credentials")
            .add("client_version", String.valueOf(clientVersion))
            .build();

    @Test
    void testSingletonViaGetInstance() {
        StandardCheckoutClient standardCheckoutClient1 = StandardCheckoutClient.getInstance(clientId,
                clientSecret, clientVersion, env);
        StandardCheckoutClient standardCheckoutClient2 = StandardCheckoutClient.getInstance(clientId,
                clientSecret, clientVersion, env);

        Assertions.assertEquals(standardCheckoutClient1, standardCheckoutClient2);
    }

    @Test
    void testSingletonWithDiffParameters() {
        StandardCheckoutClient standardCheckoutClient1 = StandardCheckoutClient.getInstance(clientId,
                clientSecret, clientVersion, env);
        PhonePeException phonePeException = assertThrows(PhonePeException.class,
                () -> StandardCheckoutClient.getInstance("clientId2",
                        "clientSecret2", 1, Env.TEST));
        Assertions.assertEquals(phonePeException.getMessage(),
                "Cannot re-initialize StandardCheckoutClient. Please utilize the existing Client object with required"
                        + " credentials");

    }

    @Test
    void testMultipleClientSingleAuthCall() {
        wireMockServer.resetRequests();
        TokenService.setOAuthResponse(null);
        String redirectUrl = "https://redirectUrl.com";
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .merchantOrderId("merchantOrderId")
                        .amount(100)
                        .redirectUrl(redirectUrl)
                        .build();

        StandardCheckoutPayResponse standardCheckoutResponse = StandardCheckoutPayResponse.builder()
                .orderId(String.valueOf(java.time.Instant.now()
                        .getEpochSecond()))
                .state("PENDING")
                .expireAt(java.time.Instant.now()
                        .getEpochSecond())
                .redirectUrl("https://google.com")
                .build();

        long currentTime = java.time.Instant.now()
                .getEpochSecond();
        OAuthResponse oAuthResponse = OAuthResponse.builder()
                .accessToken("accessToken")
                .encryptedAccessToken("encryptedAccessToken")
                .expiresAt(currentTime + 200)
                .expiresIn(453543)
                .issuedAt(currentTime)
                .refreshToken("refreshToken")
                .tokenType("O-Bearer")
                .sessionExpiresAt(currentTime + 200)
                .build();

        StandardCheckoutClient standardCheckoutClient1 = StandardCheckoutClient.getInstance(clientId,
                clientSecret, clientVersion, env);
        StandardCheckoutClient standardCheckoutClient2 = StandardCheckoutClient.getInstance(clientId,
                clientSecret, clientVersion, env);
        StandardCheckoutClient standardCheckoutClient3 = StandardCheckoutClient.getInstance(clientId,
                clientSecret, clientVersion, env);
        StandardCheckoutClient standardCheckoutClient4 = StandardCheckoutClient.getInstance(clientId,
                clientSecret, clientVersion, env);

        final String url = StandardCheckoutConstants.PAY_API;
        addStubForPostRequest(url, getHeaders(), standardCheckoutPayRequest, HttpStatus.SC_OK, Maps.newHashMap(),
                standardCheckoutResponse);
        addStubForFormDataPostRequest(authUrl, getAuthHeaders(), formBody, HttpStatus.SC_OK, Maps.newHashMap(),
                oAuthResponse);
        StandardCheckoutPayResponse actual = standardCheckoutClient1.pay(standardCheckoutPayRequest);
        actual = standardCheckoutClient2.pay(standardCheckoutPayRequest);
        actual = standardCheckoutClient3.pay(standardCheckoutPayRequest);
        actual = standardCheckoutClient4.pay(standardCheckoutPayRequest);

        wireMockServer.verify(1,
                postRequestedFor(urlPathMatching(authUrl)));
    }

    public Map<String, String> getHeaders() {
        return ImmutableMap.<String, String>builder()
                .put(Headers.CONTENT_TYPE, APPLICATION_JSON)
                .put(Headers.SOURCE, Headers.INTEGRATION)
                .put(Headers.SOURCE_VERSION, Headers.API_VERSION)
                .put(Headers.SOURCE_PLATFORM, Headers.SDK_TYPE)
                .put(Headers.SOURCE_PLATFORM_VERSION, Headers.SDK_VERSION)
                .put(Headers.OAUTH_AUTHORIZATION, "O-Bearer accessToken")
                .build();
    }

}

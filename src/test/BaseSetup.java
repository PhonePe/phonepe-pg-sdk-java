import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.configs.CredentialConfig;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.events.publisher.EventPublisher;
import com.phonepe.sdk.pg.common.tokenhandler.OAuthResponse;
import com.phonepe.sdk.pg.common.tokenhandler.TokenConstants;
import com.phonepe.sdk.pg.common.tokenhandler.TokenService;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;
import java.util.Map;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;

public class BaseSetup extends BaseWireMockTest {

    protected StandardCheckoutClient standardCheckoutClient;

    protected CustomCheckoutClient customCheckoutClient;
    protected SubscriptionClient subscriptionClient;
    protected String clientId = "CLIENTID";
    protected String clientSecret =
            "CLIENTSECRET";
    protected Integer clientVersion = 1;
    protected Env env = Env.TEST;

    protected String merchantOrderId = "Merchant121";
    protected long amount = 100;
    final String authUrl = TokenConstants.OAUTH_GET_TOKEN;

    protected OkHttpClient okHttpClient = new OkHttpClient();
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected CredentialConfig credentialConfig = CredentialConfig.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .clientVersion(clientVersion)
            .build();

    OAuthResponse oAuthResponse = OAuthResponse.builder()
            .accessToken("accessToken")
            .encryptedAccessToken("encryptedAccessToken")
            .expiresAt(345435)
            .expiresIn(2432)
            .issuedAt(4535)
            .refreshToken("refreshToken")
            .tokenType("O-Bearer")
            .sessionExpiresAt(234543534)
            .build();

    TokenService tokenService = new TokenService(this.okHttpClient, objectMapper, credentialConfig, env,
            new EventPublisher() {
                @Override
                public void run() {

                }
            });

    @BeforeEach
    public void setUp() {
        this.standardCheckoutClient = StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        this.customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        this.subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret, clientVersion, env);
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

    public Map<String, String> getHeadersForSubscription() {
        return ImmutableMap.<String, String>builder()
                .put(Headers.CONTENT_TYPE, APPLICATION_JSON)
                .put(Headers.SOURCE, Headers.INTEGRATION)
                .put(Headers.SOURCE_VERSION, Headers.SUBSCRIPTION_API_VERSION)
                .put(Headers.SOURCE_PLATFORM, Headers.SDK_TYPE)
                .put(Headers.SOURCE_PLATFORM_VERSION, Headers.SDK_VERSION)
                .put(Headers.OAUTH_AUTHORIZATION, "O-Bearer accessToken")
                .build();
    }

    public Map<String, String> getAuthHeaders() {
        return ImmutableMap.<String, String>builder()
                .put("Content-Type", "application/x-www-form-urlencoded")
                .put("accept", "application/json")
                .build();
    }

}

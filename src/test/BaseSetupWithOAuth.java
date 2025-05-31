import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.common.tokenhandler.OAuthResponse;
import com.phonepe.sdk.pg.common.tokenhandler.TokenConstants;
import java.util.Map;
import okhttp3.FormBody;
import org.junit.jupiter.api.BeforeEach;
import wiremock.org.apache.http.HttpStatus;


//Cannot mock TokenService as it is getting created inside the StandardCheckoutClient
public class BaseSetupWithOAuth extends BaseSetup {

    OAuthResponse oAuthResponse;

    FormBody formBody = new FormBody.Builder().add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("grant_type", "client_credentials")
            .add("client_version", String.valueOf(clientVersion))
            .build();

    @BeforeEach
    void oauthsetup() {
        oAuthResponse = OAuthResponse.builder()
                .accessToken("accessToken")
                .encryptedAccessToken("encryptedAccessToken")
                .expiresAt(java.time.Instant.now()
                        .getEpochSecond() + 500)
                .expiresIn(2432)
                .issuedAt(java.time.Instant.now()
                        .getEpochSecond())
                .refreshToken("refreshToken")
                .tokenType("O-Bearer")
                .sessionExpiresAt(234543534)
                .build();

        Map<String, String> authHeaders = getAuthHeaders();
        final String authUrl = TokenConstants.OAUTH_GET_TOKEN;
        addStubForFormDataPostRequest(authUrl, authHeaders, formBody, HttpStatus.SC_OK, Maps.newHashMap(),
                oAuthResponse);
    }
}

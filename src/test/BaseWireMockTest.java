import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.SneakyThrows;
import okhttp3.FormBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseWireMockTest {

    public static final int WIREMOCK_PORT = 30419;

    public final ObjectMapper mapper = new ObjectMapper();

    private static final WireMockConfiguration wireMockConfiguration = new WireMockConfiguration()
            .port(WIREMOCK_PORT);
    protected static final WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration);

    @BeforeEach
    public void baseWireMockTestRunBeforeEach() {
        wireMockServer.start();
    }

    @AfterEach
    public void baseWireMockTestExecuteAfterEach() {
        Assertions.assertEquals(0, wireMockServer.findAllUnmatchedRequests()
                .size());
        wireMockServer.stop();
    }

    protected void addStubForGetRequest(final String urlPath, final int status, final Object response) {
        addStubForGetRequest(urlPath, ImmutableMap.of(), ImmutableMap.of(), status, ImmutableMap.of(), response);
    }

    @SneakyThrows
    protected void addStubForGetRequest(final String urlPath, final Map<String, String> queryParams,
            final Map<String, String> requestHeaders, final int status,
            final Map<String, String> responseHeaders, final Object response) {

        final MappingBuilder mappingBuilder = WireMock.get(WireMock.urlPathEqualTo(urlPath));
        requestHeaders.forEach((key, value) -> mappingBuilder.withHeader(key, WireMock.containing(value)));
        queryParams.forEach((key, value) -> mappingBuilder.withQueryParam(key, WireMock.equalTo(value)));

        final ResponseDefinitionBuilder responseDefinitionBuilder = WireMock.aResponse()
                .withStatus(status)
                .withBody(
                        response instanceof String
                                ? (String) response
                                : mapper.writeValueAsString(response)
                );
        responseHeaders.forEach(responseDefinitionBuilder::withHeader);
        wireMockServer.stubFor(mappingBuilder.willReturn(responseDefinitionBuilder));
    }

    protected void addStubForPostRequest(final String urlPath, final Object request, final int status,
            final Object response) {
        addStubForPostRequest(urlPath, ImmutableMap.of(), request, status, ImmutableMap.of(), response);
    }

    protected void addStubForPostRequest(final String urlPath, final Map<String, String> requestHeaders,
            final Object request,
            final int status, final Map<String, String> responseHeaders, final Object response) {
        addStubForPostRequest(urlPath, requestHeaders, request, status, responseHeaders, response, ImmutableMap.of());
    }

    @SneakyThrows
    protected void addStubForPostRequest(final String urlPath, final Map<String, String> requestHeaders,
            final Object request,
            final int status, final Map<String, String> responseHeaders, final Object response,
            final Map<String, String> queryParams) {
        final MappingBuilder mappingBuilder = WireMock.post(WireMock.urlPathEqualTo(urlPath))
                .withRequestBody(
                        equalTo(
                                request instanceof String
                                        ? (String) request
                                        : mapper.writeValueAsString(request)
                        )
                );

        requestHeaders.forEach((key, value) -> mappingBuilder.withHeader(key, WireMock.containing(value)));
        queryParams.forEach((key, value) -> mappingBuilder.withQueryParam(key, WireMock.equalTo(value)));
        final ResponseDefinitionBuilder responseDefinitionBuilder = WireMock.aResponse()
                .withStatus(status)
                .withBody(
                        response instanceof String
                                ? (String) response
                                : mapper.writeValueAsString(response)
                );

        responseHeaders.forEach(responseDefinitionBuilder::withHeader);
        StubMapping stubMapping = wireMockServer.stubFor(mappingBuilder.willReturn(responseDefinitionBuilder));
    }

    protected void addStubForFormDataPostRequest(final String urlPath, final Map<String, String> requestHeaders,
            final Object request,
            final int status, final Map<String, String> responseHeaders, final Object response) {
        String requestBody = formDataToString((FormBody) request);
        addStubForPostRequest(urlPath, requestHeaders, requestBody, status, responseHeaders, response,
                ImmutableMap.of());
    }

    protected String formDataToString(FormBody formBody) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < formBody.size(); i++) {
            if (i > 0) {
                builder.append("&");
            }
            builder.append(formBody.encodedName(i))
                    .append("=")
                    .append(formBody.encodedValue(i));
        }
        return builder.toString();
    }
}

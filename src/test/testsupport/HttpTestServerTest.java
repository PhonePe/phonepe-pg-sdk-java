package testsupport;

import static testsupport.HttpTestServer.response;

import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpTestServerTest {

    private final OkHttpClient client = new OkHttpClient();

    @Test
    void matchesHeadersQueryAndBodyAndRecordsUnmatchedRequests() throws Exception {
        try (HttpTestServer server = new HttpTestServer(0)) {
            server.start();
            server.stub("POST", "/pay", Map.of("Authorization", "token"), "expected",
                    Map.of("details", "a b"), response(200, "accepted"));
            Request request = new Request.Builder()
                    .url(server.url("/pay").newBuilder().addQueryParameter("details", "a b").build())
                    .header("Authorization", "Bearer token")
                    .post(RequestBody.create("expected", null))
                    .build();
            try (Response result = client.newCall(request).execute()) {
                Assertions.assertEquals(200, result.code());
                Assertions.assertEquals("accepted", result.body().string());
            }
            try (Response result = client.newCall(
                    request.newBuilder().post(RequestBody.create("wrong", null)).build()).execute()) {
                Assertions.assertEquals(404, result.code());
            }
            server.verifyRequests(2, "POST", "/pay");
            server.verifyRequests(1, "POST", "/pay", "expected");
            Assertions.assertEquals(1, server.unmatchedRequests().size());
        }
    }

    @Test
    void servesSequentialResponsesAndResetsStubsAndRecordedRequests() throws Exception {
        try (HttpTestServer server = new HttpTestServer(0)) {
            server.start();
            server.stubSequence("GET", "/token", List.of(response(503, ""), response(200, "token")));
            Request request = new Request.Builder().url(server.url("/token")).build();
            for (int status : List.of(503, 200, 200)) {
                try (Response result = client.newCall(request).execute()) {
                    Assertions.assertEquals(status, result.code());
                }
            }
            server.verifyRequests(3, "GET", "/token");
            server.resetAll();
            server.verifyRequests(0, "GET", "/token");
            try (Response result = client.newCall(request).execute()) {
                Assertions.assertEquals(404, result.code());
            }
        }
    }
}

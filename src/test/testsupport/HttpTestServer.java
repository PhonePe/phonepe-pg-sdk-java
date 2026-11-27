package testsupport;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.core.Response.Status;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Assertions;

public final class HttpTestServer implements AutoCloseable {

    private final MockWebServer server = new MockWebServer();
    private final List<Stub> stubs = new CopyOnWriteArrayList<>();
    private final List<RecordedRequest> requests = new CopyOnWriteArrayList<>();
    private final List<RecordedRequest> unmatched = new CopyOnWriteArrayList<>();
    private final int port;

    public HttpTestServer(int port) {
        this.port = port;
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                requests.add(request);
                for (Stub stub : stubs) {
                    if (stub.matches(request)) {
                        return stub.nextResponse().toMockResponse();
                    }
                }
                unmatched.add(request);
                return response(404, "").toMockResponse();
            }
        });
    }

    public void start() throws IOException {
        server.start(port);
    }

    public HttpUrl url(String path) {
        return server.url(path);
    }

    @Override
    public void close() throws IOException {
        server.shutdown();
    }

    public void stub(
            String method,
            String path,
            Map<String, String> headers,
            String body,
            Map<String, String> query,
            ResponseSpec response) {
        stubs.add(0, new Stub(method, path, headers, body, query, List.of(response)));
    }

    public void stub(String method, String path, ResponseSpec response) {
        stub(method, path, Map.of(), null, Map.of(), response);
    }

    public void stubSequence(String method, String path, List<ResponseSpec> responses) {
        if (responses.isEmpty()) {
            throw new IllegalArgumentException("A response sequence must not be empty");
        }
        stubs.add(0, new Stub(method, path, Map.of(), null, Map.of(), List.copyOf(responses)));
    }

    public List<RecordedRequest> requestsFor(String method, String path) {
        return requests.stream()
                .filter(request -> method.equals(request.getMethod())
                        && path.equals(Objects.requireNonNull(request.getRequestUrl()).encodedPath()))
                .toList();
    }

    public List<RecordedRequest> unmatchedRequests() {
        return List.copyOf(unmatched);
    }

    public void verifyRequests(int count, String method, String path) {
        Assertions.assertEquals(count, requestsFor(method, path).size(), method + " " + path);
    }

    public void verifyRequests(int count, String method, String path, String body) {
        Assertions.assertEquals(
                count,
                requestsFor(method, path).stream()
                        .filter(request -> body.equals(request.getBody().clone().readUtf8()))
                        .count(),
                method + " " + path + " with body " + body);
    }

    public void resetRequests() {
        requests.clear();
        unmatched.clear();
    }

    public void resetAll() {
        resetRequests();
        stubs.clear();
    }

    public static ResponseSpec response(int status, String body) {
        return new ResponseSpec(status, Map.of("Content-Type", "application/json"), body);
    }

    public record ResponseSpec(int status, Map<String, String> headers, String body) {
        private MockResponse toMockResponse() {
            Status responseStatus = Objects.requireNonNull(Status.fromStatusCode(status));
            MockResponse response = new MockResponse()
                    .setStatus("HTTP/1.1 " + status + " " + responseStatus.getReasonPhrase())
                    .setBody(body);
            headers.forEach(response::setHeader);
            return response;
        }
    }

    private static final class Stub {
        private final String method;
        private final String path;
        private final Map<String, String> headers;
        private final String body;
        private final Map<String, String> query;
        private final List<ResponseSpec> responses;
        private final AtomicInteger responseIndex = new AtomicInteger();

        private Stub(
                String method, String path, Map<String, String> headers, String body,
                Map<String, String> query, List<ResponseSpec> responses) {
            this.method = method;
            this.path = path;
            this.headers = Map.copyOf(headers);
            this.body = body;
            this.query = Map.copyOf(query);
            this.responses = responses;
        }

        private boolean matches(RecordedRequest request) {
            HttpUrl url = Objects.requireNonNull(request.getRequestUrl());
            return method.equals(request.getMethod())
                    && path.equals(url.encodedPath())
                    && (body == null || body.equals(request.getBody().clone().readUtf8()))
                    && headers.entrySet().stream().allMatch(entry ->
                            request.getHeader(entry.getKey()) != null
                                    && request.getHeader(entry.getKey()).contains(entry.getValue()))
                    && query.entrySet().stream().allMatch(entry ->
                            entry.getValue().equals(url.queryParameter(entry.getKey())));
        }

        private ResponseSpec nextResponse() {
            return responses.get(Math.min(responseIndex.getAndIncrement(), responses.size() - 1));
        }
    }
}

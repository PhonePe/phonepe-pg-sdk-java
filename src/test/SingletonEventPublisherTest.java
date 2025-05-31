import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.common.events.publisher.EventPublisher;
import com.phonepe.sdk.pg.common.events.publisher.EventPublisherFactory;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingletonEventPublisherTest {

    @Test
    void testEventPublisherWithDifferentClients() {

        EventPublisherFactory eventPublisherFactory1 = new EventPublisherFactory(new ObjectMapper(), new OkHttpClient(),
                "hostUrl");
        EventPublisherFactory eventPublisherFactory2 = new EventPublisherFactory(new ObjectMapper(), new OkHttpClient(),
                "hostUrl");

        EventPublisher eventPublisher1 = eventPublisherFactory1.getEventPublisher(true);
        EventPublisher eventPublisher2 = eventPublisherFactory2.getEventPublisher(true);

        Assertions.assertEquals(eventPublisher1, eventPublisher2);
    }
}

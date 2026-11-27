import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JacksonConstraintsTest {

    @Test
    void rejectsOversizedCharacterBackedPropertyNames() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().setStreamReadConstraints(
                StreamReadConstraints.builder().maxNameLength(32).build());

        Assertions.assertThrows(
                StreamConstraintsException.class,
                () -> mapper.readValue("{\"" + "a".repeat(33) + "\":1}", Map.class));
    }

    @Test
    void acceptsNormalCharacterBackedPropertyNames() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().setStreamReadConstraints(
                StreamReadConstraints.builder().maxNameLength(32).build());

        Assertions.assertEquals(
                Map.of("amount", 10000), mapper.readValue("{\"amount\":10000}", Map.class));
    }
}

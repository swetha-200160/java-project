import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SampleTest {

    @Test
    void testPass() {
        int result = 2 + 2;
        assertEquals(4, result); // ✅ PASS
    }

    @Test
    void testFail() {
        int result = 2 + 2;
        assertEquals(5, result); // ❌ FAIL
    }
}
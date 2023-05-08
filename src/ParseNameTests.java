import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParseNameTests {
    @Test
    void validGeneric() {
        Assertions.assertTrue(Main.parseName("Reimu"));
    }
    @Test
    void validDashes() {
        Assertions.assertTrue(Main.parseName("Thuy-Trang"));
    }
    @Test
    void validApostrophe() {
        Assertions.assertTrue(Main.parseName("La'Hal"));
    }
    @Test
    void rejectSpaces() {
        Assertions.assertFalse(Main.parseName("Test Test"));
    }
    @Test
    void rejectNumbers() {
        Assertions.assertFalse(Main.parseName("Test123"));
    }
    @Test
    void rejectBlank() {
        Assertions.assertFalse(Main.parseName(""));
    }
    @Test
    void rejectFiftyOne() {
        Assertions.assertFalse(Main.parseName(
                "ThisIsATenThisIsATenThisIsATenThisIsATenThisIsATenA"));
    }
}

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParsePasswordTests {
    @Test
    void validPassword() {
        Assertions.assertTrue(Main.parsePassword("Password123!"));
    }
    @Test
    void rejectNoUppercase() {
        Assertions.assertFalse(Main.parsePassword("password123!"));
    }
    @Test
    void rejectNoLowercase() {
        Assertions.assertFalse(Main.parsePassword("PASSWORD123!"));
    }
    @Test
    void rejectNoNumber() {
        Assertions.assertFalse(Main.parsePassword("Password!"));
    }
    @Test
    void rejectNoSpecialChar() {
        Assertions.assertFalse(Main.parsePassword("Password123"));
    }
    @Test
    void rejectTooShort() {
        Assertions.assertFalse(Main.parsePassword("Pass1!"));
    }


}

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParsePasswordTests {
    @Test
    void validPassword() {
        Assertions.assertTrue(Main.parsePassword("12345aB!?_"));
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

    @Test
    void testValidLength() {
        Assertions.assertTrue(Main.parsePassword("aB!4567890"));
    }

    @Test
    void testValidOneUpperCase() {
        Assertions.assertTrue(Main.parsePassword("aB0!!!!!!!"));
    }

    @Test
    void testValidOneDigit() {
        Assertions.assertTrue(Main.parsePassword("aB!aBaBaB0"));
    }

    @Test
    void testValidOnePunctuation() {
        Assertions.assertTrue(Main.parsePassword("aB!4567890"));
        Assertions.assertTrue(Main.parsePassword(".aB4567890"));
        Assertions.assertTrue(Main.parsePassword("aB456,7890"));
        Assertions.assertTrue(Main.parsePassword("aB4567890?"));
    }

    @Test
    void testValidThreeLowerCase() {
        Assertions.assertTrue(Main.parsePassword("aB!aaa7890"));
    }

    @Test
    void testRejectLessThanTen() {
        Assertions.assertFalse(Main.parsePassword("aB!456789"));
    }

    @Test
    void testRejectNoUpperCase() {
        Assertions.assertFalse(Main.parsePassword("ab!4567890"));
    }

    @Test
    void testRejectNoDigit() {
        Assertions.assertFalse(Main.parsePassword("aB!cdDfgGijk"));
    }

    @Test
    void testRejectNoPunctuation() {
        Assertions.assertFalse(Main.parsePassword("aBCcdDfgGijk0"));
    }

    @Test
    void testRejectMoreConsecutiveLowerCase() {
        Assertions.assertFalse(Main.parsePassword("!abccDDfgGijk0"));
    }

}

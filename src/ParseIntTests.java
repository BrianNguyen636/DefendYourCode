import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParseIntTests {
    @Test
    void validInt() {
        Assertions.assertTrue(Main.parseInts("2015"));
    }

    @Test
    void validNegative() {
        Assertions.assertTrue(Main.parseInts("-2015"));
        Assertions.assertTrue(Main.parseInts("-2000000009"));
    }

    @Test
    void validMax() {
        Assertions.assertTrue(Main.parseInts("2147483647"));
    }

    @Test
    void validNegativeMax() {
        Assertions.assertTrue(Main.parseInts("-2147483648"));
    }

    @Test
    void validZero() {
        Assertions.assertTrue(Main.parseInts("0"));
    }

    @Test
    void validNegativeZero() {
        Assertions.assertTrue(Main.parseInts("-0"));
    }

    @Test
    void rejectOutOfBounds() {
        Assertions.assertFalse(Main.parseInts("-2147483649"));
        Assertions.assertFalse(Main.parseInts("2147483648"));
    }

    @Test
    void rejectBlank() {
        Assertions.assertFalse(Main.parseInts(""));
    }

    @Test
    void rejectOnlyDash() {
        Assertions.assertFalse(Main.parseInts("-"));
    }
}

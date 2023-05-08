import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParseFileTests {
    @Test
    void testValidTXT() {
        Assertions.assertTrue(Main.parseFile("Filename.txt"));
    }
    @Test
    void testValidNumbers() {
        Assertions.assertTrue(Main.parseFile("Filename12234.txt"));
    }
    @Test
    void testValidSpecialCharacters() {
        Assertions.assertTrue(Main.parseFile("Filename@!#@$%.txt"));
    }
    @Test
    void testValidSpaces() {
        Assertions.assertTrue(Main.parseFile("File name.txt"));
    }
    @Test
    void testValidOneLetter() {
        Assertions.assertTrue(Main.parseFile("e.txt"));
    }
    @Test
    void testRejectNoExtension() {
        Assertions.assertFalse(Main.parseFile("test"));
    }
    @Test
    void testRejectWrongExtension() {
        Assertions.assertFalse(Main.parseFile("test.md"));
    }
}

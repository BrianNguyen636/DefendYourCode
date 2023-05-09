import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Team18
 * Andrew Nguyen
 * Anthony Nguyen
 * Brian Nguyen
 *
 * Collect first and last name, two integers, and two file names from user.
 * Asks user for password and validates it. Write the collected information
 * into one of the given user files.
 *
 * @author Anthony Nguyen
 * @author Brian Nguyen
 */
public class Main {

    /**
     * Logger for exceptions.
     **/
    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    /**
     * Scanner object for keyboard.
     */
    private static final Scanner IN = new Scanner(System.in);

    /**
     * Directory of code.
     */
    private static final String CURR_DIR =
            System.getProperty("user.dir") + File.separator;

    /**
     * Main executing method.
     *
     * @param args  String command line arguments
     */
    public static void main(final String[] args) {
        final String[] names = getName();
        final int[] ints = getInts();
        final File[] files = getFiles();
        getPassword();
        saveInfo(names, ints, files);
        IN.close();
    }

    /**
     * Writes given information as well as some calculated into a given file.
     *
     * @param names     String first and last names
     * @param ints      Two integers
     * @param files     Input and output files
     */
    private static void saveInfo(final String[] names, final int[] ints,
                                 final File[] files) {
        try (final FileWriter shakespeare = new FileWriter(files[1])) {
            final BigInteger sum =
                    BigInteger.valueOf(ints[0]).add(BigInteger.valueOf(ints[1]));
            final BigInteger product =
                    BigInteger.valueOf(ints[0]).multiply(BigInteger.valueOf(ints[1]));
            final String contents =
                    new String(Files.readAllBytes(Paths.get(files[0].getPath())));
            shakespeare.write("First name: " + names[0]);
            shakespeare.write("\nLast name: " + names[1]);
            shakespeare.write("\nFirst Integer: " + ints[0]);
            shakespeare.write("\nSecond Integer: " + ints[1]);
            shakespeare.write("\nSum: " + sum);
            shakespeare.write("\nProduct: " + product);
            shakespeare.write("\nInput file name: " + files[0].getName());
            shakespeare.write("\nInput file contents:\n" + contents);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occurred", e);
        }
    }

    /**
     * Denotes what sort of test is appropriate.
     */
    private enum TEST {name, integer, file, password}

    /**
     * Text prompt for information that loops until the user given information
     * passes a particular test.
     *
     * @param displayText   Given text prompt to display
     * @param type          Type of test necessary for the info
     * @return  return valid information
     */
    private static String loopingPrompt(final String displayText,
                                        final TEST type) {
        String input = null;
        boolean valid = false;
        while (!valid) {
            System.out.printf("\n%s: ", displayText);
            input = IN.nextLine();
            switch (type) {
                case name -> valid = parseName(input);
                case integer -> valid = parseInts(input);
                case file -> valid = parseFile(input);
                case password -> valid = parsePassword(input);
                default -> System.out.println("Shouldn't reach this!");
            }
        }
        return input;
    }

    /**
     * Regex matcher helper function
     *
     * @param theRegex The pattern string
     * @param theInput The string to be pattern matched
     * @return Boolean if the input string matches the pattern
     */
    private static boolean regex(final String theRegex, final String theInput) {
        final Pattern p = Pattern.compile(theRegex);
        final Matcher m = p.matcher(theInput);
        return m.matches();
    }

    /**
     * Prompts for a password, applies salting and hashing, and then writes
     * it to an output file. Asks for password validation and compares it to
     * the first password file until it gets a match. Cleans up afterward.
     */
    public static void getPassword() {
        final String criteria = """
                Your password must meet the following criteria:
                \t- At least ten characters long
                \t- At least one uppercase letter
                \t- At least one digit
                \t- At least one lowercase letter
                \t- At least one special character (?!,:;',._)
                \t- No more than 3 consecutive lowercase characters
                Your password""";
        final String pw = loopingPrompt(criteria, TEST.password).trim();
        final byte[] salt = genSalt();
        final String path = CURR_DIR + Arrays.toString(salt) + ".txt";
        try {
            Files.write(Paths.get(path), genHash(salt, pw));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occurred", e);
        }
        final String verify = "Re-enter your password";
        boolean valid = false;
        while (!valid) {
            final String pw2 = loopingPrompt(verify, TEST.password).trim();
            valid = validatePassword(pw2, salt, path);
        }
        new File(path).deleteOnExit();
    }

    /**
     * Generates a salt using SecureRandom.
     *
     * @return  16 bytes of random bits
     */
    private static byte[] genSalt() {
        final byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Generates a hash given a salt and a password.
     *
     * @param salt      16 byte random data
     * @param password  User given password String
     * @return  The hash of the salt and password
     */
    private static byte[] genHash(final byte[] salt, final String password) {
        byte[] hashed = null;
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occurred", e);
        }
        return hashed;
    }

    /**
     * Validates a password against a previously salted and hashed stored
     * password.
     *
     * @param password2 The password to check
     * @param salt      The same salt previously used
     * @param path      The path to where the previous salted/hashed password is
     * @return  true if the passwords match, else false
     */
    private static boolean validatePassword(final String password2,
                                            final byte[] salt,
                                            final String path) {
        final String admonition = "Password storage issue occurred.";
        boolean valid = false;
        if (new File(path).exists()) {
            try {
                final byte[] hash = Files.readAllBytes(Paths.get(path));
                valid = Arrays.equals(genHash(salt, password2), hash);
            } catch (Exception e) {
                System.out.println(admonition);
                LOGGER.log(Level.SEVERE, "Exception occurred", e);
            }
        } else System.out.println(admonition);
        return valid;
    }

    /**
     * Checks if the input string contains the necessary password requirements
     *
     * @param theInput input string
     * @return true/false if pattern matches
     */
    public static boolean parsePassword(final String theInput) {
        return regex("^(?!.*([a-z]{4}))(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\\d)(?=" + ".*?[?!,:;',._\"])[\\w?!,:;',.\"]{10,}$", theInput.trim());
    }

    /**
     * Denotes whether a file is to be used as input or output.
     */
    private enum fileType {input, output}

    /**
     * Prompts user for input and output filenames.
     *
     * @return Array of 2 strings containing the filenames.
     */
    private static File[] getFiles() {
        final String criteria = """
                %s file name must meet the following criteria:
                \t- File must be in the code's directory
                \t- Name must be at least one character
                \t- Extension must be included and can only be .txt
                \t- Cannot have these special characters (/\\:*?"<>|)
                %s file name""";
        final String[] prompt = new String[]{"Your input", "Your output"};
        final fileType[] type = new fileType[]{fileType.input, fileType.output};
        final File[] files = new File[2];
        for (int i = 0; i < files.length; i++) {
            boolean valid = false;
            while (!valid) {
                final String potential = loopingPrompt(String.format(criteria
                        , prompt[i], prompt[i]), TEST.file).trim();
                if (checkFile(potential, type[i])) {
                    files[i] = new File(potential);
                    valid = true;
                }
            }
        }
        return files;
    }

    /**
     * Helper function to pattern match the filenames
     *
     * @param theInput Input String
     * @return true/false if matches the pattern or not
     */
    public static boolean parseFile(final String theInput) {
        return regex("^[^/\\\\:*?\"<>|]+\\.txt$", theInput.trim());
    }

    /**
     * Checks if a file exists and is readable for input. For output, checks if
     * a file exists; if it does, it must be writable.
     *
     * @param fileName  String name of file
     * @param type      Nature of the file, input or output
     * @return  true if above conditions satisfied, else false
     */
    private static boolean checkFile(final String fileName,
                                     final fileType type) {
        final String admonition = "Enter a valid file name.";
        boolean valid = false;
        try {
            File inFile = new File(CURR_DIR + fileName.trim());
            if (type == fileType.input) {
                valid = inFile.exists() && inFile.setReadable(true);
            } else if (type == fileType.output) {
                if (inFile.exists()) valid = inFile.setWritable(true);
                else valid = true;
            } else System.out.println("Shouldn't reach this!");
        } catch (Exception e) {
            System.out.println(admonition);
            LOGGER.log(Level.SEVERE, "Exception occurred", e);
        }
        return valid;
    }

    /**
     * Prompts the user for two integers if they are in bounds.
     *
     * @return Array of 2 integers.
     */
    private static int[] getInts() {
        final String criteria = """
                Integers must meet the following criteria:
                \t- Value must range from -2147483648 to 2147483647
                \t- Proper commas usage is allowed but optional
                Enter integer""";
        final int[] ints = new int[2];
        for (int i = 0; i < ints.length; i++)
            ints[i] =
                    Integer.parseInt(loopingPrompt(criteria, TEST.integer).trim().replace(",", ""));
        return ints;
    }

    /**
     * Helper function to regex integer input
     *
     * @param theInput String of integers
     * @return Boolean if matches the pattern (within bounds)
     */
    public static boolean parseInts(final String theInput) {
        final String admonition = "Enter a valid integer value.";
        final String in = theInput.trim();
        boolean valid = false;
        if (regex("^-?\\d{1,3}((,\\d{3}){0,3}|(\\d{3}){0,3})?$", in)) {
            // 2147483647
            try {
                Integer.parseInt(in.replace(",", ""));
                valid = true;
            } catch (Exception e) {
                System.out.println(admonition);
                LOGGER.log(Level.SEVERE, "Exception occurred", e);
            }
        } else System.out.println(admonition);
        return valid;
    }

    /**
     * Prompts the first and last name from user
     *
     * @return A String array with the First and Last name on index 0 and 1.
     */
    private static String[] getName() {
        final String criteria = """
                %s name must meet the following criteria:
                \t- 1 to 50 characters long
                \t- Only English upper and lower case letters
                \t- Dashes and apostrophes are allowed but optional
                \t- Those special characters must be preceded and followed by letters
                %s name""";
        final String[] names = new String[]{"Your first", "Your last"};
        for (int i = 0; i < names.length; i++)
            names[i] = loopingPrompt(String.format(criteria, names[i],
                    names[i]), TEST.name).trim();
        return names;
    }

    /**
     * Only allows Upper/Lowercase letters, apostrophe's and dashes, and
     * length between 1 and 50.
     *
     * @param theInput The string to parse
     * @return Boolean if it fits the requirements
     */
    public static boolean parseName(final String theInput) {
        final String in = theInput.trim();
        return in.length() > 0 && in.length() < 51 && regex("^[a-zA-Z]+" +
                "(['-]?[a-zA-Z]+)*$", in);
    }
}

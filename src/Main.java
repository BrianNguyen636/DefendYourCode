/*
Team18
Andrew Nguyen
Anthony Nguyen
Brian Nguyen
 */

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO fix file tests

public class Main {

    private static final Scanner IN = new Scanner(System.in);

    public static void main(final String[] args) {
        final String[] names = getName();
        final int[] ints = getInts();
        final File[] files = getFiles();
        getPassword();
        saveInfo(names, ints, files);
        IN.close();
    }

    private static void saveInfo(final String[] names, final int[] ints,
                                 final File[] files) {
        try (FileWriter shakespear = new FileWriter(files[1])) {
            final BigInteger sum =
                    BigInteger.valueOf(ints[0]).add(BigInteger.valueOf(ints[1]));
            final BigInteger product =
                    BigInteger.valueOf(ints[0]).multiply(BigInteger.valueOf(ints[1]));
            final String contents =
                    new String(Files.readAllBytes(Paths.get(files[0].getPath())));
            shakespear.write("First name: " + names[0]);
            shakespear.write("\nLast name: " + names[1]);
            shakespear.write("\nFirst Integer: " + ints[0]);
            shakespear.write("\nSecond Integer: " + ints[1]);
            shakespear.write("\nSum: " + sum);
            shakespear.write("\nProduct: " + product);
            shakespear.write("\nInput file name: " + files[0].getName());
            shakespear.write("\nInput file contents:\n" + contents);
        } catch (Exception e) {
            // TODO log e
        }
    }

    private enum TEST {name, integer, file, password}

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
                default ->
                        System.out.println("Shouldn't be able to reach this!");
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
     * Prompts for a password, applies the proper salting and hashing via
     * SecureRandom and SHA-512 respectively, and then writes it to an output
     * file.
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
        final String path = Paths.get("").toAbsolutePath() + "Password.txt";
        try {
            Files.write(Paths.get(path), genHash(salt, pw));
        } catch (Exception e) {
            // TODO log e
        }
        final String verify = "Re-enter your password";
        boolean valid = false;
        while (!valid) {
            final String pw2 = loopingPrompt(verify, TEST.password).trim();
            valid = validatePassword(pw2, salt, path);
        }
        new File(path).deleteOnExit();
    }

    private static byte[] genSalt() {
        final byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static byte[] genHash(final byte[] salt, final String password) {
        byte[] hashed = null;
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            // TODO log e
        }
        return hashed;
    }

    private static boolean validatePassword(final String password2,
                                            final byte[] salt,
                                            final String path) {
        final String admonition = "File issue occurred.";
        boolean valid = false;
        if (new File(path).exists()) {
            try {
                final byte[] hash = Files.readAllBytes(Paths.get(path));
                valid = Arrays.equals(genHash(salt, password2), hash);
            } catch (Exception e) {
                System.out.println(admonition);
                // TODO log e
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
        return regex("^(?!.*([a-z]{4}))(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\\d)(?=" +
                ".*?[?!,:;',._\"])[\\w?!,:;',.\"]{10,}$", theInput.trim());
    }

    /**
     * Prompts user for input and output filenames.
     *
     * @return Array of 2 strings containing the filenames.
     */
    private static File[] getFiles() {
        final String criteria = """
                %s file name must meet the following criteria:
                \t- Full path must be included
                \t- Extension must be included
                \t- Extension can only be .txt
                %s file name""";
        final String[] prompt = new String[]{"Your input", "Your output"};
        final File[] files = new File[2];
        for (int i = 0; i < files.length; i++)
            files[i] = new File(loopingPrompt(String.format(criteria,
                    prompt[i], prompt[i]), TEST.file).trim());
        return files;
    }

    /**
     * Helper function to pattern match the filenames
     *
     * @param theInput Input String
     * @return true/false if matches the pattern or not
     */
    public static boolean parseFile(final String theInput) {
        final String admonition = "Enter a valid file name.";
        boolean valid = false;
        String in = theInput.trim();
        if (regex("^.+\\.txt$", in)) {
            try {
                if (new File(in).exists()) valid = true;
                else System.out.println(admonition);
            } catch (Exception e) {
                System.out.println(admonition);
                // TODO log error
            }
        } else System.out.println(admonition);
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
                // TODO print to error log file
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

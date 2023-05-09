import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Scanner IN = new Scanner(System.in);

    public static void main(String[] args) {
        //Get Names
//        String[] name = getName();
//        String firstName = name[0];
//        String lastName = name[1];
//        System.out.println(firstName + " " + lastName);
        //Get Ints
        int[] ints = getInts();
        int firstInt = ints[0];
        int secondInt = ints[1];
        System.out.println(firstInt);
        System.out.println(secondInt);
        //Get input file
//        File[] files = getFiles();
//        File inputFile = files[0];
//        File outputFile = files[1];
//        getPassword();
    }

    private enum TEST {name, integer, file, password}

    public static String loopingPrompt(String displayText, TEST type) {
        String input = null;
        boolean valid = false;
        while (!valid) {
            System.out.println();
            System.out.print(displayText + ": ");
            input = IN.nextLine();
            switch (type) {
                case name -> valid = parseName(input);
                case integer -> valid = parseInts(input);
                case file -> valid = parseFile(input);
                case password -> valid = parsePassword(input);
            }
        }
        return input;
    }

    /**
     * Prompts for a password, then applies the proper salting and hashing
     * and writes it to an output file.
     */
    public static void getPassword() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a new password.");
        String pass;
        boolean valid = false;
        while (!valid) {
            System.out.println();
            System.out.println("Must be at least 8 characters, and at least " +
                    "one uppercase and lowercase,");
            System.out.println("at least one special character and at least " +
                    "one digit.");
            System.out.print("Password: ");
            pass = scanner.nextLine();
            valid = parsePassword(pass);
        }
    }

    /**
     * Checks if the input string contains the necessary password requirements
     *
     * @param theInput input string
     * @return true/false if pattern matches
     */
    public static boolean parsePassword(final String theInput) {
        return regex(".*[A-Z].*", theInput) && regex(".*[a-z].*", theInput) && regex(".*[^\\w\\d].*", theInput) && regex(".*\\d.*", theInput) && !regex(".{0,7}", theInput);
    }

    /**
     * Prompts user for input and output filenames.
     *
     * @return Array of 2 strings containing the filenames.
     */
    public static File[] getFiles() {
        Scanner scanner = new Scanner(System.in);
        File outputFile = null;
        File inputFile = null;
        boolean valid = false;
        while (!valid) {
            System.out.println();
            System.out.println("Please enter the name of your input file " +
                    "including the extension (.txt only)");
            System.out.print("Filename: ");
            String input = scanner.nextLine();
            valid = parseFile(input);
            if (!valid) {
                System.out.println("Improper file name.");
            } else {
                inputFile = new File(input);
                valid = inputFile.exists();
                if (!valid)
                    System.out.println("File does not exist in directory.");
            }
        }
        valid = false;
        while (!valid) {
            System.out.println();
            System.out.println("Please enter the name of your output file " +
                    "including the extension (.txt only)");
            System.out.print("Filename: ");
            String output = scanner.nextLine();
            valid = parseFile(output);
            if (!valid) {
                System.out.println("Improper file name.");
            } else {
                outputFile = new File(output);
            }
        }
        return new File[]{inputFile, outputFile};
    }

    /**
     * Helper function to pattern match the filenames
     *
     * @param theInput Input String
     * @return true/false if matches the pattern or not
     */
    public static boolean parseFile(final String theInput) {
        return regex(".+\\.txt", theInput);
    }

    /**
     * Prompts the user for two integers if they are in bounds.
     *
     * @return Array of 2 integers.
     */
    public static int[] getInts() {
        String prompt = "Enter an int (Range -2147483648 to 2147483647, proper commas optional)";
        final int[] ints = new int[2];
        for (int i = 0; i < ints.length; i++)
            ints[i] = Integer.parseInt(loopingPrompt(prompt, TEST.integer).trim().replace(",", ""));
        return ints;
    }

    /**
     * Helper function to regex integer input
     *
     * @param theInput String of integers
     * @return Boolean if matches the pattern (within bounds)
     */
    public static boolean parseInts(final String theInput) {
        String in = theInput.trim();
        boolean valid = false;
        if (regex("^-?\\d{1,3}((,\\d{3}){0,3}|(\\d{3}){0,3})?$", in)) {
            //2147483647
            try {
                Integer.parseInt(in.replace(",", ""));
                valid = true;
            } catch (Exception e) {
                System.out.println("Enter a valid integer value.");
            }
        } else System.out.println("Enter a valid integer value.");
        return valid;
    }

    /**
     * Prompts the first and last name from user
     *
     * @return A String array with the First and Last name on index 0 and 1.
     */
    public static String[] getName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your first name.");
        String first = "";
        String last = "";
        boolean valid = false;
        while (!valid) {
            System.out.println();
            System.out.println("Input must be between 1 and 50 characters " +
                    "long.");
            System.out.println("Only standard upper and lower case letters, " +
                    "dashes, and apostrophe's allowed");
            System.out.print("First name: ");
            first = scanner.nextLine();
            valid = parseName(first);
        }
        System.out.println("Please enter your last name.");
        valid = false;
        while (!valid) {
            System.out.println();
            System.out.println("Input must be between 1 and 50 characters " +
                    "long.");
            System.out.println("Only standard upper and lower case letters, " +
                    "dashes, and apostrophe's allowed");
            System.out.print("Last name: ");
            last = scanner.nextLine();
            valid = parseName(last);
        }
        return new String[]{first, last};
    }

    /**
     * Only allows Upper/Lowercase letters, apostrophe's and dashes, and
     * length between 1 and 50.
     *
     * @param theInput The string to parse
     * @return Boolean if it fits the requirements
     */
    public static boolean parseName(final String theInput) {
        return regex("[A-Za-z'-]{1,50}", theInput);
    }

    /**
     * Regex matcher helper function
     *
     * @param theRegex The pattern string
     * @param theInput The string to be pattern matched
     * @return Boolean if the input string matches the pattern
     */
    public static boolean regex(final String theRegex, final String theInput) {
        Pattern p = Pattern.compile(theRegex);
        Matcher m = p.matcher(theInput);
        return m.matches();
    }
}

package edu.utsa.cs3443.mydosemate.model;

import java.util.regex.Pattern;

/**
 * Utility class containing methods for validating user input.
 * Validation methods throw an IllegalArgumentException when
 * the supplied value is invalid.
 */
public class UserInputValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\d{10}$");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}'-]+(?:\\s+[\\p{L}'-]+)*$");

    /**
     * Prevents instantiation of this utility class.
     */
    private UserInputValidator() {
    }

    /**
     * Validates all fields required when creating a user.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address (optional)
     * @param phone the user's phone number (optional)
     * @return an array containing validation error messages for each
     *         field in the following order:
     *         first name, last name, email, phone.
     *         A null entry indicates the corresponding field is valid.
     */
    public static String[] validateUserCreation(
            String firstName,
            String lastName,
            String email,
            String phone
    ) {
        String[] errors = new String[4];

        try {
            validateName(firstName);
        } catch (IllegalArgumentException e) {
            errors[0] = e.getMessage();
        }

        try {
            validateName(lastName);
        } catch (IllegalArgumentException e) {
            errors[1] = e.getMessage();
        }

        try {
            validateEmail(email);
        } catch (IllegalArgumentException e) {
            errors[2] = e.getMessage();
        }

        try {
            validatePhone(phone);
        } catch (IllegalArgumentException e) {
            errors[3] = e.getMessage();
        }

        return errors;
    }

    /**
     * Validates a user's first or last name.
     *
     * @param name the name to validate
     * @throws IllegalArgumentException if the name is blank,
     *         too short, too long, or contains invalid characters
     */
    public static void validateName(String name)
            throws IllegalArgumentException {

        if (isBlank(name)) {
            throw new IllegalArgumentException("Name is required.");
        }

        String trimmedName = name.trim();

        if (trimmedName.length() < 2) {
            throw new IllegalArgumentException(
                    "Name must be at least 2 characters long."
            );
        }

        if (trimmedName.length() > 50) {
            throw new IllegalArgumentException(
                    "Name cannot be longer than 50 characters."
            );
        }

        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            throw new IllegalArgumentException(
                    "Name can only contain letters, spaces, apostrophes, and hyphens."
            );
        }
    }

    /**
     * Validates an email address.
     * A blank email is considered valid because the field is optional.
     *
     * @param email the email address to validate
     * @throws IllegalArgumentException if the email format is invalid
     */
    public static void validateEmail(String email)
            throws IllegalArgumentException {

        if (isBlank(email)) {
            return; // email not required
        }

        String trimmedEmail = email.trim();

        if (trimmedEmail.length() > 254) {
            throw new IllegalArgumentException("Email is too long.");
        }

        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new IllegalArgumentException(
                    "Enter a valid email address."
            );
        }
    }

    /**
     * Validates a phone number.
     * A blank phone number is considered valid because the field is optional.
     *
     * @param phone the phone number to validate
     * @throws IllegalArgumentException if the phone number does not
     *         contain exactly ten digits
     */
    public static void validatePhone(String phone)
            throws IllegalArgumentException {

        if (isBlank(phone)) {
            return; // phone not required
        }

        String cleanedPhone = phone.replaceAll("\\D", "");

        if (!PHONE_PATTERN.matcher(cleanedPhone).matches()) {
            throw new IllegalArgumentException(
                    "Phone number must contain exactly 10 digits."
            );
        }
    }

    /**
     * Determines whether a string is null, empty, or contains only
     * whitespace characters.
     *
     * @param str the string to check
     * @return true if the string is blank; false otherwise
     */
    private static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }


}
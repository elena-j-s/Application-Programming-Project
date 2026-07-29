package edu.utsa.cs3443.mydosemate.model;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

/**
 * Manages the application's single persisted user record in {@code data/user.csv}.
 * <p>
 * This class is used to detect first launch, load an existing user, and create a
 * new user file from user-provided input.
 */
public class UserManager {

    private User user;
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path USER_FILE = DATA_DIR.resolve("user.csv");

    /**
     * Checks whether the persisted user file exists.
     *
     * @return {@code true} if {@code data/user.csv} exists, otherwise {@code false}
     */
    public boolean userFileExists() {
        return Files.exists(USER_FILE);
    }

    /**
     * Creates {@code data/user.csv} for a new user and writes the supplied user data.
     * <p>
     * The {@code data/} directory is created if needed.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address
     * @param phone the user's phone number
     * @throws IOException if the file cannot be created or written
     */
    public void createUserFile(String firstName, String lastName, String email, String phone) throws IOException {
        validateUserCreation(firstName, lastName, email, phone);
        Files.createDirectories(DATA_DIR);
        user = new User(firstName.trim(), lastName.trim(), normalizeOptionalValue(email), normalizePhone(phone));

        try {
            saveUserToFile();
        } catch (IOException e) {
            throw new IOException("Failed to create user file at " + USER_FILE, e);
        }
    }

    /**
     * Loads the persisted user record from {@code data/user.csv}.
     *
     * @throws IOException if the file does not exist, is empty, or has an invalid format
     */
    public void loadUser() throws IOException {
        if (!Files.exists(USER_FILE)) {
            throw new FileNotFoundException("user.csv not found at " + USER_FILE);
        }

        try (BufferedReader reader = Files.newBufferedReader(USER_FILE)) {
            String header = reader.readLine();
            if (header == null) {
                throw new IOException("user.csv is empty: " + USER_FILE);
            }

            String expectedHeader = "first_name,last_name,email,phone,dark_mode";
            if (!expectedHeader.equals(header.trim())) {
                throw new IOException("user.csv has an invalid header: " + USER_FILE);
            }

            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) {
                throw new IOException("user.csv is empty: " + USER_FILE);
            }

            String[] fields = line.split(",", -1);
            if (fields.length != 5) {
                throw new IOException("user.csv has an invalid format: " + USER_FILE);
            }

            try {
                validateUserCreation(fields[0], fields[1], fields[2], fields[3]);
            } catch (IllegalArgumentException e) {
                throw new IOException("user.csv has an invalid user: " + USER_FILE, e);
            }

            String darkModeValue = fields[4].trim();
            if (!"true".equalsIgnoreCase(darkModeValue) && !"false".equalsIgnoreCase(darkModeValue)) {
                throw new IOException("user.csv has an invalid dark_mode value: " + USER_FILE);
            }

        user = new User(fields[0].trim(), fields[1].trim(), normalizeOptionalValue(fields[2]), normalizePhone(fields[3]));
            user.setIsDarkMode(Boolean.parseBoolean(darkModeValue));
        }
    }

    /**
     * Writes the current user to {@code data/user.csv}, replacing any existing contents.
     *
     * @throws IOException if the file cannot be written
     * @throws IllegalStateException if no user has been loaded or created
     */
    public void saveUserToFile() throws IOException {
        if (user == null) {
            throw new IllegalStateException("No user is loaded to save");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(USER_FILE)) {
            String header = "first_name,last_name,email,phone,dark_mode";
            writer.write(header);
            writer.newLine();
            writer.write(user.userToCSV());
        }
    }

    /**
     * Updates the user's first name and saves the change to disk.
     *
     * @param firstName the new first name
     * @throws IOException if the updated user cannot be saved
     */
    public void setFirstName(String firstName) throws IOException {
        ensureUserLoaded();
        user.setFirstName(firstName);
        saveUserToFile();
    }

    /**
     * Updates the user's last name and saves the change to disk.
     *
     * @param lastName the new last name
     * @throws IOException if the updated user cannot be saved
     */
    public void setLastName(String lastName) throws IOException {
        ensureUserLoaded();
        user.setLastName(lastName);
        saveUserToFile();
    }

    /**
     * Updates the user's email address and saves the change to disk.
     *
     * @param email the new email address
     * @throws IOException if the updated user cannot be saved
     */
    public void setEmail(String email) throws IOException {
        ensureUserLoaded();
        user.setEmail(email);
        saveUserToFile();
    }

    /**
     * Updates the user's phone number and saves the change to disk.
     *
     * @param phoneNumber the new phone number
     * @throws IOException if the updated user cannot be saved
     */
    public void setPhoneNumber(String phoneNumber) throws IOException {
        ensureUserLoaded();
        String cleanPhone = phoneNumber == null ? "" : phoneNumber.replaceAll("\\D", "");
        user.setPhoneNumber(cleanPhone);
        saveUserToFile();
    }

    /**
     * Updates the user's dark mode setting and saves the change to disk.
     *
     * @param darkMode the new dark mode value
     * @throws IOException if the updated user cannot be saved
     */
    public void setDarkMode(boolean darkMode) throws IOException {
        ensureUserLoaded();
        user.setIsDarkMode(darkMode);
        saveUserToFile();
    }

    /**
     * Toggles the user's dark mode setting and saves the change to disk.
     *
     * @throws IOException if the updated user cannot be saved
     */
    public void toggleDarkMode() throws IOException {
        ensureUserLoaded();
        user.toggleDarkMode();
        saveUserToFile();
    }

    /**
     * Updates the user's profile fields and saves the change to disk in one step.
     *
     * @param firstName the new first name
     * @param lastName the new last name
     * @param email the new email address
     * @param phone the new phone number
     * @throws IOException if validation or saving fails
     */
    public void updateUserProfile(String firstName, String lastName, String email, String phone)
            throws IOException {
        ensureUserLoaded();
        validateUserCreation(firstName, lastName, email, phone);

        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmail(normalizeOptionalValue(email));
        user.setPhoneNumber(normalizePhone(phone));
        saveUserToFile();
    }

    /**
     * Ensures a user has already been loaded or created.
     *
     * @throws IllegalStateException if no user is currently available
     */
    private void ensureUserLoaded() {
        if (user == null) {
            throw new IllegalStateException("No user is loaded");
        }
    }

    /**
     * Returns the currently loaded user.
     *
     * @return the current {@link User}, or {@code null} if none has been loaded
     */
    public User getUser() {return this.user;}

    /**
     * Replaces the currently loaded user.
     *
     * @param user the new user instance
     */
    public void setUser(User user) {this.user = user;}

    /**
     * Validates the user creation fields before they are persisted.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address
     * @param phone the user's phone number
     */
    private void validateUserCreation(String firstName, String lastName, String email, String phone) throws IllegalArgumentException {
        UserInputValidator.validateName(firstName);
        UserInputValidator.validateName(lastName);
        UserInputValidator.validateEmail(email);
        UserInputValidator.validatePhone(phone);
    }

    /**
     * Normalizes optional string values before persistence.
     *
     * @param value the input value
     * @return a trimmed string, or an empty string if the value is null
     */
    private String normalizeOptionalValue(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Normalizes a phone number by stripping non-digit characters.
     *
     * @param phone the phone number text
     * @return digits-only phone number, or empty string if null
     */
    private String normalizePhone(String phone) {
        return phone == null ? "" : phone.replaceAll("\\D", "");
    }
}

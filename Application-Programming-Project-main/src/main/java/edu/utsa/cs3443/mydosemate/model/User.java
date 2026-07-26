package edu.utsa.cs3443.mydosemate.model;

/**
 * Application user model, including persisted profile data and settings.
 * <p>
 * Mutation is intentionally package-private so controllers update the user
 * through {@link UserManager} rather than calling setters directly.
 */
public class User extends Person {

    /**
     * Indicates whether dark mode is enabled for the user.
     */
    boolean isDarkMode = false;

    /**
     * Creates a user with the supplied profile data.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address
     * @param phoneNumber the user's phone number
     */
    User(String firstName, String lastName, String email, String phoneNumber) {
        super(firstName, lastName, email, phoneNumber);
    }

    /**
     * Returns whether dark mode is enabled.
     *
     * @return {@code true} if dark mode is enabled, otherwise {@code false}
     */
    public boolean getIsDarkMode() {return isDarkMode;}

    /**
     * Updates the dark mode setting.
     *
     * <p>This setter is package-private so settings changes are routed through
     * {@link UserManager}.
     *
     * @param darkMode the new dark mode value
     */
    void setIsDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }

    /**
     * Toggles the dark mode setting.
     *
     * <p>This method is package-private so settings changes are routed through
     * {@link UserManager}.
     */
    void toggleDarkMode() {
        this.isDarkMode = !this.isDarkMode;
    }

    /**
     * Returns this user's data as a CSV row.
     *
     * @return the user data in {@code first,last,email,phone,dark_mode} order
     */
    public String userToCSV(){
        return super.personToCSV() + "," + isDarkMode;
    }
}

package edu.utsa.cs3443.mydosemate.model;

/**
 * Base model for a person with contact information.
 * <p>
 * This class exposes read access to person data, while write access is limited
 * to the model package so higher layers update user state through
 * {@link UserManager}.
 */
public abstract class Person {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    /**
     * Creates a person with the supplied name and contact fields.
     *
     * @param firstName the person's first name
     * @param lastName the person's last name
     * @param email the person's email address
     * @param phoneNumber the person's phone number
     */
    Person(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the person's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the person's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the person's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the person's phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Updates the person's first name.
     *
     * <p>This setter is package-private so user data can only be mutated from
     * within the model package, typically through {@link UserManager}.
     *
     * @param firstName the new first name
     */
    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Updates the person's last name.
     *
     * <p>This setter is package-private so user data can only be mutated from
     * within the model package, typically through {@link UserManager}.
     *
     * @param lastName the new last name
     */
    void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Updates the person's email address.
     *
     * <p>This setter is package-private so user data can only be mutated from
     * within the model package, typically through {@link UserManager}.
     *
     * @param email the new email address
     */
    void setEmail(String email) {
        this.email = email;
    }

    /**
     * Updates the person's phone number.
     *
     * <p>This setter is package-private so user data can only be mutated from
     * within the model package, typically through {@link UserManager}.
     *
     * @param phoneNumber the new phone number
     */
    void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the person's full name.
     *
     * @return the first and last name separated by a space
     */
    public String getFullName(){
        return this.getFirstName() + " " + this.getLastName();
    }

    /**
     * Returns a string representation of the person's fields.
     *
     * @return a formatted description of the person
     */
    @Override
    public String toString() {
        return "First Name= " + firstName + ", Last Name= " + lastName + ", Email= " + email + ", Phone Number= " + phoneNumber;
    }

    /**
     * Returns the person's fields as a CSV row.
     *
     * @return the person's data in {@code first,last,email,phone} order
     */
    public String personToCSV(){
        return String.join(",", firstName, lastName, email, phoneNumber);
    }
}

package edu.utsa.cs3443.mydosemate.model;

/**
 * Base model for a person with contact information.
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
    public Person(String firstName, String lastName, String email, String phoneNumber) {
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
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Updates the person's last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Updates the person's email address.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Updates the person's phone number.
     *
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
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
}

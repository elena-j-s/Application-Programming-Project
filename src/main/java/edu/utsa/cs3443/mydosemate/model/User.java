package edu.utsa.cs3443.mydosemate.model;

public class User extends Person {

    // add more fields for settings from settings.csv
    boolean darkMode = false;

    public User(String firstName, String lastName, String email, String phoneNumber) {
        super(firstName, lastName, email, phoneNumber);
    }

    public boolean getDarkMode() {return darkMode;}

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        updateUserFile();
    }

    public void toggleDarkMode() {
        this.darkMode = !this.darkMode;
        updateUserFile();
    }

    public void updateUserFile(){
        // save user info to user.csv
    }




}


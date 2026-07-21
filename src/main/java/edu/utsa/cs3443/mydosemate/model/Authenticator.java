package edu.utsa.cs3443.mydosemate.model;

import java.io.IOException;
import java.nio.file.Path;

/*
 * This class will be responsible for the sign in and sign up process
 * It will also be responsible for the recovery of passwords
 * This class will be used by all sign in sections of the application
 */

public class Authenticator {

    private Path usersFilePath;

    /*
     * Authenticates a user and returns the path to the user's data directory inside data/users/ or null
     * if the user is not found.
     *
     * It will search data/ for a users.csv file to parse
     * It will then search users.csv for a match and if found will return
     * the path to users/user_id/ directory associated with the user found
     */
    public Path authenticateUser (String username, String password) throws IOException, UserNotFoundException {

        return null;
    }

    /*
    * Registers a new user
    * Returns true if successful, false otherwise
    *
    * Will check if the user already exists within data/users.csv
    * if not, will create a new user, including user id and a new users/user_id/ directory
    * with dose_log.csv, medications.csv, and settings,csv
     */
    public boolean registerUser(String username, String password) {
        return false;
    }

    /*
    Not sure how to implement this yet
     */
    public String recoverPassword() {
        return null;
    }
}

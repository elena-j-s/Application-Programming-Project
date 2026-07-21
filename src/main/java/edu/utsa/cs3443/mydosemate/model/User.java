package edu.utsa.cs3443.mydosemate.model;

public class User {
    private final String user_id;
    private String username;
    private String password_hash;
    private String salt;
    private String created_at;
    // add more fields for settings from settings.csv

    public User(String user_id, String username, String password_hash, String salt, String created_at) {
        this.user_id = user_id;
        this.username = username;
        this.password_hash = password_hash;
        this.salt = salt;
        this.created_at = created_at;
    }

    /*
     * Change the password of the user and generate a new salt and hash
     */
    public void changePassword(String new_password) {
        return;
    }

    public String getUserId() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return password_hash;
    }

    public String getSalt() {
        return salt;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setUsername(String username) {this.username = username;}

    public void setPasswordHash(String password_hash) {this.password_hash = password_hash;}

    public void setSalt(String salt) {this.salt = salt;}

    public void setCreatedAt(String created_at) {this.created_at = created_at;}

}


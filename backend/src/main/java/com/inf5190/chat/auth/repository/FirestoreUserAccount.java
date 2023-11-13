package com.inf5190.chat.auth.repository;

public class FirestoreUserAccount {
    private String username;
    private String encodedPassword;

    public FirestoreUserAccount() {
    }

    public FirestoreUserAccount(String username, String encodedPassword) {
        this.username = username;
        this.encodedPassword = encodedPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }
}
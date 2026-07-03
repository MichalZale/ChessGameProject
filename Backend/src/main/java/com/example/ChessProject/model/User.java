package com.example.ChessProject.model;

import java.util.List;
import java.util.ArrayList;

public class User {
    private int userID = -1;
    private String username;
    private String email;
    private String passwordHash;
    private int rating;
    private List<Integer> gamesPlayedIDs;

    public User(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash=passwordHash;
        this.email = email;
        this.rating = 500;
        this.gamesPlayedIDs = new ArrayList<>();
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public void setPasswordHash(String hash) {
        this.passwordHash = hash;
    }

    public void setRating(int newRating) {
        this.rating = newRating;
    }

    public void addGame(int gameID) {
        gamesPlayedIDs.add(gameID);
    }

    public int getUserID() {
        return this.userID;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public String getEmail() {
        return this.email;
    }

    public int getPlayerID() {
        return this.userID;
    }

    public int getRating() {
        return this.rating;
    }
}

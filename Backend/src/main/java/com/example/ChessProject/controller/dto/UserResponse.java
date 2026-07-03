package com.example.ChessProject.controller.dto;

import com.example.ChessProject.model.User;

public class UserResponse {
    private Integer userID;
    private String username;
    private String email;

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public static UserResponse fromUser(User user) {
        UserResponse response = new UserResponse();
        response.setUserID(user.getUserID());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }
}

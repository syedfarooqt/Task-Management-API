package com.interview.assignment.taskmanagementapi.model;


import java.util.List;

public class JwtResponse {
    private String token;
    private String username;
    private String email;
    private String roles;

    public JwtResponse(String accessToken, String username, String roles) {
        this.token = accessToken;
        this.username = username;
        this.roles = roles;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoles() {
        return roles;
    }
}


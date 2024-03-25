package com.interview.assignment.taskmanagementapi.model.Request.User;

import lombok.Data;

@Data
public class LoginRequest {
    private String userName;
    private String password;
}


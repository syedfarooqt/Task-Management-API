package com.interview.assignment.taskmanagementapi.model.Request.User;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String password;
    private String email;
    private String Role;
    // Other fields as needed
}

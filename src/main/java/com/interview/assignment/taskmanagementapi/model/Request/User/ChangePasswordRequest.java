package com.interview.assignment.taskmanagementapi.model.Request.User;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}


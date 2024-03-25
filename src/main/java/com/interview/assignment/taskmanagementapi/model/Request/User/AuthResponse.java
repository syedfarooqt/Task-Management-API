package com.interview.assignment.taskmanagementapi.model.Request.User;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";


    public AuthResponse(String accessToken){
      this.accessToken=accessToken;
    }

}

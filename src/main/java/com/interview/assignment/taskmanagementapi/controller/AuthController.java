package com.interview.assignment.taskmanagementapi.controller;

import com.interview.assignment.taskmanagementapi.config.JwtGeneratorValidator;
import com.interview.assignment.taskmanagementapi.model.JwtResponse;
import com.interview.assignment.taskmanagementapi.model.Request.User.*;
import com.interview.assignment.taskmanagementapi.model.Users;
import com.interview.assignment.taskmanagementapi.repository.UserRepository;
import com.interview.assignment.taskmanagementapi.service.CustomUserDetailsService;
import com.interview.assignment.taskmanagementapi.service.UserDetailsServiceImpl;
import com.interview.assignment.taskmanagementapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
//import com.interview.assignment.taskmanagementapi.service.AuthService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtGeneratorValidator jwtGenVal;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  JwtGeneratorValidator jwtGeneratorValidator;


    // private final AuthService authService;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signUp")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> registerUser(@RequestBody UserDTO userDto) {
        Users users =  userService.save(userDto);
        if (users.equals(null))
            return generateRespose("Not able to save user ", HttpStatus.BAD_REQUEST, userDto);
        else
            return generateRespose("User saved successfully : " + users.getId(), HttpStatus.OK, users);
    }

//    @PostMapping("/login")
//    public ResponseEntity<?>  authenticateUser(@RequestBody LoginRequest loginRequest) {
//
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Return the JWT token in the response
//         String token=jwtGenVal.generateToken(authentication);
//
//        User user = (User) authentication.getPrincipal();
//        Users users=userRepository.findByUserName(user.getUsername());
//        List<String> roles = users.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(new JwtResponse(token,
//                loginRequest.getUserName(),
//                roles));
//    }
@PostMapping("/login")
public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Return the JWT token in the response
    String token = jwtGenVal.generateToken(authentication);
    User user = (User) authentication.getPrincipal();
    Users users=userRepository.findByUserName(user.getUsername());
    String role=users.getRole().toString();


    return ResponseEntity.ok(new JwtResponse(token,
            loginRequest.getUserName(),
            role));
}
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                                 Authentication authentication) {

        String username = authentication.getName();
        Users user = userRepository.findByUserName(username);

        // Check if the old password matches the current password
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password is incorrect");
        }

        // Encode and set the new password
        String newPasswordEncoded = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        user.setPassword(newPasswordEncoded);
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully");
    }


    @DeleteMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@RequestParam("username") String username) {
        try {
            userRepository.deleteUserByUserName(username);
            return ResponseEntity.ok("User with username " + username + " deleted successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }
    public ResponseEntity<Object> generateRespose(String message, HttpStatus st, Object responseobj) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("meaasge", message);
        map.put("Status", st.value());
        map.put("data", responseobj);

        return new ResponseEntity<Object>(map, st);
    }
}


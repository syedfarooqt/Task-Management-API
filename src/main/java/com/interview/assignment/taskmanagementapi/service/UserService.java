package com.interview.assignment.taskmanagementapi.service;


import com.interview.assignment.taskmanagementapi.Exception.UsernameAlreadyExistsException;
import com.interview.assignment.taskmanagementapi.model.Request.User.UserDTO;
import com.interview.assignment.taskmanagementapi.model.Request.User.UserRole;
import com.interview.assignment.taskmanagementapi.model.Users;
import com.interview.assignment.taskmanagementapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users getUserById(Long id) {
        Optional<Users> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Users createUser(Users user) {
        // Perform any necessary validations before saving the user
        return userRepository.save(user);
    }
    public Users save(UserDTO userRegisteredDTO) {
        Users user = new Users();
        user.setEmail(userRegisteredDTO.getEmail());
        user.setUserName(userRegisteredDTO.getUserName());
        user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));
        if(Objects.equals(userRegisteredDTO.getRole(), "ADMIN")){
            user.setRole(UserRole.ADMIN);
        }else if(Objects.equals(userRegisteredDTO.getRole(), "USER")){
            user.setRole(UserRole.USER);
        }else {
            //default
            user.setRole(UserRole.USER);
        }
       Users userExist= userRepository.findByUserName(user.getUsername());
        if (userExist != null) {
            throw new UsernameAlreadyExistsException("Username '" + user.getUsername() + "' is already taken.");
        } else {
            return userRepository.save(user);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}


package com.interview.assignment.taskmanagementapi.service;

import com.interview.assignment.taskmanagementapi.model.Request.User.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.interview.assignment.taskmanagementapi.model.Users;
import com.interview.assignment.taskmanagementapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
@AllArgsConstructor
@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    @Autowired
    private final UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUserName(username);
        String role = user.getRole().toString();
        Collection<? extends GrantedAuthority> authorities = mapRoleToAuthorities(role);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public Collection<? extends GrantedAuthority> mapRoleToAuthorities(String role) {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }
}

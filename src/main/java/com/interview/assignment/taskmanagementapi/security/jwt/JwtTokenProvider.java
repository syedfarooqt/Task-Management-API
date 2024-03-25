package com.interview.assignment.taskmanagementapi.security.jwt;

import com.interview.assignment.taskmanagementapi.model.Request.User.UserRole;
import com.interview.assignment.taskmanagementapi.model.Users;
import com.interview.assignment.taskmanagementapi.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpiration;
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    private final Key jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final UserRepository userRepository;

    public JwtTokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
    public List<UserRole> getUserRolesFromJwtToken(String token) {
        String userName=getUserNameFromJwtToken(token);
        Users user=userRepository.findByUserName(userName);
        List<UserRole> role = new LinkedList<>();
         role.add(user.getRole());
         return role;
    }

}

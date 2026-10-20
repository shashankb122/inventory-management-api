package com.inventory.service;

import com.inventory.exception.ConflictException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.User;
import com.inventory.repository.UserRepository;
import com.inventory.security.JwtUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder,
                       AuthenticationManager authManager, UserDetailsService userDetailsService,
                       JwtUtils jwtUtils) {
        this.userRepo         = userRepo;
        this.passwordEncoder  = passwordEncoder;
        this.authManager      = authManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils         = jwtUtils;
    }

    public User register(String username, String email, String rawPassword) {
        if (userRepo.existsByUsername(username))
            throw new ConflictException("Username already taken: " + username);
        if (userRepo.existsByEmail(email))
            throw new ConflictException("Email already registered: " + email);
        var user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(User.Role.ROLE_USER);
        return userRepo.save(user);
    }

    public String login(String username, String password) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        var ud = userDetailsService.loadUserByUsername(username);
        return jwtUtils.generateToken(ud);
    }

    public User getByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}

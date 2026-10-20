package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and obtain JWT tokens")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    static class RegisterRequest {
        @NotBlank public String username;
        @Email @NotBlank public String email;
        @NotBlank @Size(min = 8) public String password;
    }

    static class LoginRequest {
        @NotBlank public String username;
        @NotBlank public String password;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        User user = authService.register(req.username, req.email, req.password);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole()));
    }

    @Operation(summary = "Login and receive a JWT token")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req.username, req.password);
        User user = authService.getByUsername(req.username);
        return ResponseEntity.ok(Map.of("token", token, "username", user.getUsername(), "role", user.getRole()));
    }
}

package com.example.userservice.controller;

import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error","Email already in use"));
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error","Passwor is required"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        User saved = userRepository.save(user);
        return ResponseEntity.status(201).body(Map.of(
                "id",saved.getId(),
                "email",saved.getEmail(),
                "name",saved.getName(),
                "role",saved.getRole()
        ));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error","email and password required"));
        }

        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    String token = authService.generateTokenForUser(user);
                    return ResponseEntity.ok(Map.of("token",token,"role",user.getRole()));
                })
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error","Invalid credential")));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                    @RequestBody(required = false)Map<String, String> body) {
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null && body != null) {
            token = body.get("token");
        }

        if (token == null) {
            return ResponseEntity.badRequest().body(Map.of("error","token required"));
        }

        authService.removeToken(token);
        return ResponseEntity.ok(Map.of("message","Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body(Map.of("Error","Not authenticated"));
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(Map.of(
                        "id",user.getId(),
                        "email",user.getEmail(),
                        "name",user.getName(),
                        "role",user.getRole()
                )))
                .orElse(ResponseEntity.status(404).body(Map.of("error","User not found")));
    }
}

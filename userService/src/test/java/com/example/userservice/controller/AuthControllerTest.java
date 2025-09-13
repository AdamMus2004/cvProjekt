package com.example.userservice.controller;

import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {
    @Test
    void loginSuccess() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthService authService = mock(AuthService.class);
        User user = new User("Adam", "adam@example.com", "secret", Role.USER);
        user.setId(1L);

        when(userRepository.findByEmail("adam@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret","secret")).thenReturn(true);
        when(authService.generateTokenForUser(user)).thenReturn("tok123");

        AuthController authController = new AuthController(userRepository, passwordEncoder, authService);

        ResponseEntity<?> responseEntity = authController.login(Map.of("email","adam@example.com","password","secret"));
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody().toString().contains("tok123"));
    }
}

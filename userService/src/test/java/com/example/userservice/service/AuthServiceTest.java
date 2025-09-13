package com.example.userservice.service;

import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;


class AuthServiceTest {
    @Test
    void generateAndRemoveToken(){
        UserRepository userRepository = mock(UserRepository.class);
        AuthService authService = new AuthService(userRepository);
        User user = new User("Adam", "adam@example.com", "secret", Role.USER);
        user.setId(1L);

        String token = authService.generateTokenForUser(user);
        assertThat(token).isNotNull();

        Optional<User> found = authService.getUserForToken(token);
        assertThat(found).isEmpty();

        authService.removeToken(token);
        assertThat(authService.getUserForToken(token).isEmpty());
    }

}

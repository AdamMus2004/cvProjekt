package com.example.userservice.service;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, user.getId());
        log.debug("Generated token {} for user {}",token, user.getEmail());
        return token;
    }

    public Optional<User> getUserForToken(String token) {
        log.trace("Checking user for token {}", token);
        Long userId = tokens.get(token);
        if (userId == null) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

    public void removeToken(String token) {
        tokens.remove(token);
        log.info("Removed token {}", token);
    }
}

package com.example.userservice.controller;


import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.userservice.model.Role;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET
    @GetMapping("/users")
    public List<User> getAllAdminUsers() {
        return userRepository.findAll();
    }

    //PUT /admin/users/{id}/role
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userRepository.findById(id)
                .map(user -> {
                    try {
                        Role newRole = Role.valueOf(body.get("role").toUpperCase());
                        user.setRole(newRole);
                        userRepository.save(user);
                        return ResponseEntity.ok(Map.of("message","Role updated"));
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(Map.of("error","Invalid role"));
                    }
                })
                .orElse(ResponseEntity.status(404).body(Map.of("error","User not found")));
    }


    //DELETE /admin/users/{id}
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message","User deleted"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error","User not found"));
        }
    }

}

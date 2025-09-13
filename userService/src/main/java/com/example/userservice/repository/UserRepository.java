package com.example.userservice.repository;

import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);
    Optional<User> findByEmail(String email);
}

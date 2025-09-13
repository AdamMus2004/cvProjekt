package com.example.userservice.controller;

import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void testGetUsers() throws Exception {
        Mockito.when(userRepository.findAll()).thenReturn(
                List.of(new User("Adam", "adam@example.com", "123",Role.USER))
        );

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Adam"));
    }

    @Test
    void testPostUsers() throws Exception {
        User user = new User("Adam","adam@example.com","123",Role.USER);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(new User("Adam","adam@example.com","123",Role.USER));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Adam"))
                .andExpect(jsonPath("$.email").value("adam@example.com"));
    }

    @Test
    void testPutUsers() throws Exception {
        User existingUser = new User("Adam","adam@example.com","123",Role.USER);
        existingUser.setId(1L);

        User updatedUser = new User("Adam_2","adam_2@example.com","123",Role.USER);
        updatedUser.setId(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Adam_2"))
                .andExpect(jsonPath("$.email").value("adam_2@example.com"));
    }

    @Test
    void testDeleteUsers() throws Exception {

        Mockito.doNothing().when(userRepository).deleteById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

}
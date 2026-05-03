package com.taskmanager.backend.controller;

import com.taskmanager.backend.entity.User;
import com.taskmanager.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // --- UPDATED: Secured and returning a DTO to prevent password leaks ---
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        // Fetch all users, then convert them into safe DTOs
        List<UserDTO> safeUsers = userService.getAllUsers().stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(safeUsers);
    }

    // --- NEW: A simple Record to safely send user data to React ---
    public record UserDTO(Long id, String email) {}
}
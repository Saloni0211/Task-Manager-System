//package com.taskmanager.backend.controller;
//
//import com.taskmanager.backend.entity.User;
//import com.taskmanager.backend.enums.Role;
//import com.taskmanager.backend.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/test")
//@RequiredArgsConstructor
//public class TestController {
//
//    private final UserRepository userRepository;
//
//    @PostMapping("/create-user")
//    public User createTestUser() {
//        User user = User.builder()
//                .email("test@example.com")
//                .password("password123")
//                .role(Role.ADMIN)
//                .build();
//        return userRepository.save(user);
//    }
//
//    @GetMapping("/users")
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//}
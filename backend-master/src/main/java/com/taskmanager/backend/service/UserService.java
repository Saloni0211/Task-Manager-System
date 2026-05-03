package com.taskmanager.backend.service;

import com.taskmanager.backend.entity.User;
// Make sure this import matches exactly where your Role enum is saved!
import com.taskmanager.backend.enums.Role;
import com.taskmanager.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // --- MANUAL CONSTRUCTOR ---
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        // 1. Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // --- THE FIX: FORCE THE DEFAULT ROLE ---
        // This stops hackers from making themselves Admins via Postman or React.
        // It forces every new registration to be a standard MEMBER.
        user.setRole(Role.MEMBER);

        // 3. Save the user to the database
        return userRepository.save(user);
    }

    // --- Added to support Team Management & Assignment ---
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
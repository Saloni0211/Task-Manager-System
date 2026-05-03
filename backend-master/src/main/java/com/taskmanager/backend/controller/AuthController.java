package com.taskmanager.backend.controller;

import com.taskmanager.backend.repository.UserRepository;
import com.taskmanager.backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Authentication failed: User not found"));

        var jwtToken = jwtService.generateToken(user);

        // --- UPDATED: Passing token, role, and ID to the response ---
        return ResponseEntity.ok(new AuthResponse(
                jwtToken,
                user.getRole().name(),
                user.getId()
        ));
    }

    public record AuthRequest(String email, String password) {}

    // --- UPDATED: Added role and id fields to the AuthResponse record ---
    public record AuthResponse(String token, String role, Long id) {}
}
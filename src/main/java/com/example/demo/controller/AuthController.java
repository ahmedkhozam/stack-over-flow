package com.example.demo.controller;


import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.StackUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.StackUserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.StackUserDetailsService;
import com.example.demo.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StackUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final StackUserDetailsService userDetailsService;
    private final JwtService jwtService;
    @Autowired
   private final StackUserRepository stackUserRepository;
    @Autowired
    private AnswerService answerService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        StackUser user = StackUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .bio(request.getBio())
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // تحقق من الإيميل والباسورد
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // جلب بيانات اليوزر
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        StackUser user = stackUserRepository.findByEmail(request.getEmail()).orElseThrow(()->new ResourceNotFoundException("Email doesn't exist"));
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token, "Login successful", user.getReputation()));

    }



}

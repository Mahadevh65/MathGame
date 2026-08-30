package com.thinkingrealms.backend.service;

import com.thinkingrealms.backend.domain.Role;
import com.thinkingrealms.backend.domain.StudentProfile;
import com.thinkingrealms.backend.domain.User;
import com.thinkingrealms.backend.dto.auth.AuthResponse;
import com.thinkingrealms.backend.dto.auth.LoginRequest;
import com.thinkingrealms.backend.dto.auth.RegisterRequest;
import com.thinkingrealms.backend.repository.StudentProfileRepository;
import com.thinkingrealms.backend.repository.UserRepository;
import com.thinkingrealms.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        user.setRole(Role.STUDENT);
        userRepository.save(user);

        StudentProfile profile = new StudentProfile();
        profile.setUserId(user.getId());
        studentProfileRepository.save(profile);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getDisplayName(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getDisplayName(), user.getRole().name());
    }
}

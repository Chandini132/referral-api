package com.example.referral.controller;

import com.example.referral.dto.UserSignupRequest;
import com.example.referral.model.User;
import com.example.referral.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * REST controller for user and referral operations.
 */
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Signs up a new user.
     * @param request Signup details
     * @return Created user
     */
    @PostMapping("/signup")
    public User signup(@RequestBody UserSignupRequest request) {
        return userService.signup(request);
    }

    /**
     * Logs in a user and returns a JWT.
     * @param request Login credentials
     * @return JWT token
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody UserSignupRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
        return Collections.singletonMap("token", token);
    }

    /**
     * Marks the authenticated user's profile as complete.
     * @param authentication JWT authentication
     * @return Updated user
     */
    @PutMapping("/profile/complete")
    public User completeProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userService.completeProfile(user.getId());
    }

    /**
     * Retrieves referrals for the authenticated user.
     * @param authentication JWT authentication
     * @return List of referred users
     */
    @GetMapping("/referrals")
    public List<User> getReferrals(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userService.getReferrals(user.getId());
    }

    /**
     * Generates a CSV report of all users and their successful referrals.
     * @return CSV file response
     * @throws IOException If CSV generation fails
     */
    @GetMapping("/referral-report")
    public ResponseEntity<String> getReferralReport() throws IOException {
        String csv = userService.generateReferralReport();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=referral_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
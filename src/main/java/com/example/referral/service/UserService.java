package com.example.referral.service;

import com.example.referral.dto.UserSignupRequest;
import com.example.referral.model.User;
import com.example.referral.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for user-related operations and referral tracking.
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Signs up a new user, optionally linking to a referrer.
     * @param request Signup details
     * @return Saved user entity
     */
    public User signup(UserSignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getReferralCode() != null && !request.getReferralCode().isEmpty()) {
            Optional<User> referrer = userRepository.findByReferralCode(request.getReferralCode());
            if (referrer.isPresent()) {
                user.setReferrerId(referrer.get().getId());
            } else {
                throw new IllegalArgumentException("Invalid referral code");
            }
        }
        return userRepository.save(user);
    }

    /**
     * Finds a user by email for login purposes.
     * @param email User email
     * @return Optional user
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Marks a user's profile as complete, updating referral status.
     * @param userId User ID
     * @return Updated user entity
     */
    public User completeProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setProfileCompleted(true);
        return userRepository.save(user);
    }

    /**
     * Retrieves all referrals for a given user.
     * @param userId Referrer's ID
     * @return List of referred users
     */
    public List<User> getReferrals(Long userId) {
        return userRepository.findByReferrerId(userId);
    }

    /**
     * Generates a CSV report of all users and their successful referrals.
     * @return CSV string
     * @throws IOException If CSV writing fails
     */
    public String generateReferralReport() throws IOException {
        List<User> users = userRepository.findAll();
        StringWriter writer = new StringWriter();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("User ID", "Email", "Referral Code", "Referrer ID", "Profile Completed", "Successful Referrals"))) {
            for (User user : users) {
                long successfulReferrals = userRepository.findByReferrerId(user.getId()).stream()
                        .filter(User::isProfileCompleted).count();
                csvPrinter.printRecord(
                        user.getId(),
                        user.getEmail(),
                        user.getReferralCode(),
                        user.getReferrerId() != null ? user.getReferrerId() : "N/A",
                        user.isProfileCompleted(),
                        successfulReferrals
                );
            }
        }
        return writer.toString();
    }
}
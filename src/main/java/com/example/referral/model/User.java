package com.example.referral.model;

import lombok.Data;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Represents a user with referral tracking capabilities.
 */
@Entity
@Table(name = "users")
@Data
public class User {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public Long getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(Long referrerId) {
        this.referrerId = referrerId;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 8)
    private String referralCode;

    @Column(name = "referrer_id")
    private Long referrerId;

    @Column(nullable = false)
    private boolean profileCompleted = false;

    /**
     * Generates a unique referral code before saving the user.
     */
    @PrePersist
    private void generateReferralCode() {
        if (this.referralCode == null) {
            this.referralCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
package com.example.referral.dto;

import lombok.Data;

/**
 * DTO for user signup request.
 */
@Data
public class UserSignupRequest {
    private String email;
    private String password;
    private String referralCode; // Optional

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
}
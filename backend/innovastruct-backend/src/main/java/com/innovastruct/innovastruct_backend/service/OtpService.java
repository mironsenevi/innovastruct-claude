package com.innovastruct.innovastruct_backend.service;


import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.innovastruct.innovastruct_backend.model.OtpEntity ;
import com.innovastruct.innovastruct_backend.repository.OtpRepository ;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Value("${otp.expiry.minutes}")
    private int otpExpiryMinutes;

    public void generateAndSendOtp(String phone) {
        // Generate a 6-digit OTP
        String otp = generateOtp();

        // Calculate expiry time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, otpExpiryMinutes);
        Date expiryDate = calendar.getTime();

        // Save OTP to database
        Optional<OtpEntity> existingOtp = otpRepository.findByPhone(phone);
        if (existingOtp.isPresent()) {
            OtpEntity otpEntity = existingOtp.get();
            otpEntity.setOtp(otp);
            otpEntity.setExpiryDate(expiryDate);
            otpRepository.save(otpEntity);
        } else {
            OtpEntity otpEntity = new OtpEntity();
            otpEntity.setPhone(phone);
            otpEntity.setOtp(otp);
            otpEntity.setExpiryDate(expiryDate);
            otpRepository.save(otpEntity);
        }

        // In a real application, send OTP via SMS here
        // For development, just print to console
        System.out.println("OTP for " + phone + ": " + otp);
    }

    public boolean validateOtp(String phone, String otp) {
        Optional<OtpEntity> otpEntityOpt = otpRepository.findByPhone(phone);

        if (otpEntityOpt.isPresent()) {
            OtpEntity otpEntity = otpEntityOpt.get();

            // Check if OTP is expired
            if (otpEntity.isExpired()) {
                return false;
            }

            // Check if OTP matches
            return otpEntity.getOtp().equals(otp);
        }

        return false;
    }

    private String generateOtp() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }
}
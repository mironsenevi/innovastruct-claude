package com.innovastruct.innovastruct_backend.model;


import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpEntity {
    @Id
    private String id;

    private String phone;

    private String otp;

    private Date expiryDate;

    public boolean isExpired() {
        return new Date().after(expiryDate);
    }
}
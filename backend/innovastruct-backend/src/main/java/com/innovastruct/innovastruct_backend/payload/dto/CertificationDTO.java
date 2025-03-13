package com.innovastruct.innovastruct_backend.payload.dto;


import java.util.Date;

import lombok.Data;

@Data
public class CertificationDTO {
    private String name;

    private String organization;

    private String issueDate;

    private String expiryDate;

    private String imageUrl;
}
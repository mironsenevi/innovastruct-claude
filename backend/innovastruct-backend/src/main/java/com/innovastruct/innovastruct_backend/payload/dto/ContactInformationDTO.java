package com.innovastruct.innovastruct_backend.payload.dto;


import lombok.Data;

@Data
public class ContactInformationDTO {
    private String email;

    private String phoneNumber;

    private String website;
}
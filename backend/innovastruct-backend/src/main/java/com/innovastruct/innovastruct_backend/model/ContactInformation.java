package com.innovastruct.innovastruct_backend.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactInformation {
    private String email;

    private String phoneNumber;

    private String website;
}
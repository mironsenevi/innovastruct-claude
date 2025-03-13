package com.innovastruct.innovastruct_backend.payload.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompanySignupRequest extends SignupRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String companyName;

    @NotBlank
    private String licenseNumber;

    @NotBlank
    @Size(min = 20, max = 500)
    private String shortDescription;
}
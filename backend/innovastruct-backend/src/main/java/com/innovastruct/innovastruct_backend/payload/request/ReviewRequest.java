package com.innovastruct.innovastruct_backend.payload.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class ReviewRequest {
    @NotBlank
    private String companyId;

    @NotNull
    @Min(1)
    @Max(5)
    private Float rating;

    @NotBlank
    @Size(min = 10, max = 500)
    private String text;
}

package com.innovastruct.innovastruct_backend.payload.request;


import java.util.Date;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

@Data
public class TenderRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String plan;

    @NotBlank
    private String boq;

    @NotNull
    @Positive
    private Double budget;

    @NotNull
    @Future
    private Date deadline;

    @NotBlank
    private String category;

    @NotBlank
    private String location;

    private String priority = "medium"; // default value
}
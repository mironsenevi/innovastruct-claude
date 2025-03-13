package com.innovastruct.innovastruct_backend.payload.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

@Data
public class BidRequest {
    @NotNull
    @Positive
    private Double bidAmount;

    @NotNull
    @Min(1)
    private Integer proposedDuration;

    private String technicalProposalUrl;

    private String financialProposalUrl;
}
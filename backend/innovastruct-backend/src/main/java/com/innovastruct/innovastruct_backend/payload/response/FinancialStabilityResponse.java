package com.innovastruct.innovastruct_backend.payload.response;


import java.util.List;

import lombok.Data;

@Data
public class FinancialStabilityResponse {
    private String annualRevenue;
    private String growthRate;
    private String creditRating;
    private List<String> majorInvestors;
    private FinancialHealthResponse financialHealth;
}


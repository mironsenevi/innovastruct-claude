package com.innovastruct.innovastruct_backend.payload.response;

import lombok.Data;

 @Data
public class FinancialHealthResponse {
    private String cashReserves;
    private String debtToEquityRatio;
    private String longTermStability;
}
package com.innovastruct.innovastruct_backend.payload.response;


import java.util.List;

import lombok.Data;

@Data
public class ServicesOfferedResponse {
    private List<String> primaryServices;
    private List<String> specializedServices;
    private List<String> consultationServices;
    private List<String> technologyIntegration;
}
package com.innovastruct.innovastruct_backend.payload.response;


import java.util.List;

import lombok.Data;

@Data
public class CertificationsComplianceResponse {
    private List<CertificationDetailResponse> industryCertifications;
    private List<String> governmentCompliance;
    private List<String> safetyStandards;
}


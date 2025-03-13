package com.innovastruct.innovastruct_backend.payload.response;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.innovastruct.innovastruct_backend.model.Certification ;
import com.innovastruct.innovastruct_backend.model.ContactInformation ;
import com.innovastruct.innovastruct_backend.model.Project ;

import lombok.Data;

@Data
public class CompanyProfileResponse {
    private String id;

    private String userId;

    private String name;

    private String licenseNumber;

    private String shortDescription;

    private String description;

    private String established;

    private String location;

    private String employees;

    private String coverImage;

    private String profileIcon;

    private String cidaGrading;

    private String engineerCapacity;

    private List<String> services = new ArrayList<>();

    private List<ProjectResponse> projects = new ArrayList<>();

    private List<ReviewResponse> reviews = new ArrayList<>();

    private float rating;

    private String type;

    // Additional fields to match frontend structure
    private TrackRecordResponse trackRecord;

    private FinancialStabilityResponse financialStability;

    private ServicesOfferedResponse servicesOffered;

    private CertificationsComplianceResponse certificationsCompliance;

    private AwardsRecognitionsResponse awardsRecognitions;

    private ContactInformation contactInformation;
}
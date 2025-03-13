package com.innovastruct.innovastruct_backend.payload.dto;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CompanyProfileDTO {
    private String userId;

    private String companyName;

    private String licenseNumber;

    private String shortDescription;

    private String description;

    private String establishedYear;

    private String location;

    private String employeeCount;

    private String coverImageUrl;

    private String profileIconUrl;

    private String cidaGrading;

    private String engineerCapacity;

    private List<String> services = new ArrayList<>();

    private List<ProjectDTO> pastProjects = new ArrayList<>();

    private String annualRevenue;

    private String fundingSources;

    private List<CertificationDTO> certifications = new ArrayList<>();

    private ContactInformationDTO contactInformation;

    private String type;
}
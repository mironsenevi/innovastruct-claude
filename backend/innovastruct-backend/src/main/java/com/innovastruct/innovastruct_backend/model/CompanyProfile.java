package com.innovastruct.innovastruct_backend.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "company_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfile {
    @Id
    private String id;

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

    private List<Project> pastProjects = new ArrayList<>();

    private String annualRevenue;

    private String fundingSources;

    private List<Certification> certifications = new ArrayList<>();

    private ContactInformation contactInformation;

    private float rating;

    private String type; // Commercial, Residential, etc.

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
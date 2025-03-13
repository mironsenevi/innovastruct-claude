package com.innovastruct.innovastruct_backend.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovastruct.innovastruct_backend.model.Certification ;
import com.innovastruct.innovastruct_backend.model.CompanyProfile ;
import com.innovastruct.innovastruct_backend.model.ContactInformation ;
import com.innovastruct.innovastruct_backend.model.Project ;
import com.innovastruct.innovastruct_backend.model.Review ;
import com.innovastruct.innovastruct_backend.payload.dto.CompanyProfileDTO ;
import com.innovastruct.innovastruct_backend.payload.response.*;
import com.innovastruct.innovastruct_backend.security.services.UserDetailsImpl ;
import com.innovastruct.innovastruct_backend.service.CompanyProfileService ;
import com.innovastruct.innovastruct_backend.service.FileStorageService ;
import com.innovastruct.innovastruct_backend.service.ReviewService ;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/company")
public class CompanyProfileController {

    @Autowired
    private CompanyProfileService companyProfileService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/portfolio/{id}")
    public ResponseEntity<CompanyProfileResponse> getCompanyProfile(@PathVariable String id) {
        CompanyProfile companyProfile = companyProfileService.getCompanyProfile(id);
        CompanyProfileResponse response = mapToResponse(companyProfile);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/portfolio/user/{userId}")
    @PreAuthorize("hasRole('COMPANY') and #userId == authentication.principal.id")
    public ResponseEntity<CompanyProfileResponse> getCompanyProfileByUserId(@PathVariable String userId) {
        try {
            CompanyProfile companyProfile = companyProfileService.getCompanyProfileByUserId(userId);
            CompanyProfileResponse response = mapToResponse(companyProfile);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/portfolio")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyProfileResponse> createCompanyProfile(
            @RequestParam("companyData") String companyDataJson,
            @RequestParam(value = "projectImages", required = false) MultipartFile[] projectImages,
            @RequestParam(value = "certificateImages", required = false) MultipartFile[] certificateImages) {

        try {
            // Get current user ID
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Parse JSON data
            ObjectMapper mapper = new ObjectMapper();
            CompanyProfileDTO companyProfileDTO = mapper.readValue(companyDataJson, CompanyProfileDTO.class);

            // Set user ID
            companyProfileDTO.setUserId(userDetails.getId());

            // Process project images
            if (projectImages != null) {
                processProjectImages(companyProfileDTO, projectImages);
            }

            // Process certificate images
            if (certificateImages != null) {
                processCertificateImages(companyProfileDTO, certificateImages);
            }

            // Create company profile
            CompanyProfile createdProfile = companyProfileService.createCompanyProfile(companyProfileDTO);
            CompanyProfileResponse response = mapToResponse(createdProfile);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PutMapping("/portfolio/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyProfileResponse> updateCompanyProfile(
            @PathVariable String id,
            @RequestParam("companyData") String companyDataJson,
            @RequestParam(value = "projectImages", required = false) MultipartFile[] projectImages,
            @RequestParam(value = "certificateImages", required = false) MultipartFile[] certificateImages) {

        try {
            // Verify ownership
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            CompanyProfile existingProfile = companyProfileService.getCompanyProfile(id);

            if (!existingProfile.getUserId().equals(userDetails.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // Parse JSON data
            ObjectMapper mapper = new ObjectMapper();
            CompanyProfileDTO companyProfileDTO = mapper.readValue(companyDataJson, CompanyProfileDTO.class);

            // Process images if provided
            if (projectImages != null) {
                processProjectImages(companyProfileDTO, projectImages);
            }

            if (certificateImages != null) {
                processCertificateImages(companyProfileDTO, certificateImages);
            }

            // Update profile
            CompanyProfile updatedProfile = companyProfileService.updateCompanyProfile(id, companyProfileDTO);
            CompanyProfileResponse response = mapToResponse(updatedProfile);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @DeleteMapping("/portfolio/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> deleteCompanyProfile(@PathVariable String id) {
        // Verify ownership
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CompanyProfile existingProfile = companyProfileService.getCompanyProfile(id);

        if (!existingProfile.getUserId().equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        companyProfileService.deleteCompanyProfile(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/portfolios")
    public ResponseEntity<List<CompanyProfileResponse>> getAllCompanyProfiles(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) String employeeSize,
            @RequestParam(required = false) String established) {

        List<CompanyProfile> companyProfiles = companyProfileService.getAllCompanyProfiles(
                type, location, rating, employeeSize, established);

        List<CompanyProfileResponse> responses = companyProfiles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    private void processProjectImages(CompanyProfileDTO dto, MultipartFile[] projectImages) {
        for (int i = 0; i < projectImages.length; i++) {
            if (projectImages[i] != null && !projectImages[i].isEmpty()) {
                String fileName = fileStorageService.storeFile(projectImages[i]);

                // Find which project this image belongs to based on a naming convention
                // For example: project_0_image_0, project_0_image_1, project_1_image_0, etc.
                // This is a simplified example - in a real app, you might use a more robust approach
                int projectIndex = i / 3; // Assuming max 3 images per project

                if (projectIndex < dto.getPastProjects().size()) {
                    dto.getPastProjects().get(projectIndex).getImageUrls().add(fileName);
                }
            }
        }
    }

    private void processCertificateImages(CompanyProfileDTO dto, MultipartFile[] certificateImages) {
        for (int i = 0; i < certificateImages.length; i++) {
            if (certificateImages[i] != null && !certificateImages[i].isEmpty()) {
                String fileName = fileStorageService.storeFile(certificateImages[i]);

                if (i < dto.getCertifications().size()) {
                    dto.getCertifications().get(i).setImageUrl(fileName);
                }
            }
        }
    }

    private CompanyProfileResponse mapToResponse(CompanyProfile companyProfile) {
        CompanyProfileResponse response = new CompanyProfileResponse();

        // Map basic fields
        response.setId(companyProfile.getId());
        response.setUserId(companyProfile.getUserId());
        response.setName(companyProfile.getCompanyName());
        response.setLicenseNumber(companyProfile.getLicenseNumber());
        response.setShortDescription(companyProfile.getShortDescription());
        response.setDescription(companyProfile.getDescription());
        response.setEstablished(companyProfile.getEstablishedYear());
        response.setLocation(companyProfile.getLocation());
        response.setEmployees(companyProfile.getEmployeeCount());
        response.setCoverImage(companyProfile.getCoverImageUrl());
        response.setProfileIcon(companyProfile.getProfileIconUrl());
        response.setCidaGrading(companyProfile.getCidaGrading());
        response.setEngineerCapacity(companyProfile.getEngineerCapacity());
        response.setServices(companyProfile.getServices());
        response.setRating(companyProfile.getRating());
        response.setType(companyProfile.getType());
        response.setContactInformation(companyProfile.getContactInformation());

        // Map projects
        if (companyProfile.getPastProjects() != null) {
            List<ProjectResponse> projectResponses = companyProfile.getPastProjects().stream()
                    .map(this::mapToProjectResponse)
                    .collect(Collectors.toList());
            response.setProjects(projectResponses);
        }

        // Get reviews
        List<Review> reviews = reviewService.getReviewsByCompanyId(companyProfile.getId());
        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
        response.setReviews(reviewResponses);

        // Create mock data for frontend-specific structures
        response.setTrackRecord(createMockTrackRecord(companyProfile));
        response.setFinancialStability(createMockFinancialStability(companyProfile));
        response.setServicesOffered(createMockServicesOffered(companyProfile));
        response.setCertificationsCompliance(createMockCertificationsCompliance(companyProfile));
        response.setAwardsRecognitions(createMockAwardsRecognitions(companyProfile));

        return response;
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        ProjectResponse response = new ProjectResponse();

        // Generate a unique ID based on the project name (for simplicity)
        int id = project.getName().hashCode() & 0x7fffffff; // Make sure it's positive
        response.setId(id % 100); // Keep it reasonably small

        // Use the first image URL if available
        if (project.getImageUrls() != null && !project.getImageUrls().isEmpty()) {
            response.setImage(project.getImageUrls().get(0));
        } else {
            response.setImage("https://placehold.co/300x200"); // Default placeholder
        }

        response.setTitle(project.getName());
        response.setDescription(project.getDescription());

        // Parse year from completion year string
        try {
            response.setYear(Integer.parseInt(project.getCompletionYear()));
        } catch (NumberFormatException e) {
            response.setYear(2023); // Default to current year if parsing fails
        }

        return response;
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setClientName(review.getClientName());
        response.setRating(review.getRating());
        response.setText(review.getText());
        response.setDate(review.getDate().toString());
        return response;
    }

    private TrackRecordResponse createMockTrackRecord(CompanyProfile companyProfile) {
        TrackRecordResponse response = new TrackRecordResponse();

        // Set years of experience based on established year
        try {
            int established = Integer.parseInt(companyProfile.getEstablishedYear());
            int currentYear = java.time.Year.now().getValue();
            response.setYearsOfExperience(currentYear - established);
        } catch (NumberFormatException e) {
            response.setYearsOfExperience(10); // Default value
        }

        // Convert projects to notable projects
        List<NotableProjectResponse> notableProjects = companyProfile.getPastProjects().stream()
                .map(project -> {
                    NotableProjectResponse notable = new NotableProjectResponse();
                    notable.setTitle(project.getName());
                    notable.setDescription(project.getDescription());

                    // Use first image if available
                    if (project.getImageUrls() != null && !project.getImageUrls().isEmpty()) {
                        notable.setImage(project.getImageUrls().get(0));
                    } else {
                        notable.setImage("https://placehold.co/300x200");
                    }

                    return notable;
                })
                .collect(Collectors.toList());

        response.setNotableProjects(notableProjects);

        // Create client satisfaction data
        ClientSatisfactionResponse satisfaction = new ClientSatisfactionResponse();
        satisfaction.setAverageRating(companyProfile.getRating());

        List<String> positiveFeedback = List.of(
                "High-quality construction with attention to detail.",
                "Efficient project management and timely delivery.",
                "Sustainable and innovative building solutions."
        );

        List<String> challengesFaced = List.of(
                "Minor project delays due to unforeseen supply chain disruptions.",
                "Slightly higher costs due to premium materials used."
        );

        satisfaction.setPositiveFeedback(positiveFeedback);
        satisfaction.setChallengesFaced(challengesFaced);

        response.setClientSatisfaction(satisfaction);

        return response;
    }

    private FinancialStabilityResponse createMockFinancialStability(CompanyProfile companyProfile) {
        FinancialStabilityResponse response = new FinancialStabilityResponse();

        response.setAnnualRevenue(companyProfile.getAnnualRevenue() != null
                ? companyProfile.getAnnualRevenue()
                : "$50 million (approx.)");

        response.setGrowthRate("10% year-over-year increase in revenue");
        response.setCreditRating("A+");

        List<String> majorInvestors = List.of(
                "Colombo Infrastructure Fund",
                "Sri Lanka Development Bank"
        );
        response.setMajorInvestors(majorInvestors);

        FinancialHealthResponse health = new FinancialHealthResponse();
        health.setCashReserves("$15 million");
        health.setDebtToEquityRatio("0.4");
        health.setLongTermStability("Consistently profitable over the past decade with reinvestment in modern construction technologies.");

        response.setFinancialHealth(health);

        return response;
    }

    private ServicesOfferedResponse createMockServicesOffered(CompanyProfile companyProfile) {
        ServicesOfferedResponse response = new ServicesOfferedResponse();

        // Use some of the actual services for primary services
        List<String> primaryServices = companyProfile.getServices().size() > 3
                ? companyProfile.getServices().subList(0, 3)
                : companyProfile.getServices();

        response.setPrimaryServices(primaryServices);

        // Create mock data for other service categories
        List<String> specializedServices = List.of(
                "Green Building Solutions",
                "Smart Building Integration",
                "Seismic-Resistant Construction",
                "Luxury Residential Projects"
        );

        List<String> consultationServices = List.of(
                "Architectural Design & Planning",
                "Project Feasibility Analysis",
                "Sustainable Construction Consulting"
        );

        List<String> technologyIntegration = List.of(
                "Building Information Modeling (BIM)",
                "AI-Based Construction Cost Estimation",
                "3D Printing for Prototype Structures"
        );

        response.setSpecializedServices(specializedServices);
        response.setConsultationServices(consultationServices);
        response.setTechnologyIntegration(technologyIntegration);

        return response;
    }

    private CertificationsComplianceResponse createMockCertificationsCompliance(CompanyProfile companyProfile) {
        CertificationsComplianceResponse response = new CertificationsComplianceResponse();

        // Convert certifications to certification details
        List<CertificationDetailResponse> industryCertifications = companyProfile.getCertifications().stream()
                .map(cert -> {
                    CertificationDetailResponse detail = new CertificationDetailResponse();
                    detail.setCertification(cert.getName());
                    detail.setDescription("Certified for high-quality management practices ensuring excellence in construction processes.");
                    return detail;
                })
                .collect(Collectors.toList());

        // Add a default certification if none exists
        if (industryCertifications.isEmpty()) {
            CertificationDetailResponse detail = new CertificationDetailResponse();
            detail.setCertification("ISO 9001");
            detail.setDescription("Certified for high-quality management practices ensuring excellence in construction processes.");
            industryCertifications.add(detail);
        }

        response.setIndustryCertifications(industryCertifications);

        List<String> governmentCompliance = List.of(
                "Registered with the National Construction Authority of Sri Lanka",
                "Fully compliant with local and international safety regulations",
                "Licensed for large-scale infrastructure projects"
        );

        List<String> safetyStandards = List.of(
                "OSHA Compliance for worker safety",
                "Regular safety audits and risk assessment",
                "Use of eco-friendly and non-toxic construction materials"
        );

        response.setGovernmentCompliance(governmentCompliance);
        response.setSafetyStandards(safetyStandards);

        return response;
    }

    private AwardsRecognitionsResponse createMockAwardsRecognitions(CompanyProfile companyProfile) {
        AwardsRecognitionsResponse response = new AwardsRecognitionsResponse();

        List<AwardResponse> majorAwards = List.of(
                new AwardResponse("Best Commercial Builder Award", 2021,
                        "Sri Lanka Construction Excellence Awards",
                        "Recognized for outstanding commercial project execution and sustainability practices."),
                new AwardResponse("Green Building Leader Award", 2022,
                        "National Sustainable Development Awards",
                        "Awarded for pioneering green building solutions in Sri Lanka.")
        );

        List<String> mediaFeatures = List.of(
                "Featured in 'Top 10 Construction Firms in Sri Lanka' by Business Times",
                "Interview with CEO published in 'Sri Lanka Infrastructure Review'",
                "Project showcase on National Television for innovative commercial buildings"
        );

        List<String> clientRecognition = List.of(
                "Received 'Best Customer Service Award' from the Sri Lanka Contractors Association",
                "Consistently rated among the top 5 construction firms by independent real estate surveys"
        );

        response.setMajorAwards(majorAwards);
        response.setMediaFeatures(mediaFeatures);
        response.setClientRecognition(clientRecognition);

        return response;
    }
}
package com.innovastruct.innovastruct_backend.service;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.innovastruct.innovastruct_backend.exceptions.ResourceNotFoundException;
import com.innovastruct.innovastruct_backend.model.Certification;
import com.innovastruct.innovastruct_backend.model.CompanyProfile;
import com.innovastruct.innovastruct_backend.model.ContactInformation;
import com.innovastruct.innovastruct_backend.model.Project;
import com.innovastruct.innovastruct_backend.payload.dto.CertificationDTO;
import com.innovastruct.innovastruct_backend.payload.dto.CompanyProfileDTO;
import com.innovastruct.innovastruct_backend.payload.dto.ContactInformationDTO;
import com.innovastruct.innovastruct_backend.payload.dto.ProjectDTO;
import com.innovastruct.innovastruct_backend.repository.CompanyProfileRepository;
import com.innovastruct.innovastruct_backend.repository.ReviewRepository;
import com.innovastruct.innovastruct_backend.repository.UserRepository;

@Service
public class CompanyProfileService {

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public CompanyProfile getCompanyProfile(String id) {
        return companyProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found with id: " + id));
    }

    public CompanyProfile getCompanyProfileByUserId(String userId) {
        return companyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found for user id: " + userId));
    }

    public List<CompanyProfile> getAllCompanyProfiles(String type, String location, String ratingStr, String employeeSize, String established) {
        List<CompanyProfile> companies = companyProfileRepository.findAll();

        // Apply filters
        if (type != null && !type.isEmpty()) {
            companies = companies.stream()
                    .filter(company -> company.getType() != null && company.getType().equals(type))
                    .collect(Collectors.toList());
        }

        if (location != null && !location.isEmpty()) {
            companies = companies.stream()
                    .filter(company -> company.getLocation() != null && company.getLocation().contains(location))
                    .collect(Collectors.toList());
        }

        if (ratingStr != null && !ratingStr.isEmpty()) {
            try {
                float rating = Float.parseFloat(ratingStr);
                companies = companies.stream()
                        .filter(company -> company.getRating() >= rating)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Ignore invalid rating
            }
        }

        if (employeeSize != null && !employeeSize.isEmpty()) {
            companies = companies.stream()
                    .filter(company -> {
                        try {
                            int count = Integer.parseInt(company.getEmployeeCount());
                            switch (employeeSize) {
                                case "small":
                                    return count < 100;
                                case "medium":
                                    return count >= 100 && count <= 200;
                                case "large":
                                    return count > 200;
                                default:
                                    return true;
                            }
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }

        if (established != null && !established.isEmpty()) {
            companies = companies.stream()
                    .filter(company -> {
                        try {
                            int year = Integer.parseInt(company.getEstablishedYear());
                            switch (established) {
                                case "before2000":
                                    return year < 2000;
                                case "2000to2010":
                                    return year >= 2000 && year <= 2010;
                                case "after2010":
                                    return year > 2010;
                                default:
                                    return true;
                            }
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }

        return companies;
    }

    public CompanyProfile createCompanyProfile(CompanyProfileDTO companyProfileDTO) {
        // Verify that user exists
        userRepository.findById(companyProfileDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + companyProfileDTO.getUserId()));

        // Check if company profile already exists for this user
        if (companyProfileRepository.findByUserId(companyProfileDTO.getUserId()).isPresent()) {
            throw new IllegalStateException("Company profile already exists for this user");
        }

        // Map DTO to entity
        CompanyProfile companyProfile = new CompanyProfile();
        companyProfile.setUserId(companyProfileDTO.getUserId());
        companyProfile.setCompanyName(companyProfileDTO.getCompanyName());
        companyProfile.setLicenseNumber(companyProfileDTO.getLicenseNumber());
        companyProfile.setShortDescription(companyProfileDTO.getShortDescription());
        companyProfile.setDescription(companyProfileDTO.getDescription());
        companyProfile.setEstablishedYear(companyProfileDTO.getEstablishedYear());
        companyProfile.setLocation(companyProfileDTO.getLocation());
        companyProfile.setEmployeeCount(companyProfileDTO.getEmployeeCount());
        companyProfile.setCoverImageUrl(companyProfileDTO.getCoverImageUrl());
        companyProfile.setProfileIconUrl(companyProfileDTO.getProfileIconUrl());
        companyProfile.setCidaGrading(companyProfileDTO.getCidaGrading());
        companyProfile.setEngineerCapacity(companyProfileDTO.getEngineerCapacity());
        companyProfile.setServices(new ArrayList<>(companyProfileDTO.getServices()));
        companyProfile.setAnnualRevenue(companyProfileDTO.getAnnualRevenue());
        companyProfile.setFundingSources(companyProfileDTO.getFundingSources());
        companyProfile.setType(companyProfileDTO.getType());

        // Map projects
        List<Project> projects = companyProfileDTO.getPastProjects().stream()
                .map(this::mapToProject)
                .collect(Collectors.toList());
        companyProfile.setPastProjects(projects);

        // Map certifications
        List<Certification> certifications = companyProfileDTO.getCertifications().stream()
                .map(this::mapToCertification)
                .collect(Collectors.toList());
        companyProfile.setCertifications(certifications);

        // Map contact information
        if (companyProfileDTO.getContactInformation() != null) {
            companyProfile.setContactInformation(mapToContactInformation(companyProfileDTO.getContactInformation()));
        }

        // Initial rating for new companies
        companyProfile.setRating(0);

        return companyProfileRepository.save(companyProfile);
    }

    public CompanyProfile updateCompanyProfile(String id, CompanyProfileDTO companyProfileDTO) {
        CompanyProfile existingProfile = getCompanyProfile(id);

        // Update fields
        if (companyProfileDTO.getCompanyName() != null) {
            existingProfile.setCompanyName(companyProfileDTO.getCompanyName());
        }

        if (companyProfileDTO.getLicenseNumber() != null) {
            existingProfile.setLicenseNumber(companyProfileDTO.getLicenseNumber());
        }

        if (companyProfileDTO.getShortDescription() != null) {
            existingProfile.setShortDescription(companyProfileDTO.getShortDescription());
        }

        if (companyProfileDTO.getDescription() != null) {
            existingProfile.setDescription(companyProfileDTO.getDescription());
        }

        if (companyProfileDTO.getEstablishedYear() != null) {
            existingProfile.setEstablishedYear(companyProfileDTO.getEstablishedYear());
        }

        if (companyProfileDTO.getLocation() != null) {
            existingProfile.setLocation(companyProfileDTO.getLocation());
        }

        if (companyProfileDTO.getEmployeeCount() != null) {
            existingProfile.setEmployeeCount(companyProfileDTO.getEmployeeCount());
        }

        if (companyProfileDTO.getCoverImageUrl() != null) {
            existingProfile.setCoverImageUrl(companyProfileDTO.getCoverImageUrl());
        }

        if (companyProfileDTO.getProfileIconUrl() != null) {
            existingProfile.setProfileIconUrl(companyProfileDTO.getProfileIconUrl());
        }

        if (companyProfileDTO.getCidaGrading() != null) {
            existingProfile.setCidaGrading(companyProfileDTO.getCidaGrading());
        }

        if (companyProfileDTO.getEngineerCapacity() != null) {
            existingProfile.setEngineerCapacity(companyProfileDTO.getEngineerCapacity());
        }

        if (companyProfileDTO.getServices() != null && !companyProfileDTO.getServices().isEmpty()) {
            existingProfile.setServices(new ArrayList<>(companyProfileDTO.getServices()));
        }

        if (companyProfileDTO.getAnnualRevenue() != null) {
            existingProfile.setAnnualRevenue(companyProfileDTO.getAnnualRevenue());
        }

        if (companyProfileDTO.getFundingSources() != null) {
            existingProfile.setFundingSources(companyProfileDTO.getFundingSources());
        }

        if (companyProfileDTO.getType() != null) {
            existingProfile.setType(companyProfileDTO.getType());
        }

        // Update projects if provided
        if (companyProfileDTO.getPastProjects() != null && !companyProfileDTO.getPastProjects().isEmpty()) {
            List<Project> projects = companyProfileDTO.getPastProjects().stream()
                    .map(this::mapToProject)
                    .collect(Collectors.toList());
            existingProfile.setPastProjects(projects);
        }

        // Update certifications if provided
        if (companyProfileDTO.getCertifications() != null && !companyProfileDTO.getCertifications().isEmpty()) {
            List<Certification> certifications = companyProfileDTO.getCertifications().stream()
                    .map(this::mapToCertification)
                    .collect(Collectors.toList());
            existingProfile.setCertifications(certifications);
        }

        // Update contact information if provided
        if (companyProfileDTO.getContactInformation() != null) {
            existingProfile.setContactInformation(mapToContactInformation(companyProfileDTO.getContactInformation()));
        }

        return companyProfileRepository.save(existingProfile);
    }

    public void deleteCompanyProfile(String id) {
        CompanyProfile companyProfile = getCompanyProfile(id);
        companyProfileRepository.delete(companyProfile);
    }

    public void updateCompanyRating(String companyId) {
        CompanyProfile companyProfile = getCompanyProfile(companyId);

        // Calculate average rating from reviews
        List<Float> ratings = reviewRepository.findRatingsByCompanyId(companyId)
                .stream()
                .map(review -> review.getRating())
                .collect(Collectors.toList());

        if (!ratings.isEmpty()) {
            float averageRating = ratings.stream().reduce(0f, Float::sum) / ratings.size();
            companyProfile.setRating(averageRating);
            companyProfileRepository.save(companyProfile);
        }
    }

    private Project mapToProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setCompletionYear(projectDTO.getCompletionYear());
        project.setImageUrls(new ArrayList<>(projectDTO.getImageUrls()));
        return project;
    }

    private Certification mapToCertification(CertificationDTO certDTO) {
        Certification certification = new Certification();
        certification.setName(certDTO.getName());
        certification.setOrganization(certDTO.getOrganization());

        try {
            if (certDTO.getIssueDate() != null && !certDTO.getIssueDate().isEmpty()) {
                certification.setIssueDate(dateFormat.parse(certDTO.getIssueDate()));
            }

            if (certDTO.getExpiryDate() != null && !certDTO.getExpiryDate().isEmpty()) {
                certification.setExpiryDate(dateFormat.parse(certDTO.getExpiryDate()));
            }
        } catch (ParseException e) {
            // Handle date parsing exception
            throw new IllegalArgumentException("Invalid date format", e);
        }

        certification.setImageUrl(certDTO.getImageUrl());
        return certification;
    }

    private ContactInformation mapToContactInformation(ContactInformationDTO contactDTO) {
        if (contactDTO == null) {
            return null;
        }

        ContactInformation contactInfo = new ContactInformation();
        contactInfo.setEmail(contactDTO.getEmail());
        contactInfo.setPhoneNumber(contactDTO.getPhoneNumber());
        contactInfo.setWebsite(contactDTO.getWebsite());
        return contactInfo;
    }
}
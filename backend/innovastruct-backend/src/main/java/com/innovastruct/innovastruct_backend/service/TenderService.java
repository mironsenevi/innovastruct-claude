package com.innovastruct.innovastruct_backend.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.innovastruct.innovastruct_backend.exceptions.ResourceNotFoundException ;
import com.innovastruct.innovastruct_backend.model.Tender;
import com.innovastruct.innovastruct_backend.model.User;
import com.innovastruct.innovastruct_backend.model.Bid;
import com.innovastruct.innovastruct_backend.model.CompanyProfile;
import com.innovastruct.innovastruct_backend.model.Notification;
import com.innovastruct.innovastruct_backend.payload.request.TenderRequest;
import com.innovastruct.innovastruct_backend.payload.request.BidRequest;
import com.innovastruct.innovastruct_backend.payload.response.LocationTenderResponse;
import com.innovastruct.innovastruct_backend.repository.TenderRepository;
import com.innovastruct.innovastruct_backend.repository.BidRepository;
import com.innovastruct.innovastruct_backend.repository.UserRepository;
import com.innovastruct.innovastruct_backend.repository.CompanyProfileRepository;
import com.innovastruct.innovastruct_backend.repository.NotificationRepository;
import com.innovastruct.innovastruct_backend.security.services.UserDetailsImpl;

@Service
public class TenderService {

    @Autowired
    private TenderRepository tenderRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Tender> getAllTenders(String location, String category, String priority, String budget, String daysLeft) {
        List<Tender> tenders = tenderRepository.findByStatus("open");

        // Apply filters
        if (location != null && !location.isEmpty()) {
            tenders = tenders.stream()
                    .filter(tender -> tender.getLocation() != null && tender.getLocation().equals(location))
                    .collect(Collectors.toList());
        }

        if (category != null && !category.isEmpty()) {
            tenders = tenders.stream()
                    .filter(tender -> tender.getCategory() != null && tender.getCategory().equals(category))
                    .collect(Collectors.toList());
        }

        if (priority != null && !priority.isEmpty()) {
            tenders = tenders.stream()
                    .filter(tender -> tender.getPriority() != null && tender.getPriority().equals(priority))
                    .collect(Collectors.toList());
        }

        if (budget != null && !budget.isEmpty()) {
            String[] parts = budget.split("-");
            double minBudget = Double.parseDouble(parts[0]);
            if (parts.length > 1 && !parts[1].isEmpty()) {
                double maxBudget = Double.parseDouble(parts[1]);
                tenders = tenders.stream()
                        .filter(tender -> tender.getBudget() >= minBudget && tender.getBudget() <= maxBudget)
                        .collect(Collectors.toList());
            } else {
                tenders = tenders.stream()
                        .filter(tender -> tender.getBudget() >= minBudget)
                        .collect(Collectors.toList());
            }
        }

        if (daysLeft != null && !daysLeft.isEmpty()) {
            String[] parts = daysLeft.split("-");
            int minDays = Integer.parseInt(parts[0]);
            if (parts.length > 1 && !parts[1].isEmpty()) {
                int maxDays = Integer.parseInt(parts[1]);
                tenders = tenders.stream()
                        .filter(tender -> {
                            int days = calculateDaysLeft(tender.getDeadline());
                            return days >= minDays && days <= maxDays;
                        })
                        .collect(Collectors.toList());
            } else {
                tenders = tenders.stream()
                        .filter(tender -> calculateDaysLeft(tender.getDeadline()) >= minDays)
                        .collect(Collectors.toList());
            }
        }

        return tenders;
    }

    private int calculateDaysLeft(Date deadline) {
        if (deadline == null) {
            return 0;
        }

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar deadlineCalendar = Calendar.getInstance();
        deadlineCalendar.setTime(deadline);
        deadlineCalendar.set(Calendar.HOUR_OF_DAY, 0);
        deadlineCalendar.set(Calendar.MINUTE, 0);
        deadlineCalendar.set(Calendar.SECOND, 0);
        deadlineCalendar.set(Calendar.MILLISECOND, 0);

        // Calculate days difference
        long diffMillis = deadlineCalendar.getTimeInMillis() - today.getTimeInMillis();
        return Math.max(0, (int) (diffMillis / (24 * 60 * 60 * 1000)));
    }

    public Tender getTenderById(String id) {
        return tenderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found with id: " + id));
    }

    public List<Tender> getTendersByClientId(String clientId) {
        return tenderRepository.findByClientId(clientId);
    }

    @Transactional
    public Tender createTender(TenderRequest tenderRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String clientId = userDetails.getId();

        Tender tender = new Tender();
        tender.setClientId(clientId);
        tender.setTitle(tenderRequest.getTitle());
        tender.setDescription(tenderRequest.getDescription());
        tender.setPlan(tenderRequest.getPlan());
        tender.setBoq(tenderRequest.getBoq());
        tender.setBudget(tenderRequest.getBudget());
        tender.setDeadline(tenderRequest.getDeadline());
        tender.setStatus("open");
        tender.setCategory(tenderRequest.getCategory());
        tender.setLocation(tenderRequest.getLocation());
        tender.setPriority(tenderRequest.getPriority());
        tender.setBids(new ArrayList<>());
        tender.setCreatedAt(new Date());

        Tender savedTender = tenderRepository.save(tender);

        // Create notifications for all companies
        List<User> companyUsers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().name().equals("ROLE_COMPANY")))
                .collect(Collectors.toList());

        for (User user : companyUsers) {
            createNotification(user.getId(), "New tender available: " + tender.getTitle(),
                    "info", savedTender.getId(), "tender");
        }

        return savedTender;
    }

    @Transactional
    public Bid submitBid(String tenderId, BidRequest bidRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String companyId = userDetails.getId();

        // Verify tender exists and is open
        Tender tender = getTenderById(tenderId);
        if (!"open".equals(tender.getStatus())) {
            throw new IllegalStateException("Cannot submit bid for tender that is not open");
        }

        // Get company name
        CompanyProfile companyProfile = companyProfileRepository.findByUserId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found for user id: " + companyId));

        Bid bid = new Bid();
        bid.setTenderId(tenderId);
        bid.setCompanyId(companyId);
        bid.setCompanyName(companyProfile.getCompanyName());
        bid.setBidAmount(bidRequest.getBidAmount());
        bid.setProposedDuration(bidRequest.getProposedDuration());
        bid.setTechnicalProposalUrl(bidRequest.getTechnicalProposalUrl());
        bid.setFinancialProposalUrl(bidRequest.getFinancialProposalUrl());
        bid.setStatus("pending");
        bid.setCreatedAt(new Date());

        Bid savedBid = bidRepository.save(bid);

        // Add bid to tender
        if (tender.getBids() == null) {
            tender.setBids(new ArrayList<>());
        }
        tender.getBids().add(savedBid);
        tenderRepository.save(tender);

        // Notify client
        createNotification(tender.getClientId(),
                "New bid received from " + companyProfile.getCompanyName() + " for " + tender.getTitle(),
                "info", tenderId, "bid");

        return savedBid;
    }

    public List<Bid> getBidsForTender(String tenderId) {
        return bidRepository.findByTenderId(tenderId);
    }

    public List<Bid> getBidsByCompanyId(String companyId) {
        return bidRepository.findByCompanyId(companyId);
    }

    @Transactional
    public Tender closeTender(String tenderId) {
        Tender tender = getTenderById(tenderId);

        // Verify that the current user is the owner
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!tender.getClientId().equals(userDetails.getId())) {
            throw new IllegalStateException("You are not authorized to close this tender");
        }

        tender.setStatus("closed");

        // Notify bidders
        if (tender.getBids() != null) {
            for (Bid bid : tender.getBids()) {
                createNotification(bid.getCompanyId(),
                        "Tender '" + tender.getTitle() + "' has been closed",
                        "info", tenderId, "tender");
            }
        }

        return tenderRepository.save(tender);
    }

    @Transactional
    public Tender awardBid(String tenderId, String bidId) {
        Tender tender = getTenderById(tenderId);

        // Verify that the current user is the owner
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!tender.getClientId().equals(userDetails.getId())) {
            throw new IllegalStateException("You are not authorized to award this tender");
        }

        // Find the bid
        Bid awardedBid = tender.getBids().stream()
                .filter(bid -> bid.getId().equals(bidId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found with id: " + bidId));

        // Update bid status
        awardedBid.setStatus("accepted");
        bidRepository.save(awardedBid);

        // Update all other bids to rejected
        for (Bid bid : tender.getBids()) {
            if (!bid.getId().equals(bidId)) {
                bid.setStatus("rejected");
                bidRepository.save(bid);

                // Notify rejected bidders
                createNotification(bid.getCompanyId(),
                        "Your bid for '" + tender.getTitle() + "' was not selected",
                        "warning", tenderId, "bid");
            }
        }

        // Update tender status
        tender.setStatus("awarded");

        // Notify awarded bidder
        createNotification(awardedBid.getCompanyId(),
                "Congratulations! Your bid for '" + tender.getTitle() + "' has been accepted",
                "success", tenderId, "bid");

        return tenderRepository.save(tender);
    }

    public List<LocationTenderResponse> getTendersGroupedByLocation() {
        List<Tender> activeTenders = tenderRepository.findByStatus("open");

        // Group tenders by location
        Map<String, List<Tender>> tendersByLocation = activeTenders.stream()
                .filter(tender -> tender.getLocation() != null && !tender.getLocation().isEmpty())
                .collect(Collectors.groupingBy(Tender::getLocation));

        // Map of locations to coordinates (in real app, this would come from a database)
        Map<String, double[]> locationCoordinates = new HashMap<>();
        locationCoordinates.put("Colombo", new double[] { 6.9271, 79.8612 });
        locationCoordinates.put("Kandy", new double[] { 7.2906, 80.6337 });
        locationCoordinates.put("Galle", new double[] { 6.0535, 80.2210 });
        locationCoordinates.put("Ella", new double[] { 6.8750, 81.0467 });
        locationCoordinates.put("Negombo", new double[] { 7.2094, 79.8345 });
        locationCoordinates.put("Kurunegala", new double[] { 7.4863, 80.3647 });
        locationCoordinates.put("Anuradhapura", new double[] { 8.3114, 80.4037 });
        locationCoordinates.put("Jaffna", new double[] { 9.6615, 80.0255 });
        locationCoordinates.put("Battaramulla", new double[] { 6.9270, 79.9092 });
        locationCoordinates.put("Bentota", new double[] { 6.4253, 80.0022 });

        // Create response objects
        List<LocationTenderResponse> response = new ArrayList<>();

        for (Map.Entry<String, List<Tender>> entry : tendersByLocation.entrySet()) {
            String location = entry.getKey();
            List<Tender> tenders = entry.getValue();

            if (locationCoordinates.containsKey(location)) {
                double[] coords = locationCoordinates.get(location);

                LocationTenderResponse locationData = new LocationTenderResponse();
                locationData.setDistrict(location);
                locationData.setCoordinates(new com.innovastruct.innovastruct_backend.payload.response.CoordinatesResponse(coords[0], coords[1]));
                locationData.setTenderCount(tenders.size());

                // Calculate total value
                double totalValue = tenders.stream()
                        .mapToDouble(Tender::getBudget)
                        .sum();
                locationData.setTotalValue(totalValue);

                // Map active tenders to simplified response
                List<com.innovastruct.innovastruct_backend.payload.response.SimpleTenderResponse> simpleTenders = tenders.stream()
                        .map(tender -> new com.innovastruct.innovastruct_backend.payload.response.SimpleTenderResponse(
                                tender.getId(), tender.getTitle(), tender.getBudget()))
                        .collect(Collectors.toList());
                locationData.setActiveTenders(simpleTenders);

                response.add(locationData);
            }
        }

        return response;
    }

    private void createNotification(String userId, String message, String type, String relatedEntityId, String relatedEntityType) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedId(relatedEntityId);
        notification.setRead(false);
        notification.setCreatedAt(new Date());

        notificationRepository.save(notification);
    }
}
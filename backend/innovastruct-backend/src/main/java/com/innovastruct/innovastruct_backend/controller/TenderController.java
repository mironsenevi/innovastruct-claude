package com.innovastruct.innovastruct_backend.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovastruct.innovastruct_backend.model.Bid;
import com.innovastruct.innovastruct_backend.model.CompanyProfile;
import com.innovastruct.innovastruct_backend.model.Tender;
import com.innovastruct.innovastruct_backend.payload.request.BidRequest;
import com.innovastruct.innovastruct_backend.payload.request.TenderRequest;
import com.innovastruct.innovastruct_backend.payload.response.BidResponse;
import com.innovastruct.innovastruct_backend.payload.response.LocationTenderResponse;
import com.innovastruct.innovastruct_backend.payload.response.TenderResponse;
import com.innovastruct.innovastruct_backend.repository.CompanyProfileRepository;
import com.innovastruct.innovastruct_backend.security.services.UserDetailsImpl;
import com.innovastruct.innovastruct_backend.service.FileStorageService;
import com.innovastruct.innovastruct_backend.service.TenderService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tenders")
public class TenderController {

    @Autowired
    private TenderService tenderService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<TenderResponse> createTender(@Valid @RequestBody TenderRequest tenderRequest) {
        Tender createdTender = tenderService.createTender(tenderRequest);
        TenderResponse response = mapToTenderResponse(createdTender);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<TenderResponse>> getAllTenders(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String budget,
            @RequestParam(required = false) String daysLeft) {

        List<Tender> tenders = tenderService.getAllTenders(location, category, priority, budget, daysLeft);
        List<TenderResponse> response = tenders.stream()
                .map(this::mapToTenderResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenderResponse> getTenderById(@PathVariable String id) {
        Tender tender = tenderService.getTenderById(id);
        TenderResponse response = mapToTenderResponse(tender);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('CLIENT') and #clientId == authentication.principal.id")
    public ResponseEntity<List<TenderResponse>> getTendersByClientId(@PathVariable String clientId) {
        List<Tender> tenders = tenderService.getTendersByClientId(clientId);
        List<TenderResponse> response = tenders.stream()
                .map(this::mapToTenderResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{tenderId}/bids")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<BidResponse> submitBid(
            @PathVariable String tenderId,
            @RequestParam("bidData") String bidDataJson,
            @RequestParam(value = "technicalProposal", required = false) MultipartFile technicalProposal,
            @RequestParam(value = "financialProposal", required = false) MultipartFile financialProposal) {

        try {
            // Parse JSON data
            ObjectMapper mapper = new ObjectMapper();
            BidRequest bidRequest = mapper.readValue(bidDataJson, BidRequest.class);

            // Process and store files
            if (technicalProposal != null) {
                String technicalProposalUrl = fileStorageService.storeFile(technicalProposal);
                bidRequest.setTechnicalProposalUrl(technicalProposalUrl);
            }

            if (financialProposal != null) {
                String financialProposalUrl = fileStorageService.storeFile(financialProposal);
                bidRequest.setFinancialProposalUrl(financialProposalUrl);
            }

            // Create bid
            Bid createdBid = tenderService.submitBid(tenderId, bidRequest);
            BidResponse response = mapToBidResponse(createdBid);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{tenderId}/bids")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<BidResponse>> getBidsForTender(@PathVariable String tenderId) {
        List<Bid> bids = tenderService.getBidsForTender(tenderId);
        List<BidResponse> response = bids.stream()
                .map(this::mapToBidResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/bids/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<BidResponse>> getBidsByCompany() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String companyId = userDetails.getId();

        List<Bid> bids = tenderService.getBidsByCompanyId(companyId);
        List<BidResponse> response = bids.stream()
                .map(this::mapToBidResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<TenderResponse> closeTender(@PathVariable String id) {
        Tender updatedTender = tenderService.closeTender(id);
        TenderResponse response = mapToTenderResponse(updatedTender);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{tenderId}/award/{bidId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<TenderResponse> awardBid(
            @PathVariable String tenderId,
            @PathVariable String bidId) {

        Tender updatedTender = tenderService.awardBid(tenderId, bidId);
        TenderResponse response = mapToTenderResponse(updatedTender);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/map")
    public ResponseEntity<List<LocationTenderResponse>> getTendersForMap() {
        List<LocationTenderResponse> response = tenderService.getTendersGroupedByLocation();
        return ResponseEntity.ok(response);
    }

    private TenderResponse mapToTenderResponse(Tender tender) {
        TenderResponse response = new TenderResponse();
        response.setId(tender.getId());
        response.setTitle(tender.getTitle());
        response.setDescription(tender.getDescription());
        response.setLocation(tender.getLocation());
        response.setBudget(tender.getBudget());
        response.setDeadline(tender.getDeadline());
        response.setStatus(tender.getStatus());
        response.setCategory(tender.getCategory());
        response.setPriority(tender.getPriority());
        response.setBidsCount(tender.getBids() != null ? tender.getBids().size() : 0);
        response.setDaysLeft(tender.getDaysLeft());
        response.setCreatedAt(tender.getCreatedAt());

        // Calculate lowest bid
        if (tender.getBids() != null && !tender.getBids().isEmpty()) {
            double lowestBid = tender.getBids().stream()
                    .mapToDouble(Bid::getBidAmount)
                    .min()
                    .orElse(0);
            response.setLowestBid(lowestBid);
        }

        // Map bids
        if (tender.getBids() != null) {
            List<BidResponse> bidResponses = tender.getBids().stream()
                    .map(this::mapToBidResponse)
                    .collect(Collectors.toList());
            response.setBids(bidResponses);
        }

        return response;
    }

    private BidResponse mapToBidResponse(Bid bid) {
        BidResponse response = new BidResponse();
        response.setId(bid.getId());
        response.setTenderId(bid.getTenderId());
        response.setCompanyId(bid.getCompanyId());
        response.setCompanyName(bid.getCompanyName());
        response.setBidAmount(bid.getBidAmount());
        response.setProposedDuration(bid.getProposedDuration());
        response.setTechnicalProposalUrl(bid.getTechnicalProposalUrl());
        response.setFinancialProposalUrl(bid.getFinancialProposalUrl());
        response.setStatus(bid.getStatus());
        response.setCreatedAt(bid.getCreatedAt());
        return response;
    }
}
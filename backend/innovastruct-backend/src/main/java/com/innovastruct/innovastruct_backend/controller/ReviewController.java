package com.innovastruct.innovastruct_backend.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innovastruct.innovastruct_backend.model.Review ;
import com.innovastruct.innovastruct_backend.payload.request.ReviewRequest ;
import com.innovastruct.innovastruct_backend.payload.response.MessageResponse ;
import com.innovastruct.innovastruct_backend.payload.response.ReviewResponse ;
import com.innovastruct.innovastruct_backend.service.ReviewService ;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByCompanyId(@PathVariable String companyId) {
        List<Review> reviews = reviewService.getReviewsByCompanyId(companyId);
        List<ReviewResponse> response = reviews.stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest reviewRequest) {
        Review createdReview = reviewService.createReview(reviewRequest);
        ReviewResponse response = mapToReviewResponse(createdReview);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> deleteReview(@PathVariable String id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok(new MessageResponse("Review deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete review: " + e.getMessage()));
        }
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
}
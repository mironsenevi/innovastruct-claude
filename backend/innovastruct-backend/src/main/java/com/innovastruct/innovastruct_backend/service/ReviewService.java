package com.innovastruct.innovastruct_backend.service;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.innovastruct.innovastruct_backend.exceptions.ResourceNotFoundException;
import com.innovastruct.innovastruct_backend.model.Review;
import com.innovastruct.innovastruct_backend.model.User;
import com.innovastruct.innovastruct_backend.payload.request.ReviewRequest;
import com.innovastruct.innovastruct_backend.repository.CompanyProfileRepository;
import com.innovastruct.innovastruct_backend.repository.ReviewRepository;
import com.innovastruct.innovastruct_backend.repository.UserRepository;
import com.innovastruct.innovastruct_backend.security.services.UserDetailsImpl;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private CompanyProfileService companyProfileService;

    public List<Review> getReviewsByCompanyId(String companyId) {
        return reviewRepository.findByCompanyId(companyId);
    }

    @Transactional
    public Review createReview(ReviewRequest reviewRequest) {
        // Get current user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String clientId = userDetails.getId();

        // Verify company exists
        companyProfileRepository.findById(reviewRequest.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found with id: " + reviewRequest.getCompanyId()));

        // Get user name
        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + clientId));

        Review review = new Review();
        review.setClientId(clientId);
        review.setClientName(user.getName());
        review.setCompanyId(reviewRequest.getCompanyId());
        review.setRating(reviewRequest.getRating());
        review.setText(reviewRequest.getText());
        review.setDate(new Date());

        Review savedReview = reviewRepository.save(review);

        // Update company rating
        companyProfileService.updateCompanyRating(reviewRequest.getCompanyId());

        return savedReview;
    }

    public Review getReviewById(String id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }

    @Transactional
    public void deleteReview(String id) {
        // Get current user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String clientId = userDetails.getId();

        Review review = getReviewById(id);

        // Verify that the current user is the owner of the review
        if (!review.getClientId().equals(clientId)) {
            throw new IllegalStateException("You are not authorized to delete this review");
        }

        reviewRepository.delete(review);

        // Update company rating
        companyProfileService.updateCompanyRating(review.getCompanyId());
    }
}
package com.innovastruct.innovastruct_backend.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.innovastruct.innovastruct_backend.model.Review ;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByCompanyId(String companyId);

    List<Review> findByClientId(String clientId);

    @Query("{'companyId': ?0, 'rating': {$gte: ?1}}")
    List<Review> findByCompanyIdAndRatingGreaterThanEqual(String companyId, float rating);

    // Calculate average rating for a company
    @Query(value = "{ 'companyId' : ?0 }", fields = "{ 'rating' : 1 }")
    List<Review> findRatingsByCompanyId(String companyId);
}
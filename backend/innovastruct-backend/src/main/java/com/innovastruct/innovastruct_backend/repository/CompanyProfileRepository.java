package com.innovastruct.innovastruct_backend.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.innovastruct.innovastruct_backend.model.CompanyProfile ;

public interface CompanyProfileRepository extends MongoRepository<CompanyProfile, String> {
    Optional<CompanyProfile> findByUserId(String userId);

    List<CompanyProfile> findByServicesContaining(String service);

    @Query("{'rating': {$gte: ?0}}")
    List<CompanyProfile> findByRatingGreaterThanEqual(float rating);

    List<CompanyProfile> findByLocation(String location);

    List<CompanyProfile> findByType(String type);
}
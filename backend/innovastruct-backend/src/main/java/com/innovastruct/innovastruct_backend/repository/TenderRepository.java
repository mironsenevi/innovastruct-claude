package com.innovastruct.innovastruct_backend.repository;


import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.innovastruct.innovastruct_backend.model.Tender ;

public interface TenderRepository extends MongoRepository<Tender, String> {
    List<Tender> findByClientId(String clientId);

    List<Tender> findByStatus(String status);

    List<Tender> findByCategory(String category);

    List<Tender> findByLocation(String location);

    List<Tender> findByPriority(String priority);

    @Query("{'deadline': {$gte: ?0}}")
    List<Tender> findByDeadlineAfter(Date date);

    @Query("{'budget': {$gte: ?0, $lte: ?1}}")
    List<Tender> findByBudgetBetween(double min, double max);

    @Query("{'budget': {$gte: ?0}}")
    List<Tender> findByBudgetGreaterThanEqual(double min);

    List<Tender> findByDeadlineBetween(Date start, Date end);
}
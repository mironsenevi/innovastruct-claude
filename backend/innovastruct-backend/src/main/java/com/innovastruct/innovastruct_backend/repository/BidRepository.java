package com.innovastruct.innovastruct_backend.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.innovastruct.innovastruct_backend.model.Bid ;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends MongoRepository<Bid, String> {
    List<Bid> findByTenderId(String tenderId);

    Optional<Bid> findByTenderIdAndCompanyId(String tenderId, String companyId);

    List<Bid> findByCompanyId(String companyId);

    List<Bid> findByCompanyIdAndStatus(String companyId, String status);

    List<Bid> findByTenderIdAndStatus(String tenderId, String status);

    Long countByTenderId(String tenderId);

    Long countByCompanyId(String companyId);
}
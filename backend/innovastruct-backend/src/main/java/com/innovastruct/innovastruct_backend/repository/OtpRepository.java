package com.innovastruct.innovastruct_backend.repository;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.innovastruct.innovastruct_backend.model.OtpEntity ;

public interface OtpRepository extends MongoRepository<OtpEntity, String> {
    Optional<OtpEntity> findByPhone(String phone);

    void deleteByPhone(String phone);
}
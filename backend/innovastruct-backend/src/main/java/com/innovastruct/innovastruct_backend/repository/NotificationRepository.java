package com.innovastruct.innovastruct_backend.repository;


import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.innovastruct.innovastruct_backend.model.Notification ;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdAndRead(String userId, boolean read, Sort sort);

    List<Notification> findByUserId(String userId, Sort sort);

    long countByUserIdAndRead(String userId, boolean read);
}
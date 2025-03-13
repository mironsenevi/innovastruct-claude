package com.innovastruct.innovastruct_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.innovastruct.innovastruct_backend.model.User;
import com.innovastruct.innovastruct_backend.model.Role;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findAllByRoles(Role role);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
}
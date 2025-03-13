package com.innovastruct.innovastruct_backend.repository;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.innovastruct.innovastruct_backend.model.ERole ;
import com.innovastruct.innovastruct_backend.model.Role ;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
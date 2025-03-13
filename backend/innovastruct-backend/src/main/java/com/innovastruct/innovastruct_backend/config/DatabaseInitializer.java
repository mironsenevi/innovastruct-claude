package com.innovastruct.innovastruct_backend.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.innovastruct.innovastruct_backend.model.ERole ;
import com.innovastruct.innovastruct_backend.model.Role ;
import com.innovastruct.innovastruct_backend.repository.RoleRepository ;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        if (roleRepository.count() == 0) {
            // Create roles
            Role clientRole = new Role(ERole.ROLE_CLIENT);
            Role companyRole = new Role(ERole.ROLE_COMPANY);
            Role adminRole = new Role(ERole.ROLE_ADMIN);

            // Save roles
            roleRepository.save(clientRole);
            roleRepository.save(companyRole);
            roleRepository.save(adminRole);

            System.out.println("Roles initialized successfully");
        }
    }
}
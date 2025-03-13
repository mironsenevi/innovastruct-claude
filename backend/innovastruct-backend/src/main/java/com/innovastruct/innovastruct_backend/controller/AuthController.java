package com.innovastruct.innovastruct_backend.controller;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innovastruct.innovastruct_backend.model.ERole ;
import com.innovastruct.innovastruct_backend.model.Role ;
import com.innovastruct.innovastruct_backend.model.User ;
import com.innovastruct.innovastruct_backend.payload.request.CompanySignupRequest ;
import com.innovastruct.innovastruct_backend.payload.request.LoginRequest ;
import com.innovastruct.innovastruct_backend.payload.request.SignupRequest ;
import com.innovastruct.innovastruct_backend.payload.request.VerifyOtpRequest ;
import com.innovastruct.innovastruct_backend.payload.response.JwtResponse ;
import com.innovastruct.innovastruct_backend.payload.response.MessageResponse ;
import com.innovastruct.innovastruct_backend.repository.RoleRepository ;
import com.innovastruct.innovastruct_backend.repository.UserRepository ;
import com.innovastruct.innovastruct_backend.security.jwt.JwtUtils ;
import com.innovastruct.innovastruct_backend.security.services.UserDetailsImpl ;
import com.innovastruct.innovastruct_backend.service.OtpService ;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Check if user is verified
        if (!userDetails.isVerified()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Account is not verified. Please verify your phone number."));
        }

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@Valid @RequestBody SignupRequest signUpRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email is already in use!"));
        }

        // Check if phone already exists
        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Phone number is already in use!"));
        }

        // Create new user's account
        User user = new User(
                signUpRequest.getName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getPhone());

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_CLIENT)
                .orElseThrow(() -> new RuntimeException("Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        user.setVerified(false);

        userRepository.save(user);

        // Generate and send OTP
        otpService.generateAndSendOtp(user.getPhone());

        return ResponseEntity.ok(new MessageResponse("Client registered successfully! Please verify your phone number."));
    }

    @PostMapping("/register/company")
    public ResponseEntity<?> registerCompany(@Valid @RequestBody CompanySignupRequest signUpRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email is already in use!"));
        }

        // Check if phone already exists
        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Phone number is already in use!"));
        }

        // Create new user's account
        User user = new User(
                signUpRequest.getName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getPhone());

        Set<Role> roles = new HashSet<>();
        Role companyRole = roleRepository.findByName(ERole.ROLE_COMPANY)
                .orElseThrow(() -> new RuntimeException("Role is not found."));
        roles.add(companyRole);
        user.setRoles(roles);
        user.setVerified(false);

        userRepository.save(user);

        // Generate and send OTP
        otpService.generateAndSendOtp(user.getPhone());

        return ResponseEntity.ok(new MessageResponse("Company registered successfully! Please verify your phone number and await admin approval."));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        boolean isValid = otpService.validateOtp(verifyOtpRequest.getPhone(), verifyOtpRequest.getOtp());

        if (isValid) {
            User user = userRepository.findByPhone(verifyOtpRequest.getPhone())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setVerified(true);
            userRepository.save(user);

            return ResponseEntity.ok(new MessageResponse("Phone number verified successfully!"));
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Invalid OTP"));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        // Check if user exists
        if (!userRepository.existsByPhone(verifyOtpRequest.getPhone())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User not found"));
        }

        // Generate and send new OTP
        otpService.generateAndSendOtp(verifyOtpRequest.getPhone());

        return ResponseEntity.ok(new MessageResponse("OTP sent successfully"));
    }
}
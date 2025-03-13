package com.innovastruct.innovastruct_backend.model;


import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "bids")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bid {
    @Id
    private String id;

    private String tenderId;

    private String companyId;

    private String companyName;

    private double bidAmount;

    private int proposedDuration;

    private String technicalProposalUrl;

    private String financialProposalUrl;

    private String status; // "pending", "accepted", "rejected"

    @CreatedDate
    private Date createdAt;
}
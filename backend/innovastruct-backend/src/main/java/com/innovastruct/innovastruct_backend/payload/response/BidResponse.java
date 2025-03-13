package com.innovastruct.innovastruct_backend.payload.response;


import java.util.Date;

import lombok.Data;

@Data
public class BidResponse {
    private String id;
    private String tenderId;
    private String companyId;
    private String companyName;
    private double bidAmount;
    private int proposedDuration;
    private String technicalProposalUrl;
    private String financialProposalUrl;
    private String status;
    private Date createdAt;
}
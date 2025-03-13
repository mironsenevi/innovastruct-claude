package com.innovastruct.innovastruct_backend.payload.response;


import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class TenderResponse {
    private String id;
    private String title;
    private String description;
    private String location;
    private double budget;
    private Date deadline;
    private String status;
    private String category;
    private String priority;
    private int bidsCount;
    private int daysLeft;
    private double lowestBid;
    private Date createdAt;
    private List<BidResponse> bids;
}
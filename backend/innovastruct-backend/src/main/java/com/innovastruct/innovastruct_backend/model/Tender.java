package com.innovastruct.innovastruct_backend.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "tenders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tender {
    @Id
    private String id;

    private String clientId;

    private String title;

    private String description;

    private String plan;

    private String boq;

    private double budget;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deadline;

    private String status; // "open", "closed", "awarded"

    private String category; // Commercial, Residential, etc.

    private String location;

    private String priority; // hot, high, medium

    private List<Bid> bids = new ArrayList<>();

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    // Helper method to calculate days left until deadline
    public int getDaysLeft() {
        if (deadline == null) {
            return 0;
        }

        long diffInMillies = deadline.getTime() - new Date().getTime();
        int daysLeft = (int) (diffInMillies / (1000 * 60 * 60 * 24));

        return Math.max(0, daysLeft);
    }
}
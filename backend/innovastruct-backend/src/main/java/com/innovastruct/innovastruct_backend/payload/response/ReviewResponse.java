package com.innovastruct.innovastruct_backend.payload.response;


import java.util.Date;

import lombok.Data;

@Data
public class ReviewResponse {
    private String id;
    private String clientName;
    private float rating;
    private String text;
    private String date;
}
package com.innovastruct.innovastruct_backend.payload.response;


import lombok.Data;

@Data
public class ProjectResponse {
    private int id;
    private String image;
    private String title;
    private String description;
    private int year;
}
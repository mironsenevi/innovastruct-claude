package com.innovastruct.innovastruct_backend.payload.dto;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ProjectDTO {
    private String name;

    private String description;

    private String completionYear;

    private List<String> imageUrls = new ArrayList<>();
}
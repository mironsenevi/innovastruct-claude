package com.innovastruct.innovastruct_backend.payload.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwardResponse {
    private String title;
    private int year;
    private String organization;
    private String reason;
}
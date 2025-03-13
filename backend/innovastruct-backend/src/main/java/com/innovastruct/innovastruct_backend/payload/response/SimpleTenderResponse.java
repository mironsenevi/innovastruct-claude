package com.innovastruct.innovastruct_backend.payload.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleTenderResponse {
    private String id;
    private String title;
    private double budget;
}

package com.innovastruct.innovastruct_backend.payload.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesResponse {
    private double lat;
    private double lng;
}
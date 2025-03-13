package com.innovastruct.innovastruct_backend.payload.response;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationTenderResponse {
    private String district;
    private CoordinatesResponse coordinates;
    private int tenderCount;
    private List<SimpleTenderResponse> activeTenders;
    private double totalValue;
}


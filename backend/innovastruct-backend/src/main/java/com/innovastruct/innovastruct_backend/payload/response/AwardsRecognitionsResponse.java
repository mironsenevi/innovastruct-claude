package com.innovastruct.innovastruct_backend.payload.response;


import java.util.List;

import lombok.Data;

@Data
public class AwardsRecognitionsResponse {
    private List<AwardResponse> majorAwards;
    private List<String> mediaFeatures;
    private List<String> clientRecognition;
}

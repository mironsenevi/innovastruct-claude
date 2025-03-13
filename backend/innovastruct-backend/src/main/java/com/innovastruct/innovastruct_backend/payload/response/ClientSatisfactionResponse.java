package com.innovastruct.innovastruct_backend.payload.response;

import lombok.Data;

import java.util.List;

public @Data
class ClientSatisfactionResponse {
    private float averageRating;
    private List<String> positiveFeedback;
    private List<String> challengesFaced;
}

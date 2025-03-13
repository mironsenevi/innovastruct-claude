package com.innovastruct.innovastruct_backend.payload.response;


import java.util.List;

import lombok.Data;

@Data
public class TrackRecordResponse {
    private int yearsOfExperience;
    private List<NotableProjectResponse> notableProjects;
    private ClientSatisfactionResponse clientSatisfaction;
}




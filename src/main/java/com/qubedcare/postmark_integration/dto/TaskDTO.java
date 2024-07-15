package com.qubedcare.postmark_integration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {
    private String type;
    private String emailAddress;
    private String details;
    // Add other necessary fields
}
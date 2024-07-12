package com.qubedcare.postmark_integration.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailEvent {
    private EmailEventType eventType;
    private Date timestamp;
    private String emailDetails;
}

enum EmailEventType {
    SENT, DELIVERED, OPENED, CLICKED, BOUNCED
}
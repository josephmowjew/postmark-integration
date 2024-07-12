package com.qubedcare.postmark_integration.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private String id;
    private String name;
    private String emailAddress;
}
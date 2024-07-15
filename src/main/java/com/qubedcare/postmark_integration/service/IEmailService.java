package com.qubedcare.postmark_integration.service;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.exception.EmailGenerationException;
import com.qubedcare.postmark_integration.exception.SendingFailureException;

public interface IEmailService {
    String generateEmailAddress(String clientName) throws EmailGenerationException;
    void sendWelcomeEmail(Client client) throws SendingFailureException;
}
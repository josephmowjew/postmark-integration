package com.qubedcare.postmark_integration.service;

import com.qubedcare.postmark_integration.model.Client;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Mock
    private ApiClient postmarkClient;

    @Mock
    private GuerrillaMailService guerrillaMailService;

    private EmailService emailService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService(postmarkClient, guerrillaMailService);
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
    }

    @Test
    public void testGenerateEmailAddress() throws Exception {
        when(guerrillaMailService.getEmailAddress()).thenReturn("test@guerrillamail.com");
        String email = emailService.generateEmailAddress("John Doe");
        verify(guerrillaMailService, times(1)).getEmailAddress();
        assert email.equals("test@guerrillamail.com");
    }

    @Test
    public void testSendWelcomeEmail() throws Exception {
        Client client = new Client("1", "John Doe", "john@example.com");
        
        emailService.sendWelcomeEmail(client);
        
        verify(postmarkClient).deliverMessage(argThat((Message message) -> {
            return "test@example.com".equals(message.getFrom()) &&
                   "john@example.com".equals(message.getTo()) &&
                   "Welcome to Our Service".equals(message.getSubject()) &&
                   message.getHtmlBody().contains("Dear John Doe") &&
                   message.getHtmlBody().contains("Welcome to our service!");
        }));
    }
}
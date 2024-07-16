package com.qubedcare.postmark_integration.service;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.exception.EmailGenerationException;
import com.qubedcare.postmark_integration.exception.SendingFailureException;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
class EmailServiceTest {

    @Mock
    private ApiClient postmarkClient;

    @Mock
    private GuerrillaMailService guerrillaMailService;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService(postmarkClient, guerrillaMailService);
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
        ReflectionTestUtils.setField(emailService, "welcomeEmailSubject", "Welcome to Our Service");
        ReflectionTestUtils.setField(emailService, "welcomeEmailTemplate", "Dear %s,\n\nWelcome to our service!");
        ReflectionTestUtils.setField(emailService, "emailDomain", "lyvepulse.com");
    }

    @Test
    void generateEmailAddress_Success() throws IOException, EmailGenerationException {
        when(guerrillaMailService.getEmailAddress()).thenReturn("random123@guerrillamail.com");
        String generatedEmail = emailService.generateEmailAddress("John Doe");
        assertEquals("random123@guerrillamail.com", generatedEmail);
        verify(guerrillaMailService, times(1)).getEmailAddress();
    }

    @Test
    void generateEmailAddress_Failure() throws IOException {
        when(guerrillaMailService.getEmailAddress()).thenThrow(new IOException("API Error"));
        assertThrows(EmailGenerationException.class, () -> emailService.generateEmailAddress("John Doe"));
    }

    @Test
    void sendWelcomeEmail_Success() throws Exception {
        Client client = new Client("1", "John Doe", "john@example.com");
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
       
        when(postmarkClient.deliverMessage(any(Message.class))).thenReturn(null);
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(client));
        verify(postmarkClient).deliverMessage(messageCaptor.capture());
        Message capturedMessage = messageCaptor.getValue();
        assertEquals("test@example.com", capturedMessage.getFrom());
        assertEquals("john@example.com", capturedMessage.getTo());
        assertEquals("Welcome to Our Service", capturedMessage.getSubject());
        assertTrue(capturedMessage.getHtmlBody().contains("Dear John Doe"));
    }

    @Test
    void sendWelcomeEmail_Failure() throws Exception {
        Client client = new Client("1", "John Doe", "john@example.com");
        when(postmarkClient.deliverMessage(any(Message.class))).thenThrow(new RuntimeException("Sending failed"));
        assertThrows(SendingFailureException.class, () -> emailService.sendWelcomeEmail(client));
    }
}
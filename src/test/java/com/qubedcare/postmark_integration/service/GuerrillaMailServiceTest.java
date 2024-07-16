package com.qubedcare.postmark_integration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
@ActiveProfiles("test")
class GuerrillaMailServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private GuerrillaMailService guerrillaMailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        guerrillaMailService = new GuerrillaMailService(restTemplate);
    }

    @Test
    void getEmailAddress_Success() throws IOException {
        String mockResponse = "{\"email_addr\":\"test123@guerrillamail.com\"}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String email = guerrillaMailService.getEmailAddress();

        assertEquals("test123@guerrillamail.com", email);
    }

    @Test
    void getEmailAddress_Failure() {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(new RuntimeException("API Error"));

        assertThrows(IOException.class, () -> guerrillaMailService.getEmailAddress());
    }
}
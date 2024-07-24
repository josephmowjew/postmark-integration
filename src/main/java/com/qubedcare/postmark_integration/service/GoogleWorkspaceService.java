package com.qubedcare.postmark_integration.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.directory.*;
import com.google.api.services.directory.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleWorkspaceService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleWorkspaceService.class);
    private static final String APPLICATION_NAME = "LyvePulse Email Microservice";

    @Value("${google.workspace.domain}")
    private String domain;

    @Value("${google.workspace.admin.email}")
    private String adminEmail;

    private final Directory service;

    public GoogleWorkspaceService(ResourceLoader resourceLoader, 
                                  @Value("${google.workspace.credentials.path}") String credentialsPath) 
                                  throws GeneralSecurityException, IOException {
        logger.info("Initializing GoogleWorkspaceService");
        Resource resource = resourceLoader.getResource(credentialsPath);
        GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream())
                .createScoped(Collections.singleton(DirectoryScopes.ADMIN_DIRECTORY_USER))
                .createDelegated(adminEmail);

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        service = new Directory.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
        logger.info("GoogleWorkspaceService initialized successfully");
    }

    public String createUserAccount(String firstName, String lastName) throws IOException {
        logger.info("Creating user account for {} {}", firstName, lastName);
        String tempPassword = generateTemporaryPassword();
        User user = new User()
                .setPrimaryEmail(generateEmail(firstName, lastName))
                .setName(new UserName()
                        .setGivenName(firstName)
                        .setFamilyName(lastName))
                .setPassword(tempPassword);

        User createdUser = service.users().insert(user).execute();
        logger.info("User account created successfully with email: {} and temporary password: {}", createdUser.getPrimaryEmail(), tempPassword);
        return createdUser.getPrimaryEmail();
    }

    private String generateEmail(String firstName, String lastName) {
        String baseEmail = (firstName.toLowerCase() + "." + lastName.toLowerCase()).replaceAll("[^a-z0-9.]", "");
        String email = baseEmail + "@" + domain;
        logger.debug("Generated email: {}", email);
        return email;
    }

    private String generateTemporaryPassword() {
        String tempPassword = "TempPass" + System.currentTimeMillis();
        logger.debug("Generated temporary password: {}", tempPassword);
        return tempPassword;
    }
}
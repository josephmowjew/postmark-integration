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

    @Value("${google.workspace.credentials.path}")
    private String credentialsPath;

    private final ResourceLoader resourceLoader;
    private Directory service;

    public GoogleWorkspaceService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        logger.debug("GoogleWorkspaceService constructor called");
    }

    @jakarta.annotation.PostConstruct
    public void init() throws GeneralSecurityException, IOException {
        logger.debug("Initializing GoogleWorkspaceService");
        logger.debug("Domain: {}", domain);
        logger.debug("Admin Email: {}", adminEmail);
        logger.debug("Credentials Path: {}", credentialsPath);

        try {
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
        } catch (Exception e) {
            logger.error("Error initializing GoogleWorkspaceService", e);
            throw e;
        }
    }

    public String createUserAccount(String firstName, String lastName) throws IOException {
        logger.info("Creating user account for {} {}", firstName, lastName);
        String email = generateEmail(firstName, lastName);

        // Check if user already exists
        if (userExists(email)) {
            logger.info("User with email {} already exists. Skipping creation.", email);
            return email;
        }

        String tempPassword = generateTemporaryPassword();
        User user = new User()
                .setPrimaryEmail(email)
                .setName(new UserName()
                        .setGivenName(firstName)
                        .setFamilyName(lastName))
                .setPassword(tempPassword);
        
        User createdUser = service.users().insert(user).execute();
        logger.info("User account created successfully with email: {} and temporary password: {}", createdUser.getPrimaryEmail(), tempPassword);
        return createdUser.getPrimaryEmail();
    }

    private boolean userExists(String email) throws IOException {
        Users result = service.users().list().setDomain(domain).setQuery("email:" + email).execute();
        return result.getUsers() != null && !result.getUsers().isEmpty();
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
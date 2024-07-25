package com.qubedcare.postmark_integration.config;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpResponseInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpLoggingInterceptor implements HttpExecuteInterceptor, HttpResponseInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(HttpLoggingInterceptor.class);

    @Override
    public void intercept(HttpRequest request) throws IOException {
        logger.debug("Request URL: {}", request.getUrl());
        logger.debug("Request method: {}", request.getRequestMethod());
        logger.debug("Request headers: {}", request.getHeaders());
        if (request.getContent() != null) {
            logger.debug("Request content: {}", request.getContent().toString());
        }
    }

    @Override
    public void interceptResponse(HttpResponse response) throws IOException {
        logger.debug("Response status code: {}", response.getStatusCode());
        logger.debug("Response headers: {}", response.getHeaders());
        if (response.getContent() != null) {
            String responseContent = new String(response.getContent().readAllBytes());
            logger.debug("Response content: {}", responseContent);
        }
    }
}
package com.qubedcare.postmark_integration.exception;

public class EmailGenerationException extends Exception {
    public EmailGenerationException(String message) {
        super(message);
    }

    public EmailGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
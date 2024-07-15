package com.qubedcare.postmark_integration.exception;

public class SendingFailureException extends Exception {
    public SendingFailureException(String message) {
        super(message);
    }

    public SendingFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
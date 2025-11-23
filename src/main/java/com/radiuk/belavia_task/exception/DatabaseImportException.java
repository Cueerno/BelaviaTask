package com.radiuk.belavia_task.exception;

public class DatabaseImportException extends RuntimeException {
    public DatabaseImportException(String message) {
        super(message);
    }

    public DatabaseImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
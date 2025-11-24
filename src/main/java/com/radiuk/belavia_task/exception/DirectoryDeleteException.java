package com.radiuk.belavia_task.exception;

public class DirectoryDeleteException extends RuntimeException {
    public DirectoryDeleteException(String message) {
        super(message);
    }

    public DirectoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}

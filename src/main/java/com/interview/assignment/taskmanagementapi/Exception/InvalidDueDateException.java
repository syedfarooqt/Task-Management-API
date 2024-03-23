package com.interview.assignment.taskmanagementapi.Exception;

public class InvalidDueDateException extends RuntimeException {
    public InvalidDueDateException(String message) {
        super(message);
    }
}

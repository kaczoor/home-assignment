package com.task.fda.client;


public class FdaException extends RuntimeException {

    FdaException(String message) {
        super(message);
    }

    public static FdaException generalException(int responseStatus, String rawBody) {
        return new FdaException("Received status=" + responseStatus + ". responseBody=" + rawBody);
    }
}

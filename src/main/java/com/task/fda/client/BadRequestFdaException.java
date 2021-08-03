package com.task.fda.client;

public class BadRequestFdaException extends FdaException{

    BadRequestFdaException(String message) {
        super(message);
    }


    public static BadRequestFdaException ofRawBody(String rawBody){
        return new BadRequestFdaException("Wrong request parameters, responseBody=" + rawBody);
    }
}

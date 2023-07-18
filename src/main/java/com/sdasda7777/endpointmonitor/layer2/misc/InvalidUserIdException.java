package com.sdasda7777.endpointmonitor.layer2.misc;

/**
 * This exception is used to signal user with given Id does not exist
 */

public class InvalidUserIdException extends RuntimeException{
    public InvalidUserIdException(String message){
        super(message);
    }
}

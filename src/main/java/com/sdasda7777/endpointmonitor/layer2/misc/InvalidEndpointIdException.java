package com.sdasda7777.endpointmonitor.layer2.misc;

/**
 * This exception is used to signal endpoint with given Id does not exist
 */

public class InvalidEndpointIdException extends RuntimeException{
    public InvalidEndpointIdException(String message){
        super(message);
    }
}

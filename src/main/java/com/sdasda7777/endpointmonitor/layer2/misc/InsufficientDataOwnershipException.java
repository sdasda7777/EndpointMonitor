package com.sdasda7777.endpointmonitor.layer2.misc;

/**
 * This exception is used to signal user does not have privileges for operation
 */

public class InsufficientDataOwnershipException extends RuntimeException{
    public InsufficientDataOwnershipException(String message){
        super(message);
    }
}

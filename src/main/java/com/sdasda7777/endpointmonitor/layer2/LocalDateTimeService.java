package com.sdasda7777.endpointmonitor.layer2;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * This service abstracts creation of HTTP request.
 * That allows for better testability of components
 * that need to use the current date.
 */

@Service
public class LocalDateTimeService {
    public LocalDateTimeService(){}

    /**
     * Return current LocalDateTime
     * @return LocalDateTime.now()
     */
    public LocalDateTime now(){
        return LocalDateTime.now();
    }
}

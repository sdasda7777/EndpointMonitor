package com.sdasda7777.endpointmonitor.layer2;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LocalDateTimeService {
    public LocalDateTimeService(){}

    public LocalDateTime now(){
        return LocalDateTime.now();
    }
}

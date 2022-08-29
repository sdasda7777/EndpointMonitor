package com.sdasda7777.endpointmonitor.L02;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoringResultDTO;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoringResult;
import com.sdasda7777.endpointmonitor.L03.MonitoringResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class MonitoringResultService {
    @Autowired
    MonitoringResultRepository monitoringResultRepository;


    public Collection<MonitoringResult> getAllForEndpointLimited(
            Long monitoredEndpointId, Long limitResults) {
        return monitoringResultRepository.getAllForEndpoint(monitoredEndpointId)
                .stream().limit(limitResults).collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<MonitoringResult> getAllForEndpoint(Long monitoredEndpointId) {
        return monitoringResultRepository.getAllForEndpoint(monitoredEndpointId);
    }
}

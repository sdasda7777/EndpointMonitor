package com.sdasda7777.endpointmonitor.L02;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoringResult;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InsufficientDataOwnershipException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidEndpointIdException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidUserIdException;
import com.sdasda7777.endpointmonitor.L03.MonitoringResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MonitoringResultService {
    @Autowired
    MonitoringResultRepository monitoringResultRepository;

    @Autowired
    MonitoredEndpointService monitoredEndpointService;

    @Autowired
    MonitorUserService monitorUserService;

    public MonitoringResultService(MonitoringResultRepository monitoringResultRepository,
                                   MonitoredEndpointService monitoredEndpointService,
                                   MonitorUserService monitorUserService){
        this.monitoringResultRepository = monitoringResultRepository;
        this.monitoredEndpointService = monitoredEndpointService;
        this.monitorUserService = monitorUserService;
    }


    public Collection<MonitoringResult> getAllForEndpoint(String keycloakId,
                                                          Long monitoredEndpointId, Long limitResults) {
        Optional<MonitoredEndpoint> endpoint = monitoredEndpointService.getEndpointById(monitoredEndpointId);

        if(endpoint.isEmpty())
            throw new InvalidEndpointIdException(monitoredEndpointId.toString());

        Optional<MonitorUser> userOptional = monitorUserService.getUserByKeycloakId(keycloakId);

        if(userOptional.isEmpty())
            throw new InvalidUserIdException(keycloakId);

        if(userOptional.get().getId() != endpoint.get().getOwner().getId())
            throw new InsufficientDataOwnershipException("");

        if(limitResults != null){
            return monitoringResultRepository.getAllForEndpoint(monitoredEndpointId)
                    .stream().limit(limitResults).collect(Collectors.toCollection(ArrayList::new));
        }else {
            return monitoringResultRepository.getAllForEndpoint(monitoredEndpointId);
        }
    }

    public MonitoringResult createMonitoringResult(MonitoringResult monitoringResult){
        return monitoringResultRepository.save(monitoringResult);
    }
}

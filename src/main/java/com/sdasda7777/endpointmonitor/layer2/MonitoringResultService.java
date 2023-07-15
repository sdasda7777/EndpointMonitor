package com.sdasda7777.endpointmonitor.layer2;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitorUser;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult;
import com.sdasda7777.endpointmonitor.layer2.misc.InsufficientDataOwnershipException;
import com.sdasda7777.endpointmonitor.layer2.misc.InvalidEndpointIdException;
import com.sdasda7777.endpointmonitor.layer2.misc.InvalidUserIdException;
import com.sdasda7777.endpointmonitor.L03.MonitoringResultRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MonitoringResultService {
    final MonitoringResultRepository monitoringResultRepository;

    final MonitoredEndpointService monitoredEndpointService;

    final MonitorUserService monitorUserService;

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

        if(!userOptional.get().getId().equals(endpoint.get().getOwner().getId()))
            throw new InsufficientDataOwnershipException("");

        if(limitResults != null){
            return monitoringResultRepository.getAllForEndpoint(monitoredEndpointId)
                    .stream().limit(limitResults).collect(Collectors.toCollection(ArrayList::new));
        }else {
            return monitoringResultRepository.getAllForEndpoint(monitoredEndpointId);
        }
    }

    public void createMonitoringResult(MonitoringResult monitoringResult){
        monitoringResultRepository.save(monitoringResult);
    }
}

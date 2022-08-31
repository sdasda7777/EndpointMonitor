package com.sdasda7777.endpointmonitor.L02;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InsufficientDataOwnershipException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidEndpointIdException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidUserIdException;
import com.sdasda7777.endpointmonitor.L03.MonitoredEndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
public class MonitoredEndpointService {
    @Autowired
    MonitoredEndpointRepository monitoredEndpointRepository;

    @Autowired
    MonitorUserService monitorUserService;

    public MonitoredEndpointService(MonitoredEndpointRepository monitoredEndpointRepository,
                                    MonitorUserService monitorUserService){
        this.monitoredEndpointRepository = monitoredEndpointRepository;
        this.monitorUserService = monitorUserService;
    }

    public Collection<MonitoredEndpoint> getRequiringUpdate() {
        return monitoredEndpointRepository.getRequiringUpdate();
    }

    public Optional<MonitoredEndpoint> getEndpointById(Long monitoredEndpointId) {
        return monitoredEndpointRepository.findById(monitoredEndpointId);
    }

    public Collection<MonitoredEndpoint> getMonitoredEndpointsByKeycloakId(String keycloakId) {
        return monitoredEndpointRepository.getEndpointsByKeycloakId(keycloakId);
    }

    public MonitoredEndpoint createMonitoredEndpoint(String keycloakId,
                                                     MonitoredEndpoint monitoredEndpoint){
        MonitorUser monitorUser = getOrCreateUser(keycloakId);

        monitoredEndpoint.setOwner(monitorUser);
        monitoredEndpoint.setCreationDate(LocalDateTime.now());
        monitoredEndpoint.setLastCheckDate(LocalDateTime.MIN);

        return monitoredEndpointRepository.save(monitoredEndpoint);
    }

    public MonitoredEndpoint updateMonitoredEndpoint(String keycloakId, Long monitoredEndpointId, MonitoredEndpoint newEndpoint){
        Optional<MonitoredEndpoint> currentOptional = monitoredEndpointRepository.findById(monitoredEndpointId);
        if(currentOptional.isEmpty())
            throw new InvalidEndpointIdException(monitoredEndpointId.toString());

        Optional<MonitorUser> monitorUser = monitorUserService.getUserByKeycloakId(keycloakId);
        if(monitorUser.isEmpty())
            throw new InvalidUserIdException(keycloakId);

        if(monitorUser.get().getId() != currentOptional.get().getOwner().getId())
            throw new InsufficientDataOwnershipException("");

        MonitoredEndpoint currentEndpoint = currentOptional.get();
        if(newEndpoint.getName() != null)
            currentEndpoint.setName(newEndpoint.getName());
        if(newEndpoint.getUrl() != null)
            currentEndpoint.setUrl(newEndpoint.getUrl());
        if(newEndpoint.getMonitoringInterval() != null)
            currentEndpoint.setMonitoringInterval(newEndpoint.getMonitoringInterval());

        return monitoredEndpointRepository.save(currentEndpoint);
    }

    public void updateEndpointLastCheck(MonitoredEndpoint endpoint, LocalDateTime checkDate) {
        monitoredEndpointRepository.updateEndpointLastCheck(endpoint, checkDate);
    }

    public MonitoredEndpoint deleteEndpoint(String keycloakId, Long monitoredEndpointId) {
        Optional<MonitoredEndpoint> monitoredEndpoint = getEndpointById(monitoredEndpointId);

        if(monitoredEndpoint.isEmpty())
            throw new InvalidEndpointIdException(monitoredEndpointId.toString());

        Optional<MonitorUser> monitorUser = monitorUserService.getUserByKeycloakId(keycloakId);
        if(monitorUser.isEmpty())
            throw new InvalidUserIdException(keycloakId);

        if(monitorUser.get().getId() != monitoredEndpoint.get().getOwner().getId())
            throw new InsufficientDataOwnershipException("");

        monitoredEndpointRepository.deleteById(monitoredEndpointId);
        return monitoredEndpoint.get();
    }


    private MonitorUser getOrCreateUser(String keycloakId) {
        Optional<MonitorUser> monitorUser = monitorUserService.getUserByKeycloakId(keycloakId);
        if(monitorUser.isEmpty()){
            MonitorUser newMonitorUser = new MonitorUser();
            newMonitorUser.setKeycloakId(keycloakId);
            return monitorUserService.createUser(newMonitorUser);
        }
        return monitorUser.get();
    }
}

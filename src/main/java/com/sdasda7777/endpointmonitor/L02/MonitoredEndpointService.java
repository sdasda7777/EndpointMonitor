package com.sdasda7777.endpointmonitor.L02;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L03.MonitoredEndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endpoint with given Id does not exist");

        Optional<MonitorUser> monitorUser = monitorUserService.getUserByKeycloakId(keycloakId);
        if(monitorUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given Id does not exist");

        if(monitorUser.get().getId() != currentOptional.get().getOwner().getId())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not own specified endpoint");

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

        //TODO move ResponseStatusException to respective Controller
        if(monitoredEndpoint.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endpoint with given Id does not exist");

        Optional<MonitorUser> monitorUser = monitorUserService.getUserByKeycloakId(keycloakId);

        if(monitoredEndpoint.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given Id does not exist");

        if(monitorUser.get().getId() != monitoredEndpoint.get().getOwner().getId())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not own specified endpoint");

        monitoredEndpointRepository.deleteById(monitoredEndpointId);
        return monitoredEndpoint.get();
    }




    private MonitorUser getOrCreateUser(String keycloakId) {
        Optional<MonitorUser> userOptional = monitorUserService.getUserByKeycloakId(keycloakId);
        if(userOptional.isEmpty()){
            MonitorUser monitorUser = new MonitorUser();
            monitorUser.setKeycloakId(keycloakId);
            return monitorUserService.createUser(monitorUser);
        }
        return userOptional.get();
    }
}

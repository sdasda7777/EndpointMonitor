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


    public MonitoredEndpoint createMonitoredEndpoint(String token, MonitoredEndpoint monitoredEndpoint){
        MonitorUser monitorUser = checkUserIsAuthorized(token);

        monitoredEndpoint.setOwner(monitorUser);
        monitoredEndpoint.setCreationDate(LocalDateTime.now());
        monitoredEndpoint.setLastCheckDate(LocalDateTime.MIN);

        return monitoredEndpointRepository.save(monitoredEndpoint);
    }

    public MonitoredEndpoint updateMonitoredEndpoint(String token, Long monitoredEndpointId, MonitoredEndpoint newEndpoint){
        MonitorUser monitorUser = checkUserIsAuthorized(token);

        Optional<MonitoredEndpoint> currentOptional = monitoredEndpointRepository.findById(monitoredEndpointId);
        if(currentOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endpoint with given Id does not exist");
        if(monitorUser.getId() != currentOptional.get().getOwner().getId())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not own specified endpoint");

        MonitoredEndpoint currentEndpoint = currentOptional.get();
        if(newEndpoint.getName() != null)
            currentEndpoint.setName(newEndpoint.getName());
        if(newEndpoint.getUrl() != null)
            currentEndpoint.setUrl(newEndpoint.getUrl());
        if(newEndpoint.getMonitoringInterval() != null)
            currentEndpoint.setMonitoringInterval(newEndpoint.getMonitoringInterval());
        if(newEndpoint.getOwner() != null){
            if(newEndpoint.getOwner().getId() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Id must be specified");

            Optional<MonitorUser> newUser = monitorUserService.getUserById(newEndpoint.getOwner().getId());

            if(newUser.isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given Id does not exist");

            currentEndpoint.setOwner(newEndpoint.getOwner());
        }

        return monitoredEndpointRepository.save(currentEndpoint);
    }

    public Collection<MonitoredEndpoint> getRequiringUpdate() {
        return monitoredEndpointRepository.getRequiringUpdate(LocalDateTime.now());
    }

    public Optional<MonitoredEndpoint> getEndpointById(Long monitoredEndpointId) {
        return monitoredEndpointRepository.findById(monitoredEndpointId);
    }

    public Collection<MonitoredEndpoint> getMonitoredEndpointsByToken(String token) {
        MonitorUser monitorUser = checkUserIsAuthorized(token);
        return monitoredEndpointRepository.getEndpointsByUser(monitorUser);
    }



    public MonitoredEndpoint deleteEndpoint(String token, Long monitoredEndpointId) {
        MonitorUser monitorUser = checkUserIsAuthorized(token);

        Optional<MonitoredEndpoint> monitoredEndpoint = getEndpointById(monitoredEndpointId);

        if(monitoredEndpoint.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endpoint with given Id does not exist");
        if(monitorUser.getId() != monitoredEndpoint.get().getOwner().getId())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not own specified endpoint");

        monitoredEndpointRepository.deleteById(monitoredEndpointId);
        return monitoredEndpoint.get();
    }




    private MonitorUser checkUserIsAuthorized(String token) {
        Optional<MonitorUser> userOptional = monitorUserService.getUniqueUserByToken(token);
        if(userOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token did not match expected number of users");
        return userOptional.get();
    }
}

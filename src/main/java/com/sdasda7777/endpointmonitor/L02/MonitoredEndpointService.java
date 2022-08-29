package com.sdasda7777.endpointmonitor.L02;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L03.MonitoredEndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class MonitoredEndpointService {
    @Autowired
    MonitoredEndpointRepository monitoredEndpointRepository;

    public Collection<MonitoredEndpoint> getAll(){
        return monitoredEndpointRepository.findAll();
    }


    public MonitoredEndpoint createMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint){
        return monitoredEndpointRepository.save(monitoredEndpoint);
    }

    public MonitoredEndpoint updateMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint){
        Optional<MonitoredEndpoint> currentOptional = monitoredEndpointRepository.findById(monitoredEndpoint.getId());
        if(currentOptional.isEmpty())
            throw new IllegalStateException();

        MonitoredEndpoint current = currentOptional.get();
        if(monitoredEndpoint.getName() != null)
            current.setName(monitoredEndpoint.getName());
        if(monitoredEndpoint.getUrl() != null)
            current.setUrl(monitoredEndpoint.getUrl());
        if(monitoredEndpoint.getCreationDate() != null)
            current.setCreationDate(monitoredEndpoint.getCreationDate());
        if(monitoredEndpoint.getLastCheckDate() != null)
            current.setLastCheckDate(monitoredEndpoint.getLastCheckDate());
        if(monitoredEndpoint.getMonitoringInterval() != null)
            current.setMonitoringInterval(monitoredEndpoint.getMonitoringInterval());
        if(monitoredEndpoint.getOwner() != null)
            current.setOwner(monitoredEndpoint.getOwner());

        return monitoredEndpointRepository.save(current);
    }
}

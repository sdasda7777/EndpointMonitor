package com.sdasda7777.endpointmonitor.L01.DTO;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class MonitoredEndpointDTO {
    private Long id;
    private String name;
    private String url;
    private LocalDateTime creationDate;
    private LocalDateTime lastCheckDate;
    private Integer monitoringInterval;
    private Long ownerId;

    public MonitoredEndpointDTO(Long id, String name, String url,
                                LocalDateTime creationDate,
                                LocalDateTime lastCheckDate,
                                Integer monitoringInterval,
                                Long ownerId) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.creationDate = creationDate;
        this.lastCheckDate = lastCheckDate;
        this.monitoringInterval = monitoringInterval;
        this.ownerId = ownerId;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getLastCheckDate() {
        return lastCheckDate;
    }

    public Integer getMonitoringInterval() {
        return monitoringInterval;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    /*
    public static MonitoredEndpointDTO convertOne(MonitoredEndpoint endpoint){
        return new MonitoredEndpointDTO(
            endpoint.getId(),
            endpoint.getName(),
            endpoint.getUrl(),
            endpoint.getCreationDate(),
            endpoint.getlastChechDate(),
            endpoint.getMonitoringInterval(),
            endpoint.getOwner().getId()
        );
    }

    public static Collection<MonitoredEndpointDTO> convertMany(Collection<MonitoredEndpoint> endpoints){
        Collection<MonitoredEndpointDTO> dtos = new ArrayList<>();
        endpoints.forEach(i -> dtos.add(MonitoredEndpointDTO.convertOne(i)));
        return dtos;
    }
    */
}

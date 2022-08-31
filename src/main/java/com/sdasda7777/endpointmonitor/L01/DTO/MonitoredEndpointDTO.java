package com.sdasda7777.endpointmonitor.L01.DTO;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MonitoredEndpointDTO {
    private Long id;
    private String name;
    private String url;
    private LocalDateTime creationDate;
    private LocalDateTime lastCheckDate;
    private Integer monitoringInterval;
    private Long ownerId;

    public MonitoredEndpointDTO(MonitoredEndpoint endpoint) {
        this.id = endpoint.getId();
        this.name = endpoint.getName();
        this.url = endpoint.getUrl();
        this.creationDate = endpoint.getCreationDate();
        this.lastCheckDate = endpoint.getLastCheckDate();
        this.monitoringInterval = endpoint.getMonitoringInterval();
        this.ownerId = endpoint.getOwner().getId();
    }

    public MonitoredEndpointDTO(Long id, String name, String url,
                                LocalDateTime creationDate, LocalDateTime lastCheckDate,
                                Integer monitoringInterval, Long ownerId
                                ) {
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitoredEndpointDTO that = (MonitoredEndpointDTO) o;
        return id.equals(that.id) && name.equals(that.name) && url.equals(that.url) && creationDate.equals(that.creationDate) && lastCheckDate.equals(that.lastCheckDate) && monitoringInterval.equals(that.monitoringInterval) && ownerId.equals(that.ownerId);
    }


    public static MonitoredEndpointDTO convertOne(MonitoredEndpoint endpoint){
        return new MonitoredEndpointDTO(endpoint);
    }

    public static Collection<MonitoredEndpointDTO> convertMany(Collection<MonitoredEndpoint> endpoints){
        Collection<MonitoredEndpointDTO> dtos = new ArrayList<>();
        endpoints.forEach(i -> dtos.add(MonitoredEndpointDTO.convertOne(i)));
        return dtos;
    }
}

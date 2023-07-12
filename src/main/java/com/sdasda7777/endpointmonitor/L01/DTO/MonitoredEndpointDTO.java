package com.sdasda7777.endpointmonitor.L01.DTO;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a <a href="https://en.wikipedia.org/wiki/Data_transfer_object">DTO</a> of the {@link com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint} entity
 */

public class MonitoredEndpointDTO {

    /** ID number of the endpoint */
    private final Long id;

    /** Name of the endpoint given by the user */
    private final String name;

    /** URL of the endpoint */
    private final String url;

    /** Creation date of the entity */
    private final LocalDateTime creationDate;

    /** Date of the last check */
    private final LocalDateTime lastCheckDate;

    /** Interval of checks, in seconds */
    private final Integer monitoringInterval;

    /** ID of the owner of the endpoint entity */
    private final Long ownerId;

    /**
     * Constructor for MonitoredEndpointDTO which automatically takes all relevant data from the passed MonitoredEndpoint entity
     * @param endpoint entity to take relevant data from
     */
    public MonitoredEndpointDTO(MonitoredEndpoint endpoint) {
        this.id = endpoint.getId();
        this.name = endpoint.getName();
        this.url = endpoint.getUrl();
        this.creationDate = endpoint.getCreationDate();
        this.lastCheckDate = endpoint.getLastCheckDate();
        this.monitoringInterval = endpoint.getMonitoringInterval();
        this.ownerId = endpoint.getOwner().getId();
    }

    /**
     * Constructor for MonitoredEndpointDTO which takes relevant data as arguments
     * @param id id of the endpoint entity
     * @param name name given to the endpoint entity by the user
     * @param url url of the endpoint
     * @param creationDate date of creation of the endpoint entity
     * @param lastCheckDate date of the last check
     * @param monitoringInterval interval of monitoring, in seconds
     * @param ownerId id of the owner of the endpoint entity
     */
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

    /**
     * ID getter
     * @return ID of the endpoint entity
     */
    public Long getId() {
        return id;
    }

    /**
     * Name getter
     * @return name of the entity given by the user
     */
    public String getName() {
        return name;
    }

    /**
     * URL getter
     * @return url of the endpoint
     */
    public String getUrl() {
        return url;
    }

    /**
     * Creation date getter
     * @return creation date of the endpoint entity
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Last check date getter
     * @return last check date
     */
    public LocalDateTime getLastCheckDate() {
        return lastCheckDate;
    }

    /**
     * Monitoring interval getter
     * @return interval of monitoring, in seconds
     */
    public Integer getMonitoringInterval() {
        return monitoringInterval;
    }

    /**
     * Owner ID getter
     * @return ID of the owner of the endpoint entity
     */
    public Long getOwnerId() {
        return ownerId;
    }


    /**
     * equals - determines whether two objects are equivalent
     * @param o compared object
     * @return true iff values match for every field
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitoredEndpointDTO that = (MonitoredEndpointDTO) o;
        return id.equals(that.id)
                && name.equals(that.name)
                && url.equals(that.url)
                && creationDate.equals(that.creationDate)
                && lastCheckDate.equals(that.lastCheckDate)
                && monitoringInterval.equals(that.monitoringInterval)
                && ownerId.equals(that.ownerId);
    }

    /**
     * Converts one MonitoredEndpoint to one MonitoredEndpointDTO
     * @param endpoint source endpoint entity
     * @return MonitoredEndpointDTO describing provided endpoint entity
     */
    public static MonitoredEndpointDTO convertOne(MonitoredEndpoint endpoint){
        return new MonitoredEndpointDTO(endpoint);
    }

    /**
     * Converts collection of MonitoredEndpoints to collection of MonitoredEndpointDTOs
     * @param endpoints collection of endpoint entities
     * @return collection of MonitoredEndpointDTOs describing provided endpoint entities
     */
    public static Collection<MonitoredEndpointDTO> convertMany(Collection<MonitoredEndpoint> endpoints){
        Collection<MonitoredEndpointDTO> dtos = new ArrayList<>();
        endpoints.forEach(ii -> dtos.add(MonitoredEndpointDTO.convertOne(ii)));
        return dtos;
    }
}

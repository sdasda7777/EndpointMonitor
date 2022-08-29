package com.sdasda7777.endpointmonitor.L02.Entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monitoredEndpoints")
public class MonitoredEndpoint {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String url;
    private LocalDateTime creationDate;
    private LocalDateTime lastCheckDate;
    private Integer monitoringInterval;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private MonitorUser owner;

    public MonitoredEndpoint(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastCheckDate() {
        return lastCheckDate;
    }

    public void setLastCheckDate(LocalDateTime lastCheckDate) {
        this.lastCheckDate = lastCheckDate;
    }

    public Integer getMonitoringInterval() {
        return monitoringInterval;
    }

    public void setMonitoringInterval(Integer monitoringInterval) {
        this.monitoringInterval = monitoringInterval;
    }

    public MonitorUser getOwner() {
        return owner;
    }

    public void setOwner(MonitorUser owner) {
        this.owner = owner;
    }
}

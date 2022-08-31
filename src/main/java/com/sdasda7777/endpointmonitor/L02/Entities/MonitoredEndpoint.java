package com.sdasda7777.endpointmonitor.L02.Entities;

import org.apache.commons.validator.routines.UrlValidator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "monitoredEndpoints")
public class MonitoredEndpoint {

    @Id
    @GeneratedValue
    private Long id;

    private String name;


    @Column(columnDefinition="TEXT")
    private String url;
    private LocalDateTime creationDate;
    private LocalDateTime lastCheckDate;
    private LocalDateTime nextCheckDate;
    private Integer monitoringInterval;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private MonitorUser owner;

    @OneToMany(mappedBy = "monitoredEndpoint", cascade = CascadeType.ALL)
    public Collection<MonitoringResult> monitoringResults;


    public MonitoredEndpoint(){
        this.name = null;
        this.url = null;
        this.creationDate = null;
        this.lastCheckDate = null;
        this.nextCheckDate = null;
        this.monitoringInterval = null;
        this.owner = null;
        this.monitoringResults = new ArrayList<>();
    }

    public MonitoredEndpoint(String name, String url,
                             LocalDateTime creationDate, LocalDateTime lastCheckDate,
                             Integer monitoringInterval){
        this.name = name;
        this.url = url;
        this.creationDate = creationDate;
        this.lastCheckDate = lastCheckDate;
        setMonitoringInterval(monitoringInterval);

        this.owner = null;
        this.monitoringResults = new ArrayList<>();
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
        if(monitoringInterval != null){
            nextCheckDate = lastCheckDate.plusSeconds(monitoringInterval);
        }
    }

    public LocalDateTime getNextCheckDate() {
        return nextCheckDate;
    }

    public Integer getMonitoringInterval() {
        return monitoringInterval;
    }

    public void setMonitoringInterval(Integer monitoringInterval) {
        this.monitoringInterval = monitoringInterval;
        if(lastCheckDate != null){
            nextCheckDate = lastCheckDate.plusSeconds(monitoringInterval);
        }
    }

    public MonitorUser getOwner() {
        return owner;
    }

    public void setOwner(MonitorUser owner) {
        this.owner = owner;
    }

    public Collection<MonitoringResult> getMonitoringResults() {
        return monitoringResults;
    }

    public void setMonitoringResults(Collection<MonitoringResult> monitoringResults) {
        this.monitoringResults = monitoringResults;
    }

    public boolean validOrValidatableUrl() {
        UrlValidator validator = new UrlValidator();
        if (validator.isValid(url))
            return true;
        url = "https://" + url;
        return validator.isValid(url);
    }
}

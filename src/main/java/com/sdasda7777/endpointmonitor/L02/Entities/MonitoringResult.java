package com.sdasda7777.endpointmonitor.L02.Entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monitoringResults")
public class MonitoringResult {
    @Id
    @GeneratedValue
    Long id;

    LocalDateTime checkDate;
    Integer resultStatusCode;
    @Column(columnDefinition="TEXT")
    String resultPayload;

    @ManyToOne(optional = false)
    @JoinColumn(name = "monitoredEndpoint_id", nullable = false)
    MonitoredEndpoint monitoredEndpoint;


    public MonitoringResult() {
        this.checkDate = null;
        this.resultStatusCode = null;
        this.resultPayload = null;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDateTime checkDate) {
        this.checkDate = checkDate;
    }

    public Integer getResultStatusCode() {
        return resultStatusCode;
    }

    public void setResultStatusCode(Integer resultStatusCode) {
        this.resultStatusCode = resultStatusCode;
    }

    public String getResultPayload() {
        return resultPayload;
    }

    public void setResultPayload(String resultPayload) {
        this.resultPayload = resultPayload;
    }

    public MonitoredEndpoint getMonitoredEndpoint() {
        return monitoredEndpoint;
    }

    public void setMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint) {
        this.monitoredEndpoint = monitoredEndpoint;
    }
}

package com.sdasda7777.endpointmonitor.L01.DTO;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoringResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class MonitoringResultDTO {
    Long id;

    LocalDateTime checkDate;
    Integer resultStatusCode;
    String resultPayload;

    Long monitoredEndpointId;

    public MonitoringResultDTO(MonitoringResult monitoringResult){
        this.id = monitoringResult.getId();
        this.checkDate = monitoringResult.getCheckDate();
        this.resultStatusCode = monitoringResult.getResultStatusCode();
        this.resultPayload = monitoringResult.getResultPayload();
        this.monitoredEndpointId = monitoringResult.getMonitoredEndpoint().getId();
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

    public Long getMonitoredEndpointId() {
        return monitoredEndpointId;
    }

    public void setMonitoredEndpointId(Long monitoredEndpointId) {
        this.monitoredEndpointId = monitoredEndpointId;
    }


    public static MonitoringResultDTO convertOne(MonitoringResult endpoint){
        return new MonitoringResultDTO(endpoint);
    }

    public static Collection<MonitoringResultDTO> convertMany(Collection<MonitoringResult> endpoints){
        Collection<MonitoringResultDTO> dtos = new ArrayList<>();
        endpoints.forEach(i -> dtos.add(MonitoringResultDTO.convertOne(i)));
        return dtos;
    }
}

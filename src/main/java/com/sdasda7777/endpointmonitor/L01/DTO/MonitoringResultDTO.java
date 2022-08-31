package com.sdasda7777.endpointmonitor.L01.DTO;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoringResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    public MonitoringResultDTO(Long id, LocalDateTime checkDate,
                               Integer resultStatusCode, String resultPayload,
                               Long monitoredEndpointId){
        this.id = id;
        this.checkDate = checkDate;
        this.resultStatusCode = resultStatusCode;
        this.resultPayload = resultPayload;
        this.monitoredEndpointId = monitoredEndpointId;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCheckDate() {
        return checkDate;
    }

    public Integer getResultStatusCode() {
        return resultStatusCode;
    }

    public String getResultPayload() {
        return resultPayload;
    }

    public Long getMonitoredEndpointId() {
        return monitoredEndpointId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitoringResultDTO that = (MonitoringResultDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(checkDate, that.checkDate) && Objects.equals(resultStatusCode, that.resultStatusCode) && Objects.equals(resultPayload, that.resultPayload) && Objects.equals(monitoredEndpointId, that.monitoredEndpointId);
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

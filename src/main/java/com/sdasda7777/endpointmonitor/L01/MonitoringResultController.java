package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoringResultDTO;
import com.sdasda7777.endpointmonitor.L02.MonitoringResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/monitoringResults/")
public class MonitoringResultController {
    @Autowired
    MonitoringResultService monitoringResultService;

    @GetMapping("{monitoredEndpointId}")
    public Collection<MonitoringResultDTO> getMonitoringResults(
            @PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId,
            @RequestParam(name = "limit", required = false) Long limitResults){

        if(limitResults != null){
            return MonitoringResultDTO.convertMany(
                            monitoringResultService.getAllForEndpointLimited(monitoredEndpointId,
                                    limitResults));
        }else{
            return MonitoringResultDTO.convertMany(
                            monitoringResultService.getAllForEndpoint(monitoredEndpointId));
        }
    }
}

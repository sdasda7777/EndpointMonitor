package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoringResultDTO;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.MonitorUserService;
import com.sdasda7777.endpointmonitor.L02.MonitoredEndpointService;
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
    MonitoredEndpointService monitoredEndpointService;

    @Autowired
    MonitoringResultService monitoringResultService;

    @Autowired
    MonitorUserService monitorUserService;

    @GetMapping("{monitoredEndpointId}")
    public Collection<MonitoringResultDTO> getMonitoringResults(
            @PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId,
            @RequestParam(name = "limit", required = false) Long limitResults,
            @RequestHeader(value = "Authorization", required = false) String token){

        if(token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Authorization token must be provided");
        }

        Optional<MonitorUser> userOptional = monitorUserService.getUniqueUserByToken(token);
        if(userOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Token did not match expected number of users");
        }

        Optional<MonitoredEndpoint> endpoint = monitoredEndpointService.getEndpointById(monitoredEndpointId);
        if(endpoint.isEmpty() || endpoint.get().getOwner().getId() != userOptional.get().getId()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Endpoint with specified Id does not exist or user does not have privileges");
        }

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

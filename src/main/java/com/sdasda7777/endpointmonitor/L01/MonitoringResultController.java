package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoringResultDTO;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InsufficientDataOwnershipException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidEndpointIdException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidUserIdException;
import com.sdasda7777.endpointmonitor.L02.MonitorUserService;
import com.sdasda7777.endpointmonitor.L02.MonitoredEndpointService;
import com.sdasda7777.endpointmonitor.L02.MonitoringResultService;
import com.sdasda7777.endpointmonitor.security.authentication.KeycloakUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/monitoringResults/")
public class MonitoringResultController {

    @Autowired
    MonitoringResultService monitoringResultService;

    @Autowired
    KeycloakUserService keycloakCurrentUserService;

    public MonitoringResultController(MonitoringResultService monitoringResultService,
                                      KeycloakUserService keycloakCurrentUserService){
        this.monitoringResultService = monitoringResultService;
        this.keycloakCurrentUserService = keycloakCurrentUserService;
    }

    @GetMapping("{monitoredEndpointId}")
    public Collection<MonitoringResultDTO> getMonitoringResults(
            @PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId,
            @RequestParam(name = "limit", required = false) Long limitResults
    ){
        try{
            return MonitoringResultDTO.convertMany(
                    monitoringResultService.getAllForEndpoint(
                            getKeycloakId(), monitoredEndpointId, limitResults));
        } catch (InvalidEndpointIdException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Endpoint with given Id (%s) does not exist".formatted(e.getMessage()));
        } catch (InvalidUserIdException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User with given Id (%s) does not exist".formatted(e.getMessage()));
        } catch (InsufficientDataOwnershipException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "User does not own specified endpoint");
        }
    }

    private String getKeycloakId() {
        String ret = keycloakCurrentUserService.getUserId();
        if(ret == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization token must be provided");
        return ret;
    }
}

package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoredEndpointDTO;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InsufficientDataOwnershipException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidEndpointIdException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidUserIdException;
import com.sdasda7777.endpointmonitor.L02.MonitoredEndpointService;
import com.sdasda7777.endpointmonitor.security.authentication.KeycloakUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping("api/v1/monitoredEndpoints")
public class MonitoredEndpointController {

    @Autowired
    MonitoredEndpointService monitoredEndpointService;

    @Autowired
    KeycloakUserService keycloakCurrentUserService;

    public MonitoredEndpointController(MonitoredEndpointService monitoredEndpointService,
                                       KeycloakUserService keycloakCurrentUserService){
        this.monitoredEndpointService = monitoredEndpointService;
        this.keycloakCurrentUserService = keycloakCurrentUserService;
    }


    @GetMapping("")
    public Collection<MonitoredEndpointDTO> getMonitoredEndpoints(){
        return MonitoredEndpointDTO.convertMany(
                monitoredEndpointService.getMonitoredEndpointsByKeycloakId(getKeycloakId()));
    }

    @PostMapping("")
    public MonitoredEndpointDTO createEndpoint(
            @RequestBody MonitoredEndpoint monitoredEndpoint
    ){
        if(monitoredEndpoint.getName() == null || monitoredEndpoint.getName().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Endpoint name must be provided");
        if(monitoredEndpoint.getUrl() == null || ! monitoredEndpoint.validOrValidatableUrl())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Endpoint url must be provided and be in format '(http|https|ftp)://address'");
        if(monitoredEndpoint.getMonitoringInterval() == null || monitoredEndpoint.getMonitoringInterval() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Monitoring interval must be provided and be larger than 0");

        return MonitoredEndpointDTO.convertOne(
                monitoredEndpointService.createMonitoredEndpoint(getKeycloakId(), monitoredEndpoint));
    }

    @PutMapping("/{monitoredEndpointId}")
    public MonitoredEndpointDTO updateEndpoint(
            @PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId,
            @RequestBody MonitoredEndpoint monitoredEndpoint
    ){
        if(monitoredEndpoint.getName() != null && monitoredEndpoint.getName().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "If endpoint name is provided, it must not be empty");
        if(monitoredEndpoint.getUrl() != null && !monitoredEndpoint.validOrValidatableUrl())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "If endpoint url is provided, it must be in format '(http|https|ftp)://address'");
        if(monitoredEndpoint.getMonitoringInterval() != null && monitoredEndpoint.getMonitoringInterval() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "If monitoring interval is provided, it must be larger than 0");

        try {
            return MonitoredEndpointDTO.convertOne(
                    monitoredEndpointService.updateMonitoredEndpoint(getKeycloakId(),
                            monitoredEndpointId, monitoredEndpoint));
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

    @DeleteMapping("/{endpointId}")
    public MonitoredEndpointDTO deleteMonitoredEndpoint(
            @PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId
    ){
        try {
            return MonitoredEndpointDTO.convertOne(
                    monitoredEndpointService.deleteEndpoint(getKeycloakId(),
                            monitoredEndpointId));
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

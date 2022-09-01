package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoredEndpointDTO;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InsufficientDataOwnershipException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidEndpointIdException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidUserIdException;
import com.sdasda7777.endpointmonitor.L02.MonitoredEndpointService;
import com.sdasda7777.endpointmonitor.security.authentication.KeycloakUserService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collection;

@RestController
@RequestMapping("api/v1/monitoredEndpoints")
public class MonitoredEndpointController {

    @Autowired
    MonitoredEndpointService monitoredEndpointService;


    public MonitoredEndpointController(MonitoredEndpointService monitoredEndpointService){
        this.monitoredEndpointService = monitoredEndpointService;
    }


    @GetMapping("")
    public Collection<MonitoredEndpointDTO> getMonitoredEndpoints(
            HttpServletRequest request
    ){
        try {
            return MonitoredEndpointDTO.convertMany(
                    monitoredEndpointService.getMonitoredEndpointsByKeycloakId(
                            KeycloakUserService.getKeycloakId(request)));
        } catch (AuthenticationCredentialsNotFoundException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Authorization token must be provided");
        }
    }

    @PostMapping("")
    public MonitoredEndpointDTO createEndpoint(
            @RequestBody MonitoredEndpoint monitoredEndpoint,
            HttpServletRequest request
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

        try {
            return MonitoredEndpointDTO.convertOne(
                    monitoredEndpointService.createMonitoredEndpoint(
                            KeycloakUserService.getKeycloakId(request),
                            monitoredEndpoint));
        } catch (AuthenticationCredentialsNotFoundException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Authorization token must be provided");
        }
    }

    @PutMapping("/{monitoredEndpointId}")
    public MonitoredEndpointDTO updateEndpoint(
            @PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId,
            @RequestBody MonitoredEndpoint monitoredEndpoint,
            HttpServletRequest request
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
                    monitoredEndpointService.updateMonitoredEndpoint(
                            KeycloakUserService.getKeycloakId(request),
                            monitoredEndpointId, monitoredEndpoint));
        } catch (AuthenticationCredentialsNotFoundException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Authorization token must be provided");
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
            @PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId,
            HttpServletRequest request
    ){
        try {
            return MonitoredEndpointDTO.convertOne(
                    monitoredEndpointService.deleteEndpoint(
                            KeycloakUserService.getKeycloakId(request),
                            monitoredEndpointId));
        } catch (AuthenticationCredentialsNotFoundException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Authorization token must be provided");
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
}

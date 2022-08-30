package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoredEndpointDTO;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.MonitorUserService;
import com.sdasda7777.endpointmonitor.L02.MonitoredEndpointService;
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

    //TODO remove
    @Autowired
    MonitorUserService monitorUserService;

    public MonitoredEndpointController(MonitoredEndpointService monitoredEndpointService,
                                       MonitorUserService monitorUserService){
        this.monitoredEndpointService = monitoredEndpointService;
        this.monitorUserService = monitorUserService;

        MonitorUser u0 = new MonitorUser();
        u0.setAccessToken("user0_secret_access_token");
        u0 = this.monitorUserService.createUser(u0);
        MonitorUser u1 = new MonitorUser();
        u1.setAccessToken("user1_secret_access_token");
        u1 = this.monitorUserService.createUser(u1);


        MonitoredEndpoint me0 = new MonitoredEndpoint();
        me0.setName("Test endpoint 1 - should be reachable");
        me0.setUrl("https://www.google.com");
        me0.setMonitoringInterval(7);
        this.monitoredEndpointService.createMonitoredEndpoint("user0_secret_access_token", me0);

        MonitoredEndpoint me1 = new MonitoredEndpoint();
        me1.setName("Test endpoint 2 - should be unreachable");
        me1.setUrl("https://www.non-existent-page.com");
        me1.setMonitoringInterval(5);
        this.monitoredEndpointService.createMonitoredEndpoint("user1_secret_access_token", me1);

    }

    @GetMapping("")
    public Collection<MonitoredEndpointDTO> getMonitoredEndpoints(
            @RequestHeader(value = "Authorization", required = false) String token
    ){
        checkTokenPresent(token);

        return MonitoredEndpointDTO.convertMany(
                monitoredEndpointService.getMonitoredEndpointsByToken(token));
    }

    @PostMapping("")
    public MonitoredEndpointDTO createEndpoint(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody MonitoredEndpoint monitoredEndpoint
    ){
        checkTokenPresent(token);

        if(monitoredEndpoint.getName() == null || monitoredEndpoint.getName().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Endpoint name must be provided");
        if(monitoredEndpoint.getUrl() == null || ! monitoredEndpoint.validOrValidatableUrl())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Endpoint url must be provided and be in format '(http|https|ftp)://address'");
        if(monitoredEndpoint.getMonitoringInterval() == null || monitoredEndpoint.getMonitoringInterval() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Monitoring interval must be provided and be larger than 0");
        if(monitoredEndpoint.getOwner() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Endpoint owner should not be provided, as it will be set by token");

        return MonitoredEndpointDTO.convertOne(
                monitoredEndpointService.createMonitoredEndpoint(token, monitoredEndpoint));
    }

    @PutMapping("/{monitoredEndpointId}")
    public MonitoredEndpointDTO updateEndpoint(
            @PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody MonitoredEndpoint monitoredEndpoint
    ){
        checkTokenPresent(token);

        if(monitoredEndpoint.getName() != null && monitoredEndpoint.getName().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "If endpoint name is provided, it must not be empty");
        if(monitoredEndpoint.getUrl() != null && !monitoredEndpoint.validOrValidatableUrl())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "If endpoint url is provided, it must be in format '(http|https|ftp)://address'");
        if(monitoredEndpoint.getMonitoringInterval() != null && monitoredEndpoint.getMonitoringInterval() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "If monitoring interval is provided, it must be larger than 0");

        return MonitoredEndpointDTO.convertOne(
                monitoredEndpointService.updateMonitoredEndpoint(token, monitoredEndpointId, monitoredEndpoint));
    }

    @DeleteMapping("/{endpointId}")
    public MonitoredEndpointDTO deleteMonitoredEndpoint(
            @PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId,
            @RequestHeader(value = "Authorization", required = false) String token
    ){
        checkTokenPresent(token);

        return MonitoredEndpointDTO.convertOne(monitoredEndpointService.deleteEndpoint(token, monitoredEndpointId));
    }

    private void checkTokenPresent(String token) {
        if(token == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization token must be provided");
    }
}

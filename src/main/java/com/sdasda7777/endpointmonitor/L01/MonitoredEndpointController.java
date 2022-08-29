package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoredEndpointDTO;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.MonitorUserService;
import com.sdasda7777.endpointmonitor.L02.MonitoredEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/monitoredEndpoints")
public class MonitoredEndpointController {

    @Autowired
    MonitoredEndpointService monitoredEndpointService;

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
        me0.setId(11l);
        me0.setName("Test endpoint 1 - should succeed");
        me0.setUrl("https://www.google.com");
        me0.setMonitoringInterval(7);
        me0.setCreationDate(LocalDateTime.now());
        me0.setLastCheckDate(LocalDateTime.now());
        me0.setOwner(u0);
        this.monitoredEndpointService.createMonitoredEndpoint(me0);

        MonitoredEndpoint me1 = new MonitoredEndpoint();
        me1.setId(12l);
        me1.setName("Test endpoint 2 - should fail");
        me1.setUrl("https://www.non-existent-page.com");
        me1.setCreationDate(LocalDateTime.now());
        me1.setLastCheckDate(LocalDateTime.now());
        me1.setMonitoringInterval(5);
        me1.setOwner(u1);
        this.monitoredEndpointService.createMonitoredEndpoint(me1);

    }

    @GetMapping("")
    public Collection<MonitoredEndpointDTO> getMonitoredEndpoints(
            @RequestHeader(value = "Authorization", required = false) String token
    ){
        if(token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                                "Authorization token must be provided");
        }
        Optional<MonitorUser> userOptional = monitorUserService.getUniqueUserByToken(token);
        if(userOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Token did not match expected number of users");
        }

        return MonitoredEndpointDTO.convertMany(
                monitoredEndpointService.getEndpointsByUser(
                        userOptional.get()));
    }
}

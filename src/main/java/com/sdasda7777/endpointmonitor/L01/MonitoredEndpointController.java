package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoredEndpointDTO;
import com.sdasda7777.endpointmonitor.L02.MonitoredEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("api/v1/monitoredEndpoints")
public class MonitoredEndpointController {

    @Autowired
    MonitoredEndpointService monitoredEndpointService;

    @GetMapping("")
    public Collection<MonitoredEndpointDTO> getMonitoredEndpoints(){
        ArrayList<MonitoredEndpointDTO> ret = new ArrayList<>();
        ret.add(new MonitoredEndpointDTO(11l, "Test endpoint", "localhost:8080/asdf",
                                            LocalDateTime.now(), LocalDateTime.now(),
                                            10, 22l));
        ret.add(new MonitoredEndpointDTO(12l, "Test endpoint 2", "localhost:8080/asdfasdf",
                                            LocalDateTime.now(), LocalDateTime.now(),
                                            5, 23l));
        return ret;

        //return MonitoredEndpointDTO.convertMany(monitoredEndpointService.getAll());
    }
}

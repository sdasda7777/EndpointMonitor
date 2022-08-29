package com.sdasda7777.endpointmonitor.L02;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoredEndpointDTO;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class MonitoredEndpointService {


    public Collection<MonitoredEndpoint> getAll(){
        ArrayList<MonitoredEndpoint> ret = new ArrayList<>();

        MonitoredEndpoint r0 = new MonitoredEndpoint();
        r0.setId(11l);
        r0.setName("Test endpoint");
        r0.setUrl("localhost:8080/asdf");
        r0.setCreationDate(LocalDateTime.now());
        r0.setLastCheckDate(LocalDateTime.now());
        r0.setMonitoringInterval(10);
        MonitorUser u0 = new MonitorUser();
        u0.setId(22l);
        r0.setOwner(u0);
        ret.add(r0);

        MonitoredEndpoint r1 = new MonitoredEndpoint();
        r1.setId(12l);
        r1.setName("Test endpoint 2");
        r1.setUrl("localhost:8080/asdfasdf");
        r1.setCreationDate(LocalDateTime.now());
        r1.setLastCheckDate(LocalDateTime.now());
        r1.setMonitoringInterval(5);
        MonitorUser u1 = new MonitorUser();
        u1.setId(23l);
        r1.setOwner(u1);
        ret.add(r1);

        return ret;
    }
}

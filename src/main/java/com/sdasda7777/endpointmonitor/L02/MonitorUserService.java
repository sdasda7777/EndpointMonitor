package com.sdasda7777.endpointmonitor.L02;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L03.MonitorUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class MonitorUserService {
    @Autowired
    MonitorUserRepository monitorUserRepository;

    public MonitorUserService(MonitorUserRepository monitorUserRepository){
        this.monitorUserRepository = monitorUserRepository;
    }

    /*
    public Optional<MonitorUser> getUserById(Long id){
        return monitorUserRepository.findById(id);
    }
    */

    public Optional<MonitorUser> getUserByKeycloakId(String username) {
        Collection<MonitorUser> users = monitorUserRepository.findByKeycloakId(username);
        return (users.size() == 1 ? users.stream().findFirst() : Optional.empty());
    }

    public MonitorUser createUser(MonitorUser monitorUser){
        return monitorUserRepository.save(monitorUser);
    }
}

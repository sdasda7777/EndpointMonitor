package com.sdasda7777.endpointmonitor.L02;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
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

    public Optional<MonitorUser> getUserById(Long id){
        return monitorUserRepository.findById(id);
    }

    public Optional<MonitorUser> getUniqueUserByToken(String token){
        Collection<MonitorUser> users = monitorUserRepository.getByToken(token);
        return (users.size() == 1 ? users.stream().findFirst() : Optional.empty());
    }

    public MonitorUser createUser(MonitorUser monitorUser){
        return monitorUserRepository.save(monitorUser);
    }

    public MonitorUser updateUser(MonitorUser monitorUser){
        Optional<MonitorUser> currentOptional = monitorUserRepository.findById(monitorUser.getId());
        if(currentOptional.isEmpty())
            throw new IllegalStateException();

        MonitorUser current = currentOptional.get();
        if(monitorUser.getUsername() != null)
            current.setUsername(monitorUser.getUsername());
        if(monitorUser.getEmail() != null)
            current.setEmail(monitorUser.getEmail());
        if(monitorUser.getAccessToken() != null)
            current.setAccessToken(monitorUser.getAccessToken());


        return monitorUserRepository.save(current);
    }
}

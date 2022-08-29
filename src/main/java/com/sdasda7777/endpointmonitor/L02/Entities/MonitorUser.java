package com.sdasda7777.endpointmonitor.L02.Entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "monitorUsers")
public class MonitorUser {

    @Id
    @GeneratedValue
    public Long id;

    String username;
    String email;
    String accessToken;

    @OneToMany(mappedBy = "owner")
    public Collection<MonitoredEndpoint> monitoredEndpoints;

    public MonitorUser(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Collection<MonitoredEndpoint> getMonitoredEndpoints() {
        return monitoredEndpoints;
    }

    public void setMonitoredEndpoints(Collection<MonitoredEndpoint> monitoredEndpoints) {
        this.monitoredEndpoints = monitoredEndpoints;
    }
}

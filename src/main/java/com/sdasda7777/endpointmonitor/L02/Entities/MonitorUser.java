package com.sdasda7777.endpointmonitor.L02.Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "monitorUsers")
public class MonitorUser {

    @Id
    @GeneratedValue
    Long id;

    String keycloakId;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    Collection<MonitoredEndpoint> monitoredEndpoints;

    public MonitorUser(){
        this.keycloakId = null;
        this.monitoredEndpoints = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*
    public String getKeycloakId() {
        return keycloakId;
    }
    */

    public void setKeycloakId(String username) {
        this.keycloakId = username;
    }

    /*
    public Collection<MonitoredEndpoint> getMonitoredEndpoints() {
        return monitoredEndpoints;
    }

    public void setMonitoredEndpoints(Collection<MonitoredEndpoint> monitoredEndpoints) {
        this.monitoredEndpoints = monitoredEndpoints;
    }
    */
}

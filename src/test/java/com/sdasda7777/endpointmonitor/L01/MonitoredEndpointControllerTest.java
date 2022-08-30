package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.MonitorUserService;
import com.sdasda7777.endpointmonitor.L02.MonitoredEndpointService;
import com.sdasda7777.endpointmonitor.L03.MonitorUserRepository;
import com.sdasda7777.endpointmonitor.L03.MonitoredEndpointRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MonitoredEndpointControllerTest {


    @Test
    void getMonitoredEndpointsNoToken() {
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class);
        MonitorUserService monitorUserService =
                new MonitorUserService(monitorUserRepository);

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService);
        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);


        ResponseStatusException errorproto = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Authorization token must be provided");
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.getMonitoredEndpoints(null)
        );
        assertEquals(errorproto.getStatus(), error.getStatus());
        assertEquals(errorproto.getMessage(), error.getMessage());
    }

    @Test
    void getMonitoredEndpointsBadToken() {
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class);
        Mockito.when(monitorUserRepository.getByToken("bad_token"))
                .thenReturn(Collections.emptyList());
        Mockito.when(monitorUserRepository.getByToken("too_good_token"))
                .thenReturn(Arrays.asList(new MonitorUser(), new MonitorUser()));
        MonitorUserService monitorUserService =
                new MonitorUserService(monitorUserRepository);

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService);
        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);


        ResponseStatusException error1 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.getMonitoredEndpoints("bad_token")
        );
        ResponseStatusException errorproto = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                    "Token did not match expected number of users");
        assertEquals(errorproto.getStatus(), error1.getStatus());
        assertEquals(errorproto.getMessage(), error1.getMessage());

        ResponseStatusException error2 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.getMonitoredEndpoints("too_good_token")
        );
        assertEquals(errorproto.getStatus(), error1.getStatus());
        assertEquals(errorproto.getMessage(), error1.getMessage());
    }

    void PotentialTest(){
        /*
        Mockito.mock(repository);

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


         */
    }


    @Test
    void createEndpoint() {
    }

    @Test
    void updateEndpoint() {
    }

    @Test
    void deleteMonitoredEndpoint() {
    }
}
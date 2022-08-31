package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoredEndpointDTO;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.MonitorUserService;
import com.sdasda7777.endpointmonitor.L02.MonitoredEndpointService;
import com.sdasda7777.endpointmonitor.L03.MonitorUserRepository;
import com.sdasda7777.endpointmonitor.L03.MonitoredEndpointRepository;
import com.sdasda7777.endpointmonitor.security.authentication.KeycloakUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.jmx.access.InvalidInvocationException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MonitoredEndpointControllerTest {

    @Test
    void getMonitoredEndpointsNoKeycloakId() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);
        MonitorUserService monitorUserService =
                new MonitorUserService(monitorUserRepository);

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService);

        KeycloakUserService keycloakUserService =
                Mockito.mock(KeycloakUserService.class, defaultAnswer);
        Mockito.doReturn(null).when(keycloakUserService).getUserId();

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService, keycloakUserService);


        ResponseStatusException errorproto = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Authorization token must be provided");
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.getMonitoredEndpoints()
        );
        assertEquals(errorproto.getStatus(), error.getStatus());
        assertEquals(errorproto.getMessage(), error.getMessage());
    }

    @Test
    void getMonitoredEndpointsUnknownKeycloakId() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);
        Mockito.doReturn(Collections.emptyList())
                .when(monitorUserRepository).findByKeycloakId("bad_keycloakid");
        MonitorUserService monitorUserService =
                new MonitorUserService(monitorUserRepository);

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        Mockito.doReturn(Collections.emptyList())
                .when(monitoredEndpointRepository).getEndpointsByKeycloakId("bad_keycloakid");
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService);

        KeycloakUserService keycloakUserService =
                Mockito.mock(KeycloakUserService.class, defaultAnswer);
        Mockito.doReturn("bad_keycloakid")
                .when(keycloakUserService).getUserId();

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService, keycloakUserService);

        assertEquals(Collections.emptyList(),
                    monitoredEndpointController.getMonitoredEndpoints());
    }

    @Test
    void getMonitoredEndpointsAllRight() {
        var defaultAnswer = new ThrowsException(
                                new InvalidInvocationException(
                                        "Inappropriate usage of mocked object"));

        MonitorUser monitorUser = new MonitorUser();
        monitorUser.setId(43l);
        monitorUser.setKeycloakId("good_keycloakid");

        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);
        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("good_keycloakid");
        MonitorUserService monitorUserService =
                new MonitorUserService(monitorUserRepository);

        MonitoredEndpoint monitoredEndpoint0 = new MonitoredEndpoint(
            "Test endpoint 0", "http://url0.org",
            LocalDateTime.of(2001, 1, 25, 13, 42, 56),
            LocalDateTime.of(2002, 2, 26, 14, 43, 57),
            5
        );
        monitoredEndpoint0.setId(44l);
        monitoredEndpoint0.setOwner(monitorUser);
        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
            "Test endpoint 1", "https://url1.com",
            LocalDateTime.of(2003, 3, 27, 15, 44, 58),
            LocalDateTime.of(2004, 4, 28, 16, 45, 59),
            7
        );
        monitoredEndpoint1.setId(45l);
        monitoredEndpoint1.setOwner(monitorUser);

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        Mockito.doReturn(List.of(monitoredEndpoint0, monitoredEndpoint1))
                .when(monitoredEndpointRepository).getEndpointsByKeycloakId("good_keycloakid");
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService);

        KeycloakUserService keycloakUserService =
                Mockito.mock(KeycloakUserService.class, defaultAnswer);
        Mockito.doReturn("good_keycloakid")
                .when(keycloakUserService).getUserId();

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService, keycloakUserService);


        List<MonitoredEndpointDTO> resultProto = List.of(
            new MonitoredEndpointDTO(
                44l, "Test endpoint 0", "http://url0.org",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                5, 43l
            ),
            new MonitoredEndpointDTO(
                45l, "Test endpoint 1", "https://url1.com",
                LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                LocalDateTime.of(2004, 4, 28, 16, 45, 59),
                7, 43l
            )
        );
        List<MonitoredEndpointDTO> actualResult =
                monitoredEndpointController.getMonitoredEndpoints()
                        .stream().collect(Collectors.toList());

        assertEquals(2, actualResult.size());
        assertEquals(resultProto.get(0), actualResult.get(0));
        assertEquals(resultProto.get(1), actualResult.get(1));
    }

    void createEndpointInvalidEndpoint() {
    }

    void createEndpointUnknownUser() {
    }

    void createEndpointAllRight() {
    }

    void updateEndpointInvalidUser() {
    }

    void updateEndpointInvalidEndpoint() {
    }

    void updateEndpointAllRight() {
    }

    void deleteMonitoredEndpointInvalidUser() {
    }

    void deleteMonitoredEndpointInvalidEndpoint() {
    }

    void deleteMonitoredEndpointAllRight() {
    }

    void PotentialTest(){
        /*
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
}
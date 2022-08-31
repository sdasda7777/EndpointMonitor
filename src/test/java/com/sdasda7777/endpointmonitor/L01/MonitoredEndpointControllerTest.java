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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.springframework.http.HttpStatus;
import org.springframework.jmx.access.InvalidInvocationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
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

    @Test
    void createEndpointInvalidEndpoint() {
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
        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService);

        KeycloakUserService keycloakUserService =
                Mockito.mock(KeycloakUserService.class, defaultAnswer);
        Mockito.doReturn("good_keycloakid")
                .when(keycloakUserService).getUserId();

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService, keycloakUserService);

        //Test creating without setting name
        MonitoredEndpoint monitoredEndpoint0 = new MonitoredEndpoint();
        monitoredEndpoint0.setUrl("https://url.com");
        monitoredEndpoint0.setMonitoringInterval(2);
        ResponseStatusException response0 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint0)
        );
        ResponseStatusException errorProto0 = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Endpoint name must be provided");
        assertEquals(errorProto0.getStatus(), response0.getStatus());
        assertEquals(errorProto0.getMessage(), response0.getMessage());

        //Test creating with invalid name
        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint();
        monitoredEndpoint1.setName("");
        monitoredEndpoint1.setUrl("https://url.com");
        monitoredEndpoint1.setMonitoringInterval(2);
        ResponseStatusException response1 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint1)
        );
        ResponseStatusException errorProto1 = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Endpoint name must be provided");
        assertEquals(errorProto1.getStatus(), response1.getStatus());
        assertEquals(errorProto1.getMessage(), response1.getMessage());

        //Test creating without setting url
        MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint();
        monitoredEndpoint2.setName("Valid name");
        monitoredEndpoint2.setMonitoringInterval(2);
        ResponseStatusException response2 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint2)
        );
        ResponseStatusException errorProto2 = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Endpoint url must be provided and be in format '(http|https|ftp)://address'");
        assertEquals(errorProto2.getStatus(), response2.getStatus());
        assertEquals(errorProto2.getMessage(), response2.getMessage());

        //Test creating with invalid url
        MonitoredEndpoint monitoredEndpoint3 = new MonitoredEndpoint();
        monitoredEndpoint3.setName("Valid name");
        monitoredEndpoint3.setUrl("invalid url");
        monitoredEndpoint3.setMonitoringInterval(2);
        ResponseStatusException response3 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint3)
        );
        ResponseStatusException errorProto3 = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Endpoint url must be provided and be in format '(http|https|ftp)://address'");
        assertEquals(errorProto3.getStatus(), response3.getStatus());
        assertEquals(errorProto3.getMessage(), response3.getMessage());

        //Test creating without setting interval
        MonitoredEndpoint monitoredEndpoint4 = new MonitoredEndpoint();
        monitoredEndpoint4.setName("Valid name");
        monitoredEndpoint4.setUrl("https://url.com");
        ResponseStatusException response4 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint4)
        );
        ResponseStatusException errorProto4 = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Monitoring interval must be provided and be larger than 0");
        assertEquals(errorProto4.getStatus(), response4.getStatus());
        assertEquals(errorProto4.getMessage(), response4.getMessage());

        //Test creating with invalid interval
        MonitoredEndpoint monitoredEndpoint5 = new MonitoredEndpoint();
        monitoredEndpoint5.setName("Valid name");
        monitoredEndpoint5.setUrl("https://url.com");
        monitoredEndpoint5.setMonitoringInterval(0);
        ResponseStatusException response5 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint5)
        );
        ResponseStatusException errorProto5 = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Monitoring interval must be provided and be larger than 0");
        assertEquals(errorProto5.getStatus(), response5.getStatus());
        assertEquals(errorProto5.getMessage(), response5.getMessage());
    }

    @Test
    void createEndpointAllRight() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitorUser monitorUser = new MonitorUser();
        monitorUser.setId(43l);
        monitorUser.setKeycloakId("known_keycloakid");

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

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService, keycloakUserService);


        //Test with known user
        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);

        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("known_keycloakid");
        Mockito.doAnswer(i -> {
            MonitoredEndpoint arg = i.getArgument(0);
            arg.setId(44l);
            return arg;
        }).when(monitoredEndpointRepository).save(ArgumentMatchers.<MonitoredEndpoint>any());
        Mockito.doReturn("known_keycloakid")
                .when(keycloakUserService).getUserId();

        MonitoredEndpointDTO resultProto1 = new MonitoredEndpointDTO(
                44l, "Valid endpoint name", "https://url.com",
                LocalDateTime.now(),
                LocalDateTime.MIN,
                2, 43l
        );
        MonitoredEndpointDTO result1 = monitoredEndpointController.createEndpoint(monitoredEndpoint1);
        assertEquals(resultProto1.getId(), result1.getId());
        assertEquals(resultProto1.getName(), result1.getName());
        assertEquals(resultProto1.getUrl(), result1.getUrl());
        assert(resultProto1.getCreationDate().isBefore(result1.getCreationDate()));
        assert(result1.getCreationDate().isBefore(LocalDateTime.now()));
        assertEquals(resultProto1.getLastCheckDate(), result1.getLastCheckDate());
        assertEquals(resultProto1.getMonitoringInterval(), result1.getMonitoringInterval());
        assertEquals(resultProto1.getOwnerId(), result1.getOwnerId());


        //Test creating with unknown user id
        MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint();
        monitoredEndpoint2.setName("Valid endpoint name");
        monitoredEndpoint2.setUrl("https://url.com");
        monitoredEndpoint2.setMonitoringInterval(3);

        Mockito.doReturn(Collections.emptyList())
                .when(monitorUserRepository).findByKeycloakId("unknown_keycloakid");
        Mockito.doAnswer(i -> {
            MonitorUser arg = i.getArgument(0);
            arg.setId(45l);
            return arg;
        }).when(monitorUserRepository).save(ArgumentMatchers.<MonitorUser>any());
        Mockito.doAnswer(i -> {
            MonitoredEndpoint arg = i.getArgument(0);
            arg.setId(46l);
            return arg;
        }).when(monitoredEndpointRepository).save(ArgumentMatchers.<MonitoredEndpoint>any());
        Mockito.doReturn("unknown_keycloakid")
                .when(keycloakUserService).getUserId();

        MonitoredEndpointDTO resultProto2 = new MonitoredEndpointDTO(
                46l, "Valid endpoint name", "https://url.com",
                LocalDateTime.now(),
                LocalDateTime.MIN,
                3, 45l
        );
        MonitoredEndpointDTO result2 = monitoredEndpointController.createEndpoint(monitoredEndpoint2);
        assertEquals(resultProto2.getId(), result2.getId());
        assertEquals(resultProto2.getName(), result2.getName());
        assertEquals(resultProto2.getUrl(), result2.getUrl());
        assert(resultProto2.getCreationDate().isBefore(result2.getCreationDate()));
        assert(result2.getCreationDate().isBefore(LocalDateTime.now()));
        assertEquals(resultProto2.getLastCheckDate(), result2.getLastCheckDate());
        assertEquals(resultProto2.getMonitoringInterval(), result2.getMonitoringInterval());
        assertEquals(resultProto2.getOwnerId(), result2.getOwnerId());
    }

    @Test
    void updateEndpointInvalidUser() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitorUser monitorUser = new MonitorUser();
        monitorUser.setId(43l);
        monitorUser.setKeycloakId("known_keycloakid");

        MonitorUser monitorUser2 = new MonitorUser();
        monitorUser2.setId(44l);
        monitorUser2.setKeycloakId("known_keycloakid2");

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

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService, keycloakUserService);


        //Test update from non-owner
        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);
        monitoredEndpoint1.setId(45l);
        monitoredEndpoint1.setOwner(monitorUser);

        MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint(
                "New valid endpoint name", "https://new-valid-url.com",
                LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                LocalDateTime.of(2004, 4, 28, 16, 45, 59),
                3);
        monitoredEndpoint2.setId(46l);
        monitoredEndpoint2.setOwner(monitorUser2);


        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45l);
        Mockito.doReturn(List.of(monitorUser2))
                .when(monitorUserRepository).findByKeycloakId("known_keycloakid2");
        Mockito.doReturn("known_keycloakid2")
                .when(keycloakUserService).getUserId();

        ResponseStatusException protoResult1 = new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User does not own specified endpoint");
        ResponseStatusException result1 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.updateEndpoint(45l, monitoredEndpoint2)
        );
        assertEquals(protoResult1.getStatus(), result1.getStatus());
        assertEquals(protoResult1.getMessage(), result1.getMessage());

        //Test update from unknown account
        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45l);
        Mockito.doReturn(Collections.emptyList())
                .when(monitorUserRepository).findByKeycloakId("unknown_keycloakid");
        Mockito.doReturn("unknown_keycloakid")
                .when(keycloakUserService).getUserId();

        ResponseStatusException protoResult2 = new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User with given Id does not exist");
        ResponseStatusException result2 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.updateEndpoint(45l, monitoredEndpoint2)
        );
        assertEquals(protoResult2.getStatus(), result2.getStatus());
        assertEquals(protoResult2.getMessage(), result2.getMessage());
    }

    @Test
    void updateEndpointInvalidEndpoint() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitorUser monitorUser = new MonitorUser();
        monitorUser.setId(43l);
        monitorUser.setKeycloakId("known_keycloakid");

        MonitorUser monitorUser2 = new MonitorUser();
        monitorUser2.setId(44l);
        monitorUser2.setKeycloakId("known_keycloakid2");

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

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService, keycloakUserService);


        //Test updating non-existant endpoint
        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);
        monitoredEndpoint1.setId(45l);
        monitoredEndpoint1.setOwner(monitorUser);

        MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint(
                "New valid endpoint name", "https://new-valid-url.com",
                LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                LocalDateTime.of(2004, 4, 28, 16, 45, 59),
                3);
        monitoredEndpoint2.setId(46l);
        monitoredEndpoint2.setOwner(monitorUser2);


        Mockito.doReturn(Optional.empty())
                .when(monitoredEndpointRepository).findById(69l);
        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("known_keycloakid");
        Mockito.doReturn("known_keycloakid")
                .when(keycloakUserService).getUserId();

        ResponseStatusException protoResult1 = new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Endpoint with given Id does not exist");
        ResponseStatusException result1 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.updateEndpoint(69l, monitoredEndpoint2)
        );
        assertEquals(protoResult1.getStatus(), result1.getStatus());
        assertEquals(protoResult1.getMessage(), result1.getMessage());

        //Test update with invalid name
        monitoredEndpoint2.setName("");

        ResponseStatusException protoResult2 = new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "If endpoint name is provided, it must not be empty");
        ResponseStatusException result2 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.updateEndpoint(45l, monitoredEndpoint2)
        );
        assertEquals(protoResult2.getStatus(), result2.getStatus());
        assertEquals(protoResult2.getMessage(), result2.getMessage());

        //Test update with invalid url
        monitoredEndpoint2.setName("New valid endpoint name");
        monitoredEndpoint2.setUrl("invalidurl");

        ResponseStatusException protoResult3 = new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "If endpoint url is provided, it must be in format '(http|https|ftp)://address'");
        ResponseStatusException result3 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.updateEndpoint(45l, monitoredEndpoint2)
        );
        assertEquals(protoResult3.getStatus(), result3.getStatus());
        assertEquals(protoResult3.getMessage(), result3.getMessage());

        //Test update with invalid interval
        monitoredEndpoint2.setUrl("https://new-valid-url.com");
        monitoredEndpoint2.setMonitoringInterval(0);

        ResponseStatusException protoResult4 = new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "If monitoring interval is provided, it must be larger than 0");
        ResponseStatusException result4 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.updateEndpoint(45l, monitoredEndpoint2)
        );
        assertEquals(protoResult4.getStatus(), result4.getStatus());
        assertEquals(protoResult4.getMessage(), result4.getMessage());
    }

    @Test
    void updateEndpointAllRight() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitorUser monitorUser = new MonitorUser();
        monitorUser.setId(43l);
        monitorUser.setKeycloakId("known_keycloakid");

        MonitorUser monitorUser2 = new MonitorUser();
        monitorUser2.setId(44l);
        monitorUser2.setKeycloakId("known_keycloakid2");

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

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService, keycloakUserService);


        //Test with known user
        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);
        monitoredEndpoint1.setId(45l);
        monitoredEndpoint1.setOwner(monitorUser);

        MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint(
                "New valid endpoint name", "https://new-valid-url.com",
                LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                LocalDateTime.of(2004, 4, 28, 16, 45, 59),
                3);
        monitoredEndpoint2.setId(46l);
        monitoredEndpoint2.setOwner(monitorUser2);


        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45l);
        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("known_keycloakid");

        Mockito.doAnswer(i -> {
            return i.getArgument(0);
        }).when(monitoredEndpointRepository).save(ArgumentMatchers.<MonitoredEndpoint>any());
        Mockito.doReturn("known_keycloakid")
                .when(keycloakUserService).getUserId();

        MonitoredEndpointDTO resultProto1 = new MonitoredEndpointDTO(
                45l, "New valid endpoint name", "https://new-valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                3, 43l
        );
        MonitoredEndpointDTO result1 = monitoredEndpointController.updateEndpoint(
                45l, monitoredEndpoint2);
        assertEquals(resultProto1, result1);
    }

    @Test
    void deleteMonitoredEndpointInvalid() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitorUser monitorUser = new MonitorUser();
        monitorUser.setId(43l);
        monitorUser.setKeycloakId("known_keycloakid");

        MonitorUser monitorUser2 = new MonitorUser();
        monitorUser2.setId(44l);
        monitorUser2.setKeycloakId("known_keycloakid2");

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

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService, keycloakUserService);


        // Unknown endpoint
        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);
        monitoredEndpoint1.setId(45l);
        monitoredEndpoint1.setOwner(monitorUser);

        Mockito.doReturn(Optional.empty())
                .when(monitoredEndpointRepository).findById(69l);
        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("known_keycloakid");

        Mockito.doNothing().when(monitoredEndpointRepository).deleteById(ArgumentMatchers.<Long>any());
        Mockito.doReturn("known_keycloakid")
                .when(keycloakUserService).getUserId();

        ResponseStatusException protoResult1 = new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Endpoint with given Id does not exist");
        ResponseStatusException result1 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.deleteMonitoredEndpoint(69l)
        );
        assertEquals(protoResult1.getStatus(), result1.getStatus());
        assertEquals(protoResult1.getMessage(), result1.getMessage());

        // Unknown user
        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45l);
        Mockito.doReturn(Collections.emptyList())
                .when(monitorUserRepository).findByKeycloakId("unknown_keycloakid");

        Mockito.doNothing().when(monitoredEndpointRepository).deleteById(ArgumentMatchers.<Long>any());
        Mockito.doReturn("unknown_keycloakid")
                .when(keycloakUserService).getUserId();

        ResponseStatusException protoResult2 = new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User with given Id does not exist");
        ResponseStatusException result2 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.deleteMonitoredEndpoint(45l)
        );
        assertEquals(protoResult2.getStatus(), result2.getStatus());
        assertEquals(protoResult2.getMessage(), result2.getMessage());

        // Non-owner
        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45l);
        Mockito.doReturn(List.of(monitorUser2))
                .when(monitorUserRepository).findByKeycloakId("known_keycloakid2");

        Mockito.doNothing().when(monitoredEndpointRepository).deleteById(ArgumentMatchers.<Long>any());
        Mockito.doReturn("known_keycloakid2")
                .when(keycloakUserService).getUserId();

        ResponseStatusException protoResult3 = new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User does not own specified endpoint");
        ResponseStatusException result3 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.deleteMonitoredEndpoint(45l)
        );
        assertEquals(protoResult3.getStatus(), result3.getStatus());
        assertEquals(protoResult3.getMessage(), result3.getMessage());
    }

    @Test
    void deleteMonitoredEndpointAllRight() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitorUser monitorUser = new MonitorUser();
        monitorUser.setId(43l);
        monitorUser.setKeycloakId("known_keycloakid");

        MonitorUser monitorUser2 = new MonitorUser();
        monitorUser2.setId(44l);
        monitorUser2.setKeycloakId("known_keycloakid2");

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

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService, keycloakUserService);


        //Test with correct user
        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);
        monitoredEndpoint1.setId(45l);
        monitoredEndpoint1.setOwner(monitorUser);

        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45l);
        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("known_keycloakid");

        Mockito.doNothing().when(monitoredEndpointRepository).deleteById(ArgumentMatchers.<Long>any());
        Mockito.doReturn("known_keycloakid")
                .when(keycloakUserService).getUserId();

        MonitoredEndpointDTO resultProto1 = new MonitoredEndpointDTO(
                45l, "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2, 43l
        );
        MonitoredEndpointDTO result1 = monitoredEndpointController.deleteMonitoredEndpoint(45l);
        assertEquals(resultProto1, result1);
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
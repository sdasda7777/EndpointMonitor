package com.sdasda7777.endpointmonitor.layer1;

import com.sdasda7777.endpointmonitor.layer1.dto.MonitoredEndpointDTO;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.LocalDateTimeService;
import com.sdasda7777.endpointmonitor.L02.MonitorUserService;
import com.sdasda7777.endpointmonitor.L02.MonitoredEndpointService;
import com.sdasda7777.endpointmonitor.L03.MonitorUserRepository;
import com.sdasda7777.endpointmonitor.L03.MonitoredEndpointRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.springframework.http.HttpStatus;
import org.springframework.jmx.access.InvalidInvocationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
class MonitoredEndpointControllerTest {

    @Test
    void getMonitoredEndpointsNoKeycloakId() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);

        LocalDateTimeService localDateTimeService = Mockito.mock(LocalDateTimeService.class, defaultAnswer);
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService, localDateTimeService);

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);

        //
        HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(null)
                .when(request1).getUserPrincipal();

        ResponseStatusException expected_error = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Authorization token must be provided"
        );
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.getMonitoredEndpoints(request1)
        );
        assertEquals(expected_error.getStatus(), error.getStatus());
        assertEquals(expected_error.getMessage(), error.getMessage());
    }

    @Test
    void getMonitoredEndpointsUnknownKeycloakId() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        Mockito.doReturn(Collections.emptyList())
                .when(monitoredEndpointRepository).getEndpointsByKeycloakId("bad_keycloak_id");
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);
        Mockito.doReturn(Collections.emptyList())
                .when(monitorUserRepository).findByKeycloakId("bad_keycloak_id");

        LocalDateTimeService localDateTimeService = Mockito.mock(LocalDateTimeService.class, defaultAnswer);
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService, localDateTimeService);

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);

        //
        JwtAuthenticationToken principal1 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal1).isAuthenticated();
        Mockito.doReturn("bad_keycloak_id")
                .when(principal1).getName();
        HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal1)
                .when(request1).getUserPrincipal();

        assertEquals(Collections.emptyList(),
                    monitoredEndpointController.getMonitoredEndpoints(request1));
    }

    @Test
    void getMonitoredEndpointsAllRight() {
        var defaultAnswer = new ThrowsException(
                                new InvalidInvocationException(
                                        "Inappropriate usage of mocked object"));

        MonitorUser monitorUser = new MonitorUser();
        monitorUser.setId(43L);
        monitorUser.setKeycloakId("good_keycloak_id");

        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);
        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("good_keycloak_id");

        MonitoredEndpoint monitoredEndpoint0 = new MonitoredEndpoint(
            "Test endpoint 0", "https://url0.org",
            LocalDateTime.of(2001, 1, 25, 13, 42, 56),
            LocalDateTime.of(2002, 2, 26, 14, 43, 57),
            5
        );
        monitoredEndpoint0.setId(44L);
        monitoredEndpoint0.setOwner(monitorUser);
        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
            "Test endpoint 1", "https://url1.com",
            LocalDateTime.of(2003, 3, 27, 15, 44, 58),
            LocalDateTime.of(2004, 4, 28, 16, 45, 59),
            7
        );
        monitoredEndpoint1.setId(45L);
        monitoredEndpoint1.setOwner(monitorUser);

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        Mockito.doReturn(List.of(monitoredEndpoint0, monitoredEndpoint1))
                .when(monitoredEndpointRepository).getEndpointsByKeycloakId("good_keycloak_id");

        LocalDateTimeService localDateTimeService = Mockito.mock(LocalDateTimeService.class, defaultAnswer);
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService, localDateTimeService);

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);

        //
        JwtAuthenticationToken principal1 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal1).isAuthenticated();
        Mockito.doReturn("good_keycloak_id")
                .when(principal1).getName();
        HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal1)
                .when(request1).getUserPrincipal();

        List<MonitoredEndpointDTO> resultProto = List.of(
            new MonitoredEndpointDTO(
                    44L, "Test endpoint 0", "https://url0.org",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                5, 43L
            ),
            new MonitoredEndpointDTO(
                    45L, "Test endpoint 1", "https://url1.com",
                LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                LocalDateTime.of(2004, 4, 28, 16, 45, 59),
                7, 43L
            )
        );
        List<MonitoredEndpointDTO> actualResult =
                monitoredEndpointController.getMonitoredEndpoints(request1)
                        .stream().toList();

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
        monitorUser.setId(43L);
        monitorUser.setKeycloakId("good_keycloak_id");

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);
        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("good_keycloak_id");

        LocalDateTimeService localDateTimeService = Mockito.mock(LocalDateTimeService.class, defaultAnswer);
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService, localDateTimeService);

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);

        //Test creating without setting name
        JwtAuthenticationToken principal1 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal1).isAuthenticated();
        Mockito.doReturn("good_keycloak_id")
                .when(principal1).getName();
        HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal1)
                .when(request1).getUserPrincipal();

        MonitoredEndpoint monitoredEndpoint0 = new MonitoredEndpoint();
        monitoredEndpoint0.setUrl("https://url.com");
        monitoredEndpoint0.setMonitoringInterval(2);
        ResponseStatusException response0 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint0, request1)
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
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint1, request1)
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
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint2, request1)
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
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint3, request1)
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
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint4, request1)
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
                () -> monitoredEndpointController.createEndpoint(monitoredEndpoint5, request1)
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
        monitorUser.setId(43L);
        monitorUser.setKeycloakId("known_keycloak_id");

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);

        LocalDateTimeService localDateTimeService =
                Mockito.mock(LocalDateTimeService.class, defaultAnswer);
        Mockito.doReturn(LocalDateTime.of(2003, 3, 27, 15, 44, 58))
                .when(localDateTimeService).now();
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService, localDateTimeService);

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);


        //Test with known user
        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);

        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("known_keycloak_id");
        Mockito.doAnswer(i -> {
            MonitoredEndpoint arg = i.getArgument(0);
            arg.setId(44L);
            return arg;
        }).when(monitoredEndpointRepository).save(ArgumentMatchers.any());

        MonitoredEndpointDTO resultProto1 = new MonitoredEndpointDTO(
                44L, "Valid endpoint name", "https://url.com",
                LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                LocalDateTime.of(2003, 3, 27, 15, 44, 58 - 2),
                2, 43L
        );

        JwtAuthenticationToken principal1 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal1).isAuthenticated();
        Mockito.doReturn("known_keycloak_id")
                .when(principal1).getName();
        HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal1)
                .when(request1).getUserPrincipal();

        MonitoredEndpointDTO result1 = monitoredEndpointController.createEndpoint(monitoredEndpoint1, request1);
        assertEquals(resultProto1.getId(), result1.getId());
        assertEquals(resultProto1.getName(), result1.getName());
        assertEquals(resultProto1.getUrl(), result1.getUrl());
        assertEquals(resultProto1.getCreationDate(), result1.getCreationDate());
        assertEquals(resultProto1.getLastCheckDate(), result1.getLastCheckDate());
        assertEquals(resultProto1.getMonitoringInterval(), result1.getMonitoringInterval());
        assertEquals(resultProto1.getOwnerId(), result1.getOwnerId());


        //Test creating with unknown user id
        MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint();
        monitoredEndpoint2.setName("Valid endpoint name");
        monitoredEndpoint2.setUrl("https://url.com");
        monitoredEndpoint2.setMonitoringInterval(3);

        Mockito.doReturn(Collections.emptyList())
                .when(monitorUserRepository).findByKeycloakId("unknown_keycloak_id");
        Mockito.doAnswer(i -> {
            MonitorUser arg = i.getArgument(0);
            arg.setId(45L);
            return arg;
        }).when(monitorUserRepository).save(ArgumentMatchers.any());
        Mockito.doAnswer(i -> {
            MonitoredEndpoint arg = i.getArgument(0);
            arg.setId(46L);
            return arg;
        }).when(monitoredEndpointRepository).save(ArgumentMatchers.any());

        MonitoredEndpointDTO resultProto2 = new MonitoredEndpointDTO(
                46L, "Valid endpoint name", "https://url.com",
                LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                LocalDateTime.of(2003, 3, 27, 15, 44, 58 - 3),
                3, 45L
        );

        JwtAuthenticationToken principal2 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal2).isAuthenticated();
        Mockito.doReturn("unknown_keycloak_id")
                .when(principal2).getName();
        HttpServletRequest request2 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal2)
                .when(request2).getUserPrincipal();

        MonitoredEndpointDTO result2 = monitoredEndpointController.createEndpoint(monitoredEndpoint2, request2);
        assertEquals(resultProto2.getId(), result2.getId());
        assertEquals(resultProto2.getName(), result2.getName());
        assertEquals(resultProto2.getUrl(), result2.getUrl());
        assertEquals(resultProto2.getCreationDate(), result2.getCreationDate());
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
        monitorUser.setId(43L);
        monitorUser.setKeycloakId("known_keycloak_id");

        MonitorUser monitorUser2 = new MonitorUser();
        monitorUser2.setId(44L);
        monitorUser2.setKeycloakId("known_keycloak_id_2");

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);
        LocalDateTimeService localDateTimeService = Mockito.mock(LocalDateTimeService.class, defaultAnswer);
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService, localDateTimeService);

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);

        //Test update from non-owner
        JwtAuthenticationToken principal1 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal1).isAuthenticated();
        Mockito.doReturn("known_keycloak_id_2")
                .when(principal1).getName();
        HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal1)
                .when(request1).getUserPrincipal();

        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);
        monitoredEndpoint1.setId(45L);
        monitoredEndpoint1.setOwner(monitorUser);

        MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint(
                "New valid endpoint name", "https://new-valid-url.com",
                LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                LocalDateTime.of(2004, 4, 28, 16, 45, 59),
                3);
        monitoredEndpoint2.setId(46L);
        monitoredEndpoint2.setOwner(monitorUser2);


        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45L);
        Mockito.doReturn(List.of(monitorUser2))
                .when(monitorUserRepository).findByKeycloakId("known_keycloak_id_2");

        ResponseStatusException protoResult1 = new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User does not own specified endpoint");
        ResponseStatusException result1 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.updateEndpoint(45L, monitoredEndpoint2, request1)
        );
        assertEquals(protoResult1.getStatus(), result1.getStatus());
        assertEquals(protoResult1.getMessage(), result1.getMessage());

        //Test update from unknown account
        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45L);
        Mockito.doReturn(Collections.emptyList())
                .when(monitorUserRepository).findByKeycloakId("unknown_keycloak_id");
        JwtAuthenticationToken principal2 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal2).isAuthenticated();
        Mockito.doReturn("unknown_keycloak_id")
                .when(principal2).getName();
        HttpServletRequest request2 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal2)
                .when(request2).getUserPrincipal();

        ResponseStatusException protoResult2 = new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User with given Id (unknown_keycloak_id) does not exist");
        ResponseStatusException result2 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.updateEndpoint(45L, monitoredEndpoint2, request2)
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
        monitorUser.setId(43L);
        monitorUser.setKeycloakId("known_keycloak_id");

        MonitorUser monitorUser2 = new MonitorUser();
        monitorUser2.setId(44L);
        monitorUser2.setKeycloakId("known_keycloak_id_2");

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);

        LocalDateTimeService localDateTimeService = Mockito.mock(LocalDateTimeService.class, defaultAnswer);
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService, localDateTimeService);

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);


        //Test updating non-existent endpoint
        JwtAuthenticationToken principal1 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal1).isAuthenticated();
        Mockito.doReturn("known_keycloak_id")
                .when(principal1).getName();
        HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal1)
                .when(request1).getUserPrincipal();

        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);
        monitoredEndpoint1.setId(45L);
        monitoredEndpoint1.setOwner(monitorUser);

        MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint(
                "New valid endpoint name", "https://new-valid-url.com",
                LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                LocalDateTime.of(2004, 4, 28, 16, 45, 59),
                3);
        monitoredEndpoint2.setId(46L);
        monitoredEndpoint2.setOwner(monitorUser2);


        Mockito.doReturn(Optional.empty())
                .when(monitoredEndpointRepository).findById(69L);
        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("known_keycloak_id");

        ResponseStatusException protoResult1 = new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Endpoint with given Id (69) does not exist");
        ResponseStatusException result1 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.updateEndpoint(69L, monitoredEndpoint2, request1)
        );
        assertEquals(protoResult1.getStatus(), result1.getStatus());
        assertEquals(protoResult1.getMessage(), result1.getMessage());

        //Test update with invalid name
        monitoredEndpoint2.setName("");

        ResponseStatusException protoResult2 = new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "If endpoint name is provided, it must not be empty");
        ResponseStatusException result2 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.updateEndpoint(45L, monitoredEndpoint2, request1)
        );
        assertEquals(protoResult2.getStatus(), result2.getStatus());
        assertEquals(protoResult2.getMessage(), result2.getMessage());

        //Test update with invalid url
        monitoredEndpoint2.setName("New valid endpoint name");
        monitoredEndpoint2.setUrl("invalid_url");

        ResponseStatusException protoResult3 = new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "If endpoint url is provided, it must be in format '(http|https|ftp)://address'");
        ResponseStatusException result3 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.updateEndpoint(45L, monitoredEndpoint2, request1)
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
                () -> monitoredEndpointController.updateEndpoint(45L, monitoredEndpoint2, request1)
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
        monitorUser.setId(43L);
        monitorUser.setKeycloakId("known_keycloak_id");

        MonitorUser monitorUser2 = new MonitorUser();
        monitorUser2.setId(44L);
        monitorUser2.setKeycloakId("known_keycloak_id_2");

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);

        LocalDateTimeService localDateTimeService = Mockito.mock(LocalDateTimeService.class, defaultAnswer);
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService, localDateTimeService);

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);


        //Test with known user
        JwtAuthenticationToken principal1 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal1).isAuthenticated();
        Mockito.doReturn("known_keycloak_id")
                .when(principal1).getName();
        HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal1)
                .when(request1).getUserPrincipal();

        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);
        monitoredEndpoint1.setId(45L);
        monitoredEndpoint1.setOwner(monitorUser);

        MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint(
                "New valid endpoint name", "https://new-valid-url.com",
                LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                LocalDateTime.of(2004, 4, 28, 16, 45, 59),
                3);
        monitoredEndpoint2.setId(46L);
        monitoredEndpoint2.setOwner(monitorUser2);


        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45L);
        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("known_keycloak_id");

        Mockito.doAnswer(i -> i.getArgument(0))
                .when(monitoredEndpointRepository).save(ArgumentMatchers.any());

        MonitoredEndpointDTO resultProto1 = new MonitoredEndpointDTO(
                45L, "New valid endpoint name", "https://new-valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                3, 43L
        );
        MonitoredEndpointDTO result1 = monitoredEndpointController.updateEndpoint(
                45L, monitoredEndpoint2, request1);
        assertEquals(resultProto1, result1);
    }

    @Test
    void deleteMonitoredEndpointInvalid() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitorUser monitorUser = new MonitorUser();
        monitorUser.setId(43L);
        monitorUser.setKeycloakId("known_keycloak_id");

        MonitorUser monitorUser2 = new MonitorUser();
        monitorUser2.setId(44L);
        monitorUser2.setKeycloakId("known_keycloak_id_2");

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);

        LocalDateTimeService localDateTimeService = Mockito.mock(LocalDateTimeService.class, defaultAnswer);
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService, localDateTimeService);

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);


        // Unknown endpoint
        JwtAuthenticationToken principal1 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal1).isAuthenticated();
        Mockito.doReturn("known_keycloak_id")
                .when(principal1).getName();
        HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal1)
                .when(request1).getUserPrincipal();

        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);
        monitoredEndpoint1.setId(45L);
        monitoredEndpoint1.setOwner(monitorUser);

        Mockito.doReturn(Optional.empty())
                .when(monitoredEndpointRepository).findById(69L);
        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("known_keycloak_id");

        Mockito.doNothing().when(monitoredEndpointRepository).deleteById(ArgumentMatchers.<Long>any());

        ResponseStatusException protoResult1 = new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Endpoint with given Id (69) does not exist");
        ResponseStatusException result1 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.deleteMonitoredEndpoint(69L, request1)
        );
        assertEquals(protoResult1.getStatus(), result1.getStatus());
        assertEquals(protoResult1.getMessage(), result1.getMessage());

        // Unknown user
        JwtAuthenticationToken principal2 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal2).isAuthenticated();
        Mockito.doReturn("unknown_keycloak_id")
                .when(principal2).getName();
        HttpServletRequest request2 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal2)
                .when(request2).getUserPrincipal();

        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45L);
        Mockito.doReturn(Collections.emptyList())
                .when(monitorUserRepository).findByKeycloakId("unknown_keycloak_id");

        Mockito.doNothing().when(monitoredEndpointRepository).deleteById(ArgumentMatchers.<Long>any());

        ResponseStatusException protoResult2 = new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User with given Id (unknown_keycloak_id) does not exist");
        ResponseStatusException result2 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.deleteMonitoredEndpoint(45L, request2)
        );
        assertEquals(protoResult2.getStatus(), result2.getStatus());
        assertEquals(protoResult2.getMessage(), result2.getMessage());

        // Non-owner
        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45L);
        Mockito.doReturn(List.of(monitorUser2))
                .when(monitorUserRepository).findByKeycloakId("known_keycloak_id_2");

        JwtAuthenticationToken principal3 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal3).isAuthenticated();
        Mockito.doReturn("known_keycloak_id_2")
                .when(principal3).getName();
        HttpServletRequest request3 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal3)
                .when(request3).getUserPrincipal();

        Mockito.doNothing().when(monitoredEndpointRepository).deleteById(ArgumentMatchers.<Long>any());

        ResponseStatusException protoResult3 = new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User does not own specified endpoint");
        ResponseStatusException result3 = assertThrows(
                ResponseStatusException.class,
                () -> monitoredEndpointController.deleteMonitoredEndpoint(45L, request3)
        );
        assertEquals(protoResult3.getStatus(), result3.getStatus());
        assertEquals(protoResult3.getMessage(), result3.getMessage());
    }

    @Test
    void deleteMonitoredEndpointAllRight() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"
                )
        );

        MonitorUser monitorUser = new MonitorUser();
        monitorUser.setId(43L);
        monitorUser.setKeycloakId("known_keycloak_id");


        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);

        LocalDateTimeService localDateTimeService = Mockito.mock(LocalDateTimeService.class, defaultAnswer);
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService, localDateTimeService);

        MonitoredEndpointController monitoredEndpointController =
                new MonitoredEndpointController(monitoredEndpointService);


        //Test with correct user
        JwtAuthenticationToken principal1 = Mockito.mock(JwtAuthenticationToken.class, defaultAnswer);
        Mockito.doReturn(true)
                .when(principal1).isAuthenticated();
        Mockito.doReturn("known_keycloak_id")
                .when(principal1).getName();
        HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class, defaultAnswer);
        Mockito.doReturn(principal1)
                .when(request1).getUserPrincipal();

        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2);
        monitoredEndpoint1.setId(45L);
        monitoredEndpoint1.setOwner(monitorUser);

        Mockito.doReturn(Optional.of(monitoredEndpoint1))
                .when(monitoredEndpointRepository).findById(45L);
        Mockito.doReturn(List.of(monitorUser))
                .when(monitorUserRepository).findByKeycloakId("known_keycloak_id");

        Mockito.doNothing().when(monitoredEndpointRepository).deleteById(ArgumentMatchers.<Long>any());

        MonitoredEndpointDTO resultProto1 = new MonitoredEndpointDTO(
                45L, "Valid endpoint name", "https://valid-url.com",
                LocalDateTime.of(2001, 1, 25, 13, 42, 56),
                LocalDateTime.of(2002, 2, 26, 14, 43, 57),
                2, 43L
        );
        MonitoredEndpointDTO result1 =
                monitoredEndpointController.deleteMonitoredEndpoint(45L, request1);
        assertEquals(resultProto1, result1);
    }
}
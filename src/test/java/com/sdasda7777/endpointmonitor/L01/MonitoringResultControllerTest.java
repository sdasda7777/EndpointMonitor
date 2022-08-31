package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoringResultDTO;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoringResult;
import com.sdasda7777.endpointmonitor.L02.MonitorUserService;
import com.sdasda7777.endpointmonitor.L02.MonitoredEndpointService;
import com.sdasda7777.endpointmonitor.L02.MonitoringResultService;
import com.sdasda7777.endpointmonitor.L03.MonitorUserRepository;
import com.sdasda7777.endpointmonitor.L03.MonitoredEndpointRepository;
import com.sdasda7777.endpointmonitor.L03.MonitoringResultRepository;
import com.sdasda7777.endpointmonitor.security.authentication.KeycloakUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.springframework.http.HttpStatus;
import org.springframework.jmx.access.InvalidInvocationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class MonitoringResultControllerTest {

    @Test
    void getMonitoringResultsNoKeycloakId(){
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService);

        MonitoringResultRepository monitoringResultRepository =
                Mockito.mock(MonitoringResultRepository.class, defaultAnswer);
        MonitoringResultService monitoringResultService =
                new MonitoringResultService(monitoringResultRepository, monitoredEndpointService, monitorUserService);

        KeycloakUserService keycloakUserService =
                Mockito.mock(KeycloakUserService.class, defaultAnswer);

        MonitoringResultController monitoringResultController =
                new MonitoringResultController(monitoringResultService, keycloakUserService);

        // No keycloak Id
        Mockito.doReturn(null)
                .when(keycloakUserService).getUserId();

        ResponseStatusException protoResult1 = new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Authorization token must be provided");
        ResponseStatusException result1 = assertThrows(
                ResponseStatusException.class,
                () -> monitoringResultController.getMonitoringResults(69l, null)
        );
        assertEquals(protoResult1.getStatus(), result1.getStatus());
        assertEquals(protoResult1.getMessage(), result1.getMessage());
    }

    @Test
    void getMonitoringResults() {
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitorUser monitorUser1 = new MonitorUser();
        monitorUser1.setId(43l);
        monitorUser1.setKeycloakId("known_keycloakid1");

        MonitorUser monitorUser2 = new MonitorUser();
        monitorUser2.setId(44l);
        monitorUser2.setKeycloakId("known_keycloakid2");

        MonitoredEndpoint monitoredEndpoint = new MonitoredEndpoint(
            "Valid name", "https://valid-url.com",
            LocalDateTime.of(2001, 1, 25, 13, 42, 56),
            LocalDateTime.of(2002, 2, 26, 14, 43, 57),
            3
        );
        monitoredEndpoint.setId(45l);
        monitoredEndpoint.setOwner(monitorUser1);

        MonitoringResult monitoringResult1 = new MonitoringResult(
                LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                200, "Yup, this is a webpage"
        );
        monitoringResult1.setId(46l);
        monitoringResult1.setMonitoredEndpoint(monitoredEndpoint);
        MonitoringResult monitoringResult2 = new MonitoringResult(
                LocalDateTime.of(2004, 4, 28, 16, 45, 59),
                404, "Sorry, not found"
        );
        monitoringResult2.setId(47l);
        monitoringResult2.setMonitoredEndpoint(monitoredEndpoint);

        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        Mockito.doReturn(Optional.of(monitoredEndpoint))
                .when(monitoredEndpointRepository).findById(45l);
        Mockito.doReturn(Optional.empty())
                .when(monitoredEndpointRepository).findById(69l);
        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);
        Mockito.doReturn(List.of(monitorUser1))
                .when(monitorUserRepository).findByKeycloakId("known_keycloakid1");
        Mockito.doReturn(List.of(monitorUser2))
                .when(monitorUserRepository).findByKeycloakId("known_keycloakid2");
        Mockito.doReturn(Collections.emptyList())
                .when(monitorUserRepository).findByKeycloakId("unknown_keycloakid");
        MonitorUserService monitorUserService = new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository, monitorUserService);

        MonitoringResultRepository monitoringResultRepository =
                Mockito.mock(MonitoringResultRepository.class, defaultAnswer);
        Mockito.doReturn(new ArrayList<>( Arrays.asList(monitoringResult2, monitoringResult1) ))
                .when(monitoringResultRepository).getAllForEndpoint(45l);

        MonitoringResultService monitoringResultService =
                new MonitoringResultService(monitoringResultRepository, monitoredEndpointService, monitorUserService);

        KeycloakUserService keycloakUserService =
                Mockito.mock(KeycloakUserService.class, defaultAnswer);

        MonitoringResultController monitoringResultController =
                new MonitoringResultController(monitoringResultService, keycloakUserService);

        MonitoringResultDTO monitoringResultDTO1 = new MonitoringResultDTO(
                46l, LocalDateTime.of(2003, 3, 27, 15, 44, 58),
                200, "Yup, this is a webpage",
                45l
        );
        MonitoringResultDTO monitoringResultDTO2 = new MonitoringResultDTO(
                47l, LocalDateTime.of(2004, 4, 28, 16, 45, 59),
                404, "Sorry, not found",
                45l
        );

        // Unknown endpoint
        Mockito.doReturn("known_keycloakid1")
                .when(keycloakUserService).getUserId();

        ResponseStatusException protoResult1 = new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Endpoint with given Id (69) does not exist");
        ResponseStatusException result1 = assertThrows(
                ResponseStatusException.class,
                () -> monitoringResultController.getMonitoringResults(69l, null)
        );
        assertEquals(protoResult1.getStatus(), result1.getStatus());
        assertEquals(protoResult1.getMessage(), result1.getMessage());

        // Unknown user
        Mockito.doReturn("unknown_keycloakid")
                .when(keycloakUserService).getUserId();

        ResponseStatusException protoResult2 = new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User with given Id (unknown_keycloakid) does not exist");
        ResponseStatusException result2 = assertThrows(
                ResponseStatusException.class,
                () -> monitoringResultController.getMonitoringResults(45l, null)
        );
        assertEquals(protoResult2.getStatus(), result2.getStatus());
        assertEquals(protoResult2.getMessage(), result2.getMessage());

        // Non-owner user
        Mockito.doReturn("known_keycloakid2")
                .when(keycloakUserService).getUserId();

        ResponseStatusException protoResult3 = new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User does not own specified endpoint");
        ResponseStatusException result3 = assertThrows(
                ResponseStatusException.class,
                () -> monitoringResultController.getMonitoringResults(45l, null)
        );
        assertEquals(protoResult3.getStatus(), result3.getStatus());
        assertEquals(protoResult3.getMessage(), result3.getMessage());

        // Known user without limit
        Mockito.doReturn("known_keycloakid1")
                .when(keycloakUserService).getUserId();

        ArrayList<MonitoringResultDTO> result4 =
            monitoringResultController.getMonitoringResults(45l, null)
                    .stream().collect(Collectors.toCollection(ArrayList::new));
        assertEquals(2, result4.size());
        assertEquals(monitoringResultDTO2, result4.get(0));
        assertEquals(monitoringResultDTO1, result4.get(1));

        // Known user with limit smaller than result count
        ArrayList<MonitoringResultDTO> result5 =
                monitoringResultController.getMonitoringResults(45l, 1l)
                        .stream().collect(Collectors.toCollection(ArrayList::new));
        assertEquals(1, result5.size());
        MonitoringResultDTO result5DTO = result5.get(0);
        assertEquals(monitoringResultDTO2.getId(), result5DTO.getId());
        assertEquals(monitoringResultDTO2.getCheckDate(), result5DTO.getCheckDate());
        assertEquals(monitoringResultDTO2.getResultStatusCode(), result5DTO.getResultStatusCode());
        assertEquals(monitoringResultDTO2.getResultPayload(), result5DTO.getResultPayload());
        assertEquals(monitoringResultDTO2.getMonitoredEndpointId(), result5DTO.getMonitoredEndpointId());

        // Known user with limit larger than result count
        ArrayList<MonitoringResultDTO> result6 =
                monitoringResultController.getMonitoringResults(45l, 200l)
                        .stream().collect(Collectors.toCollection(ArrayList::new));
        assertEquals(2, result6.size());
        assertEquals(monitoringResultDTO2, result6.get(0));
        assertEquals(monitoringResultDTO1, result6.get(1));
    }
}
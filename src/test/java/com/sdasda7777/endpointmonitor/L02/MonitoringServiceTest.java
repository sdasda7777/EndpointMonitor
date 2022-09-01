package com.sdasda7777.endpointmonitor.L02;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoringResult;
import com.sdasda7777.endpointmonitor.L03.MonitorUserRepository;
import com.sdasda7777.endpointmonitor.L03.MonitoredEndpointRepository;
import com.sdasda7777.endpointmonitor.L03.MonitoringResultRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.springframework.jmx.access.InvalidInvocationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class MonitoringServiceTest {
    @Test
    void standardFlowTest(){
        var defaultAnswer = new ThrowsException(
                new InvalidInvocationException(
                        "Inappropriate usage of mocked object"));

        MonitorUser monitorUser = new MonitorUser();
        monitorUser.setId(43l);
        monitorUser.setKeycloakId("known_keycloakid");

        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
                "Valid endpoint", "https://www.google.com/",
                LocalDateTime.now(), LocalDateTime.MIN,
                5
        );
        monitoredEndpoint1.setId(45l);
        monitoredEndpoint1.setOwner(monitorUser);

        MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint(
                "Invalid endpoint", "https://non-existant-page.com/",
                LocalDateTime.now(), LocalDateTime.MIN,
                7
        );
        monitoredEndpoint2.setId(46l);
        monitoredEndpoint2.setOwner(monitorUser);

        class ArrayListAddWrapper {
            static ArrayList<MonitoringResult> results = new ArrayList<>();

            public static boolean add(MonitoringResult me){
                return results.add(me);
            }

            public static ArrayList<MonitoringResult> get(){
                return results;
            }
        }

        MonitorUserRepository monitorUserRepository =
                Mockito.mock(MonitorUserRepository.class, defaultAnswer);
        MonitoredEndpointRepository monitoredEndpointRepository =
                Mockito.mock(MonitoredEndpointRepository.class, defaultAnswer);
        Mockito.doReturn(new ArrayList<>(Arrays.asList(monitoredEndpoint1,
                                                        monitoredEndpoint2) ))
                .when(monitoredEndpointRepository).getRequiringUpdate();
        Mockito.doAnswer(i->{
            ((MonitoredEndpoint)i.getArgument(0))
                    .setLastCheckDate(i.getArgument(1));
            return i.getArgument(0);
        })
                .when(monitoredEndpointRepository).updateEndpointLastCheck(
                        ArgumentMatchers.<MonitoredEndpoint>any(),
                        ArgumentMatchers.<LocalDateTime>any()
                );
        MonitoringResultRepository monitoringResultRepository =
                Mockito.mock(MonitoringResultRepository.class, defaultAnswer);
        Mockito.doAnswer(i -> {
            ArrayListAddWrapper.add(i.getArgument(0));
            return i.getArgument(0);
        })
                .when(monitoringResultRepository).save(
                        ArgumentMatchers.<MonitoringResult>any());
        Mockito.doAnswer(i -> {
            return ArrayListAddWrapper.get();
        })
                .when(monitoredEndpointRepository).findAll();

        LocalDateTimeService localDateTimeService = Mockito.mock(LocalDateTimeService.class, defaultAnswer);
        Mockito.doReturn(LocalDateTime.of(2003, 3, 27, 15, 44, 58))
                .when(localDateTimeService).now();
        MonitorUserService monitorUserService =
                new MonitorUserService(monitorUserRepository);
        MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(monitoredEndpointRepository,
                                            monitorUserService,
                                            localDateTimeService);
        MonitoringResultService monitoringResultService =
                new MonitoringResultService(monitoringResultRepository,
                                            monitoredEndpointService,
                                            monitorUserService);

        MonitoringService monitoringService =
                new MonitoringService(monitoredEndpointService, monitoringResultService);

        try {
            monitoringService.checkEndpoints();
            monitoringService.awaitEndpointsChecked();

            ArrayListAddWrapper.get().sort((lhs, rhs) -> {
                if(lhs.getMonitoredEndpoint().getId() >
                        rhs.getMonitoredEndpoint().getId()){
                    return 1;
                }else if(lhs.getMonitoredEndpoint().getId() <
                        rhs.getMonitoredEndpoint().getId()){
                    return -1;
                }else{
                    return 0;
                }
            });
            assertEquals(2, ArrayListAddWrapper.get().size());
            //Note: this test will have to be rewritten if google ever goes out of business
            assertEquals(200,
                    ArrayListAddWrapper.get().get(0).getResultStatusCode());
            assertEquals(599,
                    ArrayListAddWrapper.get().get(1).getResultStatusCode());
        } catch (InterruptedException e) {
            assert(false);
        }
    }
}

package com.sdasda7777.endpointmonitor.layer2;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitorUser;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult;
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
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class MonitoringServiceTest
{
	@Test
	void standardFlowTest()
	{
		var defaultAnswer = new ThrowsException(new InvalidInvocationException(
				"Inappropriate usage of mocked object"));

		MonitorUser monitorUser = new MonitorUser("known_keycloak_id");
		monitorUser.setId(43L);

		MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
				"Valid endpoint", "https://www.google.com/",
				LocalDateTime.now(),
				5
		);
		monitoredEndpoint1.setId(45L);
		monitoredEndpoint1.setOwner(monitorUser);

		MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint(
				"Invalid endpoint", "https://non-existant-page.com/",
				LocalDateTime.now(),
				7
		);
		monitoredEndpoint2.setId(46L);
		monitoredEndpoint2.setOwner(monitorUser);

		class ArrayListAddWrapper
		{
			static final ArrayList<MonitoringResult> results =
                    new ArrayList<>();

			public static void add(MonitoringResult me)
			{
				results.add(me);
			}

			public static ArrayList<MonitoringResult> get()
			{
				return results;
			}
		}

		MonitorUserRepository monitorUserRepository = Mockito.mock(
				MonitorUserRepository.class, defaultAnswer);
		MonitoredEndpointRepository monitoredEndpointRepository = Mockito.mock(
				MonitoredEndpointRepository.class, defaultAnswer);
		Mockito.doReturn(new ArrayList<>(
				Arrays.asList(monitoredEndpoint1, monitoredEndpoint2))).when(
				monitoredEndpointRepository).getRequiringUpdate();
		Mockito.doAnswer(i ->
						 {
							 ((MonitoredEndpoint) i.getArgument(
									 0)).setLastCheckDate(i.getArgument(1));
							 return i.getArgument(0);
						 }).when(
				monitoredEndpointRepository).updateEndpointLastCheck(
				ArgumentMatchers.any(), ArgumentMatchers.any());
		MonitoringResultRepository monitoringResultRepository = Mockito.mock(
				MonitoringResultRepository.class, defaultAnswer);
		Mockito.doAnswer(i ->
						 {
							 ArrayListAddWrapper.add(i.getArgument(0));
							 return i.getArgument(0);
						 }).when(monitoringResultRepository).save(
				ArgumentMatchers.any());
		Mockito.doAnswer(i -> ArrayListAddWrapper.get()).when(
				monitoredEndpointRepository).findAll();

		LocalDateTimeService localDateTimeService = Mockito.mock(
				LocalDateTimeService.class, defaultAnswer);
		Mockito.doReturn(LocalDateTime.of(2003, 3, 27, 15, 44, 58)).when(
				localDateTimeService).now();
		MonitorUserService monitorUserService = new MonitorUserService(
				monitorUserRepository);
		MonitoredEndpointService monitoredEndpointService =
                new MonitoredEndpointService(
				monitoredEndpointRepository, monitorUserService,
				localDateTimeService
		);
		MonitoringResultService monitoringResultService =
                new MonitoringResultService(
				monitoringResultRepository, monitoredEndpointService,
				monitorUserService
		);

		MonitoringService monitoringService = new MonitoringService(
				monitoredEndpointService, monitoringResultService);

		try
		{
			monitoringService.checkEndpoints();
			monitoringService.awaitEndpointsChecked();

			ArrayListAddWrapper.get().sort(Comparator.comparing(
					lhs -> lhs.getMonitoredEndpoint().getId()));
			assertEquals(2, ArrayListAddWrapper.get().size());
			//Note: this test will have to be rewritten if Google ever goes
            // out of business
			assertEquals(200,
						 ArrayListAddWrapper.get().get(0).getResultStatusCode()
			);
			assertEquals(599,
						 ArrayListAddWrapper.get().get(1).getResultStatusCode()
			);
		}
		catch (InterruptedException e)
		{
			assert (false);
		}
	}
}

package com.sdasda7777.endpointmonitor.layer2;

import com.sdasda7777.endpointmonitor.L03.MonitorUserRepository;
import com.sdasda7777.endpointmonitor.L03.MonitoredEndpointRepository;
import com.sdasda7777.endpointmonitor.L03.MonitoringResultRepository;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitorUser;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.springframework.jmx.access.InvalidInvocationException;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpResponse;
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
				LocalDateTime.now(), 5
		);
		monitoredEndpoint1.setId(45L);
		monitoredEndpoint1.setOwner(monitorUser);

		MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint(
				"Invalid endpoint", "https://non-existant-page.com/",
				LocalDateTime.now(), 7
		);
		monitoredEndpoint2.setId(46L);
		monitoredEndpoint2.setOwner(monitorUser);


		ArrayList<MonitoringResult> monitoringResultsList = new ArrayList<>();

		MonitorUserRepository monitorUserRepository = Mockito.mock(
				MonitorUserRepository.class, defaultAnswer);
		MonitoredEndpointRepository monitoredEndpointRepository = Mockito.mock(
				MonitoredEndpointRepository.class, defaultAnswer);
		Mockito.doReturn(new ArrayList<>(
				Arrays.asList(monitoredEndpoint1, monitoredEndpoint2))).when(
				monitoredEndpointRepository).getRequiringUpdate();
		MonitoringResultRepository monitoringResultRepository = Mockito.mock(
				MonitoringResultRepository.class, defaultAnswer);
		Mockito.doAnswer(i ->
						 {
							 monitoringResultsList.add(i.getArgument(0));
							 return i.getArgument(0);
						 }).when(monitoringResultRepository).save(
				ArgumentMatchers.any());
		Mockito.doAnswer(i -> monitoringResultsList).when(
				monitoredEndpointRepository).findAll();
		Mockito.doAnswer(i -> i.getArgument(0)).when(
				monitoredEndpointRepository).save(ArgumentMatchers.any());

		InternetRequestService internetRequestService = Mockito.mock(
				InternetRequestService.class, defaultAnswer);
		try{
			HttpResponse<String> r1 = Mockito.mock(HttpResponse.class, defaultAnswer);

			Mockito.doReturn(200).when(r1).statusCode();
			Mockito.doReturn("<h1>Google</h1>").when(r1).body();

			Mockito.doReturn(r1)
				   .when(internetRequestService).makeRequest("https://www.google.com/");
			Mockito.doThrow(new ConnectException("???"))
				   .when(internetRequestService).makeRequest("https://non-existant-page.com/");
		}
		// Why would you make `.when()` return T???
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}


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

		MonitoringService monitoringService = new MonitoringService(0,
																	internetRequestService,
																	monitoredEndpointService,
																	monitoringResultService
		);

		try
		{
			// Exceptions within threads don't fail tests,
			// so the only indicator anything is wrong are the obtained values
			monitoringService.checkEndpoints();
			monitoringService.awaitEndpointsChecked();

			monitoringResultsList.sort(Comparator.comparing(
					lhs -> lhs.getMonitoredEndpoint().getId()));
			assertEquals(2, monitoringResultsList.size());
			assertEquals(200,
						 monitoringResultsList.get(0).getResultStatusCode()
			);
			assertEquals("<h1>Google</h1>",
						 monitoringResultsList.get(0).getResultPayload()
			);
			assertEquals(599,
						 monitoringResultsList.get(1).getResultStatusCode()
			);
			assertEquals("Connection to server hosting URL 'https://non-existant-page.com/' (if such server exists) could not be established.",
						 monitoringResultsList.get(1).getResultPayload()
			);
		}
		catch (InterruptedException e)
		{
			assert(false);
		}
	}
}

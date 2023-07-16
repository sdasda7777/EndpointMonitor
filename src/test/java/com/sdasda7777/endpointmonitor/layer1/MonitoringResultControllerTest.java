package com.sdasda7777.endpointmonitor.layer1;

import com.sdasda7777.endpointmonitor.layer3.MonitorUserRepository;
import com.sdasda7777.endpointmonitor.layer3.MonitoredEndpointRepository;
import com.sdasda7777.endpointmonitor.layer3.MonitoringResultRepository;
import com.sdasda7777.endpointmonitor.layer1.dto.MonitoringResultDTO;
import com.sdasda7777.endpointmonitor.layer2.LocalDateTimeService;
import com.sdasda7777.endpointmonitor.layer2.MonitorUserService;
import com.sdasda7777.endpointmonitor.layer2.MonitoredEndpointService;
import com.sdasda7777.endpointmonitor.layer2.MonitoringResultService;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitorUser;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jmx.access.InvalidInvocationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
class MonitoringResultControllerTest
{

	@Test
	void getMonitoringResultsNoKeycloakId()
	{
		var defaultAnswer = new ThrowsException(new InvalidInvocationException(
				"Inappropriate usage of mocked object"));

		MonitoredEndpointRepository monitoredEndpointRepository = Mockito.mock(
				MonitoredEndpointRepository.class, defaultAnswer);
		MonitorUserRepository monitorUserRepository = Mockito.mock(
				MonitorUserRepository.class, defaultAnswer);

		LocalDateTimeService localDateTimeService = Mockito.mock(
				LocalDateTimeService.class, defaultAnswer);
		MonitorUserService monitorUserService = new MonitorUserService(
				monitorUserRepository);
		MonitoredEndpointService monitoredEndpointService =
				new MonitoredEndpointService(
				monitoredEndpointRepository, monitorUserService,
				localDateTimeService
		);

		MonitoringResultRepository monitoringResultRepository = Mockito.mock(
				MonitoringResultRepository.class, defaultAnswer);
		MonitoringResultService monitoringResultService =
				new MonitoringResultService(
				monitoringResultRepository, monitoredEndpointService,
				monitorUserService
		);

		MonitoringResultController monitoringResultController =
				new MonitoringResultController(
				monitoringResultService);

		// No keycloak Id
		HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class,
												   defaultAnswer
		);
		Mockito.doReturn(null).when(request1).getUserPrincipal();

		ResponseStatusException protoResult1 = new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"Authorization token must be " + "provided"
		);
		ResponseStatusException result1 = assertThrows(
				ResponseStatusException.class,
				() -> monitoringResultController.getMonitoringResults(69L,
																	  null,
																	  request1
				)
		);
		assertEquals(protoResult1.getStatus(), result1.getStatus());
		assertEquals(protoResult1.getMessage(), result1.getMessage());
	}

	@Test
	void getMonitoringResults()
	{
		var defaultAnswer = new ThrowsException(new InvalidInvocationException(
				"Inappropriate usage of mocked object"));

		MonitorUser monitorUser1 = new MonitorUser("known_keycloak_id_1");
		monitorUser1.setId(43L);

		MonitorUser monitorUser2 = new MonitorUser("known_keycloak_id_2");
		monitorUser2.setId(44L);

		MonitoredEndpoint monitoredEndpoint = new MonitoredEndpoint(
				"Valid name", "https://valid-url.com",
				LocalDateTime.of(2001, 1, 25, 13, 42, 56), 3
		);
		monitoredEndpoint.setId(45L);
		monitoredEndpoint.setOwner(monitorUser1);

		MonitoringResult monitoringResult1 = new MonitoringResult(
				LocalDateTime.of(2003, 3, 27, 15, 44, 58), monitoredEndpoint,
				monitoredEndpoint.getUrl(), 200, "Yup, this is a webpage"
		);
		monitoringResult1.setId(46L);
		MonitoringResult monitoringResult2 = new MonitoringResult(
				LocalDateTime.of(2004, 4, 28, 16, 45, 59), monitoredEndpoint,
				monitoredEndpoint.getUrl(), 404, "Sorry, not found"
		);
		monitoringResult2.setId(47L);

		MonitoredEndpointRepository monitoredEndpointRepository = Mockito.mock(
				MonitoredEndpointRepository.class, defaultAnswer);
		Mockito.doReturn(Optional.of(monitoredEndpoint)).when(
				monitoredEndpointRepository).findById(45L);
		Mockito.doReturn(Optional.empty()).when(
				monitoredEndpointRepository).findById(69L);
		MonitorUserRepository monitorUserRepository = Mockito.mock(
				MonitorUserRepository.class, defaultAnswer);
		Mockito.doReturn(List.of(monitorUser1)).when(
				monitorUserRepository).findByAuthorizationId("known_keycloak_id_1");
		Mockito.doReturn(List.of(monitorUser2)).when(
				monitorUserRepository).findByAuthorizationId("known_keycloak_id_2");
		Mockito.doReturn(Collections.emptyList()).when(
				monitorUserRepository).findByAuthorizationId("unknown_keycloak_id");

		LocalDateTimeService localDateTimeService = Mockito.mock(
				LocalDateTimeService.class, defaultAnswer);
		MonitorUserService monitorUserService = new MonitorUserService(
				monitorUserRepository);
		MonitoredEndpointService monitoredEndpointService =
				new MonitoredEndpointService(
				monitoredEndpointRepository, monitorUserService,
				localDateTimeService
		);

		MonitoringResultRepository monitoringResultRepository = Mockito.mock(
				MonitoringResultRepository.class, defaultAnswer);
		Mockito.doReturn(new ArrayList<>(
				Arrays.asList(monitoringResult2, monitoringResult1))).when(
				monitoringResultRepository).getAllForEndpoint(45L,
															  Pageable.unpaged()
		);
		Mockito.doReturn(
				new ArrayList<>(List.of(monitoringResult2))).when(
				monitoringResultRepository).getAllForEndpoint(45L,
															  PageRequest.of(0,
																			 1)
		);
		Mockito.doReturn(
				new ArrayList<>(
						Arrays.asList(monitoringResult2, monitoringResult1))).when(
				monitoringResultRepository).getAllForEndpoint(45L,
															  PageRequest.of(0,
																			 200)
		);

		MonitoringResultService monitoringResultService =
				new MonitoringResultService(
				monitoringResultRepository, monitoredEndpointService,
				monitorUserService
		);

		MonitoringResultController monitoringResultController =
				new MonitoringResultController(
				monitoringResultService);

		MonitoringResultDTO monitoringResultDTO1 = new MonitoringResultDTO(46L,
																		   LocalDateTime.of(
																				   2003,
																				   3,
																				   27,
																				   15,
																				   44,
																				   58
																		   ),
																		   45L,
																		   "https://valid-url.com",
																		   200,
																		   "Yup, this is a webpage"

		);
		MonitoringResultDTO monitoringResultDTO2 = new MonitoringResultDTO(47L,
																		   LocalDateTime.of(
																				   2004,
																				   4,
																				   28,
																				   16,
																				   45,
																				   59
																		   ),
																		   45L,
																		   "https://valid-url.com",
																		   404,
																		   "Sorry, not found"
		);

		// Unknown endpoint
		JwtAuthenticationToken principal1 = Mockito.mock(
				JwtAuthenticationToken.class, defaultAnswer);
		Mockito.doReturn(true).when(principal1).isAuthenticated();
		Mockito.doReturn("known_keycloak_id_1").when(principal1).getName();
		HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class,
												   defaultAnswer
		);
		Mockito.doReturn(principal1).when(request1).getUserPrincipal();

		ResponseStatusException protoResult1 = new ResponseStatusException(
				HttpStatus.NOT_FOUND,
				"Endpoint with given Id (69) does not exist"
		);
		ResponseStatusException result1 = assertThrows(
				ResponseStatusException.class,
				() -> monitoringResultController.getMonitoringResults(69L,
																	  null,
																	  request1
				)
		);
		assertEquals(protoResult1.getStatus(), result1.getStatus());
		assertEquals(protoResult1.getMessage(), result1.getMessage());

		// Unknown user
		JwtAuthenticationToken principal2 = Mockito.mock(
				JwtAuthenticationToken.class, defaultAnswer);
		Mockito.doReturn(true).when(principal2).isAuthenticated();
		Mockito.doReturn("unknown_keycloak_id").when(principal2).getName();
		HttpServletRequest request2 = Mockito.mock(HttpServletRequest.class,
												   defaultAnswer
		);
		Mockito.doReturn(principal2).when(request2).getUserPrincipal();

		ResponseStatusException protoResult2 = new ResponseStatusException(
				HttpStatus.NOT_FOUND,
				"User with given Id (unknown_keycloak_id) does not exist"
		);
		ResponseStatusException result2 = assertThrows(
				ResponseStatusException.class,
				() -> monitoringResultController.getMonitoringResults(45L,
																	  null,
																	  request2
				)
		);
		assertEquals(protoResult2.getStatus(), result2.getStatus());
		assertEquals(protoResult2.getMessage(), result2.getMessage());

		// Non-owner user
		JwtAuthenticationToken principal3 = Mockito.mock(
				JwtAuthenticationToken.class, defaultAnswer);
		Mockito.doReturn(true).when(principal3).isAuthenticated();
		Mockito.doReturn("known_keycloak_id_2").when(principal3).getName();
		HttpServletRequest request3 = Mockito.mock(HttpServletRequest.class,
												   defaultAnswer
		);
		Mockito.doReturn(principal3).when(request3).getUserPrincipal();

		ResponseStatusException protoResult3 = new ResponseStatusException(
				HttpStatus.UNAUTHORIZED,
				"User does not own specified endpoint"
		);
		ResponseStatusException result3 = assertThrows(
				ResponseStatusException.class,
				() -> monitoringResultController.getMonitoringResults(45L,
																	  null,
																	  request3
				)
		);
		assertEquals(protoResult3.getStatus(), result3.getStatus());
		assertEquals(protoResult3.getMessage(), result3.getMessage());

		// Known user without limit
		JwtAuthenticationToken principal4 = Mockito.mock(
				JwtAuthenticationToken.class, defaultAnswer);
		Mockito.doReturn(true).when(principal4).isAuthenticated();
		Mockito.doReturn("known_keycloak_id_1").when(principal4).getName();
		HttpServletRequest request4 = Mockito.mock(HttpServletRequest.class,
												   defaultAnswer
		);
		Mockito.doReturn(principal4).when(request4).getUserPrincipal();

		ArrayList<MonitoringResultDTO> result4 = new ArrayList<>(
				monitoringResultController.getMonitoringResults(45L, null,
																request4
				));
		assertEquals(2, result4.size());
		assertEquals(monitoringResultDTO2, result4.get(0));
		assertEquals(monitoringResultDTO1, result4.get(1));

		// Known user with limit smaller than result count
		ArrayList<MonitoringResultDTO> result5 = new ArrayList<>(
				monitoringResultController.getMonitoringResults(45L, 1,
																request4
				));
		assertEquals(1, result5.size());
		MonitoringResultDTO result5DTO = result5.get(0);
		assertEquals(monitoringResultDTO2.getId(), result5DTO.getId());
		assertEquals(monitoringResultDTO2.getCheckDate(),
					 result5DTO.getCheckDate()
		);
		assertEquals(monitoringResultDTO2.getResultStatusCode(),
					 result5DTO.getResultStatusCode()
		);
		assertEquals(monitoringResultDTO2.getResultPayload(),
					 result5DTO.getResultPayload()
		);
		assertEquals(monitoringResultDTO2.getMonitoredEndpointId(),
					 result5DTO.getMonitoredEndpointId()
		);

		// Known user with limit larger than result count
		ArrayList<MonitoringResultDTO> result6 = new ArrayList<>(
				monitoringResultController.getMonitoringResults(45L, 200,
																request4
				));
		assertEquals(2, result6.size());
		assertEquals(monitoringResultDTO2, result6.get(0));
		assertEquals(monitoringResultDTO1, result6.get(1));
	}
}
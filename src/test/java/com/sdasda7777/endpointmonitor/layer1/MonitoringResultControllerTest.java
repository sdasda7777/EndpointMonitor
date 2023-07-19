package com.sdasda7777.endpointmonitor.layer1;

import com.sdasda7777.endpointmonitor.layer1.dto.MonitoringResultDTO;
import com.sdasda7777.endpointmonitor.layer2.LocalDateTimeService;
import com.sdasda7777.endpointmonitor.layer2.MonitorUserService;
import com.sdasda7777.endpointmonitor.layer2.MonitoredEndpointService;
import com.sdasda7777.endpointmonitor.layer2.MonitoringResultService;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitorUser;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult;
import com.sdasda7777.endpointmonitor.layer3.MonitorUserRepository;
import com.sdasda7777.endpointmonitor.layer3.MonitoredEndpointRepository;
import com.sdasda7777.endpointmonitor.layer3.MonitoringResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
	private ThrowsException defaultAnswer;

	private JwtAuthenticationToken principal43;
	private JwtAuthenticationToken principal44;
	private JwtAuthenticationToken principalUnknown;

	private MonitorUser monitorUser43;
	private MonitorUser monitorUser44;
	private MonitoredEndpoint monitoredEndpoint53;
	private MonitoringResult monitoringResult63;
	private MonitoringResult monitoringResult64;

	private MonitoringResultDTO monitoringResultDTO1;
	private MonitoringResultDTO monitoringResultDTO2;

	private MonitoredEndpointRepository monitoredEndpointRepository;
	private MonitorUserRepository monitorUserRepository;

	private LocalDateTimeService localDateTimeService;
	private MonitorUserService monitorUserService;
	private MonitoredEndpointService monitoredEndpointService;
	private MonitoringResultRepository monitoringResultRepository;
	private MonitoringResultService monitoringResultService;

	@BeforeEach
	void setup()
	{
		defaultAnswer = new ThrowsException(new InvalidInvocationException(
				"Inappropriate usage of mocked object"));

		principal43 = Mockito.mock(JwtAuthenticationToken.class,
								   defaultAnswer);
		Mockito.doReturn(true).when(principal43).isAuthenticated();
		Mockito.doReturn("known_keycloak_id_43").when(principal43).getName();
		principal44 = Mockito.mock(JwtAuthenticationToken.class,
								   defaultAnswer);
		Mockito.doReturn(true).when(principal44).isAuthenticated();
		Mockito.doReturn("known_keycloak_id_44").when(principal44).getName();
		principalUnknown = Mockito.mock(JwtAuthenticationToken.class,
										defaultAnswer
		);
		Mockito.doReturn(true).when(principalUnknown).isAuthenticated();
		Mockito.doReturn("unknown_keycloak_id")
			   .when(principalUnknown)
			   .getName();

		monitorUser43 = new MonitorUser("known_keycloak_id_43");
		monitorUser43.setId(43L);

		monitorUser44 = new MonitorUser("known_keycloak_id_44");
		monitorUser44.setId(44L);

		monitoredEndpoint53 = new MonitoredEndpoint("Valid name",
													"https://valid-url.com",
													LocalDateTime.of(2001, 1,
																	 25, 13,
																	 42,
																	 56
													), 3
		);
		monitoredEndpoint53.setId(53L);
		monitoredEndpoint53.setOwner(monitorUser43);

		monitoringResult63 = new MonitoringResult(
				LocalDateTime.of(2003, 3, 27, 15, 44, 58), monitoredEndpoint53,
				monitoredEndpoint53.getUrl(), 200, "Yup, this is a webpage"
		);
		monitoringResult63.setId(63L);
		monitoringResult64 = new MonitoringResult(
				LocalDateTime.of(2004, 4, 28, 16, 45, 59), monitoredEndpoint53,
				monitoredEndpoint53.getUrl(), 404, "Sorry, not found"
		);
		monitoringResult64.setId(64L);

		monitoringResultDTO1 = new MonitoringResultDTO(63L,
													   LocalDateTime.of(2003
															   , 3,
																		27, 15,
																		44, 58
													   ), 53L,
													   "https://valid-url.com",
													   200,
													   "Yup, this is a webpage"

		);
		monitoringResultDTO2 = new MonitoringResultDTO(64L,
													   LocalDateTime.of(2004
															   , 4,
																		28, 16,
																		45, 59
													   ), 53L,
													   "https://valid-url.com",
													   404, "Sorry, not found"
		);

		monitoredEndpointRepository = Mockito.mock(
				MonitoredEndpointRepository.class, defaultAnswer);
		monitorUserRepository = Mockito.mock(MonitorUserRepository.class,
											 defaultAnswer
		);

		localDateTimeService = Mockito.mock(LocalDateTimeService.class,
											defaultAnswer
		);
		monitorUserService = new MonitorUserService(monitorUserRepository);
		monitoredEndpointService = new MonitoredEndpointService(
				monitoredEndpointRepository, monitorUserService,
				localDateTimeService
		);

		monitoringResultRepository = Mockito.mock(
				MonitoringResultRepository.class, defaultAnswer);
		monitoringResultService = new MonitoringResultService(
				monitoringResultRepository, monitoredEndpointService,
				monitorUserService
		);
	}

	@Test
	void getMonitoringResultsNoKeycloakId()
	{
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

	@Nested
	class WithPreparedRelationships
	{
		@BeforeEach
		void setup()
		{
			Mockito.doReturn(Optional.of(monitoredEndpoint53)).when(
					monitoredEndpointRepository).findById(53L);
			Mockito.doReturn(Optional.empty())
				   .when(monitoredEndpointRepository)
				   .findById(79L);

			Mockito.doReturn(List.of(monitorUser43))
				   .when(monitorUserRepository)
				   .findByAuthorizationId("known_keycloak_id_43");
			Mockito.doReturn(List.of(monitorUser44))
				   .when(monitorUserRepository)
				   .findByAuthorizationId("known_keycloak_id_44");
			Mockito.doReturn(Collections.emptyList()).when(
					monitorUserRepository).findByAuthorizationId(
					"unknown_keycloak_id");

			Mockito.doReturn(new ArrayList<>(
						   Arrays.asList(monitoringResult64,
										 monitoringResult63)))
				   .when(monitoringResultRepository)
				   .getAllForEndpoint(53L, Pageable.unpaged());
			Mockito.doReturn(new ArrayList<>(List.of(monitoringResult64))).when(
					monitoringResultRepository).getAllForEndpoint(53L,
																  PageRequest.of(
																		  0, 1)
			);
			Mockito.doReturn(new ArrayList<>(
						   Arrays.asList(monitoringResult64,
										 monitoringResult63)))
				   .when(monitoringResultRepository)
				   .getAllForEndpoint(53L, PageRequest.of(0, 200));
		}

		@Test
		void unknownEndpoint()
		{
			HttpServletRequest request = Mockito.mock(HttpServletRequest.class,
													  defaultAnswer
			);
			Mockito.doReturn(principal43).when(request).getUserPrincipal();

			MonitoringResultController monitoringResultController =
					new MonitoringResultController(
					monitoringResultService);


			ResponseStatusException protoResult1 = new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"Endpoint with given Id (79) does not exist"
			);
			ResponseStatusException result1 = assertThrows(
					ResponseStatusException.class,
					() -> monitoringResultController.getMonitoringResults(79L,
																		  null,
																		  request
					)
			);
			assertEquals(protoResult1.getStatus(), result1.getStatus());
			assertEquals(protoResult1.getMessage(), result1.getMessage());
		}

		@Test
		void unknownUser()
		{
			HttpServletRequest request = Mockito.mock(HttpServletRequest.class,
													  defaultAnswer
			);
			Mockito.doReturn(principalUnknown).when(request).getUserPrincipal();

			MonitoringResultController monitoringResultController =
					new MonitoringResultController(
					monitoringResultService);


			ResponseStatusException protoResult2 = new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"User with given Id (unknown_keycloak_id) does not exist"
			);
			ResponseStatusException result2 = assertThrows(
					ResponseStatusException.class,
					() -> monitoringResultController.getMonitoringResults(53L,
																		  null,
																		  request
					)
			);
			assertEquals(protoResult2.getStatus(), result2.getStatus());
			assertEquals(protoResult2.getMessage(), result2.getMessage());
		}

		@Test
		void nonOwnerUser()
		{
			HttpServletRequest request = Mockito.mock(HttpServletRequest.class,
													  defaultAnswer
			);
			Mockito.doReturn(principal44).when(request).getUserPrincipal();

			MonitoringResultController monitoringResultController =
					new MonitoringResultController(
					monitoringResultService);

			ResponseStatusException protoResult3 = new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					"User does not own specified endpoint"
			);
			ResponseStatusException result3 = assertThrows(
					ResponseStatusException.class,
					() -> monitoringResultController.getMonitoringResults(53L,
																		  null,
																		  request
					)
			);
			assertEquals(protoResult3.getStatus(), result3.getStatus());
			assertEquals(protoResult3.getMessage(), result3.getMessage());
		}

		@Test
		void ownerNoLimit()
		{
			HttpServletRequest request = Mockito.mock(HttpServletRequest.class,
													  defaultAnswer
			);
			Mockito.doReturn(principal43).when(request).getUserPrincipal();

			MonitoringResultController monitoringResultController =
					new MonitoringResultController(
					monitoringResultService);

			ArrayList<MonitoringResultDTO> result = new ArrayList<>(
					monitoringResultController.getMonitoringResults(53L, null,
																	request
					));
			assertEquals(2, result.size());
			assertEquals(monitoringResultDTO2, result.get(0));
			assertEquals(monitoringResultDTO1, result.get(1));
		}

		@Test
		void ownerLimit1()
		{
			HttpServletRequest request = Mockito.mock(HttpServletRequest.class,
													  defaultAnswer
			);
			Mockito.doReturn(principal43).when(request).getUserPrincipal();

			MonitoringResultController monitoringResultController =
					new MonitoringResultController(
					monitoringResultService);

			ArrayList<MonitoringResultDTO> result = new ArrayList<>(
					monitoringResultController.getMonitoringResults(53L, 1,
																	request
					));
			assertEquals(1, result.size());
			MonitoringResultDTO result5DTO = result.get(0);
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
			assertEquals(monitoringResultDTO2.getMonitoredEndpointURL(),
						 result5DTO.getMonitoredEndpointURL()
			);
		}

		@Test
		void ownerLimit200()
		{
			HttpServletRequest request = Mockito.mock(HttpServletRequest.class,
													  defaultAnswer
			);
			Mockito.doReturn(principal43).when(request).getUserPrincipal();

			MonitoringResultController monitoringResultController =
					new MonitoringResultController(
					monitoringResultService);

			ArrayList<MonitoringResultDTO> result = new ArrayList<>(
					monitoringResultController.getMonitoringResults(53L, 200,
																	request
					));
			assertEquals(2, result.size());
			assertEquals(monitoringResultDTO2, result.get(0));
			assertEquals(monitoringResultDTO1, result.get(1));
		}
	}
}
package com.sdasda7777.endpointmonitor.layer1;

import com.sdasda7777.endpointmonitor.layer1.dto.MonitoredEndpointDTO;
import com.sdasda7777.endpointmonitor.layer2.LocalDateTimeService;
import com.sdasda7777.endpointmonitor.layer2.MonitorUserService;
import com.sdasda7777.endpointmonitor.layer2.MonitoredEndpointService;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitorUser;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.layer3.MonitorUserRepository;
import com.sdasda7777.endpointmonitor.layer3.MonitoredEndpointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
class MonitoredEndpointControllerTest
{

	private ThrowsException defaultAnswer;

	private JwtAuthenticationToken principal43;
	private JwtAuthenticationToken principal44;
	private JwtAuthenticationToken principalUnknown;

	private HttpServletRequest request;

	private MonitorUser monitorUser43;
	private MonitorUser monitorUser44;
	private MonitoredEndpoint monitoredEndpoint53;
	private MonitoredEndpoint monitoredEndpoint54;

	private MonitoredEndpointRepository monitoredEndpointRepository;
	private MonitorUserRepository monitorUserRepository;

	private LocalDateTimeService localDateTimeService;
	private MonitorUserService monitorUserService;
	private MonitoredEndpointService monitoredEndpointService;

	private MonitoredEndpointController monitoredEndpointController;

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

		request = Mockito.mock(HttpServletRequest.class, defaultAnswer);

		monitorUser43 = new MonitorUser("known_keycloak_id_43");
		monitorUser43.setId(43L);
		monitorUser44 = new MonitorUser("known_keycloak_id_44");
		monitorUser44.setId(44L);


		monitoredEndpoint53 = new MonitoredEndpoint("Test endpoint 0",
													"https://url0.org",
													LocalDateTime.of(2001, 1,
																	 25, 13,
																	 42,
																	 56
													), 5
		);
		monitoredEndpoint53.setId(53L);
		monitoredEndpoint53.setLastCheckDate(
				LocalDateTime.of(2002, 2, 26, 14, 43, 57));
		monitoredEndpoint53.setOwner(monitorUser43);
		monitoredEndpoint54 = new MonitoredEndpoint("Test endpoint 1",
													"https://url1.com",
													LocalDateTime.of(2003, 3,
																	 27, 15,
																	 44,
																	 58
													), 7
		);
		monitoredEndpoint54.setId(54L);
		monitoredEndpoint54.setLastCheckDate(
				LocalDateTime.of(2004, 4, 28, 16, 45, 59));
		monitoredEndpoint54.setOwner(monitorUser43);


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

		monitoredEndpointController = new MonitoredEndpointController(
				monitoredEndpointService);
	}

	@Nested
	class ReadEndpoint
	{
		@Test
		void testGetMonitoredEndpointsNoKeycloakId()
		{
			Mockito.doReturn(null).when(request).getUserPrincipal();

			ResponseStatusException expected_error =
					new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Authorization token must be " + "provided"
			);

			// Assertions
			ResponseStatusException error = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.getMonitoredEndpoints(
							request)
			);
			assertEquals(expected_error.getStatus(), error.getStatus());
			assertEquals(expected_error.getMessage(), error.getMessage());
		}

		@Test
		void testGetMonitoredEndpointsUnknownKeycloakId()
		{
			Mockito.doReturn(Collections.emptyList()).when(
					monitoredEndpointRepository).getEndpointsByAuthId(
					"unknown_keycloak_id");
			Mockito.doReturn(Collections.emptyList()).when(
					monitorUserRepository).findByAuthorizationId(
					"unknown_keycloak_id");

			MonitoredEndpointController monitoredEndpointController =
					new MonitoredEndpointController(
					monitoredEndpointService);

			HttpServletRequest request1 =
					Mockito.mock(HttpServletRequest.class,
													   defaultAnswer
			);
			Mockito.doReturn(principalUnknown)
				   .when(request1)
				   .getUserPrincipal();

			// Assertions
			assertEquals(Collections.emptyList(),
						 monitoredEndpointController.getMonitoredEndpoints(
								 request1)
			);
		}

		@Test
		void testGetMonitoredEndpointsAllRight()
		{
			Mockito.doReturn(List.of(monitoredEndpoint53, monitoredEndpoint54))
				   .when(monitoredEndpointRepository)
				   .getEndpointsByAuthId("known_keycloak_id_43");

			HttpServletRequest request1 =
					Mockito.mock(HttpServletRequest.class,
													   defaultAnswer
			);
			Mockito.doReturn(principal43).when(request1).getUserPrincipal();

			List<MonitoredEndpointDTO> expectedResult = List.of(
					new MonitoredEndpointDTO(53L, "Test endpoint 0",
											 "https://url0.org",
											 LocalDateTime.of(2001, 1, 25, 13,
															  42, 56
											 ),
											 LocalDateTime.of(2002, 2, 26, 14,
															  43, 57
											 ), 5, 43L
					), new MonitoredEndpointDTO(54L, "Test endpoint 1",
												"https://url1.com",
												LocalDateTime.of(2003, 3, 27,
																 15, 44, 58
												), LocalDateTime.of(2004, 4,
																	28,
																	16, 45, 59
					), 7, 43L
					));

			// Assertions
			List<MonitoredEndpointDTO> actualResult =
					monitoredEndpointController.getMonitoredEndpoints(
					request1).stream().toList();
			assertEquals(2, actualResult.size());
			assertEquals(expectedResult.get(0), actualResult.get(0));
			assertEquals(expectedResult.get(1), actualResult.get(1));
		}
	}


	@Nested
	class CreateEndpoint
	{
		private MonitoredEndpoint newEndpoint;

		@BeforeEach
		void setup()
		{
			Mockito.doReturn(LocalDateTime.of(2003, 3, 27, 15, 44, 58)).when(
					localDateTimeService).now();

			Mockito.doReturn(List.of(monitorUser43))
				   .when(monitorUserRepository)
				   .findByAuthorizationId("known_keycloak_id_43");
			Mockito.doReturn(Collections.emptyList()).when(
					monitorUserRepository).findByAuthorizationId(
					"unknown_keycloak_id");
			Mockito.doAnswer(i ->
							 {
								 MonitoredEndpoint arg = i.getArgument(0);
								 arg.setId(53L);
								 return arg;
							 }).when(monitoredEndpointRepository).save(
					ArgumentMatchers.any());

			newEndpoint = new MonitoredEndpoint();
			newEndpoint.setName("Valid name");
			newEndpoint.setUrl("https://url.com");
			newEndpoint.setMonitoringInterval(2);
		}

		@Test
		void testCreateNoKeycloakId()
		{
			Mockito.doReturn(null).when(request).getUserPrincipal();

			ResponseStatusException expected_error =
					new ResponseStatusException(
							HttpStatus.BAD_REQUEST,
							"Authorization token must be " + "provided"
					);

			// Assertions
			ResponseStatusException error = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.createEndpoint(
							newEndpoint, request)
			);
			assertEquals(expected_error.getStatus(), error.getStatus());
			assertEquals(expected_error.getMessage(), error.getMessage());
		}

		@Test
		void testCreateNullName()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();
			newEndpoint.setName(null);

			ResponseStatusException expectedException =
					new ResponseStatusException(
							HttpStatus.BAD_REQUEST, "Endpoint name must be provided");

			// Assertions
			ResponseStatusException response0 = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.createEndpoint(
							newEndpoint, request)
			);
			assertEquals(expectedException.getStatus(), response0.getStatus());
			assertEquals(expectedException.getMessage(),
						 response0.getMessage()
			);
		}

		@Test
		void testCreateEmptyName()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();
			newEndpoint.setName("");

			ResponseStatusException expectedException =
					new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "Endpoint name must be provided");

			// Assertions
			ResponseStatusException response = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.createEndpoint(
							newEndpoint, request)
			);
			assertEquals(expectedException.getStatus(), response.getStatus());
			assertEquals(expectedException.getMessage(),
						 response.getMessage());
		}

		@Test
		void testCreateNullUrl()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();
			newEndpoint.setUrl(null);

			ResponseStatusException expectedException =
					new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Endpoint url must be provided and be in format '"
					+ "(http|https|ftp)://address'"
			);

			// Assertions
			ResponseStatusException response = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.createEndpoint(
							newEndpoint, request)
			);
			assertEquals(expectedException.getStatus(), response.getStatus());
			assertEquals(expectedException.getMessage(),
						 response.getMessage());
		}

		@Test
		void testCreateInvalidUrl()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();
			newEndpoint.setUrl("invalid url");

			ResponseStatusException expectedException =
					new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Endpoint url must be provided and be in format '"
					+ "(http|https|ftp)://address'"
			);

			// Assertions
			ResponseStatusException response = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.createEndpoint(
							newEndpoint, request)
			);
			assertEquals(expectedException.getStatus(), response.getStatus());
			assertEquals(expectedException.getMessage(),
						 response.getMessage());
		}

		@Test
		void testCreateNullInterval()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();
			newEndpoint.setMonitoringInterval(null);

			ResponseStatusException expectedException =
					new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "Monitoring interval must be "
											+ "provided and be non-negative");

			// Assertions
			ResponseStatusException response = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.createEndpoint(
							newEndpoint, request)
			);
			assertEquals(expectedException.getStatus(), response.getStatus());
			assertEquals(expectedException.getMessage(),
						 response.getMessage());
		}

		@Test
		void testCreateNegativeInterval()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();
			newEndpoint.setMonitoringInterval(-1);

			ResponseStatusException expectedException =
					new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Monitoring interval must be provided and be non-negative"
			);

			// Assertions
			ResponseStatusException response = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.createEndpoint(
							newEndpoint, request)
			);
			assertEquals(expectedException.getStatus(), response.getStatus());
			assertEquals(expectedException.getMessage(),
						 response.getMessage());
		}

		@Test
		void testCreateTypical()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();

			MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(
					"Valid endpoint name", "https://url.com",
					LocalDateTime.of(2001, 1, 25, 13, 42, 56), 2
			);

			//Test with known user
			MonitoredEndpointDTO expectedResult1 =
					new MonitoredEndpointDTO(53L,
																			"Valid"
																			+
																			" endpoint name",
																			"https"
																			+
																			"://url.com",
																			LocalDateTime.of(
																					2003,
																					3,
																					27,
																					15,
																					44,
																					58
																			),
																			LocalDateTime.of(
																					2003,
																					3,
																					27,
																					15,
																					44,
																					58
																					- 2
																			),
																			2,
																			43L
			);

			MonitoredEndpointDTO actualResult1 =
					monitoredEndpointController.createEndpoint(
					monitoredEndpoint1, request);
			assertEquals(expectedResult1, actualResult1);
		}

		@Test
		void testCreateZeroInterval()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();

			MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint();
			monitoredEndpoint2.setName("Valid endpoint name");
			monitoredEndpoint2.setUrl("https://url.com");
			monitoredEndpoint2.setMonitoringInterval(0);

			MonitoredEndpointDTO expectedResult = new MonitoredEndpointDTO(53L,
																		   "Valid "
																		   +
																		   "endpoint"
																		   +
																		   " name",
																		   "https"
																		   +
																		   "://url"
																		   +
																		   ".com",
																		   LocalDateTime.of(
																				   2003,
																				   3,
																				   27,
																				   15,
																				   44,
																				   58
																		   ),
																		   LocalDateTime.of(
																				   2003,
																				   3,
																				   27,
																				   15,
																				   44,
																				   58
																		   )
					, 0,
																		   43L
			);

			// Assertions
			MonitoredEndpointDTO result2 =
					monitoredEndpointController.createEndpoint(
					monitoredEndpoint2, request);
			assertEquals(expectedResult, result2);
		}

		@Test
		void testCreateUnknownUser()
		{
			Mockito.doReturn(principalUnknown).when(request).getUserPrincipal();

			MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint();
			monitoredEndpoint2.setName("Valid endpoint name");
			monitoredEndpoint2.setUrl("https://url.com");
			monitoredEndpoint2.setMonitoringInterval(3);

			Mockito.doAnswer(i ->
							 {
								 MonitorUser arg = i.getArgument(0);
								 arg.setId(45L);
								 return arg;
							 }).when(monitorUserRepository).save(
					ArgumentMatchers.any());

			MonitoredEndpointDTO expectedResult = new MonitoredEndpointDTO(53L,
																		   "Valid "
																		   +
																		   "endpoint"
																		   +
																		   " name",
																		   "https"
																		   +
																		   "://url"
																		   +
																		   ".com",
																		   LocalDateTime.of(
																				   2003,
																				   3,
																				   27,
																				   15,
																				   44,
																				   58
																		   ),
																		   LocalDateTime.of(
																				   2003,
																				   3,
																				   27,
																				   15,
																				   44,
																				   58
																				   - 3
																		   )
					, 3,
																		   45L
			);

			// Assertions
			MonitoredEndpointDTO result2 =
					monitoredEndpointController.createEndpoint(
					monitoredEndpoint2, request);
			assertEquals(expectedResult, result2);
		}
	}

	@Nested
	class UpdateEndpoint
	{
		private MonitoredEndpoint oldEndpoint;
		private MonitoredEndpoint newEndpoint;

		@BeforeEach
		void setup()
		{
			oldEndpoint = new MonitoredEndpoint("Valid endpoint name",
												"https://valid-url.com",
												LocalDateTime.of(2001, 1, 25,
																 13, 42, 56
												), 2
			);
			oldEndpoint.setId(53L);
			oldEndpoint.setOwner(monitorUser43);

			newEndpoint = new MonitoredEndpoint("New valid endpoint name",
												"https://new-valid-url.com",
												LocalDateTime.of(2003, 3, 27,
																 15, 44, 58
												), 3
			);
			newEndpoint.setId(54L);
			newEndpoint.setOwner(monitorUser44);
		}

		@Test
		void testUpdateNoKeycloakId()
		{
			Mockito.doReturn(null).when(request).getUserPrincipal();

			ResponseStatusException expected_error =
					new ResponseStatusException(
							HttpStatus.BAD_REQUEST,
							"Authorization token must be " + "provided"
					);

			// Assertions
			ResponseStatusException error = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.updateEndpoint(53L,
							newEndpoint, request)
			);
			assertEquals(expected_error.getStatus(), error.getStatus());
			assertEquals(expected_error.getMessage(), error.getMessage());
		}

		@Test
		void testUpdateFromNonOwner()
		{
			Mockito.doReturn(principal44).when(request).getUserPrincipal();

			Mockito.doReturn(Optional.of(oldEndpoint)).when(
					monitoredEndpointRepository).findById(53L);
			Mockito.doReturn(List.of(monitorUser44))
				   .when(monitorUserRepository)
				   .findByAuthorizationId("known_keycloak_id_44");

			ResponseStatusException expectedResult =
					new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					"User does not own specified endpoint"
			);

			// Assertions
			ResponseStatusException result = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.updateEndpoint(53L,
																	 newEndpoint,
																	 request
					)
			);
			assertEquals(expectedResult.getStatus(), result.getStatus());
			assertEquals(expectedResult.getMessage(), result.getMessage());
		}

		@Test
		void testUpdateFromUnknownUser()
		{
			Mockito.doReturn(Optional.of(oldEndpoint)).when(
					monitoredEndpointRepository).findById(53L);
			Mockito.doReturn(Collections.emptyList()).when(
					monitorUserRepository).findByAuthorizationId(
					"unknown_keycloak_id");

			Mockito.doReturn(principalUnknown).when(request).getUserPrincipal();

			ResponseStatusException expectedException =
					new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"User with given authorization Id (unknown_keycloak_id) "
					+ "does not exist"
			);

			// Assertions
			ResponseStatusException result = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.updateEndpoint(53L,
																	 newEndpoint,
																	 request
					)
			);
			assertEquals(expectedException.getStatus(), result.getStatus());
			assertEquals(expectedException.getMessage(), result.getMessage());
		}

		@Test
		void testUpdateNonExistentEndpoint()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();

			Mockito.doReturn(Optional.empty())
				   .when(monitoredEndpointRepository)
				   .findById(69L);
			Mockito.doReturn(List.of(monitorUser43))
				   .when(monitorUserRepository)
				   .findByAuthorizationId("known_keycloak_id_43");

			ResponseStatusException expectedResult =
					new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"Endpoint with given Id (69) does not exist"
			);

			// Assertions
			ResponseStatusException result = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.updateEndpoint(69L,
																	 newEndpoint,
																	 request
					)
			);
			assertEquals(expectedResult.getStatus(), result.getStatus());
			assertEquals(expectedResult.getMessage(), result.getMessage());
		}

		@Test
		void testUpdateEmptyName()
		{
			newEndpoint.setName("");

			ResponseStatusException expectedResult =
					new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"If endpoint name is provided, it must not be empty"
			);
			ResponseStatusException result2 = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.updateEndpoint(53L,
																	 newEndpoint,
																	 request
					)
			);
			assertEquals(expectedResult.getStatus(), result2.getStatus());
			assertEquals(expectedResult.getMessage(), result2.getMessage());
		}

		@Test
		void testUpdateInvalidUrl()
		{
			newEndpoint.setUrl("invalid url");

			ResponseStatusException protoResult3 = new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"If endpoint url is provided, it must be in format '"
					+ "(http|https|ftp)://address'"
			);
			ResponseStatusException result3 = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.updateEndpoint(53L,
																	 newEndpoint,
																	 request
					)
			);
			assertEquals(protoResult3.getStatus(), result3.getStatus());
			assertEquals(protoResult3.getMessage(), result3.getMessage());
		}

		@Test
		void testUpdateNegativeInterval()
		{
			newEndpoint.setMonitoringInterval(-1);

			ResponseStatusException expectedException = new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"If monitoring interval is provided, it must be non-negative"
			);
			ResponseStatusException actualException = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.updateEndpoint(53L,
																	 newEndpoint,
																	 request
					)
			);
			assertEquals(expectedException.getStatus(), actualException.getStatus());
			assertEquals(expectedException.getMessage(), actualException.getMessage());
		}

		@Test
		void testUpdateTypical()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();

			Mockito.doReturn(Optional.of(oldEndpoint)).when(
					monitoredEndpointRepository).findById(53L);
			Mockito.doReturn(List.of(monitorUser43))
				   .when(monitorUserRepository)
				   .findByAuthorizationId("known_keycloak_id_43");

			Mockito.doAnswer(i -> i.getArgument(0)).when(
					monitoredEndpointRepository).save(ArgumentMatchers.any());

			MonitoredEndpointDTO expectedResult = new MonitoredEndpointDTO(53L,
																		   "New valid endpoint name",
																		   "https"
																		   +
																		   "://new-valid-url.com",
																		   LocalDateTime.of(
																				   2001,
																				   1,
																				   25,
																				   13,
																				   42,
																				   56
																		   ),
																		   LocalDateTime.of(
																				   2001,
																				   1,
																				   25,
																				   13,
																				   42,
																				   54
																		   )
					, 3,
																		   43L
			);
			MonitoredEndpointDTO actualResult =
					monitoredEndpointController.updateEndpoint(
					53L, newEndpoint, request);
			assertEquals(expectedResult, actualResult);
		}

		@Test
		void testUpdateZeroInterval()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();

			Mockito.doReturn(Optional.of(oldEndpoint)).when(
					monitoredEndpointRepository).findById(53L);
			Mockito.doReturn(List.of(monitorUser43))
				   .when(monitorUserRepository)
				   .findByAuthorizationId("known_keycloak_id_43");

			Mockito.doAnswer(i -> i.getArgument(0)).when(
					monitoredEndpointRepository).save(ArgumentMatchers.any());

			newEndpoint.setMonitoringInterval(0);

			MonitoredEndpointDTO expectedResult = new MonitoredEndpointDTO(53L,
																		   "New valid endpoint name",
																		   "https"
																		   +
																		   "://new-valid-url.com",
																		   LocalDateTime.of(
																				   2001,
																				   1,
																				   25,
																				   13,
																				   42,
																				   56
																		   ),
																		   LocalDateTime.of(
																				   2001,
																				   1,
																				   25,
																				   13,
																				   42,
																				   54
																		   )
					, 0,
																		   43L
			);
			MonitoredEndpointDTO actualResult =
					monitoredEndpointController.updateEndpoint(
					53L, newEndpoint, request);
			assertEquals(expectedResult, actualResult);
		}
	}

	@Nested
	class DeleteEndpoint
	{
		private MonitoredEndpoint monitoredEndpoint53;

		@BeforeEach
		void setup()
		{
			monitoredEndpoint53 = new MonitoredEndpoint("Valid endpoint name",
														"https://valid-url"
														+ ".com",
														LocalDateTime.of(2001,
																		 1, 25,
																		 13,
																		 42,
																		 56
														), 2
			);
			monitoredEndpoint53.setId(53L);
			monitoredEndpoint53.setLastCheckDate(
					LocalDateTime.of(2002, 2, 26, 14, 43, 57));
			monitoredEndpoint53.setOwner(monitorUser43);
		}

		@Test
		void testDeleteNoKeycloakId()
		{
			Mockito.doReturn(null).when(request).getUserPrincipal();

			ResponseStatusException expected_error =
					new ResponseStatusException(
							HttpStatus.BAD_REQUEST,
							"Authorization token must be " + "provided"
					);

			// Assertions
			ResponseStatusException error = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.deleteMonitoredEndpoint(53L,
																	 request)
			);
			assertEquals(expected_error.getStatus(), error.getStatus());
			assertEquals(expected_error.getMessage(), error.getMessage());
		}

		@Test
		void testDeleteUnknownEndpoint()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();

			Mockito.doReturn(Optional.empty())
				   .when(monitoredEndpointRepository)
				   .findById(79L);
			Mockito.doReturn(List.of(monitorUser43))
				   .when(monitorUserRepository)
				   .findByAuthorizationId("known_keycloak_id_43");

			Mockito.doNothing().when(monitoredEndpointRepository).deleteById(
					ArgumentMatchers.<Long>any());

			ResponseStatusException protoResult1 = new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"Endpoint with given Id (79) does not exist"
			);
			ResponseStatusException result1 = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.deleteMonitoredEndpoint(
							79L, request)
			);
			assertEquals(protoResult1.getStatus(), result1.getStatus());
			assertEquals(protoResult1.getMessage(), result1.getMessage());
		}

		@Test
		void testDeleteByUnknownUser()
		{
			Mockito.doReturn(principalUnknown).when(request).getUserPrincipal();

			Mockito.doReturn(Optional.of(monitoredEndpoint53)).when(
					monitoredEndpointRepository).findById(53L);
			Mockito.doReturn(Collections.emptyList()).when(
					monitorUserRepository).findByAuthorizationId(
					"unknown_keycloak_id");

			Mockito.doNothing().when(monitoredEndpointRepository).deleteById(
					ArgumentMatchers.<Long>any());

			ResponseStatusException protoResult2 = new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"User with given Id (unknown_keycloak_id) does not exist"
			);
			ResponseStatusException result2 = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.deleteMonitoredEndpoint(
							53L, request)
			);
			assertEquals(protoResult2.getStatus(), result2.getStatus());
			assertEquals(protoResult2.getMessage(), result2.getMessage());
		}

		@Test
		void testDeleteByNonOwner()
		{
			Mockito.doReturn(principal44).when(request).getUserPrincipal();

			Mockito.doReturn(Optional.of(monitoredEndpoint53)).when(
					monitoredEndpointRepository).findById(53L);
			Mockito.doReturn(List.of(monitorUser44))
				   .when(monitorUserRepository)
				   .findByAuthorizationId("known_keycloak_id_44");

			Mockito.doNothing().when(monitoredEndpointRepository).deleteById(
					ArgumentMatchers.<Long>any());

			ResponseStatusException protoResult3 = new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					"User does not own specified endpoint"
			);
			ResponseStatusException result3 = assertThrows(
					ResponseStatusException.class,
					() -> monitoredEndpointController.deleteMonitoredEndpoint(
							53L, request)
			);
			assertEquals(protoResult3.getStatus(), result3.getStatus());
			assertEquals(protoResult3.getMessage(), result3.getMessage());
		}

		@Test
		void testDeleteTypical()
		{
			Mockito.doReturn(principal43).when(request).getUserPrincipal();

			Mockito.doReturn(Optional.of(monitoredEndpoint53)).when(
					monitoredEndpointRepository).findById(53L);
			Mockito.doReturn(List.of(monitorUser43))
				   .when(monitorUserRepository)
				   .findByAuthorizationId("known_keycloak_id_43");

			Mockito.doNothing().when(monitoredEndpointRepository).deleteById(
					ArgumentMatchers.<Long>any());

			MonitoredEndpointDTO resultProto1 = new MonitoredEndpointDTO(53L,
																		 "Valid "
																		 +
																		 "endpoint"
																		 + " name",
																		 "https"
																		 +
																		 "://valid"
																		 +
																		 "-url"
																		 +
																		 ".com",
																		 LocalDateTime.of(
																				 2001,
																				 1,
																				 25,
																				 13,
																				 42,
																				 56
																		 ),
																		 LocalDateTime.of(
																				 2002,
																				 2,
																				 26,
																				 14,
																				 43,
																				 57
																		 ), 2,
																		 43L
			);
			MonitoredEndpointDTO result1 =
					monitoredEndpointController.deleteMonitoredEndpoint(
					53L, request);
			assertEquals(resultProto1, result1);
		}
	}
}
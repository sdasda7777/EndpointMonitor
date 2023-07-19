package com.sdasda7777.endpointmonitor.layer1;

import com.sdasda7777.endpointmonitor.layer1.dto.MonitoredEndpointDTO;
import com.sdasda7777.endpointmonitor.layer2.MonitoredEndpointService;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.layer2.misc.InsufficientDataOwnershipException;
import com.sdasda7777.endpointmonitor.layer2.misc.InvalidEndpointIdException;
import com.sdasda7777.endpointmonitor.layer2.misc.InvalidUserIdException;
import com.sdasda7777.endpointmonitor.security.authentication.KeycloakUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * This is RestController for work with Monitored Endpoints (provides complete
 * <a href="https://en.wikipedia.org/wiki/CRUD">CRUD</a>).
 * For authorization, <a href="https://www.keycloak.org/">Keycloak</a>
 * headers are expected in the request.
 */

@RestController
@RequestMapping("api/v1/monitoredEndpoints")
public class MonitoredEndpointController
{

	/**
	 * Monitored Endpoints service that provides business operations on
	 * MonitoredEndpoints stored in a storage
	 */
	final MonitoredEndpointService monitoredEndpointService;

	/**
	 * @param monitoredEndpointService monitored endpoints service
	 */
	public MonitoredEndpointController(MonitoredEndpointService monitoredEndpointService)
	{
		this.monitoredEndpointService = monitoredEndpointService;
	}

	/**
	 * Get Monitored Endpoints for given user
	 *
	 * @param request request data (Keycloak headers expected)
	 * @return collection of Monitored Endpoints belonging to the user,
	 * 		or status code 500 (if Keycloak headers aren't provided)
	 */
	@GetMapping("")
	public Collection<MonitoredEndpointDTO> getMonitoredEndpoints(
			HttpServletRequest request
	)
	{
		// Get and return owned endpoints
		try
		{
			return MonitoredEndpointDTO.convertMany(
					monitoredEndpointService.getMonitoredEndpointsByAuthId(
							KeycloakUserService.getKeycloakId(request)));
		}
		catch (AuthenticationCredentialsNotFoundException e)
		{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
											  "Authorization token must be "
											  + "provided"
			);
		}
	}

	/**
	 * Create a new Monitored Endpoint
	 *
	 * @param monitoredEndpoint endpoint to be saved
	 * @param request           request data (Keycloak headers expected)
	 * @return created endpoint (including newly assigned ID)
	 * 		or status code 400
	 */
	@PostMapping("")
	public MonitoredEndpointDTO createEndpoint(
			@RequestBody MonitoredEndpoint monitoredEndpoint,
			HttpServletRequest request
	)
	{
		// Validate received value
		if (monitoredEndpoint.getName() == null || monitoredEndpoint.getName()
																	.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
											  "Endpoint name must be provided"
			);
		}
		else if (monitoredEndpoint.getUrl() == null
				 || !monitoredEndpoint.hasValidUrl())
		{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
											  "Endpoint url must be provided "
											  + "and be in format "
											  + "'(http|https|ftp)"
											  + "://address'"
			);
		}
		else if (monitoredEndpoint.getMonitoringInterval() == null
				 || monitoredEndpoint.getMonitoringInterval() < 0)
		{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
											  "Monitoring interval must be "
											  + "provided and be non-negative"
			);
		}

		// Create new endpoint with received values
		try
		{
			return MonitoredEndpointDTO.convertOne(
					monitoredEndpointService.createMonitoredEndpoint(
							KeycloakUserService.getKeycloakId(request),
							monitoredEndpoint
					));
		}
		catch (AuthenticationCredentialsNotFoundException e)
		{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
											  "Authorization token must be "
											  + "provided"
			);
		}
	}

	/**
	 * Update existing Monitored Endpoint
	 *
	 * @param monitoredEndpointId ID of endpoint to save to
	 * @param monitoredEndpoint   endpoint value
	 * @param request             request data (Keycloak headers expected)
	 * @return updated endpoint or status codes 400, 401 or 404
	 */
	@PutMapping("/{monitoredEndpointId}")
	public MonitoredEndpointDTO updateEndpoint(
			@PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId,
			@RequestBody MonitoredEndpoint monitoredEndpoint,
			HttpServletRequest request
	)
	{
		// Validate received endpoint value
		if (monitoredEndpoint.getName() != null && monitoredEndpoint.getName()
																	.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
											  "If endpoint name is provided, "
											  + "it must not be empty"
			);
		}
		else if (monitoredEndpoint.getUrl() != null
				 && !monitoredEndpoint.hasValidUrl())
		{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
											  "If endpoint url is provided, it"
											  + " must be in "
											  + "format '(http|https|ftp)"
											  + "://address'"
			);
		}
		else if (monitoredEndpoint.getMonitoringInterval() != null
				 && monitoredEndpoint.getMonitoringInterval() < 0)
		{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
											  "If monitoring interval is "
											  + "provided, it must be "
											  + "non-negative"
			);
		}

		// Update with provided value
		try
		{
			return MonitoredEndpointDTO.convertOne(
					monitoredEndpointService.updateMonitoredEndpoint(
							KeycloakUserService.getKeycloakId(request),
							monitoredEndpointId, monitoredEndpoint
					));
		}
		catch (AuthenticationCredentialsNotFoundException e)
		{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
											  "Authorization token must be "
											  + "provided"
			);
		}
		catch (InvalidEndpointIdException e)
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, (
					"Endpoint with given Id (%s) "
					+ "does not exist").formatted(e.getMessage()));
		}
		catch (InvalidUserIdException e)
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, (
					"User with given Id (%s) does "
					+ "not exist").formatted(e.getMessage()));
		}
		catch (InsufficientDataOwnershipException e)
		{
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
											  "User does not own specified "
											  + "endpoint"
			);
		}
	}

	/**
	 * Delete specified endpoint
	 *
	 * @param monitoredEndpointId ID of endpoint to be deleted
	 * @param request             request data (Keycloak headers expected)
	 * @return status code 200 if endpoint existed and was successfully
	 * 		deleted,
	 * 		or 400, 401 or 404.
	 */
	@DeleteMapping("/{monitoredEndpointId}")
	public MonitoredEndpointDTO deleteMonitoredEndpoint(
			@PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId,
			HttpServletRequest request
	)
	{
		try
		{
			return MonitoredEndpointDTO.convertOne(
					monitoredEndpointService.deleteEndpoint(
							KeycloakUserService.getKeycloakId(request),
							monitoredEndpointId
					));
		}
		catch (AuthenticationCredentialsNotFoundException e)
		{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
											  "Authorization token must be "
											  + "provided"
			);
		}
		catch (InvalidEndpointIdException e)
		{
			// It is actually perfectly fine to not be idempotent
			//     as far as the response status code goes:
			// https://stackoverflow.com/questions/33057117/http-delete-method-idempotence
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, (
					"Endpoint with given Id (%s) "
					+ "does not exist").formatted(e.getMessage()));
		}
		catch (InvalidUserIdException e)
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, (
					"User with given Id (%s) does "
					+ "not exist").formatted(e.getMessage()));
		}
		catch (InsufficientDataOwnershipException e)
		{
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
											  "User does not own specified "
											  + "endpoint"
			);
		}
	}
}

package com.sdasda7777.endpointmonitor.L01;

import com.sdasda7777.endpointmonitor.L01.DTO.MonitoringResultDTO;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InsufficientDataOwnershipException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidEndpointIdException;
import com.sdasda7777.endpointmonitor.L02.Exceptions.InvalidUserIdException;
import com.sdasda7777.endpointmonitor.L02.MonitoringResultService;
import com.sdasda7777.endpointmonitor.security.authentication.KeycloakUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * This is RestController for work with Monitoring Results (read only).
 * For authorization, <a href="https://www.keycloak.org/">Keycloak</a>
 * headers are expected in the request.
 */

@RestController
@RequestMapping("api/v1/monitoringResults/")
public class MonitoringResultController
{

	/**
	 * Monitoring Results service that provides business operations on
	 * MonitoringResults stored in a storage
	 */
	final MonitoringResultService monitoringResultService;

	/**
	 * @param monitoringResultService monitoring results service
	 */
	public MonitoringResultController(MonitoringResultService monitoringResultService)
	{
		this.monitoringResultService = monitoringResultService;
	}

	/**
	 * Get list of Monitoring Results for an Endpoint sorted in descending
	 * order by the date of the monitoring
	 *
	 * @param monitoredEndpointId id of the endpoint entity
	 * @param limitResults        optional result limit number, for example 5
	 *                            for last 5 results
	 * @param request             request data (Keycloak headers expected)
	 * @return collection of results for given endpoint if authentication and
	 * 		authorization were successful,
	 * 		or status codes 400, 401 or 404 in case of invalid request.
	 */
	@GetMapping("{monitoredEndpointId}")
	public Collection<MonitoringResultDTO> getMonitoringResults(
			@PathVariable(name = "monitoredEndpointId") Long monitoredEndpointId,
			@RequestParam(name = "limit", required = false) Long limitResults,
			HttpServletRequest request
	)
	{
		try
		{
			return MonitoringResultDTO.convertMany(
					monitoringResultService.getAllForEndpoint(
							KeycloakUserService.getKeycloakId(request),
							monitoredEndpointId, limitResults
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
}

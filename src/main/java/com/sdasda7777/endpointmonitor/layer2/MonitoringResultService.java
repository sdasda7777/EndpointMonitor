package com.sdasda7777.endpointmonitor.layer2;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitorUser;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult;
import com.sdasda7777.endpointmonitor.layer2.misc.InsufficientDataOwnershipException;
import com.sdasda7777.endpointmonitor.layer2.misc.InvalidEndpointIdException;
import com.sdasda7777.endpointmonitor.layer2.misc.InvalidUserIdException;
import com.sdasda7777.endpointmonitor.layer3.MonitoringResultRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * This service provides business operations on Monitoring Results
 */

@Service
public class MonitoringResultService
{
	/**
	 * Repository used for access of Monitoring Results
	 */
	final MonitoringResultRepository monitoringResultRepository;

	/**
	 * Service used for work with Monitored Endpoints
	 */
	final MonitoredEndpointService monitoredEndpointService;

	/**
	 * Service used for work with Monitor Users
	 */
	final MonitorUserService monitorUserService;

	/**
	 * @param monitoringResultRepository used for access of Monitoring Results
	 * @param monitoredEndpointService   used for work with Monitored Endpoints
	 * @param monitorUserService         used for work with Monitor Users
	 */
	public MonitoringResultService(
			MonitoringResultRepository monitoringResultRepository,
			MonitoredEndpointService monitoredEndpointService,
			MonitorUserService monitorUserService
	)
	{
		this.monitoringResultRepository = monitoringResultRepository;
		this.monitoredEndpointService = monitoredEndpointService;
		this.monitorUserService = monitorUserService;
	}

	/**
	 * Get all or some Monitoring Results for given endpoint
	 *
	 * @param authId          user authorization ID
	 * @param monitoredEndpointId endpoint ID
	 * @param limitResults        limit number of results
	 * @return monitoring results, ordered newest to oldest
	 */
	public Collection<MonitoringResult> getAllForEndpoint(
			String authId,
			Long monitoredEndpointId,
			@Nullable Integer limitResults
	)
	{
		Optional<MonitoredEndpoint> endpoint =
				monitoredEndpointService.getEndpointById(
				monitoredEndpointId);

		if (endpoint.isEmpty()) throw new InvalidEndpointIdException(
				monitoredEndpointId.toString());

		Optional<MonitorUser> userOptional =
				monitorUserService.getUserByAuthId(
				authId);

		if (userOptional.isEmpty()) throw new InvalidUserIdException(
				authId);

		if (!userOptional.get().getId().equals(
				endpoint.get().getOwner().getId()))
			throw new InsufficientDataOwnershipException("");

		return monitoringResultRepository.getAllForEndpoint(monitoredEndpointId,
															(limitResults
															 != null
															 ?
															 PageRequest.of(0,
																			  limitResults
															)
															 :
															 Pageable.unpaged())

		);
	}

	/**
	 * Save new monitoring result to the repository
	 * @param monitoringResult to be saved
	 */
	public void createMonitoringResult(MonitoringResult monitoringResult)
	{
		monitoringResultRepository.save(monitoringResult);
	}
}

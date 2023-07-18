package com.sdasda7777.endpointmonitor.layer2;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitorUser;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.layer2.misc.InsufficientDataOwnershipException;
import com.sdasda7777.endpointmonitor.layer2.misc.InvalidEndpointIdException;
import com.sdasda7777.endpointmonitor.layer2.misc.InvalidUserIdException;
import com.sdasda7777.endpointmonitor.layer3.MonitoredEndpointRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

/**
 * This service provides business operations on Monitored Endpoints
 */

@Service
public class MonitoredEndpointService
{
	/**
	 * Repository used for access of Monitored Endpoints
	 */
	MonitoredEndpointRepository monitoredEndpointRepository;

	/**
	 * Service used for work with Monitor Users
	 */
	MonitorUserService monitorUserService;

	/**
	 * Service used for getting current date
	 */
	LocalDateTimeService localDateTimeService;

	/**
	 * @param monitoredEndpointRepository used for access of Monitored
	 *                                    Endpoints
	 * @param monitorUserService          used for work with Monitor Users
	 * @param localDateTimeService        used for getting current date
	 */
	public MonitoredEndpointService(
			MonitoredEndpointRepository monitoredEndpointRepository,
			MonitorUserService monitorUserService,
			LocalDateTimeService localDateTimeService
	)
	{
		this.monitoredEndpointRepository = monitoredEndpointRepository;
		this.monitorUserService = monitorUserService;
		this.localDateTimeService = localDateTimeService;
	}

	/**
	 * Get endpoints requiring update
	 *
	 * @return endpoints that are currently due for monitoring
	 */
	public Collection<MonitoredEndpoint> getRequiringUpdate()
	{
		return monitoredEndpointRepository.getRequiringUpdate();
	}

	/**
	 * Get endpoint by its identifier
	 *
	 * @param monitoredEndpointId id of desired endpoint
	 * @return optional with an endpoint iff it exists
	 */
	public Optional<MonitoredEndpoint> getEndpointById(Long monitoredEndpointId)
	{
		return monitoredEndpointRepository.findById(monitoredEndpointId);
	}

	/**
	 * Return all endpoints where user has given authorization ID
	 *
	 * @param authId authorization ID to search by
	 * @return all endpoints belonging to given user
	 */
	public Collection<MonitoredEndpoint> getMonitoredEndpointsByAuthId(
			String authId
	)
	{
		return monitoredEndpointRepository.getEndpointsByAuthId(authId);
	}

	/**
	 * Create new monitored endpoint
	 *
	 * @param authId            authorization ID of owner
	 * @param monitoredEndpoint value of the newly created endpoint
	 * @return newly created endpoint (containing newly assigned ID)
	 */
	public MonitoredEndpoint createMonitoredEndpoint(
			String authId, MonitoredEndpoint monitoredEndpoint
	)
	{
		MonitorUser monitorUser = getOrCreateUser(authId);
		LocalDateTime time = localDateTimeService.now();

		MonitoredEndpoint newEndpoint = new MonitoredEndpoint(
				monitoredEndpoint.getName(), monitoredEndpoint.getUrl(), time,
				monitoredEndpoint.getMonitoringInterval()
		);
		newEndpoint.setOwner(monitorUser);

		return monitoredEndpointRepository.save(newEndpoint);
	}

	/**
	 * Update monitored endpoint
	 *
	 * @param authId              authorization ID of the user
	 * @param monitoredEndpointId ID of the modified endpoint
	 * @param newEndpoint         new value of the endpoint
	 * @return new value of the endpoint
	 */
	public MonitoredEndpoint updateMonitoredEndpoint(
			String authId,
			Long monitoredEndpointId,
			MonitoredEndpoint newEndpoint
	)
	{
		Optional<MonitoredEndpoint> currentOptional =
				monitoredEndpointRepository.findById(
				monitoredEndpointId);
		if (currentOptional.isEmpty())
		{
			throw new InvalidEndpointIdException(
					monitoredEndpointId.toString());
		}

		Optional<MonitorUser> monitorUser =
				monitorUserService.getUserByAuthId(
				authId);
		if (monitorUser.isEmpty())
		{
			throw new InvalidUserIdException(authId);
		}

		if (!monitorUser.get().getId().equals(
				currentOptional.get().getOwner().getId()))
		{
			throw new InsufficientDataOwnershipException("");
		}

		MonitoredEndpoint currentEndpoint = currentOptional.get();
		if (newEndpoint.getName() != null) currentEndpoint.setName(
				newEndpoint.getName());
		if (newEndpoint.getUrl() != null) currentEndpoint.setUrl(
				newEndpoint.getUrl());
		if (newEndpoint.getMonitoringInterval() != null)
			currentEndpoint.setMonitoringInterval(
					newEndpoint.getMonitoringInterval());

		return monitoredEndpointRepository.save(currentEndpoint);
	}

	/**
	 * Update last check date for an endpoint
	 *
	 * @param endpoint  endpoint to be updated
	 * @param checkDate new last check date
	 */
	public void updateEndpointLastCheck(
			MonitoredEndpoint endpoint, LocalDateTime checkDate
	)
	{
		endpoint.setLastCheckDate(checkDate);
		monitoredEndpointRepository.save(endpoint);
	}

	/**
	 * Delete monitored endpoint
	 *
	 * @param authId              authorization ID of the user
	 * @param monitoredEndpointId ID of the endpoint
	 * @return value of the endpoint before it was deleted
	 */
	public MonitoredEndpoint deleteEndpoint(
			String authId, Long monitoredEndpointId
	)
	{
		Optional<MonitoredEndpoint> monitoredEndpoint = getEndpointById(
				monitoredEndpointId);

		if (monitoredEndpoint.isEmpty()){
			throw new InvalidEndpointIdException(
					monitoredEndpointId.toString());
		}

		Optional<MonitorUser> monitorUser =
				monitorUserService.getUserByAuthId(
				authId);
		if (monitorUser.isEmpty()){
			throw new InvalidUserIdException(authId);
		}

		if (!monitorUser.get().getId().equals(
				monitoredEndpoint.get().getOwner().getId())){
			throw new InsufficientDataOwnershipException("");
		}

		monitoredEndpointRepository.deleteById(monitoredEndpointId);
		return monitoredEndpoint.get();
	}

	/**
	 * Get user by authorization ID, or create
	 *
	 * @param authId authorization ID
	 * @return MonitorUser for given authorization ID
	 */
	private MonitorUser getOrCreateUser(String authId)
	{
		Optional<MonitorUser> monitorUser =
				monitorUserService.getUserByAuthId(
				authId);
		return monitorUser.orElseGet(
				() -> monitorUserService.createUser(new MonitorUser(authId)));
	}
}

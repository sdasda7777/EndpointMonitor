package com.sdasda7777.endpointmonitor.L02;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.misc.InsufficientDataOwnershipException;
import com.sdasda7777.endpointmonitor.L02.misc.InvalidEndpointIdException;
import com.sdasda7777.endpointmonitor.L02.misc.InvalidUserIdException;
import com.sdasda7777.endpointmonitor.L03.MonitoredEndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
public class MonitoredEndpointService
{
	@Autowired
	MonitoredEndpointRepository monitoredEndpointRepository;

	@Autowired
	MonitorUserService monitorUserService;

	@Autowired
	LocalDateTimeService localDateTimeService;

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

	public Collection<MonitoredEndpoint> getRequiringUpdate()
	{
		return monitoredEndpointRepository.getRequiringUpdate();
	}

	public Optional<MonitoredEndpoint> getEndpointById(Long monitoredEndpointId)
	{
		return monitoredEndpointRepository.findById(monitoredEndpointId);
	}

	public Collection<MonitoredEndpoint> getMonitoredEndpointsByKeycloakId(
			String keycloakId
	)
	{
		return monitoredEndpointRepository.getEndpointsByKeycloakId(keycloakId);
	}

	public MonitoredEndpoint createMonitoredEndpoint(
			String keycloakId, MonitoredEndpoint monitoredEndpoint
	)
	{
		MonitorUser monitorUser = getOrCreateUser(keycloakId);
		LocalDateTime time = localDateTimeService.now();

		MonitoredEndpoint newEndpoint = new MonitoredEndpoint(
				monitoredEndpoint.getName(), monitoredEndpoint.getUrl(), time,
				monitoredEndpoint.getMonitoringInterval()
		);
		newEndpoint.setOwner(monitorUser);

		return monitoredEndpointRepository.save(newEndpoint);
	}

	public MonitoredEndpoint updateMonitoredEndpoint(
			String keycloakId,
			Long monitoredEndpointId,
			MonitoredEndpoint newEndpoint
	)
	{
		Optional<MonitoredEndpoint> currentOptional =
				monitoredEndpointRepository.findById(
				monitoredEndpointId);
		if (currentOptional.isEmpty()) throw new InvalidEndpointIdException(
				monitoredEndpointId.toString());

		Optional<MonitorUser> monitorUser =
				monitorUserService.getUserByKeycloakId(
				keycloakId);
		if (monitorUser.isEmpty()) throw new InvalidUserIdException(keycloakId);

		if (!monitorUser.get().getId().equals(
				currentOptional.get().getOwner().getId()))
			throw new InsufficientDataOwnershipException("");

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

	public void updateEndpointLastCheck(
			MonitoredEndpoint endpoint, LocalDateTime checkDate
	)
	{
		monitoredEndpointRepository.updateEndpointLastCheck(endpoint,
															checkDate
		);
	}

	public MonitoredEndpoint deleteEndpoint(
			String keycloakId, Long monitoredEndpointId
	)
	{
		Optional<MonitoredEndpoint> monitoredEndpoint = getEndpointById(
				monitoredEndpointId);

		if (monitoredEndpoint.isEmpty()) throw new InvalidEndpointIdException(
				monitoredEndpointId.toString());

		Optional<MonitorUser> monitorUser =
				monitorUserService.getUserByKeycloakId(
				keycloakId);
		if (monitorUser.isEmpty()) throw new InvalidUserIdException(keycloakId);

		if (!monitorUser.get().getId().equals(
				monitoredEndpoint.get().getOwner().getId()))
			throw new InsufficientDataOwnershipException("");

		monitoredEndpointRepository.deleteById(monitoredEndpointId);
		return monitoredEndpoint.get();
	}


	private MonitorUser getOrCreateUser(String keycloakId)
	{
		Optional<MonitorUser> monitorUser =
				monitorUserService.getUserByKeycloakId(
				keycloakId);
		if (!monitorUser.isEmpty())
		{
			return monitorUser.get();
		}
		else
		{
			return monitorUserService.createUser(new MonitorUser(keycloakId));
		}
	}
}

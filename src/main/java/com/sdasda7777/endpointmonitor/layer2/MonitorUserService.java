package com.sdasda7777.endpointmonitor.layer2;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitorUser;
import com.sdasda7777.endpointmonitor.layer3.MonitorUserRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * This service provides business operations on Monitor Users
 */

@Service
public class MonitorUserService
{
    /**
     * Repository used for access of Monitor Users
     */
	private final MonitorUserRepository monitorUserRepository;

    /**
     * @param monitorUserRepository for access of Monitor Users
     */
	public MonitorUserService(MonitorUserRepository monitorUserRepository)
	{
		this.monitorUserRepository = monitorUserRepository;
	}

    /**
     * Get user by authorization ID
     * @param authId authorization ID to look for
     * @return optional containing found user if found
     */
	public Optional<MonitorUser> getUserByAuthId(String authId)
	{
		Collection<MonitorUser> users =
                monitorUserRepository.findByAuthorizationId(
				authId);
		return (users.size() == 1
				? users.stream().findFirst()
				: Optional.empty());
	}

    /**
     * Create new Monitor User
     * @param monitorUser new value to be saved
     * @return saved MonitorUser (with newly assigned ID)
     */
	public MonitorUser createUser(MonitorUser monitorUser)
	{
		return monitorUserRepository.save(monitorUser);
	}
}

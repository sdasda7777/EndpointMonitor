package com.sdasda7777.endpointmonitor.layer2.entities;

import com.sdasda7777.endpointmonitor.layer2.misc.UsedViaReflection;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This entity represents user of the service.
 * User may create new monitored endpoints, view, edit or delete their own
 * endpoints, as well as view results of their monitoring.
 * Keycloak's IDs are expected to be used as the authorizationId, but using
 * the entity with different service shouldn't be an issue if values are
 * still unique.
 */

@Entity
@Table(name = "monitorUsers")
public class MonitorUser
{
	/**
	 * Unique identifier of the MonitorUser entity
	 */
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Unique identifier in the authorization service
	 */
	@UsedViaReflection
	@Column(unique = true)
	private final String authorizationId;

	/**
	 * MonitoredEndpoints owned by given user
	 */
	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	Collection<MonitoredEndpoint> monitoredEndpoints;

	/**
	 * Empty constructor
	 */
	public MonitorUser()
	{
		this.authorizationId = null;
		this.monitoredEndpoints = new ArrayList<>();
	}

	/**
	 * Filled constructor
	 * @param authorizationId id of the user in the authorization service
	 */
	public MonitorUser(String authorizationId)
	{
		this.authorizationId = authorizationId;
		this.monitoredEndpoints = new ArrayList<>();
	}

	/**
	 * ID getter
	 * @return unique entity identifier
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * ID setter
	 * @param id new unique entity identifier
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
}

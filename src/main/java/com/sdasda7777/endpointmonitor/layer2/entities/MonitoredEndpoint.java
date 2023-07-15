package com.sdasda7777.endpointmonitor.layer2.entities;

import com.sdasda7777.endpointmonitor.layer2.misc.UsedViaReflection;
import org.apache.commons.validator.routines.UrlValidator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * This entity represents endpoint that is being monitored,
 * or perhaps rather a request for an endpoint to be monitored,
 * since distinct MonitoredEndpoints may monitor the same endpoint.
 */

@Entity
@Table(name = "monitoredEndpoints")
public class MonitoredEndpoint
{

	/**
	 * Unique identifier of the endpoint
	 */
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Name of the endpoint given by the user
	 */
	private String name;

	/**
	 * URL of the endpoint
	 */
	@Column(columnDefinition = "TEXT")
	private String url;

	/**
	 * Creation date of the entity
	 */
	@Column(columnDefinition = "TIMESTAMP")
	private final LocalDateTime creationDate;

	/**
	 * Date of the last check
	 */
	@Column(columnDefinition = "TIMESTAMP")
	private LocalDateTime lastCheckDate;

	/**
	 * Date of the next check (accessed in the JPA Query)
	 */
	@UsedViaReflection
	@Column(columnDefinition = "TIMESTAMP")
	private LocalDateTime nextCheckDate;

	/**
	 * Interval of checks, in seconds
	 */
	private Integer monitoringInterval;

	/**
	 * Owner of the instance
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "owner_id", nullable = false)
	private MonitorUser owner;

	/**
	 * Empty constructor
	 */
	public MonitoredEndpoint()
	{
		this.name = null;
		this.url = null;
		this.creationDate = null;
		this.lastCheckDate = null;
		this.nextCheckDate = null;
		this.monitoringInterval = null;
		this.owner = null;
	}

	/**
	 * Constructor which sets all values, except for the owner
	 *
	 * @param name               name of the entity
	 * @param url                address of the endpoint
	 * @param creationDate       date of creation
	 * @param monitoringInterval monitoring interval (in seconds)
	 */
	public MonitoredEndpoint(
			String name,
			String url,
			LocalDateTime creationDate,
			Integer monitoringInterval
	)
	{
		this.name = name;
		this.url = url;
		this.creationDate = creationDate;
		this.lastCheckDate = creationDate.minusSeconds(monitoringInterval);
		setMonitoringInterval(monitoringInterval);

		this.owner = null;
	}

	/**
	 * ID getter
	 *
	 * @return ID of the instance
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * ID setter
	 *
	 * @param id new ID
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Name getter
	 *
	 * @return name given to the entity by the user
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Name setter
	 *
	 * @param name new name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * URL getter
	 *
	 * @return URL of the monitored endpoint
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * URL setter
	 *
	 * @param url new URL
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * Creation date getter
	 *
	 * @return creation date
	 */
	public LocalDateTime getCreationDate()
	{
		return creationDate;
	}

	/**
	 * Last check date getter
	 *
	 * @return last check date
	 */
	public LocalDateTime getLastCheckDate()
	{
		return lastCheckDate;
	}

	/**
	 * Last check date setter
	 *
	 * @param lastCheckDate new last check date
	 */
	public void setLastCheckDate(LocalDateTime lastCheckDate)
	{
		this.lastCheckDate = lastCheckDate;
		if (monitoringInterval != null)
		{
			nextCheckDate = lastCheckDate.plusSeconds(monitoringInterval);
		}
	}

	/**
	 * Monitoring interval getter
	 *
	 * @return monitoring interval
	 */
	public Integer getMonitoringInterval()
	{
		return monitoringInterval;
	}

	/**
	 * Monitoring interval setter
	 *
	 * @param monitoringInterval new monitoring interval
	 */
	public void setMonitoringInterval(Integer monitoringInterval)
	{
		this.monitoringInterval = monitoringInterval;
		if (lastCheckDate != null)
		{
			nextCheckDate = lastCheckDate.plusSeconds(monitoringInterval);
		}
	}

	/**
	 * Owner getter
	 *
	 * @return MonitorUser that owns this instance
	 */
	public MonitorUser getOwner()
	{
		return owner;
	}

	/**
	 * Owner setter
	 *
	 * @param owner new owner
	 */
	public void setOwner(MonitorUser owner)
	{
		this.owner = owner;
	}

	/**
	 * Determine whether held URL is valid
	 *
	 * @return true iff url is valid
	 */
	public boolean hasValidUrl()
	{
		return url != null && (new UrlValidator()).isValid(url);
	}
}

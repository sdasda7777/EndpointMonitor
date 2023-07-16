package com.sdasda7777.endpointmonitor.layer2.entities;

import com.sdasda7777.endpointmonitor.layer2.misc.UsedViaReflection;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * This entity represents result of single monitoring of an endpoint.
 * In general use, every MonitoredEndpoint is likely to have large amount of
 * these, since the monitoring is recurring (every monitoring interval).
 */

@Entity
@Table(name = "monitoringResults")
public class MonitoringResult
{
	/**
	 * Unique Identifier of the Monitoring Result
	 */
	@Id
	@GeneratedValue
	@Column(nullable = false)
	private Long id;

	/**
	 * Date when the monitoring occurred
	 */
	@Column(nullable = false, columnDefinition = "TIMESTAMP")
	private final LocalDateTime checkDate;

	/**
	 * Monitored Endpoint which the result belongs to
	 */
	@UsedViaReflection
	@ManyToOne(optional = false)
	@JoinColumn(name = "monitoredEndpoint_id", nullable = false)
	private final MonitoredEndpoint monitoredEndpoint;

	/**
	 * Monitored Endpoint URL (since values in monitoredEndpoint may change over time)
	 */
	@Column(nullable = false)
	private final String monitoredEndpointURL;

	/**
	 * <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">
	 * HTTP status code</a> of the result
	 */
	@Column(nullable = false)
	private final Integer resultStatusCode;

	/**
	 * Payload (<a href="https://en.wikipedia.org/wiki/HTTP_message_body">
	 * HTTP body<a/>) of the endpoint response
	 */
	@Column(nullable = false, columnDefinition = "TEXT")
	private final String resultPayload;

	/**
	 * Empty constructor
	 */
	public MonitoringResult()
	{
		this.checkDate = null;
		this.resultStatusCode = null;
		this.resultPayload = null;
		this.monitoredEndpoint = null;
		this.monitoredEndpointURL = null;
	}

	/**
	 * Constructor which sets all fields but the ID
	 * @param checkDate date the check was performed
	 * @param monitoredEndpoint endpoint the result belongs to
	 * @param monitoredEndpointURL URL that was checked
	 * @param resultStatusCode HTTP status code of the response
	 * @param resultPayload Payload (HTTP body) of the response
	 */
	public MonitoringResult(
			LocalDateTime checkDate,
			MonitoredEndpoint monitoredEndpoint,
			String monitoredEndpointURL,
			Integer resultStatusCode,
			String resultPayload
	)
	{
		this.checkDate = checkDate;
		this.monitoredEndpoint = monitoredEndpoint;
		this.monitoredEndpointURL = monitoredEndpointURL;
		this.resultStatusCode = resultStatusCode;
		this.resultPayload = resultPayload;
	}

	/**
	 * ID getter
	 * @return unique identifier of the result
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * ID setter
	 * @param id new ID value
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Check date getter
	 * @return check date
	 */
	public LocalDateTime getCheckDate()
	{
		return checkDate;
	}

	/**
	 * Monitored Endpoint getter
	 * @return monitored endpoint
	 */
	public MonitoredEndpoint getMonitoredEndpoint()
	{
		return monitoredEndpoint;
	}

	/**
	 * Monitored Endpoint URL getter
	 * @return URL that was checked
	 */
	public String getMonitoredEndpointURL()
	{
		return monitoredEndpointURL;
	}

	/**
	 * Result status code getter
	 * @return resulting HTTP status code
	 */
	public Integer getResultStatusCode()
	{
		return resultStatusCode;
	}

	/**
	 * Result payload getter
	 * @return HTTP body of resulting response
	 */
	public String getResultPayload()
	{
		return resultPayload;
	}
}

package com.sdasda7777.endpointmonitor.layer1.dto;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a
 * <a href="https://en.wikipedia.org/wiki/Data_transfer_object">DTO</a>
 * version of the
 * {@link com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult} entity
 */

public class MonitoringResultDTO
{

	/**
	 * ID number of the MonitoringResult entity
	 */
	private final Long id;

	/**
	 * Date when the monitoring was performed
	 */
	private final LocalDateTime checkDate;

	/**
	 * ID of the monitored endpoint
	 */
	private final Long monitoredEndpointId;

	/**
	 * URL of the monitored endpoint
	 */
	private final String monitoredEndpointURL;

	/**
	 * <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">HTTP status code</a> of the endpoint response
	 */
	private final Integer resultStatusCode;

	/**
	 * Payload (
	 * <a href="https://en.wikipedia.org/wiki/HTTP_message_body">HTTP body<a/>) of the endpoint response
	 */
	private final String resultPayload;

	/**
	 * Constructor for MonitoringResultDTO which automatically takes all
	 * relevant data from the passed
	 * MonitoringResult entity
	 *
	 * @param monitoringResult entity to take relevant data from
	 */
	public MonitoringResultDTO(MonitoringResult monitoringResult)
	{
		this.id = monitoringResult.getId();
		this.checkDate = monitoringResult.getCheckDate();
		this.monitoredEndpointId =
				monitoringResult.getMonitoredEndpoint().getId();
		this.monitoredEndpointURL = monitoringResult.getMonitoredEndpointURL();
		this.resultStatusCode = monitoringResult.getResultStatusCode();
		this.resultPayload = monitoringResult.getResultPayload();
	}

	/**
	 * Constructor for MonitoringResultDTO which takes relevant data as
	 * arguments
	 *
	 * @param id                   id of the endpoint entity
	 * @param checkDate            date when the monitoring was performed
	 * @param monitoredEndpointId  ID of the monitored endpoint entity
	 * @param monitoredEndpointURL URL of the monitored endpoint
	 * @param resultStatusCode
	 * <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">HTTP status code</a>
	 *                             of the endpoint response
	 * @param resultPayload        payload (
	 *                             <a href="https://en.wikipedia.org/wiki/HTTP_message_body">HTTP body<a/>) of the endpoint response
	 */
	public MonitoringResultDTO(
			Long id,
			LocalDateTime checkDate,
			Long monitoredEndpointId,
			String monitoredEndpointURL,
			Integer resultStatusCode,
			String resultPayload
	)
	{
		this.id = id;
		this.checkDate = checkDate;
		this.monitoredEndpointId = monitoredEndpointId;
		this.monitoredEndpointURL = monitoredEndpointURL;
		this.resultStatusCode = resultStatusCode;
		this.resultPayload = resultPayload;
	}

	/**
	 * equals - determines whether two objects are equivalent
	 *
	 * @param o compared object
	 * @return true iff values match for every field
	 */
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MonitoringResultDTO that = (MonitoringResultDTO) o;
		return id.equals(that.id)
			   && checkDate.equals(that.checkDate)
			   && monitoredEndpointId.equals(that.monitoredEndpointId)
			   && monitoredEndpointURL.equals(that.monitoredEndpointURL)
			   && resultStatusCode.equals(that.resultStatusCode)
			   && resultPayload.equals(that.resultPayload);
	}

	/**
	 * Converts one MonitoringResult to one MonitoringResultDTO
	 *
	 * @param result source result entity
	 * @return MonitoringResultDTO describing provided result entity
	 */
	public static MonitoringResultDTO convertOne(MonitoringResult result)
	{
		return new MonitoringResultDTO(result);
	}

	/**
	 * Converts collection of MonitoringResults to collection of
	 * MonitoringResultDTOs
	 *
	 * @param results collection of results entities
	 * @return collection of MonitoringResultDTOs describing provided result
	 * 		entities
	 */
	public static Collection<MonitoringResultDTO> convertMany(Collection<MonitoringResult> results)
	{
		Collection<MonitoringResultDTO> dtos = new ArrayList<>();
		results.forEach(ii -> dtos.add(MonitoringResultDTO.convertOne(ii)));
		return dtos;
	}
}

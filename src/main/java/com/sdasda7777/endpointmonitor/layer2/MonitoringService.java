package com.sdasda7777.endpointmonitor.layer2;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Service which routinely checks for endpoints requiring status update,
 * and updates them using multiple threads (thread pool size can be set
 * in application.properties using endpointMonitor.thread-count).
 */

@Configuration
@EnableScheduling
public class MonitoringService
{
	/**
	 * Class representing check task to fulfill.
	 * Instances are created by MonitoringService,
	 * passed to the thread pool, executed by it.
	 */
	private class EndpointCheckWorkerThread implements Runnable
	{
		/**
		 * Service for request creation
		 */
		private final InternetRequestService internetRequestService;
		/**
		 * Endpoint to check on
		 */
		private final MonitoredEndpoint endpoint;

		/**
		 *
		 * @param internetRequestService service for request creation
		 * @param endpoint endpoint to check on
		 */
		public EndpointCheckWorkerThread(
				InternetRequestService internetRequestService,
				MonitoredEndpoint endpoint
		)
		{
			this.internetRequestService = internetRequestService;
			this.endpoint = endpoint;
		}

		/**
		 * Check the status, save result
		 */
		@Override
		// uncaught exception in a child thread doesn't fail a test
		public void run()
		{
			MonitoringResult status;

			String url = endpoint.getUrl();
			LocalDateTime requestTime = LocalDateTime.now();
			try
			{
				HttpResponse<String> response =
						internetRequestService.makeRequest(
						url);

				status = new MonitoringResult(requestTime, endpoint, url,
											  response.statusCode(),
											  response.body()
				);
			}
			catch (ConnectException c)
			{
				status = new MonitoringResult(requestTime, endpoint, url, null,
											  null);
			}
			catch (IOException | InterruptedException e)
			{
				return;
			}

			monitoringResultService.createMonitoringResult(status);
			endpoint.setLastCheckDate(status.getCheckDate());
			monitoredEndpointService.updateEndpointLastCheck(endpoint,
															 status.getCheckDate()
			);
		}
	}

	/**
	 * Thread pool size to use
	 */
	private final Integer threading;

	/**
	 * Service for creation of requests, passed to individual tasks
	 */
	private final InternetRequestService internetRequestService;

	/**
	 * Monitored Endpoint service for querying of endpoints requiring update
	 */
	private final MonitoredEndpointService monitoredEndpointService;

	/**
	 * Monitoring Results service for saving of new results
	 */
	private final MonitoringResultService monitoringResultService;

	/**
	 * Executor of check tasks
	 */
	private ExecutorService executor;

	/**
	 * @param threading number of threads to use
	 * @param internetRequestService service for creation of requests
	 * @param monitoredEndpointService service for querying of endpoints requiring update
	 * @param monitoringResultService service for saving of new results
	 */
	public MonitoringService(
			@Value("${endpointMonitor.thread-count}") Integer threading,
			InternetRequestService internetRequestService,
			MonitoredEndpointService monitoredEndpointService,
			MonitoringResultService monitoringResultService
	)
	{
		this.internetRequestService = internetRequestService;
		this.monitoredEndpointService = monitoredEndpointService;
		this.monitoringResultService = monitoringResultService;
		this.threading = (threading != null && threading > 0 ? threading : Runtime.getRuntime().availableProcessors());
		this.executor = Executors.newFixedThreadPool(this.threading);
	}

	/**
	 * Schedule check of all endpoints requiring update
	 */
	@Scheduled(fixedDelay = 1000)
	public void checkEndpoints()
	{
		for (MonitoredEndpoint endpoint :
				monitoredEndpointService.getRequiringUpdate())
		{
			executor.execute(
					new EndpointCheckWorkerThread(internetRequestService,
												  endpoint
					));
		}
	}

	/**
	 * Await termination of all currently running check tasks
	 * @throws InterruptedException
	 */
	public void awaitEndpointsChecked() throws InterruptedException
	{
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);
		executor = Executors.newFixedThreadPool(this.threading);
	}
}

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

@Configuration
@EnableScheduling
public class MonitoringService
{

	private class EndpointCheckWorkerThread implements Runnable
	{
		private final InternetRequestService internetRequestService;
		private final MonitoredEndpoint endpoint;

		public EndpointCheckWorkerThread(
				InternetRequestService internetRequestService,
				MonitoredEndpoint endpoint
		)
		{
			this.internetRequestService = internetRequestService;
			this.endpoint = endpoint;
		}

		@Override
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
				status = new MonitoringResult(requestTime, endpoint, url, 599,
											  "Connection to server hosting "
											  + "URL '"
											  + endpoint.getUrl()
											  + "' (if such server exists) "
											  + "could"
											  + " not be established."
				);
			}
			catch (Exception e)
			{
				// exception in a child thread doesn't fail a test, FYI
				return;
			}

			monitoringResultService.createMonitoringResult(status);
			endpoint.setLastCheckDate(status.getCheckDate());
			monitoredEndpointService.updateEndpointLastCheck(endpoint,
															 status.getCheckDate()
			);
		}
	}

	final Integer threading;
	final InternetRequestService internetRequestService;
	final MonitoredEndpointService monitoredEndpointService;

	final MonitoringResultService monitoringResultService;

	ExecutorService executor;

	public MonitoringService(
			@Value("${endpointMonitor.thread-count}") Integer threading,
			InternetRequestService internetRequestService,
			MonitoredEndpointService monitoredEndpointService,
			MonitoringResultService monitoringResultService
	)
	{
		this.threading = threading;
		this.internetRequestService = internetRequestService;
		this.monitoredEndpointService = monitoredEndpointService;
		this.monitoringResultService = monitoringResultService;
		this.executor = Executors.newFixedThreadPool(
				threading != null && threading > 0
				? threading
				: Runtime.getRuntime().availableProcessors());
	}

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

	public void awaitEndpointsChecked() throws InterruptedException
	{
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);
		executor = Executors.newFixedThreadPool(
				threading != null && threading > 0
				? threading
				: Runtime.getRuntime().availableProcessors());
	}
}

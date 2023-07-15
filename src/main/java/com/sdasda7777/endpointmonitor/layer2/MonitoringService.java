package com.sdasda7777.endpointmonitor.layer2;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
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
		private final MonitoredEndpoint endpoint;

		public EndpointCheckWorkerThread(MonitoredEndpoint endpoint)
		{
			this.endpoint = endpoint;
		}

		@Override
		public void run()
		{
			try
			{
				MonitoringResult status = checkStatus(endpoint);
				monitoringResultService.createMonitoringResult(status);
				monitoredEndpointService.updateEndpointLastCheck(endpoint,
																 status.getCheckDate()
				);
			}
			catch (RuntimeException e)
			{
				// Can't actually do anything here? Rethink.
			}
		}

		static MonitoringResult checkStatus(MonitoredEndpoint endpoint)
		{
			String url = endpoint.getUrl();
			LocalDateTime requestTime = LocalDateTime.now();
			try
			{
				HttpClient client = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder().uri(
						URI.create(url)).header("accept",
												"application/json"
				).build();

				HttpResponse<String> response = client.send(request,
															HttpResponse.BodyHandlers.ofString()
				);

				return new MonitoringResult(requestTime,
											endpoint, url,
											response.statusCode(),
											response.body()
				);
			}
			catch (ConnectException c)
			{
				return new MonitoringResult(requestTime,
											endpoint, url,
											599,
											"Connection to server hosting URL '"
											+ endpoint.getUrl()
											+ "' (if such server exists) could not be established."
				);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	final MonitoredEndpointService monitoredEndpointService;

	final MonitoringResultService monitoringResultService;

	ExecutorService executor;

	public MonitoringService(
			MonitoredEndpointService monitoredEndpointService,
			MonitoringResultService monitoringResultService
	)
	{
		this.monitoredEndpointService = monitoredEndpointService;
		this.monitoringResultService = monitoringResultService;
		this.executor = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());
	}

	@Scheduled(fixedDelay = 1000)
	public void checkEndpoints()
	{
		for (MonitoredEndpoint endpoint :
                monitoredEndpointService.getRequiringUpdate())
		{
			executor.execute(new EndpointCheckWorkerThread(endpoint));
		}
	}

	public void awaitEndpointsChecked() throws InterruptedException
	{
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);
		executor = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());
	}
}

package com.sdasda7777.endpointmonitor.layer2;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * This service abstracts creation of HTTP request.
 * That allows for better testability of MonitoringService, which uses it.
 */

@Service
public class InternetRequestService
{
	public InternetRequestService(){}

	/**
	 * Make an HTTP request to given address
	 * @param url address to make request to
	 * @return response
	 * @throws IOException same as HttpClient.send()
	 * @throws InterruptedException same as HttpClient.send()
	 */
	public HttpResponse<String> makeRequest(String url) throws IOException,
			InterruptedException
	{
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
										 .uri(URI.create(url))
										 .header("accept", "application/json")
										 .build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}
}

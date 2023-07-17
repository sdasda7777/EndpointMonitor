package com.sdasda7777.endpointmonitor.layer2;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.springframework.jmx.access.InvalidInvocationException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;


class InternetRequestServiceTest
{
	@Test
	void testRequestIsMade()
	{
		// Setup
		var defaultAnswer = new ThrowsException(new InvalidInvocationException(
				"Inappropriate usage of mocked object"));

		MockedStatic<HttpClient> httpClientMockedStatic = Mockito.mockStatic(
				HttpClient.class);
		HttpClient httpClientMock = Mockito.mock(HttpClient.class,
												 defaultAnswer
		);
		httpClientMockedStatic.when(HttpClient::newHttpClient).thenReturn(
				httpClientMock);
		HttpResponse<String> responseMock = Mockito.mock(HttpResponse.class,
														 defaultAnswer
		);
		try
		{
			Mockito.doAnswer(i ->
							 {
								 assertEquals("https://www.google.com",
											  ((HttpRequest) i.getArgument(
													  0)).uri().toString()
								 );
								 assertEquals(
										 HttpResponse.BodyHandlers.ofString(),
										 i.getArgument(1)
								 );
								 return responseMock;
							 }).when(httpClientMock).send(
					ArgumentMatchers.any(), ArgumentMatchers.any());
		}
		catch (IOException | InterruptedException e)
		{
			throw new RuntimeException(e);
		}

		// Test
		InternetRequestService iRS = new InternetRequestService();
		try
		{
			HttpResponse<String> r = iRS.makeRequest("https://www.google.com");
			assertEquals(responseMock, r);
			Mockito.verify(httpClientMock, times(1)).send(
					ArgumentMatchers.any(), ArgumentMatchers.any());
		}
		catch (IOException | InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
}
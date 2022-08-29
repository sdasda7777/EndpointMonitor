package com.sdasda7777.endpointmonitor.L02;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoringResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableScheduling
public class MonitoringService {
    @Autowired
    MonitoredEndpointService monitoredEndpointService;
    @Autowired
    MonitoringResultService monitoringResultService;

    MonitoringResult checkStatus(String url){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .build();

        LocalDateTime requestTime = LocalDateTime.now();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            MonitoringResult ret = new MonitoringResult();
            ret.setCheckDate(requestTime);
            ret.setResultStatusCode(response.statusCode());
            ret.setResultPayload(response.body());

            return ret;
        } catch (ConnectException c) {
            MonitoringResult ret = new MonitoringResult();
            ret.setCheckDate(requestTime);
            ret.setResultStatusCode(599);
            ret.setResultPayload("Connection to server hosting URL '" + url
                                    + "' (if such server exists) could not be established.");
            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        Collection<MonitoredEndpoint> needUpdating = monitoredEndpointService.getRequiringUpdate();

        for(MonitoredEndpoint endpoint : needUpdating){
            MonitoringResult status = checkStatus(endpoint.getUrl());
            status.setMonitoredEndpoint(endpoint);
            monitoringResultService.createMonitoringResult(status);
            endpoint.setLastCheckDate(status.getCheckDate());
            monitoredEndpointService.updateMonitoredEndpoint(endpoint);
        }
    }
}

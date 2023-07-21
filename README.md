# Endpoint Monitor

Endpoint Monitor is a simple backend which provides options for continuous monitoring of remote endpoints. Endpoint Monitor uses Keycloak for purposes of authentication and authorization.

![example workflow](https://github.com/sdasda7777/EndpointMonitor/actions/workflows/maven.yml/badge.svg)

## Setup

You should be able to run the project by running `sudo docker-compose up` in a terminal. If you want to rebuild the containers, use `--build`.

Once all containers are started up, you will find the EndpointMonitor backend at `localhost:8080` and Keycloak at `localhost:8180`. The databases for EndpointMonitor and Keycloak will be running at ports 5432 and 5433 respectively. (Port numbers may be modified in the `docker-compose.yml` file)

To create users, login at Keycloak at `localhost:8180` using credentials `admin:password`. (Credentials may be modified in the `docker-compose.yml` file)

The Endpoint Monitor will expect realm named `Demo-Realm` (may be modified in `src/main/resources/application.properties`, keys `spring.security.oauth2.resourceserver.jwt.issuer-uri` and `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`). After creating the realm, you will want to create users in it for purposes of making requests to the Endpoint Monitor.

## Usage

Endpoint Monitor provides CRUD operations on monitored endpoints (`localhost:8080/api/v1/monitoredEndpoints`) and read only access to monitoring results (`localhost:8080/api/v1/monitoringResults/`).

### Monitored Endpoints

As was mentioned above, user can Create, Read, Update and Delete the monitored endpoints, specifically only the endpoints owned by them.

To view all owned endpoints, make HTTP GET request at `localhost:8080/api/v1/monitoredEndpoints`.

To view single owned endpoint, make HTTP GET request at `localhost:8080/api/v1/monitoredEndpoints/$ENDPOINT_ID`.

To create a new endpoint, make a HTTP POST request at `localhost:8080/api/v1/monitoredEndpoints`, with the request body containing JSON object with values `name`, `url` and `monitoringInterval`, where `url` must be a valid URL and `monitoringInterval` is monitoring interval in seconds.

To update an existing endpoint, make a HTTP PUT request at `localhost:8080/api/v1/monitoredEndpoints/$ENDPOINT_ID`, with the request body containing JSON object with optional values `name`, `url` and `monitoringInterval`, where `url` must be a valid URL and `monitoringInterval` is monitoring interval in seconds

To delete an existing endpoint, make a HTTP DELETE request at `localhost:8080/api/v1/monitoredEndpoints/$ENDPOINT_ID`.

### Monitoring Results

Similarly to monitored endpoints, user can only view results for their own endpoints. This is done by sending HTTP GET request at `localhost:8080/api/v1/monitoringResults/$ENDPOINT_ID[?limit=$LIMIT_COUNT]`, where `$ENDPOINT_ID` is unique identifier of the endpoint and `$LIMIT_COUNT` is optional parameter specifying largest number of results that may be returned (lesser amount may be returned if specified number of results doesn not exist). Returned results are the newest ones, ordered newest to oldest.

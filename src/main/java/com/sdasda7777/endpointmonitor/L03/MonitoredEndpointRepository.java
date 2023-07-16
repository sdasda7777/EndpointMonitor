package com.sdasda7777.endpointmonitor.L03;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitoredEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This is the repository for Monitored Endpoints
 */

@Repository
public interface MonitoredEndpointRepository extends JpaRepository<MonitoredEndpoint, Long>
{

	/**
	 * Save (Create or Update in CRUD) stored MonitoredEndpoint (based on @Id)
	 *
	 * @param monitoredEndpoint MonitoredEndpoint to be saved
	 * @param <S>               class with MonitoredEndpoint as a base class
	 * @return stored MonitoredEndpoint with newly assigned identifier value
	 */
	@NonNull
	<S extends MonitoredEndpoint> S save(@NonNull S monitoredEndpoint);

	/**
	 * Return all stored MonitoredEndpoints
	 *
	 * @return all stored instances
	 */
	@NonNull
	List<MonitoredEndpoint> findAll();

	/**
	 * Find one instance by unique identifier
	 *
	 * @param id unique endpoint identifier
	 * @return Optional containing found instance iff it exists
	 */
	@NonNull
	Optional<MonitoredEndpoint> findById(@NonNull Long id);

	/**
	 * Find all endpoints owned by user with specific auth id
	 *
	 * @param authId authorization id
	 * @return all owned endpoints
	 */
	@Query("SELECT me FROM MonitoredEndpoint me "
		   + "WHERE me.owner.authorizationId = :id "
		   + "ORDER BY me.id ASC")
	Collection<MonitoredEndpoint> getEndpointsByAuthId(@NonNull @Param("id") String authId);

	/**
	 * Find all endpoints requiring a check
	 *
	 * @return all endpoints requiring a check
	 */
	@Query("SELECT me FROM MonitoredEndpoint me "
		   + "WHERE me.nextCheckDate <= CURRENT_TIMESTAMP "
		   + "ORDER BY me.id ASC")
	List<MonitoredEndpoint> getRequiringUpdate();

	/**
	 * Delete endpoint by unique id
	 *
	 * @param id id of endpoint to be deleted
	 */
	void deleteById(@NonNull Long id);
}

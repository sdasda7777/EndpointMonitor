package com.sdasda7777.endpointmonitor.L03;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is the repository for Monitoring Results
 */

@Repository
public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, Long>
{
	/**
	 * Save (Create or Update in CRUD) stored MonitoredEndpoint (based on @Id)
	 * @param monitoringResult MonitoringResult to be saved
	 * @return stored MonitoringResult with newly assigned identifier value
	 * @param <S> class with MonitoringResult as a base class
	 */
	@NonNull
	<S extends MonitoringResult> S save(@NonNull S monitoringResult);

	/**
	 * Return all stored MonitoringResults
	 * @return all stored instances
	 */
	@NonNull
	List<MonitoringResult> findAll();

	/**
	 * Find one instance by unique identifier
	 *
	 * @param id unique result identifier
	 * @return Optional containing found instance iff it exists
	 */
	@NonNull
	Optional<MonitoringResult> findById(@NonNull Long id);

	/**
	 * Get results for given endpoint, ordered by measurement date (newest to oldest)
	 * @param monitoredEndpointId identifier of the endpoint
	 * @param pageable paging details
	 * @return results for given endpoint, ordered newest to oldest
	 */
	@Query("SELECT r FROM MonitoringResult r "
		   + "WHERE r.monitoredEndpoint.id = :endpointId "
		   + "ORDER BY r.checkDate DESC")
	ArrayList<MonitoringResult> getAllForEndpoint(@NonNull @Param("endpointId") Long monitoredEndpointId,
												  @NonNull Pageable pageable);
}

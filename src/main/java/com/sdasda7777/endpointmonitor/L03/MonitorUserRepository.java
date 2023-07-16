package com.sdasda7777.endpointmonitor.L03;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitorUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This is the repository for Monitor User
 */

@Repository
public interface MonitorUserRepository extends JpaRepository<MonitorUser, Long>
{

	/**
	 * Save (Create or Update in CRUD) stored MonitorUser (based on @Id)
	 *
	 * @param monitorUser MonitorUser to be saved
	 * @param <S>         class with MonitorUser as a base class
	 * @return stored MonitorUser with newly assigned identifier value
	 */
	@NonNull
	<S extends MonitorUser> S save(@NonNull S monitorUser);

	/**
	 * Return all stored MonitorUsers
	 *
	 * @return all stored instances
	 */
	@NonNull
	List<MonitorUser> findAll();

	/**
	 * Find one instance by unique identifier
	 *
	 * @param id unique user identifier
	 * @return Optional containing found instance iff it exists
	 */
	@NonNull
	Optional<MonitorUser> findById(@NonNull Long id);

	/**
	 * Find users by authorization id
	 *
	 * @param id authorization id by which to search
	 * @return collection of users with given id
	 */
	Collection<MonitorUser> findByAuthorizationId(@NonNull @Param("id") String id);
}

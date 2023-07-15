package com.sdasda7777.endpointmonitor.L03;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sun.istack.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoredEndpointRepository extends JpaRepository<MonitoredEndpoint, Long> {
    <S extends MonitoredEndpoint> S save(@NotNull S monitoredEndpoint);

    List<MonitoredEndpoint> findAll();
    Optional<MonitoredEndpoint> findById(Long id);

    @Query("SELECT me FROM MonitoredEndpoint me WHERE me.owner.authorizationId = :id"
            + " ORDER BY me.id ASC")
    Collection<MonitoredEndpoint> getEndpointsByKeycloakId(@Param("id") String keycloakId);

    @Query("SELECT me FROM MonitoredEndpoint me WHERE me.nextCheckDate <= CURRENT_TIMESTAMP" +
            " ORDER BY me.id ASC")
    List<MonitoredEndpoint> getRequiringUpdate();

    void deleteById(Long id);

    @Query("UPDATE MonitoredEndpoint me SET me.lastCheckDate = :checkDate WHERE me = :endpoint")
    void updateEndpointLastCheck(@Param("endpoint") MonitoredEndpoint endpoint,
                                 @Param("endpoint") LocalDateTime checkDate);
}

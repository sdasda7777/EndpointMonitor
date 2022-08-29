package com.sdasda7777.endpointmonitor.L03;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoredEndpointRepository extends JpaRepository<MonitoredEndpoint, Long> {
    <S extends MonitoredEndpoint> S save(S monitoredEndpoint);

    List<MonitoredEndpoint> findAll();
    Optional<MonitoredEndpoint> findById(Long id);

    @Query("SELECT me FROM MonitoredEndpoint me WHERE me.nextCheckDate <= :now")
    List<MonitoredEndpoint> getRequiringUpdate(@Param("now") LocalDateTime now);

    void deleteById(Long id);
}

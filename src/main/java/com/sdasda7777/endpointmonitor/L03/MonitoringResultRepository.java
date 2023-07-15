package com.sdasda7777.endpointmonitor.L03;

import com.sdasda7777.endpointmonitor.layer2.entities.MonitoringResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, Long> {
    <S extends MonitoringResult> S save(S monitoringResult);

    List<MonitoringResult> findAll();
    Optional<MonitoringResult> findById(Long id);

    @Query("SELECT r FROM MonitoringResult r WHERE r.monitoredEndpoint.id = :endpointId" +
            " ORDER BY r.checkDate DESC")
    ArrayList<MonitoringResult> getAllForEndpoint(@Param("endpointId") Long monitoredEndpointId);
}

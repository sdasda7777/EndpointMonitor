package com.sdasda7777.endpointmonitor.L03;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitoredEndpoint;
import com.sdasda7777.endpointmonitor.L02.Entities.MonitoringResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, Long> {
    <S extends MonitoringResult> S save(S monitoringResult);

    List<MonitoringResult> findAll();
    Optional<MonitoringResult> findById(Long id);

    @Query("SELECT r FROM MonitoringResult r WHERE r.monitoredEndpoint.id = :endpointId")
    @OrderBy("checkDate DESC")
    ArrayList<MonitoringResult> getAllForEndpoint(@Param("endpointId") Long monitoredEndpointId);

    void deleteById(Long id);
}

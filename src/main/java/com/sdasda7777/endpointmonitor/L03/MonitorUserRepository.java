package com.sdasda7777.endpointmonitor.L03;

import com.sdasda7777.endpointmonitor.L02.Entities.MonitorUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonitorUserRepository extends JpaRepository<MonitorUser, Long> {
    <S extends MonitorUser> S save(S monitorUser);

    List<MonitorUser> findAll();
    Optional<MonitorUser> findById(Long id);

    void deleteById(Long id);

    @Query("SELECT mu FROM MonitorUser mu WHERE mu.accessToken = :token")
    Collection<MonitorUser> getByToken(@Param("token") String token);
}

package com.example.apacheKafka.repository;


import com.example.apacheKafka.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntry, Long> {

    List<LogEntry> findByServiceNameOrderByTimestampDesc(
            String serviceName
    );

    List<LogEntry> findByLevelOrderByTimestampDesc(
            LogEntry.LogLevel level
    );

    List<LogEntry> findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime start,
            LocalDateTime end
    );
}
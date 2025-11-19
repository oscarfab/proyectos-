package com.example.apacheKafka.controller;

import com.example.apacheKafka.model.LogEntry;

import com.example.apacheKafka.producer.LogProducer;
import com.example.apacheKafka.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogProducer logProducer;
    private final LogRepository logRepository;

    @PostMapping
    public ResponseEntity<String> sendLog(
            @RequestBody LogEntry logEntry
    ) {
        if (logEntry.getTimestamp() == null) {
            logEntry.setTimestamp(LocalDateTime.now());
        }
        logProducer.sendLog(logEntry);
        return ResponseEntity.ok("Log enviado a Kafka");
    }

    @GetMapping("/service/{serviceName}")
    public List<LogEntry> getByService(
            @PathVariable String serviceName
    ) {
        return logRepository
                .findByServiceNameOrderByTimestampDesc(serviceName);
    }

    @GetMapping("/level/{level}")
    public List<LogEntry> getByLevel(
            @PathVariable LogEntry.LogLevel level
    ) {
        return logRepository
                .findByLevelOrderByTimestampDesc(level);
    }

    @GetMapping("/all")
    public List<LogEntry> getAll() {
        return logRepository.findAll();
    }
}
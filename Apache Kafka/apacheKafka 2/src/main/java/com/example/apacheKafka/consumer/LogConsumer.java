package com.example.apacheKafka.consumer;

import com.example.apacheKafka.model.LogEntry;
import com.example.apacheKafka.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogConsumer {

    private final LogRepository logRepository;

    @KafkaListener(
            topics = "${kafka.topic.logs}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3"
    )
    public void consume(
            @Payload LogEntry logEntry,
            Acknowledgment acknowledgment
    ) {
        try {
            log.info("Procesando: {} - {}",
                    logEntry.getServiceName(),
                    logEntry.getLevel());

            logRepository.save(logEntry);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error procesando log", e);
        }
    }
}
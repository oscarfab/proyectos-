package com.example.apacheKafka.producer;
import com.example.apacheKafka.model.LogEntry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class LogProducer {

    private final KafkaTemplate<String, LogEntry> kafkaTemplate;

    @Value("${kafka.topic.logs}")
    private String topic;

    public void sendLog(LogEntry logEntry) {
        String key = logEntry.getServiceName();

        kafkaTemplate.send(topic, key, logEntry)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Log enviado correctamente");
                    } else {
                        log.error("Error enviando log", ex);
                    }
                });
    }
}
package com.example.apacheKafka.consumer;

import com.example.apacheKafka.model.notificacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumer {

    @KafkaListener(
            topics = "${kafka.topic.notifications}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(notificacion notification) {
        log.info("📨 Notificación recibida: {}", notification);

        // Procesar según el tipo
        switch (notification.getType()) {
            case ERROR:
                log.error("🔴 ERROR: {} - {}",
                        notification.getTitle(),
                        notification.getMessage());
                break;
            case WARNING:
                log.warn("🟡 WARNING: {} - {}",
                        notification.getTitle(),
                        notification.getMessage());
                break;
            case SUCCESS:
                log.info("🟢 SUCCESS: {} - {}",
                        notification.getTitle(),
                        notification.getMessage());
                break;
            case INFO:
            default:
                log.info("🔵 INFO: {} - {}",
                        notification.getTitle(),
                        notification.getMessage());
        }

        // Aquí podrías guardar en BD, enviar email, etc.
    }
}
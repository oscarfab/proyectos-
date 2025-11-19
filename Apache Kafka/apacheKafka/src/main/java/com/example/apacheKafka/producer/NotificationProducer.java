package com.example.apacheKafka.producer;
import com.example.apacheKafka.model.notificacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final KafkaTemplate<String, notificacion> kafkaTemplate;

    @Value("${kafka.topic.notifications}")
    private String topic;

    public void sendNotification(notificacion notificacion) {
        log.info("Enviando notificación: {}", notificacion);

        CompletableFuture<SendResult<String, notificacion>> future =
                kafkaTemplate.send(topic, notificacion.getId(), notificacion);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(" Notificación enviada exitosamente: {} con offset: {}",
                        notificacion.getId(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("❌ Error enviando notificación: {}",
                        notificacion.getId(), ex);
            }
        });
    }
}
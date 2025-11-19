package com.example.apacheKafka.controller;

import com.example.apacheKafka.model.notificacion;
import com.example.apacheKafka.producer.NotificationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationProducer notificationProducer;

    @PostMapping
    public ResponseEntity<String> sendNotification(
            @RequestBody NotificationRequest request
    ) {
        notificacion notification = new notificacion(
                UUID.randomUUID().toString(),
                request.getTitle(),
                request.getMessage(),
                request.getRecipient(),
                LocalDateTime.now(),
                request.getType()
        );

        notificationProducer.sendNotification(notification);

        return ResponseEntity.ok("Notificación enviada con ID: " + notification.getId());
    }

    // DTO interno
    @lombok.Data
    public static class NotificationRequest {
        private String title;
        private String message;
        private String recipient;
        private notificacion.NotificationType type;
    }
}
package com.example.apacheKafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class notificacion {
    private String id;
    private String title;
    private String message;
    private String recipient;
    private LocalDateTime timestamp;
    private NotificationType type;

    public enum NotificationType {
        INFO, WARNING, ERROR, SUCCESS
    }
}

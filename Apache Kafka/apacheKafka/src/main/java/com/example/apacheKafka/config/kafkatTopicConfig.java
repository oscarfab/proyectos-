package com.example.apacheKafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
@Configuration
public class kafkatTopicConfig {
    @Value("${kafka.topic.notifications}")
    private String notificationTopic;

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder
                .name(notificationTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

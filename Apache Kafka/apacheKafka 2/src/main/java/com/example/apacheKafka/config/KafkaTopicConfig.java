package com.example.apacheKafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.logs}")
    private String logsTopic;

    @Value("${kafka.partitions}")
    private Integer partitions;

    @Bean
    public NewTopic logsTopic() {
        return TopicBuilder
                .name(logsTopic)
                .partitions(partitions)  // 3 particiones para paralelismo
                .replicas(1)             // 1 réplica (desarrollo local)
                .build();
    }
}

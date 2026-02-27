package org.roshan.kafka.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.roshan.kafka.model.ClusterConfig;
import java.util.*;
import java.util.concurrent.Future;

@Service(Service.Level.PROJECT)
public final class KafkaProducerService {
    
    public static KafkaProducerService getInstance(Project project) {
        return project.getService(KafkaProducerService.class);
    }

    public Future<RecordMetadata> publishMessage(ClusterConfig config, String topic, String key, String value, Integer partition, Map<String, String> headers) {
        Properties props = new Properties();
        props.putAll(config.getProperties());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        if (config.getSchemaRegistryUrl() != null) {
            props.put("schema.registry.url", config.getSchemaRegistryUrl());
        }

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            ProducerRecord<String, String> record = partition != null 
                ? new ProducerRecord<>(topic, partition, key, value)
                : new ProducerRecord<>(topic, key, value);
            
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    record.headers().add(entry.getKey(), entry.getValue().getBytes());
                }
            }
            
            return producer.send(record);
        }
    }

    public void publishBulkMessages(ClusterConfig config, String topic, String template, int count) {
        Properties props = new Properties();
        props.putAll(config.getProperties());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            for (int i = 0; i < count; i++) {
                String message = template.replace("{{index}}", String.valueOf(i))
                    .replace("{{timestamp}}", String.valueOf(System.currentTimeMillis()));
                producer.send(new ProducerRecord<>(topic, "key-" + i, message));
            }
            producer.flush();
        }
    }

    public Future<RecordMetadata> publishAvroMessage(ClusterConfig config, String topic, String key, String avroJson) {
        Properties props = new Properties();
        props.putAll(config.getProperties());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("schema.registry.url", config.getSchemaRegistryUrl());

        try (KafkaProducer<String, Object> producer = new KafkaProducer<>(props)) {
            return producer.send(new ProducerRecord<>(topic, key, avroJson));
        }
    }
}

package org.roshan.kafka.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.apache.kafka.clients.producer.*;
import org.roshan.kafka.model.ClusterConfig;
import java.util.*;
import java.util.concurrent.Future;

@Service(Service.Level.PROJECT)
public final class KafkaProducerService {
    
    public static KafkaProducerService getInstance(Project project) {
        return project.getService(KafkaProducerService.class);
    }

    public Future<RecordMetadata> publishMessage(ClusterConfig config, String topic, String key, String value, Integer partition, Map<String, String> headers) {
        Properties props = config.getProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, config.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, config.getValueSerializer());
        
        if (config.getSchemaRegistryUrl() != null && !config.getSchemaRegistryUrl().isEmpty()) {
            props.put("schema.registry.url", config.getSchemaRegistryUrl());
        }

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        ProducerRecord<String, String> record = partition != null 
            ? new ProducerRecord<>(topic, partition, key, value)
            : new ProducerRecord<>(topic, key, value);
        
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                record.headers().add(entry.getKey(), entry.getValue().getBytes());
            }
        }
        
        Future<RecordMetadata> result = producer.send(record);
        producer.flush();
        producer.close();
        return result;
    }

    public void publishBulkMessages(ClusterConfig config, String topic, String template, int count) {
        Properties props = config.getProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, config.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, config.getValueSerializer());
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
        Properties props = config.getProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, config.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");
        if (config.getSchemaRegistryUrl() != null && !config.getSchemaRegistryUrl().isEmpty()) {
            props.put("schema.registry.url", config.getSchemaRegistryUrl());
        }

        try (KafkaProducer<String, Object> producer = new KafkaProducer<>(props)) {
            return producer.send(new ProducerRecord<>(topic, key, avroJson));
        }
    }
}

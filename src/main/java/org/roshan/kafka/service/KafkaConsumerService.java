package org.roshan.kafka.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.roshan.kafka.model.*;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service(Service.Level.PROJECT)
public final class KafkaConsumerService {
    
    public static KafkaConsumerService getInstance(Project project) {
        return project.getService(KafkaConsumerService.class);
    }

    public List<KafkaMessage> consumeLatest(ClusterConfig config, String topic, int maxMessages, long pollTimeoutMs) {
        Properties props = createConsumerProps(config);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singleton(topic));
            return pollMessages(consumer, maxMessages, pollTimeoutMs);
        }
    }

    public List<KafkaMessage> consumeFromBeginning(ClusterConfig config, String topic, int maxMessages, long pollTimeoutMs) {
        Properties props = createConsumerProps(config);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singleton(topic));
            return pollMessages(consumer, maxMessages, pollTimeoutMs);
        }
    }

    public List<KafkaMessage> consumeFromPartitionOffset(ClusterConfig config, String topic, int partition, long offset, int maxMessages) {
        Properties props = createConsumerProps(config);
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            TopicPartition tp = new TopicPartition(topic, partition);
            consumer.assign(Collections.singleton(tp));
            consumer.seek(tp, offset);
            return pollMessages(consumer, maxMessages, 5000);
        }
    }

    public List<KafkaMessage> searchMessages(ClusterConfig config, String topic, String keyPattern, String valuePattern, int maxMessages) {
        Properties props = createConsumerProps(config);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        Pattern keyRegex = keyPattern != null ? Pattern.compile(keyPattern) : null;
        Pattern valueRegex = valuePattern != null ? Pattern.compile(valuePattern) : null;
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singleton(topic));
            List<KafkaMessage> results = new ArrayList<>();
            
            while (results.size() < maxMessages) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
                if (records.isEmpty()) break;
                
                for (ConsumerRecord<String, String> record : records) {
                    boolean matches = true;
                    if (keyRegex != null && (record.key() == null || !keyRegex.matcher(record.key()).find())) {
                        matches = false;
                    }
                    if (valueRegex != null && (record.value() == null || !valueRegex.matcher(record.value()).find())) {
                        matches = false;
                    }
                    if (matches) {
                        results.add(new KafkaMessage(record.key(), record.value(), 
                            record.partition(), record.offset(), record.timestamp()));
                        if (results.size() >= maxMessages) break;
                    }
                }
            }
            return results;
        }
    }

    public void copyTopicMessages(ClusterConfig sourceConfig, String sourceTopic, 
                                   ClusterConfig targetConfig, String targetTopic) {
        Properties consumerProps = createConsumerProps(sourceConfig);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        Properties producerProps = new Properties();
        producerProps.putAll(targetConfig.getProperties());
        producerProps.put("bootstrap.servers", targetConfig.getBootstrapServers());
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
             org.apache.kafka.clients.producer.KafkaProducer<String, String> producer = 
                 new org.apache.kafka.clients.producer.KafkaProducer<>(producerProps)) {
            
            consumer.subscribe(Collections.singleton(sourceTopic));
            
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
                if (records.isEmpty()) break;
                
                for (ConsumerRecord<String, String> record : records) {
                    producer.send(new org.apache.kafka.clients.producer.ProducerRecord<>(
                        targetTopic, record.key(), record.value()));
                }
                producer.flush();
            }
        }
    }

    private Properties createConsumerProps(ClusterConfig config) {
        Properties props = new Properties();
        props.putAll(config.getProperties());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-client-plugin-" + UUID.randomUUID());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return props;
    }

    private List<KafkaMessage> pollMessages(KafkaConsumer<String, String> consumer, int maxMessages, long pollTimeoutMs) {
        List<KafkaMessage> messages = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        while (messages.size() < maxMessages && (System.currentTimeMillis() - startTime) < pollTimeoutMs) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                messages.add(new KafkaMessage(record.key(), record.value(), 
                    record.partition(), record.offset(), record.timestamp()));
                if (messages.size() >= maxMessages) break;
            }
            if (records.isEmpty()) break;
        }
        return messages;
    }
}

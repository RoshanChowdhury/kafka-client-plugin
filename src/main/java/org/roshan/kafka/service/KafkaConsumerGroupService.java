package org.roshan.kafka.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.roshan.kafka.model.ClusterConfig;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service(Service.Level.PROJECT)
public final class KafkaConsumerGroupService {
    
    public static KafkaConsumerGroupService getInstance(Project project) {
        return project.getService(KafkaConsumerGroupService.class);
    }

    public List<String> listConsumerGroups(ClusterConfig config) throws ExecutionException, InterruptedException {
        try (AdminClient admin = createAdminClient(config)) {
            return admin.listConsumerGroups().all().get().stream()
                .map(ConsumerGroupListing::groupId)
                .collect(Collectors.toList());
        }
    }

    public Map<TopicPartition, Long> getConsumerGroupLag(ClusterConfig config, String groupId) throws ExecutionException, InterruptedException {
        try (AdminClient admin = createAdminClient(config)) {
            Map<TopicPartition, OffsetAndMetadata> offsets = admin.listConsumerGroupOffsets(groupId)
                .partitionsToOffsetAndMetadata().get();
            
            Map<TopicPartition, Long> endOffsets = getEndOffsets(config, offsets.keySet());
            Map<TopicPartition, Long> lag = new HashMap<>();
            
            for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : offsets.entrySet()) {
                TopicPartition tp = entry.getKey();
                long currentOffset = entry.getValue().offset();
                long endOffset = endOffsets.getOrDefault(tp, 0L);
                lag.put(tp, endOffset - currentOffset);
            }
            return lag;
        }
    }

    public void resetOffsets(ClusterConfig config, String groupId, Map<TopicPartition, Long> offsets) throws ExecutionException, InterruptedException {
        try (AdminClient admin = createAdminClient(config)) {
            Map<TopicPartition, OffsetAndMetadata> offsetsToReset = offsets.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new OffsetAndMetadata(e.getValue())));
            admin.alterConsumerGroupOffsets(groupId, offsetsToReset).all().get();
        }
    }

    public void resetOffsetsToBeginning(ClusterConfig config, String groupId, Set<TopicPartition> partitions) throws ExecutionException, InterruptedException {
        Map<TopicPartition, Long> offsets = partitions.stream()
            .collect(Collectors.toMap(tp -> tp, tp -> 0L));
        resetOffsets(config, groupId, offsets);
    }

    public void resetOffsetsToEnd(ClusterConfig config, String groupId, Set<TopicPartition> partitions) throws ExecutionException, InterruptedException {
        Map<TopicPartition, Long> endOffsets = getEndOffsets(config, partitions);
        resetOffsets(config, groupId, endOffsets);
    }

    public ConsumerGroupDescription describeConsumerGroup(ClusterConfig config, String groupId) throws ExecutionException, InterruptedException {
        try (AdminClient admin = createAdminClient(config)) {
            return admin.describeConsumerGroups(Collections.singleton(groupId))
                .all().get().get(groupId);
        }
    }

    private Map<TopicPartition, Long> getEndOffsets(ClusterConfig config, Set<TopicPartition> partitions) {
        Properties props = new Properties();
        props.putAll(config.getProperties());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "temp-group-" + UUID.randomUUID());
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            return consumer.endOffsets(partitions);
        }
    }

    private AdminClient createAdminClient(ClusterConfig config) {
        Properties props = new Properties();
        props.putAll(config.getProperties());
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        return AdminClient.create(props);
    }
}

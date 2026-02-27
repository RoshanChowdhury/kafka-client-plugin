package org.roshan.kafka.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartition;
import org.roshan.kafka.model.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service(Service.Level.PROJECT)
public final class KafkaAdminService {
    
    public static KafkaAdminService getInstance(Project project) {
        return project.getService(KafkaAdminService.class);
    }

    public List<TopicInfo> listTopics(ClusterConfig config, boolean includeInternal) throws ExecutionException, InterruptedException {
        try (AdminClient admin = createAdminClient(config)) {
            ListTopicsOptions options = new ListTopicsOptions().listInternal(includeInternal);
            Set<String> topicNames = admin.listTopics(options).names().get();
            DescribeTopicsResult result = admin.describeTopics(topicNames);
            
            return result.allTopicNames().get().entrySet().stream()
                .map(e -> new TopicInfo(e.getKey(), e.getValue().partitions().size(), 
                    e.getValue().partitions().get(0).replicas().size(), e.getValue().isInternal()))
                .collect(Collectors.toList());
        }
    }

    public List<TopicInfo> searchTopics(ClusterConfig config, String regex, boolean includeInternal) throws Exception {
        Pattern pattern = Pattern.compile(regex);
        return listTopics(config, includeInternal).stream()
            .filter(t -> pattern.matcher(t.getName()).matches())
            .collect(Collectors.toList());
    }

    public void createTopic(ClusterConfig config, String name, int partitions, short replicationFactor) throws ExecutionException, InterruptedException {
        try (AdminClient admin = createAdminClient(config)) {
            NewTopic topic = new NewTopic(name, partitions, replicationFactor);
            admin.createTopics(Collections.singleton(topic)).all().get();
        }
    }

    public void deleteTopic(ClusterConfig config, String name) throws ExecutionException, InterruptedException {
        try (AdminClient admin = createAdminClient(config)) {
            admin.deleteTopics(Collections.singleton(name)).all().get();
        }
    }

    public void modifyPartitions(ClusterConfig config, String topic, int newPartitions) throws ExecutionException, InterruptedException {
        try (AdminClient admin = createAdminClient(config)) {
            Map<String, NewPartitions> map = new HashMap<>();
            map.put(topic, NewPartitions.increaseTo(newPartitions));
            admin.createPartitions(map).all().get();
        }
    }

    public void deleteRecords(ClusterConfig config, String topic, Map<Integer, Long> partitionOffsets) throws ExecutionException, InterruptedException {
        try (AdminClient admin = createAdminClient(config)) {
            Map<TopicPartition, RecordsToDelete> recordsToDelete = partitionOffsets.entrySet().stream()
                .collect(Collectors.toMap(
                    e -> new TopicPartition(topic, e.getKey()),
                    e -> RecordsToDelete.beforeOffset(e.getValue())
                ));
            admin.deleteRecords(recordsToDelete).all().get();
        }
    }

    private AdminClient createAdminClient(ClusterConfig config) {
        Properties props = new Properties();
        props.putAll(config.getProperties());
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        return AdminClient.create(props);
    }
}

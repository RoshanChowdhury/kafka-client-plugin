package org.roshan.kafka.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.roshan.kafka.model.ClusterConfig;
import java.util.*;

@Service(Service.Level.PROJECT)
public final class KafkaClusterManager {
    private final Map<String, ClusterConfig> clusters = new LinkedHashMap<>();

    public static KafkaClusterManager getInstance(Project project) {
        return project.getService(KafkaClusterManager.class);
    }

    public void addCluster(ClusterConfig config) {
        clusters.put(config.getName(), config);
    }

    public void removeCluster(String name) {
        clusters.remove(name);
    }

    public ClusterConfig getCluster(String name) {
        return clusters.get(name);
    }

    public List<ClusterConfig> getAllClusters() {
        return new ArrayList<>(clusters.values());
    }
}

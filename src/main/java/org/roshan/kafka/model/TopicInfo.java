package org.roshan.kafka.model;

public class TopicInfo {
    private String name;
    private int partitions;
    private int replicationFactor;
    private boolean isInternal;

    public TopicInfo(String name, int partitions, int replicationFactor, boolean isInternal) {
        this.name = name;
        this.partitions = partitions;
        this.replicationFactor = replicationFactor;
        this.isInternal = isInternal;
    }

    public String getName() { return name; }
    public int getPartitions() { return partitions; }
    public int getReplicationFactor() { return replicationFactor; }
    public boolean isInternal() { return isInternal; }
}

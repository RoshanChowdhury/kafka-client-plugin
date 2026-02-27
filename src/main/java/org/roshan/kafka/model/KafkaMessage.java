package org.roshan.kafka.model;

public class KafkaMessage {
    private String key;
    private String value;
    private int partition;
    private long offset;
    private long timestamp;

    public KafkaMessage(String key, String value, int partition, long offset, long timestamp) {
        this.key = key;
        this.value = value;
        this.partition = partition;
        this.offset = offset;
        this.timestamp = timestamp;
    }

    public String getKey() { return key; }
    public String getValue() { return value; }
    public int getPartition() { return partition; }
    public long getOffset() { return offset; }
    public long getTimestamp() { return timestamp; }
}

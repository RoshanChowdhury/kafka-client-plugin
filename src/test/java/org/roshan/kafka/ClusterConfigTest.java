package org.roshan.kafka;


import org.junit.jupiter.api.Test;
import org.roshan.kafka.model.ClusterConfig;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ClusterConfigTest {

    @Test
    public void testPlaintextConfig() {
        ClusterConfig config = new ClusterConfig("test", "localhost:9092");
        Properties props = config.getProperties();
        
        assertEquals("localhost:9092", props.getProperty("bootstrap.servers"));
        assertEquals("PLAINTEXT", props.getProperty("security.protocol"));
    }

    @Test
    public void testSSLConfig() {
        ClusterConfig config = new ClusterConfig("test", "localhost:9092");
        config.setSecurityProtocol("SSL");
        config.setTruststoreLocation("/path/to/truststore.jks");
        config.setTruststorePassword("password");
        
        Properties props = config.getProperties();
        
        assertEquals("SSL", props.getProperty("security.protocol"));
        assertEquals("/path/to/truststore.jks", props.getProperty("ssl.truststore.location"));
        assertEquals("password", props.getProperty("ssl.truststore.password"));
        assertEquals("JKS", props.getProperty("ssl.truststore.type"));
    }

    @Test
    public void testSerializerDefaults() {
        ClusterConfig config = new ClusterConfig("test", "localhost:9092");
        
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", config.getKeySerializer());
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", config.getValueSerializer());
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", config.getKeyDeserializer());
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", config.getValueDeserializer());
    }

    @Test
    public void testCustomSerializers() {
        ClusterConfig config = new ClusterConfig("test", "localhost:9092");
        config.setKeySerializer("io.confluent.kafka.serializers.KafkaAvroSerializer");
        config.setValueSerializer("io.confluent.kafka.serializers.KafkaAvroSerializer");
        
        assertEquals("io.confluent.kafka.serializers.KafkaAvroSerializer", config.getKeySerializer());
        assertEquals("io.confluent.kafka.serializers.KafkaAvroSerializer", config.getValueSerializer());
    }
}

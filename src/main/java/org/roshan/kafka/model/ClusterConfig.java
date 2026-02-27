package org.roshan.kafka.model;

import java.util.Properties;

public class ClusterConfig {
    private String name;
    private String bootstrapServers;
    private Properties properties;
    private String schemaRegistryUrl;
    private String securityProtocol;
    private String saslMechanism;
    private String saslUsername;
    private String saslPassword;
    private String truststoreLocation;
    private String truststorePassword;
    private String keystoreLocation;
    private String keystorePassword;

    public ClusterConfig(String name, String bootstrapServers) {
        this.name = name;
        this.bootstrapServers = bootstrapServers;
        this.properties = new Properties();
        this.securityProtocol = "PLAINTEXT";
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBootstrapServers() { return bootstrapServers; }
    public void setBootstrapServers(String bootstrapServers) { this.bootstrapServers = bootstrapServers; }
    public Properties getProperties() { 
        Properties props = new Properties(properties);
        props.put("bootstrap.servers", bootstrapServers);
        props.put("security.protocol", securityProtocol);
        
        if ("SASL_PLAINTEXT".equals(securityProtocol) || "SASL_SSL".equals(securityProtocol)) {
            if (saslMechanism != null && !saslMechanism.isEmpty()) {
                props.put("sasl.mechanism", saslMechanism);
                if (saslUsername != null && saslPassword != null) {
                    String jaasConfig = String.format(
                        "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                        saslUsername, saslPassword
                    );
                    props.put("sasl.jaas.config", jaasConfig);
                }
            }
        }
        
        if ("SSL".equals(securityProtocol) || "SASL_SSL".equals(securityProtocol)) {
            if (truststoreLocation != null && !truststoreLocation.isEmpty()) {
                props.put("ssl.truststore.location", truststoreLocation);
                if (truststorePassword != null) props.put("ssl.truststore.password", truststorePassword);
            }
            if (keystoreLocation != null && !keystoreLocation.isEmpty()) {
                props.put("ssl.keystore.location", keystoreLocation);
                if (keystorePassword != null) props.put("ssl.keystore.password", keystorePassword);
            }
        }
        
        return props;
    }
    public void setProperties(Properties properties) { this.properties = properties; }
    public String getSchemaRegistryUrl() { return schemaRegistryUrl; }
    public void setSchemaRegistryUrl(String url) { this.schemaRegistryUrl = url; }
    
    public String getSecurityProtocol() { return securityProtocol; }
    public void setSecurityProtocol(String securityProtocol) { this.securityProtocol = securityProtocol; }
    public String getSaslMechanism() { return saslMechanism; }
    public void setSaslMechanism(String saslMechanism) { this.saslMechanism = saslMechanism; }
    public String getSaslUsername() { return saslUsername; }
    public void setSaslUsername(String saslUsername) { this.saslUsername = saslUsername; }
    public String getSaslPassword() { return saslPassword; }
    public void setSaslPassword(String saslPassword) { this.saslPassword = saslPassword; }
    public String getTruststoreLocation() { return truststoreLocation; }
    public void setTruststoreLocation(String truststoreLocation) { this.truststoreLocation = truststoreLocation; }
    public String getTruststorePassword() { return truststorePassword; }
    public void setTruststorePassword(String truststorePassword) { this.truststorePassword = truststorePassword; }
    public String getKeystoreLocation() { return keystoreLocation; }
    public void setKeystoreLocation(String keystoreLocation) { this.keystoreLocation = keystoreLocation; }
    public String getKeystorePassword() { return keystorePassword; }
    public void setKeystorePassword(String keystorePassword) { this.keystorePassword = keystorePassword; }

    @Override
    public String toString() {
        return name;
    }
}

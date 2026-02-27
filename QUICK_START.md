# Kafka Client Plugin - Quick Start Guide

## Build & Run

### Option 1: Build Plugin Distribution
```bash
cd /Users/roshan/IdeaProjects/YAML-Properties-Viewer
./gradlew :kafka-client-plugin:buildPlugin
```
Output: `kafka-client-plugin/build/distributions/kafka-client-plugin-1.0.0.zip`

### Option 2: Run in Development Mode
```bash
./gradlew :kafka-client-plugin:runIde
```
This launches a new IntelliJ instance with the plugin installed.

## Installation

1. Build the plugin (see above)
2. Open IntelliJ IDEA
3. Go to `Settings/Preferences` → `Plugins`
4. Click `⚙️` (gear icon) → `Install Plugin from Disk...`
5. Select the ZIP file from `kafka-client-plugin/build/distributions/`
6. Restart IntelliJ IDEA

## First Use

### 1. Open Kafka Client Tool Window
- Look for "Kafka Client" in the right sidebar
- Or: `View` → `Tool Windows` → `Kafka Client`

### 2. Add Your First Cluster
- Click "Add Cluster" button in toolbar
- Enter:
  - **Cluster Name**: `local-kafka` (or any name)
  - **Bootstrap Servers**: `localhost:9092` (your Kafka broker)
  - **Schema Registry URL**: `http://localhost:8081` (optional)

### 3. Explore Topics
- Select your cluster in the tree
- Topics will load automatically
- Use "Refresh Topics" to reload

### 4. Create a Test Topic
- Click "Create Topic"
- Enter:
  - **Name**: `test-topic`
  - **Partitions**: `3`
  - **Replication Factor**: `1`

### 5. Publish Messages
- Select `test-topic`
- Click "Publish Message"
- Enter key and value
- Message is published immediately

### 6. Consume Messages
- Select `test-topic`
- Click "Consume Messages"
- Choose strategy: "From Beginning"
- View messages in the messages table

## Common Use Cases

### Bulk Testing
```
1. Click "Bulk Generate Messages"
2. Template: {"id": {{index}}, "timestamp": {{timestamp}}, "data": "test"}
3. Count: 10000
4. Messages generated in seconds
```

### Topic Migration
```
1. Select source topic
2. Click "Copy Topic"
3. Enter target topic name
4. Select target cluster (can be different)
5. All messages copied asynchronously
```

### Message Search
```
1. Select topic
2. Click "Search Messages"
3. Key Pattern: user-.*
4. Value Pattern: .*error.*
5. View matching messages
```

### Delete Old Messages
```
1. Use KafkaAdminService.deleteRecords()
2. Specify partition and offset
3. Messages before offset are deleted
```

## Troubleshooting

### Cannot Connect to Kafka
- Verify bootstrap servers address
- Check Kafka is running: `kafka-topics.sh --list --bootstrap-server localhost:9092`
- Check network/firewall settings

### Schema Registry Errors
- Ensure Schema Registry URL is correct
- Verify Schema Registry is running
- Check subject compatibility settings

### Plugin Not Loading
- Check IntelliJ version (requires 2024.3.3+)
- Verify Java 17+ is configured
- Check IDE logs: `Help` → `Show Log in Finder/Explorer`

## Development

### Project Structure
```
kafka-client-plugin/
├── src/main/java/org/roshan/kafka/
│   ├── action/     # UI actions
│   ├── model/      # Data models
│   ├── service/    # Business logic
│   └── ui/         # UI components
└── src/main/resources/META-INF/
    └── plugin.xml  # Plugin configuration
```

### Adding New Features

1. **New Action**: Create class in `action/` package extending `AnAction`
2. **Register**: Add to `plugin.xml` in `<actions>` section
3. **Service Logic**: Implement in appropriate service class
4. **UI Update**: Modify `KafkaToolWindowPanel` if needed

### Testing Locally

```bash
# Start local Kafka (Docker)
docker run -d --name kafka -p 9092:9092 apache/kafka:latest

# Run plugin
./gradlew :kafka-client-plugin:runIde

# Test in opened IntelliJ instance
```

## Configuration Examples

### Cluster with Authentication
```java
ClusterConfig config = new ClusterConfig("secure-cluster", "broker:9093");
Properties props = config.getProperties();
props.put("security.protocol", "SASL_SSL");
props.put("sasl.mechanism", "PLAIN");
props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"pass\";");
```

### Schema Registry with Authentication
```java
config.setSchemaRegistryUrl("https://schema-registry:8081");
Properties props = config.getProperties();
props.put("basic.auth.credentials.source", "USER_INFO");
props.put("basic.auth.user.info", "username:password");
```

## Resources

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Confluent Schema Registry](https://docs.confluent.io/platform/current/schema-registry/index.html)
- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)

## Support

For issues or questions:
1. Check the implementation documentation: `KAFKA_PLUGIN_IMPLEMENTATION.md`
2. Review the README: `kafka-client-plugin/README.md`
3. Check IntelliJ logs for errors

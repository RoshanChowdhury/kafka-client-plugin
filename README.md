# Kafka Client Plugin for IntelliJ IDEA

A powerful Apache Kafka client plugin for IntelliJ IDEA that provides comprehensive Kafka management capabilities directly within your IDE.

## Features

### ✅ Multiple Cluster Support
- Manage multiple Kafka clusters simultaneously
- Easy cluster configuration with bootstrap servers
- Schema Registry integration support

### ✅ Topic Management
- **Create Topics**: Define partitions and replication factor
- **Delete Topics**: Remove unwanted topics
- **Modify Partitions**: Increase partition count for existing topics
- **Display System Topics**: View internal Kafka topics
- **Search Topics**: Use regular expressions to find topics

### ✅ Message Consumption
- **Multiple Consumption Strategies**:
  - Consume from beginning
  - Consume latest messages
  - Consume from specific partition and offset
- **Configurable Poll Time**: Wait for messages within specified timeout
- **Partition-Specific Reading**: Target specific partitions

### ✅ Message Publishing
- **String/Byte Serialization**: Publish simple messages
- **Schema Registry Integration**:
  - JSON serialization
  - AVRO serialization
  - Protobuf serialization
- **Partition Control**: Publish to specific partitions

### ✅ Bulk Message Generation
- Generate large volumes of messages quickly
- Template-based message creation with placeholders:
  - `{{index}}` - Message sequence number
  - `{{timestamp}}` - Current timestamp
- High-performance batch publishing

### ✅ Advanced Operations
- **Copy Topics**: Copy messages from one topic to another (same or different cluster)
- **Delete Messages**: Remove messages from topics using offset-based deletion
- **Search Messages**: Find messages using regex patterns on keys or values

## Installation

1. Build the plugin:
   ```bash
   ./gradlew :kafka-client-plugin:buildPlugin
   ```

2. Install in IntelliJ IDEA:
   - Go to `Settings` → `Plugins` → `⚙️` → `Install Plugin from Disk`
   - Select the built plugin ZIP from `kafka-client-plugin/build/distributions/`

## Usage

### Adding a Cluster
1. Open the "Kafka Client" tool window (right sidebar)
2. Click "Add Cluster" action
3. Enter cluster name, bootstrap servers, and optional Schema Registry URL

### Managing Topics
- **Create**: Use "Create Topic" action, specify name, partitions, and replication factor
- **Delete**: Select topic and use "Delete Topic" action
- **Search**: Use regex patterns to filter topics
- **Refresh**: Update topic list with "Refresh Topics" action

### Publishing Messages
1. Select a topic
2. Click "Publish Message" action
3. Enter key and value
4. Optionally specify partition

### Consuming Messages
1. Select a topic
2. Click "Consume Messages" action
3. Choose consumption strategy:
   - From Beginning
   - Latest
   - Specific Offset

### Bulk Generation
1. Select a topic
2. Click "Bulk Generate Messages" action
3. Define template: `{"id": {{index}}, "timestamp": {{timestamp}}}`
4. Specify message count

### Copying Topics
1. Select source topic
2. Click "Copy Topic" action
3. Specify target topic and cluster
4. Messages will be copied asynchronously

### Searching Messages
1. Select a topic
2. Click "Search Messages" action
3. Enter regex patterns for key and/or value
4. View matching messages

## Architecture

### Services
- **KafkaClusterManager**: Manages cluster configurations
- **KafkaAdminService**: Handles topic operations (create, delete, modify)
- **KafkaProducerService**: Manages message publishing
- **KafkaConsumerService**: Handles message consumption and search

### Models
- **ClusterConfig**: Cluster connection details
- **TopicInfo**: Topic metadata
- **KafkaMessage**: Message representation

### UI Components
- **KafkaToolWindowFactory**: Tool window initialization
- **KafkaToolWindowPanel**: Main UI with cluster tree, topics table, and messages table

## Dependencies

- Apache Kafka Clients 3.6.1
- Confluent Schema Registry Serializers 7.5.3
- IntelliJ Platform SDK

## Requirements

- IntelliJ IDEA 2024.3.3 or later
- Java 17 or later

## License

Copyright © 2024 Roshan

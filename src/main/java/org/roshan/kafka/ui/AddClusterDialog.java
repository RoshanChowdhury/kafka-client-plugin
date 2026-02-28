package org.roshan.kafka.ui;

import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.*;
import com.google.gson.*;
import org.jetbrains.annotations.Nullable;
import org.roshan.kafka.model.ClusterConfig;
import org.roshan.kafka.service.KafkaAdminService;
import javax.swing.*;
import java.awt.*;
import java.io.FileReader;

public class AddClusterDialog extends DialogWrapper {
    private final Project project;
    private JBTextField nameField;
    private JBTextField bootstrapServersField;
    private JBTextField schemaRegistryField;
    private JComboBox<String> securityProtocolCombo;
    private JBTextField saslMechanismField;
    private JBTextField saslUsernameField;
    private JPasswordField saslPasswordField;
    private JBTextField truststoreLocationField;
    private JPasswordField truststorePasswordField;
    private JBTextField keystoreLocationField;
    private JPasswordField keystorePasswordField;
    private JComboBox<String> keySerializerCombo;
    private JComboBox<String> valueSerializerCombo;
    private JComboBox<String> keyDeserializerCombo;
    private JComboBox<String> valueDeserializerCombo;

    private static final String[] SERIALIZERS = {
            "org.apache.kafka.common.serialization.StringSerializer",
            "org.apache.kafka.common.serialization.ByteArraySerializer",
            "io.confluent.kafka.serializers.KafkaAvroSerializer",
            "io.confluent.kafka.serializers.KafkaJsonSerializer",
            "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer"
    };

    private static final String[] DESERIALIZERS = {
            "org.apache.kafka.common.serialization.StringDeserializer",
            "org.apache.kafka.common.serialization.ByteArrayDeserializer",
            "io.confluent.kafka.serializers.KafkaAvroDeserializer",
            "io.confluent.kafka.serializers.KafkaJsonDeserializer",
            "io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer"
    };

    public AddClusterDialog(Project project) {
        super(project);
        this.project = project;
        setTitle("Add Kafka Cluster");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        nameField = new JBTextField();
        bootstrapServersField = new JBTextField();
        schemaRegistryField = new JBTextField();
        securityProtocolCombo = new JComboBox<>(new String[]{"PLAINTEXT", "SSL", "SASL_PLAINTEXT", "SASL_SSL"});
        saslMechanismField = new JBTextField();
        saslUsernameField = new JBTextField();
        saslPasswordField = new JPasswordField();
        truststoreLocationField = new JBTextField();
        truststorePasswordField = new JPasswordField();
        keystoreLocationField = new JBTextField();
        keystorePasswordField = new JPasswordField();
        keySerializerCombo = new JComboBox<>(SERIALIZERS);
        valueSerializerCombo = new JComboBox<>(SERIALIZERS);
        keyDeserializerCombo = new JComboBox<>(DESERIALIZERS);
        valueDeserializerCombo = new JComboBox<>(DESERIALIZERS);

        int row = 0;
        addField(fieldsPanel, gbc, row++, "Cluster Name*:", nameField);
        addField(fieldsPanel, gbc, row++, "Bootstrap Servers*:", bootstrapServersField);
        addField(fieldsPanel, gbc, row++, "Schema Registry URL:", schemaRegistryField);
        addField(fieldsPanel, gbc, row++, "Security Protocol:", securityProtocolCombo);
        addField(fieldsPanel, gbc, row++, "SASL Mechanism:", saslMechanismField);
        addField(fieldsPanel, gbc, row++, "SASL Username:", saslUsernameField);
        addField(fieldsPanel, gbc, row++, "SASL Password:", saslPasswordField);
        addField(fieldsPanel, gbc, row++, "Truststore Location:", truststoreLocationField);
        addField(fieldsPanel, gbc, row++, "Truststore Password:", truststorePasswordField);
        addField(fieldsPanel, gbc, row++, "Keystore Location:", keystoreLocationField);
        addField(fieldsPanel, gbc, row++, "Keystore Password:", keystorePasswordField);
        addField(fieldsPanel, gbc, row++, "Key Serializer:", keySerializerCombo);
        addField(fieldsPanel, gbc, row++, "Value Serializer:", valueSerializerCombo);
        addField(fieldsPanel, gbc, row++, "Key Deserializer:", keyDeserializerCombo);
        addField(fieldsPanel, gbc, row++, "Value Deserializer:", valueDeserializerCombo);

        mainPanel.add(new JBScrollPane(fieldsPanel), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton testButton = new JButton("Test Connection");
        JButton importButton = new JButton("Import Config");

        testButton.addActionListener(e -> testConnection());
        importButton.addActionListener(e -> importConfig());

        buttonPanel.add(testButton);
        buttonPanel.add(importButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void testConnection() {
        ClusterConfig config = buildConfig();
        if (config == null) return;

        new Thread(() -> {
            try {
                KafkaAdminService.getInstance(project).listTopics(config, false);
                SwingUtilities.invokeLater(() ->
                        Messages.showInfoMessage(project, "Connection successful!", "Test Connection"));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        Messages.showErrorDialog(project, "Connection failed: " + ex.getMessage(), "Test Connection"));
            }
        }).start();
    }

    private void importConfig() {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("json");
        VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
        if (file != null) {
            try (FileReader reader = new FileReader(file.getPath())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                nameField.setText(json.has("name") ? json.get("name").getAsString() : "");
                bootstrapServersField.setText(json.has("bootstrapServers") ? json.get("bootstrapServers").getAsString() : "");
                schemaRegistryField.setText(json.has("schemaRegistryUrl") ? json.get("schemaRegistryUrl").getAsString() : "");
                if (json.has("securityProtocol")) securityProtocolCombo.setSelectedItem(json.get("securityProtocol").getAsString());
                saslMechanismField.setText(json.has("saslMechanism") ? json.get("saslMechanism").getAsString() : "");
                saslUsernameField.setText(json.has("saslUsername") ? json.get("saslUsername").getAsString() : "");
                truststoreLocationField.setText(json.has("truststoreLocation") ? json.get("truststoreLocation").getAsString() : "");
                keystoreLocationField.setText(json.has("keystoreLocation") ? json.get("keystoreLocation").getAsString() : "");
                Messages.showInfoMessage(project, "Configuration imported successfully", "Import");
            } catch (Exception ex) {
                Messages.showErrorDialog(project, "Failed to import: " + ex.getMessage(), "Import Error");
            }
        }
    }

    private ClusterConfig buildConfig() {
        String name = nameField.getText().trim();
        String servers = bootstrapServersField.getText().trim();
        if (name.isEmpty() || servers.isEmpty()) {
            Messages.showErrorDialog(project, "Name and bootstrap servers are required", "Error");
            return null;
        }

        ClusterConfig config = new ClusterConfig(name, servers);
        config.setSchemaRegistryUrl(schemaRegistryField.getText().trim());
        config.setSecurityProtocol((String) securityProtocolCombo.getSelectedItem());
        config.setSaslMechanism(saslMechanismField.getText().trim());
        config.setSaslUsername(saslUsernameField.getText().trim());
        config.setSaslPassword(new String(saslPasswordField.getPassword()));
        config.setTruststoreLocation(truststoreLocationField.getText().trim());
        config.setTruststorePassword(new String(truststorePasswordField.getPassword()));
        config.setKeystoreLocation(keystoreLocationField.getText().trim());
        config.setKeystorePassword(new String(keystorePasswordField.getPassword()));
        config.setKeySerializer((String) keySerializerCombo.getSelectedItem());
        config.setValueSerializer((String) valueSerializerCombo.getSelectedItem());
        config.setKeyDeserializer((String) keyDeserializerCombo.getSelectedItem());
        config.setValueDeserializer((String) valueDeserializerCombo.getSelectedItem());
        return config;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    public String getClusterName() { return nameField.getText().trim(); }
    public String getBootstrapServers() { return bootstrapServersField.getText().trim(); }
    public String getSchemaRegistryUrl() { return schemaRegistryField.getText().trim(); }
    public String getSecurityProtocol() { return (String) securityProtocolCombo.getSelectedItem(); }
    public String getSaslMechanism() { return saslMechanismField.getText().trim(); }
    public String getSaslUsername() { return saslUsernameField.getText().trim(); }
    public String getSaslPassword() { return new String(saslPasswordField.getPassword()); }
    public String getTruststoreLocation() { return truststoreLocationField.getText().trim(); }
    public String getTruststorePassword() { return new String(truststorePasswordField.getPassword()); }
    public String getKeystoreLocation() { return keystoreLocationField.getText().trim(); }
    public String getKeystorePassword() { return new String(keystorePasswordField.getPassword()); }
    public String getKeySerializer() { return (String) keySerializerCombo.getSelectedItem(); }
    public String getValueSerializer() { return (String) valueSerializerCombo.getSelectedItem(); }
    public String getKeyDeserializer() { return (String) keyDeserializerCombo.getSelectedItem(); }
    public String getValueDeserializer() { return (String) valueDeserializerCombo.getSelectedItem(); }
}

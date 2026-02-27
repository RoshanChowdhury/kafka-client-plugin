package org.roshan.kafka.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.*;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;

public class AddClusterDialog extends DialogWrapper {
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

    public AddClusterDialog(Project project) {
        super(project);
        setTitle("Add Kafka Cluster");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
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

        int row = 0;
        addField(panel, gbc, row++, "Cluster Name*:", nameField);
        addField(panel, gbc, row++, "Bootstrap Servers*:", bootstrapServersField);
        addField(panel, gbc, row++, "Schema Registry URL:", schemaRegistryField);
        addField(panel, gbc, row++, "Security Protocol:", securityProtocolCombo);
        addField(panel, gbc, row++, "SASL Mechanism:", saslMechanismField);
        addField(panel, gbc, row++, "SASL Username:", saslUsernameField);
        addField(panel, gbc, row++, "SASL Password:", saslPasswordField);
        addField(panel, gbc, row++, "Truststore Location:", truststoreLocationField);
        addField(panel, gbc, row++, "Truststore Password:", truststorePasswordField);
        addField(panel, gbc, row++, "Keystore Location:", keystoreLocationField);
        addField(panel, gbc, row++, "Keystore Password:", keystorePasswordField);

        return panel;
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
}

package org.roshan.kafka.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.*;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;

public class CopyTopicDialog extends DialogWrapper {
    private JBTextField targetTopicField;
    private JBTextField targetClusterField;

    public CopyTopicDialog(Project project, String sourceTopicName) {
        super(project);
        setTitle("Copy Topic: " + sourceTopicName);
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

        targetTopicField = new JBTextField();
        targetClusterField = new JBTextField();

        int row = 0;
        addField(panel, gbc, row++, "Target Topic Name*:", targetTopicField);
        addField(panel, gbc, row++, "Target Cluster (optional):", targetClusterField);

        JLabel hintLabel = new JLabel("<html><i>Leave target cluster empty to copy within same cluster</i></html>");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(hintLabel, gbc);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.4;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        panel.add(field, gbc);
    }

    public String getTargetTopic() { return targetTopicField.getText().trim(); }
    public String getTargetCluster() { return targetClusterField.getText().trim(); }
}

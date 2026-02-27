package org.roshan.kafka.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.*;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;

public class CreateTopicDialog extends DialogWrapper {
    private JBTextField nameField;
    private JBTextField partitionsField;
    private JBTextField replicationFactorField;

    public CreateTopicDialog(Project project) {
        super(project);
        setTitle("Create Topic");
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
        partitionsField = new JBTextField("3");
        replicationFactorField = new JBTextField("1");

        int row = 0;
        addField(panel, gbc, row++, "Topic Name*:", nameField);
        addField(panel, gbc, row++, "Partitions*:", partitionsField);
        addField(panel, gbc, row++, "Replication Factor*:", replicationFactorField);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.4;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        panel.add(field, gbc);
    }

    public String getTopicName() { return nameField.getText().trim(); }
    public int getPartitions() { return Integer.parseInt(partitionsField.getText().trim()); }
    public short getReplicationFactor() { return Short.parseShort(replicationFactorField.getText().trim()); }
}

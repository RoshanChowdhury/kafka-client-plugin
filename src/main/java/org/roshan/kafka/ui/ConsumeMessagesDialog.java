package org.roshan.kafka.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.*;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;

public class ConsumeMessagesDialog extends DialogWrapper {
    private JComboBox<String> strategyCombo;
    private JBTextField partitionField;
    private JBTextField offsetField;
    private JBTextField maxMessagesField;
    private JBTextField pollTimeoutField;

    public ConsumeMessagesDialog(Project project) {
        super(project);
        setTitle("Consume Messages");
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

        strategyCombo = new JComboBox<>(new String[]{"From Beginning", "Latest", "Specific Offset"});
        partitionField = new JBTextField();
        offsetField = new JBTextField("0");
        maxMessagesField = new JBTextField("100");
        pollTimeoutField = new JBTextField("5000");

        int row = 0;
        addField(panel, gbc, row++, "Strategy*:", strategyCombo);
        addField(panel, gbc, row++, "Partition (optional):", partitionField);
        addField(panel, gbc, row++, "Offset:", offsetField);
        addField(panel, gbc, row++, "Max Messages:", maxMessagesField);
        addField(panel, gbc, row++, "Poll Timeout (ms):", pollTimeoutField);

        offsetField.setEnabled(false);
        strategyCombo.addActionListener(e -> {
            boolean isSpecific = "Specific Offset".equals(strategyCombo.getSelectedItem());
            offsetField.setEnabled(isSpecific);
        });

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

    public String getStrategy() { return (String) strategyCombo.getSelectedItem(); }
    public Integer getPartition() {
        String text = partitionField.getText().trim();
        return text.isEmpty() ? null : Integer.parseInt(text);
    }
    public long getOffset() { return Long.parseLong(offsetField.getText().trim()); }
    public int getMaxMessages() { return Integer.parseInt(maxMessagesField.getText().trim()); }
    public long getPollTimeout() { return Long.parseLong(pollTimeoutField.getText().trim()); }
}

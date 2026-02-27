package org.roshan.kafka.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.*;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;

public class BulkGenerateDialog extends DialogWrapper {
    private JTextArea templateArea;
    private JBTextField countField;
    private JBTextField batchSizeField;

    public BulkGenerateDialog(Project project) {
        super(project);
        setTitle("Bulk Generate Messages");
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

        templateArea = new JTextArea(5, 40);
        templateArea.setText("{\"id\": {{index}}, \"timestamp\": {{timestamp}}}");
        templateArea.setLineWrap(true);
        countField = new JBTextField("1000");
        batchSizeField = new JBTextField("100");

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Template*:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(new JBScrollPane(templateArea), gbc);
        row++;

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addField(panel, gbc, row++, "Message Count*:", countField);
        addField(panel, gbc, row++, "Batch Size:", batchSizeField);

        JLabel hintLabel = new JLabel("<html><i>Use {{index}} for sequence number, {{timestamp}} for current time</i></html>");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(hintLabel, gbc);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    public String getTemplate() { return templateArea.getText(); }
    public int getCount() { return Integer.parseInt(countField.getText().trim()); }
    public int getBatchSize() { return Integer.parseInt(batchSizeField.getText().trim()); }
}

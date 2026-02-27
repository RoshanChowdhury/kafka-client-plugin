package org.roshan.kafka.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.*;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;

public class SearchMessagesDialog extends DialogWrapper {
    private JBTextField keyPatternField;
    private JBTextField valuePatternField;
    private JBTextField maxResultsField;

    public SearchMessagesDialog(Project project) {
        super(project);
        setTitle("Search Messages");
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

        keyPatternField = new JBTextField();
        valuePatternField = new JBTextField();
        maxResultsField = new JBTextField("100");

        int row = 0;
        addField(panel, gbc, row++, "Key Pattern (regex):", keyPatternField);
        addField(panel, gbc, row++, "Value Pattern (regex):", valuePatternField);
        addField(panel, gbc, row++, "Max Results:", maxResultsField);

        JLabel hintLabel = new JLabel("<html><i>Leave empty to match all. Use regex patterns like: user-.*, .*error.*</i></html>");
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

    public String getKeyPattern() { return keyPatternField.getText().trim(); }
    public String getValuePattern() { return valuePatternField.getText().trim(); }
    public int getMaxResults() { return Integer.parseInt(maxResultsField.getText().trim()); }
}

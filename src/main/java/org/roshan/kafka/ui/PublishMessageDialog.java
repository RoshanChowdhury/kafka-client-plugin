package org.roshan.kafka.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.*;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class PublishMessageDialog extends DialogWrapper {
    private JBTextField keyField;
    private JTextArea valueArea;
    private JBTextField partitionField;
    private JBTable headersTable;
    private DefaultTableModel headersModel;

    public PublishMessageDialog(Project project) {
        super(project);
        setTitle("Publish Message");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        keyField = new JBTextField();
        valueArea = new JTextArea(5, 40);
        valueArea.setLineWrap(true);
        partitionField = new JBTextField();

        int row = 0;
        addField(fieldsPanel, gbc, row++, "Key:", keyField);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        fieldsPanel.add(new JLabel("Value*:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        fieldsPanel.add(new JBScrollPane(valueArea), gbc);
        row++;
        
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addField(fieldsPanel, gbc, row++, "Partition (optional):", partitionField);

        panel.add(fieldsPanel, BorderLayout.NORTH);

        JPanel headersPanel = new JPanel(new BorderLayout(5, 5));
        headersPanel.add(new JLabel("Headers:"), BorderLayout.NORTH);
        
        headersModel = new DefaultTableModel(new String[]{"Key", "Value"}, 0);
        headersTable = new JBTable(headersModel);
        headersPanel.add(new JBScrollPane(headersTable), BorderLayout.CENTER);
        
        JPanel headerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addHeaderBtn = new JButton("Add Header");
        JButton removeHeaderBtn = new JButton("Remove Header");
        addHeaderBtn.addActionListener(e -> headersModel.addRow(new Object[]{"", ""}));
        removeHeaderBtn.addActionListener(e -> {
            int row1 = headersTable.getSelectedRow();
            if (row1 >= 0) headersModel.removeRow(row1);
        });
        headerButtonsPanel.add(addHeaderBtn);
        headerButtonsPanel.add(removeHeaderBtn);
        headersPanel.add(headerButtonsPanel, BorderLayout.SOUTH);
        
        panel.add(headersPanel, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(500, 400));

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

    public String getKey() { return keyField.getText(); }
    public String getValue() { return valueArea.getText(); }
    public Integer getPartition() {
        String text = partitionField.getText().trim();
        return text.isEmpty() ? null : Integer.parseInt(text);
    }
    
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        for (int i = 0; i < headersModel.getRowCount(); i++) {
            String key = (String) headersModel.getValueAt(i, 0);
            String value = (String) headersModel.getValueAt(i, 1);
            if (key != null && !key.trim().isEmpty()) {
                headers.put(key.trim(), value != null ? value : "");
            }
        }
        return headers;
    }
}

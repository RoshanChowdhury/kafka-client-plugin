package org.roshan.kafka.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.*;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;

public class SchemaRegistryDialog extends DialogWrapper {
    private JBTextField subjectField;
    private JComboBox<Integer> version1Combo;
    private JComboBox<Integer> version2Combo;
    private JTextArea schema1Area;
    private JTextArea schema2Area;
    private final Project project;

    public SchemaRegistryDialog(Project project) {
        super(project);
        this.project = project;
        setTitle("Schema Registry - Version Comparison");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Subject:"));
        subjectField = new JBTextField(20);
        topPanel.add(subjectField);
        
        JButton loadButton = new JButton("Load Versions");
        loadButton.addActionListener(e -> loadVersions());
        topPanel.add(loadButton);
        
        topPanel.add(new JLabel("Version 1:"));
        version1Combo = new JComboBox<>();
        topPanel.add(version1Combo);
        
        topPanel.add(new JLabel("Version 2:"));
        version2Combo = new JComboBox<>();
        topPanel.add(version2Combo);
        
        JButton compareButton = new JButton("Compare");
        compareButton.addActionListener(e -> compareVersions());
        topPanel.add(compareButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        JPanel schemasPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JPanel schema1Panel = new JPanel(new BorderLayout());
        schema1Panel.add(new JLabel("Schema Version 1:"), BorderLayout.NORTH);
        schema1Area = new JTextArea();
        schema1Area.setEditable(false);
        schema1Panel.add(new JBScrollPane(schema1Area), BorderLayout.CENTER);
        
        JPanel schema2Panel = new JPanel(new BorderLayout());
        schema2Panel.add(new JLabel("Schema Version 2:"), BorderLayout.NORTH);
        schema2Area = new JTextArea();
        schema2Area.setEditable(false);
        schema2Panel.add(new JBScrollPane(schema2Area), BorderLayout.CENTER);
        
        schemasPanel.add(schema1Panel);
        schemasPanel.add(schema2Panel);
        
        panel.add(schemasPanel, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(900, 500));
        
        return panel;
    }

    private void loadVersions() {
        // TODO: Load versions from SchemaRegistryService
    }

    private void compareVersions() {
        // TODO: Compare selected versions
    }
}

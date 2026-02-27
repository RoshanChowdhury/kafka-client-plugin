package org.roshan.kafka.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.*;
import com.intellij.ui.table.JBTable;
import org.apache.kafka.common.TopicPartition;
import org.jetbrains.annotations.Nullable;
import org.roshan.kafka.service.KafkaConsumerGroupService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class ConsumerGroupDialog extends DialogWrapper {
    private JBTextField groupIdField;
    private JBTable lagTable;
    private DefaultTableModel lagModel;
    private final Project project;

    public ConsumerGroupDialog(Project project) {
        super(project);
        this.project = project;
        setTitle("Consumer Group Monitor");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Group ID:"));
        groupIdField = new JBTextField(20);
        topPanel.add(groupIdField);
        
        JButton loadButton = new JButton("Load Lag");
        loadButton.addActionListener(e -> loadConsumerGroupLag());
        topPanel.add(loadButton);
        
        JButton resetToBeginningBtn = new JButton("Reset to Beginning");
        resetToBeginningBtn.addActionListener(e -> resetToBeginning());
        topPanel.add(resetToBeginningBtn);
        
        JButton resetToEndBtn = new JButton("Reset to End");
        resetToEndBtn.addActionListener(e -> resetToEnd());
        topPanel.add(resetToEndBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        lagModel = new DefaultTableModel(new String[]{"Topic", "Partition", "Current Offset", "End Offset", "Lag"}, 0);
        lagTable = new JBTable(lagModel);
        panel.add(new JBScrollPane(lagTable), BorderLayout.CENTER);
        
        panel.setPreferredSize(new Dimension(700, 400));
        return panel;
    }

    private void loadConsumerGroupLag() {
        String groupId = groupIdField.getText().trim();
        if (groupId.isEmpty()) return;
        
        // TODO: Get selected cluster config
        // KafkaConsumerGroupService service = KafkaConsumerGroupService.getInstance(project);
        // Map<TopicPartition, Long> lag = service.getConsumerGroupLag(config, groupId);
        // Update table with lag data
    }

    private void resetToBeginning() {
        // TODO: Implement reset to beginning
    }

    private void resetToEnd() {
        // TODO: Implement reset to end
    }
}

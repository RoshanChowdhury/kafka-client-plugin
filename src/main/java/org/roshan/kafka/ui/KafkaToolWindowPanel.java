package org.roshan.kafka.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.*;
import com.intellij.ui.components.*;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;
import org.roshan.kafka.model.*;
import org.roshan.kafka.service.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;

public class KafkaToolWindowPanel extends SimpleToolWindowPanel {
    private final Project project;
    private final Tree clusterTree;
    private final JBTable topicsTable;
    private final JBTable messagesTable;
    private final KafkaClusterManager clusterManager;
    private final KafkaAdminService adminService;
    private ClusterConfig selectedCluster;
    private String selectedTopic;

    public KafkaToolWindowPanel(Project project) {
        super(true, true);
        this.project = project;
        this.clusterManager = KafkaClusterManager.getInstance(project);
        this.adminService = KafkaAdminService.getInstance(project);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Kafka Clusters");
        clusterTree = new Tree(root);
        clusterTree.setRootVisible(true);

        topicsTable = new JBTable();
        messagesTable = new JBTable();

        JBSplitter mainSplitter = new JBSplitter(false, 0.3f);
        mainSplitter.setFirstComponent(new JBScrollPane(clusterTree));

        JBSplitter rightSplitter = new JBSplitter(true, 0.5f);
        rightSplitter.setFirstComponent(new JBScrollPane(topicsTable));
        rightSplitter.setSecondComponent(new JBScrollPane(messagesTable));
        mainSplitter.setSecondComponent(rightSplitter);

        setContent(mainSplitter);
        setToolbar(createToolbar());
        setupListeners();
    }

    private JComponent createToolbar() {
        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction("KafkaToolbar");
        ActionToolbar toolbar = actionManager.createActionToolbar("KafkaToolbar", actionGroup, true);
        toolbar.setTargetComponent(this);
        return toolbar.getComponent();
    }

    private void setupListeners() {
        clusterTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) clusterTree.getLastSelectedPathComponent();
            if (node != null && node.getUserObject() instanceof ClusterConfig) {
                selectedCluster = (ClusterConfig) node.getUserObject();
                loadTopics(selectedCluster);
            }
        });

        topicsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = topicsTable.getSelectedRow();
                if (row >= 0) {
                    selectedTopic = (String) topicsTable.getValueAt(row, 0);
                }
            }
        });
    }

    public void refreshClusters() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) clusterTree.getModel().getRoot();
        root.removeAllChildren();
        
        for (ClusterConfig cluster : clusterManager.getAllClusters()) {
            root.add(new DefaultMutableTreeNode(cluster));
        }
        ((DefaultTreeModel) clusterTree.getModel()).reload();
    }

    public void refreshTopics() {
        if (selectedCluster != null) {
            loadTopics(selectedCluster);
        }
    }

    private void loadTopics(ClusterConfig config) {
        new Thread(() -> {
            try {
                List<TopicInfo> topics = adminService.listTopics(config, true);
                SwingUtilities.invokeLater(() -> updateTopicsTable(topics));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> 
                    com.intellij.openapi.ui.Messages.showErrorDialog(project, 
                        "Failed to load topics: " + ex.getMessage(), "Error"));
            }
        }).start();
    }

    private void updateTopicsTable(List<TopicInfo> topics) {
        String[] columns = {"Topic", "Partitions", "Replication", "Internal"};
        Object[][] data = new Object[topics.size()][4];
        for (int i = 0; i < topics.size(); i++) {
            TopicInfo topic = topics.get(i);
            data[i] = new Object[]{topic.getName(), topic.getPartitions(), 
                topic.getReplicationFactor(), topic.isInternal()};
        }
        topicsTable.setModel(new javax.swing.table.DefaultTableModel(data, columns));
    }

    public void updateMessages(List<KafkaMessage> messages) {
        String[] columns = {"Key", "Value", "Partition", "Offset", "Timestamp"};
        Object[][] data = new Object[messages.size()][5];
        for (int i = 0; i < messages.size(); i++) {
            KafkaMessage msg = messages.get(i);
            data[i] = new Object[]{msg.getKey(), msg.getValue(), msg.getPartition(), 
                msg.getOffset(), msg.getTimestamp()};
        }
        messagesTable.setModel(new javax.swing.table.DefaultTableModel(data, columns));
    }

    public ClusterConfig getSelectedCluster() {
        return selectedCluster;
    }

    public String getSelectedTopic() {
        return selectedTopic;
    }
}

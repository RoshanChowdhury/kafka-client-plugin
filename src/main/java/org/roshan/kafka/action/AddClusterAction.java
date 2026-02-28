package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.roshan.kafka.model.ClusterConfig;
import org.roshan.kafka.service.*;
import org.roshan.kafka.ui.*;

public class AddClusterAction extends AnAction {
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        AddClusterDialog dialog = new AddClusterDialog(project);
        if (!dialog.showAndGet()) return;

        String name = dialog.getClusterName();
        String servers = dialog.getBootstrapServers();
        
        if (name.isEmpty() || servers.isEmpty()) {
            Messages.showErrorDialog(project, "Cluster name and bootstrap servers are required", "Error");
            return;
        }

        ClusterConfig config = new ClusterConfig(name, servers);
        config.setSchemaRegistryUrl(dialog.getSchemaRegistryUrl());
        config.setSecurityProtocol(dialog.getSecurityProtocol());
        config.setSaslMechanism(dialog.getSaslMechanism());
        config.setSaslUsername(dialog.getSaslUsername());
        config.setSaslPassword(dialog.getSaslPassword());
        config.setTruststoreLocation(dialog.getTruststoreLocation());
        config.setTruststorePassword(dialog.getTruststorePassword());
        config.setKeystoreLocation(dialog.getKeystoreLocation());
        config.setKeystorePassword(dialog.getKeystorePassword());
        config.setKeySerializer(dialog.getKeySerializer());
        config.setValueSerializer(dialog.getValueSerializer());
        config.setKeyDeserializer(dialog.getKeyDeserializer());
        config.setValueDeserializer(dialog.getValueDeserializer());

        new Thread(() -> {
            try {
                KafkaAdminService.getInstance(project).listTopics(config, false);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    KafkaClusterManager.getInstance(project).addCluster(config);
                    
                    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Kafka Client");
                    if (toolWindow != null) {
                        Content content = toolWindow.getContentManager().getContent(0);
                        if (content != null && content.getComponent() instanceof KafkaToolWindowPanel) {
                            ((KafkaToolWindowPanel) content.getComponent()).refreshClusters();
                        }
                    }
                    
                    Messages.showInfoMessage(project, "Cluster '" + name + "' connected successfully", "Success");
                });
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> 
                    Messages.showErrorDialog(project, "Failed to connect to cluster: " + ex.getMessage(), "Connection Error"));
            }
        }).start();
    }
}

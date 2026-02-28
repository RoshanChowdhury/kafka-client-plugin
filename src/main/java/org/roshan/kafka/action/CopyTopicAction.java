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

public class CopyTopicAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        ClusterConfig sourceConfig = getSelectedCluster(project);
        String sourceTopic = getSelectedTopic(project);
        
        if (sourceConfig == null || sourceTopic == null) {
            Messages.showErrorDialog(project, "Please select a cluster and topic first", "Error");
            return;
        }

        CopyTopicDialog dialog = new CopyTopicDialog(project, sourceTopic);
        if (!dialog.showAndGet()) return;

        String targetTopic = dialog.getTargetTopic();
        String targetClusterName = dialog.getTargetCluster();
        
        if (targetTopic.isEmpty()) {
            Messages.showErrorDialog(project, "Target topic name is required", "Error");
            return;
        }
        
        ClusterConfig targetConfig = targetClusterName.isEmpty() ? sourceConfig : 
            KafkaClusterManager.getInstance(project).getCluster(targetClusterName);
        
        if (targetConfig == null) {
            Messages.showErrorDialog(project, "Target cluster not found", "Error");
            return;
        }

        new Thread(() -> {
            try {
                KafkaConsumerService.getInstance(project)
                    .copyTopicMessages(sourceConfig, sourceTopic, targetConfig, targetTopic);
                javax.swing.SwingUtilities.invokeLater(() -> 
                    Messages.showInfoMessage(project, "Topic copied successfully", "Success"));
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> 
                    Messages.showErrorDialog(project, "Failed to copy topic: " + ex.getMessage(), "Error"));
            }
        }).start();
    }

    private ClusterConfig getSelectedCluster(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Kafka Client");
        if (toolWindow != null) {
            Content content = toolWindow.getContentManager().getContent(0);
            if (content != null && content.getComponent() instanceof KafkaToolWindowPanel) {
                return ((KafkaToolWindowPanel) content.getComponent()).getSelectedCluster();
            }
        }
        return null;
    }

    private String getSelectedTopic(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Kafka Client");
        if (toolWindow != null) {
            Content content = toolWindow.getContentManager().getContent(0);
            if (content != null && content.getComponent() instanceof KafkaToolWindowPanel) {
                return ((KafkaToolWindowPanel) content.getComponent()).getSelectedTopic();
            }
        }
        return null;
    }
}

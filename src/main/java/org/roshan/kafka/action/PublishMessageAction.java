package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.roshan.kafka.model.ClusterConfig;
import org.roshan.kafka.service.KafkaProducerService;
import org.roshan.kafka.ui.*;
import java.util.Map;

public class PublishMessageAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        ClusterConfig config = getSelectedCluster(project);
        String topic = getSelectedTopic(project);
        
        if (config == null || topic == null) {
            Messages.showErrorDialog(project, "Please select a cluster and topic first", "Error");
            return;
        }

        PublishMessageDialog dialog = new PublishMessageDialog(project);
        if (!dialog.showAndGet()) return;

        String key = dialog.getKey();
        String value = dialog.getValue();
        Integer partition = dialog.getPartition();
        Map<String, String> headers = dialog.getHeaders();
        
        if (value == null || value.isEmpty()) {
            Messages.showErrorDialog(project, "Message value is required", "Error");
            return;
        }
        
        new Thread(() -> {
            try {
                KafkaProducerService.getInstance(project)
                    .publishMessage(config, topic, key, value, partition, headers).get();
                javax.swing.SwingUtilities.invokeLater(() -> 
                    Messages.showInfoMessage(project, "Message published successfully", "Success"));
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> 
                    Messages.showErrorDialog(project, "Failed to publish message: " + ex.getMessage(), "Error"));
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

package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.roshan.kafka.model.*;
import org.roshan.kafka.service.KafkaConsumerService;
import org.roshan.kafka.ui.*;
import java.util.List;

public class SearchMessagesAction extends AnAction {
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

        SearchMessagesDialog dialog = new SearchMessagesDialog(project);
        if (!dialog.showAndGet()) return;

        String keyPattern = dialog.getKeyPattern();
        String valuePattern = dialog.getValuePattern();
        int maxResults = dialog.getMaxResults();
        
        new Thread(() -> {
            try {
                List<KafkaMessage> messages = KafkaConsumerService.getInstance(project)
                    .searchMessages(config, topic, keyPattern, valuePattern, maxResults);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    updateMessagesTable(project, messages);
                    Messages.showInfoMessage(project, "Found " + messages.size() + " matching messages", "Success");
                });
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> 
                    Messages.showErrorDialog(project, "Failed to search messages: " + ex.getMessage(), "Error"));
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

    private void updateMessagesTable(Project project, List<KafkaMessage> messages) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Kafka Client");
        if (toolWindow != null) {
            Content content = toolWindow.getContentManager().getContent(0);
            if (content != null && content.getComponent() instanceof KafkaToolWindowPanel) {
                ((KafkaToolWindowPanel) content.getComponent()).updateMessages(messages);
            }
        }
    }
}

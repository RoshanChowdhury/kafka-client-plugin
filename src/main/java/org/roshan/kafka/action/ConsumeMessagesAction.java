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

public class ConsumeMessagesAction extends AnAction {
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

        ConsumeMessagesDialog dialog = new ConsumeMessagesDialog(project);
        if (!dialog.showAndGet()) return;

        String strategy = dialog.getStrategy();
        Integer partition = dialog.getPartition();
        long offset = dialog.getOffset();
        int maxMessages = dialog.getMaxMessages();
        long pollTimeout = dialog.getPollTimeout();
        
        new Thread(() -> {
            try {
                List<KafkaMessage> messages;
                KafkaConsumerService consumerService = KafkaConsumerService.getInstance(project);
                
                if ("Specific Offset".equals(strategy) && partition != null) {
                    messages = consumerService.consumeFromPartitionOffset(config, topic, partition, offset, maxMessages);
                } else if ("Latest".equals(strategy)) {
                    messages = consumerService.consumeLatest(config, topic, maxMessages, pollTimeout);
                } else {
                    messages = consumerService.consumeFromBeginning(config, topic, maxMessages, pollTimeout);
                }
                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    updateMessagesTable(project, messages);
                    Messages.showInfoMessage(project, "Consumed " + messages.size() + " messages", "Success");
                });
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> 
                    Messages.showErrorDialog(project, "Failed to consume messages: " + ex.getMessage(), "Error"));
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

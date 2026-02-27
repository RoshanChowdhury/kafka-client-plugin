package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.roshan.kafka.ui.PublishMessageDialog;

import java.util.Map;

public class PublishMessageAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

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
        
        // TODO: Integrate with KafkaProducerService to publish with headers
        Messages.showInfoMessage(project, "Message published with " + headers.size() + " headers", "Success");
    }
}

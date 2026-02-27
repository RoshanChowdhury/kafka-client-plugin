package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.roshan.kafka.service.KafkaAdminService;
import org.roshan.kafka.ui.CreateTopicDialog;

public class CreateTopicAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        CreateTopicDialog dialog = new CreateTopicDialog(project);
        if (!dialog.showAndGet()) return;

        String name = dialog.getTopicName();
        if (name.isEmpty()) {
            Messages.showErrorDialog(project, "Topic name is required", "Error");
            return;
        }

        try {
            int partitions = dialog.getPartitions();
            short replication = dialog.getReplicationFactor();
            // TODO: Integrate with KafkaAdminService to create topic
            Messages.showInfoMessage(project, "Topic creation initiated", "Success");
        } catch (NumberFormatException ex) {
            Messages.showErrorDialog(project, "Invalid number format", "Error");
        }
    }
}

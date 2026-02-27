package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class ModifyPartitionsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        String newPartitions = Messages.showInputDialog(project, "New Partition Count:", "Modify Partitions", null, "3", null);
        if (newPartitions != null && !newPartitions.trim().isEmpty()) {
            try {
                int count = Integer.parseInt(newPartitions.trim());
                Messages.showInfoMessage(project, "Partition modification initiated", "Success");
            } catch (NumberFormatException ex) {
                Messages.showErrorDialog(project, "Invalid partition count", "Error");
            }
        }
    }
}

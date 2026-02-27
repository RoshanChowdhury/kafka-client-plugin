package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.roshan.kafka.ui.ConsumeMessagesDialog;

public class ConsumeMessagesAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        ConsumeMessagesDialog dialog = new ConsumeMessagesDialog(project);
        if (!dialog.showAndGet()) return;

        String strategy = dialog.getStrategy();
        Integer partition = dialog.getPartition();
        long offset = dialog.getOffset();
        int maxMessages = dialog.getMaxMessages();
        long pollTimeout = dialog.getPollTimeout();
        
        Messages.showInfoMessage(project, "Consuming messages with strategy: " + strategy, "Info");
    }
}

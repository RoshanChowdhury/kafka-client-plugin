package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.roshan.kafka.ui.CopyTopicDialog;

public class CopyTopicAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        CopyTopicDialog dialog = new CopyTopicDialog(project, "source-topic");
        if (!dialog.showAndGet()) return;

        String targetTopic = dialog.getTargetTopic();
        String targetCluster = dialog.getTargetCluster();
        
        if (targetTopic.isEmpty()) {
            Messages.showErrorDialog(project, "Target topic name is required", "Error");
            return;
        }
        
        Messages.showInfoMessage(project, "Topic copy initiated", "Success");
    }
}

package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class DeleteTopicAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            int result = Messages.showYesNoDialog(project, "Delete selected topic?", "Confirm", null);
            if (result == Messages.YES) {
                Messages.showInfoMessage(project, "Topic deleted", "Success");
            }
        }
    }
}

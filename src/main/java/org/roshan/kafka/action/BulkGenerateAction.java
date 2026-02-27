package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.roshan.kafka.ui.BulkGenerateDialog;

public class BulkGenerateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        BulkGenerateDialog dialog = new BulkGenerateDialog(project);
        if (!dialog.showAndGet()) return;

        String template = dialog.getTemplate();
        int count = dialog.getCount();
        int batchSize = dialog.getBatchSize();

        Messages.showInfoMessage(project, "Generating " + count + " messages...", "Info");
    }
}

package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.roshan.kafka.ui.ConsumerGroupDialog;

public class ConsumerGroupAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        ConsumerGroupDialog dialog = new ConsumerGroupDialog(project);
        dialog.show();
    }
}

package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.roshan.kafka.ui.SchemaRegistryDialog;

public class SchemaRegistryAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        SchemaRegistryDialog dialog = new SchemaRegistryDialog(project);
        dialog.show();
    }
}

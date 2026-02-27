package org.roshan.kafka.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.roshan.kafka.ui.SearchMessagesDialog;

public class SearchMessagesAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        SearchMessagesDialog dialog = new SearchMessagesDialog(project);
        if (!dialog.showAndGet()) return;

        String keyPattern = dialog.getKeyPattern();
        String valuePattern = dialog.getValuePattern();
        int maxResults = dialog.getMaxResults();
        
        Messages.showInfoMessage(project, "Searching messages...", "Info");
    }
}

package org.roshan.kafka.integration;

import com.intellij.codeInsight.daemon.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.*;
import com.intellij.psi.*;
import com.intellij.ui.content.Content;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.roshan.kafka.ui.KafkaToolWindowPanel;

public class KafkaListenerLineMarkerProvider implements LineMarkerProvider {

    @Nullable
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (!(element instanceof PsiMethod)) return null;

        PsiMethod method = (PsiMethod) element;
        PsiAnnotation annotation = method.getAnnotation("org.springframework.kafka.annotation.KafkaListener");

        if (annotation == null) return null;

        PsiAnnotationMemberValue topicsValue = annotation.findAttributeValue("topics");
        if (topicsValue == null) return null;

        String topicName = extractTopicName(topicsValue);
        if (topicName == null) return null;

        return new LineMarkerInfo<>(
                element,
                element.getTextRange(),
                AllIcons.Toolwindows.ToolWindowMessages,
                psiElement -> "Navigate to Kafka topic: " + topicName,
                (e, elt) -> navigateToTopic(elt.getProject(), topicName),
                GutterIconRenderer.Alignment.LEFT,
                () -> "Kafka Topic"
        );
    }

    private String extractTopicName(PsiAnnotationMemberValue value) {
        String text = value.getText();
        if (text.startsWith("\"") && text.endsWith("\"")) {
            return text.substring(1, text.length() - 1);
        }
        if (text.startsWith("{") && text.endsWith("}")) {
            String[] topics = text.substring(1, text.length() - 1).split(",");
            if (topics.length > 0) {
                String topic = topics[0].trim();
                if (topic.startsWith("\"") && topic.endsWith("\"")) {
                    return topic.substring(1, topic.length() - 1);
                }
            }
        }
        return null;
    }

    private void navigateToTopic(Project project, String topicName) {
        UIUtil.invokeLaterIfNeeded(() -> {
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Kafka Client");
            if (toolWindow != null) {
                toolWindow.activate(() -> {
                    Content content = toolWindow.getContentManager().getContent(0);
                    if (content != null && content.getComponent() instanceof KafkaToolWindowPanel) {
                        // TODO: Select and highlight the topic in the panel
                    }
                });
            }
        });
    }
}

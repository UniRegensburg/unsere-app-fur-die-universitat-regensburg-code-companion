package app;

import app.services.application.ApplicationService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Used to create Toolwindow
 */
public class CCToolWindowFactory implements ToolWindowFactory {

    public static CCToolWindow ccToolWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ccToolWindow = new CCToolWindow();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(ccToolWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
        ccToolWindow.init();
    }
}

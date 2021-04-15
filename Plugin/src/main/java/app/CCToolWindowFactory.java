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

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CCToolWindow ccToolWindow = new CCToolWindow();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(ccToolWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
        if(ApplicationService.getInstance().isStarted){
            ApplicationService.getInstance().killSession();
        }
        ccToolWindow.init();
    }
}

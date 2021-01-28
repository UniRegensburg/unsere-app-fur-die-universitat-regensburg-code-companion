package app.listeners.topics;

import app.listeners.base.BaseListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class ProjectListener extends BaseListener implements ProjectManagerListener {

    public ProjectListener() {
        super("Project");
    }

    @Override
    public void projectOpened(@NotNull Project project) {
        log("Project opened [" + project.getName() + "]");
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        log("Project closed [" + project.getName() + "]");
    }

    @Override
    public void projectClosing(@NotNull Project project) {

    }

    @Override
    public void projectClosingBeforeSave(@NotNull Project project) {
        getApplicationService().saveSession();
    }
}

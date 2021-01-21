package app;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * Runs on IntelliJ startup and stores the currently opened {@link Project}
 */
public class App implements StartupActivity {

    private static Project currentProject;

    @Override
    public void runActivity(@NotNull Project project) {
        currentProject = project;
    }

    public static Project getCurrentProject() {
        return currentProject;
    }

}

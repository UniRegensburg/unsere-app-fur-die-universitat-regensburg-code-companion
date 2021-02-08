package app;

import app.services.application.ApplicationService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;

/**
 * Runs on IntelliJ startup and stores the currently opened {@link Project}
 */
public class App implements StartupActivity {

    private static Project currentProject;

    @Override
    public void runActivity(@NotNull Project project) {
        currentProject = project;
        try {
            ApplicationService.getInstance().startSession();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Failed to Start");
        }
        System.out.println("Started");
    }

    public static Project getCurrentProject() {
        return currentProject;
    }

}

package app.listeners.topics;

import app.listeners.base.BaseListener;
import com.intellij.task.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TaskListener extends BaseListener implements ProjectTaskListener {

    public TaskListener() {
        super("Task");
    }

    @Override
    public void started(@NotNull ProjectTaskContext context) {
    }

    @Override
    public void finished(@NotNull ProjectTaskContext context, @NotNull ProjectTaskResult executionResult) {
        Map<ProjectTask, ProjectTaskState> x = executionResult.getTasksState();
        for(ProjectTask task: x.keySet()) {
            log(task.getPresentableName());
        }
    }
}

package app.listeners.topics;

import app.listeners.base.BaseListener;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;

public class RunListener extends BaseListener implements com.intellij.execution.ExecutionListener {

    public RunListener() {
        super("Runner");
    }

    @Override
    public void processStartScheduled(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
    }

    @Override
    public void processStarting(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        System.out.println("Configuration [" + executorId + "] starting");

    }

    @Override
    public void processNotStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        System.out.println("Configuration [" + executorId + "] not started");
    }

    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        System.out.println("Configuration [" + executorId + "] started");
    }

    @Override
    public void processTerminating(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {

    }

    @Override
    public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler, int exitCode) {
        System.out.println("Configuration [" + executorId + "] terminated with result (" + exitCode + ")");
    }
}

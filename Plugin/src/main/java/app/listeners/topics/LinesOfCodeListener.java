package app.listeners.topics;

import app.listeners.base.BaseListener;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Listens to changes in Code Analysis (Errors, Warnings) and creates a log when detecting changes
 */
public class LinesOfCodeListener extends BaseListener implements DaemonCodeAnalyzer.DaemonListener {

    private final Project currentProject;
    private int lastLineCount = -1;

    public LinesOfCodeListener(Project project) {
        super("CodeAnalyzer");
        currentProject = project;
    }

    @Override
    public void daemonStarting(@NotNull Collection<? extends FileEditor> fileEditors) {
    }

    @Override
    public void daemonFinished() {
    }

    @Override
    public void daemonFinished(@NotNull Collection<? extends FileEditor> fileEditors) {
        try {
            analyzeCurrentErrors();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void daemonCancelEventOccurred(@NotNull String reason) {

    }

    private void analyzeCurrentErrors() throws Exception {
        Document document = FileEditorManager.getInstance(currentProject).getSelectedTextEditor().getDocument();
        int lineCount = document.getLineCount();

        // only fire if line count changed to prevent running unnecessary code
        if (lineCount != lastLineCount) {
            handleLinesOfCode(lineCount, document);
        }
    }
}

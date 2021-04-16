package app.listeners.topics;

import app.inspectors.HighlightInfoInspector;
import app.listeners.base.BaseListener;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.command.undo.DocumentReferenceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.psi.impl.file.impl.FileManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

/**
 * Listens to changes in Code Analysis (Errors, Warnings) and creates a log when detecting changes
 */
public class CodeAnalyzerListener extends BaseListener implements DaemonCodeAnalyzer.DaemonListener {

    private final Project currentProject;
    private HighlightInfoInspector.HighlightInfoInspectorResults lastResults;

    public CodeAnalyzerListener(Project project) {
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
        List<HighlightInfo> highlightInfoList = DaemonCodeAnalyzerImpl.getHighlights(document, HighlightSeverity.INFORMATION, currentProject);
        handleErrors(highlightInfoList, document);
    }
}

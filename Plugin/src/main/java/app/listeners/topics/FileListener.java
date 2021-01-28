package app.listeners.topics;


import app.listeners.ListenerHelper;
import app.listeners.base.BaseListener;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class FileListener extends BaseListener implements FileEditorManagerListener {

    private boolean wasInvokedOnce = false;

    public FileListener() {
        super("Files");
    }

    @Override
    public void fileOpenedSync(@NotNull FileEditorManager source, @NotNull VirtualFile file, @NotNull Pair<FileEditor[], FileEditorProvider[]> editors) {
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        log("File opened: " + file.getName());
        if(!wasInvokedOnce) {
            ListenerHelper.initMenuListener(source.getSelectedEditor());
            wasInvokedOnce = true;
        }
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        log("File closed: " + file.getName());
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        log("Switched to file: " + event.getNewFile().getName());
    }
}

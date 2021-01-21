package app.listeners.topics;

import app.listeners.base.BaseListener;
import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RefactoringListener extends BaseListener implements RefactoringEventListener {

    public RefactoringListener() {
        super("Refactoring");
    }

    @Override
    public void refactoringStarted(@NotNull String refactoringId, @Nullable RefactoringEventData beforeData) {
        log("Refactoring started");
    }

    @Override
    public void refactoringDone(@NotNull String refactoringId, @Nullable RefactoringEventData afterData) {
        log("Refactoring function used");
    }

    @Override
    public void conflictsDetected(@NotNull String refactoringId, @NotNull RefactoringEventData conflictsData) {
        log("Refactoring conflict detected");
    }

    @Override
    public void undoRefactoring(@NotNull String refactoringId) {
        log("Refactoring undo started");
    }
}

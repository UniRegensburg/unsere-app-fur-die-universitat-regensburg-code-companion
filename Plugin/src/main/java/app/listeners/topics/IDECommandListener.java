package app.listeners.topics;

import app.data.Measurements;
import app.listeners.base.BaseListener;
import app.listeners.base.Event;
import app.services.application.AutoLogger;
import com.intellij.openapi.command.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

public class IDECommandListener extends BaseListener implements Measurements, AutoLogger, com.intellij.openapi.command.CommandListener {

    private static final String[] BLACKLISTED_COMMANDS = {"", "Left", "Right", "Up", "Down", "Left with Selection", "Right with Selection", "Up with Selection", "Down with Selection"};
    private static final String[] EDITING_COMMANDS = {"Typing", "Tab", "Enter", "Backspace", "Delete"};
    private Timer syncTimer;
    private boolean syncTimerIsRunning = false;
    private int currentEditingEventCount = 0;

    public IDECommandListener() {
        super("Command");
        getApplicationService().registerAutoLogger(this);
    }

    private void startSyncTimer() {
        if(syncTimer != null) {
            syncTimer.cancel();
        }
        syncTimer = new Timer();
        syncTimer.scheduleAtFixedRate(new EditingCommandLoggerTask(this), EDITING_EVENT_FRAME_IN_MS, EDITING_EVENT_FRAME_IN_MS);
        syncTimerIsRunning = true;
    }

    private void stopSyncTimer() {
        syncTimer.cancel();
        syncTimerIsRunning = false;
    }

    private boolean isTypingCommand(CommandEvent event) {
        return isInCommandList(EDITING_COMMANDS, event);
    }

    private boolean isOnBlacklist(CommandEvent event) {
        return isInCommandList(BLACKLISTED_COMMANDS, event);
    }

    private boolean isInCommandList(String[] list, CommandEvent event) {
        String name = event.getCommandName();
        if (name == null) {
            return true;
        }
        for (String command : list) {
            if (command.equals(name)) {
                return true;
            }
        }
        return false;
    }

    protected void syncEditingCommandPuffer() {
        Event event = new Event("Editing", "(Editing commands in this frame: " + currentEditingEventCount + ")");
        getApplicationService().inspectEvent(event);
        currentEditingEventCount = 0;
    }

    @Override
    public void commandStarted(@NotNull CommandEvent event) {
        if (isOnBlacklist(event)) {
            return;
        }
        if (isTypingCommand(event)) {
            currentEditingEventCount++;
            return;
        }
        log(event.getCommandName());
    }

    @Override
    public void beforeCommandFinished(@NotNull CommandEvent event) {

    }

    @Override
    public void commandFinished(@NotNull CommandEvent event) {
    }

    @Override
    public void undoTransparentActionStarted() {

    }

    @Override
    public void beforeUndoTransparentActionFinished() {

    }

    @Override
    public void undoTransparentActionFinished() {

    }

    @Override
    public boolean isRunning() {
        return syncTimerIsRunning;
    }

    @Override
    public void start() {
        startSyncTimer();
    }

    @Override
    public void stop() {
        stopSyncTimer();
    }

    private class EditingCommandLoggerTask extends TimerTask {

        private IDECommandListener parent;

        public EditingCommandLoggerTask(IDECommandListener parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void run() {
            parent.syncEditingCommandPuffer();
        }
    }
}

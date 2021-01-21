package app.services.application;

import app.listeners.ListenerHelper;
import app.listeners.base.Event;
import app.services.log.LogService;
import com.intellij.openapi.components.ServiceManager;

import java.util.ArrayList;

public class ApplicationService {

    private boolean logIsReady = false;
    private boolean listenersAreReady = false;
    private ApplicationState state = ApplicationState.IDLE;
    private LogService logService;
    private final ArrayList<AutoLogger> autoLoggers = new ArrayList<>();

    public static ApplicationService getInstance() {
        return ServiceManager.getService(ApplicationService.class);
    }

    public ApplicationState getState() {
        return state;
    }

    public void startSession() {
        if (state == ApplicationState.RECORDING) {
            return;
        }
        if(!logIsReady) {
           logService = ServiceManager.getService(LogService.class);
           logService.init();
           logIsReady = true;
        }
        if (!listenersAreReady) {
            ListenerHelper.initListener();
            listenersAreReady = true;
        }
        startAutoLoggers();
        logService.createSessionLog();
        logService.logAction("Plugin", "Session started");
        state = ApplicationState.RECORDING;
    }

    public void saveSession() {
        if (state == ApplicationState.IDLE) {
            return;
        }
        stopAutoLoggers();
        logService.logAction("Plugin", "Session saved");
//        logService.syncCurrentLog();
        state = ApplicationState.IDLE;
    }

    private void startAutoLoggers() {
        for(AutoLogger logger: autoLoggers) {
            logger.start();
        }
    }

    private void stopAutoLoggers() {
        for(AutoLogger logger: autoLoggers) {
            logger.stop();
        }
    }

    public void inspectEvent(Event event) {
        if(state == ApplicationState.IDLE) {
            return;
        }
        logService.logAction(event.label, event.msg);
    }

    public void registerAutoLogger(AutoLogger logger) {
        autoLoggers.add(logger);
    }

}

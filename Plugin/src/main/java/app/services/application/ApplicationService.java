package app.services.application;

import app.MessageHandler;
import app.TaskHandler;
import app.WebRTC;
import app.listeners.ListenerHelper;
import app.listeners.base.Event;
import app.services.log.LogService;
import com.intellij.openapi.components.ServiceManager;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

public class ApplicationService {

    private boolean logIsReady = false;
    private boolean listenersAreReady = false;
    private MessageHandler messageHandler;
    private WebRTC webRTC;
    private TaskHandler taskHandler = new TaskHandler();
    private ApplicationState state = ApplicationState.IDLE;
    private LogService logService;
    private final ArrayList<AutoLogger> autoLoggers = new ArrayList<>();


    public static ApplicationService getInstance() {
        return ServiceManager.getService(ApplicationService.class);
    }

    public ApplicationState getState() {
        return state;
    }

    public void startSession() throws URISyntaxException {
  
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
        webRTC = new WebRTC();
        messageHandler = new MessageHandler();
        UUID uuid = UUID.randomUUID();
        String stringId = uuid.toString();
        webRTC.init(stringId);
        webRTC.startSignaling();
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

    public MessageHandler getMessageHandler(){
        return this.messageHandler;
    }

    public WebRTC getWebRTC(){
        return this.webRTC;
    }

    public TaskHandler getTaskHandler(){return this.taskHandler; }

}

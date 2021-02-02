package app.services.application;

import app.MessageHandler;
import app.TaskHandler;
import app.WebRTC;
import app.listeners.ListenerHelper;
import app.listeners.base.Event;
import app.services.log.LogService;
import com.intellij.openapi.components.ServiceManager;

import java.util.ArrayList;

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

    public void startSession() {

        if (state == ApplicationState.RECORDING) {
            return;
        }
        if(!logIsReady) {
           //Hier den task handler starten?
           taskHandler.init();
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

    public void setMessageHandler(MessageHandler messageHandler){
        this.messageHandler = messageHandler;
    }

    public MessageHandler getMessageHandler(){
        return this.messageHandler;
    }

    public void setWebRTC(WebRTC webRTC){
        this.webRTC = webRTC;
    }

    public WebRTC getWebRTC(){
        return this.webRTC;
    }

    public TaskHandler getTaskHandler(){return this.taskHandler; }

}

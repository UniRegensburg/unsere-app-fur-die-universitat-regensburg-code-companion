package app.services.application;

import app.MessageHandler;
import app.TaskHandler;
import app.WebRTC;
import app.listeners.ListenerHelper;
import com.intellij.openapi.components.ServiceManager;
import java.util.UUID;

public class ApplicationService {

    private boolean listenersAreReady = false;
    private MessageHandler messageHandler;
    private WebRTC webRTC;
    private TaskHandler taskHandler = new TaskHandler();
    private ApplicationState state = ApplicationState.IDLE;
    private ApplicationServiceListener listener;
    public boolean isStarted;

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

        if (!listenersAreReady) {
            ListenerHelper.initListener();
            listenersAreReady = true;
        }

        state = ApplicationState.RECORDING;
        webRTC = new WebRTC();
        messageHandler = new MessageHandler();
        UUID uuid = UUID.randomUUID();
        String stringId = uuid.toString();
        webRTC.init(stringId);
        webRTC.startSignaling();
        if(listener != null) {
            listener.onStarted();
        }
        isStarted = true;
    }

    public MessageHandler getMessageHandler(){
        return this.messageHandler;
    }

    public WebRTC getWebRTC(){
        return this.webRTC;
    }

    public TaskHandler getTaskHandler(){return this.taskHandler; }

    public void setListener(ApplicationServiceListener listener){
        this.listener = listener;
    }

    public interface ApplicationServiceListener{
        public void onStarted();
    }
}

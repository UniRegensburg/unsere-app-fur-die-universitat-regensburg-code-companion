package app.services.application;

import app.MessageHandler;
import app.TaskHandler;
import app.WebRTC;
import app.data.Const;
import app.interfaces.ApplicationServiceListener;
import app.listeners.ListenerHelper;
import app.interfaces.WebRTCListener;
import com.intellij.openapi.components.ServiceManager;
import dev.onvoid.webrtc.RTCDataChannelState;
import dev.onvoid.webrtc.RTCPeerConnectionState;

import java.util.UUID;

public class ApplicationService implements WebRTC.WebRTCListener {

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
        taskHandler.init();
        UUID uuid = UUID.randomUUID();
        String stringId = uuid.toString();
        webRTC.init(stringId);
        webRTC.startSignaling();
        webRTC.setWebRTCListener(this);
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

    @Override
    public void onConnectionStateChanged(RTCPeerConnectionState state) {
        if(listener != null){
            listener.onConnectionStateChanged(state);
        }

        if (state == RTCPeerConnectionState.CONNECTED) {
            messageHandler = new MessageHandler();
        }

        if(state == RTCPeerConnectionState.DISCONNECTED) {
            messageHandler.deleteAlreadyExistingErrors();
            UUID uuid = UUID.randomUUID();
            String stringId = uuid.toString();
            webRTC.init(stringId);
            webRTC.startSignaling();
            if(listener != null) {
                listener.onStarted();
            }
        }
    }


    @Override
    public void onMessageReceived(String message) {
        if (message.equals(Const.Events.REFRESH_DATA_MESSAGE)) {
            messageHandler.handleRefreshData();
        }
    }

    @Override
    public void onConnectedToSignaling() {
        listener.onConnectedToSignaling();
    }

    public interface ApplicationServiceListener{
        void onStarted();
        void onConnectionStateChanged(RTCPeerConnectionState state);
        void onConnectedToSignaling();
    }

}

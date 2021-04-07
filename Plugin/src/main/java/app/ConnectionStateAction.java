package app;

import app.services.application.ApplicationService;
import com.intellij.execution.process.OSProcessUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl;
import com.intellij.openapi.components.ServiceManager;
import dev.onvoid.webrtc.RTCDataChannelState;
import dev.onvoid.webrtc.RTCPeerConnectionState;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionStateAction extends AnAction {

    private final ApplicationService manager;
    private WebRTC webRTC;
    private boolean connected;


    public ConnectionStateAction() {
        manager = ServiceManager.getService(ApplicationService.class);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
    }

    @Override
    public void update(AnActionEvent e) {
        if(manager.getWebRTC() == null){
            webRTC = null;
        }
        if(webRTC == null && manager.getWebRTC() != null){
            webRTC = manager.getWebRTC();
        }
        if(webRTC != null) {
            if(webRTC.getConnectionState() == RTCPeerConnectionState.CONNECTED && webRTC.getDataChannelState() == RTCDataChannelState.OPEN){
                if(!connected){
                    e.getPresentation().setIcon(PluginIcons.Connected);
                    e.getPresentation().setText("Connected to #id");
                    try {
                        manager.getMessageHandler().sendProjectInformation();
                    } catch (Exception exception) {
                        Logger.getAnonymousLogger().log(Level.SEVERE, exception.getMessage());
                    }
                    connected = true;
                }

            }else{
                if(connected){
                    e.getPresentation().setIcon(PluginIcons.Disconnected);
                    e.getPresentation().setText("Not Connected");
                    connected = false;
                }
            }
        }
    }
}

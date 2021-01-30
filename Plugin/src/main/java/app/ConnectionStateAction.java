package app;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import dev.onvoid.webrtc.RTCPeerConnectionState;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

public class ConnectionStateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        e.getPresentation().setIcon(PluginIcons.Connected);
        e.getPresentation().setText("Connected to #id");
    }

    @Override
    public void update(AnActionEvent e) {

    }
}

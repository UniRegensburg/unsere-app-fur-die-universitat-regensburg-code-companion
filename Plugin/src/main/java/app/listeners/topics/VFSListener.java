package app.listeners.topics;

import app.services.application.ApplicationService;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import dev.onvoid.webrtc.RTCPeerConnectionState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VFSListener implements BulkFileListener {
    private final ApplicationService manager;

    public VFSListener() {
        manager = ServiceManager.getService(ApplicationService.class);
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        if(manager.getWebRTC().getConnectionState() == RTCPeerConnectionState.CONNECTED) {
            if (events.toString().contains("task.json")) {
                System.out.println("JSON Changed");
                manager.getTaskHandler().sendTaskInfo();
            }
        }
    }
}

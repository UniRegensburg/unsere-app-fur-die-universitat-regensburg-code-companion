package app;

import app.services.application.ApplicationService;
import com.google.gson.Gson;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import dev.onvoid.webrtc.RTCDataChannelState;
import dev.onvoid.webrtc.RTCPeerConnectionState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHandler {

    private final ApplicationService manager;
    private WebRTC webRTC;

    public MessageHandler() {
        manager = ServiceManager.getService(ApplicationService.class);
    }

    public void handleMessage(List<HighlightInfo> highlightInfoList, Document document) throws Exception {
        makeString(highlightInfoList, document);
    }

    private void makeString(List<HighlightInfo> highlightInfoList, Document document) throws Exception {
        send("ERROR LOG STARTED");
        for(int i = 0; i < highlightInfoList.size();i++){
            Map<String,String> message = new HashMap();

            String type = highlightInfoList.get(i).type.getAttributesKey().toString();
            String line = Integer.toString(1 + document.getLineNumber(highlightInfoList.get(i).startOffset));
            String description = highlightInfoList.get(i).getDescription();
            String occurence = highlightInfoList.get(i).getText();
            String tag = highlightInfoList.get(i).getSeverity().toString();

            message.put("tag",tag);
            message.put("type",type);
            message.put("ocurence", occurence);
            message.put("line", line);
            message.put("description", description);

            String json = new Gson().toJson(message);
            System.out.println(json);
            send(json);

        }
    }

    private void send(String data) throws Exception {
        if (webRTC == null) {
            webRTC = manager.getWebRTC();
        }
        if (webRTC != null) {
            if (webRTC.getConnectionState() == RTCPeerConnectionState.CONNECTED && webRTC.getDataChannelState() == RTCDataChannelState.OPEN) {
                webRTC.sendData(data);
            } else {
                System.out.println("Upps something went wrong!");
            }
        }
    }
}

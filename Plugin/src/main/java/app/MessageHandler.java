package app;

import app.data.Const;
import app.services.application.ApplicationService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import dev.onvoid.webrtc.RTCDataChannelState;
import dev.onvoid.webrtc.RTCPeerConnectionState;
import netscape.javascript.JSObject;
import org.junit.Assert;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

public class MessageHandler {

    private final ApplicationService manager;
    private WebRTC webRTC;
    private Map<HighlightInfo, ErrorMessage> alreadyPresentErrors = new HashMap<>();
    private List<String> errorMessages;
    private List<String> linesOfCodeMessages;
    private final Gson gson = new Gson();
    private final String ADD_ERROR_TAG = "add";
    private final String REMOVE_ERROR_TAG = "remove";
    private long messageId = 0;
    private String lastLinesOfCodeMessage = "";

    public MessageHandler() {
        manager = ServiceManager.getService(ApplicationService.class);
    }

    public void handleMessage(List<HighlightInfo> highlightInfoList, Document document) throws Exception {
        if (webRTC == null) {
            webRTC = manager.getWebRTC();
        }

        if (webRTC.getConnectionState() == RTCPeerConnectionState.CONNECTED && webRTC.getDataChannelState() == RTCDataChannelState.OPEN) {
            errorMessages = new ArrayList<>();
            makeString(highlightInfoList, document);
            checkForRemovedErrors(highlightInfoList);
            send(gson.toJson(errorMessages));
        }
    }

    public void handleLinesOfCodeMessage(int lineCount, Document document) throws Exception {
        if (webRTC == null) {
            webRTC = manager.getWebRTC();
        }

        if (webRTC.getConnectionState() == RTCPeerConnectionState.CONNECTED && webRTC.getDataChannelState() == RTCDataChannelState.OPEN) {
            linesOfCodeMessages = new ArrayList<>();
            makeLinesOfCodeMessage(lineCount, document);

            if (!linesOfCodeMessages.get(0).equals(lastLinesOfCodeMessage)) {
                lastLinesOfCodeMessage = linesOfCodeMessages.get(0);
                send(gson.toJson(linesOfCodeMessages));
            }
        }
    }

    /**
     * Checks for errors that were recorded but are no longer present
     * Use {@link Iterator} in loop instead of foreach to avoid {@link ConcurrentModificationException}
     * @param highlightInfoList the List of currently present errors
     */
    private void checkForRemovedErrors(List<HighlightInfo> highlightInfoList) {
        for (Iterator<HighlightInfo> iterator = alreadyPresentErrors.keySet().iterator(); iterator.hasNext();) {
            HighlightInfo alreadyPresentError = iterator.next();
            if (!highlightInfoList.contains(alreadyPresentError)) {
                addRemoveErrorMessage(alreadyPresentError);
                iterator.remove();
            }
        }
    }

    /**
     * Add a message that specifies removal of a no longer present error
     * @param alreadyPresentError the error that should be removed from the list in the app
     */
    private void addRemoveErrorMessage(HighlightInfo alreadyPresentError) {
        Map<String, String> message = new HashMap<>();

        message.put("add/remove", REMOVE_ERROR_TAG);
        message.put("id", "" + alreadyPresentErrors.get(alreadyPresentError).getId());

        String json = gson.toJson(message);
        errorMessages.add(json);
    }

    /**
     * Add a message that adds a new error to the list in our app
     * @param highlightInfo contains all information about the error
     * @param document the current document, needed to determine the line of the error
     */
    private ErrorMessage addNewErrorMessage(HighlightInfo highlightInfo, Document document) {
        Map<String,String> message = new HashMap<>();

        TextAttributesKey type = highlightInfo.type.getAttributesKey();
        int line = 1 + document.getLineNumber(highlightInfo.startOffset);
        String description = highlightInfo.getDescription();
        String occurence = highlightInfo.getText();
        HighlightSeverity tag = highlightInfo.getSeverity();

        ErrorMessage errorMessage = new ErrorMessage(tag, type, occurence, line, description, ++messageId);

        message.put("add/remove", ADD_ERROR_TAG);
        message.put("tag",tag.toString());
        message.put("type",type.toString());
        message.put("ocurence", occurence); // typo --> occurrence / used on android side? fix later?
        message.put("line", Integer.toString(line));
        message.put("description", description);
        message.put("id", "" + messageId);

        String json = gson.toJson(message);
        errorMessages.add(json);
        return errorMessage;
    }

    private void makeLinesOfCodeMessage(int lineCount, Document document) {
        Map<String, String> message = new HashMap<>();
        String fileName = FileEditorManager.getInstance(App.getCurrentProject()).getSelectedEditor().getFile().getName();

        message.put("stats", "linesOfCode");
        message.put("documentName", fileName);
        message.put("lineCount", String.valueOf(lineCount));

        String json = gson.toJson(message);
        linesOfCodeMessages.add(json);
    }

    private void makeString(List<HighlightInfo> highlightInfoList, Document document) {

        for (HighlightInfo highlightInfo : highlightInfoList) {
            if (alreadyPresentErrors.containsKey(highlightInfo)) {
                continue;
            }

            ErrorMessage errorMessage = addNewErrorMessage(highlightInfo, document);
            alreadyPresentErrors.put(highlightInfo, errorMessage);
        }

    }

    public void send(String data) throws Exception {
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

    public void handleRefreshData() {
        List<String> messagesToSend = new ArrayList<>();

        for (ErrorMessage error : alreadyPresentErrors.values()) {
            Map<String, String> message = new HashMap<>();
            message.put("add/remove", ADD_ERROR_TAG);
            message.put("tag", error.getTag().toString());
            message.put("type",error.getType().toString());
            message.put("ocurence", error.getOccurrence()); // typo --> occurrence / used on android side? fix later?
            message.put("line", Integer.toString(error.getLine()));
            message.put("description", error.getDescription());
            message.put("id", "" + error.getId());

            String json = gson.toJson(message);
            messagesToSend.add(json);
        }

        try {
            send(gson.toJson(messagesToSend));
        } catch (Exception e) {
            System.out.println("Refresh data failed!");
            e.printStackTrace();
        }

    }

    public void openGoogleQuery(String message) {
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> map = gson.fromJson(message, type);
        Assert.assertEquals(1, map.size());

        try {
            URI url = new URI(map.get(Const.Events.GOOGLE_QUERY_MESSAGE));
            Desktop.getDesktop().browse(url);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void sendProjectInformation() throws Exception {
        Map<String, String> message = new HashMap<>();

        message.put("projectName", App.getCurrentProject().getName());
        message.put("projectPath", App.getCurrentProject().getPresentableUrl());
        String json = gson.toJson(message);
        send(json);
    }

    public void prepareForReconnect() {
        lastLinesOfCodeMessage = "";
        alreadyPresentErrors.clear();
    }
}

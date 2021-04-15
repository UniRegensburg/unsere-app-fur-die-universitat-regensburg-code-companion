package com.example.codecompanion.util;

import android.util.Log;

import com.example.codecompanion.models.CompilerMessage;
import com.example.codecompanion.services.ErrorMessageReceiverService;
import com.example.codecompanion.services.LinesOfCodeMessageReceiverService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MessageManager cares about all messages received
 */
public class MessageManager {

    public interface MessageManagerListener{
        void onDataChanged();
    }

    private static MessageManager instance;
    private ErrorMessageReceiverService errorMessageReceiverService;
    private LinesOfCodeMessageReceiverService linesOfCodeMessageReceiverService;

    private MessageManager() {

    }

    public static MessageManager getInstance(){
        if(MessageManager.instance == null){
            MessageManager.instance = new MessageManager();
        }
        return MessageManager.instance;
    }

    public List<CompilerMessage> getCompilerMessages() {
        return errorMessageReceiverService.getCompilerMessages();
    }

    public void setErrorMessageListener(MessageManagerListener listener){
        errorMessageReceiverService.setListener(listener);
    }

    public void removeErrorMessageListener(){
        errorMessageReceiverService.setListener(null);
    }

    public ErrorMessageReceiverService getErrorMessageReceiverService() {
        return errorMessageReceiverService;
    }

    public void setErrorMessageReceiverService(ErrorMessageReceiverService errorMessageReceiverService) {
        this.errorMessageReceiverService = errorMessageReceiverService;
    }

    public void setLinesOfCodeMessageReceiverService(LinesOfCodeMessageReceiverService service) {
        this.linesOfCodeMessageReceiverService = service;
    }

    public void clearAllMessages() {
        if (errorMessageReceiverService != null) {
            errorMessageReceiverService.clearAllMessages();
        }
    }

    public static HashMap<String,String> unpackString(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject(message);
        HashMap<String, String> messageMap = new Gson().fromJson(jsonObject.toString(), HashMap.class);
        Log.d("Test",messageMap.toString());
        return messageMap;
    }
}

package com.example.codecompanion.util;

import com.example.codecompanion.services.ErrorMessageReceiverService;

import java.util.List;
import java.util.Map;

public class MessageManager {

    public interface MessageManagerListener{
        void onDataChanged();
    }

    private static MessageManager instance;
    private ErrorMessageReceiverService errorMessageReceiverService;

    private MessageManager() {

    }

    public static MessageManager getInstance(){
        if(MessageManager.instance == null){
            MessageManager.instance = new MessageManager();
        }
        return MessageManager.instance;
    }

    public List<Map<String, String>> getErrors() {
        return errorMessageReceiverService.getErrors();
    }

    public List<Map<String, String>> getWarnings() {
        return errorMessageReceiverService.getWarnings();
    }

    public void setListener(MessageManagerListener listener){
        errorMessageReceiverService.setListener(listener);
    }

    public void removeListener(){
        errorMessageReceiverService.setListener(null);
    }

    public ErrorMessageReceiverService getErrorMessageReceiverService() {
        return errorMessageReceiverService;
    }

    public void setErrorMessageReceiverService(ErrorMessageReceiverService errorMessageReceiverService) {
        this.errorMessageReceiverService = errorMessageReceiverService;
    }
}

package com.example.codecompanion.util;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    public interface MessageManagerListener{
        public void onDataChanged();
    }

    private List<String> errors;
    private List<String> warnings;
    private MessageManagerListener listener;
    private static MessageManager instance;

    private MessageManager() {
        errors = new ArrayList<String>();
        warnings = new ArrayList<String>();
    }

    public static MessageManager getInstance(){
        if(MessageManager.instance == null){
            MessageManager.instance = new MessageManager();
        }
        return MessageManager.instance;
    }

    public void addMessage(String message, int type){
        switch(type){
            case 0:
                warnings.add(message);
                break;
            case 1:
                errors.add(message);
                break;
        }
        if(listener != null) {
            listener.onDataChanged();
        }
    }

    public void removeMessage(String message, int type){
        switch(type){
            case 0:
                warnings.remove(message);
                break;
            case 1:
                errors.remove(message);
                break;
        }
        if(listener != null) {
            listener.onDataChanged();
        }
    }

    public void removeAll(){
        errors.clear();
        warnings.clear();
        if(listener != null) {
            listener.onDataChanged();
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setListener(MessageManagerListener listener){
        this.listener = listener;
    }

    public void removeListener(){
        this.listener = null;
    }
}

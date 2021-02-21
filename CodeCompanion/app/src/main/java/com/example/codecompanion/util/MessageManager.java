package com.example.codecompanion.util;

import android.util.Log;

import com.example.codecompanion.services.ErrorMessageRecieverService;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageManager {

    public interface MessageManagerListener{
        void onDataChanged();
    }

    private static MessageManager instance;
    private ErrorMessageRecieverService errorMessageRecieverService;

    private MessageManager() {

    }

    public static MessageManager getInstance(){
        if(MessageManager.instance == null){
            MessageManager.instance = new MessageManager();
        }
        return MessageManager.instance;
    }

    public List<Map<String, String>> getErrors() {
        return errorMessageRecieverService.getErrors();
    }

    public List<Map<String, String>> getWarnings() {
        return errorMessageRecieverService.getWarnings();
    }

    public void setListener(MessageManagerListener listener){
        errorMessageRecieverService.setListener(listener);
    }

    public void removeListener(){
        errorMessageRecieverService.setListener(null);
    }

    public ErrorMessageRecieverService getErrorMessageRecieverService() {
        return errorMessageRecieverService;
    }

    public void setErrorMessageRecieverService(ErrorMessageRecieverService errorMessageRecieverService) {
        this.errorMessageRecieverService = errorMessageRecieverService;
    }
}

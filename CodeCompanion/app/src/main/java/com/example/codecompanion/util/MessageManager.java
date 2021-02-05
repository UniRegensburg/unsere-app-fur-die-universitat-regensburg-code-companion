package com.example.codecompanion.util;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManager {

    public interface MessageManagerListener{
        public void onDataChanged();
    }

    private List<Map> errors;
    private List<Map> warnings;
    private MessageManagerListener listener;
    private static MessageManager instance;

    private MessageManager() {
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
    }

    public static MessageManager getInstance(){
        if(MessageManager.instance == null){
            MessageManager.instance = new MessageManager();
        }
        return MessageManager.instance;
    }

    public void handleMessage(String message) throws JSONException {
        if(message.equals("ERROR LOG STARTED")){
            removeAll();
        }else{
            HashMap<String, String> messageMap = unpackString(message);
            String tag = messageMap.get("tag");
            String desc = messageMap.get("description");
            String line = messageMap.get("line");
            String type = messageMap.get("type");
            String oc = messageMap.get("ocurence");
            int messageType;

            if(tag.equals("WARNING")){
                messageType = 0;
            }else{
                messageType = 1;
            }

            String messageText = oc + ": " + line + "\n" + type + "\n" + desc;
            Map convertedMessage = new HashMap();
            convertedMessage.put("tag", messageMap.get("tag"));
            convertedMessage.put("text", messageText);

            addMessage(convertedMessage);
        }
    }

    public void addMessage(Map message){
        Object tag = message.get("tag");
        if ("WARNING".equals(tag)) {
            warnings.add(message);
        } else if ("ERROR".equals(tag)) {
            errors.add(message);
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

    public List<Map> getErrors() {
        return errors;
    }

    public List<Map> getWarnings() {
        return warnings;
    }

    public void setListener(MessageManagerListener listener){
        this.listener = listener;
    }

    public void removeListener(){
        this.listener = null;
    }


    private HashMap<String,String> unpackString(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject(message);
        HashMap<String, String> messageMap = new Gson().fromJson(jsonObject.toString(), HashMap.class);
        Log.d("Test",messageMap.toString());
        return messageMap;
    }
    

}

package com.example.codecompanion.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {

    public interface TaskManagerListener{
        public void onTaskReceived();
    }

    private static TaskManager instance;
    private TaskManagerListener listener;
    private List<JSONObject> tasks;
    private HashMap<String, String> informationMap;

    private TaskManager() {
    }

    public static TaskManager getInstance() {
        if(TaskManager.instance == null) {
            TaskManager.instance = new TaskManager();
        }
        return TaskManager.instance;
    }

    public void setListener(TaskManagerListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }

    public void handleTaskInfo(String message) throws JSONException {
        informationMap = unpackString(message);
        tasks = extractTaskObjects(message);
        if(listener != null) {
            listener.onTaskReceived();
        }
    }

    public List<JSONObject> getTasks() {
        return this.tasks;
    }

    public HashMap<String, String> getInformation() {
        return this.informationMap;
    }

    private List<JSONObject> extractTaskObjects(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject(message);
        JSONArray tasks = jsonObject.getJSONArray("tasks");
        List<JSONObject> taskList = new ArrayList<>();
        for(int i = 0; i < tasks.length(); i++) {
            taskList.add((JSONObject) tasks.get(i));
        }


        return taskList;
    }

    private HashMap<String, String> unpackString(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject(message);
        HashMap<String, String> information = new Gson().fromJson(jsonObject.getJSONObject("informations").toString(), HashMap.class);
        return information;
    }


}

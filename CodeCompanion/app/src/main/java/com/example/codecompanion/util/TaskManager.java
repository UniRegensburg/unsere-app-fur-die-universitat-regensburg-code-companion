package com.example.codecompanion.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    public interface TaskManagerListener{
        public void onTaskReceived();
    }

    private static TaskManager instance;
    private TaskManagerListener listener;

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
        HashMap<String, String> informationMap = unpackString(message);
        JSONArray taskList = extractTaskObjects(message);
/*        System.out.println(taskList.length());
        for(int i = 0; i < taskList.length(); i++) {
            System.out.println(taskList.getJSONObject(i).getString("headline"));
        }*/
    }

    private JSONArray extractTaskObjects(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject(message);
        JSONArray tasks = jsonObject.getJSONArray("tasks");

        return tasks;
    }

    private HashMap<String, String> unpackString(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject(message);
        HashMap<String, String> information = new Gson().fromJson(jsonObject.getJSONObject("informations").toString(), HashMap.class);
        return information;
    }


}

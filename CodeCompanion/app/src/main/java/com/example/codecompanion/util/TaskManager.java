package com.example.codecompanion.util;

import com.example.codecompanion.entity.Task;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Implements functionality for tasks objects
 */
public class TaskManager {

    public interface TaskManagerListener{
        public void onTaskReceived();
    }

    public interface DeadlineLineListener{
        public void onDeadlineReceived(Date deadline, String title);
    }

    private static TaskManager instance;
    private TaskManagerListener listener;
    private DeadlineLineListener deadlineListener;
    private ArrayList<Task> tasks;
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
        if(deadlineListener != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = dateFormat.parse(informationMap.get("deadline"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            deadlineListener.onDeadlineReceived(date, informationMap.get("name"));
        }
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public HashMap<String, String> getInformation() {
        return this.informationMap;
    }

    private ArrayList<Task> extractTaskObjects(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject(message);
        JSONArray tasks = jsonObject.getJSONArray("tasks");
        ArrayList<Task> taskList = new ArrayList<>();
        for(int i = 0; i < tasks.length(); i++) {
            String taskString = tasks.getJSONObject(i).getString("description");
            Task task = new Task(taskString, false);
            taskList.add(task);
        }


        return taskList;
    }

    private HashMap<String, String> unpackString(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject(message);
        HashMap<String, String> information = new Gson().fromJson(jsonObject.getJSONObject("informations").toString(), HashMap.class);
        return information;
    }

    public void removeDeadlineListener(){
        this.deadlineListener = null;
    }

    public void setDeadlineListener(DeadlineLineListener listener){
        this.deadlineListener = listener;
    }


}

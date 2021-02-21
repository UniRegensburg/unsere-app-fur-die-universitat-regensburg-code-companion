package com.example.codecompanion.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.codecompanion.util.MessageManager;
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

public class ErrorMessageRecieverService extends Service {

	private final IBinder binder = new ErrorMessageBinder();

	private final List<Map<String, String>> errors;
	private final List<Map<String, String>> warnings;
	private MessageManager.MessageManagerListener listener;

	public ErrorMessageRecieverService() {
		errors = new ArrayList<>();
		warnings = new ArrayList<>();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void handleMessage(String data) throws JSONException {
		List<Map<String,String>> unpackedMessageList = new ArrayList<>();

		JSONArray jsonArray = new JSONArray(data);
		List<String> messages = new Gson().fromJson(jsonArray.toString(), ArrayList.class);
		for (String message : messages) {
			Log.d("message", message);
			unpackedMessageList.add(unpackString(message));
		}

		for (Map<String, String> map : unpackedMessageList) {
			if (Objects.equals(map.get("add/remove"), "add")) {
				addMessage(map);
			} else if (Objects.equals(map.get("add/remove"), "remove")) {
				removeMessage(map);
			}
		}
	}

	public List<Map<String, String>> getErrors() {
		return errors;
	}

	public List<Map<String, String>> getWarnings() {
		return warnings;
	}

	public class ErrorMessageBinder extends Binder {
		public ErrorMessageRecieverService getService() {
			return ErrorMessageRecieverService.this;
		}
	}

	private void addMessage(Map<String, String> message){
		String tag = message.get("tag");
		if ("WARNING".equals(tag)) {
			warnings.add(message);
		} else if ("ERROR".equals(tag)) {
			errors.add(message);
		}
		notifyDataChanged();
	}

	private void removeMessage(Map<String, String> message) {
		String id = message.get("id");

		for (Iterator<Map<String, String>> iterator = warnings.iterator(); iterator.hasNext();) {
			Map<String, String> warning = iterator.next();
			if (Objects.equals(warning.get("id"), id)) {
				iterator.remove();
				notifyDataChanged();
				return;
			}
		}

		for (Iterator<Map<String, String>> iterator = errors.iterator(); iterator.hasNext();) {
			Map<String, String> error = iterator.next();
			if (Objects.equals(error.get("id"), id)) {
				iterator.remove();
				notifyDataChanged();
				return;
			}
		}
	}

	private HashMap<String,String> unpackString(String message) throws JSONException {
		JSONObject jsonObject = new JSONObject(message);
		HashMap<String, String> messageMap = new Gson().fromJson(jsonObject.toString(), HashMap.class);
		Log.d("Test",messageMap.toString());
		return messageMap;
	}

	private void notifyDataChanged() {
		if(listener != null) {
			listener.onDataChanged();
		}
	}

	public IBinder getBinder() {
		return binder;
	}

	public MessageManager.MessageManagerListener getListener() {
		return listener;
	}

	public void setListener(MessageManager.MessageManagerListener listener) {
		this.listener = listener;
	}
}

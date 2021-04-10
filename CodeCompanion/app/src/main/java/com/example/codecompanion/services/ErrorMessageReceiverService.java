package com.example.codecompanion.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.codecompanion.cache.StatsCache;
import com.example.codecompanion.models.CompilerMessage;
import com.example.codecompanion.models.CompilerMessageCatalogue;
import com.example.codecompanion.models.SeverityType;
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

public class ErrorMessageReceiverService extends Service {

	private final IBinder binder = new ErrorMessageBinder();
	private final List<CompilerMessage> compilerMessages;
	private MessageManager.MessageManagerListener listener;

	public ErrorMessageReceiverService() {
		compilerMessages = new ArrayList<>();
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
			unpackedMessageList.add(MessageManager.unpackString(message));
		}

		for (Map<String, String> map : unpackedMessageList) {
			if (Objects.equals(map.get("add/remove"), "add")) {
				addMessage(map);
			} else if (Objects.equals(map.get("add/remove"), "remove")) {
				removeMessage(map);
			}
		}
	}

	public class ErrorMessageBinder extends Binder {
		public ErrorMessageReceiverService getService() {
			return ErrorMessageReceiverService.this;
		}
	}

	private void addMessage(Map<String, String> message){
		String tag = message.get("tag");
		String description = message.get("description");
		if ("WARNING".equals(tag)) {
			compilerMessages.add(new CompilerMessage(SeverityType.WARNING, description,
					CompilerMessageCatalogue.getShortExplanationByDescription(description),
					CompilerMessageCatalogue.getLongExplanationByDescription(description),
					Integer.parseInt(Objects.requireNonNull(message.get("id")))));
			StatsCache.addWarningToProject();
		} else if ("ERROR".equals(tag)) {
			compilerMessages.add(new CompilerMessage(SeverityType.ERROR, description,
					CompilerMessageCatalogue.getShortExplanationByDescription(description),
					CompilerMessageCatalogue.getLongExplanationByDescription(description),
					Integer.parseInt(Objects.requireNonNull(message.get("id")))));
			StatsCache.addErrorToProject();
		}
		notifyDataChanged();
	}

	private void removeMessage(Map<String, String> message) {
		int id = Integer.parseInt(Objects.requireNonNull(message.get("id")));

		for (Iterator<CompilerMessage> iterator = compilerMessages.iterator(); iterator.hasNext();) {
			CompilerMessage warning = iterator.next();
			if (warning.getId() == id) {
				iterator.remove();
				notifyDataChanged();
				return;
			}
		}
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

	public List<CompilerMessage> getCompilerMessages() {
		return compilerMessages;
	}

	public void clearAllMessages() {
		compilerMessages.clear();
	}
}

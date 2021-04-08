package com.example.codecompanion.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.codecompanion.MainActivity;
import com.example.codecompanion.cache.StatsCache;
import com.example.codecompanion.db.AppDatabase;
import com.example.codecompanion.db.DocumentInformation;
import com.example.codecompanion.db.DocumentInformationDAO;
import com.example.codecompanion.db.ProjectInformation;
import com.example.codecompanion.models.CompilerMessage;
import com.example.codecompanion.models.CompilerMessageCatalogue;
import com.example.codecompanion.models.SeverityType;
import com.example.codecompanion.util.MessageManager;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class LinesOfCodeMessageReceiverService extends Service {

	private final IBinder binder = new LinesOfCodeMessageBinder();
	private List<DocumentInformation> documentInformationList;
	private AppDatabase db = null;
	private DocumentInformation currentDocumentInformation;

	public LinesOfCodeMessageReceiverService() {

	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void handleMessage(String data) throws JSONException {
		if (db == null) {
			db = MainActivity.db;
		}

		currentDocumentInformation = null;
		JSONArray jsonArray = new JSONArray(data);
		JSONObject jsonObject = new JSONObject(jsonArray.getString(0));
		int lineCount = jsonObject.getInt("lineCount");
		String documentName = jsonObject.getString("documentName");

		// fire query only once and store results in a variable
		if (documentInformationList == null || documentInformationList.isEmpty()) {
			documentInformationList = db.documentInformationDAO().findAll();
		}

		// save the currently open document to the database if the user opens a new document
		if (StatsCache.currentDocument != null && !StatsCache.currentDocument.getDocumentName().equals(documentName)) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executor);
			ListenableFuture<?> submit = listeningExecutorService.submit(StatsCache::updateCurrentDocument);
			submit.addListener(() -> startUpdateProcess(documentName, lineCount, true), executor);
			return;
		}

		startUpdateProcess(documentName, lineCount, false);
	}

	private void startUpdateProcess(String documentName, int lineCount, boolean needsToReloadCachedDocument) {
		retrieveDocumentFromDb(documentName);
		handleDocumentUpdate(documentName, lineCount, needsToReloadCachedDocument);
	}

	private void retrieveDocumentFromDb(String documentName) {
		// search for the open document in the cached database, and retrieve it if found
		for (DocumentInformation documentInformation : documentInformationList) {
			if (documentInformation.getDocumentName().equals(documentName)) {
				currentDocumentInformation = documentInformation;
				break;
			}
		}
	}

	private void handleDocumentUpdate(String documentName, int lineCount, boolean needsToReloadCachedDocument) {
		// null if new document is not yet in database, not null if already exists
		if (currentDocumentInformation == null) {
			currentDocumentInformation = new DocumentInformation(documentName, lineCount, StatsCache.currentProject.getId());
			insertNewDocumentInDb(currentDocumentInformation);
			needsToReloadCachedDocument = true;
		} else {
			currentDocumentInformation.setLinesOfCode(lineCount);
			StatsCache.currentDocument = currentDocumentInformation;
			StatsCache.lineCountChanged();
		}

		reloadDocumentIfNeeded(needsToReloadCachedDocument);
	}

	private void reloadDocumentIfNeeded(boolean needsToReloadCachedDocument) {
		if (needsToReloadCachedDocument) {
			StatsCache.currentDocumentChanged();
		}
	}


	/**
	 * Inserts new {@link DocumentInformation} into the database and updates the Cached variables
	 */
	private void insertNewDocumentInDb(DocumentInformation currentDocumentInformation) {
		long insertId = db.documentInformationDAO().insert(currentDocumentInformation);
		StatsCache.currentDocument = db.documentInformationDAO().findById(insertId);
		documentInformationList = db.documentInformationDAO().findAll();
	}

	public class LinesOfCodeMessageBinder extends Binder {
		public LinesOfCodeMessageReceiverService getService() {
			return LinesOfCodeMessageReceiverService.this;
		}
	}

	public IBinder getBinder() {
		return binder;
	}
}

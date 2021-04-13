package com.example.codecompanion.cache;

import com.example.codecompanion.MainActivity;
import com.example.codecompanion.db.AppDatabase;
import com.example.codecompanion.db.DocumentInformation;
import com.example.codecompanion.db.ProjectInformation;
import com.example.codecompanion.util.StatsChangedListener;

import org.joda.time.DateTime;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class used to hold information the about currently opened project and document.
 * Data is stored in static variables of this class in order to minimize database queries.
 * The database is only updated when necessary (e.g. on opening new documents, the user trying to access the data)
 */
public abstract class StatsCache {

	public static DocumentInformation currentDocument;
	public static ProjectInformation currentProject;
	public static String currentProjectTag;
	public static String currentProjectName;
	public static int warningsSinceLastUpdate = 0;
	public static int errorsSinceLastUpdate = 0;
	private static StatsChangedListener statsChangedListener;

	public static DateTime projectOpenedDate;

	private static final AppDatabase db = MainActivity.db;

	/**
	 * Updates the current {@link DocumentInformation} in the database
	 */
	public static void updateCurrentDocument() {
		if (currentDocument != null) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.execute(() -> db.documentInformationDAO().updateDocumentInformation(currentDocument));

			// quickfix to wait for DB query.. alternatives??
			try {
				Thread.sleep(300);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Updates the current {@link ProjectInformation} in the database
	 */
	public static void updateCurrentProject() {
		if (currentProject != null) {
			db.projectInformationDAO().updateProject(currentProject);
			errorsSinceLastUpdate = 0;
			warningsSinceLastUpdate = 0;
		}
	}

	/**
	 * Helper method to add an error to the current {@link ProjectInformation}
	 */
	public static void addErrorToProject() {
		if (currentProject != null) {
			currentProject.totalErrors++;
			errorsSinceLastUpdate++;
		}

		if (statsChangedListener != null) {
			statsChangedListener.errorsChanged();
		}
	}

	/**
	 * Helper method to add a warning to the current {@link ProjectInformation}
	 */
	public static void addWarningToProject() {
		if (currentProject != null) {
			currentProject.totalWarnings++;
			warningsSinceLastUpdate++;
		}

		if (statsChangedListener != null) {
			statsChangedListener.warningsChanged();
		}
	}

	public static void setStatsChangedListener(StatsChangedListener listener) {
		statsChangedListener = listener;
	}

	public static void currentDocumentChanged() {
		if (statsChangedListener != null) {
			statsChangedListener.documentChanged();
		}
	}

	public static void lineCountChanged() {
		if (statsChangedListener != null) {
			statsChangedListener.linesOfCodeChanged();
		}
	}

	public static void handleConnectionClosed() {
		statsChangedListener.connectionClosed();
	}
}

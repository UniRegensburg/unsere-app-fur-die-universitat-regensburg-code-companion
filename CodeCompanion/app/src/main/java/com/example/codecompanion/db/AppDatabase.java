package com.example.codecompanion.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Creates database for user statistics and project informations
 */

@Database(entities = {DocumentInformation.class, ProjectInformation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	private static AppDatabase instance;

	public static AppDatabase getDatabase(Context context) {
		if (instance == null) {
			instance = Room.databaseBuilder(context, AppDatabase.class, "database").allowMainThreadQueries().build();
		}
		return instance;
	}

	public abstract DocumentInformationDAO documentInformationDAO();
	public abstract ProjectInformationDAO projectInformationDAO();
}

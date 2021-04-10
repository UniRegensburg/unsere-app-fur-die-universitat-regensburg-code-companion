package com.example.codecompanion.db;


import androidx.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DocumentInformationListTypeConverter {

	private static final String DOCUMENT_NAME = "documentName";
	private static final String LINES_OF_CODE = "linesOfCode";
	private static final String PARENT_PROJECT_ID = "parentProjectId";

	@TypeConverter
	public static List<DocumentInformation> fromString(String string) {
		if (string == null || string.isEmpty()) {
			return null;
		}

		List<DocumentInformation> returnVal = new ArrayList<>();

		try {
			JSONArray jsonArray = new JSONArray(string);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String documentName = jsonObject.getString(DOCUMENT_NAME);
				int linesOfCode = jsonObject.getInt(LINES_OF_CODE);
				long parentProjectId = jsonObject.getLong(PARENT_PROJECT_ID);
				DocumentInformation information = new DocumentInformation(documentName, linesOfCode, parentProjectId);
				returnVal.add(information);
			}

			return returnVal;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@TypeConverter
	public static String documentInformationListToString(List<DocumentInformation> inputList) {
		JSONArray jsonArray = new JSONArray();

		if (inputList == null) {
			return null;
		}

		for (DocumentInformation document : inputList) {
			JSONObject jsonObject = new JSONObject();
			String documentName = document.getDocumentName();
			int linesOfCode = document.getLinesOfCode();
			long parentProjectId = document.parentProjectId;

			try {
				jsonObject.put(DOCUMENT_NAME, documentName);
				jsonObject.put(LINES_OF_CODE, linesOfCode);
				jsonObject.put(PARENT_PROJECT_ID, parentProjectId);
				jsonArray.put(jsonObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonArray.toString();
	}
}

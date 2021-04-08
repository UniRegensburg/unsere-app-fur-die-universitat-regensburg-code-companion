package com.example.codecompanion.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = ProjectInformation.class,
parentColumns = "id", childColumns = "parent_project_id", onDelete = ForeignKey.CASCADE)})
public class DocumentInformation {
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	private long id;

	@ColumnInfo(name = "document_name")
	private final String documentName;


	@ColumnInfo(name = "lines_of_code")
	private int linesOfCode;

	@ColumnInfo(name = "parent_project_id")
	public long parentProjectId;

	public DocumentInformation(String documentName, int linesOfCode, long parentProjectId) {
		this.documentName = documentName;
		this.linesOfCode = linesOfCode;
		this.parentProjectId = parentProjectId;
	}

	public DocumentInformation(DocumentInformation documentInformation) {
		this(documentInformation.getDocumentName(), documentInformation.getLinesOfCode(), documentInformation.parentProjectId);
		this.id = documentInformation.id;
	}

	public long getId() {
		return id;
	}

	public String getDocumentName() {
		return documentName;
	}

	public int getLinesOfCode() {
		return linesOfCode;
	}

	public void setLinesOfCode(int linesOfCode) {
		this.linesOfCode = linesOfCode;
	}

	public void setId(long id) {
		this.id = id;
	}
}

package com.example.codecompanion.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.codecompanion.MainActivity;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "ProjectInformation")
public class ProjectInformation {
	@PrimaryKey(autoGenerate = true)
	private long id;

	@ColumnInfo(name = "project_name")
	private String projectName;

	@ColumnInfo(name = "project_path")
	private String projectPath;

	@ColumnInfo(name = "project_tag")
	private String projectTag;

	@ColumnInfo(name = "total_errors")
	public int totalErrors;

	@ColumnInfo(name = "total_warnings")
	public int totalWarnings;

	@ColumnInfo(name = "seconds_spent_on_project")
	public int secondsSpentOnProject;

	public ProjectInformation(String projectTag) {
		this.projectTag = projectTag;
	}

	public long getId() {
		return id;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public String getProjectTag() {
		return projectTag;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public void setProjectTag(String projectTag) {
		this.projectTag = projectTag;
	}

}

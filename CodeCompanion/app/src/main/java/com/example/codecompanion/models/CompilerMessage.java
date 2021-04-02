package com.example.codecompanion.models;

import org.jetbrains.annotations.NotNull;

public class CompilerMessage {

	private SeverityType severityType;
	private String description;
	private String shortExplanation;
	private String longExplanation;
	private final int id;

	public CompilerMessage(SeverityType severityType, String description, String shortExplanation, String longExplanation, int id) {
		this.severityType = severityType;
		this.description = description;
		this.shortExplanation = shortExplanation;
		this.longExplanation = longExplanation;
		this.id = id;
	}

	@NotNull
	@Override
	public String toString(){
		return description;
	}

	public SeverityType getSeverityType() {
		return severityType;
	}

	public String getDescription() {
		return description;
	}

	public String getShortExplanation() {
		return shortExplanation;
	}

	public String getLongExplanation() {
		return longExplanation;
	}

	public int getId() {
		return id;
	}
}

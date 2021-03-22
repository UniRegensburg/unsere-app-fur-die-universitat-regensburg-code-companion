package com.example.codecompanion.models;

import org.jetbrains.annotations.NotNull;

public class CompilerMessage {

	private SeverityType severityType;
	private String description;
	private String shortExplanation;
	private String longExplanation;

	public CompilerMessage(SeverityType severityType, String description, String shortExplanation, String longExplanation) {
		this.severityType = severityType;
		this.description = description;
		this.shortExplanation = shortExplanation;
		this.longExplanation = longExplanation;
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
}

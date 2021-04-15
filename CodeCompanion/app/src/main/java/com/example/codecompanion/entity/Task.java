package com.example.codecompanion.entity;

/**
 * This class creates a task object with description and a boolean if the task is checked
 */

public class Task {

    private String description;
    private boolean checked;

    public Task(String description, boolean checked) {
        this.description = description;
        this.checked = checked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

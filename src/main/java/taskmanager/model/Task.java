package taskmanager.model;

import java.time.LocalDateTime;

/**
 * Represents a task with its details and weather sensitivity.
 */
public class Task {

    private final String id; // The unique identifier of the task.
    private String title; // The main title of the task.
    private String description; // A detailed description of the task.
    private LocalDateTime dueDateTime; // The scheduled date and time for the task.
    private boolean weatherSensitive; // Indicates if the task depends on weather conditions.

    public Task(String id, String title, LocalDateTime dueDateTime, boolean weatherSensitive) {
        this.id = id;
        this.title = title;
        this.dueDateTime = dueDateTime;
        this.weatherSensitive = weatherSensitive;
    }

    /** Gets the task ID. */
    public String getId() {
        return id;
    }

    /** Gets the task title. */
    public String getTitle() {
        return title;
    }

    /** Sets the task title. */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Gets the task description. */
    public String getDescription() {
        return description;
    }

    /** Sets the task description. */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Gets the task due date and time. */
    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    /** Sets the task due date and time. */
    public void setDueDateTime(LocalDateTime dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    /** Checks if the task is weather sensitive. */
    public boolean isWeatherSensitive() {
        return weatherSensitive;
    }

    /** Sets the weather sensitivity of the task. */
    public void setWeatherSensitive(boolean weatherSensitive) {
        this.weatherSensitive = weatherSensitive;
    }
}
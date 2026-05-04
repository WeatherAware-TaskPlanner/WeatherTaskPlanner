package taskmanager.impl;

import taskmanager.api.SchedulePlanner;
import taskmanager.api.TaskManager;
import taskmanager.api.TaskManager.TaskManagerBuilder;

public class DefaultTaskManagerBuilder implements TaskManager.TaskManagerBuilder {

    private String Key; // The API key for authenticating with external weather service.
    private SchedulePlanner planner; // planner responsible for generating schedule recommendations based on tasks and weather.
    private String path; // file path where tasks will be stored or loaded from.

    /**
     * Sets the weather API key for the task manager.
     *
     * @param Key the API key to be used for weather data retrieval.
     * @return this builder instance for method chaining.
     */
    @Override
    public TaskManager.TaskManagerBuilder withWeatherApiKey(String Key) {
        this.Key = Key;
        return this;
    }

    /**
     * Sets the storage path for the task manager's data.
     *
     * @param path the file path string indicating where data should be stored.
     * @return this builder instance for method chaining.
     */
    @Override
    public TaskManager.TaskManagerBuilder withStoragePath(String path) {
        this.path = path;
        return this;
    }

    /**
     * Sets the schedule planner logic for the task manager.
     *
     * @param planner the implementation to be used.
     * @return this builder instance for method chaining.
     */
    @Override
    public TaskManager.TaskManagerBuilder withSchedulePlanner(SchedulePlanner planner) {
        this.planner = planner;
        return this;
    }

    /**
     * Creates a new TaskManager with the provided settings
     * (Weather API Key, Storage Path, and Schedule Planner).
     * 
     * @return a new, fully initialized instance of DefaultTaskManager.
     */
    @Override
    public TaskManager build() {
        return new DefaultTaskManager(Key, planner);
        // DefaultTaskManager Manager = new DefaultTaskManager(Key,planner);
        // return Manager;
    }

}

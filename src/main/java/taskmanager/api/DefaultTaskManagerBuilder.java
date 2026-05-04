package taskmanager.api;

import taskmanager.api.TaskManager.TaskManagerBuilder;

public class DefaultTaskManagerBuilder implements TaskManager.TaskManagerBuilder {

    private String Key;
    private SchedulePlanner planner;
    private String path;
    
    @Override
    public TaskManager.TaskManagerBuilder withWeatherApiKey(String Key) {
        this.Key = Key;
        return this;
    }

    @Override
    public TaskManager.TaskManagerBuilder withStoragePath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public TaskManager.TaskManagerBuilder withSchedulePlanner(SchedulePlanner planner) {
        this.planner = planner;
        return this;
    }

    @Override
    public TaskManager build() {
        // return new DefaultTaskManager(Key, new DefaultSchedulePlaner());
        return new DefaultTaskManager(Key, planner);
        // DefaultTaskManager Manager = new DefaultTaskManager(Key,planner);
        // return Manager;
    }

}

 

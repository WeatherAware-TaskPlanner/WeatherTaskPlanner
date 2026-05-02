package taskmanager.api;

import taskmanager.api.TaskManager.TaskManagerBuilder;

public class DefaultTaskManagerBuilder implements TaskManager.TaskManagerBuilder {

    private String Key;
    private SchedulePlanner planner;

    
    @Override
    public TaskManager.TaskManagerBuilder withWeatherApiKey(String Key) {
        this.Key = Key;
        return this;
    }

    @Override
    public TaskManager.TaskManagerBuilder withStoragePath(String path) {

        return this;
    }

    @Override
    public TaskManager.TaskManagerBuilder withSchedulePlanner(SchedulePlanner planner) {
        this.planner = planner;
        return this;
    }

    @Override
    public TaskManager build() {
        DefaultTaskManager Manager = new DefaultTaskManager(Key,planner);
        return Manager;
    }

}

 

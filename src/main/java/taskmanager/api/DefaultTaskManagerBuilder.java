package taskmanager.api;

public class DefaultTaskManagerBuilder implements TaskManager.TaskManagerBuilder {

    private String Key;

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
    public TaskManager build() {
        DefaultTaskManager Manager = new DefaultTaskManager(Key);
        return Manager;
    }

}

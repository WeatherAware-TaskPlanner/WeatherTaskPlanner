package taskmanager.api;

import java.util.ArrayList;
import java.util.List;

import reactor.core.publisher.Mono;

public class DefaultTaskManager implements TaskManager {

    List<Task> MyTasks;
    String Key;


    public DefaultTaskManager(String Key) {
        Key = Key;
        MyTasks = new ArrayList<>();
    }

    @Override
    public void addTask(Task task) {
        MyTasks.add(task);
    }

    @Override
    public List<Task> getTasks() {
        return MyTasks;
    }

    @Override
    public void removeTask(String taskId) {
        //   --------------------------------------
    }


    @Override
    public Mono<WeatherForecast> fetchWeather(String location) {
        //  --------------------------------------
        return Mono.empty();
    }

    @Override
    public SchedulePlanner getPlanner() {
        //  --------------------------------------
        return null;
    }

   
    
}

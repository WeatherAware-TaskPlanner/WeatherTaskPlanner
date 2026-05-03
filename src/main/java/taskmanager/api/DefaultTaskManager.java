package taskmanager.api;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

public class DefaultTaskManager implements TaskManager {

    List<Task> MyTasks;
    String Key;
    private final SchedulePlanner planner;

    public TaskService taskService = new DefaultTaskService();

    public DefaultTaskManager(String Key, SchedulePlanner planner) {
        this.Key = Key;
        this.planner = planner;
        MyTasks = new ArrayList<>();
        // returnDataFromFile();
    }

    @Override
    public void addTask(Task task) {

        taskService.addTask(task).block();

        // MyTasks.add(task);
        // SyncFile().subscribe();

    }

    @Override
    public List<Task> getTasks() {
        return taskService.findAllTasksAsList().block();
    }

    @Override
    public void removeTask(String taskId) {

        taskService.removeTask(taskId).block();

        // for(Task t:MyTasks){
        // if(t.getId().equals(taskId)){
        // MyTasks.remove(t);
        // break;
        // }
        // }

        // SyncFile().subscribe();
    }

    private final WebClient webClient = WebClient.create();

    private record WeatherResponse(List<forecastInfo> list) {
    }

    private record forecastInfo(Main main, List<Weather> weather, String dt_txt, double pop) {
    }

    private record Main(double temp) {
    }

    private record Weather(String description) {
    }

    @Override
    public Mono<WeatherForecast> fetchWeather(String location) {
        String URL = "https://api.openweathermap.org/data/2.5/forecast?q=" + location + "&appid=" + Key
                + "&units=metric";

        return webClient
                .get().uri(URL).retrieve().bodyToMono(WeatherResponse.class)
                .onErrorMap(e -> new WeatherAPIException("failed to fech weather data from API", e))
                .map(response -> {
                    WeatherForecast f = new WeatherForecast(location);
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    for (forecastInfo n : response.list()) {
                        String time = n.dt_txt();
                        LocalDateTime fTime = LocalDateTime.parse(time, format);

                        f.addF(fTime, n.main().temp(), n.weather().get(0).description(), n.pop());
                    }

                    return f;
                }
                // response -> {
                // forecastInfo info = response.list().get(0);
                // double temp = info.main().temp();
                // String condition = info.weather().get(0).description();
                // String time = info.dt_txt();
                // DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd
                // HH:mm:ss");
                // LocalDateTime fTime = LocalDateTime.parse(time, format);
                // return new WeatherForecast(location, fTime, temp, condition, info.pop());
                // }

                );
    }

    @Override
    public SchedulePlanner getPlanner() {
        return planner;
    }

}

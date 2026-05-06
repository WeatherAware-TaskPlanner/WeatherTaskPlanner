package taskmanager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import reactor.core.publisher.Mono;
import taskmanager.api.SchedulePlanner;
import taskmanager.api.TaskManager;
import taskmanager.api.TaskService;
import taskmanager.expception.WeatherAPIException;
import taskmanager.model.Task;
import taskmanager.model.WeatherForecast;

import org.springframework.web.reactive.function.client.WebClient;

public class DefaultTaskManager implements TaskManager {

    List<Task> MyTasks;
    /** stores the list of tasks */

    String Key;/* stores the API key */
    private final SchedulePlanner planner;
    /** stores the schedule planner */
    /** cache to store weather forecasts per city and reduce API calls. */
    private final java.util.concurrent.ConcurrentHashMap<String, Mono<WeatherForecast>> weatherCache = new ConcurrentHashMap<>();

    /** internal task operations */
    public TaskService taskService = new DefaultTaskService();

    /**
     * Initializes the manager with security keys and the planner logic.
     * 
     * @param Key     weather API access key
     * @param planner the scheduling planner to use for task recommendations
     */
    public DefaultTaskManager(String Key, SchedulePlanner planner) {
        this.Key = Key;
        this.planner = planner;
        MyTasks = new ArrayList<>();

    }

    /**
     * Saves a new task and waits for the operation to complete
     *
     * @param task The task to be registered
     */
    @Override
    public void addTask(Task task) {

        taskService.addTask(task).subscribe();

    }

    /**
     * Retrieves all stored tasks as a synchronous list
     * 
     * @return a list of all tasks
     * 
     */
    @Override
    public List<Task> getTasks() {
        return taskService.findAllTasksAsList().block();
    }

    /**
     * Deletes a task by ID and blocks until finished
     *
     * @param taskId the ID of the task to remove
     */
    @Override
    public void removeTask(String taskId) {

        taskService.removeTask(taskId).subscribe();

    }

    /**
     * HTTP client configured for making weather API requests
     *
     * @param location the location for which to fetch weather data
     * @return a Mono emitting the weather forecast
     */
    private final WebClient webClient = WebClient.create();

    /**
     * A record representing the weather response from the API
     *
     * @param list the list of forecast information
     */
    private record WeatherResponse(List<forecastInfo> list) {
    }

    /**
     * A record representing the forecast information for a specific time.
     *
     * @param main    Core metrics like temperature
     * @param weather the list of weather conditions
     * @param dt_txt  the date and time of the forecast
     * @param pop     the probability of precipitation
     */
    private record forecastInfo(Main main, List<Weather> weather, String dt_txt, double pop) {
    }

    /**
     * 
     * A record representing the main weather data, including temperature.
     * 
     * @param temp the temperature in Celsius
     * 
     */
    private record Main(double temp) {
    }

    /**
     * 
     * A record representing the weather conditions, including a description.
     * 
     * @param description the description of the weather condition
     * 
     */
    private record Weather(String description) {
    }

    /**
     * Fetches weather data asynchronously without blocking.
     *
     * @param location the location for which to fetch weather data
     * @return a Mono emitting the weather forecast
     */
    @Override
    public Mono<WeatherForecast> fetchWeather(String location) {
        String cityKey = location.toLowerCase();
        return weatherCache.computeIfAbsent(cityKey, loc -> {
            String URL = "https://api.openweathermap.org/data/2.5/forecast?q=" + loc + "&appid=" + Key
                    + "&units=metric";
            return webClient
                    .get().uri(URL).retrieve().bodyToMono(WeatherResponse.class)
                    .onErrorMap(e -> new WeatherAPIException("failed to fetch weather data from API", e))
                    .map(response -> {
                        WeatherForecast f = new WeatherForecast(loc);
                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                        for (forecastInfo n : response.list()) {
                            String time = n.dt_txt();
                            LocalDateTime fTime = LocalDateTime.parse(time, format);

                            f.addF(fTime, n.main().temp(), n.weather().get(0).description(), n.pop());
                        }

                        return f;
                    }).cache(java.time.Duration.ofHours(5));

        });
    }

    /**
     * Provides the scheduling engine used to organize tasks.
     * 
     * @return The current schedule planner instance.
     */
    @Override
    public SchedulePlanner getPlanner() {
        return planner;
    }

}

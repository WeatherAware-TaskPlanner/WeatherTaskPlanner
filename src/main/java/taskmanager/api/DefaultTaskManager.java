package taskmanager.api;

import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.io.IOException;
import java.io.PipedWriter;

import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

public class DefaultTaskManager implements TaskManager {

    List<Task> MyTasks;
    String Key;

    public DefaultTaskManager(String Key) {
        this.Key = Key;
        MyTasks = new ArrayList<>();
    }

    public void SyncFile() {
        try {
            FileWriter f = new FileWriter("Tasks.txt", true);
            PrintWriter p = new PrintWriter(f);
            for (Task t : MyTasks) {
                p.println(t.getId() + "," + t.getTitle() + "," + t.getDescription()
                        + "," + t.getDueDateTime() + "," + t.isWeatherSensitive());
            }
            p.close();
            System.out.println("Tasks added to file");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public List<Task> returnDataFromFile() {
        try {
            FileReader f = new FileReader("Tasks.txt");
            BufferedReader r = new BufferedReader(f);
            String task;
            MyTasks.clear();
            while ((task = r.readLine()) != null) {
                String[] newList = task.split(",");
                String id = newList[0];
                String title = newList[1];
                String Description = newList[2];
                String dateString = newList[3].trim();
                LocalDateTime DueDate = LocalDateTime.parse(dateString);
                boolean WeatherSensitive = Boolean.parseBoolean(newList[4]);
                Task newTask = new Task(id, title, DueDate, WeatherSensitive);
                newTask.setDescription(Description);
                MyTasks.add(newTask);
            }
            r.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return MyTasks;

    }

    @Override
    public void addTask(Task task) {
        MyTasks.add(task);
        SyncFile();

    }

    @Override
    public List<Task> getTasks() {
        return MyTasks;
    }

    @Override
    public void removeTask(String taskId) {
        // --------------------------------------
    }

    private final WebClient webClient = WebClient.create();

    private record WeatherResponse(Main main, List<Weather> weather) {
        public record Main(double temp) {
        }

        public record Weather(String description) {
        }
    }

    @Override
    public Mono<WeatherForecast> fetchWeather(String location) {
        String URL = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + Key
                + "&units=metric";

        return webClient
                .get().uri(URL).retrieve().bodyToMono(WeatherResponse.class)
                .map(response -> {
                    double temp = response.main().temp();
                    String condition = response.weather().get(0).description();
                    return new WeatherForecast(location, java.time.LocalDateTime.now(), temp, condition, 0.0);
                });
    }

    @Override
    public SchedulePlanner getPlanner() {
        return new DefaultSchedulePlaner();
    }

}

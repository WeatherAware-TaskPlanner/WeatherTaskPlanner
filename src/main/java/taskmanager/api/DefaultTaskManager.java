package taskmanager.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;

import javax.swing.filechooser.FileFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.io.IOException;
import java.io.PipedWriter;
import java.time.format.DateTimeFormatter;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import org.springframework.web.reactive.function.client.WebClient;

public class DefaultTaskManager implements TaskManager {

    List<Task> MyTasks;
    String Key;
    private final SchedulePlanner planner;

    public DefaultTaskManager(String Key, SchedulePlanner planner) {
        this.Key = Key;
        this.planner = planner;
        MyTasks = new ArrayList<>();
        returnDataFromFile();
    }

    public Mono<Void> SyncFile() {
        return Mono.fromRunnable(() ->{

        try {
            FileWriter f = new FileWriter("Tasks.txt", false);
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

        }).then().subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<Task>> returnDataFromFile() {
        return Mono.fromCallable(() -> {
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
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public void addTask(Task task) {
        MyTasks.add(task);
        SyncFile().subscribe();

    }

    @Override
    public List<Task> getTasks() {
        return MyTasks;
    }

    @Override
    public void removeTask(String taskId) {
       
        for(Task t:MyTasks){
            if(t.getId().equals(taskId)){
                MyTasks.remove(t);
                break;
            }
        }

        SyncFile().subscribe();
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
                .map(response ->{
                    WeatherForecast f=new WeatherForecast(location);
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    for(forecastInfo n:response.list()){
                       String time = n.dt_txt(); 
                       LocalDateTime fTime = LocalDateTime.parse(time, format);

                       f.addF(fTime, n.main().temp(), n.weather().get(0).description());
                    }
                
                    return f;
                   } 
                //     response -> {
                //     forecastInfo info = response.list().get(0);
                //     double temp = info.main().temp();
                //     String condition = info.weather().get(0).description();
                //     String time = info.dt_txt();
                //     DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                //     LocalDateTime fTime = LocalDateTime.parse(time, format);
                //     return new WeatherForecast(location, fTime, temp, condition, info.pop());
                // }
            
            );
    }

    @Override
    public SchedulePlanner getPlanner() {
        return planner;
    }

}

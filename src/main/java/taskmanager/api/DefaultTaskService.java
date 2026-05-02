package taskmanager.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class DefaultTaskService implements TaskService {

    List<Task> MyTasks=new ArrayList<>();

        public DefaultTaskService() {
            returnDataFromFile().block();
        }

    public Mono<Void> SyncFile() {
        return Mono.fromRunnable(() -> {

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
    public Mono<Void> addTask(Task task) {

        return Mono.fromRunnable(()->{
            MyTasks.add(task);
            SyncFile().subscribe();
        }).then().subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> removeTask(String taskId) {

        return Mono.fromRunnable(()->{
            for(Task t:MyTasks){
                if(t.getId().equals(taskId)){
                    MyTasks.remove(t);
                    
                    break;
                }
            }
            SyncFile().subscribe();
        }).then().subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Task> findTaskById(String taskId) {

        return Mono.fromCallable(()->{
            for(Task t:MyTasks){
                if(t.getId().equals(taskId)){
                    return t;
                }
            }
            throw new TaskNotFoundException("Id Wrong");
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Task> findAllTasks() {

        return Flux.fromIterable(MyTasks)
            .subscribeOn(Schedulers.boundedElastic());
    }

    // S
    @Override
    public Mono<List<Task>> findAllTasksAsList() {
        return Mono.empty();
    }

}
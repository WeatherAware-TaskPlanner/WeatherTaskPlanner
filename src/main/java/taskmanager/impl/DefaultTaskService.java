package taskmanager.impl;

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
import taskmanager.api.TaskService;
import taskmanager.expception.InvalidTaskException;
import taskmanager.expception.TaskNotFoundException;
import taskmanager.model.Task;


public class DefaultTaskService implements TaskService {

    List<Task> MyTasks = new java.util.concurrent.CopyOnWriteArrayList<>();

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
         if(task == null || task.getId().isBlank() || task.getTitle().isBlank() || task.getDueDateTime() == null) {
                return Mono.error(new InvalidTaskException("Task and its required fields must not be null"));
            }
        return Mono.fromRunnable(() -> MyTasks.add(task))
                .then(SyncFile())
                .subscribeOn(Schedulers.boundedElastic());
           
        
    }

    @Override
    public Mono<Void> removeTask(String taskId) {

        return Flux.fromIterable(MyTasks)
            .filter(t ->t.getId().equals(taskId))
            .next()
            .switchIfEmpty(Mono.error(new TaskNotFoundException("Task not found")))
            .doOnNext(t -> MyTasks.remove(t))
            .then(SyncFile())
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Task> findTaskById(String taskId) {

        return Flux.fromIterable(MyTasks)
            .filter(t -> t.getId().equals(taskId))
            .next()
            .switchIfEmpty(Mono.error(new TaskNotFoundException("Task not found")))
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Task> findAllTasks() {

        return Flux.fromIterable(MyTasks);
                
    }

    @Override
    public Mono<List<Task>> findAllTasksAsList() {
        return Mono.just(MyTasks);
    }

}
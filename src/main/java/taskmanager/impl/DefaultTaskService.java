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
    // A thread-safe list that stores all the current tasks in memory.
    List<Task> MyTasks = new java.util.concurrent.CopyOnWriteArrayList<>();

    /** Initializes the DefaultTaskService. */
    public DefaultTaskService() {
        returnDataFromFile().block();
    }

    /**
     * Saves all current tasks to the storage file.
     * Precondition: The task list is initialized.
     * Postcondition: The storage file is updated to match the list exactly
     * 
     * @return an empty Mono when the writing is complete
     */
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

    /**
     * Loads tasks from the storage file into the list.
     *
     * Precondition: The storage file ("Tasks.txt") exists.
     * Postcondition: The list is cleared and filled with the file's data.
     *
     * @return a Mono containing the loaded list of tasks
     */
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

    /**
     * Adds a new task to the list and updates the storage file.
     *
     * Precondition: The task object is not null, and its ID and Title are not
     * blank.
     * Postcondition: The task is added to the list and saved to the physical file.
     *
     * @param task the task to be added
     * @return an empty Mono on success, or an error if the task is invalid
     */
    @Override
    public Mono<Void> addTask(Task task) {
        if (task == null || task.getId().isBlank() || task.getTitle().isBlank() || task.getDueDateTime() == null) {
            return Mono.error(new InvalidTaskException("Task and its required fields must not be null"));
        }
        return Mono.fromRunnable(() -> MyTasks.add(task))
                .then(SyncFile())
                .subscribeOn(Schedulers.boundedElastic());

    }

    /**
     * Removes a task by its ID and updates the storage file.
     *
     * Precondition: A task with the provided ID exists in the list.
     * Postcondition: The task is removed from list, and the storage file is
     * updated.
     *
     * @param taskId the ID of the task to remove
     * @return an empty Mono on success, or an error if the task is not found
     */
    @Override
    public Mono<Void> removeTask(String taskId) {

        return Flux.fromIterable(MyTasks)
                .filter(t -> t.getId().equals(taskId))
                .next()
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Task not found")))
                .doOnNext(t -> MyTasks.remove(t))
                .then(SyncFile())
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Retrieves a specific task by its ID from the list.
     *
     * Precondition: A task with the provided ID exists in the list.
     * Postcondition: The list and storage file remain unchanged.
     *
     * @param taskId the ID of the task to find
     * @return a Mono emitting the found task, or an error if not found
     */
    @Override
    public Mono<Task> findTaskById(String taskId) {

        return Flux.fromIterable(MyTasks)
                .filter(t -> t.getId().equals(taskId))
                .next()
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Task not found")))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Retrieves all tasks as a reactive stream.
     *
     * Precondition: The list is initialized.
     * Postcondition: The list and storage file remain unchanged.
     *
     * @return a Flux emitting all tasks currently in list
     */
    @Override
    public Flux<Task> findAllTasks() {

        return Flux.fromIterable(MyTasks);

    }

    /**
     * Retrieves the entire list of tasks in a single response.
     *
     * Precondition: The MyTasks list is initialized.
     * Postcondition: The list and storage file remain unchanged.
     *
     * @return a Mono containing the complete list of tasks
     */
    @Override
    public Mono<List<Task>> findAllTasksAsList() {
        return Mono.just(MyTasks);
    }

}
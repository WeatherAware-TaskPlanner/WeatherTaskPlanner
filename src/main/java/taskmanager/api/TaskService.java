package taskmanager.api;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import taskmanager.model.Task;

import java.util.List;

/**
 * Service for task operations
 *  Reactive service handling core task logic 
 */
public interface TaskService {

    /**
     * Adds a new task to the system.
     * @param task the task to be added
     * @return a Mono that completes when the task is added
     */
    Mono<Void> addTask(Task task);

    /**
     *  Removes a task by its ID.
     * @param taskId the ID of the task to be removed
     * @return a Mono that completes when the task is removed
     */
    Mono<Void> removeTask(String taskId);

    /** 
     * Finds a task by its ID.
     * @param taskId the ID of the task to find
     * @return a Mono emitting the found task, or empty if not found
    */
    Mono<Task> findTaskById(String taskId);

    /**
     * Retrieves all tasks 
     * @return a Flux emitting all tasks 
     */
    Flux<Task> findAllTasks();

    /**
     * Retrieves all tasks as a List.
     * @return a Mono emitting a List of all tasks
     */
    Mono<List<Task>> findAllTasksAsList();
}
package taskmanager.expception;
/**
 * Exception thrown when a specific task cannot be found by its ID
 */
public class TaskNotFoundException extends RuntimeException {
    /**
     * Thrown when trying to access a task that doesn't exist in the system
     * @param taskId The ID of the task that couldn't be found
     */
    public TaskNotFoundException(String taskId) {
        super("Task not found: " + taskId);
    }
}


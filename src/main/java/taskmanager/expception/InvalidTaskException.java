package taskmanager.expception;

/**
 * Custom error thrown when task data (like title ) is missing or incorrect
 */
public class InvalidTaskException extends RuntimeException {
    /**
     * Thrown when a task has missing or invalid data
     *
     * @param message message Details about why the task is invalid
     */
    public InvalidTaskException(String message) {
        super(message);
    }
}
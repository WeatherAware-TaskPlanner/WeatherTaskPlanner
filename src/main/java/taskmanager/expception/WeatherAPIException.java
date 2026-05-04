package taskmanager.expception;

/**
 * Thrown when there's an issue fetching weather data from the external service
 */
public class WeatherAPIException extends RuntimeException {
    /**
     * 
     *
     * @param message Error details
     * @param cause   the underlying cause of the exception 
     */
    public WeatherAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
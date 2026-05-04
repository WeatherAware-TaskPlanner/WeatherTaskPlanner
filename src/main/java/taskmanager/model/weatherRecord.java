package taskmanager.model;
import java.time.LocalDateTime;

/**
 * A class representing a single weather record, containing information about the time,
 *  temperature, weather condition, and probability of precipitation.
 */
public class weatherRecord {
    
    private LocalDateTime time;/** The date and time of the weather record */
    private double temp;/** The temperature in Celsius */
    private String condition;/** A description of the weather condition */
    private double pop;/** The probability of precipitation */

    /* 
     * Main constructor to initialize weather stats
     *
     * @param time the date 
     * @param temp the temperature in Celsius
     * @param condition a description of the weather condition (e.g., "clear sky", "rain")
     * @param pop the probability of precipitation 
     */
    public weatherRecord(LocalDateTime time, double temp, String condition, double pop) {
        this.time = time;
        this.temp = temp;
        this.condition = condition;
        this.pop = pop;
    }

    /**
     * Gets the date and time of the weather record.
     * @return the date and time of the weather record
     */
    public LocalDateTime getTime() {
        return time;
    }
    /**
     * Gets the temperature in Celsius from the weather record.
     * @return the temperature in Celsius
     */
    public double getTemp() {
        return temp;
    }

    /**
     * Gets the weather condition from the weather record.
     * @return the weather condition
     */
    public String getCondition() {
        return condition;
    }
    /**
     * Gets the probability of precipitation from the weather record.
     * @return the probability of precipitation
     */
    public double getPop() {
        return pop;
    }

}

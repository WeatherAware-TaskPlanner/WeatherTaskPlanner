package taskmanager.model;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of weather data for a specific area over time
 */
public class WeatherForecast {


    private final String location;/** stores the city */
    private final List<weatherRecord> dList=new ArrayList<>();/** stores the weather records */

    /**
     * Creates a new forecast container for a location
     * @param location the location for which the forecast is being created
     */
    public WeatherForecast(String location) {
        this.location = location;
    }

    /**
     * Records a new weather entry into the list
     * @param time  time of the weather record
     * @param temp temperature in Celsius
     * @param condition weather condition description (e.g., "clear sky", "rain")
     * @param pop probability of precipitation
     */
    public void addF(LocalDateTime time, double temp, String condition, double pop) {
        dList.add(new weatherRecord(time, temp, condition, pop));
    }

    /**
     * he target location for this forecast
     * @return the location associated with this weather forecast
     */
    public String getLocation() {
        return location;
    }

    /**
     * Retrieves the list of weather records for this forecast
     * @return the list of weather records
     */
    public List<weatherRecord> getdList() {
        return dList;
    }


    /**
     * Calculates the average rain probability across all records
     * @return the average probability of precipitation, or 0.0 if there are no records
     */
    public double getPrecipitationProbability() {
    return dList.stream()
            .mapToDouble(w -> w.getPop())
            .average()
            .orElse(0.0);
}
    

}
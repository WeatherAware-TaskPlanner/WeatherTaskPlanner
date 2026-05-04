package taskmanager.api;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DefaultSchedulePlaner implements SchedulePlanner {
   
    DefaultTaskManager w;

    /**
     * Purpose: Analyzes a list of tasks against a given weather forecast to
     * generate schedule recommendations.
     * 
     * @param tasks    The list of objects to be evaluated.
     * @param forecast The containing weather records for a specific location.
     * @return A Mono emitting a list of ScheduleRecommendation for the tasks.
     */
    @Override
    public Mono<List<ScheduleRecommendation>> suggestSchedule(List<Task> tasks, WeatherForecast forecast) {

        return Flux.fromIterable(tasks)

                .filter(t -> t.isWeatherSensitive())
                .map(t -> {
                    weatherRecord w = closest(t, forecast.getdList());
                    return new ScheduleRecommendation(t, WeatherStatus(w));

                })

                .collectList();

    }

    /**
     * Purpose: Finds the weather record that is closest in time to a task's due
     * date.
     * 
     * @param t t The task containing the target due date and time.
     * @param r The list of weatherRecords to search through.
     * @return The weatherRecord with the minimum time difference, or null if the
     *         list is empty.
     */
    public weatherRecord closest(Task t, List<weatherRecord> r) {
        return r.stream()
                .min((a, b) -> {
                    long A = Math.abs(Duration.between(t.getDueDateTime(), a.getTime()).toMinutes());

                    long B = Math.abs(Duration.between(t.getDueDateTime(), b.getTime()).toMinutes());

                    return Long.compare(A, B);
                }).orElse(null);
    }

    /**
     * Purpose: Evaluates a weather record and returns a descriptive warning.
     * 
     * @param w The weatherRecord containing temperature and conditions.
     * @return A descriptive warning string.
     */
    public String WeatherStatus(weatherRecord w) {

        double temp = w.getTemp();
        String condition = w.getCondition().toLowerCase();
        double pop = w.getPop();

        if (temp > 40) {
            return "it's very sunny";
        } else if (temp < 15) {
            return "it's very cold";
        } else if (condition.contains("thunderstorm")) {
            return "there is a thunderstorm";
        } else if (condition.contains("rain")) {
            return "there is a rain";
        } else if (condition.contains("haze")) {
            return "there is a haze";
        } else if (pop > 0.5) {
            return "there is a high chance of rain";
        } else {
            return "weather is perfect for this task";
        }

    }

    /**
     * Purpose: Fetches the weather forecast for a specified location and generates
     * schedule recommendations.
     *
     * @param tasks    The list of objects to be evaluated.
     * @param location Name of the city to fetch the weather for.
     * @return A Mono emitting a list of ScheduleRecommendations based on the
     *         weather conditions.
     */
    @Override
    public Mono<List<ScheduleRecommendation>> suggestScheduleForLocation(List<Task> tasks, String location) {

        return w.fetchWeather(location)
                .flatMap(forecast -> suggestSchedule(tasks, forecast));
    }

}

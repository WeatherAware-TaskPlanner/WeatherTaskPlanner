package taskmanager.api;

import reactor.core.publisher.Mono;
import taskmanager.model.ScheduleRecommendation;
import taskmanager.model.Task;
import taskmanager.model.WeatherForecast;

import java.util.List;

//A planner that suggests optimal task schedules based on weather conditions.
public interface SchedulePlanner {
        /**
         * Recommends schedules based on the provided weather forecast.
         *
         * Precondition: tasks and forecast are not null.
         * Postcondition: Returns a stream of recommendations.
         *
         * @param tasks    the tasks to evaluate
         * @param forecast the weather data
         * @return a Mono emitting the recommendations
         */
        Mono<List<ScheduleRecommendation>> suggestSchedule(
                        List<Task> tasks,
                        WeatherForecast forecast);

        /**
         * Fetches the weather forecast for a specific location and generates
         * recommendations.
         *
         * Precondition: The tasks list is not null, and the location string is not
         * blank.
         * Postcondition: Returns a stream of recommendations
         * 
         * @param tasks    the list of tasks to evaluate
         * @param location the city name to fetch the weather
         * @return a Mono emitting a list of schedule recommendations
         */
        Mono<List<ScheduleRecommendation>> suggestScheduleForLocation(
                        List<Task> tasks,
                        String location);
}

// record ScheduleRecommendation(Task task, String recommendation) {}
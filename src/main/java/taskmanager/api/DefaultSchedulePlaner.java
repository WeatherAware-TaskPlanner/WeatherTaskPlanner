package taskmanager.api;

import java.util.List;

import reactor.core.publisher.Mono;

public class DefaultSchedulePlaner implements SchedulePlanner {

    @Override
    public Mono<List<ScheduleRecommendation>> suggestSchedule(List<Task> tasks, WeatherForecast forecast) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'suggestSchedule'");
    }

    @Override
    public Mono<List<ScheduleRecommendation>> suggestScheduleForLocation(List<Task> tasks, String location) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'suggestScheduleForLocation'");
    }

}

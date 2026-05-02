package taskmanager.api;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DefaultSchedulePlaner implements SchedulePlanner {

    @Override
    public Mono<List<ScheduleRecommendation>> suggestSchedule(List<Task> tasks, WeatherForecast forecast) {
        // TODO Auto-generated method stub

        
        return Flux.fromIterable(tasks)

            .filter(t ->t.isWeatherSensitive())
            .map(t -> {
                weatherRecord w = closest(t,forecast.getdList());
                return new ScheduleRecommendation(t, WeatherStatus(w));
                
            })

            .collectList();

        
    }

    public weatherRecord closest(Task t ,List<weatherRecord> r){
        return r.stream()
        .min((a,b) ->{
            long A=Math.abs(Duration.between(t.getDueDateTime(), a.getTime()).toMinutes());

            long B=Math.abs(Duration.between(t.getDueDateTime(), b.getTime()).toMinutes());

            return Long.compare(A, B);
        }).orElse(null);
    }

    

    public String WeatherStatus(weatherRecord w){

        double temp = w.getTemp();
        String condition = w.getCondition().toLowerCase();

        if(temp >40){
            return "it's very sunny";
        }else if(temp <15){
            return "it's very cold";
        }else if(condition.contains("thunderstorm")){
            return "there is a thunderstorm";
        }else if(condition.contains("rain")){
            return "there is a rain";
        }else  if(condition.contains("haze")){
             return "there is a haze";
        }else{
            return null;
        }
     
       
    }
    
    
    //S
    @Override
    public Mono<List<ScheduleRecommendation>> suggestScheduleForLocation(List<Task> tasks, String location) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'suggestScheduleForLocation'");
    }

}

package taskmanager.api;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class WeatherForecast {


    private final String location;
    private final List<weatherRecord> dList=new ArrayList<>();


    public WeatherForecast(String location) {
        this.location = location;
    }

    public void addF(LocalDateTime time, double temp, String condition) {
        dList.add(new weatherRecord(time, temp, condition));
    }

    public String getLocation() {
        return location;
    }

    public List<weatherRecord> getdList() {
        return dList;
    }

    


    // private final String location;

    // private final List<DefaultTaskManager.forecastInfo> forecastInfoList;

    // public WeatherForecast(String location, List<forecastInfo> forecastInfoList) {
    //     this.location = location;
    //     this.forecastInfoList = (forecastInfoList != null) ? forecastInfoList : new ArrayList<>();
    // }

    // public String getLocation() {
    //     return location;
    // }

    // public List<DefaultTaskManager.forecastInfo> getForecastInfoList() {
    //     return forecastInfoList;
    // }

    

    // private final LocalDateTime time;
    // private final double temperatureCelsius;
    // private final String condition;
    // private final double precipitationProbability;

    // public WeatherForecast(String location, LocalDateTime time,
    // double temperatureCelsius,
    // String condition,
    // double precipitationProbability) {
    // this.location = location;
    // this.time = time;
    // this.temperatureCelsius = temperatureCelsius;
    // this.condition = condition;
    // this.precipitationProbability = precipitationProbability;
    // }

    // public String getLocation() {
    // return location;
    // }

    // public LocalDateTime getTime() {
    // return time;
    // }

    // public double getTemperatureCelsius() {
    // return temperatureCelsius;
    // }

    // public String getCondition() {
    // return condition;
    // }

    // public double getPrecipitationProbability() {
    // return precipitationProbability;
    // }
}
package taskmanager.api;

import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.PipedWriter;

import reactor.core.publisher.Mono;

public class DefaultTaskManager implements TaskManager {

    List<Task> MyTasks;
    String Key;


    public DefaultTaskManager(String Key) {
        Key = Key;
        MyTasks = new ArrayList<>();
    }


    public void SyncFile(){
          try{
                FileWriter f=new FileWriter("Tasks.txt",true);
                PrintWriter p=new PrintWriter(f);
                for(Task t: MyTasks){
                    p.println("id: " + t.getId() + ", title: " + t.getTitle()+ "Description :" +t.getDescription()+ "Due Date: " + t.getDueDateTime() + "Weather Sensitive: " + t.isWeatherSensitive());
                }
                p.close();
                System.out.println("Tasks added to file");
            }catch(IOException e){
                System.out.println("Error: " + e.getMessage());
            }
    }

    @Override
    public void addTask(Task task) {
        MyTasks.add(task);
        SyncFile();
          
    }

    @Override
    public List<Task> getTasks() {
        return MyTasks;
    }

    @Override
    public void removeTask(String taskId) {
        //   --------------------------------------
    }


    @Override
    public Mono<WeatherForecast> fetchWeather(String location) {
        //  --------------------------------------
        return Mono.empty();
    }

    @Override
    public SchedulePlanner getPlanner() {
        //  --------------------------------------
        return null;
    }

   
    
}

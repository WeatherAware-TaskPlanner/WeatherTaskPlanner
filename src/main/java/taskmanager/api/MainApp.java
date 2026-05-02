package taskmanager.api;

import java.time.LocalDateTime;
import taskmanager.api.WeatherForecast;
import taskmanager.api.weatherRecord;

public class MainApp {

        public static void main(String[] args) {
                // Build the TaskManager (students will implement DefaultTaskManager)
                TaskManager tm = TaskManager.builder()
                                .withWeatherApiKey("169fb16b23c8963bb5d881cd53c1b855")
                                .withSchedulePlanner(new DefaultSchedulePlaner())
                                .withStoragePath("Tasks.txt")
                                .build();

                // Add a couple of test tasks
                Task task1 = new Task(
                                "task-001",
                                "Morning run",
                                LocalDateTime.now().plusHours(2),
                                true);
                Task task2 = new Task(
                                "task-002",
                                "Coding session",
                                LocalDateTime.now().plusHours(4),
                                false);

                tm.addTask(task1);
                tm.addTask(task2);

                System.out.println("Tasks loaded: " + tm.getTasks().size());

                // Wire this to the Swing UI
                // SmartTaskManagerFrame frame = new SmartTaskManagerFrame(tm);
                // javax.swing.SwingUtilities.invokeLater(() -> frame.setVisible(true));

                System.out.println("Tasks ----------> " + tm.getTasks());

                for (Task t : tm.getTasks()) {
                        System.out.println("id: " + t.getId() + ", title: " + t.getTitle() + "Description :"
                                        + t.getDescription());
                }

                tm.fetchWeather("Reykjavik")
                                .flatMap(forecast -> tm.getPlanner().suggestSchedule(tm.getTasks(), forecast))
                                .subscribe(recommendations -> {
                                        for (ScheduleRecommendation rec : recommendations) {
                                                System.out.println(
                                                                "task: " + rec.task().getTitle() +
                                                                                " | Recommendation: " + rec.recommendation());
                                        }
                                });

                // SmartTaskManagerFrame frame = new SmartTaskManagerFrame(tm);
                // frame.setVisible(true);

        }
}
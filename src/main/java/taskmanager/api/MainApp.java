package taskmanager.api;

import java.time.LocalDateTime;

public class MainApp {

    public static void main(String[] args) {
        // Build the TaskManager (students will implement DefaultTaskManager)
        TaskManager tm = TaskManager.builder()
                .withWeatherApiKey("3ff3cbdc1e244a653f8c9e0264375a0f")
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
            System.out.println("id: " + t.getId() + ", title: " + t.getTitle() + "Description :" + t.getDescription());
        }
    }
}
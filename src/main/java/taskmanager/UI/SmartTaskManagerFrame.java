
package taskmanager.UI;

import taskmanager.api.TaskManager;
import taskmanager.expception.InvalidTaskException;
import taskmanager.expception.TaskNotFoundException;
import taskmanager.impl.DefaultTaskManager;
import taskmanager.model.ScheduleRecommendation;
import taskmanager.model.Task;
import taskmanager.model.WeatherForecast;
import taskmanager.api.TaskService;
import taskmanager.api.SchedulePlanner;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class SmartTaskManagerFrame extends JFrame {

    private final TaskManager taskManager;
    // to be initialized from taskManager.impl
    private final SchedulePlanner schedulePlanner;

    private final JTable taskTable;
    private final DefaultTableModel tableModel;

    private final JButton updateWeatherButton;
    private final JLabel statusLabel;
    private final JButton addButton;
    private final JButton deleteButton;
    private final JButton edButton;
    private final JButton suggestButton;

    private final String[] columnNames = { "ID", "Title", "Due Time", "Weather Sensitive", "Status" };

    public SmartTaskManagerFrame(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.schedulePlanner = taskManager.getPlanner();

        setTitle("Smart Task Manager (Swing)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 400);

        tableModel = new DefaultTableModel(columnNames, 0);
        taskTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);

        updateWeatherButton = new JButton("Update Weather for Selected Task");
        updateWeatherButton.setEnabled(false);

        addButton = new JButton("Add Task");
        deleteButton = new JButton("Delete Task");
        deleteButton.setEnabled(false);
        edButton = new JButton("Edit Task");
        edButton.setEnabled(false);
        suggestButton = new JButton("Suggest Schedule");
        suggestButton.setEnabled(false);

        statusLabel = new JLabel("Ready");

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(updateWeatherButton, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);

        // Initialization: load tasks
        JPanel bottomButtons = new JPanel();
        bottomButtons.add(addButton);
        bottomButtons.add(deleteButton);
        bottomButtons.add(updateWeatherButton);
        bottomButtons.add(edButton);
        bottomButtons.add(suggestButton);
        add(bottomButtons, BorderLayout.SOUTH);

        loadTasks();

        // Wiring: select row → enable weather button
        taskTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            updateWeatherButton.setEnabled(selectedRow >= 0);
            deleteButton.setEnabled(selectedRow >= 0);
            edButton.setEnabled(selectedRow >= 0);
            suggestButton.setEnabled(selectedRow >= 0);

            updateWeatherButton.setEnabled(isSelectedTaskWeatherSensitive(selectedRow));
        });

        // “Update Weather” clicked
        updateWeatherButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow < 0)
                return;

            String taskId = (String) tableModel.getValueAt(selectedRow, 0);
            updateWeatherForTask(taskId);
        });

        // add task from user
        addButton.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField titleField = new JTextField();
            JTextField dateField = new JTextField("2026-05-10T12:00:00");
            JCheckBox weatherBox = new JCheckBox("Weather Sensitive?");

            JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
            panel.add(new JLabel("Task ID:"));
            panel.add(idField);

            panel.add(new JLabel("Title:"));
            panel.add(titleField);

            panel.add(new JLabel("Due Date (yyyy-MM-ddTHH:mm:ss):"));
            panel.add(dateField);

            panel.add(new JLabel("Weather Sensitive?"));
            panel.add(weatherBox);

            int result = JOptionPane.showConfirmDialog(this, panel, "Add New Task", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String id = idField.getText().trim();
                    String title = titleField.getText().trim();
                    String StrDate = dateField.getText().trim();
                    if (id.isEmpty() || title.isEmpty() || StrDate.isEmpty()) {
                        throw new InvalidTaskException("Task ID, Title, and Date cannot be empty!");
                    }

                    Task newTask = new Task(idField.getText(), titleField.getText(),
                            LocalDateTime.parse(dateField.getText()), weatherBox.isSelected());
                    taskManager.addTask(newTask);

                    loadTasks();
                    statusLabel.setText("Task Added Successfully!");

                } catch (InvalidTaskException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(this, "Error in Data Format, check the date.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //

        suggestButton.addActionListener(e -> {
            taskManager.fetchWeather("Norilsk")
                    .flatMap(forecast -> schedulePlanner.suggestSchedule(taskManager.getTasks(), forecast))
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnNext(recommendations -> SwingUtilities.invokeLater(() -> {
                        StringBuilder sb = new StringBuilder();
                        for (ScheduleRecommendation r : recommendations) {
                            sb.append("- Task: ").append(r.task().getTitle())
                                    .append("\n")
                                    .append("  Status: ").append(r.recommendation())
                                    .append("\n\n");
                        }
                        JOptionPane.showMessageDialog(
                                this,
                                sb.toString(),
                                "Schedule Suggestions",
                                JOptionPane.INFORMATION_MESSAGE);
                    }))
                    .doOnError(error -> SwingUtilities
                            .invokeLater(() -> statusLabel.setText("Suggest failed: " + error.getMessage())))
                    .subscribe();
        });

        edButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow < 0)
                return;

            String taskId = (String) tableModel.getValueAt(selectedRow, 0);

            JTextField titleField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
            JTextField dateField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
            JCheckBox weatherBox = new JCheckBox("Weather Sensitive?",
                    (boolean) tableModel.getValueAt(selectedRow, 3));

            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.add(new JLabel("Title:"));
            panel.add(titleField);
            panel.add(new JLabel("Due Date (yyyy-MM-ddTHH:mm:ss):"));
            panel.add(dateField);
            panel.add(new JLabel("Weather Sensitive?"));
            panel.add(weatherBox);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Task", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String title = titleField.getText().trim();
                    String strDate = dateField.getText().trim();
                    if (title.isEmpty() || strDate.isEmpty()) {
                        throw new InvalidTaskException("Title and Date cannot be empty!");
                    }
                    taskManager.removeTask(taskId);
                    Task updated = new Task(taskId, title,
                            LocalDateTime.parse(strDate), weatherBox.isSelected());
                    taskManager.addTask(updated);
                    loadTasks();
                    statusLabel.setText("Task Updated Successfully!");
                } catch (InvalidTaskException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error in Data Format, check the date.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // delete task from user

        deleteButton.addActionListener(e -> {
            int SelectedRow = taskTable.getSelectedRow();
            if (SelectedRow < 0) {
                JOptionPane pane = new JOptionPane(
                        "Select The Task to be Deleted",
                        JOptionPane.WARNING_MESSAGE);

                JDialog dialog = pane.createDialog(this, "Warning");

                new Timer(1000, evt -> dialog.dispose()).start();
                dialog.setVisible(true);
                return;
            }

            String taskId = (String) tableModel.getValueAt(SelectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to delete this task? " + taskId + " ?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                try {
                    taskManager.removeTask(taskId);
                    tableModel.removeRow(SelectedRow);
                    statusLabel.setText("Task Deleted Successfully!");
                } catch (TaskNotFoundException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting task: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
        });

    }

    // ... Keep your existing loadTasks, populateTable, and updateWeatherForTask
    // methods ...
    private void loadTasks() {
        Mono.just(taskManager.getTasks())
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(tasks -> SwingUtilities.invokeLater(() -> {
                    populateTable(tasks);
                    loadWeatherStatus(tasks);
                }))
                .subscribe();
    }

    private void loadWeatherStatus(List<Task> tasks) {
        tasks.stream()
                .filter(t -> t.isWeatherSensitive())
                .forEach(t -> updateWeatherForTask(t.getId()));
    }

    private void populateTable(List<Task> tasks) {
        tableModel.setRowCount(0);
        for (Task t : tasks) {
            tableModel.addRow(
                    new Object[] { t.getId(), t.getTitle(), t.getDueDateTime(), t.isWeatherSensitive(), "N/A" });
        }
    }

    private void updateWeatherForTask(String taskId) {
        Mono<WeatherForecast> forecastMono = taskManager.fetchWeather("Norilsk"); // fixed city

        forecastMono
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(forecast -> SwingUtilities.invokeLater(() -> {
                    // Simple weather‑aware status logic

                    String status = forecast.getPrecipitationProbability() > 0.6
                            ? "RISKY (rain)"
                            : "SAFE";

                    updateTaskStatusInTable(taskId, status);
                    statusLabel.setText("Weather updated for task: " + taskId);
                }))
                .doOnError(error -> SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Weather fetch failed: " + error.getMessage());
                }))
                .subscribe();
    }

    private void updateTaskStatusInTable(String taskId, String status) {
        int rowCount = tableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String idInTable = (String) tableModel.getValueAt(i, 0);
            if (idInTable.equals(taskId)) {
                tableModel.setValueAt(status, i, 4);
                break;
            }
        }
    }

    private boolean isSelectedTaskWeatherSensitive(int selectedRow) {
        if (selectedRow < 0)
            return false;
        return (boolean) tableModel.getValueAt(selectedRow, 3);
    }

}

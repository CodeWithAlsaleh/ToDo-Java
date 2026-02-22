package com.sivan.todo.ui;

import com.sivan.todo.model.Status;
import com.sivan.todo.model.Task;
import com.sivan.todo.service.TaskService;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final TaskService service;

    public ConsoleUI(TaskService service) {
        this.service = Objects.requireNonNull(service, "TaskService implementation cannot be null");
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> handleAddTask();
                    case "2" -> handleViewAllTasks();
                    case "3" -> handleUpdateTask();
                    case "4" -> handleDeleteTask();
                    case "5" -> handleSearchTask();
                    case "0" -> {
                        System.out.println("Exiting application. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                // Catches errors if the user types "abc" instead of an ID number
                System.err.println("Error: Please enter a valid number for the Id.");
            } catch (IllegalArgumentException e) {
                /*
                 *   This is where the magic happens!
                 *   We catch the exception thrown by the Service and
                 * */
                System.err.println("Error: " + e.getMessage());
            } catch (Exception e) {
                // Catch unexpected errors so the app doesn't crash completely
                System.err.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== To-Do Application ===");
        System.out.println("""
                1. Add Task
                2. View All Tasks
                3. Update Task
                4. Delete Task
                5. Search Task
                0. Exit""");

        System.out.print("Enter your choice: ");
    }

    private void handleAddTask() {
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();

        boolean success = service.addTask(description);
        if (success)
            System.out.println("Task added successfully!");
        else
            System.out.println("Failed to add task to the database.");
    }

    private void handleViewAllTasks() {
        List<Task> tasks = service.getAllTasks();

        if (tasks == null || tasks.isEmpty()) {
            System.out.println("Your To-Do list is empty.");
            return;
        }

        printResult("Your Tasks", tasks);
    }

    private void handleUpdateTask() {
        System.out.print("Enter the ID of the task to update: ");
        int id = Integer.parseInt(scanner.nextLine()); // Safely parses string to int

        // 1. Fetch the existing task first
        Task existingTask = service.getTask(id);
        if (existingTask == null)
            throw new IllegalArgumentException("No task found with ID: " + id);

        // 2. Ask for new description (allow them to keep the old one by just pressing Enter)
        System.out.print("Enter new description (or press Enter to keep '" + existingTask.description() + "'): ");

        String newDescription = scanner.nextLine();
        if (newDescription.isBlank())
            newDescription = existingTask.description();

        // 3. Ask for new status
        System.out.print("Enter new status (PENDING / COMPLETED) or press Enter to keep '" + existingTask.status() + "': ");
        String statusInput = scanner.nextLine().trim().toUpperCase();

        Status newStatus = existingTask.status();
        if (!statusInput.isBlank()) {
            try {
                newStatus = Status.valueOf(statusInput);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status. Must be PENDING or COMPLETED.");
            }
        }

        // 4. Send to service
        boolean success = service.updateTask(existingTask, newDescription, newStatus);
        if (success)
            System.out.println("Task updated successfully!");
        else
            System.out.println("Failed to update the task.");
    }

    private void handleDeleteTask() {
        System.out.print("Enter the ID of the task to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        boolean success = service.deleteTask(id);
        if (success)
            System.out.println("Task deleted successfully!");
        else
            System.out.println("Failed to delete task. Make sure the ID exists.");
    }

    private void handleSearchTask() {
        /*
            However, if you ever build an app for a company with 1 million tasks in the database
            pulling all of them into Java memory just to find one task would crash the server.

            In an enterprise environment, you would push this work to the database by adding a
            new method to your TaskDAO and TaskService that executes a SQL query like this:

            SELECT * FROM Task WHERE Description LIKE ? (and passing %keyword% to the PreparedStatement).
        */

        System.out.print("Enter a keyword to search in task descriptions: ");
        String keyword = scanner.nextLine().toLowerCase();

        List<Task> allTasks = service.getAllTasks();

        if (allTasks == null || allTasks.isEmpty()) {
            System.out.println("Your To-Do list is empty. Nothing to search.");
            return;
        }

        // Professional Java: Using Streams to filter the list
        List<Task> foundTasks = allTasks.stream()
                .filter(task -> task.description().toLowerCase().contains(keyword))
                .toList();

        if (foundTasks.isEmpty()) {
            System.out.println("No tasks found containing the word: '" + keyword + "'");
            return;
        }

        printResult("Search Results", foundTasks);
    }

    private void printResult(String header, List<Task> tasks) {
        System.out.printf("\n--- %s ---%n", header);
        System.out.printf("%-5s | %-12s | %-30s | %s%n", "ID", "Status", "Description", "Created At");
        System.out.println("-".repeat(75));

        for (Task task : tasks) {
            System.out.printf("%-5d | %-12s | %-30s | %s%n",
                    task.id(), task.status(), task.description(), task.createdAt());
        }
    }
}
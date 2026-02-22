package com.sivan.todo.service;

import com.sivan.todo.dao.TaskDAO;
import com.sivan.todo.model.Status;
import com.sivan.todo.model.Task;

import java.util.List;
import java.util.Objects;

public class TaskService {
    private final TaskDAO db;

    /*
     *   private static final TaskDAO db = TaskDAO.getDB("mysql");
     *
     *   This tightly couples your Service to the DAO factory. If you ever wanted
     *   to write a Unit Test for TaskService without hitting your actual MySQL
     *   database, you couldn't. Instead of the Service creating its own dependency
     *   we should inject it via the constructor.
     * */

    public TaskService(TaskDAO db) {
        // Imagine if the UI layer sends me null instead of TaskDAO obj ?
        this.db = Objects.requireNonNull(db, "TaskDAO implementation cannot be null");
    }

    public boolean addTask(String description) {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Description can't be null or blank.");

        return db.add(new Task(description));
    }

    public Task getTask(int id) {
        if (id <= 0)
            return null;

        return db.get(id);
    }

    public boolean updateTask(Task task, String description, Status status) {
        if (task == null)
            throw new IllegalArgumentException("Task can't be null.");

        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Description can't be null or blank.");

        return db.update(new Task(task.id(), description, status, task.createdAt()));
    }

    public boolean deleteTask(int id) {
        if (id <= 0)
            throw new IllegalArgumentException("Id can't be zero or negative number.");

        return db.delete(id);
    }

    public List<Task> getAllTasks() {
        return db.getAll();
    }
}

/*
    This code isn't good:

    Improvement 2: Return Types vs. UI Concerns

    Right now, your methods return hardcoded English String messages (e.g., "Task deleted successfully").

    The Problem: The Service layer shouldn't know how the UI wants to talk to the user. What if you
    translate the app to Arabic later ? What if the UI wants to print the message in red text ?

    The Fix: Have the Service return a boolean (or throw an IllegalArgumentException for bad input).
    Let the UI layer (Scanner logic) inspect the result and decide what System.out.println() to show.




    public String addTask(String description) {
        if (description == null || description.isBlank())
            return "Description can't be null or blank.";

        Task task = new Task(description);

        if (!db.add(task))
            return "Something went wrong when adding the task. Please try again!";

        return "Task added successfully.";
    }

    public String updateTask(Task task, String description, Status status) {
        if (task == null)
            return "Task can't be null.";

        if (description == null || description.isBlank())
            return "Description can't be null or blank.";

        if (!db.update(new Task(task.id(), description, status, task.createdAt())))
            return "Something went wrong when updating the task. Please try again!";

        return "Task updated successfully.";
    }

    public String deleteTask(int id) {
        if (id <= 0)
            return "Id can't be zero or negative number.";

        if (!db.delete(id))
            return "Something went wrong when deleting the task. Please try again!";

        return "Task deleted successfully";
    }
*/
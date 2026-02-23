package com.sivan.todo.dao;

import com.sivan.todo.model.Task;

import java.util.List;

public interface TaskDAO {
    boolean add(Task task);

    Task get(Integer id);

    boolean update(Task task);

    boolean delete(Integer id);

    List<Task> getAll();

    List<Task> search(String description);

    static TaskDAO getDB(String dbName) {
        return switch (dbName.toLowerCase()) {
            case "mysql" -> new MySQLTaskDAO();
            default -> throw new IllegalArgumentException(dbName + " DB not supported");
        };
    }
}

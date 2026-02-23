package com.sivan.todo.dao;

import com.sivan.todo.model.Status;
import com.sivan.todo.model.Task;
import com.sivan.todo.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// We only need transactions if we only execute multiple statements together
class MySQLTaskDAO implements TaskDAO {
    private static final String FETCH_ALL_TASKS = "SELECT * FROM Task";
    private static final String DELETE_TASK = "DELETE FROM Task WHERE Id=?";
    private static final String FETCH_TASK = "SELECT * FROM Task WHERE Id=?";
    private static final String INSERT_TASK = "INSERT INTO Task (Description) VALUES(?)";
    private static final String UPDATE_TASK = "UPDATE Task SET Description=?, Status=? WHERE Id=?";
    private static final String SEARCH_TASKS = "SELECT * FROM Task WHERE Description LIKE CONCAT('%', ?, '%')";

    @Override
    public boolean add(Task task) {
        try (
                Connection conn = DatabaseConnection.getConnection();
                /*
                 *  We don't have to put it here cuz once conn is closed
                 *  it will close any statement got created by it.
                 * */
                PreparedStatement stat = conn.prepareStatement(INSERT_TASK)
        ) {
            // Search about this "RETURN_GENERATED_KEYS"
            stat.setString(1, task.description());

            return stat.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add task", e);
        }
    }

    @Override
    public Task get(Integer id) {
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stat = conn.prepareStatement(FETCH_TASK)
        ) {
            stat.setInt(1, id);
            ResultSet res = stat.executeQuery();

            if (!res.next())
                return null;

            return mapRowToTask(res);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch task with Id: " + id, e);
        }
    }

    @Override
    public boolean update(Task task) {
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stat = conn.prepareStatement(UPDATE_TASK)
        ) {
            stat.setString(1, task.description());
            stat.setString(2, task.status().toString());
            stat.setInt(3, task.id());

            return stat.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update task", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stat = conn.prepareStatement(DELETE_TASK)
        ) {
            stat.setInt(1, id);

            return stat.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete task", e);
        }
    }

    @Override
    public List<Task> getAll() {
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stat = conn.prepareStatement(FETCH_ALL_TASKS)
        ) {
            List<Task> tasks = new ArrayList<>();
            ResultSet res = stat.executeQuery();

            while (res.next())
                tasks.add(mapRowToTask(res));

            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all tasks", e);
        }
    }

    @Override
    public List<Task> search(String description) {
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stat = conn.prepareStatement(SEARCH_TASKS)
        ) {
            List<Task> tasks = new ArrayList<>();
            stat.setString(1, description);

            ResultSet res = stat.executeQuery();

            while (res.next())
                tasks.add(mapRowToTask(res));

            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search for the specified description", e);
        }
    }

    private Task mapRowToTask(ResultSet res) throws SQLException {
        int taskId = res.getInt("Id");
        String taskDescription = res.getString("Description");
        Status taskStatus = Status.valueOf(res.getString("Status"));
        LocalDateTime taskDate = res.getTimestamp("Created_At").toLocalDateTime();

        return new Task(taskId, taskDescription, taskStatus, taskDate);
    }
}

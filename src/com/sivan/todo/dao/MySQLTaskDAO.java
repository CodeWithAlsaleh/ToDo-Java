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

class MySQLTaskDAO implements TaskDAO {
    @Override
    public boolean add(Task task) {
        try (
                Connection conn = DatabaseConnection.getConnection()
        ) {
            // Search about this "RETURN_GENERATED_KEYS"
            PreparedStatement stat = conn.prepareStatement("INSERT INTO Task (Description) VALUES(?)");
            stat.setString(1, task.description());

            return stat.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Task get(Integer id) {
        try (
                Connection conn = DatabaseConnection.getConnection()
        ) {
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM Task WHERE Id=?");
            stat.setInt(1, id);

            ResultSet res = stat.executeQuery();

            if (res.next())
                return mapRowToTask(res);
            else
                return null;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean update(Task task) {
        try (
                Connection conn = DatabaseConnection.getConnection()
        ) {
            PreparedStatement stat = conn.prepareStatement("UPDATE Task SET Description=?, Status=? WHERE Id=?");
            stat.setString(1, task.description());
            stat.setString(2, task.status().toString());
            stat.setInt(3, task.id());

            return stat.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (
                Connection conn = DatabaseConnection.getConnection()
        ) {
            PreparedStatement stat = conn.prepareStatement("DELETE FROM Task WHERE Id=?");
            stat.setInt(1, id);

            return stat.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<Task> getAll() {
        try (
                Connection conn = DatabaseConnection.getConnection()
        ) {
            List<Task> tasks = new ArrayList<>();
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM Task");

            ResultSet res = stat.executeQuery();

            while (res.next())
                tasks.add(mapRowToTask(res));

            return tasks;
        } catch (SQLException e) {
            return null;
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

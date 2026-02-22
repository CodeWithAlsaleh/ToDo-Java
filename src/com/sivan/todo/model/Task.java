package com.sivan.todo.model;

import java.time.LocalDateTime;

/*
 *   NOTE: Read about LocalDateTime and it's usage and Java DateTime in general
 * */
public record Task(Integer id, String description, Status status, LocalDateTime createdAt) {
    public Task(String description) {
        this(null, description, Status.PENDING, null);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}

package com.sivan.todo.util;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final DataSource dataSource;

    // Since the class is a utility class, nobody should create objects from it.
    private DatabaseConnection() {
    }

    // Configuration happens ONCE here (When the class is initialized by the JVM)
    static {
        Properties props = new Properties();

        try (
                InputStream input = DatabaseConnection.class.getClassLoader()
                        .getResourceAsStream("db.properties")
        ) {
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database config", e);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.username");
        String pass = props.getProperty("db.password");

        /*
         *   The IllegalStateException in Java is an unchecked runtime exception
         *   that signals a method has been invoked at an illegal or inappropriate
         *   time, given the current state of the application or object.
         * */
        if (url == null || user == null || pass == null)
            throw new IllegalStateException("Database environment variables are not set");

        /*
         *   Now if we want to change the DB we will just change this line
         *
         *   Like this:
         *
         *   PGSimpleDataSource ds = new PGSimpleDataSource();
         *   or
         *   SQLServerDataSource ds = new SQLServerDataSource();
         * */
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUrl(url);
        ds.setUser(user);
        ds.setPassword(pass);

        dataSource = ds;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

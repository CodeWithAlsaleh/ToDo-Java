package com.sivan.todo;

import com.sivan.todo.dao.TaskDAO;
import com.sivan.todo.service.TaskService;
import com.sivan.todo.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        /*
         *   The main method has only one job: Wire everything together and press Start.
         * */

        // 1. Instantiate the DAO
        TaskDAO dao = TaskDAO.getDB("mysql");

        // 2. Inject DAO into Service
        TaskService service = new TaskService(dao);

        // 3. Inject Service into UI
        ConsoleUI ui = new ConsoleUI(service);

        // 4. Start the application
        ui.start();
    }
}

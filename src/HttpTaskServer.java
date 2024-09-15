import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.EpicsHttpHandler;
import server.HistoryHttpHandler;
import server.PrioritizedHttpHandler;
import server.SubtasksHttpHandler;
import server.TasksHttpHandler;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    HttpServer httpServer;
    TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TasksHttpHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHttpHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHttpHandler(taskManager));
        httpServer.createContext("/history", new HistoryHttpHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHttpHandler(taskManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    public static void main(String[] args) throws IOException {

        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.start();
    }
}




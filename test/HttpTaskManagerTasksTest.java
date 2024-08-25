import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.BaseHttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskManagerTasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = BaseHttpHandler.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtask();
        manager.deleteAllEpic();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        Task task2 = new Task("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30));
        manager.createTask(task1);
        manager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Task> serverListTasks = gson.fromJson(response.body(), new ServerTasksTypeToken().getType());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertEquals(2, serverListTasks.size(), "Некорректное количество задач");
        assertEquals(manager.getAllTasks(), serverListTasks, "Возвращаются некорректные задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        Task task2 = new Task("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30));
        manager.createTask(task1);
        manager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromServer = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertEquals(manager.getTask(1), taskFromServer, "Возвращается некорректная задача");
    }


    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        String task = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неправельный код ответа");
        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Решить задачу", tasksFromManager.getFirst().getNameTask(),
                "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        manager.createTask(task1);
        Task task2 = new Task("Новое имя", "Выучить теорию", TaskStatus.IN_PROGRESS, 1,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        String task = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неправельный код ответа");
        List<Task> tasksFromManager = manager.getAllTasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Новое имя", tasksFromManager.getFirst().getNameTask(), "Имя не обновилось");
    }

    @Test
    public void testDeleteAllTask() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        Task task2 = new Task("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30));
        manager.createTask(task1);
        manager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertTrue(manager.getAllTasks().isEmpty(), "Задачи не удалились");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        Task task2 = new Task("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30));
        manager.createTask(task1);
        manager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertEquals(1, manager.getAllTasks().size(), "Некорректное количество задач");
        assertEquals(manager.getAllTasks().getFirst().getNameTask(), task2.getNameTask(),
                "Удалилась  задача с другим id");
    }

    @Test
    public void testGetTaskWithNoCorrectId() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        Task task2 = new Task("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30));
        manager.createTask(task1);
        manager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/gsgs");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testGetTaskWithNoExistingId() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        Task task2 = new Task("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30));
        manager.createTask(task1);
        manager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/6");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testAddTaskWithIntersections() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        Task task2 = new Task("Решить", "Выучить", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 15, 30));
        manager.createTask(task1);
        String task = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testDeleteTaskWithNoCorrectId() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        Task task2 = new Task("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30));
        manager.createTask(task1);
        manager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/fafa");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testDeleteTaskWithNoExistingId() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        Task task2 = new Task("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30));
        manager.createTask(task1);
        manager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/5");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неправельный код ответа");
    }
}

class ServerTasksTypeToken extends TypeToken<ArrayList<Task>> {
}


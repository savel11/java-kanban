import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
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

public class HttpTaskManagerSubtasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = BaseHttpHandler.getGson();

    public HttpTaskManagerSubtasksTest() throws IOException {
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
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        Subtask subtask2 = new Subtask("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30), epic);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Subtask> serverListSubtasks = gson.fromJson(response.body(), new ServerSubtasksTypeToken().getType());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertEquals(2, serverListSubtasks.size(), "Некорректное количество подзадач");
        assertEquals(manager.getAllSubtasks(), serverListSubtasks, "Возвращаются некорректные подзадачи");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        Subtask subtask2 = new Subtask("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30), epic);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskFromServer = gson.fromJson(response.body(), Subtask.class);
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertEquals(manager.getSubtask(2), subtaskFromServer, "Возвращается некорректная подзадача");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию",
                TaskStatus.IN_PROGRESS, Duration.ofHours(3),
                LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        String subtask = gson.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неправельный код ответа");
        List<Task> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Решить задачу", subtasksFromManager.getFirst().getNameTask(),
                "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        manager.createEpic(epic);
        Subtask subtask2 = new Subtask("Решить задачу", "Выучить теорию",
                TaskStatus.IN_PROGRESS, Duration.ofHours(3),
                LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        manager.createSubtask(subtask2);
        Subtask subtask1 = new Subtask("Новое имя", "Выучить теорию",
                TaskStatus.IN_PROGRESS, epic, 2, Duration.ofHours(3),
                LocalDateTime.of(2024, 8, 24, 14, 30));
        String subtask = gson.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неправельный код ответа");
        List<Task> subtasksFromManager = manager.getAllSubtasks();
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Новое имя", subtasksFromManager.getFirst().getNameTask(),
                "Имя подзадачи не обновилось");
    }

    @Test
    public void testDeleteAllSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        Subtask subtask2 = new Subtask("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30), epic);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Задачи не удалились");
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        Subtask subtask2 = new Subtask("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30), epic);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertEquals(1, manager.getAllSubtasks().size(), "Некорректное количество задач");
        assertEquals(manager.getAllSubtasks().getFirst().getNameTask(), subtask2.getNameTask(),
                "Удалилась  задача с другим id");
    }

    @Test
    public void testGetSubtaskWithNoCorrectId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        Subtask subtask2 = new Subtask("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30), epic);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/sfsfs");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testGetSubtaskWithNoExistingId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        Subtask subtask2 = new Subtask("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30), epic);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/5");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testAddSubtaskWithIntersections() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        Subtask subtask2 = new Subtask("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 15, 30), epic);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        String subtask = gson.toJson(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testDeleteSubtaskWithNoCorrectId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        Subtask subtask2 = new Subtask("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30), epic);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/ggf");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testDeleteSubtaskWithNoExistingId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        Subtask subtask2 = new Subtask("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30), epic);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/6");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неправельный код ответа");
    }
}

class ServerSubtasksTypeToken extends TypeToken<ArrayList<Subtask>> {
}

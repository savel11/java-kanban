import com.google.gson.Gson;
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
import typetokens.EpicsTypeToken;
import typetokens.SubtasksTypeToken;

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

public class HttpTaskManagerEpicsTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = BaseHttpHandler.getGson();

    public HttpTaskManagerEpicsTest() throws IOException {
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
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Testing1");
        Epic epic2 = new Epic("Epic2", "Testing2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Epic> serverListEpics = gson.fromJson(response.body(), new EpicsTypeToken().getType());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertEquals(2, serverListEpics.size(), "Некорректное количество эпиков");
        assertEquals(manager.getAllEpic(), serverListEpics, "Возвращаются некорректные эпики");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Testing1");
        Epic epic2 = new Epic("Epic2", "Testing2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromServer = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertEquals(manager.getEpic(1), epicFromServer, "Возвращается некорректный эпик");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic", "Testing");
        String epic = gson.toJson(epic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epic))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неправельный код ответа");
        List<Task> epicsFromManager = manager.getAllEpic();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic", epicsFromManager.getFirst().getNameTask(),
                "Некорректное имя эпика");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic2 = new Epic("Ep", "Testing1");
        manager.createEpic(epic2);
        epic2.setNameTask("Epic");
        String epic = gson.toJson(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epic))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неправельный код ответа");
        List<Task> epicsFromManager = manager.getAllEpic();
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic", epicsFromManager.getFirst().getNameTask(),
                "Эпик не обновился");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        Subtask subtask2 = new Subtask("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30), epic);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Subtask> serverListSubtasks = gson.fromJson(response.body(), new SubtasksTypeToken().getType());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertEquals(2, serverListSubtasks.size(), "Некорректное количество подзадач");
        assertEquals(manager.getEpic(1).getSubTasks(), serverListSubtasks, "Возвращаются некорректные подзадачи");
    }

    @Test
    public void testDeleteAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Testing1");
        Epic epic2 = new Epic("Epic2", "Testing2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertTrue(manager.getAllEpic().isEmpty(), "Эпики не удалились");
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Testing1");
        Epic epic2 = new Epic("Epic2", "Testing2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        assertEquals(1, manager.getAllEpic().size(), "Некорректное количество эпиков");
        assertEquals(manager.getAllEpic().getFirst(), epic2, "Удалился некорректный эпик");
    }

    @Test
    public void testGetEpicWithNoCorrectId() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Testing1");
        Epic epic2 = new Epic("Epic2", "Testing2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/g");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testGetEpicWithNotExistingtId() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Testing1");
        Epic epic2 = new Epic("Epic2", "Testing2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/5");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testGetEpicSubtasksWithNotExistingId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing");
        Subtask subtask1 = new Subtask("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30), epic);
        Subtask subtask2 = new Subtask("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30), epic);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/5/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testDeleteEpicWithNoCorrectId() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Testing1");
        Epic epic2 = new Epic("Epic2", "Testing2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/one");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Неправельный код ответа");
    }

    @Test
    public void testDeleteEpicWithNotExistingId() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Testing1");
        Epic epic2 = new Epic("Epic2", "Testing2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/8");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неправельный код ответа");
    }
}


import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.BaseHttpHandler;
import typetokens.TasksTypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerHistoryTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = BaseHttpHandler.getGson();

    public HttpTaskManagerHistoryTest() throws IOException {
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
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        Task task2 = new Task("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30));
        Epic epic = new Epic("Test", "testing");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic);
        manager.getTask(1);
        manager.getEpic(3);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        List<Task> serverHistory = gson.fromJson(response.body(), new TasksTypeToken().getType());
        List<Task> history = manager.getHistory();
        assertEquals(2, serverHistory.size(), "Неккоректный размери истории");
        assertEquals(serverHistory.getFirst().getNameTask(), history.getFirst().getNameTask(),
                "Некорректная история");
        assertEquals(serverHistory.get(1).getNameTask(), history.get(1).getNameTask(),
                "Некорректная история");
    }
}

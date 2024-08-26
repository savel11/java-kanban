import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.BaseHttpHandler;
import typetokens.PrioritizedTypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerPrioritizedTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = BaseHttpHandler.getGson();

    public HttpTaskManagerPrioritizedTest() throws IOException {
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
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Решить задачу", "Выучить теорию", TaskStatus.IN_PROGRESS,
                Duration.ofHours(3), LocalDateTime.of(2024, 8, 24, 14, 30));
        Task task2 = new Task("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 8, 24, 14, 30));
        Task task3 = new Task("Решить задачи", "Выучить теорию", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2025, 9, 24, 14, 30));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправельный код ответа");
        Set<Task> serverPrioritized = gson.fromJson(response.body(), new PrioritizedTypeToken().getType());
        assertEquals(3, serverPrioritized.size(), "Неккоректный размери списка");
        assertEquals(manager.getPrioritizedTasks(), serverPrioritized,
                "Некорректнай список");
    }
}



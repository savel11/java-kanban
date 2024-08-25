package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;


public class PrioritizedHttpHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        Endpoint endpoint = getEndpoint(h.getRequestURI().getPath(), h.getRequestMethod());
        if (endpoint.equals(Endpoint.GET_PRIORITIZED)) {
            handlePrioritizedHttpHandler(h);
        } else {
            sendNotFound(h, "Данного эндпоинта не существует");
        }
    }

    private void handlePrioritizedHttpHandler(HttpExchange h) throws IOException {
        if (!manager.getPrioritizedTasks().isEmpty()) {
            Gson gson = getGson();
            String prioritized = gson.toJson(manager.getPrioritizedTasks());
            sendText(h, prioritized);
        } else {
            sendText(h, "Список пуст");
        }
    }
}

package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;


public class HistoryHttpHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHttpHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        Endpoint endpoint = getEndpoint(h.getRequestURI().getPath(), h.getRequestMethod());
        if (endpoint.equals(Endpoint.GET_HISTORY)) {
            handleGetHistory(h);
        } else {
            sendNotFound(h, "Данного эндпоинта не существует");
        }
    }

    private void handleGetHistory(HttpExchange h) throws IOException {
        if (!manager.getHistory().isEmpty()) {
            Gson gson = getGson();
            String history = gson.toJson(manager.getHistory());
            sendText(h, history);
        } else {
            sendText(h, "История пуста");
        }
    }
}

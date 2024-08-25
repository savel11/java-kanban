package server;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class BaseHttpHandler {
    TaskManager manager;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendCreated(HttpExchange h, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(201, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(404, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        byte[] response = "Задача не может быть добавлена/обновленна, из-за пересечения".getBytes(
                StandardCharsets.UTF_8);
        h.sendResponseHeaders(406, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendBadRequest(HttpExchange h, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(400, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendInternetServerError(HttpExchange h) throws IOException {
        byte[] response = "Ошибка работы сервера".getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(500, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if ((pathParts[1].equals("tasks") || pathParts[1].equals("subtasks")
                || pathParts[1].equals("epics")) && pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_CREATE;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE;
            }
        }
        if ((pathParts[1].equals("tasks") || pathParts[1].equals("subtasks")
                || pathParts[1].equals("epics")) && pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASK_BY_ID;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_UPDATE;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_BY_ID;
            }
        }
        if (pathParts[1].equals("epics") && pathParts[3].equals("subtasks") && pathParts.length == 4) {
            return Endpoint.GET_EPIC_SUBTASKS;
        }
        if (pathParts[1].equals("history") && pathParts.length == 2 && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        }
        if (pathParts[1].equals("prioritized") && pathParts.length == 2 && requestMethod.equals("GET")) {
            return Endpoint.GET_PRIORITIZED;
        }
        return Endpoint.UNKNOWN;
    }

    protected Optional<Integer> getTaskId(HttpExchange h) {
        String[] pathParts = h.getRequestURI().getPath().split("/");
        try{
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getAnnotation(Expose.class) != null;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        });
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }
}

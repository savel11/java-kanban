package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.NotFoundException;
import manager.TaskManager;
import model.Task;


import java.io.IOException;
import java.nio.charset.StandardCharsets;


import java.util.Optional;

public class TasksHttpHandler extends BaseHttpHandler implements HttpHandler {
    public TasksHttpHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        Endpoint endpoint = getEndpoint(h.getRequestURI().getPath(), h.getRequestMethod());
        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(h);
                break;
            }
            case GET_TASK_BY_ID: {
                handleGetTasksById(h);
                break;
            }
            case POST_CREATE: {
                handlePostTask(h);
                break;
            }
            case POST_UPDATE: {
                handleTaskUpdate(h);
                break;
            }
            case DELETE: {
                handleDeleteTasks(h);
                break;
            }
            case DELETE_BY_ID: {
                handleDeleteTaskById(h);
                break;
            }
            default: {
                sendNotFound(h, "Данного эндпоинта не существует");
            }
        }
    }

    private void handleGetTasks(HttpExchange h) throws IOException {
        if (!manager.getAllTasks().isEmpty()) {
            Gson gson = getGson();
            String tasks = gson.toJson(manager.getAllTasks());
            sendText(h, tasks);
        } else {
            sendText(h, "Список задач пуст");
        }
    }

    private void handleGetTasksById(HttpExchange h) throws IOException {
        Optional<Integer> idOptional = getTaskId(h);
        if (idOptional.isEmpty()) {
            sendBadRequest(h, "Некорректный идентификатор задачи");
            return;
        }
        int id = idOptional.get();
        try {
            Gson gson = getGson();
            String task = gson.toJson(manager.getTask(id));
            sendText(h, task);
        } catch (NotFoundException e) {
            sendNotFound(h, "Задача с идентификатором " + id + " не найдена");
        }
    }

    private void handlePostTask(HttpExchange h) throws IOException {
        Optional<Task> taskOptional = getTaskFromRequest(h);
        if (taskOptional.isEmpty()) {
            sendBadRequest(h, "Поля задачи не могут быть пустыми");
        } else {
            Task task = taskOptional.get();
            if (task.getNameTask() == null || task.getStatus() == null) {
                sendBadRequest(h, "Поля задачи не могут быть пустыми");
            } else {
                try {
                    manager.createTask(task);
                    sendCreated(h, "Задача успешна добавлена");
                } catch (IllegalStateException e) {
                    sendHasInteractions(h);
                } catch (ManagerSaveException e) {
                    sendInternetServerError(h);
                }
            }
        }
    }

    private Optional<Task> getTaskFromRequest(HttpExchange h) throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (body.isEmpty()) {
            return Optional.empty();
        } else {
            Gson gson = getGson();
            return Optional.of(gson.fromJson(body, Task.class));
        }
    }

    private void handleTaskUpdate(HttpExchange h) throws IOException {
        Optional<Integer> idOptional = getTaskId(h);
        if (idOptional.isEmpty()) {
            sendBadRequest(h, "Некорректный идентификатор задачи");
            return;
        }
        int id = idOptional.get();
        Optional<Task> taskOptional = getTaskFromRequest(h);
        if (taskOptional.isEmpty()) {
            sendBadRequest(h, "Поля задачи не могут быть пустыми");
            return;
        }
        Task task = taskOptional.get();
        if (task.getNameTask() == null || task.getStatus() == null) {
            sendBadRequest(h, "Поля задачи не могут быть пустыми");
            return;
        }
        try {
            manager.updateTask(task);
            sendCreated(h, "Задача успешна обновленна");
        } catch (IllegalStateException e) {
            sendHasInteractions(h);
        } catch (NotFoundException e) {
            sendNotFound(h, "Задача с идентификатором " + id + " не найдена");
        } catch (ManagerSaveException e) {
            sendInternetServerError(h);
        }
    }

    private void handleDeleteTasks(HttpExchange h) throws IOException {
        try {
            manager.deleteAllTasks();
            sendText(h, "Список задач очищен");
        }  catch (ManagerSaveException e) {
            sendInternetServerError(h);
        }
    }

    private void handleDeleteTaskById(HttpExchange h) throws IOException {
        Optional<Integer> idOptional = getTaskId(h);
        if (idOptional.isEmpty()) {
            sendBadRequest(h, "Некорректный идентификатор задачи");
            return;
        }
        int id = idOptional.get();
        try {
            manager.deleteTaskById(id);
            sendText(h, "Задача с идентификатором " + id + " успешно удалена");
        } catch (NotFoundException e) {
            sendNotFound(h, "Задача с идентификатором " + id + " не найдена");
        }  catch (ManagerSaveException e) {
            sendInternetServerError(h);
        }
    }
}






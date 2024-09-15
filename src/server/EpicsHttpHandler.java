package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.NotFoundException;
import manager.TaskManager;
import model.Epic;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicsHttpHandler extends BaseHttpHandler implements HttpHandler {
    public EpicsHttpHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        Endpoint endpoint = getEndpoint(h.getRequestURI().getPath(), h.getRequestMethod());
        switch (endpoint) {
            case GET_TASKS: {
                handleGetEpics(h);
                break;
            }
            case GET_TASK_BY_ID: {
                handleGetEpicById(h);
                break;
            }
            case POST_CREATE: {
                handlePostEpic(h);
                break;
            }
            case POST_UPDATE: {
                handleEpicUpdate(h);
                break;
            }
            case DELETE: {
                handleDeleteEpics(h);
                break;
            }
            case DELETE_BY_ID: {
                handleDeleteEpicById(h);
                break;
            }
            case GET_EPIC_SUBTASKS: {
                handleGetEpicSubtasks(h);
            }
            default: {
                sendNotFound(h, "Данного эндпоинта не существует");
            }
        }
    }

    private void handleGetEpics(HttpExchange h) throws IOException {
        if (!manager.getAllEpic().isEmpty()) {
            Gson gson = getGson();
            String epics = gson.toJson(manager.getAllEpic());
            sendText(h, epics);
        } else {
            sendText(h, "Список эпиков пуст");
        }
    }

    private void handleGetEpicById(HttpExchange h) throws IOException {
        Optional<Integer> idOptional = getTaskId(h);
        if (idOptional.isEmpty()) {
            sendBadRequest(h, "Некорректный идентификатор эпика");
            return;
        }
        int id = idOptional.get();
        try {
            Gson gson = getGson();
            String epic = gson.toJson(manager.getEpic(id));
            sendText(h, epic);
        } catch (NotFoundException e) {
            sendNotFound(h, "Эпик с идентификатором " + id + " не найдена");
        }
    }

    private void handleGetEpicSubtasks(HttpExchange h) throws IOException {
        Optional<Integer> idOptional = getTaskId(h);
        if (idOptional.isEmpty()) {
            sendBadRequest(h, "Некорректный идентификатор эпика");
            return;
        }
        int id = idOptional.get();
        try {
            Gson gson = getGson();
            String subtasks = gson.toJson(manager.getSubtasksByEpic(manager.getEpic(id)));
            sendText(h, subtasks);
        } catch (NotFoundException e) {
            sendNotFound(h, "Эпик с идентификатором " + id + " не найдена");
        }
    }

    private void handlePostEpic(HttpExchange h) throws IOException {
        Optional<Epic> epicOptional = getEpicFromRequest(h);
        if (epicOptional.isEmpty()) {
            sendBadRequest(h, "Поля эпика не могут быть пустыми");
        } else {
            Epic epic = epicOptional.get();
            if (epic.getNameTask() == null) {
                sendBadRequest(h, "Поля эпика не могут быть пустыми");
            } else {
                try {
                    manager.createEpic(epic);
                    sendCreated(h, "Эпик успешно добавлен");
                } catch (ManagerSaveException e) {
                    sendInternetServerError(h);
                }
            }
        }
    }

    private Optional<Epic> getEpicFromRequest(HttpExchange h) throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (body.isEmpty()) {
            return Optional.empty();
        } else {
            Gson gson = getGson();
            return Optional.of(gson.fromJson(body, Epic.class));
        }
    }

    private void handleEpicUpdate(HttpExchange h) throws IOException {
        Optional<Integer> idOptional = getTaskId(h);
        if (idOptional.isEmpty()) {
            sendBadRequest(h, "Некорректный идентификатор эпика");
            return;
        }
        int id = idOptional.get();
        Optional<Epic> epicOptional = getEpicFromRequest(h);
        if (epicOptional.isEmpty()) {
            sendBadRequest(h, "Поля эпика не могут быть пустыми");
            return;
        }
        Epic epic = epicOptional.get();
        if (epic.getNameTask() == null) {
            sendBadRequest(h, "Поля эпика не могут быть пустыми");
            return;
        }
        try {
            manager.updateEpic(epic);
            sendCreated(h, "Эпик успешно обновлен");
        } catch (NotFoundException e) {
            sendNotFound(h, "Эпик с идентификатором " + id + " не найден");
        } catch (ManagerSaveException e) {
            sendInternetServerError(h);
        }
    }

    private void handleDeleteEpics(HttpExchange h) throws IOException {
        try {
            manager.deleteAllEpic();
            sendText(h, "Список эпиков очищен");
        } catch (ManagerSaveException e) {
            sendInternetServerError(h);
        }
    }

    private void handleDeleteEpicById(HttpExchange h) throws IOException {
        Optional<Integer> idOptional = getTaskId(h);
        if (idOptional.isEmpty()) {
            sendBadRequest(h, "Некорректный идентификатор эпика");
            return;
        }
        int id = idOptional.get();
        try {
            manager.deleteEpicById(id);
            sendText(h, "Эпик с идентификатором " + id + " успешно удален");
        } catch (NotFoundException e) {
            sendNotFound(h, "Эпик с идентификатором " + id + " не найден");
        } catch (ManagerSaveException e) {
            sendInternetServerError(h);
        }
    }
}

package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.NotFoundException;
import manager.TaskManager;
import model.Subtask;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtasksHttpHandler extends BaseHttpHandler implements HttpHandler {
    public SubtasksHttpHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        Endpoint endpoint = getEndpoint(h.getRequestURI().getPath(), h.getRequestMethod());
        switch (endpoint) {
            case GET_TASKS: {
                handleGetSubtasks(h);
                break;
            }
            case GET_TASK_BY_ID: {
                handleGetSubtaskById(h);
                break;
            }
            case POST_CREATE: {
                handlePostSubtask(h);
                break;
            }
            case POST_UPDATE: {
                handleSubtaskUpdate(h);
                break;
            }
            case DELETE: {
                handleDeleteSubtasks(h);
                break;
            }
            case DELETE_BY_ID: {
                handleDeleteSubtaskById(h);
                break;
            }
            default: {
                sendNotFound(h, "Данного эндпоинта не существует");
            }
        }
    }

    private void handleGetSubtasks(HttpExchange h) throws IOException {
        if (!manager.getAllSubtasks().isEmpty()) {
            Gson gson = getGson();
            String subtasks = gson.toJson(manager.getAllSubtasks());
            sendText(h, subtasks);
        } else {
            sendText(h, "Список подзадач пуст");
        }
    }

    private void handleGetSubtaskById(HttpExchange h) throws IOException {
        Optional<Integer> idOptional = getTaskId(h);
        if (idOptional.isEmpty()) {
            sendBadRequest(h, "Некорректный идентификатор подзадачи");
            return;
        }
        int id = idOptional.get();
        try {
            Gson gson = getGson();
            String subtask = gson.toJson(manager.getSubtask(id));
            sendText(h, subtask);
        } catch (NotFoundException e) {
            sendNotFound(h, "Подзадача с идентификатором " + id + " не найдена");
        }
    }

    private void handlePostSubtask(HttpExchange h) throws IOException {
        Optional<Subtask> taskOptional = getSubtaskFromRequest(h);
        if (taskOptional.isEmpty()) {
            sendBadRequest(h, "Поля подзадачи не могут быть пустыми");
        } else {
            Subtask task = taskOptional.get();
            if (task.getNameTask() == null || task.getStatus() == null || task.getEpic() == null) {
                sendBadRequest(h, "Поля подзадачи не могут быть пустыми");
            } else {
                try {
                    manager.createSubtask(task);
                    sendCreated(h, "Подзадача успешна добавлена");
                } catch (IllegalStateException e) {
                    sendHasInteractions(h);
                }  catch (ManagerSaveException e) {
                    sendInternetServerError(h);
                }
            }
        }
    }

    private Optional<Subtask> getSubtaskFromRequest(HttpExchange h) throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (body.isEmpty()) {
            return Optional.empty();
        } else {
            Gson gson = getGson();
            return Optional.of(gson.fromJson(body, Subtask.class));
        }
    }

    private void handleSubtaskUpdate(HttpExchange h) throws IOException {
        Optional<Integer> idOptional = getTaskId(h);
        if (idOptional.isEmpty()) {
            sendBadRequest(h, "Некорректный идентификатор подзадачи");
            return;
        }
        int id = idOptional.get();
        Optional<Subtask> taskOptional = getSubtaskFromRequest(h);
        if (taskOptional.isEmpty()) {
            sendBadRequest(h, "Поля подзадачи не могут быть пустыми");
            return;
        }
        Subtask task = taskOptional.get();
        if (task.getNameTask() == null || task.getStatus() == null || task.getEpic() == null) {
            sendBadRequest(h, "Поля подзадачи не могут быть пустыми");
            return;
        }
        try {
            manager.updateSubtask(task);
            sendCreated(h, "Подзадача успешна обновленна");
        } catch (IllegalStateException e) {
            sendHasInteractions(h);
        } catch (NotFoundException e) {
            sendNotFound(h, "Подзадача с идентификатором " + id + " не найдена");
        }  catch (ManagerSaveException e) {
            sendInternetServerError(h);
        }
    }

    private void handleDeleteSubtasks(HttpExchange h) throws IOException {
        try {
            manager.deleteAllSubtask();
            sendText(h, "Список подзадач очищен");
        } catch (ManagerSaveException e) {
                sendInternetServerError(h);
            }
    }

    private void handleDeleteSubtaskById(HttpExchange h) throws IOException {
        Optional<Integer> idOptional = getTaskId(h);
        if (idOptional.isEmpty()) {
            sendBadRequest(h, "Некорректный идентификатор подзадачи");
            return;
        }
        int id = idOptional.get();
        try {
            manager.deleteSubtaskById(id);
            sendText(h, "Подзадача с идентификатором " + id + " успешно удалена");
        } catch (NotFoundException e) {
            sendNotFound(h, "Подзадача с идентификатором " + id + " не найдена");
        }  catch (ManagerSaveException e) {
            sendInternetServerError(h);
        }
    }
}

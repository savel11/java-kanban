package manager;

import model.Epic;
import model.Task;
import model.Subtask;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, Subtask> subtasks;

    Managers managers = new Managers();
    HistoryManager inMemoryHistoryManager = managers.getDefaultHistory();

    private int id = 0;


    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }


    private int generatedId() {
        return ++id;
    }

    @Override
    public Task getTask(int id) {
        inMemoryHistoryManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        inMemoryHistoryManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        inMemoryHistoryManager.add(subtasks.get(id));

        return subtasks.get(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generatedId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generatedId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpic().getId());
        epic.addTask(subtask, epic);
        epic.calculateStatus(epic);
        subtask.setId(generatedId());
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    @Override
    public void updateTask(Task task) {

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = subtask.getEpic();
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.changeSubTasks(epic.getSubTasks(), subtask);
        savedEpic.calculateStatus(savedEpic);
        epics.put(savedEpic.getId(), savedEpic);
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        saved.setNameTask(epic.getNameTask());
        saved.setDescriptionTask(epic.getDescriptionTask());
        epics.put(saved.getId(), saved);
    }

    @Override
    public List<Task> getAllTasks() {

        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTaskForId(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskForId(int id) {
        Subtask removeSubtask = getSubtask(id);
        Epic epic = removeSubtask.getEpic();
        Epic epicSaved = epics.get(epic.getId());
        epicSaved.removeSubtask(epicSaved, removeSubtask);
        subtasks.remove(id);
        epicSaved.calculateStatus(epicSaved);
        epics.put(epicSaved.getId(), epicSaved);

    }

    @Override
    public void deleteEpicForId(int id) {
        Epic epic = getEpic(id);
        List<Subtask> subTasks = epic.getSubTasks();
        for (Subtask element : subTasks) {
            subtasks.remove(element.getId());
        }
        epics.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtasks : subtasks.values()) {
            Epic epic = subtasks.getEpic();
            Epic epicSaved = epics.get(epic.getId());
            epicSaved.removeSubtask(epicSaved, subtasks);
            epicSaved.calculateStatus(epicSaved);
            epics.put(epicSaved.getId(), epicSaved);
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public List<Task> getSubtasksForEpic(Epic epic) {
        List<Subtask> subTasks = epic.getSubTasks();
        List<Task> taskList = new ArrayList<>();
        for (Subtask subtask : subTasks) {
            taskList.add(subtask);
        }
        return taskList;
    }

}

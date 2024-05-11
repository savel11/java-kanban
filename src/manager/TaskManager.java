package manager;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> mapSubtasks;
    private int seq = 0;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.mapSubtasks = new HashMap<>();
    }
    private int generatedId() {
        return ++seq;
    }
    public Task getTask(int id) {
        return tasks.get(id);
    }
    public Epic getEpic(int id) {
        return epics.get(id);
    }
    public Subtask getSubtask(int id) {
        return mapSubtasks.get(id);
    }

    public Task createTask(Task task) {
        task.setId(generatedId());
        tasks.put(task.getId(), task);
        return task;
    }
    public Epic createEpic(Epic epic) {
        epic.setId(generatedId());
        epics.put(epic.getId(),epic);
        return epic;
    }
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpic().getId());
        epic.addTask(subtask, epic);
        epic.calculateStatus(epic);
        subtask.setId(generatedId());
        mapSubtasks.put(subtask.getId(), subtask);
        return subtask;
    }
    public void updateTask(Task task) {

        tasks.put(task.getId(), task);
    }
    public void updateSubtask(Subtask subtask) {
        Epic epic = subtask.getEpic();
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.changeSubTasks(epic.getSubTasks(),subtask );
        savedEpic.calculateStatus(savedEpic);
        epics.put(savedEpic.getId() , savedEpic);
        mapSubtasks.put(subtask.getId(), subtask);



    }
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        saved.setNameTask(epic.getNameTask());
        saved.setDescriptionTask(epic.getDescriptionTask());
        epics.put(saved.getId(), saved);

    }
    public List<Task> getAllTasks() {

        return new ArrayList<>(tasks.values());
    }
    public List<Task> getAllEpic() {
        return new ArrayList<>(epics.values());
    }
    public List<Task> getAllSubtasks() {
        return new ArrayList<>(mapSubtasks.values());
    }
    public void deleteTaskForId(int id) {
        tasks.remove(id);
    }
    public void deleteSubtaskForId(int id) {
        Subtask removeSubtask = getSubtask(id);
        Epic epic = removeSubtask.getEpic();
        Epic epicSaved = epics.get(epic.getId());
        epicSaved.removeSubtask(epicSaved, removeSubtask );
        mapSubtasks.remove(id);
        epicSaved.calculateStatus(epicSaved);
        epics.put(epicSaved.getId(), epicSaved );

    }
    public void deleteEpicForId(int id) { // При удаление эпика, нужно удалить все задачи, которые ему принадлежали
        Epic epic = getEpic(id);
        List<Subtask> subTasks = epic.getSubTasks();
        for (Subtask subtasks:  subTasks) {
           mapSubtasks.remove(subtasks.getId());

        }
        epics.remove(id);


    }
    public void deleteAllTasks() {
        tasks.clear();
    }
    public void deleteAllSubtask() {
        for(Subtask subtasks: mapSubtasks.values()) {
            Epic epic = subtasks.getEpic();
            Epic epicSaved = epics.get(epic.getId());
            epicSaved.removeSubtask(epicSaved,subtasks);
            epicSaved.calculateStatus(epicSaved);
            epics.put(epicSaved.getId(), epicSaved );
        }
        mapSubtasks.clear();

    }
    public void deleteAllEpic() {
        epics.clear();
        mapSubtasks.clear();

    }

    public List<Task> getSubtasksForEpic(Epic epic) {
        List<Subtask> subTasks = epic.getSubTasks();
        List<Task> taskList = new ArrayList<>();
        for(Subtask subtask: subTasks) {
            taskList.add(subtask);
        }
        return taskList;
    }


}

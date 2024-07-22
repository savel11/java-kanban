package manager;

import model.Epic;
import model.Task;
import model.Subtask;

import java.util.List;


public interface TaskManager {


    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    List<Task> getAllTasks();

    List<Task> getAllEpic();

    List<Task> getAllSubtasks();

    void deleteTaskForId(int id);

    void deleteSubtaskForId(int id);

    void deleteEpicForId(int id);

    void deleteAllTasks();

    void deleteAllSubtask();

    void deleteAllEpic();

    List<Task> getSubtasksForEpic(Epic epic);


}

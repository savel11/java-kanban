
import manager.Managers;
import manager.TaskManager;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import model.Task;
import model.Subtask;
import model.Epic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryTaskManagerTest {
    TaskManager taskManager = Managers.getDefault();
    @Test
    void shouldBeChangedId() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 50);
        taskManager.createTask(task);
        Task task1 = taskManager.getTask(task.getId());
        assertNotEquals(50, task1.getId());
    }

    @Test
    void shouldBeEqualsAllArgumentsTaskAfterAddInManager() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE);
        taskManager.createTask(task);
        Task task1 = taskManager.getTask(task.getId());
        assertEquals("Уборка", task1.getNameTask());
        assertEquals("Помыть посуду", task1.getDescriptionTask());
    }

    @Test
    void addOtherTaskAndSearch() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE);
        taskManager.createTask(task);
        final List<Task> tasks = taskManager.getAllTasks();
        Epic epic = new Epic("Переезд", "Собрать все вещи");
        taskManager.createEpic(epic);
        final List<Task> epics = taskManager.getAllEpic();
        Subtask subtask = new Subtask("Отдых", "Ничего не делать", TaskStatus.NEW, epic);
        taskManager.createSubtask(subtask);
        final List<Task> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "История не пустая.");
        assertNotNull(epics, "История не пустая.");
        assertNotNull(tasks, "История не пустая.");
        assertEquals(task, taskManager.getTask(task.getId()));
        assertEquals(epic, taskManager.getEpic(epic.getId()));
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }

    @Test
    void shouldNotBeDeletetedSubtaskIdInEpic() {
        Epic epic = new Epic("Переезд", "Собрать все вещи");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Собрать вещи", "Разложить вещи по коробкам", TaskStatus.NEW, epic);
        taskManager.createSubtask(subtask);
        Subtask subtask1 = new Subtask("Убрать квартиру", "Убрать", TaskStatus.NEW, epic);
        taskManager.createSubtask(subtask1);
        taskManager.deleteSubtaskById(subtask1.getId());
        taskManager.updateEpic(epic);
        List<Subtask> subtaskList = epic.getSubTasks();
        for (Subtask sub : subtaskList) {
            assertNotEquals(sub.getId(), subtask1.getId(), "Эпик содержит не актуальный id подзачи.");
        }
    }


}
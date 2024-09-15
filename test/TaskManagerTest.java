import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    public TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    @Test
    void shouldBeCreatedGetAndDeleteTasks() {
        Task task1 = new Task("Test1", "t1", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2017, 5, 5, 10, 0));
        taskManager.createTask(task1);
        Task task2 = new Task("Test2", "t2", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2016, 5, 5, 10, 0));
        assertEquals(task2, taskManager.createTask(task2), "Задача не создана");
        Epic epic1 = new Epic("Epic1", "epic1");
        Epic epic2 = new Epic("Epic2", "epic2");
        taskManager.createEpic(epic1);
        assertEquals(epic2, taskManager.createEpic(epic2), "Эпик не создан");
        Subtask subtask1 = new Subtask("Subtask1", "subtask1", TaskStatus.NEW, epic1,
                -1, Duration.ofSeconds(600), LocalDateTime.of(2016, 6, 5, 10, 0));
        Subtask subtask2 = new Subtask("Subtask2", "subtask2", TaskStatus.NEW, epic1,
                -2, Duration.ofSeconds(600), LocalDateTime.of(2016, 7, 5, 10, 0));
        Subtask subtask3 = new Subtask("Subtask3", "subtask3", TaskStatus.NEW, epic2,
                -2, Duration.ofSeconds(600), LocalDateTime.of(2011, 7, 5, 10, 0));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask3);
        assertEquals(subtask2, taskManager.createSubtask(subtask2), "Подзача не создана");
        assertEquals(task1, taskManager.getTask(task1.getId()), "getTask работает неверно");
        assertEquals(subtask1, taskManager.getSubtask(subtask1.getId()), "getSubtask работает неверно");
        assertEquals(epic1, taskManager.getEpic(epic1.getId()), "getEpic работает неверно");
        assertEquals(2, taskManager.getAllTasks().size(), "Не все задачи полученны");
        assertEquals(2, taskManager.getAllEpic().size(), "Не все эпики полученны");
        assertEquals(3, taskManager.getAllSubtasks().size(), "Не все подзадачи полученны");
        assertEquals(2, taskManager.getSubtasksByEpic(taskManager.getEpic(epic1.getId())).size(),
                "Не все подзадачи эпика получены");
        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteSubtaskById(subtask2.getId());
        taskManager.deleteEpicById(epic2.getId());
        assertFalse(taskManager.getAllTasks().contains(task1), "Задача не была удалена");
        assertFalse(taskManager.getAllSubtasks().contains(subtask2), "Подзадача не была удалена");
        assertFalse(taskManager.getAllSubtasks().contains(subtask3), "Подзадача не была удалена" +
                " при удалении эпика");
        assertFalse(taskManager.getAllEpic().contains(epic2), "Эпик не был удален");
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtask();
        taskManager.deleteAllEpic();
        assertEquals(0, taskManager.getAllTasks().size(), "Не все задачи удалены");
        assertEquals(0, taskManager.getAllEpic().size(), "Не все эпики удалены");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Не все подзадачи удалены");
    }

    @Test
    void shouldUpdateTasks() {
        Task task1 = new Task("Test1", "t1", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2017, 5, 5, 10, 0));
        taskManager.createTask(task1);
        task1.setNameTask("NewName");
        taskManager.updateTask(task1);
        assertEquals("NewName", taskManager.getTask(task1.getId()).getNameTask(),
                "Задача не обновленна");
        Epic epic1 = new Epic("Epic1", "epic1");
        taskManager.createEpic(epic1);
        epic1.setNameTask("NewName");
        taskManager.updateEpic(epic1);
        assertEquals("NewName", taskManager.getEpic(epic1.getId()).getNameTask(), "Эпик не обновлен");
        Subtask subtask1 = new Subtask("Subtask1", "subtask1", TaskStatus.NEW, epic1,
                -1, Duration.ofSeconds(600), LocalDateTime.of(2016, 6, 5, 10, 0));
        taskManager.createSubtask(subtask1);
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        assertEquals(TaskStatus.DONE, taskManager.getSubtask(subtask1.getId()).getStatus(),
                "Подзадача не обновленна");
        assertEquals(TaskStatus.DONE, taskManager.getEpic(epic1.getId()).getStatus(),
                "После обновления подзадачи не обновлен эпик");
    }

    @Test
    void shouldGetHistory() {
        Task task1 = new Task("Test1", "t1", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2017, 5, 5, 10, 0));
        taskManager.createTask(task1);
        Task task2 = new Task("Test2", "t2", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2016, 5, 5, 10, 0));
        taskManager.createTask(task2);
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        List<Task> history = List.of(task1, task2);
        assertEquals(history, taskManager.getHistory(), "Получена неверная история");
    }

    @Test
    void shouldGetPrioritizedTask() {
        Task task1 = new Task("Test1", "t1", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2017, 5, 5, 10, 0));
        taskManager.createTask(task1);
        Task task2 = new Task("Test2", "t2", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2016, 5, 5, 10, 0));
        taskManager.createTask(task2);
        Set<Task> history = Set.of(task2, task1);
        assertEquals(history, taskManager.getPrioritizedTasks(), "Получена неверный приоритетный список");
    }

    @Test
    void shouldCalculatedStatus() {
        Epic epic1 = new Epic("Epic1", "epic1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "subtask1", TaskStatus.NEW, epic1,
                -1, Duration.ofSeconds(600), LocalDateTime.of(2016, 6, 5, 10, 0));
        Subtask subtask2 = new Subtask("Subtask2", "subtask2", TaskStatus.NEW, epic1,
                -2, Duration.ofSeconds(600), LocalDateTime.of(2016, 7, 5, 10, 0));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        assertEquals(TaskStatus.NEW, taskManager.getEpic(epic1.getId()).getStatus(),
                "Неправельно рассчитан статус");
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epic1.getId()).getStatus(),
                "Неправельно рассчитан статус");
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        assertEquals(TaskStatus.DONE, taskManager.getEpic(epic1.getId()).getStatus(),
                "Неправельно рассчитан статус");
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epic1.getId()).getStatus(),
                "Неправельно рассчитан статус");
    }

    @Test
    void shouldCalculatedOverlap() {
        Task task1 = new Task("Test1", "t1", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2017, 5, 5, 10, 0));
        Task task2 = new Task("Test2", "t2", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2016, 5, 5, 10, 0));
        Task task3 = new Task("Test2", "t2", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2016, 5, 5, 10, 5));
        assertTrue(taskManager.isTasksOverlapInTime(task2, task3), "Время выполнения  задач пересекается");
        assertFalse(taskManager.isTasksOverlapInTime(task1, task2), "Время выполенения задач не пересекается");
    }
}

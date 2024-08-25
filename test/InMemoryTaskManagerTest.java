
import manager.Managers;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import model.Task;
import model.Subtask;
import model.Epic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryTaskManagerTest extends TaskManagerTest {

    public InMemoryTaskManagerTest() {
        super(Managers.getDefault());
    }

    @Test
    void shouldBeCreatedGetAndDeleteTasks() {
        super.shouldBeCreatedGetAndDeleteTasks();
    }

    @Test
    void shouldCalculatedStatus() {
        super.shouldCalculatedStatus();
    }

    @Test
    void shouldCalculatedOverlap() {
        super.shouldCalculatedOverlap();
    }

    @Test
    void shouldUpdatedTasks() {
        super.shouldUpdateTasks();
    }

    @Test
    void shouldGetHistory() {
        super.shouldGetHistory();
    }

    @Test
    void shouldGetPrioritizedTask() {
        super.shouldGetPrioritizedTask();
    }

    @Test
    void shouldBeChangedId() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 50);
        taskManager.createTask(task);
        Task task1 = taskManager.getTask(task.getId());
        assertNotEquals(50, task1.getId());
    }

    @Test
    void shouldBeEqualsAllArgumentsTaskAfterAddInManager() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, Duration.ofSeconds(60),
                LocalDateTime.of(2024, 8, 7, 0, 8));
        taskManager.createTask(task);
        Task task1 = taskManager.getTask(task.getId());
        assertEquals("Уборка", task1.getNameTask());
        assertEquals("Помыть посуду", task1.getDescriptionTask());
        assertEquals(TaskStatus.DONE, task1.getStatus());
        assertEquals(Duration.ofSeconds(60), task1.getDuration());
        assertEquals(LocalDateTime.of(2024, 8, 7, 0, 8), task1.getStartTime());
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
    void shouldNotBeDeletedSubtaskIdInEpic() {
        Epic epic = new Epic("Переезд", "Собрать все вещи");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Собрать вещи", "Разложить вещи по коробкам", TaskStatus.NEW, epic);
        taskManager.createSubtask(subtask);
        Subtask subtask1 = new Subtask("Убрать квартиру", "Убрать", TaskStatus.NEW, epic);
        taskManager.createSubtask(subtask1);
        taskManager.deleteSubtaskById(subtask1.getId());
        taskManager.updateEpic(epic);
        List<Subtask> subtaskList = taskManager.getEpic(epic.getId()).getSubTasks();
        for (Subtask sub : subtaskList) {
            assertNotEquals(sub.getId(), subtask1.getId(), "Эпик содержит не актуальный id подзачи.");
        }
    }
}
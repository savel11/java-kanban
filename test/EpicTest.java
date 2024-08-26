import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import model.Epic;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


class EpicTest {
    @Test
    void shouldBeEqualsEpicWithSameId() {
        Epic epic = new Epic("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        Task epic1 = new Epic("Готовка", "Приготвоить обед", TaskStatus.NEW, 5);
        assertEquals(epic, epic1);
    }

    @Test
    void calculatedEpicStatusWithAllSubtasksWithStatusNew() {
        Epic epic = new Epic("Уборка", "Помыть посуду");
        Subtask subtask1 = new Subtask("Subtask1", "subtask1", TaskStatus.NEW, epic);
        Subtask subtask2 = new Subtask("Subtask2", "subtask2", TaskStatus.NEW, epic);
        epic.addTask(subtask1, epic);
        epic.addTask(subtask2, epic);
        epic.calculateStatus(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус не верно рассчитан");
    }

    @Test
    void calculatedEpicStatusWithAllSubtasksWithStatusDone() {
        Epic epic = new Epic("Уборка", "Помыть посуду");
        Subtask subtask1 = new Subtask("Subtask1", "subtask1", TaskStatus.DONE, epic);
        Subtask subtask2 = new Subtask("Subtask2", "subtask2", TaskStatus.DONE, epic);
        epic.addTask(subtask1, epic);
        epic.addTask(subtask2, epic);
        epic.calculateStatus(epic);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус не верно рассчитан");
    }

    @Test
    void calculatedEpicStatusWithSubtasksWithStatusDoneAndNew() {
        Epic epic = new Epic("Уборка", "Помыть посуду");
        Subtask subtask1 = new Subtask("Subtask1", "subtask1", TaskStatus.DONE, epic);
        Subtask subtask2 = new Subtask("Subtask2", "subtask2", TaskStatus.NEW, epic);
        epic.addTask(subtask1, epic);
        epic.addTask(subtask2, epic);
        epic.calculateStatus(epic);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус не верно рассчитан");
    }

    @Test
    void calculatedEpicStatusWithSubtasksWithStatusInProgress() {
        Epic epic = new Epic("Уборка", "Помыть посуду");
        Subtask subtask1 = new Subtask("Subtask1", "subtask1", TaskStatus.DONE, epic);
        Subtask subtask2 = new Subtask("Subtask2", "subtask2", TaskStatus.NEW, epic);
        Subtask subtask3 = new Subtask("Subtask3", "subtask3", TaskStatus.IN_PROGRESS, epic);
        epic.addTask(subtask1, epic);
        epic.addTask(subtask2, epic);
        epic.addTask(subtask3, epic);
        epic.calculateStatus(epic);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус не верно рассчитан");
    }

    @Test
    void calculationStartAndEndTime() {
        Epic epic = new Epic("Уборка", "Помыть посуду");
        Subtask subtask1 = new Subtask("Subtask1", "subtask1", TaskStatus.DONE, Duration.ofHours(1),
                LocalDateTime.of(2024,8,5,12,30), epic);
        Subtask subtask2 = new Subtask("Subtask2", "subtask2", TaskStatus.DONE, Duration.ofHours(1),
                LocalDateTime.of(2024,8,3,12,30), epic);
        Subtask subtask3 = new Subtask("Subtask3", "subtask3", TaskStatus.DONE, Duration.ofHours(1),
                LocalDateTime.of(2024,8,6,12,30), epic);
        epic.addTask(subtask1, epic);
        epic.addTask(subtask2, epic);
        epic.addTask(subtask3, epic);
        epic.calculateDurationStartAndEndTime(epic);
        assertEquals(subtask2.getStartTime(), epic.getStartTime(), "Неправельное время начала эпика");
        assertEquals(subtask3.getEndTime(), epic.getEndTime(), "Неправельное время завершения эпика");
    }


}
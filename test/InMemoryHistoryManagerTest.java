import manager.HistoryManager;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import manager.Managers;
import model.Task;

import java.util.List;

class InMemoryHistoryManagerTest {
    Managers managers = new Managers();

    HistoryManager historyManager = managers.getDefaultHistory();

    @Test
    void add() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void shouldNotEqualsTaskInHistoryAfterChange() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        historyManager.add(task);
        task.setNameTask("Убрать кухню");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotEquals(history.get(0).getNameTask(), history.get(1).getNameTask(), "Не сохранялись предыдущие данные");
    }


}
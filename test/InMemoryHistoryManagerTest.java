import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import model.Task;

import java.util.List;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void add() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "Размер истории неверен.");
    }

    @Test
    void shouldNotEqualsTaskInHistoryAfterChange() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        historyManager.add(task);
        task.setNameTask("Убрать кухню");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotEquals(history.get(0).getNameTask(), "Уборка", "Не сохранялись предыдущие данные");
    }

    @Test
    void shouldNotBeDuplicatesInHistory() {

        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        historyManager.add(task);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории есть дубликат.");
    }

    @Test
    void shouldDuplicatesInHistoryBeRemovedOldValueAndAddToEndOfTheHistory() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        Task task1 = new Task("Тренировка", "Пробежать 5 км", TaskStatus.NEW, 3);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Дубликат не удалился.");
        assertEquals("Уборка", history.get(history.size() - 1).getNameTask(), "Дубликат не добавлен в конец списка.");
    }
}
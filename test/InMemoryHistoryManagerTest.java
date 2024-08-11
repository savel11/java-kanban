import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import model.Task;

import java.util.List;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void add() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history, "История пустая.");
        Assertions.assertEquals(1, history.size(), "Размер истории неверен.");
        historyManager.add(task);
        Assertions.assertEquals(1, history.size(), "В историю записался дубликат.");
    }


    @Test
    void shouldNotEqualsTaskInHistoryAfterChange() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        historyManager.add(task);
        task.setNameTask("Убрать кухню");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        Assertions.assertNotEquals(history.get(0).getNameTask(), "Уборка", "Не сохранялись предыдущие данные");
    }

    @Test
    void shouldNotBeDuplicatesInHistory() {

        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        historyManager.add(task);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size(), "В истории есть дубликат.");
    }

    @Test
    void shouldDuplicatesInHistoryBeRemovedOldValueAndAddToEndOfTheHistory() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        Task task1 = new Task("Тренировка", "Пробежать 5 км", TaskStatus.NEW, 3);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size(), "Дубликат не удалился.");
        Assertions.assertEquals("Уборка", history.get(history.size() - 1).getNameTask(), "Дубликат не добавлен в конец списка.");
    }

    @Test
    void remove() {
        Assertions.assertDoesNotThrow(() -> {
            historyManager.remove(2);
        }, "Удаление из пустой истории не должно вызывать исключения");
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        Task task1 = new Task("Тренировка", "Пробежать 5 км", TaskStatus.NEW, 3);
        Task task3 = new Task("Task3", "task3", TaskStatus.DONE, 6);
        Task task4 = new Task("Task4", "task4", TaskStatus.NEW, 7);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.remove(task.getId());
        Assertions.assertTrue(historyManager.getHistory().stream().map(t -> t.getId()).noneMatch(id -> id == task.getId()),
                "Задача не удалилась из начала списка");
        historyManager.remove(task3.getId());
        Assertions.assertTrue(historyManager.getHistory().stream().map(t -> t.getId()).noneMatch(id -> id == task3.getId()),
                "Задача не удалилась из середины списка");
        historyManager.remove(task4.getId());
        Assertions.assertTrue(historyManager.getHistory().stream().map(t -> t.getId()).noneMatch(id -> id == task4.getId()),
                "Задача не удалилась из конца списка");
    }

    @Test
    void getHistory() {
        Assertions.assertEquals(0, historyManager.getHistory().size(), "История не пустая");
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        Task task1 = new Task("Тренировка", "Пробежать 5 км", TaskStatus.NEW, 3);
        historyManager.add(task);
        historyManager.add(task1);
        Assertions.assertEquals(List.of(task, task1), historyManager.getHistory(), "Неверная история");
    }
}
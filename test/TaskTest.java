import org.junit.jupiter.api.Test;
import model.Task;
import model.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TaskTest {
    @Test
    void shouldBeEqualsTaskWithSameId() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        Task task1 = new Task("Готовка", "Приготвоить обед", TaskStatus.NEW, 5);
        assertEquals(task, task1);
    }
}
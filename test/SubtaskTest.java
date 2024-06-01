import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SubtaskTest {
    @Test
    void shouldBeEqualsSubTaskWithSameId() {
        Subtask subtask = new Subtask("Уборка", "Помыть посуду", TaskStatus.DONE, null, 5);
        Subtask subtask1 = new Subtask("Готовка", "Приготвоить обед", TaskStatus.NEW, null, 5);
        assertEquals(subtask, subtask1);
    }

}
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import model.Epic;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    void shouldBeEqualsEpicWithSameId() {
        Epic epic = new Epic("Уборка", "Помыть посуду", TaskStatus.DONE, 5);
        Task epic1 = new Epic("Готовка", "Приготвоить обед", TaskStatus.NEW, 5);
        assertEquals(epic, epic1);
    }


}
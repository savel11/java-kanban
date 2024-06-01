import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    void shoudBeNotNull() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        HistoryManager historyManager = managers.getDefaultHistory();
        assertNotNull(historyManager);
        assertNotNull(taskManager);
    }

}
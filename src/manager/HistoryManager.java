package manager;
import model.Epic;
import model.Task;
import model.Subtask;

import java.util.ArrayList;
import java.util.List;


public interface HistoryManager {
    final int maxIndexInHistiry = 10;
      List<Task> history = new ArrayList<>();
    void add(Task task);
    List<Task> getHistory();


}

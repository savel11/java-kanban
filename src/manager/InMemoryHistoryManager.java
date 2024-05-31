package manager;
import model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    @Override
    public void add(Task task){
        if (history.size() < maxIndexInHistiry ) {
            Task  taskHistory = new Task(task.getNameTask(), task.getDescriptionTask(),
                    task.getStatus(),task.getId());
            history.add(taskHistory);
        } else{
            history.remove(0);
            Task  taskHistory = new Task(task.getNameTask(), task.getDescriptionTask(),
                    task.getStatus(), task.getId());
            history.add(taskHistory);
        }

    }
    @Override
    public List<Task> getHistory(){
        return history;
    }
}



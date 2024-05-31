import manager.Managers;
import manager.TaskManager;
import manager.InMemoryTaskManager;
import model.*;
import manager.InMemoryHistoryManager;
import manager.HistoryManager;
public class Main {

    public static void main(String[] args) {
        Managers managers = new Managers();
        TaskManager inMemoryTaskManager = managers.getDefault();
       HistoryManager inMemoryHistoryManager = managers.getDefaultHistory();
        Task task = new Task("run", "running", TaskStatus.NEW );
        inMemoryTaskManager.createTask(task);


       Task task2 = new Task("Chek", "Chek work", TaskStatus.NEW );
        inMemoryTaskManager.createTask(task2);


        Epic epic = new Epic("Move","Move");
        Subtask subtask = new Subtask("F","f",TaskStatus.NEW, epic);
        Subtask subtask2 = new Subtask("ff","fff",TaskStatus.IN_PROGRESS, epic);
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createSubtask(subtask2);

        Epic epic1 = new Epic("Study","studying");
        inMemoryTaskManager.createEpic(epic1);
        Subtask subtask3 = new Subtask("read","reading",TaskStatus.DONE, epic1);

        inMemoryTaskManager.createSubtask(subtask3);

        System.out.println("Список задач:" + inMemoryTaskManager.getAllTasks());
        System.out.println("Список эпиков:" + inMemoryTaskManager.getAllEpic());
        System.out.println("Список подзадач:" + inMemoryTaskManager.getAllSubtasks());
        System.out.println("");

     //   task.setStatus(TaskStatus.DONE);
       // task2.setStatus(TaskStatus.IN_PROGRESS);
      //  subtask3.setStatus(TaskStatus.IN_PROGRESS);
      //  subtask2.setStatus(TaskStatus.DONE);
      //  subtask.setStatus(TaskStatus.DONE);
     //   inMemoryTaskManager.updateSubtask(subtask3);
     //   inMemoryTaskManager.updateSubtask(subtask);
      //  inMemoryTaskManager.updateSubtask(subtask2);

       // System.out.println("Список задач после обновления:" + inMemoryTaskManager.getAllTasks());
      //  System.out.println("Список эпиков после обновления:" + inMemoryTaskManager.getAllEpic());
      //  System.out.println("Список подзадач после обновления:" + inMemoryTaskManager.getAllSubtasks());
      //  System.out.println("");

      //  inMemoryTaskManager.deleteEpicForId(epic.getId());
      //  inMemoryTaskManager.deleteTaskForId(task.getId());



       // System.out.println("Список задач после удаления:" + inMemoryTaskManager.getAllTasks());
       // System.out.println("Список эпиков после удаления:" + inMemoryTaskManager.getAllEpic());
       // System.out.println("Список подзадач после удаления:" + inMemoryTaskManager.getAllSubtasks());
       // System.out.println("");

        System.out.println("Список задач:" + inMemoryTaskManager.getTask(task.getId()));
        System.out.println("Список задач:" + inMemoryTaskManager.getEpic(epic.getId()));
        System.out.println("Список задач:" + inMemoryTaskManager.getTask(task.getId()));
        System.out.println("Список задач:" + inMemoryTaskManager.getSubtask(subtask.getId()));
        System.out.println(inMemoryHistoryManager.getHistory());

        task.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
         subtask.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask);
         inMemoryTaskManager.updateSubtask(subtask2);

        System.out.println("Список задач:" + inMemoryTaskManager.getTask(task.getId()));
        System.out.println("Список задач:" + inMemoryTaskManager.getEpic(epic.getId()));
        System.out.println("Список задач:" + inMemoryTaskManager.getSubtask(subtask.getId()));













    }
}

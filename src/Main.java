import manager.TaskManager;
import model.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task = new Task("run", "running", TaskStatus.NEW );
        taskManager.createTask(task);


        Task task2 = new Task("Chek", "Chek work", TaskStatus.NEW );
        taskManager.createTask(task2);


        Epic epic = new Epic("Move","Move");
        Subtask subtask = new Subtask("F","f",TaskStatus.NEW, epic);
        Subtask subtask2 = new Subtask("ff","fff",TaskStatus.IN_PROGRESS, epic);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);

        Epic epic1 = new Epic("Study","studying");
        taskManager.createEpic(epic1);
        Subtask subtask3 = new Subtask("read","reading",TaskStatus.DONE, epic1);
        
        taskManager.createSubtask(subtask3);

        System.out.println("Список задач:" + taskManager.getAllTasks());
        System.out.println("Список эпиков:" + taskManager.getAllEpic());
        System.out.println("Список подзадач:" + taskManager.getAllSubtasks());
        System.out.println("");

        task.setStatus(TaskStatus.DONE);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        subtask3.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.DONE);
        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask3);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask2);

        System.out.println("Список задач после обновления:" + taskManager.getAllTasks());
        System.out.println("Список эпиков после обновления:" + taskManager.getAllEpic());
        System.out.println("Список подзадач после обновления:" + taskManager.getAllSubtasks());
        System.out.println("");

        taskManager.deleteEpicForId(epic.getId());
        taskManager.deleteTaskForId(task.getId());



        System.out.println("Список задач после удаления:" + taskManager.getAllTasks());
        System.out.println("Список эпиков после удаления:" + taskManager.getAllEpic());
        System.out.println("Список подзадач после удаления:" + taskManager.getAllSubtasks());
        System.out.println("");












    }
}

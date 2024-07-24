import manager.*;
import model.Epic;
import model.Task;
import model.TaskStatus;
import model.Subtask;

import java.io.File;


import static manager.FileBackedTaskManager.loadFromFile;


public class Main {

    public static void main(String[] args) {

        TaskManager fileBackedTaskManagers1 = loadFromFile(new File("savedTasks.txt"));

        TaskManager inMemoryTaskManager = Managers.getDefault();
        TaskManager fileBackedTaskManagers = new FileBackedTaskManager(new File("savedTasks.txt"));


        Task task = new Task("run", "running", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);
        Task task2 = new Task("Chek", "Chek work", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task2);


        Epic epic = new Epic("Move", "Move");
        Subtask subtask = new Subtask("F", "f", TaskStatus.NEW, epic);
        Subtask subtask2 = new Subtask("ff", "fff", TaskStatus.IN_PROGRESS, epic);
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createSubtask(subtask2);

        Epic epic1 = new Epic("Study", "studying");
        inMemoryTaskManager.createEpic(epic1);
        Subtask subtask3 = new Subtask("read", "reading", TaskStatus.DONE, epic1);

        inMemoryTaskManager.createSubtask(subtask3);

        System.out.println("Список задач:" + inMemoryTaskManager.getAllTasks());
        System.out.println("Список эпиков:" + inMemoryTaskManager.getAllEpic());
        System.out.println("Список подзадач:" + inMemoryTaskManager.getAllSubtasks());
        System.out.println("");


        System.out.println("Список задач:" + inMemoryTaskManager.getTask(task.getId()));
        System.out.println("Список задач:" + inMemoryTaskManager.getTask(task.getId()));
        System.out.println("Список задач:" + inMemoryTaskManager.getEpic(epic.getId()));
        System.out.println("Список задач:" + inMemoryTaskManager.getTask(task.getId()));
        System.out.println("Список задач:" + inMemoryTaskManager.getSubtask(subtask.getId()));
        System.out.println("История " + inMemoryTaskManager.getHistory());
        System.out.println("Размер " + inMemoryTaskManager.getHistory().size());

        task.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask);
        inMemoryTaskManager.updateSubtask(subtask2);

        System.out.println("Список задач:" + inMemoryTaskManager.getTask(task.getId()));
        System.out.println("Список задач:" + inMemoryTaskManager.getEpic(epic.getId()));
        System.out.println("Список задач:" + inMemoryTaskManager.getSubtask(subtask.getId()));


        System.out.println(inMemoryTaskManager.getHistory());

        System.out.println("Размер " + inMemoryTaskManager.getHistory().size());
        task.setNameTask("NoRun");
        Task task12 = new Task("Studydf", "Studyingdf", TaskStatus.NEW);
       // fileBackedTaskManagers1.createTask(task12);
        System.out.println(fileBackedTaskManagers1.getAllTasks());
        System.out.println(fileBackedTaskManagers1.getAllSubtasks());
      //  System.out.println(inMemoryTaskManager.getSubtasksByEpic(epic));


     fileBackedTaskManagers.createTask(task);
        fileBackedTaskManagers.createTask(task2);
        Epic epic4 = new Epic("Move", "Move");
        fileBackedTaskManagers.createEpic(epic4);
        Subtask subtask4 = new Subtask("read", "reading", TaskStatus.DONE, epic4);
        fileBackedTaskManagers.createSubtask(subtask4);
        subtask4.setStatus(TaskStatus.NEW);
        fileBackedTaskManagers.updateSubtask(subtask4);
    }
}

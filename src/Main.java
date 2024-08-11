import manager.*;
import model.Epic;
import model.Task;
import model.TaskStatus;
import model.Subtask;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;



import static manager.FileBackedTaskManager.loadFromFile;


public class Main {

    public static void main(String[] args) {

       TaskManager fileBackedTaskManagers1 = loadFromFile(new File("savedTasks.txt"));
        TaskManager inMemoryTaskManager= Managers.getDefault();
        TaskManager fileBackedTaskManagers = new FileBackedTaskManager(new File("savedTasks.txt"));
        Task task1 = new Task("Test1", "t", TaskStatus.NEW);
        fileBackedTaskManagers.createTask(task1);
        Task task2 = new Task("Test2", "t", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2016, 5, 5, 10,0));
        fileBackedTaskManagers.createTask(task2);
        Task task3 = new Task("Test3", "t", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2016, 5, 5, 10,5));
        Task task4 = new Task("Test4", "t", TaskStatus.IN_PROGRESS, Duration.ofSeconds(600),
                LocalDateTime.of(2017, 5, 5, 10,5));
        fileBackedTaskManagers.createTask(task3);
        fileBackedTaskManagers.createTask(task4);
        task3.setStartTime(LocalDateTime.of(2017, 5, 5, 10,5));
     System.out.println(fileBackedTaskManagers.getTask(task1.getId()));
        System.out.println(fileBackedTaskManagers.getHistory());
        Epic epic1 = new Epic("Epic1", "epic1");
        Subtask subtask =  new Subtask("Subtask1", "subtask1", TaskStatus.NEW, epic1,
                -1, Duration.ofSeconds(600), LocalDateTime.of(2016, 6, 5, 10,0));
        fileBackedTaskManagers.createEpic(epic1);
        fileBackedTaskManagers.createSubtask(subtask);
        System.out.println(subtask.getId());
    }
}

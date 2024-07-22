import manager.FileBackedTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    @Test
    void shouldBeSaveVoidFileAndDowloandVoidFile() {
        try {
            File file = File.createTempFile("fileForTest", ".txt", new File("C:\\Users\\Савелий\\first-project\\java-kanban"));
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
            assertEquals(fileBackedTaskManager.getFileWithSavedTasks(), file, "Файл не сохранен");
            assertTrue(fileBackedTaskManager.getFileWithSavedTasks().length() == 0, "Файл не пустой");
            assertEquals(fileBackedTaskManager1.getFileWithSavedTasks(), file, "Файл не загружен");
            assertTrue(fileBackedTaskManager1.getFileWithSavedTasks().length() == 0, "Файл не пустой");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shoulToSaveTasksAndDowlondTasks() {
        try {
            File file1 = File.createTempFile("fileForTest", ".txt", new File("C:\\Users\\Савелий\\first-project\\java-kanban"));
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file1);
            Task task = new Task("run", "running", TaskStatus.NEW);
            fileBackedTaskManager.createTask(task);
            Epic epic = new Epic("Move", "Move");
            Subtask subtask = new Subtask("House", "Buy new house", TaskStatus.NEW, epic);
            fileBackedTaskManager.createEpic(epic);
            fileBackedTaskManager.createSubtask(subtask);
            BufferedReader fileReader = new BufferedReader(new FileReader(file1.getName()));
            String line = fileReader.readLine();
            while (fileReader.ready()) {
                line = fileReader.readLine();
                if (fileBackedTaskManager.getNameClass(line).equals("Task")) {
                    assertEquals(task, fileBackedTaskManager.fromStringTask(line), "Задача не сохранилась в файл");
                } else if (fileBackedTaskManager.getNameClass(line).equals("Subtask")) {
                    assertEquals(subtask, fileBackedTaskManager.fromStringSubtask(line), "Задача не сохранилась в файл");
                } else {
                    assertEquals(epic, fileBackedTaskManager.fromStringEpic(line), "Задача не сохранилась в файл");
                }

            }

            fileReader.close();
            FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file1);
            assertEquals(task, fileBackedTaskManager1.getTask(1), "Задачи не загрузились");
            assertEquals(epic, fileBackedTaskManager1.getEpic(2), "Задачи не загрузились");
            assertEquals(subtask, fileBackedTaskManager1.getSubtask(3), "Задачи не загрузились");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private File fileWithSavedTasks;
    private static final String COLUMN_DESIGNATIONS = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File fileWithSavedTasks) {
        this.fileWithSavedTasks = fileWithSavedTasks;
    }

    public File getFileWithSavedTasks() {
        return fileWithSavedTasks;
    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(fileWithSavedTasks.getPath())) {
            fileWriter.write(COLUMN_DESIGNATIONS);
            for (Map.Entry<Integer, Task> entry : getTasks().entrySet()) {
                fileWriter.write("\n" + toString(entry.getValue()));
            }

            for (Map.Entry<Integer, Epic> entry : getEpics().entrySet()) {
                fileWriter.write("\n" + toString(entry.getValue()));
            }
            for (Map.Entry<Integer, Subtask> entry : getSubtasks().entrySet()) {
                fileWriter.write("\n" + toString(entry.getValue()));
            }

        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public String toString(Task task) {
        String stringTask;
        if (task.getClass().getSimpleName().equals("Task")) {
            stringTask = String.format("%d,%s,%s,%s,%s", task.getId(), TypeClass.TASK, task.getNameTask(),
                    task.getStatus(), task.getDescriptionTask());
        } else if (task.getClass().getSimpleName().equals("Subtask")) {
            stringTask = String.format("%d,%s,%s,%s,%s,%d", task.getId(), TypeClass.SUBTASK, task.getNameTask(),
                    task.getStatus(), task.getDescriptionTask(), task.getEpic().getId());
        } else {
            stringTask = String.format("%d,%s,%s,%s,%s", task.getId(), TypeClass.EPIC, task.getNameTask(),
                    task.getStatus(), task.getDescriptionTask());
        }
        return stringTask;
    }

    public Task fromStringTask(String value) {
        String[] infoAboutTask = value.split(",");
        return new Task(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                Integer.parseInt(infoAboutTask[0]));
    }

    public Subtask fromStringSubtask(String value) {
        String[] infoAboutTask = value.split(",");
        return new Subtask(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                getEpicWithoutAddInHistory(Integer.parseInt(infoAboutTask[5])), Integer.parseInt(infoAboutTask[0]));
    }

    public Epic fromStringEpic(String value) {
        String[] infoAboutTask = value.split(",");
        return new Epic(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                Integer.parseInt(infoAboutTask[0]));
    }

    public String getNameClass(String str) {
        String nameClass;
        String[] infoAboutTask = str.split(",");
        nameClass = switch (infoAboutTask[1]) {
            case "TASK" -> "Task";
            case "SUBTASK" -> "Subtask";
            default -> "Epic";
        };
        return nameClass;
    }

    public TaskStatus getTaskStatus(String status) {
        TaskStatus taskStatus;
        taskStatus = switch (status) {
            case "NEW" -> TaskStatus.NEW;
            case "DONE" -> TaskStatus.DONE;
            default -> TaskStatus.IN_PROGRESS;
        };
        return taskStatus;
    }

    public Epic getEpicWithoutAddInHistory(int id) {
        return getEpics().get(id);
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int id = 0;
      //  try (BufferedReader fileReader = new BufferedReader(new FileReader(file.getName()))) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file.getPath()))) {
            String line = fileReader.readLine();
            while (fileReader.ready()) {
                line = fileReader.readLine();
                if (fileBackedTaskManager.fromStringTask(line).getId() > id) {
                    id = fileBackedTaskManager.fromStringTask(line).getId();
                }
                if (fileBackedTaskManager.getNameClass(line).equals("Task")) {
                    fileBackedTaskManager.getTasks().put(fileBackedTaskManager.fromStringTask(line).getId(),
                            fileBackedTaskManager.fromStringTask(line));
                } else if (fileBackedTaskManager.getNameClass(line).equals("Subtask")) {
                    fileBackedTaskManager.getSubtasks().put(fileBackedTaskManager.fromStringSubtask(line).getId(),
                            fileBackedTaskManager.fromStringSubtask(line));
                } else {
                    fileBackedTaskManager.getEpics().put(fileBackedTaskManager.fromStringEpic(line).getId(),
                            fileBackedTaskManager.fromStringEpic(line));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        fileBackedTaskManager.setIdCounter(id);
        return fileBackedTaskManager;
    }
}


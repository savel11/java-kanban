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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private File fileWithSavedTasks;
    private static final String COLUMN_DESIGNATIONS = "id,type,name,status,description,duration,startTime,endTime,epic";
    private static final String MISSING_VALUE = "notFound";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("HH.mm.dd.MM.yy");

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
            stringTask = String.format("%d,%s,%s,%s,%s,%s,%s,%s", task.getId(), TypeClass.TASK, task.getNameTask(),
                    task.getStatus(), task.getDescriptionTask(), toStringDuration(task.getDuration()),
                    toStringLocalDateTime(task.getStartTime()), MISSING_VALUE);
            if (task.getStartTime() != null) {
                stringTask = stringTask.replace(MISSING_VALUE, toStringLocalDateTime(task.getEndTime()));
            }
        } else if (task.getClass().getSimpleName().equals("Subtask")) {
            stringTask = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%d", task.getId(), TypeClass.SUBTASK, task.getNameTask(),
                    task.getStatus(), task.getDescriptionTask(), toStringDuration(task.getDuration()),
                    toStringLocalDateTime(task.getStartTime()), MISSING_VALUE, task.getEpic().getId());

        } else {
            stringTask = String.format("%d,%s,%s,%s,%s,%s,%s,%s", task.getId(), TypeClass.EPIC, task.getNameTask(),
                    task.getStatus(), task.getDescriptionTask(), toStringDuration(task.getDuration()),
                    toStringLocalDateTime(task.getStartTime()), MISSING_VALUE);
        }
        if (task.getStartTime() != null) {
            stringTask = stringTask.replace(MISSING_VALUE, toStringLocalDateTime(task.getEndTime()));
        }
        return stringTask;
    }

    public String toStringDuration(Duration duration) {
        Optional<Duration> optionalDuration = Optional.ofNullable(duration);
        return optionalDuration.map(value -> Long.toString(value.getSeconds())).orElse(MISSING_VALUE);
    }

    public String toStringLocalDateTime(LocalDateTime time) {
        Optional<LocalDateTime> optionalTime = Optional.ofNullable(time);
        return optionalTime.map(localDateTime -> localDateTime.format(DATE_FORMATTER)).orElse(MISSING_VALUE);
    }


    public Task fromStringTask(String value) {
        String[] infoAboutTask = value.split(",");
        if (infoAboutTask[5].equals(MISSING_VALUE) || infoAboutTask[6].equals(MISSING_VALUE)) {
            return new Task(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                    Integer.parseInt(infoAboutTask[0]));
        } else {
            return new Task(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                    Integer.parseInt(infoAboutTask[0]), Duration.ofSeconds(Long.parseLong(infoAboutTask[5])),
                    LocalDateTime.parse(infoAboutTask[6], DATE_FORMATTER));
        }
    }

    public Subtask fromStringSubtask(String value) {
        String[] infoAboutTask = value.split(",");
        if (infoAboutTask[5].equals(MISSING_VALUE) || infoAboutTask[6].equals(MISSING_VALUE)) {
            return new Subtask(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                    getEpicWithoutAddInHistory(Integer.parseInt(infoAboutTask[8])), Integer.parseInt(infoAboutTask[0]));
        } else {
            return new Subtask(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                    getEpicWithoutAddInHistory(Integer.parseInt(infoAboutTask[8])), Integer.parseInt(infoAboutTask[0]),
                    Duration.ofSeconds(Long.parseLong(infoAboutTask[5])), LocalDateTime.parse(infoAboutTask[6], DATE_FORMATTER));
        }
    }

    public Epic fromStringEpic(String value) {
        String[] infoAboutTask = value.split(",");
        if (infoAboutTask[5].equals(MISSING_VALUE) || infoAboutTask[6].equals(MISSING_VALUE)) {
            return new Epic(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                    Integer.parseInt(infoAboutTask[0]));
        } else {
            return new Epic(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                    Integer.parseInt(infoAboutTask[0]), Duration.ofSeconds(Long.parseLong(infoAboutTask[5])),
                    LocalDateTime.parse(infoAboutTask[6], DATE_FORMATTER), LocalDateTime.parse(infoAboutTask[7], DATE_FORMATTER));
        }
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

    public void addTaskInPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            getPrioritizedTasks().add(task);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int id = 0;
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
                    fileBackedTaskManager.addTaskInPrioritizedTasks(fileBackedTaskManager.fromStringTask(line));
                } else if (fileBackedTaskManager.getNameClass(line).equals("Subtask")) {
                    fileBackedTaskManager.getSubtasks().put(fileBackedTaskManager.fromStringSubtask(line).getId(),
                            fileBackedTaskManager.fromStringSubtask(line));
                    fileBackedTaskManager.addTaskInPrioritizedTasks(fileBackedTaskManager.fromStringTask(line));
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


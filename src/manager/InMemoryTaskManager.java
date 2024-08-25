package manager;

import model.Epic;
import model.Task;
import model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, Subtask> subtasks;
    private HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private int id = 0;
    private Set<Task> prioritizedTasks = new TreeSet<>((Task task1, Task task2) -> {
        if (task1.getStartTime().equals(task2.getStartTime())) {
            return 0;
        } else if (task1.getStartTime().isAfter(task2.getStartTime())) {
            return 1;
        } else {
            return -1;
        }
    });

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    public void setIdCounter(int count) {
        id = count;
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public int getIdCounter() {
        return id;
    }

    private int generatedId() {
        return ++id;
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            inMemoryHistoryManager.add(tasks.get(id));
            Task task = tasks.get(id);
            return new Task(task.getNameTask(), task.getDescriptionTask(), task.getStatus(),
                    task.getId(), task.getDuration(), task.getStartTime());
        } else {
             throw new NotFoundException();
        }
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            inMemoryHistoryManager.add(epics.get(id));
            Epic epic = epics.get(id);
            Epic epic1 = new Epic(epic.getNameTask(), epic.getDescriptionTask(), epic.getStatus(), epic.getId(),
                    epic.getDuration(), epic.getStartTime(), epic.getEndTime());
            epic1.getSubTasks().addAll(epic.getSubTasks());
            return epic1;
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            inMemoryHistoryManager.add(subtasks.get(id));
            Subtask subtask = subtasks.get(id);
            return new Subtask(subtask.getNameTask(), subtask.getDescriptionTask(), subtask.getStatus(), subtask.getEpic(),
                    subtask.getId(), subtask.getDuration(), subtask.getStartTime());
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public Task createTask(Task task) throws IllegalStateException {
        Task newTask;
            if (getPrioritizedTasks().stream().noneMatch(element -> isTasksOverlapInTime(task, element))) {
                task.setId(generatedId());
                if (task.getStartTime() != null) {
                    newTask = new Task(task.getNameTask(), task.getDescriptionTask(), task.getStatus(),
                            task.getId(), task.getDuration(), task.getStartTime());
                    prioritizedTasks.add(newTask);
                } else {
                    newTask = new Task(task.getNameTask(), task.getDescriptionTask(), task.getStatus(), task.getId());

                }
                tasks.put(newTask.getId(), newTask);
            } else {
                throw new IllegalStateException();
            }
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generatedId());
        Epic newEpic = new Epic(epic.getNameTask(), epic.getDescriptionTask(), epic.getStatus(), epic.getId(),
                epic.getDuration(), epic.getStartTime(), epic.getEndTime());
        epics.put(newEpic.getId(), newEpic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws IllegalStateException {
        Subtask newSubtask;

            if (getPrioritizedTasks().stream().noneMatch(element -> isTasksOverlapInTime(subtask, element))) {
                Epic epic = epics.get(subtask.getEpic().getId());
                subtask.setId(generatedId());
                if ((subtask.getStartTime() != null)) {
                    newSubtask = new Subtask(subtask.getNameTask(), subtask.getDescriptionTask(), subtask.getStatus(),
                            subtask.getEpic(), subtask.getId(), subtask.getDuration(), subtask.getStartTime());
                    prioritizedTasks.add(subtask);
                    epic.addTask(newSubtask, epic);
                    epic.calculateDurationStartAndEndTime(epic);
                } else {
                    newSubtask = new Subtask(subtask.getNameTask(), subtask.getDescriptionTask(), subtask.getStatus(),
                            subtask.getEpic(), subtask.getId());
                    epic.addTask(newSubtask, epic);
                }
                epic.calculateStatus(epic);
                subtasks.put(newSubtask.getId(), newSubtask);
            } else {
                throw new IllegalStateException();
            }
        return subtask;
    }

    @Override
    public void updateTask(Task task) throws IllegalStateException {
        if (tasks.containsKey(task.getId())) {
            Task newTask;
            try {
                if (getPrioritizedTasks().stream().filter(element -> !element.equals(task)).noneMatch(element ->
                        isTasksOverlapInTime(task, element))) {
                    if (task.getStartTime() != null) {
                        newTask = new Task(task.getNameTask(), task.getDescriptionTask(), task.getStatus(),
                                task.getId(), task.getDuration(), task.getStartTime());
                        prioritizedTasks.remove(tasks.get(task.getId()));
                        prioritizedTasks.add(newTask);
                    } else {
                        newTask = new Task(task.getNameTask(), task.getDescriptionTask(), task.getStatus(),
                                task.getId());
                    }
                    tasks.put(newTask.getId(), newTask);
                } else {
                    throw new IllegalStateException();
                }
            } catch (IllegalStateException e) {
                System.out.println("Задача: " + task + " не может быть изменена," +
                        " так как она будет пересекается по времени выполнения с другой задачей");
            }
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask newSubtask;
            Epic savedEpic = epics.get(subtask.getEpic().getId());
            if (savedEpic == null) {
                return;
            }
            try {
                if (getPrioritizedTasks().stream().filter(element -> !element.equals(subtask)).noneMatch(element ->
                        isTasksOverlapInTime(subtask, element))) {
                    if (subtask.getStartTime() != null) {
                        newSubtask = new Subtask(subtask.getNameTask(), subtask.getDescriptionTask(), subtask.getStatus(),
                                subtask.getEpic(), subtask.getId(), subtask.getDuration(), subtask.getStartTime());
                        prioritizedTasks.remove(subtasks.get(subtask.getId()));
                        prioritizedTasks.add(newSubtask);
                    } else {
                        newSubtask = new Subtask(subtask.getNameTask(), subtask.getDescriptionTask(), subtask.getStatus(),
                                subtask.getEpic(), subtask.getId());
                    }
                    savedEpic.changeSubTasks(savedEpic.getSubTasks(), newSubtask);
                    savedEpic.calculateStatus(savedEpic);
                    savedEpic.calculateDurationStartAndEndTime(savedEpic);
                    epics.put(savedEpic.getId(), savedEpic);
                    subtasks.put(newSubtask.getId(), newSubtask);
                } else {
                    throw new IllegalStateException();
                }
            } catch (IllegalStateException e) {
                System.out.println("Задача: " + subtask + " не может быть изменена," +
                        " так как она будет пересекается по времени выполнения с другой задачей");
            }
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic saved = epics.get(epic.getId());
            if (saved == null) {
                return;
            }
            saved.setNameTask(epic.getNameTask());
            saved.setDescriptionTask(epic.getDescriptionTask());
            epics.put(saved.getId(), saved);
        } else {
           throw new NotFoundException();
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            if (inMemoryHistoryManager.getHistory().contains(tasks.get(id))) {
                inMemoryHistoryManager.remove(id);
            }
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            if (inMemoryHistoryManager.getHistory().contains(subtasks.get(id))) {
                inMemoryHistoryManager.remove(id);
            }
            Subtask removeSubtask = getSubtask(id);
            Epic epicSaved = epics.get(removeSubtask.getEpic().getId());
            epicSaved.removeSubtask(epicSaved, removeSubtask);
            if (removeSubtask.getStartTime() != null) {
                prioritizedTasks.remove(subtasks.get(id));
                epicSaved.calculateDurationStartAndEndTime(epicSaved);
            }
            subtasks.remove(id);
            epicSaved.calculateStatus(epicSaved);
            epics.put(epicSaved.getId(), epicSaved);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            getEpic(id).getSubTasks().stream().forEach(subtask -> {
                if (inMemoryHistoryManager.getHistory().contains(subtask)) {
                    inMemoryHistoryManager.remove(subtask.getId());
                }
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtask.getId());
            });
            if (inMemoryHistoryManager.getHistory().contains(epics.get(id))) {
                inMemoryHistoryManager.remove(id);
            }
            epics.remove(id);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().stream().forEach(task -> {
            if (inMemoryHistoryManager.getHistory().contains(task)) {
                inMemoryHistoryManager.remove(task.getId());
            }
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
        });
        tasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
        subtasks.values().stream().forEach(subtask -> {
            Epic epicSaved = epics.get(subtask.getEpic().getId());
            epicSaved.removeSubtask(epicSaved, subtask);
            epicSaved.calculateStatus(epicSaved);
            epics.put(epicSaved.getId(), epicSaved);
            if (inMemoryHistoryManager.getHistory().contains(subtask)) {
                inMemoryHistoryManager.remove(subtask.getId());
            }
            prioritizedTasks.remove(subtask);
        });
        subtasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        epics.values().stream().filter(epic -> inMemoryHistoryManager.getHistory().contains(epic))
                .forEach(epic -> inMemoryHistoryManager.remove(epic.getId()));
        subtasks.values().stream().forEach(subtask -> {
            if (inMemoryHistoryManager.getHistory().contains(subtask)) {
                inMemoryHistoryManager.remove(subtask.getId());
            }
            prioritizedTasks.remove(subtask);
        });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubTasks();
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public boolean isTasksOverlapInTime(Task task1, Task task2) {
        if (task1.getStartTime() != null && task2.getStartTime() != null) {
            return (!task1.getStartTime().isAfter(task2.getStartTime()) ||
                    !task1.getStartTime().isAfter(task2.getEndTime())) &&
                    (!task1.getStartTime().isBefore(task2.getStartTime()) ||
                            !task1.getEndTime().isBefore(task2.getStartTime()));
        } else {
            return false;
        }
    }
}


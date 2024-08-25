package model;

import com.google.gson.annotations.Expose;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class Epic extends Task {
    @Expose
    private List<Subtask> subTasks = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(String nameTask, String descriptionTask) {
        super(nameTask, descriptionTask, null);
    }

    public Epic(String nameTask, String descriptionTask, TaskStatus status, int id) {
        super(nameTask, descriptionTask, status, id);
    }

    public Epic(String nameTask, String descriptionTask, TaskStatus status, int id, Duration duration,
                LocalDateTime startTime, LocalDateTime endTime) {
        super(nameTask, descriptionTask, status, id, duration, startTime);
        this.endTime = endTime;
    }

    public List<Subtask> getSubTasks() {
        return subTasks;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    private void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void changeSubTasks(List<Subtask> subTasks, Subtask subtask) {
        subTasks.set(subTasks.indexOf(subtask), subtask);
    }

    public void addTask(Subtask subtask, Epic epic) {
        epic.getSubTasks().add(subtask);
    }

    public void removeSubtask(Epic epic, Subtask subtask) {
        epic.getSubTasks().remove(subtask);
    }

    public void calculateStatus(Epic epic) {
        int sizeSubtask = epic.getSubTasks().size();
        int numberOfStatusDone = 0;
        int numberOfStatusProgress = 0;
        TaskStatus result = TaskStatus.NEW;
        for (Subtask element : epic.getSubTasks()) {
            if (element.getStatus().equals(TaskStatus.DONE)) {
                numberOfStatusDone += 1;
            } else if (element.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                numberOfStatusProgress += 1;
            }
        }
        if (numberOfStatusDone == sizeSubtask) {
            result = TaskStatus.DONE;
        } else if (numberOfStatusProgress != 0 || numberOfStatusDone != 0) {
            result = TaskStatus.IN_PROGRESS;
        }
        epic.setStatus(result);
    }

    public void calculateDurationStartAndEndTime(Epic epic) {
        Duration duration = Duration.ofHours(0);
        List<Subtask> subtasks = epic.getSubTasks();
        Supplier<Stream<Subtask>> streamSupplier = () -> subtasks.stream();
        Optional<Subtask> subtaskWithMinStartTime =
                streamSupplier.get().min((Subtask subtask1, Subtask subtask2) -> {
                    if (subtask1.getStartTime().equals(subtask2.getStartTime())) {
                        return 0;
                    } else if (subtask1.getStartTime().isAfter(subtask2.getStartTime())) {
                        return 1;
                    } else {
                        return -1;
                    }
                });
        subtaskWithMinStartTime.ifPresent(subtask -> epic.setStartTime(subtask.getStartTime()));
        Optional<Subtask> subtaskWithMaxEndTime =
                streamSupplier.get().max((Subtask subtask1, Subtask subtask2) -> {
                    if (subtask1.getEndTime().equals(subtask2.getEndTime())) {
                        return 0;
                    } else if (subtask1.getEndTime().isAfter(subtask2.getEndTime())) {
                        return 1;
                    } else {
                        return -1;
                    }
                });
        subtaskWithMaxEndTime.ifPresent(subtask -> epic.setEndTime(subtask.getEndTime()));
        for (Subtask element : subtasks) {
            duration = duration.plus(element.getDuration());
        }
        epic.setDuration(duration);
    }
}

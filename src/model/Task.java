package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
    private String nameTask;
    private String descriptionTask;
    private TaskStatus status;
    private int id;
    private Duration duration;
    private LocalDateTime startTime;


    public Task(String nameTask, String descriptionTask, TaskStatus status) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
    }

    public Task(String nameTask, String descriptionTask, TaskStatus status, int id) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
        this.id = id;
    }

    public Task(String nameTask, String descriptionTask, TaskStatus status,
                Duration duration, LocalDateTime startTime) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String nameTask, String descriptionTask, TaskStatus status, int id,
                Duration duration, LocalDateTime startTime) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public String getNameTask() {
        return nameTask;
    }

    public String getDescriptionTask() {
        return descriptionTask;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public void setDescriptionTask(String descriptionTask) {
        this.descriptionTask = descriptionTask;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Epic getEpic() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        if (startTime != null) {
            return "Task{" + "nameTask='" + nameTask + '\'' + ", descriptionTask='" +
                    descriptionTask + '\'' + ", status='" + status + '\'' +
                    ", id='" + id + '\'' + ", duration='" + duration.toHours() + ":" + duration.toMinutesPart()
                    + '\'' + ", startTime='" + startTime.format(DATE_TIME_FORMATTER) + '\'' + ", endTime='" +
                    getEndTime().format(DATE_TIME_FORMATTER) + '\'' + "}";
        } else
            return "Task{" + "nameTask='" + nameTask + '\'' + ", descriptionTask='" +
                    descriptionTask + '\'' + ", status='" + status + '\'' +
                    ", id='" + id + '\'' + "}";
    }
}

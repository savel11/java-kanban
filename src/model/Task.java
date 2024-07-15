package model;

import java.util.Objects;

public class Task {
    private String nameTask;
    private String descriptionTask;
    protected TaskStatus status;
    private int id;


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

    public TaskStatus getStatus() {
        return status;
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
        return "Task{" + "nameTask='" + nameTask + '\'' + ", descriptionTask='" +
                descriptionTask + '\'' + ", status='" + status + '\'' +
                ", id='" + id + '\'' + "}";

    }


}

package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;


    public Subtask(String nameTask, String descriptionTask, TaskStatus status, Epic epic) {
        super(nameTask, descriptionTask, status);
        this.epic = epic;
    }

    public Subtask(String nameTask, String descriptionTask, TaskStatus status, Epic epic, int id) {
        super(nameTask, descriptionTask, status, id);
        this.epic = epic;
    }

    public Subtask(String nameTask, String descriptionTask, TaskStatus status,
                   Duration duration, LocalDateTime startTime, Epic epic) {
        super(nameTask, descriptionTask, status, duration, startTime);
        this.epic = epic;
    }

    public Subtask(String nameTask, String descriptionTask, TaskStatus status, Epic epic, int id,
                   Duration duration, LocalDateTime startTime) {
        super(nameTask, descriptionTask, status, id, duration, startTime);
        this.epic = epic;
    }


    public void setEpicId(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        if (getStartTime() != null) {
            return "Task{" + "nameTask='" + getNameTask() + '\'' + ", descriptionTask='" +
                    getDescriptionTask() + '\'' + ", status='" + getStatus() + '\'' +
                    ", id='" + getId() + '\'' + ", epic=' " + getEpic() + '\'' + ", duration='"
                    + getDuration().toHours() + ":" + getDuration().toMinutesPart()
                    + '\'' + ", startTime='" + getStartTime().format(DATE_TIME_FORMATTER) + '\'' + ", endTime='" +
                    getEndTime().format(DATE_TIME_FORMATTER) + '\'' + "}";
        } else
            return "Task{" + "nameTask='" + getNameTask() + '\'' + ", descriptionTask='" +
                    getDescriptionTask() + '\'' + ", status='" + getStatus() + '\'' +
                    ", id='" + getId() + '\'' + ", epic=' " + getEpic() + '\'' + "}";

    }
}

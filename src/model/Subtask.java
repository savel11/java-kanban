package model;

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

    public void setEpicId(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}

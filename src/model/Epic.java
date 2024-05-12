package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    List<Subtask> subTasks = new ArrayList<>();


    public Epic(String nameTask, String descriptionTask) {
        super(nameTask, descriptionTask, null);
    }

    public List<Subtask> getSubTasks() {
        return subTasks;
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


}

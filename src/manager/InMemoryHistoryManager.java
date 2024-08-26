package manager;

import model.Epic;
import model.Subtask;
import model.Task;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private TasksDoubleList<Task> tasksDoubleList = new TasksDoubleList<>();
    private Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        Task taskHistory;
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        if ("Task".equals(task.getClass().getSimpleName())) {
            taskHistory = new Task(task.getNameTask(), task.getDescriptionTask(), task.getStatus(), task.getId(),
                    task.getDuration(), task.getStartTime());
        } else if ("Subtask".equals(task.getClass().getSimpleName())) {
            taskHistory = new Subtask(task.getNameTask(), task.getDescriptionTask(), task.getStatus(),
                    task.getEpic(), task.getId(), task.getDuration(), task.getStartTime());
        } else {
            taskHistory = new Epic(task.getNameTask(), task.getDescriptionTask(), task.getStatus(), task.getId(),
                    task.getDuration(), task.getStartTime(), task.getEndTime());
        }
        tasksDoubleList.linkLast(taskHistory);
        nodeMap.put(task.getId(), tasksDoubleList.tail);
    }

    @Override
    public List<Task> getHistory() {
        return tasksDoubleList.getTask();
    }

    @Override
    public void remove(int id) {
        if (getHistory().stream().map(task -> task.getId()).anyMatch(taskId -> taskId == id)) {
            tasksDoubleList.removeNode(nodeMap.get(id));
            nodeMap.remove(id);
        }
    }

    public static class TasksDoubleList<T> {
        public Node<T> head;
        public Node<T> tail;
        private int size = 0;

        public void linkLast(T task) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(task, oldTail, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            size++;
        }

        public List<T> getTask() {
            List<T> history = new ArrayList<>();
            Node<T> task = head;
            for (int i = 1; i <= size; i++) {
                history.add(task.data);
                task = task.next;
            }
            return history;
        }

        public void removeNode(Node<T> taskNode) {
            size--;
            if (size == 0) {
                tail = head = null;
            } else if (taskNode.next == null) {
                tail = taskNode.prev;
                taskNode.prev.next = null;
            } else if (taskNode.prev == null) {
                head = taskNode.next;
                taskNode.next.prev = null;
            } else {
                taskNode.next.prev = taskNode.prev;
                taskNode.prev.next = taskNode.next;
            }
        }
    }
}




package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    List<Task> history = new ArrayList<>();
    TasksDoubleList<Task> tasksDoubleList = new TasksDoubleList<>();
    Map<Integer, Node<Task>> nodeMap = new HashMap<>();


    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            tasksDoubleList.removeNode(nodeMap.get(task.getId()));
            Task taskHistory = new Task(task.getNameTask(), task.getDescriptionTask(), task.getStatus(), task.getId());

            tasksDoubleList.linkLast(taskHistory);
            nodeMap.put(task.getId(), tasksDoubleList.tail);
        } else {
            Task taskHistory = new Task(task.getNameTask(), task.getDescriptionTask(), task.getStatus(), task.getId());

            tasksDoubleList.linkLast(taskHistory);
            nodeMap.put(task.getId(), tasksDoubleList.tail);

        }

    }

    @Override
    public List<Task> getHistory() {
        return history = tasksDoubleList.getTask();
    }
    @Override
    public void clearHistory() {
        tasksDoubleList.clear();
    }
    @Override
    public void remove(int id) {
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
        public void clear() {
            if (size > 0) {
                head = null;
                tail = null;
                size = 0;
            }
        }
    }
}




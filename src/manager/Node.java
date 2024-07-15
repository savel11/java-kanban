package manager;

public class Node<Task> {
public Task data;
    public Node<Task> next;
    public Node<Task> prev;

    public Node(Task task, Node<Task> prev, Node<Task> next) {
        this.data = task;
        this.next = next;
        this.prev = prev;
    }
}



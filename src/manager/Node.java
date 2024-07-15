package manager;

public class Node<Task> {
public Task data;
    public Node<Task> next;
    public Node<Task> prev;

    public Node(Task data, Node<Task> prev, Node<Task> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}



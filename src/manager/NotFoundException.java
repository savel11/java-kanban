package manager;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(int id) {
        super("Задача с идентификатором " + id + " не найдена");
    }

}

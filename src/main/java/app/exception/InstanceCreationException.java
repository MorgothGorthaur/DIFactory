package app.exception;

public class InstanceCreationException extends RuntimeException {
    public InstanceCreationException(String message, Class<?> clazz) {
        super("Error creating an instance of class: " + clazz.getName() + ". Reason: " + message);
    }
}

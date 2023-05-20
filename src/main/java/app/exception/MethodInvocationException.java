package app.exception;

public class MethodInvocationException extends RuntimeException {
    public MethodInvocationException(String message, Class<?> clazz) {
        super("Error invoking method in class: " + clazz.getName() + ". Reason: " + message);
    }
}

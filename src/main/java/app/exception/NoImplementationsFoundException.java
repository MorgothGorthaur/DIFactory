package app.exception;

public class NoImplementationsFoundException extends RuntimeException{
    public NoImplementationsFoundException(Class<?> implementationClazz) {
        super("no implementations found for " + implementationClazz.getName());
    }
}

package app.exception;

public class FoundSecondImplementationException extends RuntimeException{
    public FoundSecondImplementationException(Class<?> interfaceClazz){
        super("found second implementation for " + interfaceClazz.getName());
    }
}

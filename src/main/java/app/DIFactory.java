package app;

public interface DIFactory {
    <T> T getInstance(Class<T> interfaceClass);
}

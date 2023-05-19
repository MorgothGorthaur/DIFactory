package app;

public interface ServiceFactory {
    <T> T createInstance(Class<T> interfaceClass);
}

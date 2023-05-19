package app.factory;

public interface ServiceFactory {
    <T> T createInstance(Class<T> interfaceClass);
}

package app.factory;

public interface RegisterFactory {
    <T> T createInstance(Class<T> interfaceClass, ServiceFactory factory);
}

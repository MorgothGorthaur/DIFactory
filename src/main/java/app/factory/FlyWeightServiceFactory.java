package app.factory;


import java.util.HashMap;
import java.util.Map;

class FlyWeightServiceFactory implements ServiceFactory {
    private final Map<Class<?>, Object> instances;
    private final RegisterFactory serviceFactory;
    public FlyWeightServiceFactory(RegisterFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        instances = new HashMap<>();
    }
    @Override
    public <T> T createInstance(Class<T> interfaceClass) {
        if(instances.containsKey(interfaceClass)) return interfaceClass.cast(instances.get(interfaceClass));
        T instance = serviceFactory.createInstance(interfaceClass, this);
        instances.put(interfaceClass, instance);
        return instance;
    }
}

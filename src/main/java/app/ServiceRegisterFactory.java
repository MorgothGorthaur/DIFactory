package app;

import java.util.HashMap;
import java.util.function.Function;

public class ServiceRegisterFactory implements ServiceFactory{
    private static final HashMap<Class<?>, Function<ServiceFactory, ?>> serviceMap = new HashMap<>();

    public <T> T createInstance(Class<T> interfaceClass) {
        if(!serviceMap.containsKey(interfaceClass)) throw new RuntimeException("No implementations found!");
        return interfaceClass.cast(serviceMap.get(interfaceClass).apply(this));
    }

    public static void  register(Class<?> interfaceClass, Function<ServiceFactory, ? > creator) {
        if(serviceMap.containsKey(interfaceClass)) throw new RuntimeException("Found second implementation for "  + interfaceClass.getName());
        serviceMap.put(interfaceClass, creator);
    }
}

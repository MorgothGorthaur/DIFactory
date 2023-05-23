package app.factory;

import app.exception.FoundSecondImplementationException;
import app.exception.NoImplementationsFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ServiceRegisterFactoryImpl implements RegisterFactory{
    private static final Map<Class<?>, Function<ServiceFactory, ?>> serviceMap = new HashMap<>();

    @Override
    public <T> T createInstance(Class<T> interfaceClass, ServiceFactory factory) {
        if(!serviceMap.containsKey(interfaceClass)) throw new NoImplementationsFoundException(interfaceClass);
        return interfaceClass.cast(serviceMap.get(interfaceClass).apply(factory));
    }


    public static void  register(Class<?> interfaceClass, Function<ServiceFactory, ? > creator) {
        if(serviceMap.containsKey(interfaceClass)) throw new FoundSecondImplementationException(interfaceClass);
        serviceMap.put(interfaceClass, creator);
    }
}

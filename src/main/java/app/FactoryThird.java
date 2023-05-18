package app;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

public class FactoryThird implements DIFactory{

    private final Map<Class<?>, Class<?>> interfaceImplementationMap;
    private final Map<Class<?>, Object> interfaceInstanceMap;

    public FactoryThird(Scanner scanner) {
        interfaceImplementationMap = scanner.getInterfaceImplementationMap();
        interfaceInstanceMap = scanner.getInterfaceInstanceMap();
    }


    @Override
    public <T> T getInstance(Class<T> interfaceClazz) {
        if (interfaceInstanceMap.containsKey(interfaceClazz)) return interfaceClazz.cast(interfaceInstanceMap.get(interfaceClazz));
        if (interfaceImplementationMap.containsKey(interfaceClazz)) {
            createInstanceFromImplementation(interfaceClazz);
            return getInstance(interfaceClazz);
        }
        throw new RuntimeException("no implementations found for " + interfaceClazz.getName());
    }

    private void createInstanceFromImplementation(Class<?> interfaceClass) {
        try {
            Constructor<?> constructor = Arrays
                    .stream(interfaceImplementationMap.get(interfaceClass).getDeclaredConstructors())
                    .findFirst().orElseThrow(RuntimeException::new);
            Object[] objects = Arrays.stream(constructor.getParameters())
                    .map(Parameter::getType).map(this::getInstance).toArray();
            interfaceInstanceMap.put(interfaceClass, constructor.newInstance(objects));
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

}

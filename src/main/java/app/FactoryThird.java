package app;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FactoryThird implements DIFactory{

    private final Map<Class<?>, Class<?>> context;
    private final Map<Class<?>, Object> hash;

    public FactoryThird(Map<Class<?>, Class<?>> context) {
        this.context = context;
        this.hash = new HashMap<>();
    }

    public FactoryThird(Map<Class<?>, Class<?>> context, Map<Class<?>, Object> hash) {
        this.context = context;
        this.hash = hash;
    }
    @Override
    public <T> T getInstance(Class<T> interfaceClazz) {
        if (hash.containsKey(interfaceClazz)) return interfaceClazz.cast(hash.get(interfaceClazz));
        else if (context.containsKey(interfaceClazz)) {
            Class<?> implementationClass = context.get(interfaceClazz);
            T fromImplementation = createFromImplementation(interfaceClazz, implementationClass);
            hash.put(interfaceClazz, fromImplementation);
            return fromImplementation;
        }
        throw new RuntimeException();
    }

    private <T> T createFromImplementation(Class<T> interfaceClass, Class<?> implementationClass) {
        try {
            Constructor<?> constructor = Arrays.stream(implementationClass.getDeclaredConstructors())
                    .findFirst().orElseThrow(RuntimeException::new);
            Object[] objects = Arrays.stream(constructor.getParameters())
                    .map(Parameter::getType).map(this::getInstance).toArray();
            return interfaceClass.cast(constructor.newInstance(objects));
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}

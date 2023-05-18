package app;

import app.annotation.Bean;
import app.annotation.Component;
import app.annotation.Config;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FactorySecond implements DIFactory {
    private final Map<Class<?>, Class<?>> context;
    private final Map<Class<?>, Object> hash;

    private final Reflections reflections;

    private final String packageName;

    public FactorySecond(String packageName) {
        this.packageName = packageName;
        this.reflections = new Reflections(packageName);
        context = new HashMap<>();
        hash = new HashMap<>();
        scanConfigs();
        scanComponents();
        System.out.println(hash);
        System.out.println(context);
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
        throw new RuntimeException("no implementations found!");
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

    private void scanComponents() {
        try {
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Component.class);
            annotatedClasses.forEach(clazz -> Arrays.stream(clazz.getInterfaces())
                    .filter(interfaceClazz -> interfaceClazz.getPackage().getName().startsWith(packageName))
                    .forEach(interfaceClazz -> {
                        if (hash.containsKey(interfaceClazz) || context.containsKey(interfaceClazz))
                            throw new RuntimeException("Found second implementation");
                        else context.put(interfaceClazz, clazz);
                    })
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void scanConfigs() {
        try {
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Config.class);
            for (Class<?> clazz : annotatedClasses) {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Method[] methods = Arrays.stream(clazz.getMethods()).filter(v -> v.isAnnotationPresent(Bean.class)).toArray(Method[]::new);
                for (Method method : methods) {
                    Object invoke = method.invoke(instance);
                    Class<?> anInterface = invoke.getClass().getInterfaces()[0];
                    if (hash.containsKey(anInterface)) throw new RuntimeException("Found second implementation");
                    else hash.put(anInterface, invoke);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

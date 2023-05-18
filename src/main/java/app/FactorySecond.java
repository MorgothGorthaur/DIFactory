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
import java.util.function.Predicate;

public class FactorySecond implements DIFactory {
    private final Map<Class<?>, Class<?>> interfaceImplementationMap;
    private final Map<Class<?>, Object> interfaceInstanceMap;

    private final Reflections reflections;

    private final String packageName;

    public FactorySecond(String packageName) {
        this.packageName = packageName;
        reflections = new Reflections(packageName);
        interfaceImplementationMap = new HashMap<>();
        interfaceInstanceMap = new HashMap<>();
        scanConfigs();
        scanComponents();
    }

    @Override
    public <T> T getInstance(Class<T> interfaceClazz) {
        if (interfaceInstanceMap.containsKey(interfaceClazz)) return interfaceClazz.cast(interfaceInstanceMap.get(interfaceClazz));
        else if (interfaceImplementationMap.containsKey(interfaceClazz)) {
            Class<?> implementationClass = interfaceImplementationMap.get(interfaceClazz);
            T fromImplementation = createFromImplementation(interfaceClazz, implementationClass);
            interfaceInstanceMap.put(interfaceClazz, fromImplementation);
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
        reflections.getTypesAnnotatedWith(Component.class).forEach(this::handleAnnotatedClass);
    }

    private void handleAnnotatedClass(Class<?> clazz) {
        Predicate<Class<?>> isDesiredInterface = interfaceClazz
                -> interfaceClazz.getPackage().getName().startsWith(packageName);
        Arrays.stream(clazz.getInterfaces()).filter(isDesiredInterface)
                .forEach(interfaceClazz -> addToContext(clazz, interfaceClazz));
    }

    private void addToContext(Class<?> clazz, Class<?> interfaceClazz) {
        if (interfaceInstanceMap.containsKey(interfaceClazz) || interfaceImplementationMap.containsKey(interfaceClazz))
            throw new RuntimeException("Found second implementation");
        else interfaceImplementationMap.put(interfaceClazz, clazz);
    }

    private void scanConfigs() {
        reflections.getTypesAnnotatedWith(Config.class).forEach(this::handleConfig);
    }

    private void handleConfig(Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Arrays.stream(clazz.getMethods()).filter(v -> v.isAnnotationPresent(Bean.class))
                    .forEach(v -> handleMethod(instance, v));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleMethod(Object instance, Method method) {
        try {
            Object invoke = method.invoke(instance);
            Class<?> anInterface = method.getReturnType();
            addToHash(invoke, anInterface);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void addToHash(Object invoke, Class<?> anInterface) {
        if (interfaceInstanceMap.containsKey(anInterface)) throw new RuntimeException("Found second implementation");
        else interfaceInstanceMap.put(anInterface, invoke);
    }

}

package app;

import app.annotation.Bean;
import app.annotation.Component;
import app.annotation.Config;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


public class Factory {
    private final Map<Class<?>, Object> context;
    public Factory() {
        new HashMap<>();
        context = new HashMap<>();
        scanConfigs();
        var map = scanComponents();

        for(var e : map.entrySet()) {
            createImpl(e.getKey(), map);
        }
    }

    public <T> T getInstance(Class<T> interfaceClass) {
        return interfaceClass.cast(context.get(interfaceClass));
    }

    private <T> Object createImpl(Class<T> interfaceClass, Map<Class<?>, Class<?>> map) {
        try {
            if (context.containsKey(interfaceClass)) return context.get(interfaceClass);
            else if (map.containsKey(interfaceClass)) {
                Constructor<?> constructor = Arrays.stream(map.get(interfaceClass).getDeclaredConstructors()).findFirst().orElseThrow(RuntimeException::new);
                Object[] objects = Arrays.stream(constructor.getParameters()).map(Parameter::getType)
                        .map(clazz -> createImpl(clazz, map)).toArray(Object[]::new);
                Object o = constructor.newInstance(objects);
                context.put(interfaceClass, o);
                return o;
            } else throw new RuntimeException();
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }
    private Map<Class<?>,  Class<?>> scanComponents() {
        try {
            Map<Class<?>, Class<?>> map = new HashMap<>();
            String packageName = "app";
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Component.class);
            annotatedClasses.forEach(clazz -> Arrays.stream(clazz.getInterfaces())
                    .filter(interfaceClazz -> interfaceClazz.getName().startsWith(packageName))
                    .forEach(interfaceClazz -> {
                        if(map.containsKey(interfaceClazz)) throw new RuntimeException();
                        else map.put(interfaceClazz, clazz);
                    })
            );
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void scanConfigs() {
        try {
            String packageName = "app";
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Config.class);
            for(Class<?> clazz : annotatedClasses) {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Method[] methods = Arrays.stream(clazz.getMethods()).filter(v -> v.isAnnotationPresent(Bean.class)).toArray(Method[]::new);
                for (Method method : methods) {
                    Object invoke = method.invoke(instance);
                    Class<?> anInterface = invoke.getClass().getInterfaces()[0];
                    if(context.containsKey(anInterface)) throw new RuntimeException();
                    else context.put(anInterface, invoke);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

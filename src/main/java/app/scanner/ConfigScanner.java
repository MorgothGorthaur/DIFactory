package app.scanner;

import app.ServiceRegisterFactory;
import app.annotation.Bean;
import app.annotation.Config;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ConfigScanner implements InstanceScanner {
    private final Reflections reflections;

    public ConfigScanner(String packageName) {
        reflections = new Reflections(packageName);
    }

    @Override
    public void scan() {
        reflections.getTypesAnnotatedWith(Config.class).forEach(this::handleConfig);
    }

    private void handleConfig(Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Arrays.stream(clazz.getMethods()).filter(v -> v.isAnnotationPresent(Bean.class))
                    .forEach(v -> handleMethod(instance, v));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleMethod(Object instance, Method method) {
        Class<?> anInterface = method.getReturnType();
        ServiceRegisterFactory.register(anInterface, (ServiceFactory) -> {
            try {
                return method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

}

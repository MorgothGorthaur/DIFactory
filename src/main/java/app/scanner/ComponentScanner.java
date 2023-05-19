package app.scanner;

import app.ServiceFactory;
import app.ServiceRegisterFactory;
import app.annotation.Component;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class ComponentScanner implements InstanceScanner {
    private final String packageName;
    private final Reflections reflections;

    public ComponentScanner(String packageName) {
        this.packageName = packageName;
        reflections = new Reflections(packageName);
    }

    @Override
    public void scan() {
        reflections.getTypesAnnotatedWith(Component.class).forEach(this::handleComponent);
    }

    private void handleComponent(Class<?> clazz) {
            Predicate<Class<?>> isDesiredInterface = interfaceClazz
                    -> interfaceClazz.getPackage().getName().startsWith(packageName);
            Arrays.stream(clazz.getInterfaces()).filter(isDesiredInterface)
                    .forEach(interfaceClazz -> register(clazz, interfaceClazz));
    }

    private void register(Class<?> clazz, Class<?> interfaceClazz) {
        ServiceRegisterFactory.register(interfaceClazz, getInstance(clazz));
    }

    private Function<ServiceFactory, Object> getInstance(Class<?> clazz) {
        return (serviceFactory) -> {
            try {
                Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors())
                        .findFirst().orElseThrow(RuntimeException::new);
                Object[] objects = Arrays.stream(constructor.getParameters()).map(Parameter::getType).map(serviceFactory::createInstance).toArray();
                return constructor.newInstance(objects);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}

package app.factory.register;

import app.exception.ConstructorNotFoundException;
import app.exception.InstanceCreationException;
import app.factory.ServiceFactory;
import app.annotation.Component;
import app.factory.ServiceRegisterFactoryImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class ComponentRegister implements Register {
    private Register nextRegister;
    private final String packageName;

    public ComponentRegister(String packageName, Register nextRegister) {
        this.nextRegister = nextRegister;
        this.packageName = packageName;
    }

    public ComponentRegister(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void register(Class<?> clazz) {
        if (clazz.getDeclaredAnnotation(Component.class) != null) registerComponent(clazz);
        else if (nextRegister != null) nextRegister.register(clazz);
    }

    private void registerComponent(Class<?> clazz) {
        Predicate<Class<?>> isDesiredInterface = interfaceClazz
                -> interfaceClazz.getPackage().getName().startsWith(packageName);
        Arrays.stream(clazz.getInterfaces()).filter(isDesiredInterface)
                .forEach(interfaceClazz -> ServiceRegisterFactoryImpl.register(interfaceClazz, getInstance(clazz)));
    }

    private Function<ServiceFactory, Object> getInstance(Class<?> clazz) {
        return (serviceFactory) -> {
            try {
                Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors())
                        .findFirst().orElseThrow(() -> new ConstructorNotFoundException(clazz));
                Object[] objects = Arrays.stream(constructor.getParameters()).map(Parameter::getType).map(serviceFactory::createInstance).toArray();
                return constructor.newInstance(objects);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
                throw new InstanceCreationException(ex.getMessage(), clazz);
            }
        };
    }
}

package app.register;

import app.ServiceFactory;
import app.ServiceRegisterFactory;
import app.annotation.Bean;
import app.annotation.Config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

public class ConfigRegister implements Register{

    private Register nextRegister;
    public ConfigRegister(){

    }
    public ConfigRegister(Register nextRegister) {
        this.nextRegister = nextRegister;
    }
    @Override
    public void register(Class<?> clazz) {
        if(clazz.getDeclaredAnnotation(Config.class) != null) registerConfig(clazz);
        else if(nextRegister != null) nextRegister.register(clazz);
    }

    private void registerConfig(Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Arrays.stream(clazz.getMethods()).filter(v -> v.isAnnotationPresent(Bean.class))
                    .forEach(v -> ServiceRegisterFactory.register(v.getReturnType(), getInstance(instance, v)));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Function<ServiceFactory, Object> getInstance(Object instance, Method method) {
        return (ServiceFactory) -> {
            try {
                return method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }
}

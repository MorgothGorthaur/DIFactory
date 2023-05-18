package app;

import java.util.Map;

public interface Scanner {
    Map<Class<?>, Class<?>> getInterfaceImplementationMap();
    Map<Class<?>, Object> getInterfaceInstanceMap();
}

package app.scanner;

import app.annotation.Component;
import org.reflections.Reflections;

import java.util.List;

public class ScannerImpl implements Scanner {
    private final Reflections reflections;
    public ScannerImpl(String packageName){
        reflections = new Reflections(packageName);
    }
    @Override
    public List<Class<?>> getComponents() {
        return reflections.getTypesAnnotatedWith(Component.class).stream().toList();
    }
}

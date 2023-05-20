package app.factory;

import app.factory.register.ComponentRegister;
import app.factory.register.ConfigRegister;
import app.factory.register.Register;
import app.factory.scanner.ScannerImpl;

class ServiceFactoryFacade implements ServiceFactory{
    private final ServiceFactory factory;
    public ServiceFactoryFacade() {
        String packageName = "app";
        Register register =new ComponentRegister(packageName, new ConfigRegister());
        new ScannerImpl(packageName).getComponents().forEach(register::register);
        this.factory = new FlyWeightServiceFactory(new ServiceRegisterFactory());
    }

    @Override
    public <T> T createInstance(Class<T> interfaceClass) {
        return factory.createInstance(interfaceClass);
    }
}

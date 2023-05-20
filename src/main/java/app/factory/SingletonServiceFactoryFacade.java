package app.factory;

public class SingletonServiceFactoryFacade {
    private static ServiceFactory serviceFactory;

    private SingletonServiceFactoryFacade() {
    }

    public static ServiceFactory getServiceFactory() {
        if (serviceFactory == null) serviceFactory = new ServiceFactoryFacade();
        return serviceFactory;
    }
}

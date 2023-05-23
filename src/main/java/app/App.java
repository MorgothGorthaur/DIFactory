package app;


import app.calc.Calc;
import app.calc.CalcPro;
import app.factory.ServiceFactory;
import app.factory.SingletonServiceFactoryFacade;


public class App {
    public static void main(String[] args) {
        ServiceFactory serviceFactory = SingletonServiceFactoryFacade.getServiceFactory();
        CalcPro instance1 = serviceFactory.createInstance(CalcPro.class);
        Calc instance = serviceFactory.createInstance(Calc.class);
        System.out.println(instance);
        System.out.println(instance1);
        System.out.println(instance == serviceFactory.createInstance(Calc.class));
    }
}

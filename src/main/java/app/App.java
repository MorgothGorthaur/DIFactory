package app;


import app.calc.Calc;
import app.calc.CalcPro;
import app.register.ComponentRegister;
import app.register.ConfigRegister;
import app.register.Register;
import app.scanner.ScannerImpl;

import java.util.List;

public class App {
    public static void main(String[] args) {
        String packageName = "app";
        Register register =new ComponentRegister(packageName, new ConfigRegister());
        List<Class<?>> components = new ScannerImpl(packageName).getComponents();
        System.out.println(components);
        components.forEach(register::register);

        ServiceFactory serviceFactory = new SingletonServiceFactory(new ServiceRegisterFactory());
        Calc instance = serviceFactory.createInstance(Calc.class);
        CalcPro instance1 = serviceFactory.createInstance(CalcPro.class);
        System.out.println(instance);
        System.out.println(instance1);
        System.out.println(instance == serviceFactory.createInstance(Calc.class));

    }
}

package app;


import app.calc.Calc;
import app.calc.CalcPro;
import app.scanner.ComponentScanner;
import app.scanner.ConfigScanner;

public class App {
    public static void main(String[] args) {

        new ComponentScanner("app").scan();
        new ConfigScanner("app").scan();

        ServiceFactory serviceFactory = new ServiceLazyFactory(new ServiceRegisterFactory());
        Calc instance = serviceFactory.createInstance(Calc.class);
        CalcPro instance1 = serviceFactory.createInstance(CalcPro.class);
        System.out.println(instance);
        System.out.println(instance1);
        System.out.println(instance == serviceFactory.createInstance(Calc.class));

    }
}

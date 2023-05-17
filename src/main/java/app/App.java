package app;

import app.calc.Calc;
import app.calc.CalcPro;

public class App {
    public static void main(String[] args) {
        var factory = new FactorySecond("app");
        Calc calc = factory.getInstance(Calc.class);
        Calc calc1 = factory.getInstance(Calc.class);

        System.out.println(calc);
        CalcPro calcPro = factory.getInstance(CalcPro.class);
        CalcPro calcPro1 = factory.getInstance(CalcPro.class);
        System.out.println(calcPro);
        System.out.println(calc == calc1);
        System.out.println(calcPro == calcPro1);
    }
}

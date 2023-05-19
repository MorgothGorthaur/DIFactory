package app.calc;

import app.annotation.Component;

public class CalcProImpl implements CalcPro {
    private final Calc calc;

    public Calc getCalc(){
        return calc;
    }
    public CalcProImpl(Calc calc) {
        this.calc = calc;
    }

    @Override
    public String toString() {
        return "CalcProImpl{}" + calc;
    }
}

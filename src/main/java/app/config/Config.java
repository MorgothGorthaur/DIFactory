package app.config;

import app.annotation.Bean;
import app.calc.Calc;
import app.calc.CalcImpl;
import app.calc.CalcPro;
import app.calc.CalcProSecondImpl;

@app.annotation.Config
public class Config {

    @Bean
    public Calc calc() {
        return new CalcImpl();
    }
}

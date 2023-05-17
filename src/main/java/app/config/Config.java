package app.config;

import app.annotation.Bean;
import app.calc.CalcPro;
import app.calc.CalcProSecondImpl;

@app.annotation.Config
public class Config {


    public CalcPro calcPro() {
        return new CalcProSecondImpl();
    }
}

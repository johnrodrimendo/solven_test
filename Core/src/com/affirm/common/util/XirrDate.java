package com.affirm.common.util;

import com.affirm.system.configuration.Configuration;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sTbn on 7/09/16.
 */
public class XirrDate {

    public static final double tol = 1E-8;

    public static int dateDiff(Date d1, Date d2) {

        long diffInMillies = d1.getTime() - d2.getTime();
        return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static double f_xirr(double p, Date dt, Date dt0, double x) {
        return p * Math.pow((1.0 + x), (dateDiff(dt0, dt) / 365.0));
    }

    public static double df_xirr(double p, Date dt, Date dt0, double x) {
        return (1.0 / 365.0) * dateDiff(dt0, dt) * p * Math.pow((x + 1.0), ((dateDiff(dt0, dt) / 365.0) - 1.0));
    }

    public static double total_f_xirr(Double[] payments, Date[] days, double x) {
        double resf = 0.0;

        System.out.println("Days lenght: " + days.length);
        System.out.println("payments lenght: " + payments.length);

        for (int i = 0; i < payments.length; i++) {
            resf = resf + f_xirr(payments[i], days[i], days[0], x);
        }

        return resf;
    }

    public static double total_df_xirr(Double[] payments, Date[] days, double x) {
        double resf = 0.0;

        for (int i = 0; i < payments.length; i++) {
            resf = resf + df_xirr(payments[i], days[i], days[0], x);
        }

        return resf;
    }

    /*public static double Newtons_method(double guess, Double[] payments, Date[] days) {
        double x0 = guess;
        double x1;
        double err = 1e+100;

        while (err > tol) {
            x1 = x0 - total_f_xirr(payments, days, x0) / total_df_xirr(payments, days, x0);
            err = Math.abs(x1 - x0);
            x0 = x1;
        }

        //=(E2+1)^(360/365)-1
        System.out.println("XIRR ANUALIZADO: " + (Math.pow((x0 + 1), (360.0 / 365.0)) - 1));
        return (Math.pow((x0 + 1), (360.0 / 365.0)) - 1) * 100;
//        return x0 * 100;
    }*/

    public static double Newtons_method(double guess, Double[] payments, Date[] days, Boolean annualizeXirr) throws Exception {

        String filePath = new File("").getAbsolutePath();
        XirrDate d = new XirrDate();

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        if (Configuration.hostEnvIsLocal()) {
            // Fix only for intellij
            //String pathToProyect = d.getClass().getResource("/").getPath() + "Projects/solven";
            String pathToProyect = d.getClass().getResource("/").getPath();
            try {
                engine.eval(new FileReader(new File(pathToProyect).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath() + "/Core/src/com/affirm/common/util/finance.js"));
            } catch (FileNotFoundException e) {
                try{
                    engine.eval(new FileReader(new File(pathToProyect).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath() + "/solven/Core/src/com/affirm/common/util/finance.js"));
                }catch (Exception ex){
                    try {
                        engine.eval(new FileReader(new File(pathToProyect).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath() + "/Web/solven/Core/src/com/affirm/common/util/finance.js"));
                    }
                    catch (Exception exa){
                        engine.eval(new FileReader(System.getenv("FINANCE_FILE_URL")));
                    }
                }
            }
        } else {
            engine.eval(new FileReader(filePath + "/Core/src/com/affirm/common/util/finance.js"));

        }

        Invocable inv = (Invocable) engine;

        Double result = (Double) inv.invokeFunction("XIRR", payments, days, guess);
        System.out.println(result);
        System.out.println(result.getClass());

        System.out.println("SIN ANUALIZAR " + result);
        System.out.println("Non scientific notation " + String.format("%.8f", result));

        if(annualizeXirr)
            return  Math.floor(((Math.pow((result / 100.0 + 1.0), (360.00 / 365.00)) - 1) * 100) * 100) / 100;
        else
            return Double.valueOf(result);
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public static Date strToDate(String str) {
        try {
            return sdf.parse(str);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {

        Double payments[] = new Double[]{-2000.0, 189.20, 189.20, 189.20, 189.20, 189.20, 189.20, 189.20, 189.20, 189.20, 189.20, 189.20, 189.20};

        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");

        String dateInString0 = "5/6/2017";
        String dateInString1 = "15/7/2017";
        String dateInString2 = "15/8/2017";
        String dateInString3 = "15/9/2017";
        String dateInString4 = "15/10/2017";
        String dateInString5 = "15/11/2017";
        String dateInString6 = "15/12/2017";
        String dateInString7 = "15/1/2018";
        String dateInString8 = "15/2/2018";
        String dateInString9 = "15/3/2018";
        String dateInString10 = "15/4/2018";
        String dateInString11 = "15/5/2018";
        String dateInString12 = "15/6/2018";

        Date date0 = sdf.parse(dateInString0);
        Date date1 = sdf.parse(dateInString1);
        Date date2 = sdf.parse(dateInString2);
        Date date3 = sdf.parse(dateInString3);
        Date date4 = sdf.parse(dateInString4);
        Date date5 = sdf.parse(dateInString5);
        Date date6 = sdf.parse(dateInString6);
        Date date7 = sdf.parse(dateInString7);
        Date date8 = sdf.parse(dateInString8);
        Date date9 = sdf.parse(dateInString9);
        Date date10 = sdf.parse(dateInString10);
        Date date11 = sdf.parse(dateInString11);
        Date date12 = sdf.parse(dateInString12);

        Date[] days = new Date[]{date0, date1, date2, date3, date4, date5, date6, date7, date8, date9, date10, date11, date12};

        double guess = 0.1;

        System.out.println("MAIN " + (Newtons_method(guess, payments, days, true)));

    }

}

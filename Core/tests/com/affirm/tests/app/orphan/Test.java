package com.affirm.tests.app.orphan;

import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

    public static void main(String[] args) throws Exception {
//        LocalDate startDateLocal = new SimpleDateFormat("dd/MM/yyyy").parse("21/04/2019").toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        LocalDate nowDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        Period periodBeetwen = Period.between(startDateLocal, nowDateLocal);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println(sdf.parse("27/12/2022").before(sdf.parse("27/12/2022")));
        System.out.println(sdf.parse("27/12/2022").before(sdf.parse("26/12/2022")));
        System.out.println(sdf.parse("27/12/2022").before(sdf.parse("28/12/2022")));

        System.out.println(DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH).after(sdf.parse("27/12/2022")));
        System.out.println(DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH).after(sdf.parse("26/12/2022")));
        System.out.println(DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH).after(sdf.parse("28/12/2022")));

    }


}

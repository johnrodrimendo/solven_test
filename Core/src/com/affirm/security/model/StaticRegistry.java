package com.affirm.security.model;

import com.codahale.metrics.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by jarmando on 11/11/16.
 */
public final class StaticRegistry {
    public static final MetricRegistry metrics = new MetricRegistry();

    public static final Counter appSessionsCounter = metrics.counter("appSessionsCounter");

    public static final Meter appRequestsMeter = metrics.meter("appRequestsMeter");
    public static final Meter appErrorsMeter = metrics.meter("appErrorsMeter");
    public static final Meter appBadCSRFTokenMeter = metrics.meter("appBadCSRFTokenMeter");

    public static final Timer appControllerTime = StaticRegistry.metrics.timer("appControllerTime");


    private StaticRegistry(){}

//    static {
//        wait5Seconds();
//        wait5Seconds();
//        wait5Seconds();
//        wait5Seconds();
//        wait5Seconds();
//        wait5Seconds();
//        startReport();
//    }


    static void startReport() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(20, TimeUnit.SECONDS);
    }

    static void wait5Seconds() {
        try {
            Thread.sleep(5*1000);
        }
        catch(InterruptedException e) {}
    }
}

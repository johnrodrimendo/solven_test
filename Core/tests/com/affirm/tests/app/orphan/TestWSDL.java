package com.affirm.tests.app.orphan;

import com.affirm.common.util.Marshall;
import com.affirm.equifax.HeaderHandlerResolver;
import com.affirm.equifax.ws.*;

/**
 * Created by stbn on 31/10/16.
 */
public class TestWSDL {

    public static void callService() {
        CreditReportPortService creditReportPortService = new CreditReportPortService();
        HeaderHandlerResolver handler = new HeaderHandlerResolver();
        creditReportPortService.setHandlerResolver(handler);
        Endpoint endpoint = creditReportPortService.getCreditReportPort();

        try {

            QueryDataType queryDataType = new QueryDataType();
            queryDataType.setCodigoReporte("600");
            queryDataType.setNumeroDocumento("72153070");
            queryDataType.setTipoDocumento("1");
            queryDataType.setTipoPersona("1");

            ReporteCrediticio reporteCrediticio = endpoint.getReporteOnline(queryDataType);

            if (reporteCrediticio != null) {

                //MARSHALL
                //JAXBContext jc = JAXBContext.newInstance(ReporteCrediticio.class);
                //JAXBContext jc = JAXBContext.newInstance("com.affirm.equifax.ws");

                Marshall marshall = new Marshall();
                String jsonInString = marshall.toJson(reporteCrediticio);

                //String jsonInString =  IOUtils.toString(new FileInputStream(new File("/home/stbn/Descargas/json_07231763.txt")), "UTF-8");

                System.out.println("\nJSON\n" + jsonInString);

                ReporteCrediticio read = marshall.unmarshall(jsonInString, ReporteCrediticio.class);

                for (ReporteCrediticio.Modulos.Modulo mod : read.getModulos().getModulo()) {
                    //   if (mod.getData().getAny() instanceof RiskPredictor) {
                    if (mod.getCodigo().equals("601") && mod.getData().getAny() instanceof RiskPredictor) {
                        if (mod.getData().getAny() != null) {
                            System.out.println("MOD 601" + mod.getData().getAny().getClass());
                        }
                    }

                    if (mod.getCodigo().equals("602") && mod.getData().getAny() instanceof DirectorioPersona) {
                        if (mod.getData().getAny() != null) {

                            System.out.println("MOD 602 " + mod.getData().getAny().getClass());
                            System.out.println("Nombres " + ((DirectorioPersona) mod.getData().getAny()).getNombres());
                        }
                    }
                    if (mod.getCodigo().equals("604") && mod.getData().getAny() instanceof DirectorioSUNAT) {
                        if (mod.getData().getAny() != null) {
                            System.out.println("MOD 604 " + mod.getData().getAny().getClass());

                            for (DirectorioSUNAT.Directorio directorioSUNAT : ((DirectorioSUNAT) mod.getData().getAny()).getDirectorio()) {
                                System.out.println(directorioSUNAT.getNombreComercial());
                            }
                        }
                    }
                }
            } else {
                System.out.println("vacio :(");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void main(String ... vargs) {
        callService();
        //bytesToHex(null);
    }
    /*
    public static void main(String[] args) {
        System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
        callService();

        /*String localhost = "http://localhost:8080";
        String interbank = "https://bancaporinternet.interbank.com.pe/Warhol/login";
        String otro = "";

        String urlString = localhost;
        //Create a default system-wide CookieManager
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        //Open a connection for the given URL
        URL url = null;
        try {
            url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            System.out.println(urlConnection.getContent());
            System.out.println("------------------ Headers ------------------");
            urlConnection.getHeaderFields().forEach( (k, v) -> System.out.println(k+" "+v));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Get CookieStore which is the default internal in-memory
        CookieStore cookieStore = cookieManager.getCookieStore();
        //Retrieve all stored HttpCookies from CookieStore
        List<HttpCookie> cookies = cookieStore.getCookies();
        int cookieIdx = 0;
        //Iterate HttpCookie object
        for (HttpCookie ck : cookies) {
            System.out.println("------------------ Cookie." + ++cookieIdx  + " ------------------");
            System.out.println("Resumen");
            System.out.println(ck.getName()+"="+ck.getValue()+";$Path="+ck.getPath()+ (ck.getSecure()? ";secure;":"") +(ck.isHttpOnly()? ";HttpOnly":"") );
            //Get the cookie name
            System.out.println("Cookie name: " + ck.getName());
            //Get the domain set for the cookie
            System.out.println("Domain: " + ck.getDomain());
            //Get the max age of the cookie
            System.out.println("Max age: " + ck.getMaxAge());
            //Get the path of the server
            System.out.println("Server path: " + ck.getPath());
            //Get boolean if the cookie is being restricted to a secure protocol
            System.out.println("Is secured: " + ck.getSecure());
            //Gets the value of the cookie
            System.out.println("Cookie value: " + ck.getValue());
            //Gets the version of the protocol with which the given cookie is related.
            System.out.println("Cookie protocol version: " + ck.getVersion());
            return;
        }
    }*/
}

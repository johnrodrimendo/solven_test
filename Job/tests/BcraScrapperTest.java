/**
 * Created by solven9 on 01/02/18.
 */

import com.affirm.common.model.transactional.BcraResult;
import com.affirm.jobs.webscrapper.BcraScrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class BcraScrapperTest {


    @Before
    public  void checkEnv(){
        //System.out.println("Env total"+System.getenv());
        System.out.println("Env name : "+System.getenv("HOSTENV"));
        System.out.println("User: "+System.getenv("user.name"));
    }

    @Test
    @Disabled
    public void testScrapping1(){
        System.out.println("testScrapping1");
        BcraScrapper scrapper = new BcraScrapper("fturconi", "ostk2004", 45);
        //Resultado simple
        //20337949232 20035439294  20069277331 20064951239 33508358259 	30517576960 *30502793175
        BcraResult bcraResult = null;
        try {
            bcraResult = scrapper.getData(BcraScrapper.DOC_ARG_CDI, "30502793175");

            System.out.println("Query ID:"+bcraResult.getQueryId());
            System.out.println("Document number:"+bcraResult.getInDocumentNumber());
            System.out.println("Document type:"+bcraResult.getInDocumentType());
            System.out.println("Origin date:"+bcraResult.getOriginDate());

            System.out.println("Numero de Deurores:"+bcraResult.getDeudores().size());
            System.out.println("Numero de Cheques :"+bcraResult.getCheques().size());
            System.out.println("Numero de Historial de deudas :"+bcraResult.getHistorialDeudas().size());

            System.out.println("Existe tabla cheques "+scrapper.isExistChequesTable());
            System.out.println("Existe tabla deudas "+scrapper.isExistDeudoresTable());
            System.out.println("Existe tabla historial "+scrapper.isExistHistorialTable());

            Assert.assertEquals(true,scrapper.isExistChequesTable() );
            Assert.assertEquals(true,scrapper.isExistDeudoresTable() );
            Assert.assertEquals(true,scrapper.isExistHistorialTable());

            scrapper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Disabled
    public void testScrapping2(){
        System.out.println("testScrapping2");
        BcraScrapper scrapper = new BcraScrapper("fturconi", "ostk2004", 45);
        //Resultado simple
        //*20337949232 20035439294  20069277331 20064951239 33508358259 	30517576960 *30502793175
        BcraResult bcraResult = null;
        try {
            bcraResult = scrapper.getData(BcraScrapper.DOC_ARG_CDI, "20337949232");

            System.out.println("Query ID:"+bcraResult.getQueryId());
            System.out.println("Document number:"+bcraResult.getInDocumentNumber());
            System.out.println("Document type:"+bcraResult.getInDocumentType());
            System.out.println("Origin date:"+bcraResult.getOriginDate());

            System.out.println("Numero de Deurores:"+bcraResult.getDeudores().size());
            System.out.println("Numero de Cheques :"+bcraResult.getCheques().size());
            System.out.println("Numero de Historial de deudas :"+bcraResult.getHistorialDeudas().size());

            System.out.println("Existe tabla cheques "+scrapper.isExistChequesTable());
            System.out.println("Existe tabla deudas "+scrapper.isExistDeudoresTable());
            System.out.println("Existe tabla historial "+scrapper.isExistHistorialTable());

            Assert.assertEquals(true,scrapper.isExistChequesTable() );
            //assertEquals(false,scrapper.isExistDeudoresTable() );
            Assert.assertEquals(true,scrapper.isExistHistorialTable());

            scrapper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    @Disabled
    public void testScrapping3(){
        System.out.println("testScrapping3");
        BcraScrapper scrapper = new BcraScrapper("fturconi", "ostk2004", 45);

        BcraResult bcraResult = null;
        try {
            bcraResult = scrapper.getData(BcraScrapper.DOC_ARG_CDI, "20069277331");
            System.out.println("Query ID:"+bcraResult.getQueryId());
            System.out.println("Document number:"+bcraResult.getInDocumentNumber());
            System.out.println("Document type:"+bcraResult.getInDocumentType());
            System.out.println("Origin date:"+bcraResult.getOriginDate());

            System.out.println("Numero de Deurores:"+bcraResult.getDeudores().size());
            System.out.println("Numero de Cheques :"+bcraResult.getCheques().size());
            System.out.println("Numero de Historial de deudas :"+bcraResult.getHistorialDeudas().size());

            System.out.println("Existe tabla cheques "+scrapper.isExistChequesTable());
            System.out.println("Existe tabla deudas "+scrapper.isExistDeudoresTable());
            System.out.println("Existe tabla historial "+scrapper.isExistHistorialTable());

            Assert.assertEquals(true,scrapper.isExistChequesTable() );
            Assert.assertEquals(true,scrapper.isExistDeudoresTable() );
            Assert.assertEquals(true,scrapper.isExistHistorialTable());

            scrapper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Disabled
    public void testScrapping4(){
        System.out.println("testScrapping4");
        BcraScrapper scrapper = new BcraScrapper("fturconi", "ostk2004", 45);
        BcraResult bcraResult = null;

        try {
            //Resultado complejo
            bcraResult = scrapper.getData(BcraScrapper.DOC_ARG_CDI, "30536259194");
            System.out.println("Query ID:"+bcraResult.getQueryId());
            System.out.println("Document number:"+bcraResult.getInDocumentNumber());
            System.out.println("Document type:"+bcraResult.getInDocumentType());
            System.out.println("Origin date:"+bcraResult.getOriginDate());

            System.out.println("Numero de Deurores:"+bcraResult.getDeudores().size());
            System.out.println("Numero de Cheques :"+bcraResult.getCheques().size());
            System.out.println("Numero de Historial de deudas :"+bcraResult.getHistorialDeudas().size());

            System.out.println("Existe tabla cheques "+scrapper.isExistChequesTable());
            System.out.println("Existe tabla deudas "+scrapper.isExistDeudoresTable());
            System.out.println("Existe tabla historial "+scrapper.isExistHistorialTable());

            Assert.assertEquals(true,scrapper.isExistChequesTable() );
            Assert.assertEquals(true,scrapper.isExistDeudoresTable() );
            Assert.assertEquals(true,scrapper.isExistHistorialTable());
            scrapper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    @Disabled
    public void testScrapping5(){
        System.out.println("testScrapping1");
        BcraScrapper scrapper = new BcraScrapper("fturconi", "ostk2004", 45);
        //Resultado simple
        //*20337949232 *20035439294  *20069277331 20064951239 33508358259 	*30517576960 *30502793175
        BcraResult bcraResult = null;
        try {
            bcraResult = scrapper.getData(BcraScrapper.DOC_ARG_CDI, "30517576960");

            System.out.println("Query ID:"+bcraResult.getQueryId());
            System.out.println("Document number:"+bcraResult.getInDocumentNumber());
            System.out.println("Document type:"+bcraResult.getInDocumentType());
            System.out.println("Origin date:"+bcraResult.getOriginDate());

            System.out.println("Numero de Deurores:"+bcraResult.getDeudores().size());
            System.out.println("Numero de Cheques :"+bcraResult.getCheques().size());
            System.out.println("Numero de Historial de deudas :"+bcraResult.getHistorialDeudas().size());

            System.out.println("Existe tabla cheques "+scrapper.isExistChequesTable());
            System.out.println("Existe tabla deudas "+scrapper.isExistDeudoresTable());
            System.out.println("Existe tabla historial "+scrapper.isExistHistorialTable());

            //assertEquals(false,scrapper.isExistChequesTable() );
            //assertEquals(false,scrapper.isExistDeudoresTable() );
            Assert.assertEquals(true,scrapper.isExistHistorialTable());

            scrapper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

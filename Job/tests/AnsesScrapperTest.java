import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.AnsesResult;
import com.affirm.common.service.CatalogService;
import com.affirm.jobs.webscrapper.AnsesScrapper;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


@RunWith(JUnitPlatform.class)
@IncludeEngines("junit-jupiter")
@ContextConfiguration(classes = ConfigurationTest.class)

public class AnsesScrapperTest {

    @Autowired
    CatalogService catalogService;

    @BeforeEach
    public void checkEnv() {
        //System.out.println("Env total"+System.getenv());
        System.out.println("Env name : " + System.getenv("HOSTENV"));
        System.out.println("User: " + System.getenv("user.name"));
    }

    @Test
    @Disabled
    public void testScrapping1() {
        System.out.println("testAnsesScrapping1");

        AnsesResult ansesResult = null;
        try {
            AnsesScrapper scrapper = new AnsesScrapper("fturconi", "ostk2004", 45);
            //EJEMPLO1
            //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "20161987337");

            //EJEMPLO2

            //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "20337949232");
            //
            //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "20299992412");
            //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "27299992417");
            //20266479299

            AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "20180552732");

            System.out.println("result = " + new Gson().toJson(result));
            scrapper.close();

            scrapper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Disabled
    public void testScrappingProxy1() {
        System.out.println("testAnsesScrappingProxy1");

        AnsesResult ansesResult = null;
        try {

            Proxy proxy = catalogService.getRandomProxyByCountry(CountryParam.COUNTRY_ARGENTINA);

            AnsesScrapper scrapper = new AnsesScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy);

            //AnsesScrapper scrapper = new AnsesScrapper("fturconi", "ostk2004", 45);
            //EJEMPLO1
            //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "20161987337");

            //EJEMPLO2

            //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "20337949232");
            //
            //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "20299992412");
            //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "27299992417");
            //20266479299

            AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "20180552732");

            System.out.println("result = " + new Gson().toJson(result));
            scrapper.close();

            scrapper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
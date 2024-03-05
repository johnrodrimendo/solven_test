package com.affirm.tests.service

import com.affirm.client.model.LoanApplicationExtranetRequestPainter
import com.affirm.client.model.LoggedUserEntity
import com.affirm.client.service.EntityExtranetService
import com.affirm.client.service.EntityMarketingCampaignService
import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.model.catalog.Entity
import com.affirm.common.model.catalog.EntityExtranetConfiguration
import com.affirm.common.model.catalog.EntityProduct
import com.affirm.common.model.catalog.EntityProductParams
import com.affirm.common.model.catalog.Product
import com.affirm.common.model.catalog.RateCommissionProduct
import com.affirm.common.model.transactional.ExtranetMenuEntity
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.common.model.transactional.UserEntity
import com.affirm.common.service.BrandingService
import com.affirm.common.service.CatalogService
import com.affirm.common.service.FileService
import com.affirm.common.service.GoogleAnalyticsReportingService
import com.affirm.common.util.AjaxResponse
import com.affirm.entityExt.controller.EntityExtranetFunnelFinController
import com.affirm.entityExt.controller.EntityExtranetLoanEvaluationController
import com.affirm.entityExt.models.form.SendTestCampaignForm
import com.affirm.marketingCampaign.model.MarketingCampaign
import com.affirm.security.dao.SecurityDAO
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseClientConfig
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting
import com.google.api.services.analyticsreporting.v4.model.DateRange
import com.google.api.services.analyticsreporting.v4.model.DimensionFilter
import com.google.api.services.analyticsreporting.v4.model.DimensionFilterClause
import com.google.api.services.analyticsreporting.v4.model.Metric
import com.google.api.services.analyticsreporting.v4.model.Report
import com.google.api.services.analyticsreporting.v4.model.ReportRequest
import com.google.gson.Gson
import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat

@CompileStatic
class EntityExtranetServiceTest extends BaseClientConfig{

    @Autowired
    EntityExtranetService entityExtranetService;
    @Autowired
    CatalogService catalogService;
    @Autowired
    GoogleAnalyticsReportingService googleAnalyticsReportingService;
    @Autowired
    EntityExtranetFunnelFinController entityExtranetFunnelFinController;
    @Autowired
    EntityExtranetLoanEvaluationController entityExtranetLoanEvaluationController;
    @Autowired
    LoanApplicationDAO loanApplicationDAO;
    @Autowired
    FileService fileService;
    @Autowired
    private EntityMarketingCampaignService entityMarketingCampaignService;
    @Autowired
    private SecurityDAO securityDAO;

    @Autowired
    private BrandingService brandingService;


    @Test
    @Disabled
    void testFileUpload() {
        println("hola")
        String projectCode = "1";
        String filePath = "C:/Users/nefi1/Downloads/hotfix.xlsx";
        Path path = Paths.get(filePath);
        String name = filePath.substring(filePath.lastIndexOf("/"));
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (IOException ignored) {
        }
        MultipartFile multipartFile;
        JSONArray validationErrors;
        multipartFile = new MockMultipartFile(name, name, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);

        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (!Arrays.asList("png", "jpeg", "jpg").contains(extension)) {
            println("file no valido")
        }else {
            Map<String, Object> data = new HashMap<String, Object>();
            //LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
            //Get Id ENTITY
            int entityId = 1;


            String nameFile = UUID.randomUUID().toString()+ "-" + multipartFile.getOriginalFilename();
            String endPoint = fileService.writeEntity(multipartFile.getBytes(),entityId,nameFile);;

            data.put("endPoint",endPoint);

            println(data.get("endPoint"));
        }
        }

    @Test
    @Disabled
    void sendTestCampaign() {
        String imgUrl ="https://empresas.blogthinkbig.com/wp-content/uploads/2019/11/Imagen3-245003649.jpg?fit=960%2C720";
        String body = "este es un mensaje de prueba";
        String subject = "prueba test";
        String destination = "920334646";
        char type = 'S';

        JSONObject jsonVariables = new JSONObject();
        jsonVariables.put("IMG_URL", imgUrl);
        jsonVariables.put("body", body);


        EntityExtranetConfiguration entityExtranetConfiguration = brandingService.getExtranetBrandingAsJson(26);

        if (type.equals(MarketingCampaign.EMAIL))
            entityMarketingCampaignService.sendTestEmail(
                    null,
                    entityMarketingCampaignService.getSenderEmailToUse(entityExtranetConfiguration.getMarketingCampaignConfiguration()),
                    null,
                    destination,
                    null,
                    subject,
                    body,
                    body,
                    entityExtranetConfiguration.getMarketingCampaignConfiguration().getMarketingCampaignAwsTemplate(),
                    null,
                    jsonVariables,
                    null,
                    null);


        if(type.equals(MarketingCampaign.SMS))
            entityMarketingCampaignService.sendTestSMS(destination,body, null);

        entityMarketingCampaignService.insertTestCampaign(1, 1, type);
    }

    @Test
    void createTemplate() {
        String urlImg ="https://empresas.blogthinkbig.com/wp-content/uploads/2019/11/Imagen3-245003649.jpg?fit=960%2C720";
        String name = "este es un mensaje de prueba";
        String body = "este es un mensaje de prueba";
        String subject = "prueba test";
        Integer template_id = null;
        char type = 'S';

        int entityId = 1;
        int entityUserId = 1;
        entityMarketingCampaignService.insertCampaignTemplate(name,type,template_id,entityId,entityUserId,subject,body,urlImg,true,true);
    }

    @Test
    @Disabled
    void createPasswordLink(){
        List<String> emails = new ArrayList<>();
//        emails.add("maria.nunezborja@bancoazteca.com.pe");
//        emails.add("jayaypoma@bancoazteca.com.pe");
//        emails.add("mrobless@bancoazteca.com.pe");
//        emails.add("luis.geldres@bancoazteca.com.pe");
//        emails.add("lizeth.ocana@bancoazteca.com.pe");
//        emails.add("malvaradoh@bancoazteca.com.pe");
//        emails.add("mleon@bancoazteca.com.pe");
//        emails.add("evargasm@elektra.com.mx");
//        emails.add("yquispe@bancoazteca.com.pe");
//        emails.add("oscar.vega@bancoazteca.com.pe");
//        emails.add("victor.ramosc@bancoazteca.com.pe");
//        emails.add("Ivonne.mauricio@bancoazteca.com.pe");
//        emails.add("Ricardo.cortez@elektra.com.mx");
//        emails.add("cespinozas@bancoazteca.com.pe");
//        emails.add("tatiana.socola@bancoazteca.com.pe");
//        emails.add("jpasco@bancoazteca.com.pe");
//        emails.add("moises.godenzi@bancoazteca.com.pe");
//        emails.add("oscar.vega@bancoazteca.com.pe");
////        emails.add("Alvaro.otero@bancoazteca.com.pe");
//        emails.add("maxgubetta@gmail.com");
//        emails.add("sanchezalfredo1095@gmail.com");
        emails.add("RMASELLI@banbif.com.pe");

        Integer entity = Entity.BANBIF;
        for(String email : emails){
            String link = entityExtranetService.generateResetLink(email, entity, 48);
            System.out.println(email + " -> " + link);
        }


    }

    @Test
    @Disabled
    void getUsuariosVisitas() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        AnalyticsReporting service = googleAnalyticsReportingService.initializeAnalyticsReporting();
        println entityExtranetFunnelFinController.getUsuariosVisitas(sdf.parse("25/11/2022"), sdf.parse("26/12/2022"), service, 26, null,8);
    }


    @Test
    @Disabled
    void migrateUsersData() {

        List<Integer> entitiesId = Arrays.asList(Entity.ACCESO,Entity.BANBIF,Entity.FINANSOL,Entity.PRISMA,Entity.AZTECA);
        entityExtranetService.migrateUserData(entitiesId);
    }

}

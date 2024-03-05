package com.affirm.entityExt.controller;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.client.service.EntityMarketingCampaignService;
import com.affirm.common.dao.BasesDAO;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.EntityExtranetDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.entityExt.models.FieldsEntityBaseEmail;
import com.affirm.entityExt.models.PaginatorTableFilterForm;
import com.affirm.entityExt.models.form.*;
import com.affirm.marketingCampaign.dao.MarketingCampaignDAO;
import com.affirm.marketingCampaign.model.MarketingCampaign;
import com.affirm.marketingCampaign.model.MarketingCampaignExtranetPainter;
import com.affirm.marketingCampaign.model.TemplateCampaign;
import com.google.gson.Gson;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller("entityExtranetMarketingCampaignController")
public class EntityExtranetMarketingCampaignController {

    public final static String URL = "marketingCampaign";

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private EntityExtranetService entityExtranetService;
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private EntityExtranetDAO entityExtranetDAO;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private MarketingCampaignDAO marketingCampaignDAO;
    @Autowired
    private BrandingService brandingService;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private FileService fileService;
    @Autowired
    private EntityMarketingCampaignService entityMarketingCampaignService;
    @Autowired
    private BasesDAO basesDAO;
    @Autowired
    private UtilService utilService;
    @Autowired
    private LoanApplicationService loanApplicationService;


    @RequestMapping(value = URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object communicationsView(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        int entityId = entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId();

        List<MarketingCampaignExtranetPainter> data = marketingCampaignDAO.getMarketingCampaignExtranet(entityId,
                null,
                null,
                null,
                PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, 0,
                locale);

        Pair<Integer, Double> countAdSum = marketingCampaignDAO.getMarketingCampaignExtranetCount(entityId,
                null,
                null,
                null,
                locale);

        EntityGatewayBaseDetail entityGatewayBaseDetail = entityExtranetDAO.getEntityCOllectionBaseDetail(entityId);

        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("data", data);
        model.addAttribute("page", "marketingCampaign");
        model.addAttribute("title", "Campaña");
        model.addAttribute("baseDetail", entityGatewayBaseDetail);
        model.addAttribute("date", new Date());

        return new ModelAndView("/entityExtranet/extranetMarketingCampaign");
    }

    @RequestMapping(value = URL + "/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object getCommunications(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if (filter != null) limit = filter.getLimit();
        if (filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if (filter != null) {
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();

        List<MarketingCampaignExtranetPainter> data = marketingCampaignDAO.getMarketingCampaignExtranet(entityId,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                limit, offset,
                locale);

        Pair<Integer, Double> countAdSum = marketingCampaignDAO.getMarketingCampaignExtranetCount(entityId,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                locale);

        if (offset < 0) offset = 0;
        model.addAttribute("offset", offset);
        model.addAttribute("data", data);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("page", "communications");
        model.addAttribute("title", "Campaña");

        return new ModelAndView("/entityExtranet/extranetMarketingCampaign :: list");
    }

    @RequestMapping(value = URL + "/count", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", type = RequiresPermissionOr403.Type.WEB, webApp = WebApplication.ENTITY_EXTRANET)
    public Object getCommunicationsCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if (filter != null) {
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        Pair<Integer, Double> countAdSum = marketingCampaignDAO.getMarketingCampaignExtranetCount(entityId,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                locale);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count", countAdSum.getLeft());

        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = URL + "/configuration", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", type = RequiresPermissionOr403.Type.WEB, webApp = WebApplication.ENTITY_EXTRANET)
    public Object getCommunicationsConfigurationModal(
            ModelMap model, Locale locale) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();
        Map<String, Object> data = new HashMap<String, Object>();

        UpdateMarketingConfiguration updateMarketingConfiguration = new UpdateMarketingConfiguration();

        EntityExtranetConfiguration entityExtranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId, true);
        if (entityExtranetConfiguration != null && entityExtranetConfiguration.getMarketingCampaignConfiguration() != null) {
            EntityExtranetConfiguration.MarketingCampaignConfiguration marketingCampaignConfiguration = entityExtranetConfiguration.getMarketingCampaignConfiguration();
            updateMarketingConfiguration.setSenderEmail(marketingCampaignConfiguration.getEmail());
            updateMarketingConfiguration.setSenderName(marketingCampaignConfiguration.getSenderName());
            model.addAttribute("showEmailConfiguration",marketingCampaignConfiguration.getAvailableTypes() != null && marketingCampaignConfiguration.getAvailableTypes().stream().anyMatch(e -> e.getType().equalsIgnoreCase(EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType.EMAIL_TYPE)));
            model.addAttribute("showSMSConfiguration",marketingCampaignConfiguration.getAvailableTypes() != null && marketingCampaignConfiguration.getAvailableTypes().stream().anyMatch(e -> e.getType().equalsIgnoreCase(EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType.SMS_TYPE)));
            model.addAttribute("smsSolvenCost",marketingCampaignConfiguration.getSmsSolvenCost());
            model.addAttribute("emailSolvenCost",marketingCampaignConfiguration.getEmailSolvenCost());

            EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType smsType = marketingCampaignConfiguration.getAvailableTypes().stream().filter(e -> e.getType().equalsIgnoreCase(EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType.SMS_TYPE)).findFirst().orElse(null);
            updateMarketingConfiguration.setSmsFollowUp(smsType != null && smsType.getSendingTypeFollowUp());
            updateMarketingConfiguration.setSmsOnDemand(smsType != null && smsType.getSendingTypeOnDemand());
            updateMarketingConfiguration.setSmsServiceType(smsType != null ? smsType.getServiceType() : null);

            EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType emailType = marketingCampaignConfiguration.getAvailableTypes().stream().filter(e -> e.getType().equalsIgnoreCase(EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType.EMAIL_TYPE)).findFirst().orElse(null);
            updateMarketingConfiguration.setEmailFollowUp(emailType != null && emailType.getSendingTypeFollowUp());
            updateMarketingConfiguration.setEmailOnDemand(emailType != null && emailType.getSendingTypeOnDemand());

            if (updateMarketingConfiguration.getSenderEmail() != null)
                updateMarketingConfiguration.setStatus(awsSesEmailService.getVerificationEmailStatus(updateMarketingConfiguration.getSenderEmail()));
        }

        model.addAttribute("form", updateMarketingConfiguration);

        return new ModelAndView("/entityExtranet/extranetMarketingCampaign :: configurationModal");

    }

    @RequestMapping(value = URL + "/configuration", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    public Object updateCommunicationsConfiguration(
            ModelMap model, Locale locale,
            @RequestParam(value = "field") String field, UpdateMarketingConfiguration form) throws Exception {


        if (form == null) {
            return AjaxResponse.errorMessage("No hay datos enviados");
        }

        if (field == null) return AjaxResponse.errorMessage("Campo desconocido");

        switch (field) {
            case "senderName":
                ((UpdateMarketingConfiguration.UpdateMarketingConfigurationFormValidator) form.getValidator()).senderName.setRequired(true);
                ((UpdateMarketingConfiguration.UpdateMarketingConfigurationFormValidator) form.getValidator()).senderEmail.setRequired(false);
                break;
            case "senderEmail":
                ((UpdateMarketingConfiguration.UpdateMarketingConfigurationFormValidator) form.getValidator()).senderName.setRequired(false);
                ((UpdateMarketingConfiguration.UpdateMarketingConfigurationFormValidator) form.getValidator()).senderEmail.setRequired(true);
                break;
            default:
                break;
        }
        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();

        Map<String, Object> data = new HashMap<String, Object>();

        EntityExtranetConfiguration entityExtranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId, true);
        if (entityExtranetConfiguration.getMarketingCampaignConfiguration() == null)
            entityExtranetConfiguration.setMarketingCampaignConfiguration(new EntityExtranetConfiguration.MarketingCampaignConfiguration());

        EntityExtranetConfiguration.MarketingCampaignConfiguration marketingCampaignConfiguration = entityExtranetConfiguration.getMarketingCampaignConfiguration();

        boolean showEmailConfiguration = marketingCampaignConfiguration.getAvailableTypes() != null && marketingCampaignConfiguration.getAvailableTypes().stream().anyMatch(e -> e.getType().equalsIgnoreCase(EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType.EMAIL_TYPE));

        if(!showEmailConfiguration) return AjaxResponse.errorMessage("No puede realizar esta acción");

        Boolean sendValidation = false;

        EntityExtranetConfiguration.MarketingCampaignConfigurationLog marketingCampaignConfigurationLog = new EntityExtranetConfiguration.MarketingCampaignConfigurationLog();
        if (marketingCampaignConfiguration.getEmail() == null || !marketingCampaignConfiguration.getEmail().equalsIgnoreCase(form.getSenderEmail()))
            sendValidation = true;
        marketingCampaignConfigurationLog.setEmail(marketingCampaignConfiguration.getEmail());
        marketingCampaignConfigurationLog.setSenderName(marketingCampaignConfiguration.getSenderName());
        if (marketingCampaignConfiguration.getLogs() == null) marketingCampaignConfiguration.setLogs(new ArrayList<>());
        marketingCampaignConfiguration.getLogs().add(marketingCampaignConfigurationLog);
        marketingCampaignConfiguration.setEmail(form.getSenderEmail());
        marketingCampaignConfiguration.setSenderName(form.getSenderName());

        entityExtranetConfiguration.setMarketingCampaignConfiguration(marketingCampaignConfiguration);

        catalogService.updateEntityBrandingExtranetConfiguration(entityId, entityExtranetConfiguration);

        if (sendValidation && form.getSenderEmail() != null)
            awsSesEmailService.sendEmailVerification(form.getSenderEmail());

        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = URL + "/upload-image", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    public Object uploadImage(@RequestParam("file") MultipartFile file) throws Exception {

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!Arrays.asList("png", "jpeg", "jpg").contains(extension)) {
            return AjaxResponse.errorMessage("Formato de archivo no valido");
        }
        Map<String, Object> data = new HashMap<String, Object>();
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        //Get Id ENTITY
        int entityId = loggedUserEntity.getPrincipalEntity().getId();


        String nameFile = UUID.randomUUID().toString() + "-" + Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFD).replaceAll(" ","-").replaceAll(" ","-").replaceAll("[^\\p{ASCII}]", "").replaceAll("/[`~!@#$%^&*()_|+=?;:'\",.<>\\{\\}\\[\\]\\\\\\/]/gi", "");
        String endPoint = fileService.writeEntity(file.getBytes(), entityId, nameFile);


        data.put("fileUrl", endPoint);

        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = URL + "/send_verification", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    public Object sendVerification(
            ModelMap model, Locale locale) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();

        Map<String, Object> data = new HashMap<String, Object>();

        EntityExtranetConfiguration entityExtranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId, true);

        if (entityExtranetConfiguration.getMarketingCampaignConfiguration() == null)
            entityExtranetConfiguration.setMarketingCampaignConfiguration(new EntityExtranetConfiguration.MarketingCampaignConfiguration());

        EntityExtranetConfiguration.MarketingCampaignConfiguration marketingCampaignConfiguration = entityExtranetConfiguration.getMarketingCampaignConfiguration();

        if (marketingCampaignConfiguration.getEmail() == null)
            return AjaxResponse.errorMessage("Debe indicar un correo electrónico");

        String status = awsSesEmailService.getVerificationEmailStatus(marketingCampaignConfiguration.getEmail());

        if (status.equalsIgnoreCase("Success"))
            return AjaxResponse.errorMessage("El correo electrónico ya se encuentra verificado");

        awsSesEmailService.sendEmailVerification(marketingCampaignConfiguration.getEmail());

        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = URL + "/create-template", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    public Object createTemplate(ModelMap model, Locale locale, CreateTemplateForm form) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        if(form.getType().equals(MarketingCampaign.EMAIL)){
            Boolean isValidUrl = utilService.validateUrl(form.getHeaderImg());

            if(!isValidUrl) return AjaxResponse.errorMessage("Debe indicar una imagen de cabecera");
        }

        Map<String, Object> data = new HashMap<String, Object>();

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();
        int entityUserId = loggedUserEntity.getId();

        EntityExtranetConfiguration entityExtranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId, true);
        if(!entityMarketingCampaignService.canCreateByAvailableType(form.getType(),entityExtranetConfiguration.getMarketingCampaignConfiguration().getAvailableTypes())) return AjaxResponse.errorMessage("No puede crear campaña con el método de envío indicado");

        entityMarketingCampaignService.insertCampaignTemplate(form.getName(), form.getType(), form.getParentTemplateId(), entityId,entityUserId,form.getSubject(), form.getBody(), form.getHeaderImg(), true,true);
        return AjaxResponse.ok(new Gson().toJson(data));
    }


    @RequestMapping(value = URL + "/send-test-campaign", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    public Object sendTestCampaign(ModelMap model, Locale locale, SendTestCampaignForm form) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        if(form.getType().equals(MarketingCampaign.EMAIL)){
            Boolean isValidUrl = utilService.validateUrl(form.getHeaderImg());

            if(!isValidUrl) return AjaxResponse.errorMessage("Debe indicar una imagen de cabecera");
        }

        Map<String, Object> data = new HashMap<String, Object>();

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();
        EntityExtranetConfiguration entityExtranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId, true);

        if(!entityMarketingCampaignService.canCreateByAvailableType(form.getType(),entityExtranetConfiguration.getMarketingCampaignConfiguration().getAvailableTypes())) return AjaxResponse.errorMessage("No puede realizar pruebas de campañas con el método de envío indicado");

        Map<String, String> jsonVars =  null;

        if(entityId == Entity.AZTECA){
            AztecaGetawayBase aztecaGetawayBase = new AztecaGetawayBase();
            aztecaGetawayBase.setCelular1("999999999");
            aztecaGetawayBase.setCelular2("999999999");
            aztecaGetawayBase.setCelular3("999999999");
            aztecaGetawayBase.setCelular4("999999999");
            aztecaGetawayBase.setCorreo1("prueba@azteca.com");
            aztecaGetawayBase.setCorreo2("prueba@azteca.com");
            aztecaGetawayBase.setDepartamento("LIMAPrueba");
            aztecaGetawayBase.setDistrito("LIMAPrueba");
            aztecaGetawayBase.setProvincia("LIMAPrueba");
            aztecaGetawayBase.setPais("PeruPrueba");
            aztecaGetawayBase.setNombre("NombrePrueba");
            aztecaGetawayBase.setApPaterno("ApPaternoPrueba");
            aztecaGetawayBase.setApMaterno("ApMaternoPrueba");
            aztecaGetawayBase.setTipoDocumento("DNIPrueba");
            aztecaGetawayBase.setSaldoCapital(999.0);
            aztecaGetawayBase.setSaldoInteres(999.0);
            aztecaGetawayBase.setSaldoMoratorio(999.0);
            aztecaGetawayBase.setSaldoTotal(999.0);
            aztecaGetawayBase.setMontoCampania(999.0);
            aztecaGetawayBase.setCodigoClienteExterno("999");
            aztecaGetawayBase.setDiasAtrazo(999);
            aztecaGetawayBase.setNumeroDocumento("999999999");
            aztecaGetawayBase.setVencimientoCampania(new Date());
            jsonVars = entityMarketingCampaignService.fillJsonWithAztecaCobranzaBase(aztecaGetawayBase);
        }

        JSONObject jsonVariables = new JSONObject();
        if (jsonVars != null) {
            for (Map.Entry<String, String> entry : jsonVars.entrySet()) {
                jsonVariables.put(entry.getKey(), entry.getValue());
            }
        }

        EntityBranding entityBranding = catalogService.getEntityBranding(entityId);

        MarketingCampaign marketingCampaign = new MarketingCampaign();
        marketingCampaign.setType(form.getType());
        String linkToChange = loanApplicationService.generateLandingLinkEntity(entityBranding.getEntity().getId(),ProductCategory.GATEWAY);


        if(linkToChange != null) linkToChange = linkToChange.concat(entityMarketingCampaignService.additionalParamsLinkCampaign(marketingCampaign));

        jsonVariables.put("LINK",linkToChange);

        if (form.getType().equals(MarketingCampaign.EMAIL)) {

            jsonVariables.put("IMG_URL", form.getHeaderImg());
            jsonVariables.put("body", form.getBody());

            entityMarketingCampaignService.sendTestEmail(
                    null,
                    entityMarketingCampaignService.getSenderEmailToUse(entityExtranetConfiguration.getMarketingCampaignConfiguration()),
                    null,
                    form.getDestination(),
                    null,
                    form.getSubject(),
                    form.getBody(),
                    form.getBody(),
                    entityExtranetConfiguration.getMarketingCampaignConfiguration().getMarketingCampaignAwsTemplate(),
                    null,
                    jsonVariables,
                    null,
                    null);
        }

        if (form.getType().equals(MarketingCampaign.SMS))
            entityMarketingCampaignService.sendTestSMS(form.getDestination(), form.getBody(), jsonVariables);

        entityMarketingCampaignService.insertTestCampaign(entityId, loggedUserEntity.getId(), form.getType());

        return AjaxResponse.ok(new Gson().toJson(data));

    }

    @RequestMapping(value = URL + "/campaign", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", type = RequiresPermissionOr403.Type.AJAX)
    public Object getCommunicationsCampaignModal(
            ModelMap model, Locale locale, @RequestParam(value = "campaignId", required = false) Integer campaignId) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();
        Map<String, Object> data = new HashMap<String, Object>();

        List<FieldsEntityBaseEmail> fieldsEntityBaseEmail = new ArrayList<>();

        List<String> baseFields = entityMarketingCampaignService.getBaseFields(ProductCategory.GATEWAY, entityId);

        EntityExtranetConfiguration entityExtranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId);

        for (String baseField : baseFields) {
            FieldsEntityBaseEmail field = new FieldsEntityBaseEmail();
            field.setValue(baseField);
            field.setKey("%" + baseField + "%");
            fieldsEntityBaseEmail.add(field);
        }

        EntityBranding entityBranding = catalogService.getEntityBranding(entityId);

        FieldsEntityBaseEmail field = new FieldsEntityBaseEmail();
        field.setValue("BOTÓN");
        field.setKey("%LINK%");
        field.setHtmlTemplate("<div style=text-align:center><a href=\"%LINK%\" style=\"background:%BUTTON_COLOR%;border-radius:3px;color:white !important;display:inline-block;padding:.75em 2em;margin:1em auto;text-decoration:none !important;text-transform:uppercase\">Ingresa aquí</a></div>".replaceAll("%BUTTON_COLOR%", entityBranding.getEntityPrimaryColor()));
        fieldsEntityBaseEmail.add(field);

        EntityExtranetConfiguration.MarketingCampaignConfiguration marketingCampaignConfiguration = entityExtranetConfiguration != null ? entityExtranetConfiguration.getMarketingCampaignConfiguration() : null;

        model.addAttribute("showEmailOption",marketingCampaignConfiguration != null && marketingCampaignConfiguration.getAvailableTypes() != null && marketingCampaignConfiguration.getAvailableTypes().stream().anyMatch(e -> e.getType().equalsIgnoreCase(EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType.EMAIL_TYPE)) && marketingCampaignConfiguration.getAvailableTypes().stream().filter(e -> e.getType().equalsIgnoreCase(EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType.EMAIL_TYPE)).findFirst().orElse(null).getSendingTypeOnDemand());
        model.addAttribute("showSMSOption",marketingCampaignConfiguration != null && marketingCampaignConfiguration.getAvailableTypes() != null && marketingCampaignConfiguration.getAvailableTypes().stream().anyMatch(e -> e.getType().equalsIgnoreCase(EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType.SMS_TYPE)) && marketingCampaignConfiguration.getAvailableTypes().stream().filter(e -> e.getType().equalsIgnoreCase(EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType.SMS_TYPE)).findFirst().orElse(null).getSendingTypeOnDemand());

        if(campaignId != null){
            MarketingCampaign marketingCampaign = marketingCampaignDAO.getMarketingCampaign(campaignId, locale);
            if(marketingCampaign != null && marketingCampaign.getEntity().getId() != entityId) return AjaxResponse.errorMessage("No tiene acceso a esta campaña");
            if(marketingCampaign != null){
                model.addAttribute("marketingCampaign", marketingCampaign);
            }
        }

        model.addAttribute("baseFields", fieldsEntityBaseEmail);

        model.addAttribute("templateList", marketingCampaignDAO.getCampaignTemplateExtranet(entityId,locale));

        model.addAttribute("form",new ValidateCampaignOrTemplateForm());

        return new ModelAndView("/entityExtranet/extranetMarketingCampaign :: campaignModal");

    }

    @RequestMapping(value = URL+"/template", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", type = RequiresPermissionOr403.Type.AJAX)
    public Object getTemplateDetail(
            ModelMap model, Locale locale, @RequestParam(value = "templateId") Integer templateId) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();

        TemplateCampaign templateCampaign = marketingCampaignDAO.getCampaignTemplateExtranetById(templateId,locale);

        if(templateCampaign != null && templateCampaign.getEntity().getId() != entityId) return AjaxResponse.errorMessage("No tiene acceso a esta plantilla");

        return AjaxResponse.ok(new Gson().toJson(templateCampaign));

    }


    @RequestMapping(value = URL + "/receivers", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", type = RequiresPermissionOr403.Type.WEB)
    public Object getReceiverModal(
                ModelMap model, Locale locale, @RequestParam(value = "type") Character type) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();
        Integer count = basesDAO.getAztecaCobranzaPhonesCount();
        BaseCount baseCount = entityExtranetDAO.getBaseCount(entityId, type, locale);

        model.addAttribute("count", count);
        model.addAttribute("countUsers", count);
        model.addAttribute("type", type);
        model.addAttribute("baseCount", baseCount);

        return new ModelAndView("/entityExtranet/extranetMarketingCampaign :: destinationModal");

    }

    @RequestMapping(value = URL + "/campaign/create", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    public Object createCampaign( ModelMap model, Locale locale, CreateCampaignForm form) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        if(form.getType().equals(MarketingCampaign.EMAIL)){
            Boolean isValidUrl = utilService.validateUrl(form.getHeaderImg());

            if(!isValidUrl) return AjaxResponse.errorMessage("Debe indicar una imagen de cabecera");
        }


        Map<String, Object> data = new HashMap<String, Object>();

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();
        EntityExtranetConfiguration entityExtranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId, true);

        if(!entityMarketingCampaignService.canCreateByAvailableType(form.getType(),entityExtranetConfiguration.getMarketingCampaignConfiguration().getAvailableTypes())) return AjaxResponse.errorMessage("No puede crear campaña con el método de envío indicado");

        TemplateCampaign templateCampaign = null;

        Integer templateId = marketingCampaignDAO.insertCampaignTemplate(form.getTemplateName(),form.getType(),form.getParentTemplateId(),entityId,loggedUserEntity.getId(),form.getSubject(),form.getBody(),form.getHeaderImg(),true,false);
        if(templateId != null) templateCampaign = marketingCampaignDAO.getCampaignTemplateExtranetById(templateId,locale);
        GatewayBaseEvent gatewayBaseEvent = basesDAO.getLastCollectionBaseEventByType(entityId, GatewayBaseEvent.TYPE_BASE);

        Integer marketingCampaignId =  marketingCampaignDAO.createMarketingCampaign(form.getCampaignName(),form.getType(),MarketingCampaign.PENDING_STATUS,templateId,entityId,loggedUserEntity.getId(),null, gatewayBaseEvent != null ? gatewayBaseEvent.getId() : null,form.getReceiverType(),templateCampaign,entityExtranetConfiguration.getMarketingCampaignConfiguration());

        QueryBot queryBot = null;

        if(queryBot != null) marketingCampaignDAO.updateMarketingCampaignQueryBotId(marketingCampaignId,queryBot.getId());


        return AjaxResponse.ok(new Gson().toJson(data));

    }

    @RequestMapping(value = URL + "/validate/form", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    public Object validateForm( ModelMap model, Locale locale, ValidateCampaignOrTemplateForm form) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        if(form.getType().equals(MarketingCampaign.EMAIL)){
            Boolean isValidUrl = utilService.validateUrl(form.getHeaderImg());
            if(!isValidUrl) return AjaxResponse.errorMessage("Debe indicar una imagen de cabecera");
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();

        EntityExtranetConfiguration entityExtranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId, true);
        if(!entityMarketingCampaignService.canCreateByAvailableType(form.getType(),entityExtranetConfiguration.getMarketingCampaignConfiguration().getAvailableTypes())) return AjaxResponse.errorMessage("No puede crear plantilla/campaña con el método de envío indicado");

        Map<String, Object> data = new HashMap<String, Object>();

        return AjaxResponse.ok(new Gson().toJson(data));

    }


    @RequestMapping(value = URL + "/campaign/resend", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:marketingCampaign:view", saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    public Object resendCampaign( ModelMap model, Locale locale, @RequestParam("marketingCampaignId") Integer marketingCampaignId) throws Exception {

        MarketingCampaign marketingCampaign = marketingCampaignDAO.getMarketingCampaign(marketingCampaignId,locale);

        if(marketingCampaign == null) return AjaxResponse.errorMessage("La campaña no pudo ser encontrada");

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();

        if(marketingCampaign != null && marketingCampaign.getEntity().getId() != entityId) return AjaxResponse.errorMessage("No tiene acceso a esta campaña");

        GatewayBaseEvent gatewayBaseEvent = basesDAO.getLastCollectionBaseEventByType(entityId, GatewayBaseEvent.TYPE_BASE);

        if(gatewayBaseEvent != null && !gatewayBaseEvent.getId().equals(marketingCampaign.getCollectionBaseId())) return AjaxResponse.errorMessage("No se puede reenviar esta campaña. La base ha sido modificada.");

        EntityExtranetConfiguration entityExtranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId, true);

        if(!entityMarketingCampaignService.canCreateByAvailableType(marketingCampaign.getType(),entityExtranetConfiguration.getMarketingCampaignConfiguration().getAvailableTypes())) return AjaxResponse.errorMessage("No puede crear campaña con el método de envío indicado");

        Integer marketingCampaignIdResend =  marketingCampaignDAO.createMarketingCampaign(marketingCampaign.getName(),marketingCampaign.getType(),MarketingCampaign.PENDING_STATUS,marketingCampaign.getCampaignTemplateId(),entityId,loggedUserEntity.getId(),null,marketingCampaign.getCollectionBaseId(),marketingCampaign.getReceiverType(),marketingCampaign.getJsTemplate(),entityExtranetConfiguration.getMarketingCampaignConfiguration());

        QueryBot queryBot = null;

        if(queryBot != null) marketingCampaignDAO.updateMarketingCampaignQueryBotId(marketingCampaignIdResend,queryBot.getId());

        Map<String, Object> data = new HashMap<String, Object>();

        return AjaxResponse.ok(new Gson().toJson(data));

    }


}

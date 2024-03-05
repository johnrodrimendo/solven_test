package com.affirm.client.service.impl;

import com.affirm.client.service.EntityExtranetService;
import com.affirm.client.service.EntityMarketingCampaignService;
import com.affirm.common.model.catalog.EntityExtranetConfiguration;
import com.affirm.common.model.transactional.Attachment;
import com.affirm.common.model.transactional.AztecaGetawayBase;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.BrandingService;
import com.affirm.common.service.UtilService;
import com.affirm.intico.service.impl.InticoClientServiceImpl;
import com.affirm.marketingCampaign.dao.MarketingCampaignDAO;
import com.affirm.marketingCampaign.model.MarketingCampaign;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.catalog.Entity;

import java.util.*;

@Service("entityMarketingCampaignService")
public class EntityMarketingCampaignServiceImpl implements EntityMarketingCampaignService {

    @Autowired
    private AwsSesEmailService awsSesEmailService;

    @Autowired
    private InticoClientServiceImpl inticoClientService;

    @Autowired
    private EntityExtranetService entityExtranetService;

    @Autowired
    private BrandingService brandingService;

    @Autowired
    private MarketingCampaignDAO marketingCampaignDAO;

    @Autowired
    private UtilService utilService;

    @Override
    public Object sendTestEmail(Integer personInteractionId,
                                String from,
                                String fromName,
                                String to,
                                String[] cc,
                                String subject,
                                String plainMessage,
                                String htmlMessage,
                                String templateId,
                                List<Attachment> attached,
                                JSONObject jsonVariables,
                                String hourOfDay,
                                Map<String,
                                        String> mapTags) throws Exception {

        plainMessage = replaceVariables(plainMessage, jsonVariables);
        htmlMessage = replaceVariables(plainMessage, jsonVariables);

        return awsSesEmailService.sendRawEmail(personInteractionId, from, fromName, to, cc, subject, plainMessage, htmlMessage, templateId, attached, jsonVariables, hourOfDay, mapTags);

    }

    @Override
    public Integer insertTestCampaign(Integer idEntity, Integer idEntityUser, Character type) throws Exception {
        return marketingCampaignDAO.insertTestCampaign(idEntity, idEntityUser, type);
    }

    @Override
    public Integer insertCampaignTemplate(String name, Character type, Integer parent_campaign_template_id, Integer entity_id, Integer entity_user_id, String subject, String body, String header_img, Boolean is_active, Boolean is_saved) throws Exception {
        return marketingCampaignDAO.insertCampaignTemplate(name, type, parent_campaign_template_id, entity_id, entity_user_id, subject, body, header_img, is_active, is_saved);
    }

    @Override
    public Object sendTestSMS(String destination, String body, JSONObject jsonVars) throws Exception {
        body = replaceVariables(body, jsonVars);
        return inticoClientService.sendInticoSms(destination, body);
    }

    @Override
    public String getSenderEmailToUse(EntityExtranetConfiguration.MarketingCampaignConfiguration marketingCampaignConfiguration) throws Exception {

        String EMAIL_DEFAULT = "hola@solven.pe";

        if (marketingCampaignConfiguration == null || marketingCampaignConfiguration.getEmail() == null)
            return EMAIL_DEFAULT;

        String status = awsSesEmailService.getVerificationEmailStatus(marketingCampaignConfiguration.getEmail());

        if (status.equalsIgnoreCase("Success")) {
            return marketingCampaignConfiguration.getEmail();
        } else {
            return EMAIL_DEFAULT;
        }

    }

    @Override
    public List<String> getBaseFields(Integer productCategory, Integer entityId) {
        if (productCategory == null) return new ArrayList<>();
        switch (productCategory) {
            case ProductCategory.GATEWAY:
                switch (entityId) {
                    case Entity.AZTECA:
                        return Arrays.asList("NUMERO_DOCUMENTO", "NOMBRE", "AP_PATERNO", "AP_MATERNO", "SALDO_CAPITAL", "SALDO_INTERES", "SALDO_MORATORIO", "SALDO_TOTAL", "DIAS_ATRASO", "MONTO_CAMPANIA", "VENCIMIENTO_CAMPANIA", "CODIGO_CLIENTE_EXTERNO");
                }
                break;
        }
        return new ArrayList<>();
    }

    @Override
    public Boolean canCreateByAvailableType(Character type, List<EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType> availableTypes) {
        if(type == null || availableTypes == null || availableTypes.isEmpty()) return false;
        String typeToCompare = "";
        if(type == MarketingCampaign.EMAIL) typeToCompare = EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType.EMAIL_TYPE;
        else if(type == MarketingCampaign.SMS) typeToCompare = EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType.SMS_TYPE;
        String finalTypeToCompare = typeToCompare;
        return availableTypes.stream().anyMatch(e -> e.getType().equalsIgnoreCase(finalTypeToCompare)) && availableTypes.stream().filter(e -> e.getType().equalsIgnoreCase(finalTypeToCompare)).findFirst().orElse(null).getSendingTypeOnDemand();
    }

    @Override
    public Map<String, String>  fillJsonWithAztecaCobranzaBase(AztecaGetawayBase aztecaGetawayBase) {
        Map<String, String> templateVars = new HashMap<>();
        templateVars.put("PAIS", aztecaGetawayBase.getPais());
        templateVars.put("TIPO_DOCUMENTO", aztecaGetawayBase.getTipoDocumento());
        templateVars.put("NUMERO_DOCUMENTO", aztecaGetawayBase.getNumeroDocumento());
        templateVars.put("NOMBRE", aztecaGetawayBase.getNombre());
        templateVars.put("AP_PATERNO", aztecaGetawayBase.getApPaterno());
        templateVars.put("AP_MATERNO", aztecaGetawayBase.getApMaterno());
        templateVars.put("CELULAR_1", aztecaGetawayBase.getCelular1());
        templateVars.put("CELULAR_2", aztecaGetawayBase.getCelular2());
        templateVars.put("CELULAR_3", aztecaGetawayBase.getCelular3());
        templateVars.put("CELULAR_4", aztecaGetawayBase.getCelular4());
        templateVars.put("CELULAR_5", aztecaGetawayBase.getCelular5());
        templateVars.put("SALDO_CAPITAL", aztecaGetawayBase.getSaldoCapital() != null ? utilService.customDoubleFormat(aztecaGetawayBase.getSaldoCapital(),2) : null);
        templateVars.put("SALDO_INTERES", aztecaGetawayBase.getSaldoInteres() != null ? utilService.customDoubleFormat(aztecaGetawayBase.getSaldoInteres(),2) : null);
        templateVars.put("SALDO_MORATORIO", aztecaGetawayBase.getSaldoMoratorio() != null ? utilService.customDoubleFormat(aztecaGetawayBase.getSaldoMoratorio(),2) : null);
        templateVars.put("SALDO_TOTAL", aztecaGetawayBase.getSaldoTotal() != null ? utilService.customDoubleFormat(aztecaGetawayBase.getSaldoTotal(),2) : null);
        templateVars.put("DIAS_ATRASO", aztecaGetawayBase.getDiasAtrazo() != null ? aztecaGetawayBase.getDiasAtrazo().toString() : null);
        templateVars.put("MONTO_CAMPANIA", aztecaGetawayBase.getMontoCampania() != null ? utilService.customDoubleFormat(aztecaGetawayBase.getMontoCampania(),2) : null);
        templateVars.put("VENCIMIENTO_CAMPANIA", aztecaGetawayBase.getVencimientoCampania() != null ? utilService.dateFormat( aztecaGetawayBase.getVencimientoCampania()) : null);
        templateVars.put("DOMICILIO", aztecaGetawayBase.getDomicilio());
        templateVars.put("DEPARTAMENTO", aztecaGetawayBase.getDepartamento());
        templateVars.put("PROVINCIA", aztecaGetawayBase.getProvincia());
        templateVars.put("DISTRITO", aztecaGetawayBase.getDistrito());
        templateVars.put("CODIGO_CLIENTE_EXTERNO", aztecaGetawayBase.getCodigoClienteExterno());
        templateVars.put("CORREO_1", aztecaGetawayBase.getCorreo1());
        templateVars.put("CORREO_2", aztecaGetawayBase.getCorreo2());
        return templateVars;
    }

    @Override
    public Object sendTestSMS(String destination, String body) throws Exception {
        return sendTestSMS(destination,body,null);
    }

    private static String replaceVariables(String message, JSONObject jsonReplace) {
        if (message == null) {
            return null;
        }
        if (jsonReplace == null) {
            return message;
        }

        Iterator<?> keys = jsonReplace.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
//            SENDGRID VARIABLES
            message = message.replaceAll("%" + key + "%", jsonReplace.getString(key));
//            AWS SES VARIABLES
            message = message.replaceAll("(\\{\\{ |\\{\\{)" + key + "( \\}\\}|\\}\\})", jsonReplace.getString(key));
        }
        return message;
    }


    public String additionalParamsLinkCampaign(MarketingCampaign marketingCampaign){
        String source = "";
        String medium = "";

        if(marketingCampaign != null){
            source = marketingCampaign.getType().equals(MarketingCampaign.EMAIL) ? "MAIL" : (marketingCampaign.getType().equals(MarketingCampaign.SMS) ? "" : "");
            medium = source;
        }
        return "?utm_source=".concat(source)
                .concat("&utm_medium=").concat(medium);

    }


}

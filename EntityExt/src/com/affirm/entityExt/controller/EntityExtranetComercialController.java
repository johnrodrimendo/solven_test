package com.affirm.entityExt.controller;

import com.affirm.bancodelsol.model.CommissionClusterForm;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.EntityExtranetDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Controller("entityExtranetComercialController")
public class EntityExtranetComercialController {

    public static final String URL = "comercial";

    private EntityExtranetService entityExtranetService;
    private EntityExtranetDAO entityExtranetDAO;
    private CatalogService catalogService;
    private UtilService utilService;

    @Autowired
    public EntityExtranetComercialController(EntityExtranetService entityExtranetService, EntityExtranetDAO entityExtranetDAO, CatalogService catalogService, UtilService utilService) {
        this.entityExtranetService = entityExtranetService;
        this.entityExtranetDAO = entityExtranetDAO;
        this.catalogService = catalogService;
        this.utilService = utilService;
    }

    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:commercial:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showEntityClusters(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        Entity loggedEntity = entityExtranetService.getLoggedUserEntity().getPrincipalEntity();
        Integer countryId = loggedEntity.getCountryId();
        List<RateCommissionProduct> productClusters = catalogService.getRateCommissionProductByEntity(loggedEntity.getId());

        CommissionClusterForm form = new CommissionClusterForm();
        ((CommissionClusterForm.Validator) form.getValidator()).configValidator(countryId);

        model.addAttribute("countryId", countryId);
        model.addAttribute("products", productClusters);
        model.addAttribute("form", form);
        return new ModelAndView("/entityExtranet/extranetComercial");
    }

    @RequestMapping(value = "/" + URL + "/cluster/{clusterId}/{productId}/{priceId}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "commercial:rateCommission:edit", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loadSingleEntityCluster(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("clusterId") Integer clusterId,
            @PathVariable("productId") Integer productId,
            @PathVariable("priceId") Integer priceId) throws Exception {

        Entity loggedEntity = entityExtranetService.getLoggedUserEntity().getPrincipalEntity();
        Integer countryId = loggedEntity.getCountryId();
        List<RateCommissionProduct> productClusters = catalogService.getRateCommissionProductByEntity(loggedEntity.getId());
        RateCommissionProduct product = productClusters.stream().filter(p -> p.getProductId().equals(productId)).findFirst().orElse(null);
        RateCommissionPrice price = product.getPrices().stream().filter(p -> p.getPriceId().equals(priceId)).findFirst().orElse(null);
        RateCommissionCluster cluster = price.getClusters().stream().filter(c -> c.getId().equals(clusterId)).findFirst().orElse(null);

        CommissionClusterForm form = new CommissionClusterForm();
        ((CommissionClusterForm.Validator) form.getValidator()).configValidator(countryId);

        model.addAttribute("countryId", countryId);
        model.addAttribute("product", product);
        model.addAttribute("price", price);
        model.addAttribute("cluster", cluster);
        model.addAttribute("form", form);
        return new ModelAndView("/entityExtranet/extranetComercial :: cluster");
    }

    @RequestMapping(value = "/" + URL + "/cluster/{clusterId}/{productId}/{priceId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @RequiresPermissionOr403(permissions = "commercial:rateCommission:edit", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object bulkSaveCluster(
            Locale locale,
            @PathVariable("clusterId") Integer clusterId,
            @PathVariable("productId") Integer productId,
            @PathVariable("priceId") Integer priceId,
            String rateCommissionsJson) throws Exception {

        Entity loggedEntity = entityExtranetService.getLoggedUserEntity().getPrincipalEntity();
        List<CommissionClusterForm> rateCommissions = new Gson().fromJson(rateCommissionsJson, new TypeToken<List<CommissionClusterForm>>() {
        }.getType());

//        VALIDATE INSTALLMENTS ARE IN SEQUENCE
        for (int i = 0; i < rateCommissions.size(); i++) {
            CommissionClusterForm currentForm = rateCommissions.get(i);
            ((CommissionClusterForm.Validator) currentForm.getValidator()).configValidator(loggedEntity.getCountryId());
            if (i > 0) {
                CommissionClusterForm previousForm = rateCommissions.get(i - 1);
                ((CommissionClusterForm.Validator) currentForm.getValidator()).minInstallments.setMinValue(previousForm.getMaxInstallments() + 1);
                ((CommissionClusterForm.Validator) currentForm.getValidator()).maxInstallments.setMinValue(previousForm.getMaxInstallments() + 1);
            }

//            VALIDATE MIN - MAX AMOUNT
            ((CommissionClusterForm.Validator) currentForm.getValidator()).minAmount.setMaxValue(currentForm.getMaxAmount());
            ((CommissionClusterForm.Validator) currentForm.getValidator()).maxAmount.setMinValue(currentForm.getMinAmount());

            currentForm.getValidator().validate(Configuration.getDefaultLocale());
            if (currentForm.getValidator().isHasErrors()) {
                StringBuilder customErrorMessage = new StringBuilder();
                JSONObject errorMessage = new JSONObject(currentForm.getValidator().getErrorsJson());
                for (String key : errorMessage.keySet()) {
                    customErrorMessage.append(String.format("[Fila %s] %s", i + 1, errorMessage.get(key)));
                    customErrorMessage.append("\n");
                    customErrorMessage.append(System.getProperty("line.separator"));
                }
                return AjaxResponse.errorMessage(customErrorMessage.toString());
            }
        }

        // Convert from TNA to TEA in ARG
        if(loggedEntity.getCountryId() == CountryParam.COUNTRY_ARGENTINA){
            JSONArray jsonArray = new JSONArray(rateCommissionsJson);
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                jsonObject.put("effectiveAnnualRate", utilService.tnaToTea(jsonObject.getDouble("effectiveAnnualRate") / 100.0) * 100.0);
            }
            rateCommissionsJson = jsonArray.toString();
        }

        List<RateCommissionProduct> paramsBeforeChange = catalogService.getRateCommissionProductByEntity(loggedEntity.getId());
        entityExtranetDAO.saveBatchCommissionCluster(entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId(), productId, priceId, clusterId, rateCommissionsJson);
        List<RateCommissionProduct> paramsAfterChange = catalogService.getRateCommissionProductByEntity(loggedEntity.getId());

        entityExtranetService.sendRateCommissionChangeMail(loggedEntity, entityExtranetService.getLoggedUserEntity(), paramsBeforeChange, paramsAfterChange);

        return AjaxResponse.ok(null);
    }

}

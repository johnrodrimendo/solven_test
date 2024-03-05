/**
 *
 */
package com.affirm.common.controller;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */

@Controller
@Scope("request")
@RequestMapping("/catalog")
public class CatalogController {

    private static final Logger logger = Logger.getLogger(CatalogController.class);

    @Autowired
    private CatalogService catalogService;

    @RequestMapping(value = "/departments/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getDepartments(
            Locale locale) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getDepartments());
    }

    @RequestMapping(value = "/provinces/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getProvincesByDepartment(
            Locale locale,
            @RequestParam("departmentId") String departmentId) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getProvinces(departmentId));
    }

    @RequestMapping(value = "/provinces/general/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getProvincesGenearlByDepartment(
            @RequestParam("departmentId") Integer departmentId) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getGeneralProvinces(departmentId));
    }

    @RequestMapping(value = "/districts/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getDistrictsByProvince(
            Locale locale,
            @RequestParam("departmentId") String departmentId,
            @RequestParam("provinceId") String provinceId) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getDistricts(departmentId, provinceId));
    }

    @RequestMapping(value = "/districts/general/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getDistrictsGeneralByProvince(
            @RequestParam("provinceId") Integer provinceId) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getDistrictsByProvinceId(provinceId));
    }

    @RequestMapping(value = "/generalDistrict/json", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getGeneralDistricts(
            ModelMap model, Locale locale,
            @RequestParam("provinceId") Integer provinceId) throws Exception {
        Gson gson = new Gson();
        return AjaxResponse.ok(gson.toJson(catalogService.getGeneralDistrictByProvince(provinceId)));
    }

    @RequestMapping(value = "/nationalities/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getNationalities(Locale locale) throws Exception {
        List<Nationality> nationalities = catalogService.getNationalities(locale);
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(nationalities);
    }

    @RequestMapping(value = "/maritalStatus/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getMaritalStatus(Locale locale) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getMaritalStatus(locale));
    }

    @RequestMapping(value = "/documentType/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getDocumentTypes(Locale locale) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getIdentityDocumentTypes());
    }

    @RequestMapping(value = "/activityType/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getActivityTypes(
            Locale locale,
            @RequestParam("principal") boolean principal,
            @RequestParam(required = false, name = "countryId") Integer countryId) throws Exception {
        //TODO Implementar Ajaxresponse
        if(countryId != null){
            locale = LocaleUtils.toLocale(catalogService.getCountryParam(countryId).getLocale());
        }
        return new Gson().toJson(catalogService.getActivityTypes(locale, principal));
    }

    @RequestMapping(value = "/subActivityType/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getSubActivityTypes(
            Locale locale) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getSubActivityTypes(locale));
    }

    @RequestMapping(value = "/ocupation/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getOcupations(
            Locale locale) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getOcupations(locale));
    }

    @RequestMapping(value = "/sector/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getSectors(
            Locale locale) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getSectors());
    }

    @RequestMapping(value = "/banks/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getBanks(Locale locale, @RequestParam("countryId") Integer countryId) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getBanks(false, countryId));
    }

    @RequestMapping(value = "/voucherTypes/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getVoucherTypes(Locale locale) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getVoucherTypes(locale));
    }

    @RequestMapping(value = "/belongings/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getBelongings(Locale locale) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getBelongings(locale));
    }

    @RequestMapping(value = "/pensionPayers/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getPensionPayers(Locale locale) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getPensionPayers(locale));
    }

    @RequestMapping(value = "/relationships/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getRelationships(Locale locale) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getRelationships(locale));
    }

    @RequestMapping(value = "/phoneType/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getPhoneTypes(Locale locale) throws Exception {
        List<PhoneType> phoneTypes = new ArrayList<>();
        PhoneType phoneTypeL = new PhoneType("L", "Fijo");
        PhoneType phoneTypeM = new PhoneType("M", "Celular");
        phoneTypes.add(phoneTypeL);
        phoneTypes.add(phoneTypeM);
        return new Gson().toJson(phoneTypes);
    }

    @RequestMapping(value = "/creditCardBrand/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public String getCreditCardBrands(Locale locale) throws Exception {
        //TODO Implementar Ajaxresponse
        return new Gson().toJson(catalogService.getCreditCardBrands(locale));
    }

    @RequestMapping(value = "/interactionsContent/json", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> getInteractionsContent(
            @RequestParam(value = "countryId", required = false) Integer countryId
    ) throws Exception {
        List<InteractionContent> interactionContentList = catalogService.getInteractionContents()
                .stream()
                .filter(i -> InteractionType.MAIL != i.getType().getId())
                .filter(i -> i.getCountryId() == (countryId == null ? CountryParam.COUNTRY_PERU : countryId))
                .collect(Collectors.toList());
        return AjaxResponse.ok(new Gson().toJson(interactionContentList));
    }


}

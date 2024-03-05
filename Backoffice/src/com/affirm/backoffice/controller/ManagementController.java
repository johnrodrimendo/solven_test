package com.affirm.backoffice.controller;

import com.affirm.backoffice.dao.LoanApplicationBODAO;
import com.affirm.backoffice.model.LoanApplicationBoPainter;
import com.affirm.backoffice.model.LoanApplicationSummaryBoPainter;
import com.affirm.backoffice.service.BackofficeService;
import com.affirm.backoffice.service.LoanAplicationBoService;
import com.affirm.backoffice.util.PaginationWrapper;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.WebscrapperService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
@Controller("managementController")
@RequestMapping("/loanApplication/managementv2")
public class ManagementController {

    public static final String TAB_PIN = "pin";
    public static final String TAB_WITH_OFFER = "list";
    public static final String TAB_APPROVED = "approved";
    public static final String TAB_PENDING_DISBURSEMENT = "pending_disbursement";
    public static final String TAB_BEFORE_OFFER = "before_offer";

    private final BackofficeService backofficeService;
    private final LoanApplicationService loanApplicationService;
    private final CatalogService catalogService;
    private final LoanAplicationBoService loanAplicationBoService;
    private final WebscrapperService webscrapperService;

    private final LoanApplicationBODAO loanApplicationBODAO;
    private final LoanApplicationDAO loanApplicationDao;
    private final PersonDAO personDao;

    public ManagementController(BackofficeService backofficeService, LoanApplicationService loanApplicationService, CatalogService catalogService, LoanAplicationBoService loanAplicationBoService, LoanApplicationBODAO loanApplicationBODAO, LoanApplicationDAO loanApplicationDao, PersonDAO personDao, WebscrapperService webscrapperService) {
        this.backofficeService = backofficeService;
        this.loanApplicationService = loanApplicationService;
        this.catalogService = catalogService;
        this.loanApplicationBODAO = loanApplicationBODAO;
        this.loanAplicationBoService = loanAplicationBoService;
        this.loanApplicationDao = loanApplicationDao;
        this.personDao = personDao;
        this.webscrapperService = webscrapperService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:management", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoanApplicationsManagement(ModelMap model) {

        model.addAttribute("questionsBeforeOffer", catalogService.getQuestionsBeforeOffer());
        model.addAttribute("questionsAfterOffer", catalogService.getQuestionsAfterOffer());

        return "loanApplicationsManagementv2";
    }

    @RequestMapping(value = "/{tab}/row", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:management", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showLoanApplicationsManagementRow(
            ModelMap model, Locale locale,
            @PathVariable("tab") String tab,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {
        int viewId = 0;
        switch (tab) {
            case TAB_PIN:
                viewId = 1;
                break;
            case TAB_WITH_OFFER:
                viewId = 2;
                break;
            case TAB_APPROVED:
                viewId = 3;
                break;
            case TAB_PENDING_DISBURSEMENT:
                viewId = 4;
                break;
            case TAB_BEFORE_OFFER:
                viewId = 5;
                break;
        }
        model.addAttribute("loan", loanApplicationBODAO.getManagementLoanApplicationById(viewId, loanApplicationId, Configuration.getDefaultLocale()));
        model.addAttribute("tab", tab);
        return new ModelAndView("loanApplicationsManagementv2 :: row_" + tab);
    }

    @RequestMapping(value = "/{tab}/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:management", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showLoanApplicationsManagementList(
            ModelMap model, Locale locale,
            @PathVariable(value = "tab") String tab,
            @RequestParam(value = "amountFrom[]", required = false) String amountFromFilter,
            @RequestParam(value = "amountTo[]", required = false) String amountToFilter,
            @RequestParam(value = "creationFrom[]", required = false) String creationFromFilter,
            @RequestParam(value = "creationTo[]", required = false) String creationToFilter,
            @RequestParam(value = "document_number[]", required = false) String documentNumberFilter,
            @RequestParam(value = "reason[]", required = false) Integer[] reason,
            @RequestParam(value = "entity[]", required = false) Integer[] entity,
            @RequestParam(value = "question[]", required = false) Integer[] question,
            @RequestParam(value = "analyst[]", required = false) String analyst,
            @RequestParam(value = "hoursNextContact[]", required = false) Integer[] hoursNextContactFilter,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "200") int limit) throws Exception {

        model.addAttribute("wrapper", loadFilteredTab(locale, tab, amountFromFilter, amountToFilter, creationFromFilter, creationToFilter, documentNumberFilter, reason, entity, question, analyst, hoursNextContactFilter, offset, limit));
        model.addAttribute("tab", tab);

//        loanApplicationsManagementv2.html#tableResults STORES APPLIED FILTERS. TODO: IMPROVE
        JSONObject appliedFiltersJson = new JSONObject();
        appliedFiltersJson.put("amountFrom[]", amountFromFilter);
        appliedFiltersJson.put("amountTo[]", amountToFilter);
        appliedFiltersJson.put("creationFrom[]", creationFromFilter);
        appliedFiltersJson.put("creationTo[]", creationToFilter);
        appliedFiltersJson.put("document_number[]", documentNumberFilter);
        appliedFiltersJson.put("reason[]", reason);
        appliedFiltersJson.put("entity[]", entity);
        appliedFiltersJson.put("question[]", question);
        appliedFiltersJson.put("analyst[]", analyst);
        appliedFiltersJson.put("hoursNextContact[]", hoursNextContactFilter);
        appliedFiltersJson.put("offset", offset);
        appliedFiltersJson.put("limit", limit);
        model.addAttribute("appliedFilters", appliedFiltersJson.toString());

        return new ModelAndView("loanApplicationsManagementv2 :: " + tab);
    }

    @RequestMapping(value = "/{loanApplicationId}/getMissingDocs", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:management", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showMissingDocumentation(
            ModelMap model, Locale locale,
            @PathVariable("loanApplicationId") Integer loanApplicationId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());

        List<LoanApplicationUserFiles> userFilesObjectList = personDao.getUserFiles(loanApplication.getPersonId(), locale);
        List<LoanApplicationBoPainter> applications = loanApplicationDao.getLoanApplicationsByPerson(locale, loanApplication.getPersonId(), LoanApplicationBoPainter.class);

        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, loanApplication.getPersonId());
        if (ocupations != null) {
            Optional<PersonOcupationalInformation> principalOcupation = ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst();
            principalOcupation.ifPresent(x -> model.addAttribute("personPrincipalOcupationalInfo", x));
        }

        if (applications != null) {
            LoanApplicationBoPainter application = applications.stream().filter(app -> loanApplicationId.equals(app.getId())).findFirst().orElse(null);

            if (application != null) {
                for (LoanApplicationUserFiles loanApplicationUserFiles : userFilesObjectList) {
                    if (loanApplicationUserFiles.getLoanApplicationCode() == null) continue;
                    if (loanApplicationUserFiles.getLoanApplicationCode().equals(application.getCode())) {
                        application.setUserFilesObjectList(loanApplicationUserFiles);
                    }
                }

                List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(application.getId());
                HashSet<Integer> userFilesId = new HashSet<>();

                if (userFiles != null) {
                    for (UserFile userFile : userFiles) {
                        userFilesId.add(userFile.getFileType().getId());
                    }
                }

                List<LoanOffer> offers = loanApplicationDao.getLoanOffers(application.getId());
                if (offers != null) {
                    List<Pair<Integer, Boolean>> pendingDocuments = loanApplicationService.getRequiredDocumentsByLoanApplication(application).stream().filter(p -> !userFilesId.contains(p.getLeft())).collect(Collectors.toList());
                    if (!pendingDocuments.isEmpty()) {
                        application.setShowMissingDocumentationButton(true);
                    }
                }

                model.addAttribute("userFilesObject", application.getUserFilesObjectList());
                model.addAttribute("showMissingDocumentationButton", application.isShowMissingDocumentationButton());
            }

            model.addAttribute("person", personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false));
            model.addAttribute("loanApplication", application);

            return "fragments/personFragmentsDocumentation :: tab-documentation";
        } else {
            return AjaxResponse.errorMessage("Usuario no tiene una solicitud activa");
        }
    }

    @RequestMapping(value = "/sendBulkInteractions", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:sendBulkInteractions", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object sendBulkInteractions(
            Locale locale,
            @RequestParam("interactionId") Integer interactionId,
            @RequestParam("loanApplicationIds[]") List<Integer> loanApplicationIds,
            @RequestParam("isCheckedAll") boolean isCheckedAll,
            // FILTERS NEEDED TO GET LIST BY TAB
            @RequestParam(value = "tab") String tab,
            @RequestParam(value = "amountFrom[]", required = false) String amountFromFilter,
            @RequestParam(value = "amountTo[]", required = false) String amountToFilter,
            @RequestParam(value = "creationFrom[]", required = false) String creationFromFilter,
            @RequestParam(value = "creationTo[]", required = false) String creationToFilter,
            @RequestParam(value = "document_number[]", required = false) String documentNumberFilter,
            @RequestParam(value = "reason[]", required = false) Integer[] reason,
            @RequestParam(value = "entity[]", required = false) Integer[] entity,
            @RequestParam(value = "employer[]", required = false) Integer[] employer,
            @RequestParam(value = "analyst[]", required = false) String analyst,
            @RequestParam(value = "hoursNextContact[]", required = false) Integer[] hoursNextContactFilter,
            // FILTERS NEEDED TO GET LIST BY TAB
            @RequestParam("filters") String filters
    ) throws Exception {
        if (catalogService.getInteractionContents().stream().anyMatch(i -> interactionId.equals(i.getId()))) {
            if (isCheckedAll) {
                int MAX_LIMIT = 500;

                PaginationWrapper<LoanApplicationSummaryBoPainter> loanApplicationSummaryBoPainterList = loadFilteredTab(locale, tab, amountFromFilter, amountToFilter, creationFromFilter, creationToFilter, documentNumberFilter, reason, entity, employer, analyst, hoursNextContactFilter, 0, MAX_LIMIT);
                loanApplicationIds = loanApplicationSummaryBoPainterList == null ? null : loanApplicationSummaryBoPainterList.getResults()
                        .stream()
                        .map(LoanApplicationSummaryBoPainter::getId)
                        .collect(Collectors.toList());

                JSONObject filtersJson = new JSONObject(filters);
                filtersJson.put("limit", MAX_LIMIT);
                filters = filtersJson.toString();
            }

            if (loanApplicationIds == null) {
                return AjaxResponse.errorMessage("No se pudo listar todas las interacciones. Vuelve a intentar nuevamente.");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("filters", filters);

            webscrapperService.callBoManagementFollowupInteraction(interactionId, new JSONArray(loanApplicationIds), params, backofficeService.getLoggedSysuser().getId());

            return AjaxResponse.ok(null);
        } else {
            return AjaxResponse.errorMessage("No existe la interaccion seleccionada.");
        }
    }

    private PaginationWrapper<LoanApplicationSummaryBoPainter> loadFilteredTab(Locale locale, String tab, String amountFromFilter, String amountToFilter, String creationFromFilter, String creationToFilter, String documentNumberFilter, Integer[] reason, Integer[] entity, Integer[] question, String analyst, Integer[] hoursNextContactFilter, int offset, int limit) throws Exception {
        Date creationFrom = creationFromFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFromFilter) : null;
        Date creationTo = creationToFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationToFilter) : null;
        Integer entities = entity != null ? (entity.length > 0 ? entity[0] : null) : null;
        Integer reasons = reason != null ? (reason.length > 0 ? reason[0] : null) : null;
        Integer hoursNextContact = hoursNextContactFilter != null ? (hoursNextContactFilter.length > 0 ? hoursNextContactFilter[0] : null) : null;
        Integer questions = question != null ? (question.length > 0 ? question[0] : null) : null;

        switch (tab) {
            case TAB_PIN:
                return loanAplicationBoService.getLoanApplicationsSummariesListManagement(
                        1,
                        creationFrom,
                        creationTo,
                        amountFromFilter,
                        amountToFilter,
                        documentNumberFilter,
                        entities,
                        null,
                        reasons,
                        backofficeService.getCountryActiveSysuser(),
                        analyst,
                        locale,
                        offset,
                        limit,
                        hoursNextContact,
                        questions);
            case TAB_WITH_OFFER:
                return loanAplicationBoService.getLoanApplicationsSummariesListManagement(
                        2,
                        creationFrom,
                        creationTo,
                        amountFromFilter,
                        amountToFilter,
                        documentNumberFilter,
                        entities,
                        null,
                        reasons,
                        backofficeService.getCountryActiveSysuser(),
                        analyst,
                        locale,
                        offset,
                        limit,
                        hoursNextContact,
                        questions);
            case TAB_APPROVED:
                return loanAplicationBoService.getLoanApplicationsSummariesListManagement(
                        3,
                        creationFrom,
                        creationTo,
                        amountFromFilter,
                        amountToFilter,
                        documentNumberFilter,
                        entities,
                        null,
                        reasons,
                        backofficeService.getCountryActiveSysuser(),
                        analyst,
                        locale,
                        offset,
                        limit,
                        hoursNextContact,
                        questions);
            case TAB_PENDING_DISBURSEMENT:
                return loanAplicationBoService.getLoanApplicationsSummariesListManagement(
                        4,
                        creationFrom,
                        creationTo,
                        amountFromFilter,
                        amountToFilter,
                        documentNumberFilter,
                        entities,
                        null,
                        reasons,
                        backofficeService.getCountryActiveSysuser(),
                        analyst,
                        locale,
                        offset,
                        limit,
                        hoursNextContact,
                        questions);
            case TAB_BEFORE_OFFER:
                return loanAplicationBoService.getLoanApplicationsSummariesListManagement(
                        5,
                        creationFrom,
                        creationTo,
                        amountFromFilter,
                        amountToFilter,
                        documentNumberFilter,
                        entities,
                        null,
                        reasons,
                        backofficeService.getCountryActiveSysuser(),
                        analyst,
                        locale,
                        offset,
                        limit,
                        hoursNextContact,
                        questions);
            default:
                return null;
        }
    }

}

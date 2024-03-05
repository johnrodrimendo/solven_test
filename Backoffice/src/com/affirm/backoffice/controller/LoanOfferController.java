/**
 *
 */
package com.affirm.backoffice.controller;

import com.affirm.backoffice.model.form.CreateLoanOfferForm;
import com.affirm.backoffice.service.BackofficeService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.PreApprovedInfo;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.InteractionService;
import com.affirm.common.service.ProductService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class LoanOfferController {

    private static Logger logger = Logger.getLogger(LoanOfferController.class);

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private BackofficeService backofficeService;
    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/loanOffer/modal/create", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "loanApplication:forceApproval", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getLoanOfferCreateModal(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") int loanApplicationId,
            @RequestParam(value = "entityId", required = false) Integer entityId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if (loanApplication.getCreditAnalystSysUserId() == null || loanApplication.getCreditAnalystSysUserId().intValue() != backofficeService.getLoggedSysuser().getId())
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");

        CreateLoanOfferForm form = new CreateLoanOfferForm();
        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

        LoanOffer offer;
        if (entityId == null)
            offer = loanApplicationDao.getLoanOffers(loanApplicationId).stream().filter(i -> i.getSelected()).findFirst().orElse(null);
        else
            offer = loanApplicationDao.getLoanOffers(loanApplicationId).stream().filter(i -> i.getEntityId() == entityId).findFirst().orElse(null);

        if (offer == null)
            return AjaxResponse.errorMessage("No existe ninguna oferta creada aún");

        Employer employer = offer.getEmployer();

        List<EntityProduct> entityProducts = null;
        if (employer != null) {
            entityProducts = catalogService.getEntityProductsByProduct(offer.getProduct().getId()).stream()
                    .filter(e -> e.getEmployer() != null && e.getEmployer().getId() == employer.getId().intValue())
                    .collect(Collectors.toList());
        } else {
            entityProducts = catalogService.getEntityProductsByProduct(offer.getProduct().getId());
        }


        if (entityProducts == null || entityProducts.isEmpty()) {
            return AjaxResponse.errorMessage("No existe entidad financiadora para este producto");
        }

        List<PreApprovedInfo> preApprovedInfos = personDao.getPreApprovedDataByDocument(offer.getProduct().getId(), person.getDocumentType().getId(), person.getDocumentNumber());
        PreApprovedInfo filteredPreApprovedInfo = preApprovedInfos != null ? preApprovedInfos.stream().filter(e -> e.getEntity().getId().intValue() == offer.getEntityId()).findFirst().orElse(null) : null;

        Double maxAmount;
        Integer maxInstallments;

        if (filteredPreApprovedInfo != null && filteredPreApprovedInfo.getMaxAmount() != null) {
            maxAmount = filteredPreApprovedInfo.getMaxAmount();
            maxInstallments = filteredPreApprovedInfo.getMaxInstallments();
            model.addAttribute("entitySelected", offer.getEntityId());
        } else {
            maxAmount = Double.valueOf(offer.getProduct().getProductParams(loanApplication.getCountryId()).getMaxAmount());
            maxInstallments = offer.getProduct().getProductParams(loanApplication.getCountryId()).getMaxInstallments();
            model.addAttribute("entitySelected", entityId);
        }


        ((CreateLoanOfferForm.CreateLoanOfferFormValidator) form.getValidator()).ammount.setMaxValue(maxAmount.intValue());
        ((CreateLoanOfferForm.CreateLoanOfferFormValidator) form.getValidator()).ammount.setMinValue(offer.getProduct().getProductParams(loanApplication.getCountryId()).getMinAmount());
        ((CreateLoanOfferForm.CreateLoanOfferFormValidator) form.getValidator()).installments.setMaxValue(maxInstallments);
        ((CreateLoanOfferForm.CreateLoanOfferFormValidator) form.getValidator()).installments.setMinValue(offer.getProduct().getProductParams(loanApplication.getCountryId()).getMinInstallments());

        List<Entity> entities = new ArrayList<>();
        entities.add(catalogService.getEntity(loanApplication.getSelectedEntityId()));

        if (offer.getProduct().getId() == Product.DEBT_CONSOLIDATION) {
            double newAmmount = 0;
            loanApplication.setConsolidableDebts(loanApplicationDao.getConsolidationAccounts(loanApplicationId));
            for (int i = 0; i < loanApplication.getConsolidableDebts().size(); i++) {
                if (loanApplication.getConsolidableDebts().get(i).isSelected())
                    newAmmount += loanApplication.getConsolidableDebts().get(i).getBalance();
            }
            model.addAttribute("loanAmmount", newAmmount);
        }

        model.addAttribute("entities", entities);
        model.addAttribute("createLoanOfferForm", form);
        model.addAttribute("loanApplicationId", loanApplicationId);
        loanApplication.setInstallments(Math.min(loanApplication.getInstallments(), maxInstallments));
        model.addAttribute("loanApplication", loanApplication);
        return new ModelAndView("fragments/personFragments :: createLoanOfferModal");
    }

    @RequestMapping(value = "/loanOffer/create", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:forceApproval", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> createLoanOffer(
            ModelMap model, Locale locale,
            //CreateLoanOfferForm form,
            @RequestParam("ammount") Double ammount,
            @RequestParam("entityId") Integer entityId,
            @RequestParam("installments") Integer installments,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        // Generate new offer
        LoanApplication loan = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if (loan.getCreditAnalystSysUserId() == null || loan.getCreditAnalystSysUserId().intValue() != backofficeService.getLoggedSysuser().getId())
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");

        Employee employee = personDao.getEmployeeByPerson(loan.getPersonId(), locale);

        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplicationId);

        Integer entityProductParameterId = offers.get(0).getEntityProductParameterId();

        LoanOffer offer = loanApplicationDao.createLoanOfferAnalyst(loanApplicationId, ammount, installments, entityId, offers.get(0).getProduct().getId(), employee != null ? employee.getEmployer().getId() : null, entityProductParameterId);
        loanApplicationDao.selectLoanOfferAnalyst(offer.getId(), 1);
        loanApplicationDao.updateLoanApplicationStatus(loanApplicationId, LoanApplicationStatus.WAITING_APPROVAL, 1);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);

        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        PersonContactInformation personContact = personDao.getPersonContactInformation(locale, person.getId());
        loanApplicationDao.updateLoanOfferGeneratedStatus(loanApplicationId, false);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanOffer/{loanOfferId}/notify", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanOffer:select", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> notifyClient(
            ModelMap model, Locale locale,
            @PathVariable("loanOfferId") Integer loanOfferId,
            @RequestParam("loanId") Integer loanApplicationId
    ) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if (loanApplication.getCreditAnalystSysUserId() == null || loanApplication.getCreditAnalystSysUserId().intValue() != backofficeService.getLoggedSysuser().getId())
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");

        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        PersonContactInformation personContact = personDao.getPersonContactInformation(locale, person.getId());
        LoanOffer offer = loanApplicationDao.getLoanOffers(loanApplicationId).stream().filter(e -> e.getSelected() && e.getSelectedByAnalyst()).collect(Collectors.toList()).get(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonVars = new JSONObject();
                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("AMOUNT", utilService.doubleMoneyFormat(offer.getAmmount()));
                    jsonVars.put("INSTALLMENTS", offer.getInstallments() + " Meses");
                    jsonVars.put("INSTALLMENT_AMOUNT", utilService.doubleMoneyFormat(offer.getInstallmentAmountAvg()));
                    jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                    jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                    PersonInteraction interaction = new PersonInteraction();
                    interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.FORCED_APPROVAl_MAIL, loanApplication.getCountryId()));
                    interaction.setDestination(personContact.getEmail());
                    interaction.setLoanApplicationId(loanApplicationId);
                    interaction.setPersonId(person.getId());

                    interactionService.sendPersonInteraction(interaction, jsonVars, null);
                } catch (Exception ex) {
                    logger.error("Error sending email", ex);
                }
            }
        }).start();

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanOffer/{loanOfferId}/select", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanOffer:select", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> selectLoanOffer(
            ModelMap model, Locale locale,
            @PathVariable("loanOfferId") Integer loanOfferId,
            @RequestParam("loanId") Integer loanId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId, locale);
        if (loanApplication.getCreditAnalystSysUserId() == null || loanApplication.getCreditAnalystSysUserId().intValue() != backofficeService.getLoggedSysuser().getId())
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");
        loanApplicationDao.selectLoanOfferAnalyst(loanOfferId, ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());
        loanApplicationDao.updateLoanApplicationStatus(loanId, LoanApplicationStatus.WAITING_APPROVAL, 1);

        LoanOffer loanOffer = loanApplicationDao.getLoanOffersAll(loanId).stream().filter(o -> o.getId().equals(loanOfferId)).findFirst().orElse(null);

        if (loanOffer == null)
            return AjaxResponse.errorMessage("No tiene ofertas.");

        if (loanOffer.getEntityProductParam().getSignatureType() == 1)
            loanApplicationDao.boResetContract(loanId);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanOffer/consolidableDebt/select", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanOffer:consolidableDebt:select", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> selectConsolidableDebt(
            ModelMap model, Locale locale,
            @RequestParam("accountType") Integer accountType,
            @RequestParam("entityCode") String entityCode,
            @RequestParam("loanId") Integer loanId,
            @RequestParam("balance") Double balance,
            @RequestParam("installments") Integer installments,
            @RequestParam("rate") Double rate,
            @RequestParam("accountCardNumber") String accountCardNumber,
            @RequestParam("active") Boolean active,
            @RequestParam("brandId") Integer brandId,
            @RequestParam("departmentUbigeo") String ubigeoDepartment,
            @RequestParam("entityId") Integer entityId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId, locale);
        if (loanApplication.getCreditAnalystSysUserId() == null || loanApplication.getCreditAnalystSysUserId().intValue() != backofficeService.getLoggedSysuser().getId())
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");

        List<ConsolidableDebt> consolidableDebts = loanApplicationDao.getConsolidationAccounts(loanId);
        int count = 0;
        for (int i = 0; i < consolidableDebts.size(); i++) {
            if (consolidableDebts.get(i).isSelected()) count++;
        }

        if (count == 1 && !active) {
            return AjaxResponse.errorMessage("Debes seleccionar por lo menos una deuda a consolidar");
        } else {
            loanApplicationDao.registerConsolidationAccount(loanId, accountType, entityCode, balance, installments, rate, active, accountCardNumber, brandId, ubigeoDepartment);
            loanApplicationDao.updateLoanOfferGeneratedStatus(loanId, true);
            return AjaxResponse.ok(null);
        }
    }

    @RequestMapping(value = "/loanOffer/{field}", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> updateProperty(
            ModelMap model, Locale locale,
            @PathVariable("field") String field,
            @RequestParam("value") String value,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("consolidationAccounttype") Integer consolidationAccounttype,
            @RequestParam("entity") String entity) throws Exception {
        switch (field) {
            case "accountCardNumber":
                loanApplicationDao.registerConsolidationAccount(loanApplicationId, consolidationAccounttype, entity, null, null, null, null, value, null, null);
                break;
            case "balance":
                loanApplicationDao.registerConsolidationAccount(loanApplicationId, consolidationAccounttype, entity, Double.valueOf(value), null, null, null, null, null, null);
                break;
            case "rate":
                loanApplicationDao.registerConsolidationAccount(loanApplicationId, consolidationAccounttype, entity, null, null, Double.valueOf(value), null, null, null, null);
                break;
            case "installments":
                loanApplicationDao.registerConsolidationAccount(loanApplicationId, consolidationAccounttype, entity, null, Integer.valueOf(value), null, null, null, null, null);
                break;
            case "creditCard":
                loanApplicationDao.registerConsolidationAccount(loanApplicationId, consolidationAccounttype, entity, null, null, null, null, null, Integer.valueOf(value), null);
                break;
            case "ubigeo":
                loanApplicationDao.registerConsolidationAccount(loanApplicationId, consolidationAccounttype, entity, null, null, null, null, null, null, value);
                break;
        }
        return AjaxResponse.ok(null);
    }

}

package com.affirm.common.service.question;

import com.affirm.bancodelsol.service.BancoDelSolService;
import com.affirm.bantotalrest.exception.BT40147Exception;
import com.affirm.bantotalrest.model.RBTPG075.BTPrestamosSimularResponse;
import com.affirm.bantotalrest.model.RBTPG265.BTPrestamosSimularAmortizableSinClienteRequest;
import com.affirm.bantotalrest.service.BTApiRestService;
import com.affirm.bantotalrest.service.impl.BTApiRestServiceImpl;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question50Form;
import com.affirm.common.model.form.UpdateLoanApplicationForm;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.*;
import com.affirm.fdlm.FdlmServiceCall;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service("question50Service")
public class Question50Service extends AbstractQuestionService<Question50Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private FdlmServiceCall fdlmServiceCall;
    @Autowired
    private BancoDelSolService bancoDelSolService;
    @Autowired
    private TranslatorDAO translatorDAO;
    @Autowired
    private BTApiRestService btApiRestService;

    private static final int REJECT_OFFER = -1;
    private static final int BACK_TO_FIRST_DUE_DATE = -2;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Boolean showDiscountData = false;
                List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(loanApplication.getId(), locale, JsonResolverDAO.EVALUATION_FOLLOWER_DB)
                        .stream()
                        .filter(e -> e.getApproved() != null && e.getApproved())
                        .collect(Collectors.toList());


                if (loanApplication.getStatus().getId() == LoanApplicationStatus.CROSS_SELLING_OFFER) {
                    loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.EVAL_APPROVED, null);
                    loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                }

                if (loanApplication.getFirstDueDate() == null) {

                    // Get the entityProductParam to use in the schedulevalidations
                    EntityProductParams scheduleEntityProductParam = null;
                    for (LoanApplicationEvaluation e : evaluations) {
                        EntityProductParams param = catalogService.getEntityProductParamById(e.getEntityProductParameterId());
                        if (param.getGracePeriod() != null) {
                            scheduleEntityProductParam = param;
                            break;
                        }
                    }

                    if (scheduleEntityProductParam != null) {
                        List<Date> enableDates = getScheduleEnablesDates(scheduleEntityProductParam);

                        if (enableDates.size() == 1) {
                            loanApplicationDao.updateFirstDueDate(loanApplication.getId(), enableDates.get(0));
                            createLoanOffers(loanApplication);
                        } else {
                            String[] enabledDatesFormatted = new String[enableDates.size()];
                            for (int i = 0; i < enableDates.size(); i++) {
                                enabledDatesFormatted[i] = new SimpleDateFormat("dd/MM/yyyy").format(enableDates.get(i));
                            }

                            String selectedDateFormatted = enabledDatesFormatted[0];
                            if (scheduleEntityProductParam.getEntity().getId() == Entity.BANCO_DEL_SOL) {
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.add(Calendar.MONTH, 1);

                                for (Date date : enableDates) {
                                    if (date.before(selectedDate.getTime())) {
                                        selectedDateFormatted = new SimpleDateFormat("dd/MM/yyyy").format(date);
                                    } else {
                                        break;
                                    }
                                }
                            }

                            if (loanApplication.getEntityId() == null || (scheduleEntityProductParam.getId() != null && scheduleEntityProductParam.getEntity().getId() == Entity.AZTECA)) {
                                String firstDueDate;

                                if (scheduleEntityProductParam.getEntity().getId() == Entity.AZTECA) {
                                    attributes.put("showOfferMedal", false);
                                    attributes.put("enableDates", enabledDatesFormatted);
                                    attributes.put("selectedDate", selectedDateFormatted);
                                    attributes.put("showFirstDueDate", true);
                                    attributes.put("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
                                    attributes.put("loanApplication", loanApplicationDao.getLoanApplication(loanApplication.getId(), locale));
                                    attributes.put("showLoanOfferUpdateModal", false);
                                    break;
                                } else if (Arrays.stream(enabledDatesFormatted).anyMatch(e -> e.startsWith("02"))) {
                                    firstDueDate = Arrays.stream(enabledDatesFormatted).filter(e -> e.startsWith("02")).findFirst().orElse(null);
                                } else {
                                    firstDueDate = enabledDatesFormatted[0];
                                }

                                loanApplicationDao.updateFirstDueDate(loanApplication.getId(), new SimpleDateFormat("dd/MM/yyyy").parse(firstDueDate));
                                createLoanOffers(loanApplication);
                            } else {
                                attributes.put("showOfferMedal", false);
                                attributes.put("enableDates", enabledDatesFormatted);
                                attributes.put("selectedDate", selectedDateFormatted);
                                attributes.put("showFirstDueDate", true);
                                attributes.put("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
                                attributes.put("loanApplication", loanApplicationDao.getLoanApplication(loanApplication.getId(), locale));
                                attributes.put("showLoanOfferUpdateModal", false);
                                break;
                            }


                        }

                    }
                }
                // Check if the loan has n azteca insurance type selected
                if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA) {
                    if (loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.AZTECA_INSURANCE_TYPE.getKey()) == null) {
                        attributes.put("showAztecaInsuranceSelection", true);
                        attributes.put("entityProductParam", catalogService.getEntityProductParamById(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE));
                        break;
                    }
                }

                List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                if (offers == null) {
                    offers = createLoanOffers(loanApplication);
                }

                if (offers.get(0).getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_BANBIF_LIBRE_DISPONIBILIDAD) {
                    Collections.reverse(offers);
                } else if (Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA, EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE).contains(offers.get(0).getEntityProductParameterId())) {
                    //Collections.reverse(offers);
                } else {
                    if (offers.size() > 1) {
                        Collections.swap(offers, 0, 1); // Show first the offer of more money
                    }
                }

                // Offer order 4 is the simulated offer, this should not be showed
                offers.removeIf(o -> o.getLoanOfferOrder() == 4);

                LoanOffer minAmountOffer = null;
                List<LoanOffer> comparableLoanOffer = loanApplicationDao.getComparableLoanOffers(loanApplication.getId());
                if (comparableLoanOffer != null && comparableLoanOffer.stream().anyMatch(e -> !e.getSelected()))
                    minAmountOffer = comparableLoanOffer.stream().filter(e -> !e.getSelected() && e.getInstallmentAmmount() != null).min(Comparator.comparing(LoanOffer::getInstallmentAmmount)).orElse(null);

                Calendar today = Calendar.getInstance();
                today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE), 0, 0, 0);
                if (offers.get(0).getRegisterDate().before(today.getTime())) {
                    offers = createLoanOffers(loanApplication);
                } else if (loanApplication.getSelectedEntityId() != null && loanApplication.getProduct() != null && (offers.get(0).getEntityId().intValue() != loanApplication.getSelectedEntityId() || offers.get(0).getProduct().getId().intValue() != loanApplication.getProduct().getId())) {
                    offers = createLoanOffers(loanApplication);
                }

                // If is BDS and the max_amount is 0, there is a problem! Dont let it pass
                if (offers.stream().anyMatch(o -> o.getMaxAmmount() != null && o.getMaxAmmount() == 0)) {
                    // TODO Send email with warning
                }

                // If the offers are from consolidation product, go to ConsolidationOffer
                if (offers.get(0).getProduct().getId() == Product.DEBT_CONSOLIDATION || offers.get(0).getProduct().getId() == Product.DEBT_CONSOLIDATION_OPEN) {
                    throw new GoToNextQuestionException("CONSOLIDACION", ProcessQuestionSequence.TYPE_SKIPPED);
                }

                // If the offer is from tarjetas peruanas, skip it
                if (offers.get(0).getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO) {
                    loanApplicationDao.registerSelectedLoanOffer(offers.get(0).getId(), offers.get(0).getFirstDueDate());
                    if (offers.get(0).getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO) {
                        List<String> tarjetasPeruanasColors = Arrays.asList("purple", "green");
                        Collections.shuffle(tarjetasPeruanasColors);
                        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.TARJETA_PERUANA_CARD_COLOR.getKey(), tarjetasPeruanasColors.get(0));
                        loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                    }
                    throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_SKIPPED);
                }

                String offersDiff = null;
                Boolean onlineDisbursement = false;
                EntityProductParams entityProductParams = catalogService.getEntityProductParamById(offers.get(offers.size() - 1).getEntityProductParameterId());
                if (minAmountOffer != null) {
                    if (minAmountOffer.getInstallmentAmmount() > offers.get(offers.size() - 1).getInstallmentAmmount()) {
                        Double diff = Math.abs(minAmountOffer.getInstallmentAmmount() - offers.get(offers.size() - 1).getInstallmentAmmount());
                        offersDiff = utilService.doubleMoneyFormat(diff, offers.get(offers.size() - 1).getCurrency());
                    } else {
                        if (entityProductParams.getDisbursementType() == EntityProductParams.DISBURSEMENT_TYPE_DEPOSIT) {
                            onlineDisbursement = true;
                        }
                    }
                }

                // Set the offer rejection reason to null
                loanApplicationDao.updateApplicationOfferRejection(loanApplication.getId(), null);

                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                attributes.put("showDiscountData", showDiscountData);
                attributes.put("showOfferMedal", offers.get(0).getEntity().getId() != Entity.BANCO_DEL_SOL);
                attributes.put("shouldOpenScheduleOnClickOffer", offers.get(0).getEntity().getId() == Entity.BANCO_DEL_SOL);
                attributes.put("personName", person.getFirstName());
                attributes.put("offersDiff", offersDiff);
                attributes.put("onlineDisbursement", onlineDisbursement);
                attributes.put("offers", offers);
                attributes.put("entityProductParam", entityProductParams);
                attributes.put("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
                attributes.put("loanApplication", loanApplicationDao.getLoanApplication(loanApplication.getId(), locale));
                attributes.put("entity", catalogService.getEntity(loanApplication.getEntityId()));
                attributes.put("differentEntityProductParams", offers.stream().mapToInt(LoanOffer::getEntityProductParameterId).distinct().count() > 1);
                attributes.put("showTarjetasPeruanasOffer", offers.stream().allMatch(o -> o.getEntityProductParameterId().equals(EntityProductParams.ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO)));
                attributes.put("showRejectionLink", !evaluationService.isQuestionRepeatedInProces(loanApplication.getQuestionSequence(), ProcessQuestion.Question.Constants.OFFER_REJECTION_REASON));

                if (entityProductParams.getSimulatedOffer()) {
                    attributes.put("showSimulatedOffer", entityProductParams.getSimulatedOffer());
                    attributes.put("simulatedOfferForm", getSimulatedOfferForm(offers, loanApplication));
                }

                if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.FUNDACION_DE_LA_MUJER) {
                    attributes.put("fdlmSaldoTotal", fdlmServiceCall.obtenerTopazConsultarCredito(loanApplication.getId()));
                }

                if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA) {
                    if (loanApplication.getProductCategoryId() != null && loanApplication.getProductCategoryId() == ProductCategory.CONSUMO) {
                        attributes.put("showAztecaLegalFooter", true);
                        attributes.put("showGoBack", true);
                        Integer insuranceType = JsonUtil.getIntFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.AZTECA_INSURANCE_TYPE.getKey(), null);
                        if (insuranceType != null)
                            switch (insuranceType) {
                                case LoanApplication.FAMILY_PROTECTION_INSURANCE_TYPE:
                                    attributes.put("aztecaInsuranceName", "Protección Familiar");
                                    break;
                                case LoanApplication.WITH_DEVOLUTION_INSURANCE_TYPE:
                                    attributes.put("aztecaInsuranceName", "Desgravamen con Devolución");
                                    break;
                                case LoanApplication.WITHOUT_DEVOLUTION_INSURANCE_TYPE:
                                    attributes.put("aztecaInsuranceName", "Desgravamen sin Devolución");
                                    break;
                            }
                    }
                }

                if (offers != null && !offers.isEmpty() && offers.get(0).getEntityProductParameterId() != null && Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA, EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE).contains(offers.get(0).getEntityProductParameterId())) {
                    attributes.put("showLoanOfferUpdateModal", true);
                    attributes.put("showLoanApplicationUpdateModal", false);
                }

                break;

        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question50Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return (form.getSelectedOffer() == REJECT_OFFER) ? "REJECT" : "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question50Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question50Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getSelectedOffer() != REJECT_OFFER) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                    LoanOffer offer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getId().equals(form.getSelectedOffer())).findFirst().orElse(null);

                    // If is BDS and the max_amount is 0, there is a problem! Dont let it pass
                    if (offer.getEntity().getId() == Entity.BANCO_DEL_SOL && offer.getMaxAmmount() != null && offer.getMaxAmmount() == 0) {
                        throw new ResponseEntityException(AjaxResponse.errorMessage("Hubo un error con la oferta seleccionada. Por favor, comunicate con Soporte al Productor para resolver el problema."));
                    }

                    if (offer.getEntity().getId() == Entity.BANCO_DEL_SOL) {
                        loanApplicationDao.registerSelectedLoanOffer(form.getSelectedOffer(), loanApplication.getFirstDueDate());
                    } else {
                        loanApplicationDao.registerSelectedLoanOffer(form.getSelectedOffer(), offer.getFirstDueDate());
                    }
                    if (offer.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO) {
                        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.TARJETA_PERUANA_CARD_COLOR.getKey(), form.getSelectedColor());
                        loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                    }
                    if (offer.getEntityId() == Entity.AZTECA && loanApplication.getProductCategoryId() == ProductCategory.CONSUMO) {
                        if (JsonUtil.getIntFromJson(offer.getEntityCustomData(), LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_PLAN.getKey(), null) != null)
                            loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.AZTECA_INSURANCE_PLAN.getKey(), JsonUtil.getIntFromJson(offer.getEntityCustomData(), LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_PLAN.getKey(), null));
                        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.AZTECA_INSURANCE_COST.getKey(), JsonUtil.getIntFromJson(offer.getEntityCustomData(), LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_COST.getKey(), null));
                        loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                    }
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                if (offers != null) {
                    // If the offers are from consolidation product, go to ConsolidationOffer
                    if (loanApplication.getFirstDueDate() != null) {
                        if (offers.get(0).getProduct().getId() == Product.DEBT_CONSOLIDATION || offers.get(0).getProduct().getId() == Product.DEBT_CONSOLIDATION_OPEN) {
                            return "CONSOLIDACION";
                        }
                    }
                }

                break;
        }
        return null;
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "firstDueDate": {
                        String firstDueDate = (String) params.get("firstDueDate");

                        if (!new StringFieldValidator(ValidatorUtil.FIRST_DUE_DATE, firstDueDate).validate(locale)) {
                            return AjaxResponse.errorMessage("La fecha no es v&aacute;lida.");
                        }

                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(loanApplication.getId(), locale, JsonResolverDAO.EVALUATION_FOLLOWER_DB)
                                .stream()
                                .filter(e -> e.getApproved() != null && e.getApproved())
                                .collect(Collectors.toList());
                        EntityProductParams scheduleEntityProductParam = null;
                        for (LoanApplicationEvaluation e : evaluations) {
                            EntityProductParams param = catalogService.getEntityProductParamById(e.getEntityProductParameterId());
                            if (param.getGracePeriod() != null) {
                                scheduleEntityProductParam = param;
                                break;
                            }
                        }

                        List<Date> enableDates = getScheduleEnablesDates(scheduleEntityProductParam);

                        Date firstDueDateDate = new SimpleDateFormat("dd/MM/yyyy").parse(firstDueDate);
                        for (Date date : enableDates) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            Calendar cal1 = Calendar.getInstance();
                            cal1.setTime(firstDueDateDate);
                            if (cal1.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                                    cal1.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                                loanApplicationDao.updateFirstDueDate(loanApplication.getId(), new SimpleDateFormat("dd/MM/yyyy").parse(firstDueDate));
                                if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getProductCategoryId() == ProductCategory.CONSUMO) {
                                    return AjaxResponse.ok(null);
                                } else {
                                    createLoanOffers(loanApplication);
                                    return AjaxResponse.ok(null);
                                }
                            }
                        }
                        return AjaxResponse.errorMessage("La fecha no esta en el rango permitido.");
                    }
                    case "insuranceType": {
                        String insuranceType = (String) params.get("insuranceType");

                        if (!Arrays.asList("1", "2", "3").contains(insuranceType)) {
                            return AjaxResponse.errorMessage("El tipo de seguro no es v&aacute;lido.");
                        }

                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.AZTECA_INSURANCE_TYPE.getKey(), insuranceType);
                        loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                        return AjaxResponse.ok(null);
                    }
                    case "simulateOffer": {
                        Integer amount = Integer.parseInt(params.get("amount") + "");
                        Integer installments = Integer.parseInt(params.get("installments") + "");
                        boolean mobile = params.containsKey("mobile") ? Boolean.parseBoolean(params.get("mobile") + "") : false;

                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                        UpdateLoanApplicationForm form = getSimulatedOfferForm(offers, loanApplication);
                        form.setAmount(amount);
                        form.setInstallments(installments);

                        form.getValidator().validate(locale);
                        if (form.getValidator().isHasErrors()) {
                            JSONObject json = new JSONObject(form.getValidator().getErrorsJson());
                            if (json.has("amount")) {
                                return AjaxResponse.errorMessage("[Monto] - " + json.getString("amount"));
                            } else if (json.has("installments")) {
                                return AjaxResponse.errorMessage("[Cuotas] - " + json.getString("installments"));
                            } else {
                                return AjaxResponse.errorMessage("Los valores enviados son inválidos.");
                            }
                        }

                        LoanOffer simulatedOffer = loanApplicationDao.createLoanOfferAnalyst(loanApplication.getId(), amount, installments, offers.get(0).getEntity().getId(), offers.get(0).getProduct().getId(), null, offers.get(0).getEntityProductParameterId());
                        form.setInstallments(simulatedOffer.getInstallments());
                        form.setAmount(simulatedOffer.getAmmount().intValue());

                        Map<String, Object> attributes = new HashMap<>();
                        attributes.put("offer", simulatedOffer);
                        attributes.put("loanApplication", loanApplication);
                        attributes.put("person", personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false));
                        attributes.put("simulatedOfferForm", form);
                        if (mobile) {
                            return new ModelAndView("loanApplication/formQuestions/50_question :: question_50_simulated_offer_mobile", attributes);
                        } else {
                            return new ModelAndView("loanApplication/formQuestions/50_question :: question_50_simulated_offer", attributes);
                        }
                    }
                    case "backToFirstDueDate": {
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        loanApplicationDao.setFirstDueDateToNull(id);
                        if (loanApplication.getEntityId() == Entity.AZTECA) {
                            loanApplication.getEntityCustomData().remove(LoanApplication.EntityCustomDataKeys.AZTECA_INSURANCE_TYPE.getKey());
                            loanApplication.getEntityCustomData().remove(LoanApplication.EntityCustomDataKeys.AZTECA_INSURANCE_PLAN.getKey());
                            loanApplicationDao.updateEntityCustomData(id, loanApplication.getEntityCustomData());
                            loanApplicationDao.removeLoanOffers(loanApplication.getId());
                        }
                        return AjaxResponse.ok(null);
                    }
                }
                break;
        }
        throw new Exception("No method configured");
    }

    private UpdateLoanApplicationForm getSimulatedOfferForm(List<LoanOffer> offers, LoanApplication loanApplication) {
        List<Product> products = catalogService.getCatalog(Product.class, Configuration.getDefaultLocale(), true, p -> p.getProductCategoryId() == loanApplication.getProductCategoryId().intValue());
        Integer minInstallments = products.stream().filter(p -> p.getProductParams(loanApplication.getCountryId()) != null).collect(Collectors.toList()).stream().map(p -> p.getProductParams(loanApplication.getCountryId())).min(Comparator.comparingInt(p -> p.getMinInstallments())).get().getMinInstallments();
        UpdateLoanApplicationForm form = new UpdateLoanApplicationForm();
        ((UpdateLoanApplicationForm.Validator) form.getValidator()).amount.setMaxValue((int) offers.stream().mapToDouble(o -> o.getMaxAmmount()).max().orElse(0));
        ((UpdateLoanApplicationForm.Validator) form.getValidator()).amount.setMinValue((int) offers.stream().mapToDouble(o -> o.getMinAmmount()).min().orElse(0));
        ((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.setMaxValue(offers.stream().mapToInt(o -> o.getMaxInstallments()).max().orElse(0));
        ((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.setMinValue(minInstallments);

        return form;
    }

    public List<Date> getScheduleEnablesDates(EntityProductParams scheduleEntityProductParam) {
        List<Integer> gracePeriod = scheduleEntityProductParam.getGracePeriod();
        List<Integer> fixedDueDate = scheduleEntityProductParam.getFixedDueDate();
        List<Integer> avoidedWeekDays = scheduleEntityProductParam.getScheduleAvoidWeekDays();
        Boolean avoidHolidays = scheduleEntityProductParam.getScheduleAvoidHolidays();
        List<Holiday> holidays = catalogService.getHolidaysByCountry(scheduleEntityProductParam.getEntity().getCountryId());


        List<Date> enableDates = new ArrayList<>();

        // FDLM has a calendar of 30 days
        if (scheduleEntityProductParam.getId() == EntityProductParams.ENT_PROD_PARAM_FDLM) {
            int startDate = gracePeriod.get(0);
            int endDate = gracePeriod.get(1);

            int currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = 1; i <= endDate; i++) {
                currentDayOfMonth++;
//                Calendar cal = Calendar.getInstance();
//                int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                if (currentDayOfMonth > 30) {
                    currentDayOfMonth = 1;
                    currentMonth++;
                    if (currentMonth > 12) {
                        currentMonth = 1;
                        currentYear++;
                    }
                }

                if (i >= startDate) {
                    String dateString = StringUtils.leftPad(currentDayOfMonth + "", 2, '0') + "/" +
                            StringUtils.leftPad(currentMonth + "", 2, '0') + "/" + currentYear;
                    if (parseDateIfValid(dateString) != null) {
                        boolean addDate = true;
                        if (fixedDueDate != null) {
                            if (!fixedDueDate.contains(currentDayOfMonth)) {
                                addDate = false;
                            }
                        }

                        if (addDate)
                            enableDates.add(parseDateIfValid(dateString));
                    }
                }
            }
        } else if (scheduleEntityProductParam.getEntity().getId() == Entity.BANCO_DEL_SOL) {
            Calendar minRangeDate = Calendar.getInstance();
            minRangeDate.add(Calendar.DAY_OF_YEAR, gracePeriod.get(0));
            Calendar maxRangeDate = Calendar.getInstance();
            maxRangeDate.add(Calendar.DAY_OF_YEAR, gracePeriod.get(1));

            // First get the first fixed date for the grace period
            Date fixedDate = null;
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(minRangeDate.getTime());
                while (!cal.getTime().after(maxRangeDate.getTime())) {
                    //                boolean dateRun = false;
                    boolean addDate = true;
                    if (fixedDueDate != null) {
                        if (!fixedDueDate.contains(cal.get(Calendar.DAY_OF_MONTH))) {
                            addDate = false;
                        }
                    }
                    if (addDate) {
                        fixedDate = cal.getTime();
                        break; // Only add one date
                    }

                    cal.add(Calendar.DATE, 1);
                }
            }
            // Then check if its a holiday, or a week_avoid_date, go to the nearest available
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(fixedDate);
                boolean isOk = true;
                if (avoidedWeekDays != null && avoidedWeekDays.contains(cal.get(Calendar.DAY_OF_WEEK) - 1)) {
                    isOk = false;
                }
                if (avoidHolidays != null && avoidHolidays) {
                    if (holidays.stream().anyMatch(h -> DateUtils.isSameDay(h.getDate(), cal.getTime())))
                        isOk = false;
                }

                if (isOk) {
                    enableDates.add(cal.getTime());
                } else {
                    for (int i = 1; i <= 30; i++) {
                        if (!enableDates.isEmpty())
                            break;

                        Calendar newDate = Calendar.getInstance();
                        newDate.setTime(fixedDate);
                        newDate.add(Calendar.DATE, i);
                        boolean addDate = true;
                        if (avoidedWeekDays != null && avoidedWeekDays.contains(newDate.get(Calendar.DAY_OF_WEEK) - 1)) {
                            addDate = false;
                        }
                        if (avoidHolidays != null && avoidHolidays) {
                            if (holidays.stream().anyMatch(h -> DateUtils.isSameDay(h.getDate(), newDate.getTime())))
                                addDate = false;
                        }

                        if (addDate) {
                            enableDates.add(newDate.getTime());
                        }
                    }
                }
            }

        } else {
            Calendar minRangeDate = Calendar.getInstance();
            minRangeDate.add(Calendar.DAY_OF_YEAR, gracePeriod.get(0));
            Calendar maxRangeDate = Calendar.getInstance();
            maxRangeDate.add(Calendar.DAY_OF_YEAR, gracePeriod.get(1));

            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(minRangeDate.getTime());
                while (!cal.getTime().after(maxRangeDate.getTime())) {
                    //                boolean dateRun = false;
                    boolean addDate = true;
                    if (fixedDueDate != null) {
                        if (!fixedDueDate.contains(cal.get(Calendar.DAY_OF_MONTH))) {
                            addDate = false;
                        }
                    } else {
                        if (avoidedWeekDays != null && avoidedWeekDays.contains(cal.get(Calendar.DAY_OF_WEEK) - 1)) {
                            addDate = false;
                        }
                    }
                    //                }else{
                    //                    if (avoidedWeekDays != null && avoidedWeekDays.contains(cal.get(Calendar.DAY_OF_WEEK) - 1)) {
                    //                        addDate = false;
                    //                        if (scheduleEntityProductParam.getRunFixedDueDate() && fixedDueDate.contains(cal.get(Calendar.DAY_OF_MONTH))) {
                    //                            cal.add(Calendar.DATE, 1);
                    //                            fixedDueDate.add(cal.get(Calendar.DAY_OF_MONTH));
                    //                            dateRun = true;
                    //                        }
                    //                    }
                    //                    if (avoidHolidays != null && avoidHolidays) {
                    //                        if (holidays.stream().anyMatch(h -> DateUtils.isSameDay(h.getDate(), cal.getTime()))) {
                    //                            addDate = false;
                    //                            if (scheduleEntityProductParam.getRunFixedDueDate() && fixedDueDate.contains(cal.get(Calendar.DAY_OF_MONTH))) {
                    //                                cal.add(Calendar.DATE, 1);
                    //                                fixedDueDate.add(cal.get(Calendar.DAY_OF_MONTH));
                    //                                dateRun = true;
                    //                            }
                    //                        }
                    //                    }
                    //                }

                    if (addDate)
                        enableDates.add(cal.getTime());

                    //                if (!dateRun)
                    cal.add(Calendar.DATE, 1);
                }
            }

            if (enableDates.isEmpty()) {
                List<Integer> copiedFixedDueDate = fixedDueDate.stream().map(d -> d + 1).collect(Collectors.toList());

                Calendar cal = Calendar.getInstance();
                cal.setTime(minRangeDate.getTime());
                while (!cal.getTime().after(maxRangeDate.getTime())) {
                    boolean addDate = true;
                    if (copiedFixedDueDate != null && !copiedFixedDueDate.contains(cal.get(Calendar.DAY_OF_MONTH))) {
                        addDate = false;
                    }
                    if (avoidedWeekDays != null && avoidedWeekDays.contains(cal.get(Calendar.DAY_OF_WEEK) - 1)) {
                        addDate = false;
                    }
                    if (avoidHolidays != null && avoidHolidays) {
                        if (holidays.stream().anyMatch(h -> DateUtils.isSameDay(h.getDate(), cal.getTime())))
                            addDate = false;
                    }

                    if (addDate)
                        enableDates.add(cal.getTime());

                    cal.add(Calendar.DATE, 1);
                }
            }
            if (enableDates.isEmpty()) {
                List<Integer> copiedFixedDueDate = fixedDueDate.stream().map(d -> d + 2).collect(Collectors.toList());

                Calendar cal = Calendar.getInstance();
                cal.setTime(minRangeDate.getTime());
                while (!cal.getTime().after(maxRangeDate.getTime())) {
                    boolean addDate = true;
                    if (copiedFixedDueDate != null && !copiedFixedDueDate.contains(cal.get(Calendar.DAY_OF_MONTH))) {
                        addDate = false;
                    }
                    if (avoidedWeekDays != null && avoidedWeekDays.contains(cal.get(Calendar.DAY_OF_WEEK) - 1)) {
                        addDate = false;
                    }
                    if (avoidHolidays != null && avoidHolidays) {
                        if (holidays.stream().anyMatch(h -> DateUtils.isSameDay(h.getDate(), cal.getTime())))
                            addDate = false;
                    }

                    if (addDate)
                        enableDates.add(cal.getTime());

                    cal.add(Calendar.DATE, 1);
                }
            }
        }

        return enableDates;
    }

    private Date parseDateIfValid(String validDate) {
        try {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            format.setLenient(false);
            return format.parse(validDate);
        } catch (Exception ex) {
            return null;
        }
    }

    private List<LoanOffer> createLoanOffers(LoanApplication loanApplication) throws Exception {
        loanApplicationDao.createLoanOffers(loanApplication.getId());
        // For Alfin COnsumo, call the simular sin cliente
        if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getProductCategoryId() == ProductCategory.CONSUMO) {

            List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
            if (offers != null) {
               try {
                   regenerateAlfinOfferSchedules(offers, loanApplication);
               }
               catch (BT40147Exception ex){
                   this.loanApplicationDao.removeLoanOffers(loanApplication.getId());
                   loanApplicationDao.createLoanOffers(loanApplication.getId());
                   offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                   regenerateAlfinOfferSchedules(offers, loanApplication);
               }
            }
            return offers;
        } else
            return loanApplicationDao.getLoanOffers(loanApplication.getId());
    }

    private void regenerateAlfinOfferSchedules(List<LoanOffer> offers, LoanApplication loanApplication) throws Exception {
        String insuranceType = loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.AZTECA_INSURANCE_TYPE.getKey());
        if (insuranceType.equals(LoanApplication.FAMILY_PROTECTION_INSURANCE_TYPE + "")) {
            // Set the insurance plan for the offer
            for (LoanOffer loanOffer : offers) {
                if (loanOffer.getEntityCustomData() == null)
                    loanOffer.setEntityCustomData(new JSONObject());

                if (loanOffer.getAmmount() <= 3000) {
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_PLAN.getKey(), 1);
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_COST.getKey(), 8);
                } else if (loanOffer.getAmmount() <= 7000) {
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_PLAN.getKey(), 2);
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_COST.getKey(), 12);
                } else if (loanOffer.getAmmount() <= 10000) {
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_PLAN.getKey(), 3);
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_COST.getKey(), 18);
                } else if (loanOffer.getAmmount() <= 15000) {
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_PLAN.getKey(), 4);
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_COST.getKey(), 25);
                } else if (loanOffer.getAmmount() <= 25000) {
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_PLAN.getKey(), 5);
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_COST.getKey(), 40);
                } else if (loanOffer.getAmmount() <= 30000) {
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_PLAN.getKey(), 6);
                    loanOffer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.ALFIN_INSURANCE_COST.getKey(), 60);
                }
                loanApplicationDao.updateLoanOfferEntityCustomData(loanOffer.getId(), loanOffer.getEntityCustomData());
            }
        } else {
            boolean isValidDueDate = loanApplicationService.isValidDueDate(loanApplication, 0);
            if(!isValidDueDate){
                List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale(), JsonResolverDAO.EVALUATION_FOLLOWER_DB)
                        .stream()
                        .filter(e -> e.getApproved() != null && e.getApproved())
                        .collect(Collectors.toList());
                EntityProductParams scheduleEntityProductParam = null;
                for (LoanApplicationEvaluation e : evaluations) {
                    EntityProductParams param = catalogService.getEntityProductParamById(e.getEntityProductParameterId());
                    if (param.getGracePeriod() != null) {
                        scheduleEntityProductParam = param;
                        break;
                    }
                }
                this.loanApplicationService.updateAlfinDueDate(loanApplication, this.getScheduleEnablesDates(scheduleEntityProductParam), scheduleEntityProductParam);
                throw new BT40147Exception("INVALID_DATE");
            }
            Long activityId = Long.valueOf(BTApiRestServiceImpl.DEFAULT_ACTIVITY_ID);
            Integer activitySelected = JsonUtil.getIntFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.CUSTOM_ENTITY_ACTIVITY_ID.getKey(), null);
            if (activitySelected != null) {
                CustomEntityActivity activity = catalogService.getCustomActivity(activitySelected);
                if (activity != null && activity.getIdentifier() != null)
                    activityId = Long.valueOf(activity.getIdentifier());
            }
            BTPrestamosSimularResponse btPrestamosSimularResponse = null;
            for (LoanOffer loanOffer : offers) {
                try{
                    this.simulateAndValidateScheduleInvalidDateErrorAlfin(loanApplication, activityId, loanOffer, insuranceType);
                }
                catch (BT40147Exception ex){
                    this.loanApplicationService.updateAlfinDueDate(loanApplication, this.getScheduleEnablesDates(loanOffer.getEntityProductParam()), loanOffer.getEntityProductParam());
                    throw ex;
                }
            }
        }


    }


    private void simulateAndValidateScheduleInvalidDateErrorAlfin(LoanApplication loanApplication, Long activityId,  LoanOffer loanOffer, String insuranceType) throws Exception {
        BTPrestamosSimularAmortizableSinClienteRequest.SBTDatosAmortizable sdtDatosAmortizable = new BTPrestamosSimularAmortizableSinClienteRequest.SBTDatosAmortizable();
        sdtDatosAmortizable.setMonto(loanOffer.getAmmount());
        sdtDatosAmortizable.setPeriodoCuotas(loanOffer.getEntityProductParam().getFixedInstallmentDays());
        sdtDatosAmortizable.setProductoUId(insuranceType.equals(LoanApplication.WITH_DEVOLUTION_INSURANCE_TYPE + "") ? 95L : 94L);
        sdtDatosAmortizable.setCantidadCuotas(loanOffer.getInstallments());
        sdtDatosAmortizable.setFechaPrimerPago(btApiRestService.convertDateToStringFormat(loanOffer.getFirstDueDate()));
        sdtDatosAmortizable.setActividad(activityId);
        BTPrestamosSimularResponse btPrestamosSimularResponse = btApiRestService.btPrestamosSimularSinCliente(loanApplication, null, sdtDatosAmortizable);
        long daysUntilFirstDueDate = utilService.daysBetween(new Date(), loanOffer.getFirstDueDate());
        for (OriginalSchedule schedule : loanOffer.getOfferSchedule()) {
            // Update the values from the WS
            schedule.setInstallmentAmount(btPrestamosSimularResponse.getSdtSimulacion().getCronograma().getsBTCuotaPrestamoAlta().get(schedule.getInstallmentId() - 1).getImporte());
            schedule.setDueDate(btApiRestService.convertStringToDate(btPrestamosSimularResponse.getSdtSimulacion().getCronograma().getsBTCuotaPrestamoAlta().get(schedule.getInstallmentId() - 1).getFechaPago()));
            // Recalculate insurance
            OriginalSchedule beforeSchedule = schedule.getInstallmentId() <= 1 ? null : loanOffer.getOfferSchedule().get(schedule.getInstallmentId() - 2);
            if (schedule.getInstallmentId() == 1)
                schedule.setInsurance(Math.abs(1 + (daysUntilFirstDueDate - 1 - 30) / 30.0) * loanOffer.getAmmount() * loanOffer.getInsurance());
            else
                schedule.setInsurance(1 * beforeSchedule.getRemainingCapital() * loanOffer.getInsurance());
            // Recalculate interest
            if (schedule.getInstallmentId() == 1)
                schedule.setInterest(loanOffer.getAmmount() * schedule.getEffectiveDailyRateFactor());
            else
                schedule.setInterest(1 * beforeSchedule.getRemainingCapital() * schedule.getEffectiveDailyRateFactor());
            // Recalculate installmentCapital
            schedule.setInstallmentCapital(schedule.getInstallmentAmount() - schedule.getInsurance() - schedule.getTotalInterest());
            // Recalculate remaining capital
            if (schedule.getInstallmentId() == 1)
                schedule.setRemainingCapital(loanOffer.getAmmount() - schedule.getInstallmentCapital());
            else
                schedule.setRemainingCapital(beforeSchedule.getRemainingCapital() - schedule.getInstallmentCapital());
            loanApplicationDao.updateOfferOriginalScheduleData(loanOffer.getId(), schedule.getInstallmentId(), schedule.getDueDate(), schedule.getInstallmentAmount(), schedule.getInstallmentCapital(), schedule.getInterest(), schedule.getInsurance(), schedule.getRemainingCapital());
//                    loanOffer.getOfferSchedule().set(schedule.getInstallmentId()-1, schedule);
        }
        loanOffer.setInstallmentAmmount(loanOffer.getOfferSchedule().get(0).getInstallmentAmount());
        loanOffer.setInstallmentAmountAvg(loanOffer.getOfferSchedule().get(0).getInstallmentAmount());
        loanApplicationDao.updateLoanOfferInstallmentAmountAndAvg(loanOffer.getId(), loanOffer.getInstallmentAmmount(), loanOffer.getInstallmentAmountAvg());
    }
}


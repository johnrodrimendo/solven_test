package com.affirm.common.service.question;

import com.affirm.common.dao.EntityProductEvaluationProcessDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question125Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationEvaluation;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.FileService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.FormValidationException;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("question125Service")
public class Question125Service extends AbstractQuestionService<Question125Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EntityProductEvaluationProcessDAO entityProductEvaluationProcessDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private FileService fileService;
    @Autowired
    private MessageSource messageSource;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                if (!loanApplication.getStatus().getId().equals(LoanApplicationStatus.CROSS_SELLING_OFFER)) {
                    loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.CROSS_SELLING_OFFER, null);
                    loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                }

                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                Product guaranteedProduct = catalogService.getProduct(Product.GUARANTEED);
                ProductMaxMinParameter guaranteedMaxMinParameter = guaranteedProduct.getProductParams(loanApplication.getCountryId());
                Product debtConsolidationProduct = catalogService.getProduct(Product.DEBT_CONSOLIDATION_OPEN);
                ProductMaxMinParameter consolidationMaxMinParameter = debtConsolidationProduct.getProductParams(loanApplication.getCountryId());
                Product prepayCardProduct = catalogService.getProduct(Product.PREPAY_CARD);
                ProductMaxMinParameter prepayCardMaxMinParameter = prepayCardProduct.getProductParams(loanApplication.getCountryId());

                List<Integer> entityProductsToShow = loanApplicationService.getRescueSreenParams(loanApplication.getId(), loanApplication.getPersonId()).getEntityProductParams();
                int rescueOfferCount = entityProductsToShow.size();
                attributes.put("showGuaranteed", entityProductsToShow.contains(EntityProductParams.ENT_PROD_PARAM_ACCESO_GARANTIZADO));
                attributes.put("showConsolidation", entityProductsToShow.contains(EntityProductParams.ENT_PROD_PARAM_EFL_DEBT_CONSOLIDATION_OPEN));
                attributes.put("showPrepayCard", entityProductsToShow.contains(EntityProductParams.ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO));
                attributes.put("showLeadsRedirect", entityProductsToShow.contains(EntityProductParams.ENT_PROD_PARAM_WENANCE_LEAD));
                attributes.put("showAutoplan", entityProductsToShow.contains(EntityProductParams.ENT_PROD_PARAM_AUTOPLAN_LEAD));
                attributes.put("showILCLead", entityProductsToShow.contains(EntityProductParams.ENT_PROD_PARAM_INVERSIONES_LA_CRUZ));
                attributes.put("showPrestamype", entityProductsToShow.contains(EntityProductParams.ENT_PROD_PARAM_PRESTAMYPE_PRESTAMO_GAR_HIPOT));
                attributes.put("personFirstName", person.getFirstName());
                attributes.put("loanApplication", loanApplication);
                attributes.put("guaranteedParam", guaranteedMaxMinParameter);
                attributes.put("consolidationParam", consolidationMaxMinParameter);
                attributes.put("prepayCardParam", prepayCardMaxMinParameter);
                attributes.put("rescueOfferCount", rescueOfferCount);

                if(loanApplication.getOrigin() == LoanApplication.ORIGIN_LANDING_LEADS){
                    if(rescueOfferCount > 1)
                        attributes.put("title", messageSource.getMessage("static.question.125.leads.p2", new Object[]{person.getFirstName()}, locale));
                    else
                        attributes.put("title", messageSource.getMessage("static.question.125.leads.p", new Object[]{person.getFirstName()}, locale));
                }
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question125Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form != null && form.getProduct() != null) {
                    if (form.getProduct().equals(Product.GUARANTEED))
                        return "GUARANTEED";
                    if (form.getProduct().equals(Product.DEBT_CONSOLIDATION_OPEN))
                        return "CONSOLIDATION";
                    if (form.getProduct().equals(Product.LEADS_CONSUMO))
                        return "WENANCE";
                    if (form.getProduct().equals(Product.PREPAY_CARD)) {
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        if (loanApplication.getQuestionSequence().stream().anyMatch(s -> s.getId().equals(ProcessQuestion.Question.Constants.RUNNING_EVALUATION))) {
                            return "EVALUATION";
                        } else {
                            return "PRELIMINARY_EVALUATION";
                        }
                    }
                } else if (form != null && form.getEntityProductParam() != null) {
                    if (form.getEntityProductParam().equals(EntityProductParams.ENT_PROD_PARAM_AUTOPLAN_LEAD))
                        return "AUTOPLAN";
                    if (form.getEntityProductParam().equals(EntityProductParams.ENT_PROD_PARAM_PRESTAMYPE_PRESTAMO_GAR_HIPOT))
                        return "PRESTAMYPE";
                }

                return "NOT_INTERESTED";
//                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
//                if (loanApplication.getQuestionSequence().stream().anyMatch(s -> s.getId().equals(ProcessQuestion.Question.Constants.RUNNING_EVALUATION))) {
//                    return "EVALUATION";
//                } else if (loanApplication.getQuestionSequence().stream().anyMatch(s -> s.getId().equals(ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION))) {
//                    return "PRELIMINARY_EVALUATION";
//                }
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question125Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form != null && form.getProduct() != null) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                    List<Integer> productsToShow = loanApplicationService.getRescueSreenParams(loanApplication.getId(), loanApplication.getPersonId()).getProducts();
                    if (!productsToShow.contains(form.getProduct())) {
                        throw new FormValidationException("El producto no existe");
                    }
                }
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question125Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form != null && form.getProduct() != null) {
                    entityProductEvaluationProcessDao.updateIsSelectable(true, id, form.getProduct());
                } else if (form != null && form.getEntityProductParam() != null) {
                    EntityProductParams entityProductParams = catalogService.getEntityProductParamById(form.getEntityProductParam());
                    entityProductEvaluationProcessDao.updateIsSelectable(true, id, entityProductParams.getProduct().getId(), entityProductParams.getEntity().getId());

                    if (entityProductParams.getId() == EntityProductParams.ENT_PROD_PARAM_AUTOPLAN_LEAD) {
                        loanApplicationDao.updateSelectedEntityProductParamId(id, entityProductParams.getId());
                        loanApplicationDao.updateSelectedEntityId(id, entityProductParams.getEntity().getId());
                        loanApplicationDao.updateSelectedProductId(id, entityProductParams.getProduct().getId());
                    }

                } else {
                    List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(id, locale, JsonResolverDAO.EVALUATION_FOLLOWER_DB);
                    if (evaluations != null && !evaluations.isEmpty()) {
                        loanApplicationDao.updateLoanApplicationStatus(id, LoanApplicationStatus.REJECTED, null);
                    } else {
                        loanApplicationDao.updateLoanApplicationStatus(id, LoanApplicationStatus.REJECTED_AUTOMATIC, null);
                    }
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                if (!loanApplicationService.getRescueSreenParams(loanApplication.getId(), loanApplication.getPersonId()).getShowScreen()) {
                    return "NOT_INTERESTED";
                }
                if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.PRESTAMYPE){
                    return "PRESTAMYPE";
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
                    case "contract":
                        byte[] pdf = fileService.getContract(catalogService.getEntityProductParamById(EntityProductParams.ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO).getContract().getPdfPath());

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_PDF);
                        ResponseEntity<byte[]> downloadablePdf = new ResponseEntity<byte[]>(pdf, headers, HttpStatus.OK);
                        return downloadablePdf;
                    case "ilcLead":
                        loanApplicationDao.updateSelectedEntityProductParamId(id, EntityProductParams.ENT_PROD_PARAM_INVERSIONES_LA_CRUZ);
                        loanApplicationDao.updateSelectedEntityId(id, Entity.INVERSIONES_LA_CRUZ);
                        loanApplicationDao.updateSelectedProductId(id, Product.LEADS_CONSUMO);

                        loanApplicationDao.updateLoanApplicationStatus(id, LoanApplicationStatus.LEAD_REFERRED, null);
                        return AjaxResponse.ok(null);
                }
                break;
        }
        throw new Exception("No method configured");
    }
}


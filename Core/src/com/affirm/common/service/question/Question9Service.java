package com.affirm.common.service.question;

import com.affirm.common.dao.BotDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.ProofNotifierService;
import com.affirm.common.service.SelfEvaluationService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.ResponseEntityException;
import com.affirm.system.configuration.Configuration;
import com.affirm.warmi.service.WarmiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("question9Service")
public class Question9Service extends AbstractQuestionService<FormGeneric> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private SelfEvaluationService selfEvaluationService;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ProofNotifierService proofNotifierService;
    @Autowired
    private BotDAO botDao;
    @Autowired
    private WarmiService warmiService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                String name = person.getFirstName();
                LoanApplicationEvaluationsProcess evaluationsProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());

                // If the bot doesnt run yet, run it
                if (evaluationsProcess.getReadyForEvaluation() == null) {
                    loanApplicationDao.updateEvaluationProcessReadyPreEvaluation(loanApplication.getId(), true);
                    loanApplicationDao.updateEvaluationProcessReadyEvaluation(loanApplication.getId(), true);
                    // call evaluation bot
                    loanApplicationService.runEvaluationBot(loanApplication.getId(), false);
                }

                attributes.put("isSelfEvaluation", false);
                attributes.put("isEvaluation", true);
                attributes.put("loanApplication", loanApplication);

                if (((Integer) Entity.BANCO_DEL_SOL).equals(loanApplication.getEntityId()))
                    attributes.put("message", "Estamos procesando la información");
                else if (((Integer) Entity.BANBIF).equals(loanApplication.getEntityId()))
                        attributes.put("message", "Estamos revisando tus ofertas, por favor necesitamos unos segundos más...");
                else
                    attributes.put("message", messageSource.getMessage("questions.block.informationProcessing", new Object[]{name}, Configuration.getDefaultLocale()));

                attributes.put("showCarousel", loanApplication.getEntityId() == null);

                if(loanApplication.getEntityId() != null &&
                        Arrays.asList(Entity.AZTECA).contains(loanApplication.getEntityId()) &&
                        loanApplication.getProduct() != null &&
                        Arrays.asList(Product.VALIDACION_IDENTIDAD).contains(loanApplication.getProduct().getId()) &&
                        Arrays.asList(LoanApplicationStatus.REJECTED_AUTOMATIC,LoanApplicationStatus.REJECTED,LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION).contains(loanApplication.getStatus().getId())
                ){
                    loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.EVAL_APPROVED, null);
                }
                break;
            case SELFEVALUATION:
                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, locale);
                if (selfEvaluation.getCountryParam().getId() == CountryParam.COUNTRY_ARGENTINA) {
                    if (selfEvaluation.getBots() == null) {
                        // Run SelfEvaluation
                        selfEvaluationService.runSelfEvaluationArgentina(selfEvaluation);
                    }
                }

                attributes.put("isSelfEvaluation", true);
                attributes.put("isEvaluation", false);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, FormGeneric form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                String questionToGo = getQuestionResultToGoEvaluation(loanApplication);
                if(questionToGo != null)
                    return questionToGo;
                else
                    throw new ResponseEntityException(AjaxResponse.ok(null));

            case SELFEVALUATION:
                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, Configuration.getDefaultLocale());
                if (selfEvaluation.getCountryParam().getId() == CountryParam.COUNTRY_ARGENTINA) {
                    // Check if the bots finished
                    boolean botsFinished = true;
                    for (Integer queryBotId : selfEvaluation.getBots()) {
                        QueryBot queryBot = botDao.getQueryBot(queryBotId);
                        if (queryBot.getStatusId() == QueryBot.STATUS_QUEUE || queryBot.getStatusId() == QueryBot.STATUS_RUNNING) {
                            botsFinished = false;
                            break;
                        }
                    }

                    if (botsFinished) {
                        selfEvaluationDao.runSelfEvaluation(selfEvaluation.getId());
                        proofNotifierService.notifySelfEvaluation(selfEvaluation);
                        return "APROBADO";
                    }
                    throw new ResponseEntityException(AjaxResponse.ok(null));

                } else if (selfEvaluation.getCountryParam().getId() == CountryParam.COUNTRY_PERU) {
                    proofNotifierService.notifySelfEvaluation(selfEvaluation);
                    return "APROBADO";
                }
                break;
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, FormGeneric form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, FormGeneric form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "status":
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        LoanApplicationEvaluationsProcess evaluationsProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());

                        // If the preliminary evaluation has failed, then go to the disapproval question
                        if (evaluationsProcess.getPreEvaluationStatus() != null && evaluationsProcess.getPreEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_FINISHED) {
                            LoanApplicationPreliminaryEvaluation loanPreEvaluation = loanApplicationService.getLastPreliminaryEvaluation(loanApplication.getId(), Configuration.getDefaultLocale(), null, true);
                            if (loanPreEvaluation != null && loanPreEvaluation.getStatus() == 'S') {
                                if (!loanPreEvaluation.getApproved()) {
                                    return AjaxResponse.ok("hasResult");
                                }
                            }
                        }


                        // There should be a 15 seconds delay for the evaluation bots to finish
                        LoanApplicationEvaluation loanEvaluation = null;
                        if ((loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA) || (evaluationsProcess.getEvaluationStartDate() != null && new Date().getTime() - evaluationsProcess.getEvaluationStartDate().getTime() > 15 * 1000)) {
                            loanEvaluation = loanApplicationService.getLastEvaluation(loanApplication.getId(), loanApplication.getPersonId(), Configuration.getDefaultLocale(), true);
                        }
                        if (evaluationsProcess.getEvaluationStatus() != null && loanEvaluation != null) {
                            if (evaluationsProcess.getEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_RUNNING_DELAYED ||
                                    evaluationsProcess.getEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_STOPPED) {
                                updateToSendDelayEmail(loanApplication.getId(), evaluationsProcess);
                                return AjaxResponse.ok("delayed");
                            } else if (evaluationsProcess.getEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_FINISHED && loanEvaluation.getApproved()) {
                                return AjaxResponse.ok("hasResult");
                            } else if (evaluationsProcess.getEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_FINISHED && !loanEvaluation.getApproved()) {
                                return AjaxResponse.ok("hasResult");
                            }
                        } else if (evaluationsProcess.getPreEvaluationStatus() != null) {
                            if (evaluationsProcess.getPreEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_RUNNING_DELAYED) {
                                updateToSendDelayEmail(loanApplication.getId(), evaluationsProcess);
                                return AjaxResponse.ok("delayed");
                            }
                        }

                        return AjaxResponse.ok("waiting");
                }
                break;
            case SELFEVALUATION:
                switch (path) {
                    case "status":
                        SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, Configuration.getDefaultLocale());
                        if (selfEvaluation.getCountryParam().getId() == CountryParam.COUNTRY_ARGENTINA) {

                            // Check if the bots finished
                            boolean botsNotFinishYet = selfEvaluation.getBots().stream().anyMatch(i -> {
                                QueryBot queryBot = botDao.getQueryBot(i);
                                return queryBot.getStatusId() == QueryBot.STATUS_QUEUE || queryBot.getStatusId() == QueryBot.STATUS_RUNNING;
                            });

                            if (!botsNotFinishYet) {
                                return AjaxResponse.ok("hasResult");
                            }
                            return AjaxResponse.ok("waiting");

                        } else if (selfEvaluation.getCountryParam().getId() == CountryParam.COUNTRY_PERU) {
                            return AjaxResponse.ok("hasResult");
                        }
                        break;
                }
                break;
        }
        return null;
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                LoanApplicationEvaluationsProcess evaluationsProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());

                // If the preliminary evaluation has failed, then go to the disapproval question
                if (evaluationsProcess.getPreEvaluationStatus() != null && evaluationsProcess.getPreEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_FINISHED) {
                    LoanApplicationPreliminaryEvaluation loanPreEvaluation = loanApplicationService.getLastPreliminaryEvaluation(loanApplication.getId(), locale, null, true);
                    if (loanPreEvaluation != null) {
                        if (loanPreEvaluation.getApproved() != null && !loanPreEvaluation.getApproved()) {
                            if (hasRescueOffers(loanApplication.getId(), loanApplication.getPersonId()))
                                return "RESCUE_OFFERS";
                            return "DESAPROBADO";
                        }
                    }
                }
                break;
            case SELFEVALUATION:
//                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, locale);
//                if (selfEvaluation.getCountryParam().getId() == CountryParam.COUNTRY_ARGENTINA) {
//                    if (selfEvaluation.getBots() != null) {
//                        // Check if the bots finished
//                        boolean botsFinished = true;
//                        for (Integer queryBotId : selfEvaluation.getBots()) {
//                            QueryBot queryBot = botDao.getQueryBot(queryBotId);
//                            if (queryBot.getStatusId() == QueryBot.STATUS_QUEUE || queryBot.getStatusId() == QueryBot.STATUS_RUNNING) {
//                                botsFinished = false;
//                                break;
//                            }
//                        }
//                        if (botsFinished)
//                            return "APROBADO";
//                    }
//                }
                break;
        }
        return null;
    }

    private void updateToSendDelayEmail(int loanApplicationId, LoanApplicationEvaluationsProcess evaluationProcess) {
        if (evaluationProcess.getSendDelayedEmail() == null)
            loanApplicationDao.updateEvaluationProcessSendDelayedEmail(loanApplicationId, true);
    }

    private boolean hasRescueOffers(int loanApplicationId, int personId) throws Exception {
        return loanApplicationService.getRescueSreenParams(loanApplicationId, personId).getShowScreen();
    }

    private String getQuestionResultToGoEvaluation(LoanApplication loanApplication) throws Exception{
        LoanApplicationEvaluationsProcess evaluationsProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());

        // If the preliminary evaluation has failed, then go to the disapproval question
        if (evaluationsProcess.getPreEvaluationStatus() != null && evaluationsProcess.getPreEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_FINISHED) {
            LoanApplicationPreliminaryEvaluation loanPreEvaluation = loanApplicationService.getLastPreliminaryEvaluation(loanApplication.getId(), Configuration.getDefaultLocale(), null, true);
            if (loanPreEvaluation != null) {
                if (!loanPreEvaluation.getApproved()) {
                    if (hasRescueOffers(loanApplication.getId(), loanApplication.getPersonId()))
                        return "RESCUE_OFFERS";
                    return "DESAPROBADO";
                }
            }
        }

        // There should be a 15 seconds delay for the evaluation bots to finish
        LoanApplicationEvaluation loanEvaluation = null;
        if (evaluationsProcess.getEvaluationStartDate() == null || (evaluationsProcess.getEvaluationStartDate() != null && new Date().getTime() - evaluationsProcess.getEvaluationStartDate().getTime() > 15 * 1000)) {
            loanEvaluation = loanApplicationService.getLastEvaluation(loanApplication.getId(), loanApplication.getPersonId(), Configuration.getDefaultLocale(), true);
        }

        if (loanEvaluation != null) {
            // Theres result
            if (loanEvaluation.getApproved()) {
                if (hasRescueOffers(loanApplication.getId(), loanApplication.getPersonId()))
                    return "RESCUE_OFFERS";

                Person person = personDao.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale());
                if(loanApplication.getEntityId() == null || !Arrays.asList(Entity.AZTECA).contains(loanApplication.getEntityId())) warmiService.runProcess(loanApplication, person);

                return "APROBADO";
            } else if (!loanEvaluation.getApproved()) {
                if (hasRescueOffers(loanApplication.getId(), loanApplication.getPersonId()))
                    return "RESCUE_OFFERS";
                return "DESAPROBADO";
            }
        } else if (evaluationsProcess.getEvaluationStatus() != null) {
            if (evaluationsProcess.getEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_RUNNING_DELAYED ||
                    evaluationsProcess.getEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_STOPPED) {
                updateToSendDelayEmail(loanApplication.getId(), evaluationsProcess);
//                        throw new ResponseEntityException(AjaxResponse.ok("delayed"));
            }
        } else if (evaluationsProcess.getPreEvaluationStatus() != null) {
            if (evaluationsProcess.getPreEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_RUNNING_DELAYED) {
                updateToSendDelayEmail(loanApplication.getId(), evaluationsProcess);
//                        throw new ResponseEntityException(AjaxResponse.ok("delayed"));
            }
        }
        return null;
    }
}


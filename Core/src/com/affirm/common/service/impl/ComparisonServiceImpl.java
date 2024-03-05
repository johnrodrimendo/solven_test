/**
 *
 */
package com.affirm.common.service.impl;

import com.affirm.common.dao.ComparisonDAO;
import com.affirm.common.model.catalog.ComparisonCreditCost;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.ProcessQuestionsConfiguration;
import com.affirm.common.model.transactional.Comparison;
import com.affirm.common.model.transactional.ComparisonResult;
import com.affirm.common.model.transactional.OriginalSchedule;
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ComparisonService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.Irr;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */

@Service("comparisonService")
public class ComparisonServiceImpl implements ComparisonService {

    private static Logger logger = Logger.getLogger(ComparisonServiceImpl.class);
    public static final int COMPARISON_RATE_TYPE_MIN = 1;
    public static final int COMPARISON_RATE_TYPE_MAX = 2;
    public static final int COMPARISON_RATE_TYPE_PERSONALIZED = 3;

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ComparisonDAO comparisonDao;
    @Autowired
    private UtilService utilService;

    @Override
    public String generateComparisonToken(int comparisonId) {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("comparison", comparisonId);
        return CryptoUtil.encrypt(jsonParam.toString());
    }

    @Override
    public Integer getIdFromToken(String token) throws Exception {
        JSONObject jsonObject = new JSONObject(CryptoUtil.decrypt(token));
        return JsonUtil.getIntFromJson(jsonObject, "comparison", null);
    }

    @Override
    public ProcessQuestionsConfiguration getComparisonProcess(Comparison comparison) throws Exception {
        ProcessQuestionsConfiguration config = new ProcessQuestionsConfiguration();
        config.fillFromDb(new JSONObject(Configuration.COMPARISON_PROCESS));
        return config;
    }

    @Override
    public Integer getQuestionIdByResult(Comparison comparison, int questionId, String processQuestionResult) throws Exception {
        ProcessQuestion processQuestion = getComparisonProcess(comparison).getQuestions().stream().filter(q -> q.getId() == questionId).findFirst().orElse(null);
        if (processQuestion != null) {
            return processQuestion.getResultQuestionId(processQuestionResult);
        }
        return null;
    }

    @Override
    public Integer forwardByResult(Comparison comparison, String processQuestionResult, HttpServletRequest request) throws Exception {

        Integer questionIdtoUpdate;

        // If there is no current question, update with the first question in configuration
        if (comparison.getCurrentQuestionId() == null) {
            questionIdtoUpdate = getComparisonProcess(comparison).getFirstQuestionId();
        }
        // Else, update with the result question
        else {
            questionIdtoUpdate = getQuestionIdByResult(comparison, comparison.getCurrentQuestionId(), processQuestionResult);
        }
        comparisonDao.updateCurrentQuestion(comparison.getId(), questionIdtoUpdate);
        comparison.setCurrentQuestionId(questionIdtoUpdate);

        // Update finish date last question
        if (!comparison.getQuestionSequence().isEmpty())
            comparison.getQuestionSequence().get(comparison.getQuestionSequence().size() - 1).setFinishDate(new Date());

        // Update sequence
        comparison.getQuestionSequence().add(new ProcessQuestionSequence(questionIdtoUpdate, ProcessQuestionSequence.TYPE_FORWARD, new Date(), null, request != null ? request.getHeader("User-Agent") : null));
        comparisonDao.updateQuestionSequence(comparison.getId(), new Gson().toJson(comparison.getQuestionSequence()));

        return questionIdtoUpdate;
    }

    @Override
    public Integer backward(Comparison comparison, HttpServletRequest request) throws Exception {

        Integer questionIdtoUpdate = null;

        // If there is no current question, update with the first question in configuration
        if (comparison.getQuestionSequence().size() > 1) {
            for (int i = comparison.getQuestionSequence().size() - 1; i >= 0; i--) {
                ProcessQuestionSequence question = comparison.getQuestionSequence().get(i);
                if (question.getId().intValue() == comparison.getCurrentQuestionId() && question.getType() == ProcessQuestionSequence.TYPE_FORWARD) {
                    questionIdtoUpdate = comparison.getQuestionSequence().get(i - 1).getId();
                    break;
                }
            }
        }
        if (questionIdtoUpdate == null) {
            throw new Exception("Cant go back!!");
        }

        comparisonDao.updateCurrentQuestion(comparison.getId(), questionIdtoUpdate);
        comparison.setCurrentQuestionId(questionIdtoUpdate);

        // Update finish date last question
        if (!comparison.getQuestionSequence().isEmpty())
            comparison.getQuestionSequence().get(comparison.getQuestionSequence().size() - 1).setFinishDate(new Date());

        // Update sequence
        comparison.getQuestionSequence().add(new ProcessQuestionSequence(questionIdtoUpdate, ProcessQuestionSequence.TYPE_BACKWARD, new Date(), null, request != null ? request.getHeader("User-Agent") : null));
        comparisonDao.updateQuestionSequence(comparison.getId(), new Gson().toJson(comparison.getQuestionSequence()));

        return questionIdtoUpdate;
    }

    @Override
    public List<ComparisonResult> executeComparison(Comparison comparison, Locale locale, int comparisonRateType, Integer selfEvaluaionScore, int offset) throws Exception {
        List<ComparisonResult> results = comparisonDao.executeComparison(comparison.getId(), locale);
        if (results == null || offset > results.size())
            return null;

        for (ComparisonResult result : results) {
            MutableDouble desgravamen = new MutableDouble();
            MutableDouble desgravamenPrimaUnica = new MutableDouble();

            result.getCosts().stream().filter(c -> c.getComparisonCreditCost().getId() == ComparisonCreditCost.DESGRAVAMEN).findFirst().ifPresent(c -> {
                switch (c.getComparisonCreditCost().getType()) {
                    case 'P':
                        desgravamen.setValue(c.getValue() / 100);
                        break;
                    case 'V':
                        desgravamen.setValue(c.getValue());
                        break;
                }
            });
            result.getCosts().stream().filter(c -> c.getComparisonCreditCost().getId() == ComparisonCreditCost.DESGRAVAMEN_PRIMA_UNICA).findFirst().ifPresent(c -> {
                switch (c.getComparisonCreditCost().getType()) {
                    case 'P':
                        desgravamenPrimaUnica.setValue(c.getValue() / 100);
                        break;
                    case 'V':
                        desgravamenPrimaUnica.setValue(c.getValue());
                        break;
                }
            });

            double effectiveAnnualrate;
            if (comparisonRateType == COMPARISON_RATE_TYPE_PERSONALIZED && selfEvaluaionScore != null) {
                double rateDifference = result.getMaxEffectiveAnualRate() - result.getMinEffectiveAnualRate();
                if (rateDifference <= 0) {
                    effectiveAnnualrate = result.getMinEffectiveAnualRate();
                } else {
                    double multiplier = rateDifference / 4.0;
                    switch (selfEvaluaionScore) {
                        case 5:
                            effectiveAnnualrate = result.getMinEffectiveAnualRate();
                            break;
                        case 4:
                            effectiveAnnualrate = result.getMinEffectiveAnualRate() + (multiplier * 1.0);
                            break;
                        case 3:
                            effectiveAnnualrate = result.getMinEffectiveAnualRate() + (multiplier * 2.0);
                            break;
                        case 2:
                            effectiveAnnualrate = result.getMinEffectiveAnualRate() + (multiplier * 3.0);
                            break;
                        default:
                            effectiveAnnualrate = result.getMinEffectiveAnualRate() + (multiplier * 4.0);
                            break;
                    }
                }
            } else if (comparisonRateType == COMPARISON_RATE_TYPE_MAX) {
                effectiveAnnualrate = result.getMaxEffectiveAnualRate();
            } else {
                effectiveAnnualrate = result.getMinEffectiveAnualRate();
            }
            setComparisonResultPayments(
                    comparison.getAmount(),
                    comparison.getInstallments(),
                    effectiveAnnualrate,
                    desgravamen.getValue(), desgravamenPrimaUnica.getValue(), result);
            result.setEffectiveAnualRate(effectiveAnnualrate);
        }

        // Order by first the ones that can apply
        List<ComparisonResult> firstOnes = new ArrayList<>(results.stream().filter(r -> r.canApplyOnlineSolven()).collect(Collectors.toList()));
        List<ComparisonResult> lastOnes = new ArrayList<>(results.stream().filter(r -> !r.canApplyOnlineSolven()).collect(Collectors.toList()));

        firstOnes.sort((o1, o2) -> o1.getMonthlyPaymentAvg().compareTo(o2.getMonthlyPaymentAvg()));
        lastOnes.sort((o1, o2) -> o1.getMonthlyPaymentAvg().compareTo(o2.getMonthlyPaymentAvg()));

        results.clear();
        results.addAll(firstOnes);
        results.addAll(lastOnes);

        // Return by offset and limit
        return results.subList(offset, Math.min(offset + 10, results.size()));
    }

    @Override
    public void setComparisonResultPayments(Double amount, int installment, Double tea, Double desgravamen, Double desgravamenPrimaUnica, ComparisonResult comparisonResult) throws Exception {

        double tem = Math.pow(1.0 + tea / 100.0, (1.0 / 12.0)) - 1;
//        tem = tem/100.0;
        double fee = (amount * (1 + (desgravamenPrimaUnica != null ? desgravamenPrimaUnica : 0))) * ((tem * Math.pow(1 + tem, installment)) / ((Math.pow(1 + tem, installment)) - 1));

        List<OriginalSchedule> schedule = new ArrayList<>();

        // first payment
        OriginalSchedule firstPayment = new OriginalSchedule();
        firstPayment.setRemainingCapital(amount * (1 + (desgravamenPrimaUnica != null ? desgravamenPrimaUnica : 0)));
        firstPayment.setInstallmentAmount(-amount);
        schedule.add(firstPayment);

        // Others payments
        for (int i = 0; i < installment; i++) {
            OriginalSchedule payment = new OriginalSchedule();
            payment.setInterest(schedule.get(i).getRemainingCapital() * tem);
            payment.setInstallmentCapital(fee - payment.getInterest());
            payment.setRemainingCapital(schedule.get(i).getRemainingCapital() - payment.getInstallmentCapital());
            payment.setInsurance(desgravamen != null ? payment.getRemainingCapital() * desgravamen : 0);
            MutableDouble monthlyCharges = new MutableDouble();
            if (comparisonResult.getCosts().stream().anyMatch(r -> r.getComparisonCreditCost().getId() != ComparisonCreditCost.DESGRAVAMEN && r.getComparisonCreditCost().getId() != ComparisonCreditCost.DESGRAVAMEN_PRIMA_UNICA)) {
                comparisonResult.getCosts().stream().filter(r -> r.getComparisonCreditCost().getId() != ComparisonCreditCost.DESGRAVAMEN && r.getComparisonCreditCost().getId() != ComparisonCreditCost.DESGRAVAMEN_PRIMA_UNICA).forEach(r -> {
                    if (r.getComparisonCreditCost().getType() == 'V') {
                        monthlyCharges.add(r.getValue());
                    } else if (r.getComparisonCreditCost().getType() == 'P') {
                        monthlyCharges.add(payment.getRemainingCapital() * (r.getValue() / 100));
                    }
                });
            }
            payment.setInstallmentFactor(monthlyCharges.doubleValue());
            payment.setInstallmentAmount(Math.round((payment.getInterest() + payment.getInstallmentCapital() + payment.getInsurance() + monthlyCharges.doubleValue()) * 100.0) / 100.0);

            schedule.add(payment);
        }

        // Calculate TCEA
        double[] installmentAmounts = new double[schedule.size()];
        for (int i = 0; i < schedule.size(); i++) {
            installmentAmounts[i] = schedule.get(i).getInstallmentAmount();
        }
        double tcea = Math.pow(1 + Irr.irr(installmentAmounts), 12) - 1;

        // Calculate total payment
        double totalPayment = 0;
        for (int i = 1; i < schedule.size(); i++) {
            totalPayment += schedule.get(i).getInstallmentAmount();
        }

        // Calculate average monthly payment
        double monthlyPaymentAvg = totalPayment / (schedule.size() - 1);

        // Calculate average insurance payment
        double totalInsurance = 0;
        for (int i = 1; i < schedule.size(); i++) {
            totalInsurance += schedule.get(i).getInsurance();
        }
        double insurancePaymentAvg = totalInsurance / (schedule.size() - 1);

        // Calculate average monthly costs
        double totalMonthlyCosts = 0;
        for (int i = 1; i < schedule.size(); i++) {
            totalMonthlyCosts += schedule.get(i).getInstallmentFactor();
        }
        double monthlyCostsAvg = totalMonthlyCosts / (schedule.size() - 1);

        // Set the values
        comparisonResult.setEffectiveAnualCostrate(tcea);
        comparisonResult.setTotalPayment(totalPayment);
        comparisonResult.setMonthlyPaymentAvg(monthlyPaymentAvg);
        comparisonResult.setInsurancePaymentAvg(insurancePaymentAvg);
        comparisonResult.setMonthlyCostsAvg(monthlyCostsAvg);
    }
}
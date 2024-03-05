package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.ApplicationRejectionReason;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.form.Question145Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("question145Service")
public class Question145Service extends AbstractQuestionService<Question145Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UtilService utilService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                List<Date> enableDates = new ArrayList<>();
                int countDays = 1;
                while (countDays <= 120) {
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE, countDays);
                    enableDates.add(c.getTime());
                    countDays++;
                }

                String[] enabledDatesFormatted = new String[enableDates.size()];
                for (int i = 0; i < enableDates.size(); i++) {
                    enabledDatesFormatted[i] = new SimpleDateFormat("dd/MM/yyyy").format(enableDates.get(i));
                }

                attributes.put("enableDates", enabledDatesFormatted);
                attributes.put("form", new Question145Form());
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question145Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getServiceOrder()) {
                    return "DEFAULT";
                } else {
                    return "REJECT";
                }
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question145Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question145Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (form.getServiceOrder()) {
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.CREDIGOB_MONTO_OS.getKey(), form.getAmount());
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.CREDIGOB_NUMERO_OS.getKey(), form.getOrderNumber());
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.CREDIGOB_FEC_ACTIVACION.getKey(), form.getActivationDate());
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.CREDIGOB_PRCT_PARTICIPACION.getKey(), form.getParticipationPercentage() != null ? form.getParticipationPercentage() + "" : null);
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.CREDIGOB_RANGO_INGRESOS.getKey(), form.getIncomeRange());
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.CREDIGOB_RANGO_INGRESOS_EMPRESA.getKey(), form.getIncomeRangeCompany());

                    Date activationDate = new SimpleDateFormat("dd/MM/yyyy").parse(form.getActivationDate());
                    long days = utilService.daysBetween(new Date(), activationDate);
                    int daysToRegister = 0;
                    if (days > 120) {
                        throw new SqlErrorMessageException(null, "La fecha es invalida.");
                    } else if (days > 90) {
                        daysToRegister = 120;
                    } else if (days > 60) {
                        daysToRegister = 90;
                    } else if (days > 30) {
                        daysToRegister = 60;
                    } else {
                        daysToRegister = 30;
                    }

                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.CREDIGOB_TIEMPO_PAGO.getKey(), daysToRegister);

                    Calendar fecVencimiento = Calendar.getInstance();
//                    fecVencimiento.add(Calendar.DATE, daysToRegister);
                    fecVencimiento.add(Calendar.DATE, 30); // Segun ultimo requerimiento de credigob, siempre a 30 dias
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.CREDIGOB_FEC_VENCIMIENTO.getKey(), new SimpleDateFormat("dd/MM/yyyy").format(fecVencimiento.getTime()));
                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                } else {
                    // Reject the loan
                    loanApplicationDao.updateLoanApplicationStatus(id, LoanApplicationStatus.REJECTED, null);
                    loanApplicationDao.registerRejectionWithComment(id, ApplicationRejectionReason.CREDIGOB_NO_ORDEN_SERVICIO, null);
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        return null;
    }
}

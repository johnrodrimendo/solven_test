package com.affirm.strategy.contracts;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.strategy.ContractStrategy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CajaSullanaContractStrategy extends BaseContract implements ContractStrategy {
    public CajaSullanaContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO) throws Exception {
        super(contract, catalogService, personDAO, utilService, creditDAO, loanApplicationDAO, null);
    }

    @Override
    public void fillPdf(Person person, Credit credit, LoanOffer loanOffer, EntityProductParams params, User user, String signature, Person partner, PersonContactInformation contactInfo, List<PersonOcupationalInformation> ocupations, PersonOcupationalInformation principalOcupation, PersonBankAccountInformation personBankAccountInformation, LoanApplication loanApplication, Locale locale, SunatResult sunatResult) throws Exception {
        int DEFAULT_FONT_SIZE_CAJA_SULLANA = 10;

        Double effectiveAnnualRate;
        Double moratoriumRate;
        Double effectiveAnnualCostRate;
        Double amount;
        String creditCode;
        Double scheduleTotalInterest;
        Date dueDate;
        Double scheduleInsurance;
        if (credit != null) {
            effectiveAnnualRate = credit.getEffectiveAnnualRate();
            moratoriumRate = credit.getMoratoriumRate();
            effectiveAnnualCostRate = credit.getEffectiveAnnualCostRate();
            amount = credit.getAmount();
            creditCode = credit.getCode();
            scheduleTotalInterest = credit.getTotalScheduleField("totalInterest", 'M');
            dueDate = credit.getDueDate();
            scheduleInsurance = credit.getTotalScheduleField("insurance", 'M');
        } else {
            effectiveAnnualRate = loanOffer.getEffectiveAnualRate();
            moratoriumRate = loanOffer.getMoratoriumRate();
            effectiveAnnualCostRate = loanOffer.getEffectiveAnnualCostRate();
            amount = loanOffer.getAmmount();
            creditCode = null;
            scheduleTotalInterest = loanOffer.getTotalScheduleField("totalInterest");
            dueDate = loanOffer.getFirstDueDate();
            scheduleInsurance = loanOffer.getTotalScheduleField("insurance");
        }

        String cityAndDate = "Lima, "+ utilService.dateCustomFormat(new Date(), "dd 'de' MMMM 'del' YYYY", locale);
        String addressStreetName = contactInfo.getAddressStreetName();

        fillText("cityAndDate", cityAndDate, DEFAULT_FONT_SIZE_CAJA_SULLANA);
        fillText("date", new SimpleDateFormat("dd/MM/yyyy").format(new Date()), DEFAULT_FONT_SIZE_CAJA_SULLANA);

        fillText("person.fullname", person.getFullName(), DEFAULT_FONT_SIZE_CAJA_SULLANA);
        fillText("person.docNumber", person.getDocumentNumber(), DEFAULT_FONT_SIZE_CAJA_SULLANA);
        String address = addressStreetName.contains("Ref.:") ? addressStreetName.substring(0, addressStreetName.indexOf("Ref.:")) : addressStreetName;
        fillText("person.address", address.length() > 65 ? address.substring(0, 65).trim() : address.trim(), 7);
        fillText("person.email", contactInfo.getEmail(), defaultFont, DEFAULT_FONT_SIZE_CAJA_SULLANA);

        fillText("credit.teaCompensatoria", utilService.percentFormat(effectiveAnnualRate));
        fillText("credit.teaMoratoria", utilService.percentFormat(moratoriumRate));
        fillText("credit.tcea", utilService.percentFormat(effectiveAnnualCostRate));
        fillText("credit.currency", "SOLES");
        fillText("credit.disturbesement", utilService.doubleMoneyFormat(amount));
        fillText("credit.paymentFrequency", "MENSUAL");
        fillText("credit.code", creditCode);
        fillText("credit.totalAmountInterest", utilService.doubleMoneyFormat(scheduleTotalInterest));
        fillText("credit.firstDueDate", dueDate);
        fillText("credit.insurance", utilService.doubleMoneyFormat(scheduleInsurance));

        if (partner != null) {
            fillText("partner.fullname", partner.getFullName(), DEFAULT_FONT_SIZE_CAJA_SULLANA);
            fillText("partner.docNumber", partner.getDocumentNumber(), DEFAULT_FONT_SIZE_CAJA_SULLANA);
        }

        fillCheckBox("receiveEmail", true);

        fillText("signature", signature != null ? signature : " ", handWritingFont, 18);
    }
}

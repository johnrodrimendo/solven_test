package com.affirm.strategy.contracts;

import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.affirm.compartamos.model.GenerarCreditoResponse;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CompartamosContractStrategy extends BaseContract {

    protected TranslatorDAO translatorDAO;

    public CompartamosContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO, TranslatorDAO translatorDAO, WebServiceDAO webServiceDao) throws Exception {
        super(contract, catalogService, personDAO, utilService, creditDAO, loanApplicationDAO, webServiceDao);
        this.translatorDAO = translatorDAO;
    }

    @Override
    public void fillPdf(Person person, Credit credit, LoanOffer loanOffer, EntityProductParams params, User user, String signature, Person partner, PersonContactInformation contactInfo, List<PersonOcupationalInformation> ocupations, PersonOcupationalInformation principalOcupation, PersonBankAccountInformation personBankAccountInformation, LoanApplication loanApplication, Locale locale, SunatResult sunatResult) throws Exception {
        int defaultFontSizeCompartamos = 12;

        Double amount;
        OriginalSchedule zeroInstallment;
        Integer installments;
        Double effectiveAnualRate;
        Double effectiveAnualCostRate;
        Double scheduleTotalInterest;
        String entityCreditCode;
        String cronogramaTitle;
        List<OriginalSchedule> originalSchedule;
        Double scheduleInstallmentAmount;
        Double scheduleInstallmentCapital;
        Double scheduleInterest;
        Double scheduleInsurance;
        Double scheduleInterestTax;
        if (credit != null) {
            amount = credit.getAmount();
            zeroInstallment = credit.getOriginalSchedule().stream().filter(o -> o.getInstallmentId() == 0).findFirst().orElse(null);
            installments = credit.getInstallments();
            effectiveAnualRate = credit.getEffectiveAnnualRate();
            effectiveAnualCostRate = credit.getEffectiveAnnualCostRate();
            scheduleTotalInterest = credit.getTotalScheduleField("totalInterest", 'M');
            entityCreditCode = credit.getEntityCreditCode();
            cronogramaTitle = credit.getStatus().getId() != CreditStatus.INACTIVE_W_SCHEDULE ? "CRONOGRAMA DE PAGOS" : "CRONOGRAMA DE PAGOS PRELIMINAR";
            originalSchedule = credit.getOriginalSchedule();
            scheduleInstallmentAmount = credit.getTotalScheduleField("installmentAmount", 'O');
            scheduleInstallmentCapital = credit.getTotalScheduleField("installmentCapital", 'O');
            scheduleInterest = credit.getTotalScheduleField("interest", 'O');
            scheduleInsurance = credit.getTotalScheduleField("insurance", 'O');
            scheduleInterestTax = credit.getTotalScheduleField("interestTax", 'O');
        } else {
            amount = loanOffer.getAmmount();
            zeroInstallment = loanOffer.getOfferSchedule().stream().filter(o -> o.getInstallmentId() == 0).findFirst().orElse(null);
            installments = loanOffer.getInstallments();
            effectiveAnualRate = loanOffer.getEffectiveAnualRate();
            effectiveAnualCostRate = loanOffer.getEffectiveAnnualCostRate();
            scheduleTotalInterest = loanOffer.getTotalScheduleField("totalInterest");
            entityCreditCode = null;
            cronogramaTitle = "CRONOGRAMA DE PAGOS PRELIMINAR";
            originalSchedule = loanOffer.getOfferSchedule();
            scheduleInstallmentAmount = loanOffer.getTotalScheduleField("installmentAmount");
            scheduleInstallmentCapital = loanOffer.getTotalScheduleField("installmentCapital");
            scheduleInterest = loanOffer.getTotalScheduleField("interest");
            scheduleInsurance = loanOffer.getTotalScheduleField("insurance");
            scheduleInterestTax = loanOffer.getTotalScheduleField("interestTax");
        }

        List<EntityWebServiceLog> generacionCreditoLogs = webServiceDao.getEntityWebServiceLog(loanApplication.getId(), EntityWebService.COMPARTAMOS_GENERAR_CREDITO);
        EntityWebServiceLog generacionCreditoLog = generacionCreditoLogs != null ? generacionCreditoLogs.stream().sorted(Comparator.nullsLast(
                (e1, e2) -> e2.getStartDate().compareTo(e1.getStartDate()))).filter(l -> l.getStatus() == EntityWebServiceLog.STATUS_SUCCESS).findFirst().orElse(null) : null;

        GenerarCreditoResponse compartaomsCreditResponse = null;
        if (generacionCreditoLog != null)
            compartaomsCreditResponse = (GenerarCreditoResponse) generacionCreditoLog.getParsedResponse(GenerarCreditoResponse.class);

        fillText("person.fullname", person.getFullName(), defaultFont, defaultFontSizeCompartamos);
        fillText("person.code", compartaomsCreditResponse != null && compartaomsCreditResponse.getCliente() != null ? compartaomsCreditResponse.getCliente().getCodigoCliente() : null, defaultFont, defaultFontSizeCompartamos);
        fillText("credit.amountFormated", utilService.doubleMoneyFormat(amount), defaultFont, defaultFontSizeCompartamos);
        fillText("disbursement.date", zeroInstallment != null ? utilService.dateFormat(zeroInstallment.getDueDate()) : null, defaultFont, defaultFontSizeCompartamos);
        fillText("credit.dueDate", installments + " meses", defaultFont, defaultFontSizeCompartamos);
        fillText("credit.tea", utilService.percentFormat(effectiveAnualRate), defaultFont, defaultFontSizeCompartamos);
        fillText("credit.tcea", utilService.customDoubleFormat(effectiveAnualCostRate, 2) + "%", defaultFont, defaultFontSizeCompartamos);
        fillText("credit.installmentAmount", utilService.doubleMoneyFormat(scheduleTotalInterest), defaultFont, defaultFontSizeCompartamos);
        fillText("warranty", "", defaultFont, defaultFontSizeCompartamos);
        fillText("person.docNumber", person.getDocumentNumber(), defaultFont, defaultFontSizeCompartamos);
        String cityAndDate = "Lima, " + utilService.dateCustomFormat(new Date(), "dd 'de' MMMM 'del' YYYY", locale);
        fillText("cityAndDate", cityAndDate, defaultFont, defaultFontSizeCompartamos);
        fillCheckBox("authorization.personalData", true);

        fillText("signature.signature", signature != null ? signature : " ", handWritingFont, 18);
        // consider schedule in all loans
        fillText("person.address", contactInfo.getAddressStreetName(), defaultFont, defaultFontSizeCompartamos);
        switch (person.getDocumentType().getId()) {
            case IdentityDocumentType.DNI:
                fillText("person.dni", person.getDocumentNumber(), defaultFont, defaultFontSizeCompartamos);
                fillText("person.ruc", null, defaultFont, defaultFontSizeCompartamos);
                break;
            case IdentityDocumentType.RUC:
                fillText("person.ruc", person.getDocumentNumber(), defaultFont, defaultFontSizeCompartamos);
                fillText("person.dni", null, defaultFont, defaultFontSizeCompartamos);
                break;
        }
        fillText("person.econ_group", person.getProfession() != null ? translatorDAO.translate(Entity.COMPARTAMOS, 44, person.getProfession().getId().toString(), null) : null, defaultFont, defaultFontSizeCompartamos);
        fillText("person.capital", null, defaultFont, defaultFontSizeCompartamos);
        fillText("person.sbs", null, defaultFont, defaultFontSizeCompartamos);
        fillText("patrimony", null, defaultFont, defaultFontSizeCompartamos);
        fillText("amount_patrimony", null, defaultFont, defaultFontSizeCompartamos);
        fillText("personal.patrimony", loanApplicationDAO.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst().orElse(null).getAdmissionTotalIncome(), defaultFont, defaultFontSizeCompartamos);
        if (principalOcupation != null) {
            fillText("person.activity.name", personDAO.getRucInfo(principalOcupation.getRuc()) != null ? personDAO.getRucInfo(principalOcupation.getRuc()).getPrincipalActivity() : null, defaultFont, defaultFontSizeCompartamos);
            fillText("person.activity.FirstDate", principalOcupation.getStartDate() != null ? utilService.dateFormat(principalOcupation.getStartDate()) : null, defaultFont, defaultFontSizeCompartamos);
        }
        fillText("person.ciiu", personDAO.getRucInfo(principalOcupation.getRuc()) != null ? personDAO.getRucInfo(principalOcupation.getRuc()).getCiiu() : null, defaultFont, defaultFontSizeCompartamos);
        fillText("person.valuechain", null, defaultFont, defaultFontSizeCompartamos);
        fillText("person.otherActivities", null, defaultFont, defaultFontSizeCompartamos);

        if (personBankAccountInformation != null && personBankAccountInformation.getBank() != null) {

            fillText("paymentAccountCheck", "X", defaultFont, defaultFontSizeCompartamos);
            fillText("paymentAccount", personBankAccountInformation.getBankAccount(), defaultFont, defaultFontSizeCompartamos);
            fillText("accountBankName", personBankAccountInformation.getBank().getName(), defaultFont, defaultFontSizeCompartamos);
            fillText("titularAccountName", person.getFullName(), defaultFont, defaultFontSizeCompartamos);
            fillText("documentNumber", person.getDocumentNumber(), defaultFont, defaultFontSizeCompartamos);

            fillText("disbursement.type", personBankAccountInformation.getBank().getName(), defaultFont, defaultFontSizeCompartamos);

        } else {
            fillText("disbursement.type", null, defaultFont, defaultFontSizeCompartamos);
        }

        fillText("credit.code", entityCreditCode, defaultFont, defaultFontSizeCompartamos);
        fillText("product.type", compartaomsCreditResponse != null && compartaomsCreditResponse.getCredito() != null ? compartaomsCreditResponse.getCredito().getTipoProducto() : null, defaultFont, defaultFontSizeCompartamos);

        int defaultFontSizeTablaCuotas = 10;
        // The number of rows on PDF is 18 ;)
        fillText("cronograma.title", cronogramaTitle, defaultFontSizeCompartamos);
        for (int index = 1; index <= originalSchedule.size() && index <= 18; index++) {
            OriginalSchedule cuota = originalSchedule.get(index - 1);
            String baseFieldCuota = "cuota." + index;
            fillText(baseFieldCuota + ".date", utilService.dateFormat(cuota.getDueDate()), defaultFont, defaultFontSizeTablaCuotas);
            fillText(baseFieldCuota + ".nro", cuota.getInstallmentId(), defaultFont, defaultFontSizeTablaCuotas);
            fillText(baseFieldCuota + ".value", utilService.doubleMoneyFormat(cuota.getInstallmentAmount()), defaultFont, defaultFontSizeTablaCuotas);
            fillText(baseFieldCuota + ".capital", utilService.doubleMoneyFormat(cuota.getInstallmentCapital()), defaultFont, defaultFontSizeTablaCuotas);
            fillText(baseFieldCuota + ".interesComp", utilService.doubleMoneyFormat(cuota.getInterest()), defaultFont, defaultFontSizeTablaCuotas);
            fillText(baseFieldCuota + ".seguroDesg", utilService.doubleMoneyFormat(cuota.getInsurance()), defaultFont, defaultFontSizeTablaCuotas);
            fillText(baseFieldCuota + ".itf", utilService.doubleMoneyFormat(cuota.getInterestTax()), defaultFont, defaultFontSizeTablaCuotas);
            fillText(baseFieldCuota + ".seguroIncTodoRiesgo", utilService.doubleMoneyFormat(cuota.getCollectionCommissionTax()), defaultFont, defaultFontSizeTablaCuotas);
            fillText(baseFieldCuota + ".saldoCapital", utilService.doubleMoneyFormat(cuota.getRemainingCapital()), defaultFont, defaultFontSizeTablaCuotas);
        }
        fillText("cuota.total.value", utilService.doubleMoneyFormat(scheduleInstallmentAmount), defaultFont, defaultFontSizeTablaCuotas);
        fillText("cuota.total.capital", utilService.doubleMoneyFormat(scheduleInstallmentCapital), defaultFont, defaultFontSizeTablaCuotas);
        fillText("cuota.total.interesComp", utilService.doubleMoneyFormat(scheduleInterest), defaultFont, defaultFontSizeTablaCuotas);
        fillText("cuota.total.seguroDesg", utilService.doubleMoneyFormat(scheduleInsurance), defaultFont, defaultFontSizeTablaCuotas);
        fillText("cuota.total.itf", utilService.doubleMoneyFormat(scheduleInterestTax), defaultFont, defaultFontSizeTablaCuotas);

        String paymentType = "";
        String paymentTypeChar = JsonUtil.getStringFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.APPLICATION_PAYMENT_TYPE.getKey(), null);
        String paymentTypeA = "";
        String paymentTypeD = "";
        if (paymentTypeChar != null && !paymentTypeChar.isEmpty()) {
            switch (paymentTypeChar) {
                case "A":
                    paymentType = "PAGO ANTICIPADO";
                    paymentTypeA = "X";
                    break;
                case "D":
                    paymentType = "ADELANTO DE CUOTAS";
                    paymentTypeD = "X";
                    break;
            }
        }

        fillText("paymentTypeA", paymentTypeA, defaultFont, defaultFontSizeCompartamos);
        fillText("paymentTypeD", paymentTypeD, defaultFont, defaultFontSizeCompartamos);
        fillText("documentType", "MEDIO ELECTRÓNICO", defaultFont, defaultFontSizeCompartamos);
        fillText("sendingDocumentType", "ENVIO DE EST. DE CTA. CREDITOS", defaultFont, defaultFontSizeCompartamos);
        fillText("paymentsType", paymentType, defaultFont, defaultFontSizeCompartamos);
        fillText("person.signature", signature != null ? signature : " ", handWritingFont, 18);

        Boolean statementFormat = JsonUtil.getBooleanFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANK_ACCOUNT_STATEMENT.getKey(), false);
        if (statementFormat) {
            fillText("statement.format.physical", "X", defaultFont, defaultFontSizeCompartamos);
        } else {
            fillText("statement.format.digital", "X", defaultFont, defaultFontSizeCompartamos);
            fillText("person.email", user.getEmail(), defaultFont, 8);
        }

        if (partner != null) {
            fillText("partner.fullname", partner.getFullName(), defaultFont, defaultFontSizeCompartamos);
            fillText("partner.fullname2", partner.getFullName(), defaultFont, defaultFontSizeCompartamos);
            fillText("partner.docNumber", partner.getDocumentNumber(), defaultFont, defaultFontSizeCompartamos);
            fillText("partner.signature", partner.getFullName(), handWritingFont, 18);
        }
    }
}

/**
 *
 */
package com.affirm.common.service.impl;

import com.affirm.abaco.client.AbacoClient;
import com.affirm.abaco.client.ERptaCredito;
import com.affirm.acceso.model.Direccion;
import com.affirm.bantotalrest.model.common.SDTCajaAhorro;
import com.affirm.bantotalrest.model.common.SDTPrestamo;
import com.affirm.common.dao.*;
import com.affirm.common.model.AfipActivitiy;
import com.affirm.common.model.BankAccountOfferData;
import com.affirm.common.model.BantotalApiData;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.entities.TarjetasPeruanasPrepagoService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.NumberToTextConverter;
import com.affirm.common.util.XirrDate;
import com.affirm.strategy.contracts.*;
import com.affirm.system.configuration.Configuration;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.poi.ss.formula.functions.Irr;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.expression.ThymeleafEvaluationContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */

@Service("creditService")
public class CreditServiceImpl implements CreditService {

    private static Logger logger = Logger.getLogger(CreditServiceImpl.class);
    private static String PDF_FILL_DEFAULT_NULL_VALUE = " ----";

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private EmployerDAO employerDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private DebtConsolidationService debtConsolidationService;
    @Autowired
    private WebServiceDAO webServiceDao;
    @Autowired
    private TranslatorDAO translatorDAO;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private AgreementService agreementService;
    @Autowired
    private LoanNotifierService loanNotifierService;
    @Autowired
    private CreditService creditService;
    @Autowired
    private UserService userService;
    @Autowired
    private AbacoClient abacoClient;
    @Autowired
    private TarjetasPeruanasPrepagoService tarjetasPeruanasPrepagoService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private OfflineConversionService offlineConversionService;
    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public byte[] createOfferContract(LoanApplication loanApplication, LoanOffer loanOffer, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine, String filename, boolean toDownload) throws Exception {

        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), true);

        // Create the contract
        byte[] pdfAsBytes = getContractHtmlAsPdf(request, response, locale, templateEngine, null, loanOffer, person, loanApplication);
        return pdfAsBytes;
    }

    @Override
    public byte[] createContract(int creditId, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine, String filename, boolean toDownload) throws Exception {
        return createContract(creditId, request, response, locale, templateEngine, filename, toDownload, true);
    }

    @Override
    public byte[] createContract(int creditId, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine, String filename, boolean toDownload, boolean async) throws Exception {

        Credit credit = creditDao.getCreditByID(creditId, locale, true, Credit.class);
        Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), true);

        // Create the contract if it doesnt exists
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);

        byte[] pdfAsBytes = getContractHtmlAsPdf(request, response, locale, templateEngine, credit, null, person, loanApplication);
        if (!toDownload) {
            if (filename == null || filename.isEmpty()) {
                filename = credit.getId() + "_contrato_" + personDao.getPerson(catalogService, locale, person.getId(), false).getFirstName() + ".pdf";
            }
            saveContract(loanApplication, person, pdfAsBytes, filename, async);
        }

        return pdfAsBytes;
    }

    @Override
    public void createAndSaveContractApplication(LoanApplication loanApplication, LoanOffer selectedOffer, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine) throws Exception {

        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), true);

        byte[] pdfAsBytes = getContractHtmlAsPdf(request, response, locale, templateEngine, null, selectedOffer, person, loanApplication);
        String filename = loanApplication.getId() + "_solicitud_" + personDao.getPerson(catalogService, locale, person.getId(), false).getFirstName() + ".pdf";

        fileService.writeUserFile(pdfAsBytes, person.getUserId(), filename);
        userDao.registerUserFile(person.getUserId(), loanApplication.getId(), UserFileType.CONTRATO_SOLICITUD, filename);
    }

    private byte[] getContractHtmlAsPdf(HttpServletRequest request, HttpServletResponse response, Locale locale,
                                        SpringTemplateEngine templateEngine, Credit credit, LoanOffer loanOffer, Person person, LoanApplication loanApplication) throws Exception {

        boolean hasToOrder = false;
        List<Integer> copyPosition = new ArrayList<>();
        // get Params
        EntityProductParams params = null;
        EntityBranding entityBranding = null;
        String signature = null;
        if (credit != null) {
            params = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
            entityBranding = catalogService.getEntityBranding(credit.getEntity().getId());
            LoanOffer offer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
            if (offer != null) {
                signature = offer.getSignatureFullName();
            }
        } else if (loanOffer != null) {
            params = catalogService.getEntityProductParamById(loanOffer.getEntityProductParameterId());
            entityBranding = catalogService.getEntityBranding(loanOffer.getEntity().getId());
            signature = loanOffer.getSignatureFullName();
        }

        if (credit != null) {
            if (credit.getEntity().getId().equals(Entity.COMPARTAMOS)) {
                if (credit.getAmount() >= Integer.valueOf(params.getEntityProductParameter().get(0)))
                    params.setContract(params.getContracts().get(0));
                else params.setContract(params.getContracts().get(1));
            }
        } else if (loanOffer != null) {
            if (loanOffer.getEntity().getId().equals(Entity.COMPARTAMOS)) {
                if (loanOffer.getAmmount() >= Integer.valueOf(params.getEntityProductParameter().get(0)))
                    params.setContract(params.getContracts().get(0));
                else params.setContract(params.getContracts().get(1));
            }
        }

        // Return if there is no pdf contract, else join with the pdf contract
        if (params.getContracts() == null || params.getContracts().size() == 0) {
            return createSummarySheet(request, response, locale, templateEngine, credit, loanOffer, person, loanApplication, params, entityBranding, signature);
        } else {
            User user = userDao.getUser(person.getUserId());
            byte[] contractBytes = null;
            String pdfPath = params.getContract().getPdfPath();
            if (FilenameUtils.getExtension(pdfPath).equalsIgnoreCase("pdf")) {
                contractBytes = renderCreditContractFromPdf(params, loanApplication, credit, loanOffer, person, user, locale, signature);
            } else if (FilenameUtils.getExtension(pdfPath).equalsIgnoreCase("html")) {
                contractBytes = renderCreditContractFromHtml(loanApplication, params, credit, loanOffer, person, user, locale, signature);
            }
            List<Integer> insuranceSheetsPositions = new ArrayList<>();
            List<Integer> summarySheetsPositions = new ArrayList<>();
            int maxPage = 0;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PDFMergerUtility mergeUtility = new PDFMergerUtility();
            if (contractBytes != null)
                mergeUtility.addSource(new ByteArrayInputStream(contractBytes));

            // Only add the summary sheet and cronograma if the entity is EFL or SOLVEN
            if (params.getEntity().getId() == Entity.EFL || params.getEntity().getId() == Entity.AFFIRM || params.getEntity().getId() == Entity.BF) {
                byte[] pdfAsBytes = createSummarySheet(request, response, locale, templateEngine, credit,
                        loanOffer, person, loanApplication, params, entityBranding, signature);
                mergeUtility.addSource(new ByteArrayInputStream(pdfAsBytes));
            } else if (params.getEntity().getId() == Entity.FINANSOL) {
                FinansolFichaInscripcionContractStrategy finansolStrategy = new FinansolFichaInscripcionContractStrategy(
                        fileService.getContract("Ficha_InscripcionSocio_template.pdf"),
                        catalogService, personDao, utilService, creditDao, loanApplicationDao);

                mergeUtility.addSource(new ByteArrayInputStream(finansolStrategy.renderPdf(person, loanApplication, credit, loanOffer, params, user, signature, locale)));
            } else if (params.getEntity().getId() == Entity.PRISMA) {
                byte[] contractBytesSchedulePrisma = renderCreditContractFromHtml(loanApplication, params, credit, loanOffer, person, user, locale, signature);
                mergeUtility.addSource(new ByteArrayInputStream(contractBytesSchedulePrisma));
            } else if (params.getEntity().getId() == Entity.AZTECA) {
                if(params.getId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO){
                    hasToOrder = false;
                }
                else{
                    Boolean isClientContract = false;
                    Boolean hasCreatedDebitCard = false;
                    BantotalApiData bantotalApiData = loanApplication.getBanTotalApiData();
                    if(bantotalApiData != null && bantotalApiData.getPoseeCuentaExistente() != null && bantotalApiData.getPoseeCuentaExistente()) isClientContract = true;
                    if(bantotalApiData != null && (bantotalApiData.getTarjetaUId() != null || (bantotalApiData.getPoseeTarjetaDebito() != null && !bantotalApiData.getPoseeTarjetaDebito()))) hasCreatedDebitCard = true;
                    byte[] contractBytesScheduleAzteca = renderCreditContractFromHtml(loanApplication, params, credit, loanOffer, person, user, locale, signature);
                    mergeUtility.addSource(new ByteArrayInputStream(contractBytesScheduleAzteca));

                    byte[] summarySheetContractBytes =  renderSummarySheetContractFromPdf(params, loanApplication, credit, loanOffer, person, user, locale, signature);
                    if(summarySheetContractBytes != null) {
                        mergeUtility.addSource(new ByteArrayInputStream(summarySheetContractBytes));
                        summarySheetsPositions = Arrays.asList(1,2);
                    }

                    Integer insuranceType = JsonUtil.getIntFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.AZTECA_INSURANCE_TYPE.getKey(), null);
                    if( insuranceType != null){
                        byte[] insuranceContractBytes =  renderInsuranceContractFromPdf(params, loanApplication, credit, loanOffer, person, user, locale, signature);
                        if(insuranceContractBytes != null) {
                            mergeUtility.addSource(new ByteArrayInputStream(insuranceContractBytes));
                            int from = 0;
                            if(!summarySheetsPositions.isEmpty()) from = summarySheetsPositions.get(summarySheetsPositions.size() - 1);
                            int maxInsurancePage = 0;
                            switch (insuranceType){
                                case LoanApplication.FAMILY_PROTECTION_INSURANCE_TYPE:
                                    maxInsurancePage = 10;
                                    break;
                                case LoanApplication.WITH_DEVOLUTION_INSURANCE_TYPE:
                                    maxInsurancePage = 6;
                                    break;
                                case LoanApplication.WITHOUT_DEVOLUTION_INSURANCE_TYPE:
                                    maxInsurancePage = 5;
                                    break;
                            }
                            for (int i = 1; i <= maxInsurancePage ; i++) {
                                insuranceSheetsPositions.add(i+from);
                            }
                        }
                    }

                    if(isClientContract){
                        if(!hasCreatedDebitCard) {
                            copyPosition = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 23, Contract.SUMMARY_SHEET_POSITION, 22, Contract.INSURANCE_POSITION);
                            maxPage = 23;
                        }
                        else {
                            copyPosition = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 24, Contract.SUMMARY_SHEET_POSITION, 22, Contract.INSURANCE_POSITION, 23);
                            maxPage = 24;
                        }
                    }
                    else {
                        if(!hasCreatedDebitCard) {
                            maxPage = 26;
                            copyPosition = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 26, Contract.SUMMARY_SHEET_POSITION, 22, Contract.INSURANCE_POSITION, 23, 24, 25);
                        }
                        else {
                            maxPage = 27;
                            copyPosition = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 27, Contract.SUMMARY_SHEET_POSITION, 22, Contract.INSURANCE_POSITION, 23, 24, 25, 26);
                        }
                    }

                    hasToOrder = true;
                }
            }

            mergeUtility.setDestinationStream(out);
            mergeUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());// TODO Do some research for the memory!!

            if(!hasToOrder) return out.toByteArray();
            else{
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                ByteArrayOutputStream baCopy = new ByteArrayOutputStream();
                PdfCopy copy = new PdfCopy(document, baCopy);
                document.open();
                PdfReader pdfReader = new PdfReader(out.toByteArray());
                for (Integer position : copyPosition) {
                    if(position == Contract.SUMMARY_SHEET_POSITION){
                        for (Integer summarySheetsPosition : summarySheetsPositions) {
                            copy.addPage(copy.getImportedPage(pdfReader, summarySheetsPosition+maxPage));
                        }
                    }
                    else if(position == Contract.INSURANCE_POSITION){
                        for (Integer insuranceSheetsPosition : insuranceSheetsPositions) {
                            copy.addPage(copy.getImportedPage(pdfReader, insuranceSheetsPosition+maxPage));
                        }
                    }
                    else{
                        copy.addPage(copy.getImportedPage(pdfReader, position));
                    }
                }
                document.close();
                return baCopy.toByteArray();
            }

        }
    }

    private byte[] renderCreditContractFromPdf(EntityProductParams params, LoanApplication loanApplication, Credit credit, LoanOffer offer, Person person, User user, Locale locale, String signature) throws Exception {

        String pdfPath = params.getContract().getPdfPath();
        BaseContract entityContract = null;
        if (pdfPath != null) {
            switch (params.getEntity().getId()) {
                case Entity.ABACO:
                    entityContract = new AbacoContractStrategy(fileService.getContract(pdfPath), catalogService, personDao, utilService, creditDao, loanApplicationDao);
                    break;
                case Entity.BF:
                    entityContract = new BancoFinancieroContractStrategy(fileService.getContract(pdfPath), catalogService, personDao, utilService, creditDao, loanApplicationDao);
                    break;
                case Entity.RIPLEY:
                    entityContract = new RipleyContractStrategy(fileService.getContract(pdfPath), catalogService, personDao, utilService, creditDao, loanApplicationDao);
                    break;
                case Entity.EFECTIVA:
                    entityContract = new EfectivaContractStrategy(fileService.getContract(pdfPath), catalogService, personDao, utilService, creditDao, loanApplicationDao);
                    break;
                case Entity.ACCESO:
                    if(EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(loanApplication.getSelectedEntityProductParameterId())) {
                        entityContract = new AccesoLibreDisponibilidadContractStrategy(fileService.getContract(pdfPath), catalogService, personDao, utilService, creditDao, loanApplicationDao);
                    } else {
                        entityContract = new AccesoContractStrategy(fileService.getContract(pdfPath), catalogService, personDao, utilService, creditDao, loanApplicationDao);
                    }
                    break;
                case Entity.COMPARTAMOS:
                    entityContract = new CompartamosContractStrategy(fileService.getContract(pdfPath), catalogService, personDao, utilService, creditDao, loanApplicationDao, translatorDAO, webServiceDao);
                    break;
                case Entity.CAJASULLANA:
                    entityContract = new CajaSullanaContractStrategy(fileService.getContract(pdfPath), catalogService, personDao, utilService, creditDao, loanApplicationDao);
                    break;
                case Entity.MULTIFINANZAS:
                    entityContract = new MultifinanzasContractStrategy(fileService.getContract(pdfPath), catalogService, personDao, utilService, creditDao, loanApplicationDao);
                    break;
                case Entity.TARJETAS_PERUANAS:
                    entityContract = new TarjetasPeruanasContractStrategy(fileService.getContract(pdfPath), catalogService, personDao, utilService, creditDao, loanApplicationDao);
                    break;
                case Entity.PRISMA:
                    entityContract = new PrismaContractStrategy(fileService.getContract(pdfPath), catalogService, personDao, utilService, creditDao, loanApplicationDao);
                    break;
            }
        }

        if (entityContract != null)
            return entityContract.renderPdf(person, loanApplication, credit, offer, params, user, signature, locale);
        return null;
    }

    private byte[] renderSummarySheetContractFromPdf(EntityProductParams params, LoanApplication loanApplication, Credit credit, LoanOffer offer, Person person, User user, Locale locale, String signature) throws Exception {
        BaseContract entityContract = null;

        if (entityContract != null)
            return entityContract.renderPdf(person, loanApplication, credit, offer, params, user, signature, locale);
        return null;
    }

    private byte[] renderInsuranceContractFromPdf(EntityProductParams params, LoanApplication loanApplication, Credit credit, LoanOffer offer, Person person, User user, Locale locale, String signature) throws Exception {
        BaseContract entityContract = null;

        if (entityContract != null)
            return entityContract.renderPdf(person, loanApplication, credit, offer, params, user, signature, locale);
        return null;
    }

    private byte[] renderCreditContractFromHtml(LoanApplication loanApplication, EntityProductParams params, Credit credit, LoanOffer offer, Person person, User user, Locale locale, String signature) throws Exception {
        String pdfPath = params.getContracts().get(0).getPdfPath();
//        String htmlContract = new String(fileService.getContract(params.getContracts().get(0).getPdfPath()), Charset.forName("ISO-8859-1"));
        String htmlContract = null;

        PersonContactInformation contactInfo = personDao.getPersonContactInformation(locale, person.getId());

        Date signatureDate;
        Date firstDueDate;
        Date dueDate;
        List<ConsolidableDebt> consolidableDebts;
        boolean isDebtConsolidation;
        Double amount;
        Currency currency;
        Double effectiveAnualRate;
        Double moratoriumRate;
        Double commision2;
        String creditCode;
        Double firstInstallmentAmount;
        Double secondInstallmentAmount = null;
        Double installmentAmountAvg;
        String depositorCode;

        if(credit != null){
            signatureDate = offer != null && offer.getSignatureDate() != null ? offer.getSignatureDate() : new Date();
            firstDueDate = credit.getManagementSchedule().get(0).getDueDate();
            dueDate = credit.getManagementSchedule().get(credit.getManagementSchedule().size() - 1).getDueDate();
            consolidableDebts = creditDao.getConsolidatedDebts(credit.getId());
            isDebtConsolidation = credit.getProduct().getId() == Product.DEBT_CONSOLIDATION || credit.getProduct().getId() == Product.DEBT_CONSOLIDATION_OPEN;
            currency = credit.getCurrency();
            amount = credit.getAmount();
            effectiveAnualRate = credit.getEffectiveAnnualRate();
            moratoriumRate = credit.getMoratoriumRate();
            commision2 = credit.getCommission2();
            creditCode = credit.getCode();
            firstInstallmentAmount = credit.getOriginalSchedule().get(0).getInstallmentAmount();
            if(credit.getOriginalSchedule().size() > 1)
                secondInstallmentAmount = credit.getOriginalSchedule().get(1).getInstallmentAmount();
            installmentAmountAvg = credit.getInstallmentAmountAvg();
            depositorCode = credit.getDepositorCode();
        }else{
            signatureDate = offer.getSignatureDate() != null ? offer.getSignatureDate() : new Date();
            firstDueDate = offer.getOfferSchedule().get(0).getDueDate();
            dueDate = offer.getOfferSchedule().get(offer.getOfferSchedule().size() - 1).getDueDate();
            consolidableDebts = loanApplicationDao.getConsolidationAccounts(loanApplication.getId());
            isDebtConsolidation = offer.getProduct().getId() == Product.DEBT_CONSOLIDATION || offer.getProduct().getId() == Product.DEBT_CONSOLIDATION_OPEN;
            currency = offer.getCurrency();
            amount = offer.getAmmount();
            effectiveAnualRate = offer.getEffectiveAnualRate();
            moratoriumRate = offer.getMoratoriumRate();
            commision2 = offer.getCommission2();
            creditCode = "";
            firstInstallmentAmount = offer.getOfferSchedule().get(0).getInstallmentAmount();
            if(offer.getOfferSchedule().size() > 1)
                secondInstallmentAmount = offer.getOfferSchedule().get(1).getInstallmentAmount();
            installmentAmountAvg = offer.getInstallmentAmountAvg();
            depositorCode = "";
        }

        switch (params.getEntity().getId()) {
            case Entity.ABACO:
            case Entity.AFFIRM: {
                htmlContract = new String(fileService.getContract(pdfPath), Charset.forName("ISO-8859-1"));
                htmlContract = htmlContract
                        .replaceAll("%FULL_NAME%", person.getFullName() != null ? person.getFullName() : "")
                        .replaceAll("%IDENTITY_DOCUMENT%", person.getDocumentType().getName() + " " + person.getDocumentNumber())
                        .replaceAll("%ADDRESS%", contactInfo != null && contactInfo.getAddressStreetName() != null ? contactInfo.getAddressStreetName() : "")
                        .replaceAll("%SIGNATURE%", signature != null ? signature : "")
                        .replaceAll("%DAYS%", new SimpleDateFormat("dd").format(signatureDate))
                        .replaceAll("%MONTH%", new SimpleDateFormat("MMMM", locale).format(signatureDate))
                        .replaceAll("%YEAR%", new SimpleDateFormat("yyyy").format(signatureDate));
                break;
            }
            case Entity.EFL:{
                htmlContract = new String(fileService.getContract(pdfPath), Charset.forName("ISO-8859-1"));
                StringBuilder debtDetailRows = new StringBuilder();
                String bank = "";
                String bankAccount = "";
                String remainingAmount = "";

                if (isDebtConsolidation) {
                    if (consolidableDebts != null) {
                        for (ConsolidableDebt debt : consolidableDebts) {
                            debtDetailRows.append(String.format("<tr><td style=\"width:200px\">%s</td><td style=\"width:200px\">%s</td><td>%s</td></tr>",
                                    debt.getEntity().getBank().getName(),
                                    debt.getAccountCardNumber(),
                                    utilService.doubleMoneyFormat(debt.getBalanceDouble(), currency)));
                        }
                    }

                    PersonBankAccountInformation bankAccountInformation = personDao.getPersonBankAccountInformation(locale, person.getId());
                    if (bankAccountInformation != null && bankAccountInformation.getBank() != null) {
                        bank = bankAccountInformation.getBank().getName();
                        bankAccount = bankAccountInformation.getBankAccount();
                    }

                    if(consolidableDebts != null){
                        double totalConsolidableDebt = consolidableDebts.stream().filter(c -> c.isSelected()).mapToDouble(c -> c.getTotalBalance()).sum();
                        double leftAmount = amount - totalConsolidableDebt;
                        remainingAmount = utilService.doubleMoneyFormat(leftAmount);
                    }
                }

                htmlContract = htmlContract
                        .replaceAll("%FULL_NAME%", person.getFullName() != null ? person.getFullName() : "")
                        .replaceAll("%DOCUMENT_NUMBER%", person.getDocumentNumber())
                        .replaceAll("%IDENTITY_DOCUMENT%", person.getDocumentType().getName() + " " + person.getDocumentNumber())
                        .replaceAll("%ADDRESS%", contactInfo != null && contactInfo.getAddressStreetName() != null ? contactInfo.getAddressStreetName() : "")
                        .replaceAll("%SIGNATURE%", signature != null ? signature : "")
                        .replaceAll("%DAYS%", new SimpleDateFormat("dd").format(signatureDate))
                        .replaceAll("%MONTH%", new SimpleDateFormat("MMMM", locale).format(signatureDate))
                        .replaceAll("%YEAR%", new SimpleDateFormat("yyyy").format(signatureDate))
                        .replaceAll("%AMOUNT%", utilService.doubleMoneyFormat(amount))
                        .replaceAll("%DUE_DAY%", new SimpleDateFormat("dd").format(dueDate))
                        .replaceAll("%DUE_MONTH%", new SimpleDateFormat("MMMM", locale).format(dueDate))
                        .replaceAll("%DUE_YEAR%", new SimpleDateFormat("yyyy").format(dueDate))
                        .replaceAll("%TEA%", utilService.doubleFormat(effectiveAnualRate))
                        .replaceAll("%TEA_MORATORIA%", utilService.doubleFormat(moratoriumRate != null ? moratoriumRate * 100 : null))
                        .replaceAll("%AUTOMATIC_CHARGE%", utilService.doubleMoneyFormat(commision2))
                        .replaceAll("%EMITENTE%", person.getFullName())
                        .replaceAll("%SHOW_ATTACH_1_CSS%", isDebtConsolidation ? "block" : "none")
                        .replaceAll("%SHOW_PAGE_BREAK%", isDebtConsolidation ? "page-break-after: always;" : "")
                        .replaceAll("%CREDIT_CODE%", creditCode)
                        .replaceAll("%BANK_ACCOUNT_NUMBER%", bankAccount)
                        .replaceAll("%BANK%", bank)
                        .replaceAll("%DEBT_DETAIL_ROWS%", debtDetailRows.toString())
                        .replaceAll("%INSTALLMENT_AMOUNT%", utilService.doubleMoneyFormat(installmentAmountAvg))
                        .replaceAll("%PAYMENT_CODE%", depositorCode)
                        .replaceAll("%REMAINING_AMOUNT%", remainingAmount);

                break;
            }
            case Entity.BANCO_DEL_SOL: {
                String filePT = pdfPath;

                if(loanApplication.getEntityUserId() != null){
                    UserEntity userEntity = userDao.getEntityUserById(loanApplication.getEntityUserId(), locale);
                    if(userEntity != null && userEntity.getEntityAcquisitionChannelId() != null){
                        EntityAcquisitionChannel entityAcquisitionChannel = catalogService.getEntityAcquisitionChannelById(userEntity.getEntityAcquisitionChannelId());
                        if(entityAcquisitionChannel != null && entityAcquisitionChannel.getEntityProductParameterId() != null){
                            switch (entityAcquisitionChannel.getEntityProductParameterId()){
                                case EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_AGENCIAS:
                                    filePT = "contrato-bds-agencias.html";
                                    break;
                            }
                        }
                    }
                }

                htmlContract = new String(fileService.getContract(filePT), Charset.forName("UTF-8"));
                String bank = null;
                String bankAccount = null;
                String bankAccountCBU = null;
                String bankAccountType = null;

                PersonDisqualifier pep = null;
                PersonDisqualifier facta = null;

                String bancoDelSolClientType = loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey());
                AfipActivitiy afipActivitiy = null;
                if(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ACTIVITY_TYPE.getKey()) != null){
                    Integer activityTypeId = Integer.parseInt(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ACTIVITY_TYPE.getKey()));
                    afipActivitiy = catalogService.getAfipActivityById(activityTypeId);
                }

                PersonBankAccountInformation bankAccountInformation = personDao.getPersonBankAccountInformation(locale, person.getId());
                if (bankAccountInformation != null && bankAccountInformation.getBank() != null) {
                    bank = bankAccountInformation.getBank().getName();
                    bankAccount = bankAccountInformation.getBankAccount();
                    bankAccountCBU = bankAccountInformation.getCciCode();
                    bankAccountType = bankAccountInformation.getAccountType();
                }

                List<PersonDisqualifier> personDisqualifierList = personDao.getPersonDisqualifierByPersonId(person.getId());
                if(personDisqualifierList != null) {
                    pep = personDisqualifierList.stream().filter(p -> PersonDisqualifier.PEP.equals(p.getType())).findFirst().orElse(null);
                    facta = personDisqualifierList.stream().filter(p -> PersonDisqualifier.FACTA.equals(p.getType())).findFirst().orElse(null);
                }

                Direccion direccion = personDao.getDisggregatedAddress(person.getId(), "H");
                District generalDistrict = catalogService.getGeneralDistrictById(direccion.getLocalityId());

                String calendarioOferta;
                String totalCapital;
                String totalInteres;
                String totalCuota;
                if(credit != null){
                    calendarioOferta = credit.getOriginalSchedule().stream()
                            .map(o -> String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                                    o.getInstallmentId(),
                                    utilService.dateCustomFormat(o.getDueDate(), "dd/MM/yyyy", locale),
                                    "\\" + utilService.doubleMoneyFormat(o.getRemainingCapital(), credit.getCurrency()),
                                    "\\" + utilService.doubleMoneyFormat(o.getInstallmentCapital(), credit.getCurrency()),
                                    "\\" + utilService.doubleMoneyFormat(o.getTotalInterest(), credit.getCurrency()),
                                    "\\" + utilService.doubleMoneyFormat(o.getInstallmentAmount(), credit.getCurrency())
                            ))
                            .reduce("", String::concat).trim();
                    totalCapital = "\\" + utilService.doubleMoneyFormat(credit.getTotalScheduleField("installmentCapital", 'O'), credit.getCurrency());
                    totalInteres = "\\" + utilService.doubleMoneyFormat(credit.getTotalScheduleField("totalInterest", 'O'), credit.getCurrency());
                    totalCuota = "\\" + utilService.doubleMoneyFormat(credit.getTotalScheduleField("installmentAmount", 'O'), credit.getCurrency());
                }else{
                    if (offer == null)
                        offer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(LoanOffer::getSelected).findFirst().orElse(null);
                    final LoanOffer selectedOffer = offer;
                    calendarioOferta = offer.getOfferSchedule().stream()
                            .map(o -> String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                                    o.getInstallmentId(),
                                    utilService.dateCustomFormat(o.getDueDate(), "dd/MM/yyyy", locale),
                                    "\\" + utilService.doubleMoneyFormat(o.getRemainingCapital(), selectedOffer.getCurrency()),
                                    "\\" + utilService.doubleMoneyFormat(o.getInstallmentCapital(), selectedOffer.getCurrency()),
                                    "\\" + utilService.doubleMoneyFormat(o.getTotalInterest(), selectedOffer.getCurrency()),
                                    "\\" + utilService.doubleMoneyFormat(o.getInstallmentAmount(), selectedOffer.getCurrency())
                            ))
                            .reduce("", String::concat).trim();
                    totalCapital = "\\" + utilService.doubleMoneyFormat(selectedOffer.getTotalScheduleField("installmentCapital"), selectedOffer.getCurrency());
                    totalInteres = "\\" + utilService.doubleMoneyFormat(selectedOffer.getTotalScheduleField("totalInterest"), selectedOffer.getCurrency());
                    totalCuota = "\\" + utilService.doubleMoneyFormat(selectedOffer.getTotalScheduleField("installmentAmount"), selectedOffer.getCurrency());
                }

                String DYNAMIC_INSTALLMENT_TABLE_ROWS = "<table style=\"text-align:center;font-weight:bold;font-size:12px;margin:0 auto;\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"> " +
                        "<tbody> " +
                        "<tr> " +
                        "<td style=\"background:#E88846;text-align:center;width:100px;\">Cuota</td>" +
                        "<td style=\"background:#E88846;text-align:center;width:100px;\">Vencimiento</td>" +
                        "<td style=\"background:#E88846;text-align:center;width:100px;\">Saldo de deuda</td>" +
                        "<td style=\"background:#E88846;text-align:center;width:100px;\">Capital</td>" +
                        "<td style=\"background:#E88846;text-align:center;width:100px;\">Interés (Inc. IVA)</td>" +
                        "<td style=\"background:#E88846;text-align:center;width:100px;\">Cuota total</td>" +
                        "</tr>" +
                        calendarioOferta +
                        "<tr> <td colspan=\"2\" style=\"background: #E88846;\">Totales</td><td style=\"background: #E88846;\"></td><td style=\"background: #E88846;\">" + totalCapital + "</td><td style=\"background: #E88846;\">" + totalInteres + "</td><td style=\"background: #E88846;\">" + totalCuota + "</td></tr></tbody> </table>";
                Double amountWithStampTax = amount + (credit != null ? credit.getStampTax() : offer.getStampTax());

                String currentProvince = JsonUtil.getStringFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_PROVINCIA_RETIRO.getKey(), null);
                String provinceName = "";
                if(currentProvince != null && !currentProvince.isEmpty()){
                 Province province = catalogService.getGeneralProvinceById(Integer.valueOf(currentProvince));
                 if(province != null) provinceName = province.getName();
                }


                htmlContract = htmlContract
                        .replaceAll("%FULL_NAME%", printText(person.getFullName()))
                        .replaceAll("%DNI_DOCUMENT_NUMBER%", printText(person.getDNIFromCUIT()))
                        .replaceAll("%CUIT_DOCUMENT_NUMBER%", printText(person.getDocumentNumber()))
                        .replaceAll("%BIRTHDATE%", new SimpleDateFormat("dd/MM/yyyy").format(person.getBirthday()))
                        .replaceAll("%CIVIL_STATUS%", printText(person.getMaritalStatus() != null ? person.getMaritalStatus().getId() == MaritalStatus.COHABITANT ? "Unión Convivencial" : messageSource.getMessage(person.getMaritalStatus().getMessageKey(), null, Configuration.getDefaultLocale()) : null))// TODO TRANSLATE
                        .replaceAll("%STREET_NAME%", printText(direccion.getNombreVia()))
                        .replaceAll("%STREET_NUMBER%", printText(direccion.getNumeroVia() + (direccion.getFloor() != null ? ", piso " + direccion.getFloor() :"") + (direccion.getNumeroInterior() != null ? ", dpto. " + direccion.getNumeroInterior() :"") ))
                        .replaceAll("%STREET_REFERENCE%", printText(direccion.getReferencia()))
                        .replaceAll("%LOCALITY_NAME%", printText(generalDistrict.getName()))
                        .replaceAll("%PROVINCE_NAME%", printText(generalDistrict.getProvince().getName()))
                        .replaceAll("%COUNTRY_NAME%", printText(generalDistrict.getCountry().getName()))
                        .replaceAll("%POSTAL_CODE%", printText(generalDistrict.getPostalCode()))
                        .replaceAll("%LANDLINE_PHONENUMBER%", printText(person.getLandline()))
                        .replaceAll("%EMAIL%", printText(user.getEmail()))
                        .replaceAll("%ACTIVITY_TYPE%", printText(afipActivitiy != null ? afipActivitiy.getDescription() : null))
                        .replaceAll("%CURRENCY_SYMBOL%", "\\" + loanApplication.getCurrency().getSymbol())// escaped
                        .replaceAll("%LOAN_AMOUNT%", utilService.doubleOnlyMoneyFormat(amount, loanApplication.getCurrency()))
                        .replaceAll("%LOAN_AMOUNT_WITH_TAX%", utilService.doubleOnlyMoneyFormat(amountWithStampTax, loanApplication.getCurrency()))
                        .replaceAll("%FIRST_FEE_AMOUNT%", printText(utilService.doubleFormat(firstInstallmentAmount)))
                        .replaceAll("%FIRST_FEE_EXPIRATION_DATE%", printText(new SimpleDateFormat("dd/MM/yyyy").format(firstDueDate)))
                        .replaceAll("%FEE_AMOUNT%", printText(utilService.doubleFormat(secondInstallmentAmount)))
                        .replaceAll("%FEE_EXPIRATION_DAY%", printText(new SimpleDateFormat("dd").format(dueDate)))
                        .replaceAll("%TNA_PERCENTAGE%", printText(utilService.percentFormat(credit != null ? credit.getNominalAnualRate() : offer.getNominalAnualRate())))
                        .replaceAll("%TNA_PERCENTAGE_WITHOUT_IVA%", printText(utilService.percentFormat(credit != null ? generateCreditTceaWithoutIva(credit) : loanApplicationService.generateOfferTCEAWithoutIva(offer))))
                        .replaceAll("%TEA_PERCENTAGE%", utilService.doubleFormat(effectiveAnualRate))
                        .replaceAll("%IVA_PERCENTAGE%", printText(utilService.doubleFormat(3.0)))
                        .replaceAll("%INSTALMENTS%", printText(credit != null ? credit.getInstallments() + "" : offer.getInstallments() + ""))

                        .replaceAll("%BANK_NAME%", printText(bank))
                        .replaceAll("%BANK_OFFICE%", printText(" "))
                        .replaceAll("%BANK_ACCOUNT_TYPE%", printText(bankAccountType))
                        .replaceAll("%BANK_ACCOUNT_NUMBER%", printText(bankAccount))
                        .replaceAll("%BANK_ACCOUNT_CBU%", printText(bankAccountCBU))
                        .replaceAll("%BANK_ACCOUNT_ALIAS%", printText(" "))

                        .replaceAll("%LOAN_APPLICATION_REASON%", printText(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_REASON.getKey())))

                        .replaceAll("%IS_FACTA%", printText(facta != null ? facta.isDisqualified() ? "es" : "no es" : "es"))// FATCA
                        .replaceAll("%IS_FACTA_CSS%", facta != null ? facta.isDisqualified() ? "" : "display: none" : "")// FATCA
                        .replaceAll("%IS_PEP%", printText(pep != null ? pep.isDisqualified() ? "si" : "no" : "si"))
                        .replaceAll("%IS_PEP_CSS%", pep != null ? pep.isDisqualified() ? "" : "display: none" : "")
                        .replaceAll("%PERSON_ASSIGNEE_FULL_NAME%", printText(person.getFullName() + " / " + "----"))
                        .replaceAll("%YES_IS_FACTA_CSS%", facta != null ? facta.isDisqualified() ? "" : "text-decoration: line-through" : "")// FATCA
                        .replaceAll("%NO_IS_FACTA_CSS%", facta != null ? facta.isDisqualified() ? "text-decoration: line-through" : "" : "text-decoration: line-through")// FATCA
                        .replaceAll("%CLIENT_TYPE_VINCULADO%", "Vinculado".equalsIgnoreCase(bancoDelSolClientType) || "Actual".equalsIgnoreCase(bancoDelSolClientType) ? "[X]" : " ")// ver extranetDashboardFragments.html
                        .replaceAll("%CLIENT_TYPE_NO_VINCULADO%", "No Vinculado".equalsIgnoreCase(bancoDelSolClientType) ? "[X]" : " ")// ver extranetDashboardFragments.html
                        .replaceAll("%SIGN_DOES_EXIST%", "")
                        .replaceAll("%SIGN_DOES_NOT_EXIST%", "")
                        .replaceAll("%IS_HE%", person.getGender() != null ? "M".equalsIgnoreCase(person.getGender().toString()) ? "text-decoration: line-through" : " " : " ")// IS M then add line to SHE
                        .replaceAll("%IS_SHE%", person.getGender() != null ? "F".equalsIgnoreCase(person.getGender().toString()) ? "text-decoration: line-through" : " " : " ")// IS F then add line to SHE
                        .replaceAll("%TODAY%", new SimpleDateFormat("dd/MM/yyyy").format(new Date()))
                        .replaceAll("%PERSON_SIGN%", signature != null ? signature : "&nbsp;")
                        .replaceAll("%DOCUMENT_TYPE%", ((Integer) CountryParam.COUNTRY_ARGENTINA).equals(loanApplication.getCountryId()) ? "DNI" : person.getDocumentType().getName())
                        .replaceAll("%DOCUMENT_NUMBER%", ((Integer) CountryParam.COUNTRY_ARGENTINA).equals(loanApplication.getCountryId()) ? person.getDNIFromCUIT() : person.getDocumentNumber())
                        .replaceAll("%PERSON_FOREIGN_COUNTRY%", printText(" "))
                        .replaceAll("%PERSON_ASSIGNEE%", printText(" "))
                        .replaceAll("%PERSON_LEGAL_NAME%", printText(" "))
                        .replaceAll("%IS_CUIL_ASSIGNEE%", "text-decoration: line-through")
                        .replaceAll("%IS_CUIT_ASSIGNEE%", " ")
                        .replaceAll("%PERSON_LEGAL_DOCUMENT_NUMBER%", printText(person.getDocumentNumber()))
                        .replaceAll("%OBSERVATION%", printText(" "))
//                        CAMPOS A SER LLENADOS MANUALMENTE SON VACIOS
                        .replaceAll("%PAGARE_NUMERO_RESOLUCION%", "&nbsp;")
                        .replaceAll("%PAGARE_NUMERO_ORDEN%", "&nbsp;")
                        .replaceAll("%PAGARE_FECHA%", new SimpleDateFormat("dd/MM/yyyy").format(new Date()))
                        .replaceAll("%PAGARE_IMPUESTO_SELLOS%", printText(utilService.doubleFormat(credit != null ? credit.getStampTax() : offer.getStampTax())))
                        .replaceAll("%PAGARE_POR_BDS%", "&nbsp;")
                        .replaceAll("%PAGARE_ACUERDO_NRO%", "&nbsp;")
                        .replaceAll("%PAGARE_PAGARE_NRO%", "&nbsp;")
                        .replaceAll("%PAGARE_CC%", "&nbsp;")
                        .replaceAll("%PAGARE_CONTROL_TECNICO%", "&nbsp;")
                        .replaceAll("%PAGARE_CONTROL_FIRMAS%", "&nbsp;")
                        .replaceAll("%PAGARE_POR_AMOUNT%", printText(utilService.doubleFormat(amountWithStampTax)))
                        .replaceAll("%PAGARE_LUGAR_FECHA%", printText(provinceName + ", " + new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Configuration.getDefaultLocale()).format(new Date())))// TODO HARDCODED
                        .replaceAll("%PAGARE_PAGARE_AMOUNT%", printText(utilService.doubleFormat(amountWithStampTax)))
                        .replaceAll("%PAGARE_TASA_ANUAL%", utilService.doubleFormat(effectiveAnualRate))
                        .replaceAll("%PAGARE_DUE_DATE%", "")
                        .replaceAll("%PAGARE_PAGADERO%", "")
                        .replaceAll("%PAGARE_FIRMA_LIBRADOR%", "&nbsp;")
                        .replaceAll("%PAGARE_DOMICILIO_LIBRADOR%", printText(direccion.getDireccionCompleta()))
                        .replaceAll("%PAGARE_ACLARACION_FIRMA%", "&nbsp;")
                        .replaceAll("%PAGARE_DOCUMENTO%", "<br/><br/><br/>" + (((Integer) CountryParam.COUNTRY_ARGENTINA).equals(loanApplication.getCountryId()) ? person.getDNIFromCUIT() : person.getDocumentNumber()))

                        .replaceAll("%DYNAMIC_INSTALLMENT_TABLE_ROWS%", DYNAMIC_INSTALLMENT_TABLE_ROWS)
//                        .replaceAll("%INSTALLMENT_AMOUNT_TOTAL%", String.format(dynamicTable, "\\" + utilService.doubleMoneyFormat(offer.getTotalScheduleField("installmentAmount"), offer.getCurrency())))
                ;
                break;
            }
            case Entity.FINANSOL: {
                if(person.getPartner() != null)
                    pdfPath = params.getContracts().get(1).getPdfPath();
                else
                    pdfPath = params.getContracts().get(0).getPdfPath();

                String installmentRows = "";
                String totalInstallmentCapital = "";
                String totalInterest = "";
                String totalInstallment = "";
                String totalCommission = "";
                String totalInsurance = "";
                String totalInstallmentAmount = "";
                String tcea = "";
                if(credit != null){
                    for(OriginalSchedule schedule : credit.getOriginalSchedule()){
                        installmentRows = installmentRows + "<tr>\n" +
                                "<td>" + schedule.getInstallmentId() + "</td>\n" +
                                "<td>" + utilService.dateFormat(schedule.getDueDate()) + "</td>\n" +
                                "<td>" + utilService.doubleFormat(schedule.getRemainingCapital()) +"</td>\n" +
                                "<td>"+utilService.doubleFormat(schedule.getInstallmentCapital()) +"</td>\n" +
                                "<td>" + utilService.doubleFormat(schedule.getInterest())+ "</td>\n" +
                                "<td>"+utilService.doubleFormat(schedule.getInstallment()) +"</td>\n" +
                                "<td>"+utilService.doubleFormat(schedule.getTotalCollectionCommission()) +"</td>\n" +
                                "<td>"+utilService.doubleFormat(schedule.getInsurance()) +"</td>\n" +
                                "<td>"+utilService.doubleFormat(schedule.getInstallmentAmount()) +"</td>\n" +
                                "</tr>";
                    }
                    totalInstallmentCapital = utilService.doubleFormat(credit.getTotalScheduleField("installmentCapital", 'O'));
                    totalInterest = utilService.doubleFormat(credit.getTotalScheduleField("totalInterest", 'O'));
                    totalInstallment = utilService.doubleFormat(credit.getTotalScheduleField("installment", 'O'));
                    totalCommission = utilService.doubleFormat(credit.getTotalScheduleField("totalCollectionCommission", 'O'));
                    totalInsurance = utilService.doubleFormat(credit.getTotalScheduleField("insurance", 'O'));
                    totalInstallmentAmount = utilService.doubleFormat(credit.getTotalScheduleField("installmentAmount", 'O'));
                    tcea = utilService.percentFormat(credit.getEffectiveAnnualCostRate());

                }else{
                    for(OriginalSchedule schedule : offer.getOfferSchedule()){
                        installmentRows = installmentRows + "<tr>\n" +
                                "<td>" + schedule.getInstallmentId() + "</td>\n" +
                                "<td>" + utilService.dateFormat(schedule.getDueDate()) + "</td>\n" +
                                "<td>" + utilService.doubleFormat(schedule.getRemainingCapital()) +"</td>\n" +
                                "<td>"+utilService.doubleFormat(schedule.getInstallmentCapital()) +"</td>\n" +
                                "<td>" + utilService.doubleFormat(schedule.getInterest())+ "</td>\n" +
                                "<td>"+utilService.doubleFormat(schedule.getInstallment()) +"</td>\n" +
                                "<td>"+utilService.doubleFormat(schedule.getTotalCollectionCommission()) +"</td>\n" +
                                "<td>"+utilService.doubleFormat(schedule.getInsurance()) +"</td>\n" +
                                "<td>"+utilService.doubleFormat(schedule.getInstallmentAmount()) +"</td>\n" +
                                "</tr>";
                    }
                    totalInstallmentCapital = utilService.doubleFormat(offer.getTotalScheduleField("installmentCapital"));
                    totalInterest = utilService.doubleFormat(offer.getTotalScheduleField("interest"));
                    totalInstallment = utilService.doubleFormat(offer.getTotalScheduleField("installment"));
                    totalCommission = utilService.doubleFormat(offer.getTotalScheduleField("collectionCommission"));
                    totalInsurance = utilService.doubleFormat(offer.getTotalScheduleField("insurance"));
                    totalInstallmentAmount = utilService.doubleFormat(offer.getTotalScheduleField("installmentAmount"));
                    tcea = utilService.percentFormat(offer.getEffectiveAnnualCostRate());
                }

                String personTitle;
                String maritalStatus = person.getMaritalStatus().getStatus().toLowerCase();

                if (person.getGender() == null) {
                    personTitle = "el SR(A).";
                } else if (person.getGender() == 'M') {
                    personTitle = "el SR.";
                } else {
                    if (person.getMaritalStatus().getId() == MaritalStatus.SINGLE) {
                        personTitle = "la SRTA.";
                    } else {
                        personTitle = "la SRA.";
                    }
                    if (person.getMaritalStatus().getId() != MaritalStatus.COHABITANT) {
                        maritalStatus = maritalStatus.substring(0, maritalStatus.length() - 1) + "a";
                    }
                }

                Calendar calendar = Calendar.getInstance(locale);

                htmlContract = new String(fileService.getContract(pdfPath), Charset.forName("UTF-8"));
                htmlContract = htmlContract
//                        .replaceAll("%FULL_NAME%", printText(person.getFullName()))
                        .replaceAll("%DISBURSEMENT_DATE%", new SimpleDateFormat("dd/MM/yyyy", locale).format(calendar.getTime()))
                        .replaceAll("%LOAN_AMOUNT%", utilService.doubleOnlyMoneyFormat(amount, loanApplication.getCurrency()))
                        .replaceAll("%INSTALMENTS%", printText((credit != null ? credit.getInstallments() + "" : offer.getInstallments() + "") + " meses"))
                        .replaceAll("%TEA_PERCENTAGE%", utilService.doubleFormat(effectiveAnualRate))
                        .replaceAll("%INSTALLMENT_ROWS%", installmentRows)
                        .replaceAll("%TOTAL_INSTALMENT_CAPITAL%", totalInstallmentCapital)
                        .replaceAll("%TOTAL_INTEREST%", totalInterest)
                        .replaceAll("%TOTAL_INSTALMENT%", totalInstallment)
                        .replaceAll("%TOTAL_COMMISSION%", totalCommission)
                        .replaceAll("%TOTAL_INSURANCE%", totalInsurance)
                        .replaceAll("%TOTAL_INSTALLMENT_AMOUNT%", totalInstallmentAmount)
                        .replaceAll("%PERSON_TITLE%", personTitle)
                        .replaceAll("%FULL_NAME%", String.format("%s, %s", person.getFullSurnames().toUpperCase(), person.getName().toUpperCase()))
                        .replaceAll("%IDENTIFIED_TEXT%", person.getGender() == null ? "identificado(a)" : (person.getGender() == 'M' ? "identificado" : "identificada"))
                        .replaceAll("%MARITAL_STATUS%", maritalStatus)
                        .replaceAll("%IDENTITY_DOCUMENT_TYPE%", person.getDocumentType().getId() == IdentityDocumentType.DNI ? "DNI" : "CE")
                        .replaceAll("%IDENTITY_DOCUMENT_NUMBER%", person.getDocumentNumber())
                        .replaceAll("%LOAN_AMOUNT_INTEGER_TEXT%", NumberToTextConverter.convert(amount.intValue() + ""))
                        .replaceAll("%LOAN_AMOUNT_CENTS%", "00")
                        .replaceAll("%LOAN_AMOUNT_FORMATTED%", utilService.doubleOnlyMoneyFormat(amount, loanApplication.getCurrency()))
                        .replaceAll("%DENOMINATION%", person.getGender() == null ? "EL SOCIO(A)" : (person.getGender() == 'M' ? "EL SOCIO" : "LA SOCIA"))
                        .replaceAll("%CONTRACT_DATE%", new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", locale).format(calendar.getTime()))
                        .replaceAll("%MONTHS_NUMBER%", printText(credit != null ? credit.getInstallments() + "" : offer.getInstallments() + ""))
                        .replaceAll("%INSTALLMENTS_NUMBER%", printText(credit != null ? credit.getInstallments() + "" : offer.getInstallments() + ""))
                        .replaceAll("%PAYMENT_PERIODS%", "mensuales")
                        .replaceAll("%PERSON_FULL_ADDRESS%", contactInfo.getFullAddressBO())
                        .replaceAll("%SIGNATURE%", signature != null ? signature : "&nbsp;")
                        .replaceAll("%TCEA%", tcea)
                ;

                if (person.hasPartner()) {
                    htmlContract = htmlContract
                            .replaceAll("%PARTNER_TITLE%", person.getPartner().getGender() == null ? "el SR(A)." : (person.getPartner().getGender() == 'M' ? "el SR." : "la SRA."))
                            .replaceAll("%PARTNER_FULL_NAME%", String.format("%s, %s", person.getPartner().getFullSurnames().toUpperCase(), person.getPartner().getName().toUpperCase()))
                            .replaceAll("%PARTNER_IDENTIFIED_TEXT%", person.getPartner().getGender() == null ? "identificado(a)" : (person.getPartner().getGender() == 'M' ? "identificado" : "identificada"))
                            .replaceAll("%PARTNER_IDENTITY_DOCUMENT_TYPE%", person.getPartner().getDocumentType().getId() == IdentityDocumentType.DNI ? "DNI" : "CE")
                            .replaceAll("%PARTNER_IDENTITY_DOCUMENT_NUMBER%", person.getPartner().getDocumentNumber())
                            .replaceAll("%PHONE_NUMBER%", contactInfo.getPhoneNumber())
                            .replaceAll("%PARTNER_PHONE_NUMBER%", "") // TODO
                    ;
                }

                break;
            }
            case Entity.PRISMA: {
                pdfPath = "Cronograma-MF-Prisma.html";

                String installmentRows = "";
                Double totalInstallmentCapital;
                Double totalInterest;
                Double totalInstallment;
                Double totalInsurance;
                double lifeInsurance = 3.00;
                double contribution = 10.00;
                double other = 0.00;
                int installments = 0;

                if (credit != null) {
                    for (OriginalSchedule schedule : credit.getOriginalSchedule()) {
                        installmentRows = installmentRows + "<tr>\n" +
                                "<td>" + String.format("%3s", schedule.getInstallmentId()).replace(' ', '0')  + "</td>\n" +
                                "<td>" + utilService.dateFormat(schedule.getDueDate()) + "</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(schedule.getInstallmentAmount()) +"</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(schedule.getInstallmentCapital()) +"</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(schedule.getInterest())+ "</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(lifeInsurance) +"</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(schedule.getInsurance()) +"</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(contribution) +"</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(other) +"</td>\n" +
                                "<td>" + new SimpleDateFormat("EEEE", locale).format(schedule.getDueDate()).toUpperCase() +"</td>\n" +
                                "</tr>";
                    }
                    totalInstallmentCapital = credit.getTotalScheduleField("installmentCapital", 'O');
                    totalInterest = credit.getTotalScheduleField("totalInterest", 'O');
                    totalInstallment = credit.getTotalScheduleField("installment", 'O');
                    totalInsurance = credit.getTotalScheduleField("insurance", 'O');
                    installments = credit.getInstallments();

                } else {
                    for (OriginalSchedule schedule : offer.getOfferSchedule()) {
                        installmentRows = installmentRows + "<tr>\n" +
                                "<td>" + String.format("%3s", schedule.getInstallmentId()).replace(' ', '0')  + "</td>\n" +
                                "<td>" + utilService.dateFormat(schedule.getDueDate()) + "</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(schedule.getInstallmentAmount()) +"</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(schedule.getInstallmentCapital()) +"</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(schedule.getInterest())+ "</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(lifeInsurance) +"</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(schedule.getInsurance()) +"</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(contribution) +"</td>\n" +
                                "<td align=\"right\">" + utilService.doubleFormat(other) +"</td>\n" +
                                "<td>" + new SimpleDateFormat("EEEE", locale).format(schedule.getDueDate()).toUpperCase() +"</td>\n" +
                                "</tr>";
                    }
                    totalInstallmentCapital = offer.getTotalScheduleField("installmentCapital");
                    totalInterest = offer.getTotalScheduleField("interest");
                    totalInstallment = offer.getTotalScheduleField("installment");
                    totalInsurance = offer.getTotalScheduleField("insurance");
                    installments = offer.getInstallments();
                }

                Calendar calendar = Calendar.getInstance(locale);
                PersonBankAccountInformation bankAccount = personDao.getPersonBankAccountInformation(locale, person.getId());
                Direccion disaggregatedHomeAddress = personDao.getDisggregatedAddress(person.getId(), "H");

                String department = null;
                String province = null;
                String district = null;

                if (disaggregatedHomeAddress != null) {
                    Ubigeo ubigeo = catalogService.getUbigeo(disaggregatedHomeAddress.getUbigeo());
                    district = ubigeo.getDistrict().getName();
                    province = ubigeo.getProvince().getName();
                    department = ubigeo.getDepartment().getName();
                } else if (contactInfo != null && contactInfo.getAddressUbigeo() != null) {
                    district = contactInfo.getAddressUbigeo().getDistrict().getName();
                    province = contactInfo.getAddressUbigeo().getProvince().getName();
                    department = contactInfo.getAddressUbigeo().getDepartment().getName();
                }

                htmlContract = new String(fileService.getContract(pdfPath), StandardCharsets.UTF_8);
                htmlContract = htmlContract
                        .replaceAll("%DISBURSEMENT_DATE%", new SimpleDateFormat("dd/MM/yyyy", locale).format(calendar.getTime()))
                        .replaceAll("%ISSUED%", new SimpleDateFormat("dd/MM/yyyy", locale).format(calendar.getTime()))
                        .replaceAll("%RUC%", "20553180695") //TODO: PREGUNTAR
                        .replaceAll("%AGENCY%", "")//TODO
                        .replaceAll("%ADM_CARTERA%", "")//TODO
                        .replaceAll("%CURRENT_TIME%", new SimpleDateFormat("hh:mm:ss aa", locale).format(calendar.getTime()))
                        .replaceAll("%BANK_ACCOUNT_NUMBER%", bankAccount != null ? bankAccount.getBankAccount() : "")
                        .replaceAll("%CLIENT_NUMBER%", "")//TODO
                        .replaceAll("%FILE_NUMBER%", "")//TODO
                        .replaceAll("%PERSON_FULLNAME%", person.getFullName())
                        .replaceAll("%ADDRESS%", contactInfo != null ? contactInfo.getAddressStreetName() : "")
                        .replaceAll("%DEPARTMENT%", department)
                        .replaceAll("%PROVINCE%", province)
                        .replaceAll("%DISTRICT%", district)
                        .replaceAll("%SOURCE%", "")//TODO
                        .replaceAll("%LINE%", "")//TODO
                        .replaceAll("%PROGRAM%", "")//TODO
                        .replaceAll("%CURRENCY%", "SOLES")
                        .replaceAll("%AMOUNT%", utilService.doubleMoneyFormat(amount, loanApplication.getCurrency()))
                        .replaceAll("%INSTALLMENTS%", installments + "")
                        .replaceAll("%MODALITY%", "FIJA")
                        .replaceAll("%TEM%", "")//TODO
                        .replaceAll("%POLICY_NUMBER%", "")//TODO
                        .replaceAll("%SC_NUMBER%", "")//TODO
                        .replaceAll("%AMOUNT_LOAN%", utilService.doubleMoneyFormat(amount, loanApplication.getCurrency()))
                        .replaceAll("%TOTAL_AMOUNT%", utilService.doubleMoneyFormat(amount, loanApplication.getCurrency()))
                        .replaceAll("%INSTALLMENT_ROWS%", installmentRows)

                        .replaceAll("%TOTAL_INSTALMENT%", utilService.doubleMoneyFormat(totalInstallment))
                        .replaceAll("%TOTAL_INSTALMENT_CAPITAL%", utilService.doubleMoneyFormat(totalInstallmentCapital))
                        .replaceAll("%TOTAL_INTEREST%", utilService.doubleMoneyFormat(totalInterest))
                        .replaceAll("%TOTAL_INSURANCE%", utilService.doubleMoneyFormat(calculateTotal(lifeInsurance, installments)))
                        .replaceAll("%TOTAL_DISENCUMBRANCE%", utilService.doubleMoneyFormat(totalInsurance))
                        .replaceAll("%TOTAL_CONTRIBUTIONS%", utilService.doubleMoneyFormat(calculateTotal(contribution, installments)))
                        .replaceAll("%TOTAL_OTHERS%", utilService.doubleMoneyFormat(calculateTotal(other, installments)))
                        .replaceAll("%SIGNATURE%", signature != null ? signature : "&nbsp;")
                        .replaceAll("%FULL_NAME%", person.getFullName())
                        .replaceAll("%IDENTITY_DOCUMENT_NUMBER%", person.getDocumentNumber())
                        .replaceAll("%CLIENT_RUC%", "")
                ;

                break;
            }
        }


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(
                StringEscapeUtils.unescapeHtml4(htmlContract),
                Configuration.getClientDomain());
        renderer.layout();
        renderer.createPDF(os);

        byte[] pdfAsBytes = os.toByteArray();
        os.close();

        return pdfAsBytes;
    }

    @Override
    public byte[] renderAssociatedFileFromPdf(Credit credit, Person person, User user, Locale locale) throws Exception {

        if (FilenameUtils.getExtension(credit.getEntity().getApplicationFormUrl()).equalsIgnoreCase("pdf")) {
            PDDocument pdfDoc = PDDocument.load(fileService.getAssociatedFile(credit.getEntity().getApplicationFormUrl()));
            PDAcroForm acroForm = pdfDoc.getDocumentCatalog().getAcroForm();

            if (acroForm != null) {
                COSName handWritingFont = prepareFont(pdfDoc, "/PhontPhreak-sHandwriting.ttf");
                COSName defaultFont = prepareFont(pdfDoc, "/Calibri.ttf");

                // Fill the data
                switch (credit.getEntity().getId()) {
                    case Entity.AFFIRM: {
                        Person partner = person.getPartner();
                        if (partner == null)
                            partner = personDao.getPerson(catalogService, locale, person.getId(), true).getPartner();

                        PersonContactInformation contactInfo = personDao.getPersonContactInformation(locale, person.getId());

                        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, person.getId());
                        PersonOcupationalInformation principalOcupation = ocupations != null ?
                                ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null) : null;
                        MutableInt otherMonthlyIncomes = new MutableInt();
                        if (ocupations != null)
                            ocupations.stream().filter(o -> o.getNumber() != PersonOcupationalInformation.PRINCIPAL).forEach(o -> otherMonthlyIncomes.add(o.getFixedGrossIncome() != null ? o.getFixedGrossIncome() : 0));

                        Integer principalNetIncome = principalOcupation != null && principalOcupation.getFixedGrossIncome() != null ?
                                principalOcupation.getFixedGrossIncome().intValue() : new Integer(0);

                        // TODO Sleect the employee that the person selected in the beginig
                        List<Employee> personEmployees = personDao.getEmployeesByDocument(person.getDocumentType().getId(), person.getDocumentNumber(), locale);
                        PersonBankAccountInformation personBankInformation = personDao.getPersonBankAccountInformation(locale, person.getId());
                        Employee personEmployee = null;
                        if (personEmployees != null && credit.getEmployer() != null)
                            personEmployee = personEmployees.stream().filter(e -> e.getEmployer().getId().intValue() == credit.getEmployer().getId()).findFirst().orElse(null);

                        fillPdfBoxText(acroForm, "fs.person.name", person.getName(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.surnames", person.getFullSurnames(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.nationality", person.getNationality().getName(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.birthday", utilService.dateCustomFormat(person.getBirthday(), "dd/MM/yyyy", locale), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.birthPlace", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.instructionDegree", person.getStudyLevel() != null ? person.getStudyLevel().getLevel() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.profession", person.getProfession() != null ? person.getProfession().getProfession() : null, defaultFont);
                        if (person.getDocumentType().getId() != IdentityDocumentType.RUC)
                            fillPdfBoxText(acroForm, "fs.person.docNumber", person.getDocumentType().getName() + " " + person.getDocumentNumber(), defaultFont);
                        else if (person.getDocumentType().getId() == IdentityDocumentType.RUC)
                            fillPdfBoxText(acroForm, "fs.person.ruc", person.getDocumentType().getName() + " " + person.getDocumentNumber(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.lm", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.license", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.passport", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.maritalStatus", person.getMaritalStatus() != null ? person.getMaritalStatus().getStatus() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.home.address", contactInfo != null ? contactInfo.getFullAddress() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.home.type.own", contactInfo != null && contactInfo.getHousingType() != null && (contactInfo.getHousingType().getId() == contactInfo.getHousingType().OWN || contactInfo.getHousingType().getId() == contactInfo.getHousingType().OWN_FINANCED) ? 'X' : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.home.type.rented", contactInfo != null && contactInfo.getHousingType() != null && contactInfo.getHousingType().getId() == contactInfo.getHousingType().RENTED ? 'X' : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.home.type.family", contactInfo != null && contactInfo.getHousingType() != null && contactInfo.getHousingType().getId() == contactInfo.getHousingType().FAMILY ? 'X' : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.home.district", contactInfo != null && contactInfo.getAddressUbigeo() != null ? contactInfo.getAddressUbigeo().getDistrict().getName() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.landline", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.cellphone", user.getPhoneNumber(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.employee.employer.name", personEmployee != null ? personEmployee.getEmployer().getName() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.employee.activityType", "DEPEN", defaultFont);
                        fillPdfBoxText(acroForm, "fs.employee.employer.fax", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.employee.employer.address", personEmployee != null ? personEmployee.getEmployer().getAddress() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.employee.employer.district", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.employee.employer.telephone", personEmployee != null ? personEmployee.getEmployer().getPhoneNumber() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.employee.position", principalOcupation != null && principalOcupation.getOcupation() != null ? principalOcupation.getOcupation().getOcupation() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.employee.startDate", personEmployee != null ?
                                utilService.dateCustomFormat(personEmployee.getEmploymentStartDateDate(), "dd/MM/yyyy", locale) : null, defaultFont);
                        if (personEmployee != null && personEmployee.getEmploymentStartDateDate() != null) {
                            LocalDate dateStart = personEmployee.getEmploymentStartDateDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            LocalDate dateNow = LocalDate.now();
                            fillPdfBoxText(acroForm, "fs.employee.serviceTime", ChronoUnit.MONTHS.between(dateStart, dateNow) + " meses", defaultFont);
                        }
                        fillPdfBoxText(acroForm, "fs.person.otherIncomes", utilService.doubleMoneyFormat(otherMonthlyIncomes.intValue()), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.monthlyIncome", utilService.doubleMoneyFormat(principalNetIncome), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.incomes", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.specifyOrigins", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.totalFamilyIncomes", utilService.doubleMoneyFormat(otherMonthlyIncomes.intValue() + principalNetIncome), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.names", partner != null ? partner.getName() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.surnames", partner != null ? partner.getFullSurnames() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.nationality", partner != null && partner.getNationality() != null ? partner.getNationality().getName() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.birthday", partner != null ? utilService.dateCustomFormat(partner.getBirthday(), "dd/MM/yyyy", locale) : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.birthplace", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.instructionDegree", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.profession", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.docNumber", partner != null ? partner.getDocumentType().getName() + " " + partner.getDocumentNumber() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.ruc", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.lm", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.employee.employer.name", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.employee.activityType", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.employee.employer.address", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.employee.employer.district", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.employee.employer.telephone", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.partner.employee.position", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.creditcards.1.name", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.creditcards.1.line", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.creditcards.2.name", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.creditcards.2.line", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.creditcards.3.name", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.creditcards.3.line", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.signature.date", utilService.dateCustomFormat(new Date(), "dd/MM/yyyy", locale), defaultFont);
//                        fillPdfBoxText(acroForm, "fs.signature.signature", "");
                        break;
                    }
                    case Entity.ABACO: {
                        PersonContactInformation contactInfo = personDao.getPersonContactInformation(locale, person.getId());
                        PersonAssociated personAssociated = personDao.getAssociated(person.getId(), credit.getEntity().getId());

                        fillPdfBoxText(acroForm, "fs.partnerCode", personAssociated != null ? personAssociated.getAssociatedId() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.name", person.getName(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.surnames", person.getFirstSurname(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.lastname", person.getLastSurname(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.nationality", person.getNationality() != null ? person.getNationality().getName() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.nationality2", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.birthday", utilService.dateCustomFormat(person.getBirthday(), "dd/MM/yyyy", locale), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.gender", person.getGender(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.document", person.getDocumentType().getName(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.docNumber", person.getDocumentNumber(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.maritalStatus", person.getMaritalStatus() != null ? person.getMaritalStatus().getStatus() : null, defaultFont);

                        fillPdfBoxText(acroForm, "fs.person.home.road", contactInfo != null && contactInfo.getAddressStreetType() != null ? contactInfo.getAddressStreetType().getType() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.home.address", contactInfo != null ? contactInfo.getFullAddress() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.home.department", contactInfo != null && contactInfo.getAddressUbigeo() != null ? contactInfo.getAddressUbigeo().getDepartment().getName() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.home.region", contactInfo != null && contactInfo.getAddressUbigeo() != null ? contactInfo.getAddressUbigeo().getProvince().getName() : null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.home.district", contactInfo != null && contactInfo.getAddressUbigeo() != null ? contactInfo.getAddressUbigeo().getDistrict().getName() : null, defaultFont);

                        fillPdfBoxText(acroForm, "fs.person.country.address", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.home.country", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.landline", null, defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.cellphone", user.getPhoneNumber(), defaultFont);
                        fillPdfBoxText(acroForm, "fs.person.email", user.getEmail(), defaultFont);
//                        fillPdfBoxText(acroForm, "fs.signature.signature", "");
                        break;
                    }
                }

                // Flat the pdf (not editable)
                acroForm.flatten();
            }

            // Save it to byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pdfDoc.save(out);
            pdfDoc.close();

            return out.toByteArray();

        } else if (FilenameUtils.getExtension(credit.getEntity().getApplicationFormUrl()).equalsIgnoreCase("html")) {

            String htmlContent = new String(fileService.getAssociatedFile(credit.getEntity().getApplicationFormUrl()), Charset.forName("ISO-8859-1"));

            PersonContactInformation contactInfo = personDao.getPersonContactInformation(locale, person.getId());
            Date today = new Date();

            switch (credit.getEntity().getId()) {
                case Entity.EFL:
                case Entity.AFFIRM:
                    credit.setManagementSchedule(creditDao.getManagementSchedule(credit.getId()));
                    Date dueDate = credit.getManagementSchedule().get(credit.getManagementSchedule().size() - 1).getDueDate();

                    htmlContent = htmlContent
                            .replaceAll("%AMOUNT%", "")
                            .replaceAll("%TEA%", utilService.doubleFormat(credit.getEffectiveAnnualRate()))
                            .replaceAll("%ADDRESS%", contactInfo != null && contactInfo.getAddressStreetName() != null ? contactInfo.getAddressStreetName() : "")
                            .replaceAll("%IDENTITY_DOCUMENT%", person.getDocumentNumber())
                            .replaceAll("%SIGNATURE%", "")
                            .replaceAll("%DAYS%", new SimpleDateFormat("dd").format(today))
                            .replaceAll("%MONTH%", new SimpleDateFormat("MMMM", locale).format(today))
                            .replaceAll("%YEAR%", new SimpleDateFormat("yyyy").format(today))
                            .replaceAll("%DUE_DAY%", new SimpleDateFormat("dd").format(dueDate))
                            .replaceAll("%DUE_MONTH%", new SimpleDateFormat("MMMM", locale).format(dueDate))
                            .replaceAll("%DUE_YEAR%", new SimpleDateFormat("yyyy").format(dueDate))
                            .replaceAll("%TEA_MORATORIA%", utilService.doubleFormat(credit.getMoratoriumRate() != null ? credit.getMoratoriumRate() * 100 : null))
                            .replaceAll("%AUTOMATIC_CHARGE%", utilService.doubleMoneyFormat(credit.getCommission2()))
                            .replaceAll("%EMITENTE%", person.getFullName());
                    break;
            }


            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(
                    StringEscapeUtils.unescapeHtml4(htmlContent),
                    Configuration.getClientDomain());
            renderer.layout();
            renderer.createPDF(os);

            byte[] pdfAsBytes = os.toByteArray();
            os.close();

            return pdfAsBytes;
        }

        return null;
    }

    private void fillPdfBoxCheck(PDAcroForm acroForm, String fieldName, boolean value) {
        try {
            if (value)
                ((PDCheckBox) acroForm.getField(fieldName)).check();
            else
                ((PDCheckBox) acroForm.getField(fieldName)).unCheck();
        } catch (Exception ex) {
//            logger.error("Error setting value to pdfBox", ex);
        }
    }

    private void fillPdfBoxText(PDAcroForm acroForm, String fieldName, Object value, COSName font) {
        fillPdfBoxText(acroForm, fieldName, value, font, null);
    }

    private void fillPdfBoxText(PDAcroForm acroForm, String fieldName, Object value, COSName font, Integer fontSize) {
        try {
            PDField field = acroForm.getField(fieldName);

            COSDictionary dict = field.getCOSObject();
            COSString defaultAppearance = (COSString) dict.getDictionaryObject(COSName.DA);
            if (defaultAppearance != null) {
                String[] daString = defaultAppearance.getASCII().split(" ", 2);
                String[] fontSizeSplit = daString[1].split(" ", 2);
                dict.setString(COSName.DA, "/" + font.getName() + " " + (fontSize != null ? fontSize : fontSizeSplit[0]) + " " + fontSizeSplit[1]);
            }
            ((PDTextField) acroForm.getField(fieldName)).setValue(value != null ? value + "" : PDF_FILL_DEFAULT_NULL_VALUE);
        } catch (Exception ex) {
//            logger.error("Error setting value to pdfBox" + fieldName, ex);
        }
    }

    private String printText(String text) {
        return text != null ? text : PDF_FILL_DEFAULT_NULL_VALUE;
    }

    public COSName prepareFont(PDDocument _pdfDocument, String path) throws IOException {
        PDDocumentCatalog docCatalog = _pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();

        PDResources res = acroForm.getDefaultResources();
        if (res == null)
            res = new PDResources();

        InputStream fontStream = getClass().getResourceAsStream(path);
        PDType0Font font = PDType0Font.load(_pdfDocument, fontStream);
        COSName fontName = res.add(font);
        acroForm.setDefaultResources(res);

        return fontName;
    }

    @Override
    public byte[] createCreditContract(int creditId, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine, String filename, boolean toDownload) throws Exception {

        Credit credit = creditDao.getCreditByID(creditId, locale, true, Credit.class);
        Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), false);
        EntityProductParams params = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
        EntityBranding entityBranding = catalogService.getEntityBranding(credit.getEntity().getId());

        // Create the contract if it doesnt exists
        if (credit.getContractUserFileId() == null) {
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);

            WebApplicationContext webappContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());

            final Map<String, Object> mergedModel = new HashMap<String, Object>(30);
            final ConversionService conversionService = (ConversionService) request.getAttribute(ConversionService.class.getName());
            final ThymeleafEvaluationContext evaluationContext = new ThymeleafEvaluationContext(webappContext, conversionService);
            mergedModel.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

            SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), locale, mergedModel, WebApplicationContextUtils.getWebApplicationContext(request.getServletContext()));
            // String pdfPath = params.getContracts().get(0).getPdfPath();
            String pdfPath = params.getContracts().get(0).getPdfPath();
            ctx.setVariable("params", params);
            ctx.setVariable("pdfPath", pdfPath);
            ctx.setVariable("entityBranding", entityBranding);
            ctx.setVariable("credit", credit);
            ctx.setVariable("person", person);
            ctx.setVariable("personAddress", personDao.getPersonContactInformation(locale, person.getId()));
            ctx.setVariable("loanApplication", loanApplication);
            Optional<LoanOffer> offer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst();
            if (offer.isPresent() && offer.get().getSignatureFullName() != null) {
                ctx.setVariable("signature", offer.get().getSignatureFullName());
            }

            String htmlContent = templateEngine.process("exportSignaturePdf", ctx);
            htmlContent = htmlContent.replace("$amp;", "&");

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();

            renderer.setDocumentFromString(
                    StringEscapeUtils.unescapeHtml4(htmlContent),
                    Configuration.getClientDomain());
            renderer.layout();
            renderer.createPDF(os);

            byte[] pdfAsBytes = os.toByteArray();
            os.close();

            if (!toDownload) {
                if (filename == null || filename.isEmpty()) {
                    filename = credit.getId() + "_contrato_" + personDao.getPerson(catalogService, locale, person.getId(), false).getFirstName() + ".pdf";
                }
                saveContract(loanApplication, person, pdfAsBytes, filename, true);
            }

            return pdfAsBytes;
        }
        return null;
    }

    private void saveContract(LoanApplication loanApplication, Person person, byte[] pdfAsBytes, String filename, boolean async) throws Exception {
        String newFileName = fileService.writeUserFile(pdfAsBytes, person.getUserId(), filename, async);
        Integer productId = loanApplication.getProduct() != null ? loanApplication.getProduct().getId() : null;
        if(productId == null) productId = loanApplication.getSelectedProductId();
        int contractUserFileId = userDao.registerUserFile(person.getUserId(), loanApplication.getId(),
                productId == Product.SALARY_ADVANCE ? UserFileType.CONTRATO_FIRMA_ADELANTO_SUELDO : UserFileType.CONTRATO_FIRMA, newFileName);
        creditDao.updateCreditContract(loanApplication.getCreditId(), new Integer[]{contractUserFileId});
    }

    @Override
    public void sendDisbursementConfirmationEmailAndSms(Credit credit, Person person, User user, byte[] contractBytes) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), Configuration.getDefaultLocale());


        // Set vars
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("CLIENT_NAME", person.getFirstName());
        jsonParams.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonParams.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        jsonParams.put("ENTITY", credit.getEntity().getFullName());

        // Send email
        if (credit.getProduct().getId() == Product.DEBT_CONSOLIDATION_OPEN) {
            debtConsolidationService.sendConsolidableAccountsEmail(credit.getId(), Configuration.getDefaultLocale(), InteractionContent.DISBURSEMENT_CONSOLIDATION_OPEN_MAIL, contractBytes);
        } else {
            PersonInteraction interaction = new PersonInteraction();
            interaction.setPersonId(person.getId());
            interaction.setCreditId(credit.getId());
            interaction.setCreditCode(credit.getCode());
            interaction.setLoanApplicationId(credit.getLoanApplicationId());
            interaction.setDestination(user.getEmail());
            interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
            interaction.setInteractionContent(
                    catalogService.getInteractionContent(credit.getProduct().getId() == Product.SALARY_ADVANCE ? InteractionContent.DISBURSEMENT_MAIL_SALARY_ADVANCE : InteractionContent.DISBURSEMENT_MAIL, loanApplication.getCountryId()));

            EntityProductParams entityParam = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
            if (entityParam.getSendContract()) {

                // Validate the contract exists
                if (credit.getContractUserFileId() == null) {
                    throw new Exception("There is no contract created yet, and its not posible create it in worker");
                }

                interaction.setAttachments(new ArrayList<>());

                // Add the CONTRACT to te email as an attachment
                if (contractBytes != null) {
                    PersonInteractionAttachment attachment = new PersonInteractionAttachment();
                    attachment.setBytes(contractBytes);
                    // TODO Instead of putting the first of the list, send the userFileId as parameter with th byte[]
                    attachment.setUserFileId(credit.getContractUserFileId().get(0));
                    interaction.getAttachments().add(attachment);
                } else {
                    for (int i = 0; i < credit.getContractUserFileId().size(); i++) {
                        PersonInteractionAttachment attachment = new PersonInteractionAttachment();
                        attachment.setUserFileId(credit.getContractUserFileId().get(i));
                        interaction.getAttachments().add(attachment);
                    }
                }

                // Add the las HOJA RESUMEN created to the email as an attachment
                List<UserFile> loanUserFiles = loanApplicationDao.getLoanApplicationUserFiles(credit.getLoanApplicationId());
                if (loanUserFiles != null) {
                    List<UserFile> hojaResumenes = loanUserFiles
                            .stream()
                            .filter(f -> f.getFileType().getId() == UserFileType.HOJA_RESUMEN)
                            .sorted(Comparator.comparing(UserFile::getUploadTime, Comparator.nullsLast(Comparator.reverseOrder())))
                            .collect(Collectors.toList());
                    if (!hojaResumenes.isEmpty()) {
                        PersonInteractionAttachment attachment = new PersonInteractionAttachment();
                        attachment.setUserFileId(hojaResumenes.get(0).getId());
                        interaction.getAttachments().add(attachment);
                    }
                }
            }

            interactionService.sendPersonInteraction(interaction, jsonParams, null);
        }

        // Send sms
        PersonInteraction smsInteraction = new PersonInteraction();
        smsInteraction.setPersonId(person.getId());
        smsInteraction.setCreditId(credit.getId());
        smsInteraction.setCreditCode(credit.getCode());
        smsInteraction.setLoanApplicationId(credit.getLoanApplicationId());
        smsInteraction.setDestination("+" + user.getCountryCode() + user.getPhoneNumber());
        smsInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.SMS));
        smsInteraction.setInteractionContent(
                catalogService.getInteractionContent(InteractionContent.DISBURSEMENT_SMS, loanApplication.getCountryId()));

        interactionService.sendPersonInteraction(smsInteraction, jsonParams, null);
    }

    @Override
    public void sendDisbursementConfirmationEmailDealerEntity(Credit credit, Person person, User user, byte[] contractBytes) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), Configuration.getDefaultLocale());


        // Set vars
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("CLIENT_NAME", person.getFirstName());
        jsonParams.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonParams.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        jsonParams.put("ENTITY", credit.getEntity().getFullName());

        // Send email
        String[] ccEmails = {credit.getEntity().getEmail()};


        PersonInteraction interaction = new PersonInteraction();
        interaction.setPersonId(person.getId());
        interaction.setCreditId(credit.getId());
        interaction.setCreditCode(credit.getCode());
        interaction.setLoanApplicationId(credit.getLoanApplicationId());
        interaction.setDestination(credit.getVehicle().getVehicleDealership().getEmail());
        interaction.setCcMails(ccEmails);
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.DISBURSEMENT_DEALER_ENTITY, loanApplication.getCountryId()));
        EntityProductParams entityParam = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
        if (entityParam.getSendContract()) {
            // Validate the contract exists
            if (credit.getContractUserFileId() == null) {
                throw new Exception("There is no contract created yet, and its not posible create it in worker");
            }

            interaction.setAttachments(new ArrayList<>());
            if (contractBytes != null) {
                PersonInteractionAttachment attachment = new PersonInteractionAttachment();
                attachment.setBytes(contractBytes);
                // TODO Instead of putting the first of the list, send the userFileId as parameter with th byte[]
                attachment.setUserFileId(credit.getContractUserFileId().get(0));
                interaction.getAttachments().add(attachment);
            } else {
                for (int i = 0; i < credit.getContractUserFileId().size(); i++) {
                    PersonInteractionAttachment attachment = new PersonInteractionAttachment();
                    attachment.setUserFileId(credit.getContractUserFileId().get(i));
                    interaction.getAttachments().add(attachment);
                }
            }
        }

        interactionService.sendPersonInteraction(interaction, jsonParams, null);
    }

    @Override
    public void sendAccreditedPaymentEmailAndSms(Credit credit, Person person, User user) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), Configuration.getDefaultLocale());

        // Set vars
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("CLIENT_NAME", person.getFirstName());
        jsonParams.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonParams.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        jsonParams.put("DAYS_AFTER_END_OF_MONTH", ((credit.getEmployer() != null ? credit.getEmployer().getDaysAfterEndOfMonth() : null) != null) ? (credit.getEmployer().getDaysAfterEndOfMonth().toString()) : null);

        // Send email
        if (credit.getStatus().getId() == CreditStatus.CANCELED && credit.getProduct().getId() == Product.SALARY_ADVANCE) {
            PersonInteraction mailInteraction = new PersonInteraction();
            mailInteraction.setPersonId(person.getId());
            mailInteraction.setCreditId(credit.getId());
            mailInteraction.setCreditCode(credit.getCode());
            mailInteraction.setLoanApplicationId(credit.getLoanApplicationId());
            mailInteraction.setDestination(user.getEmail());
            mailInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
            mailInteraction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.SALARY_ADVANCE_TOTAL_PAYMENT_RECIVED_MAIL, loanApplication.getCountryId()));

            interactionService.sendPersonInteraction(mailInteraction, jsonParams, null);
        } else if (credit.getStatus().getId() == CreditStatus.CANCELED) {
            PersonInteraction mailInteraction = new PersonInteraction();
            mailInteraction.setPersonId(person.getId());
            mailInteraction.setCreditId(credit.getId());
            mailInteraction.setCreditCode(credit.getCode());
            mailInteraction.setLoanApplicationId(credit.getLoanApplicationId());
            mailInteraction.setDestination(user.getEmail());
            mailInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
            mailInteraction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.TOTAL_PAYMENT_RECIVED_MAIL, loanApplication.getCountryId()));

            interactionService.sendPersonInteraction(mailInteraction, jsonParams, null);
        } else {
            PersonInteraction mailInteraction = new PersonInteraction();
            mailInteraction.setPersonId(person.getId());
            mailInteraction.setCreditId(credit.getId());
            mailInteraction.setCreditCode(credit.getCode());
            mailInteraction.setLoanApplicationId(credit.getLoanApplicationId());
            mailInteraction.setDestination(user.getEmail());
            mailInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
            mailInteraction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.PAYMENT_RECIVED_MAIL, loanApplication.getCountryId()));

            interactionService.sendPersonInteraction(mailInteraction, jsonParams, null);
        }

        // Send sms
        if (credit.getStatus().getId() == CreditStatus.CANCELED) {
            PersonInteraction smsInteraction = new PersonInteraction();
            smsInteraction.setPersonId(person.getId());
            smsInteraction.setCreditId(credit.getId());
            smsInteraction.setCreditCode(credit.getCode());
            smsInteraction.setLoanApplicationId(credit.getLoanApplicationId());
            smsInteraction.setDestination("+" + user.getCountryCode() + user.getPhoneNumber());
            smsInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.SMS));
            smsInteraction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.TOTAL_PAYMENT_RECIVED_SMS, loanApplication.getCountryId()));

            interactionService.sendPersonInteraction(smsInteraction, jsonParams, null);
        } else {
            PersonInteraction smsInteraction = new PersonInteraction();
            smsInteraction.setPersonId(person.getId());
            smsInteraction.setCreditId(credit.getId());
            smsInteraction.setCreditCode(credit.getCode());
            smsInteraction.setLoanApplicationId(credit.getLoanApplicationId());
            smsInteraction.setDestination("+" + user.getCountryCode() + user.getPhoneNumber());
            smsInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.SMS));
            smsInteraction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.PAYMENT_RECIVED_SMS, loanApplication.getCountryId()));

            interactionService.sendPersonInteraction(smsInteraction, jsonParams, null);
        }

        // Send mail to total employer payment
        if (credit.getProduct().getId() == Product.SALARY_ADVANCE) {
            Employee employee = personDao.getEmployeeByPerson(credit.getPersonId(), null, Configuration.getDefaultLocale());
            EmployerCreditStats stats = employerDao.getEmployerCreditStats(employee.getEmployer().getId());
            if (stats.getTotalPendinAmount() == 0) {
                List<UserEmployer> userEmployers = employerDao.getUserEmployersByEmployer(employee.getEmployer().getId(), Configuration.getDefaultLocale());
                if (userEmployers != null && !userEmployers.isEmpty()) {
                    UserEmployer administrator = userEmployers.get(0);
                    List<UserEmployer> otherEmployers = userEmployers.subList(1, userEmployers.size());
                    jsonParams.put("ADMINISTRADOR_NAME", administrator.getName()); // TODO Identify the real ADMINISTRATOR

                    PersonInteraction mailInteraction = new PersonInteraction();
                    mailInteraction.setDestination(administrator.getEmail());
                    if (!otherEmployers.isEmpty()) {
                        List<String> ccMails = otherEmployers.stream().map(UserEmployer::getEmail).collect(Collectors.toList());
                        mailInteraction.setCcMails(ccMails.toArray(new String[otherEmployers.size()]));
                    }
                    mailInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    mailInteraction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.EMPLOYER_TOTAL_PAYMENT_RECIVED_MAIL, loanApplication.getCountryId()));

                    interactionService.sendPersonInteraction(mailInteraction, jsonParams, null);
                }
            }
        }
    }

    @Override
    public String renderContract(Integer creditId, Locale locale, boolean toSign, String signature) throws Exception {
        Credit credit = creditDao.getCreditByID(creditId, locale, false, Credit.class);
        return renderContract(credit.getPersonId(), credit.getEntity().getId(), credit.getProduct().getId(), locale, toSign, signature);
    }

    @Override
    public Boolean isDisbursementActive(Date date) throws Exception {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(date);

        int currentHour = currentDate.get(Calendar.HOUR_OF_DAY);
        int currentMin = currentDate.get(Calendar.MINUTE);

        DateFormat formatter = new SimpleDateFormat("hh:mm");
        Date toCompare = (Date) formatter.parse(currentHour + ":" + currentMin);
        Date iniWork;
        Date endWork;

        switch (currentDate.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
            case Calendar.FRIDAY:
                iniWork = formatter.parse("07:00");
                endWork = formatter.parse("18:30");
                if (toCompare.before(iniWork) || toCompare.after(endWork)) {
                    return false;
                }
                break;
            case Calendar.SATURDAY:
                iniWork = formatter.parse("07:00");
                endWork = formatter.parse("13:00");
                if (toCompare.before(iniWork) || toCompare.after(endWork)) {
                    return false;
                }
                break;
            case Calendar.SUNDAY:
                return false;
        }
        return true;
    }

    @Override
    public String renderContract(int personId, int entityId, int productId, Locale locale, boolean toSign, String signature) throws Exception {
        Person person = personDao.getPerson(catalogService, locale, personId, false);
        PersonContactInformation personContact = personDao.getPersonContactInformation(locale, personId);
        EntityProductParams params = catalogService.getEntityProductParam(entityId, productId);

        Document document = Jsoup.parse(params.getContracts().get(0).getContract(), "", Parser.xmlParser());
        if (toSign && document.getElementById("signed") != null) {
            document.getElementById("signed").remove();
        } else if (!toSign && document.getElementById("unsigned") != null) {
            document.getElementById("unsigned").remove();
        }

        return document.html()
                .replaceAll("%YEAR%", Calendar.getInstance().get(Calendar.YEAR) + "")
                .replaceAll("%DAYS%", Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "")
                .replaceAll("%MONTH%", Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, locale) + "")
                .replaceAll("%SIGNATURE%", signature != null ? signature : "")
                .replaceAll("%PERSON_NAME%", person.getFullName())
                .replaceAll("%DOCUMENT_TYPE%", person.getDocumentType().getName())
                .replaceAll("%PERSON_ADDRESS%", personContact.getFullAddress())
                .replaceAll("%DOCUMENT_NUMBER%", person.getDocumentNumber());
    }


    @Override
    public byte[] renderAccesoCertificadoPrepaprobacion(LoanApplication loanApplication, LoanOffer offer, Person person) throws Exception {

        PDDocument pdfDoc = PDDocument.load(fileService.getAssociatedFile("Certificado_preaprobacion_acceso.pdf"));
        PDAcroForm acroForm = pdfDoc.getDocumentCatalog().getAcroForm();

        if (acroForm != null) {
            COSName handWritingFont = prepareFont(pdfDoc, "/PhontPhreak-sHandwriting.ttf");
            COSName defaultFont = prepareFont(pdfDoc, "/Calibri.ttf");

            // Get all the data to use


            // Fill the data
            fillPdfBoxText(acroForm, "loanOffer.fecha", "Lima, " + new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Configuration.getDefaultLocale()).format(new Date()), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.nombre", person.getFullNameSurnameFirst(), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.documento", person.getDocumentNumber(), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.concesionario", loanApplication.getVehicle().getVehicleDealership().getName(), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.marca", loanApplication.getVehicle().getBrand().getBrand(), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.modelo", loanApplication.getVehicle().getModel(), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.valor", utilService.doubleMoneyFormat(loanApplication.getVehicle().getPrice().doubleValue(), Currency.USD_CURRENCY), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.cuotaInicial", utilService.doubleMoneyFormat(offer.getDownPayment(), Currency.USD_CURRENCY), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.maf", utilService.doubleMoneyFormat(offer.getAmmount()), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.otrosGastos", null, defaultFont);
            // Cuota promedio tiene incluido el seguro del auto
            Double installmentAmountAvg = offer.getInstallmentAmountAvg();
            Double carInsurance = offer.getOfferSchedule().get(0).getCarInsurance();
            fillPdfBoxText(acroForm, "loanOffer.cuotaMensual", utilService.doubleMoneyFormat(installmentAmountAvg - carInsurance), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.seguro", utilService.doubleMoneyFormat(carInsurance), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.cuotaTotal", utilService.doubleMoneyFormat(installmentAmountAvg), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.plazo", offer.getInstallments(), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.desgravamen", utilService.doubleMoneyFormat(offer.getOfferSchedule().get(0).getInsurance()), defaultFont);
            fillPdfBoxText(acroForm, "loanOffer.firma", "", defaultFont);

            // Flat the pdf (not editable)
            acroForm.flatten();
        }

        // Save it to byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pdfDoc.save(out);
        pdfDoc.close();

        return out.toByteArray();
    }

    @Override
    public byte[] createSummarySheet(HttpServletRequest request, HttpServletResponse response, Locale locale,
                                     SpringTemplateEngine templateEngine, Credit credit, LoanOffer loanOffer, Person person, LoanApplication loanApplication,
                                     EntityProductParams params, EntityBranding entityBranding, String signature) throws Exception {

        WebApplicationContext webappContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        final Map<String, Object> mergedModel = new HashMap<String, Object>(30);
        final ConversionService conversionService = (ConversionService) request.getAttribute(ConversionService.class.getName());
        final ThymeleafEvaluationContext evaluationContext = new ThymeleafEvaluationContext(webappContext, conversionService);
        mergedModel.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

        SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), locale, mergedModel, WebApplicationContextUtils.getWebApplicationContext(request.getServletContext()));
        ctx.setVariable("params", params);
        ctx.setVariable("entityBranding", entityBranding);
        ctx.setVariable("credit", credit);
        ctx.setVariable("offer", loanOffer);
        ctx.setVariable("person", person);
        ctx.setVariable("personAddress", personDao.getPersonContactInformation(locale, person.getId()));
        ctx.setVariable("loanApplication", loanApplication);
        ctx.setVariable("signature", signature);

        String htmlContent = templateEngine.process("exportSignaturePdf", ctx);
        htmlContent = htmlContent.replace("$amp;", "&");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();

        renderer.setDocumentFromString(
                StringEscapeUtils.unescapeHtml4(htmlContent),
                Configuration.getClientDomain());
        renderer.layout();
        renderer.createPDF(os);

        byte[] pdfAsBytes = os.toByteArray();
        os.close();
        return pdfAsBytes;
    }

    @Override
    public String getGoogleTagManagerKey(Integer creditId) {
        Integer countryId = null;
        countryId = creditDao.getCountryIdByCreditId(creditId);
        String tagManagerKey = Configuration.getGoogleTagManagerKey(countryId);
        return tagManagerKey;
    }

    @Override
    public void updateCreditConditionsAmountInstallmentsAndTea(int creditId, Double newAmount, Integer newInstallments, Double newTea, Integer entityUserId) throws Exception {
        Credit credit = creditDao.getCreditByID(creditId, Configuration.getDefaultLocale(), false, Credit.class);
        Double oldAmount = credit.getAmount();
        int oldInstallments = credit.getInstallments();
        double oldTea = credit.getEffectiveAnnualRate();

        newAmount = newAmount != null ? newAmount : oldAmount;
        newInstallments = newInstallments != null ? newInstallments : oldInstallments;
        newTea = newTea != null ? newTea : oldTea;

        // Update the data from the form, if its different than the alredy registered
        boolean updateCreditData = false;
        boolean sendDataNotification = false;
        if (newAmount.doubleValue() != oldAmount)
            updateCreditData = true;
        if (newInstallments != oldInstallments)
            updateCreditData = true;
        if (newTea != oldTea)
            updateCreditData = true;

        if (updateCreditData) {
            double differenceInAmount = newAmount - oldAmount;
            differenceInAmount = differenceInAmount < 0 ? differenceInAmount * -1 : differenceInAmount;
            if (differenceInAmount > (oldAmount * 0.15) || differenceInAmount > 500)
                sendDataNotification = true;

            creditDao.updateCreditDataOnDisbursement(creditId, newAmount, newInstallments, newTea, entityUserId);
            credit = creditDao.getCreditByID(creditId, Configuration.getDefaultLocale(), true, Credit.class);
            creditDao.generateOriginalSchedule(credit);
        }

        if (sendDataNotification) {
            String body = ("Monto original\t: " + utilService.doubleMoneyFormat(oldAmount, credit.getCurrency()) + "<br/>") +
                    "Monto Nuevo\t: " + utilService.doubleMoneyFormat(newAmount, credit.getCurrency()) + "<br/><br/>" +
                    "Plazo Original\t: " + oldInstallments + "<br/>" +
                    "Plazo Nuevo\t: " + newInstallments + "<br/><br/>" +
                    "Tasa original\t: " + utilService.percentFormat(oldTea, credit.getCurrency()) + "<br/>" +
                    "Tasa Nueva\t: " + utilService.percentFormat(newTea, credit.getCurrency()) + "<br/>";


            awsSesEmailService.sendEmail(
                    "notificaciones@solven.pe"
                    , Configuration.getCreditAmountModificationMailTo()
                    , null
                    , "Se han modificado las condiciones del crédito " + creditId
                    , null
                    , body
                    , null);
        }

    }

    @Override
    public void originateCreditByEntity(int creditId, int entityId, Date disbursedDate, SpringTemplateEngine templateEngine, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        creditDao.updateCreditStatus(creditId, CreditStatus.ORIGINATED, entityId);
        creditDao.updateCreditSubStatus(creditId, CreditSubStatus.PENDING_ENTIY_DISBURSEMENT);
        creditDao.updateGeneratedInEntity(creditId, null);

        if (disbursedDate != null) {
            creditDao.updateDisbursementDate(creditId, disbursedDate);
            Credit credit = creditDao.getCreditByID(creditId, Configuration.getDefaultLocale(), true, Credit.class);
            creditDao.generateOriginalSchedule(credit);

            if (credit.getEntityProductParameterId().equals(EntityProductParams.ENT_PROD_PARAM_ABACO_CONVENIO)) {
                // For Abaco - Convenio, create the summary sheet and save it
                EntityProductParams params = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
                EntityBranding entityBranding = loanApplication.getEntityId() != null ? catalogService.getEntityBranding(loanApplication.getEntityId()) : null;
                Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), false);
                byte[] summarySheet = creditService.createSummarySheet(request, response, locale, templateEngine, credit,
                        null, person, loanApplication, params, entityBranding, "");
                userService.registerUserFileByte(summarySheet, loanApplication.getId(), person.getUserId(), UserFileType.HOJA_RESUMEN, ".pdf");
            }

            if (credit.getEntity().getId() == Entity.ABACO) {
                // TODO Call abaco WS to updateTrx the schedule

                PersonBankAccountInformation accountInfo = personDao.getPersonBankAccountInformationByCredit(Configuration.getDefaultLocale(), credit.getPersonId(), credit.getId());
                credit.setBank(accountInfo.getBank());
                credit.setBankAccount(accountInfo.getBankAccount());
                credit.setBankAccountType(accountInfo.getBankAccountType());

                Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), false);
                ERptaCredito socioResponse = agreementService.getAsociatedByEmployeeEntity(person.getDocumentType().getId(), person.getDocumentNumber(), credit.getEntity().getId(), credit.getLoanApplicationId());
                ERptaCredito abacoCredit = abacoClient.crearCredito(
                        socioResponse.getIdSocio(),
                        socioResponse.getIdCredito(),
                        socioResponse.getSaldoCredito(),
                        credit,
                        credit.getLoanApplicationId(),
                        credit.getEntityCreditCode() != null ? Integer.parseInt(credit.getEntityCreditCode()) : null);
            }
        }
    }

    @Override
    public void disburseCreditByEntity(int creditId, int entityId, HttpServletRequest request, HttpServletResponse response, SpringTemplateEngine templateEngine, Locale locale) throws Exception {

        Credit credit = creditDao.getCreditByID(creditId, Configuration.getDefaultLocale(), false, Credit.class);
        creditDao.updateCreditStatus(creditId, CreditStatus.ORIGINATED_DISBURSED, entityId);
        creditDao.updateDisbursmentInInEntity(creditId, null);
        if (credit.getEntity().getId() == Entity.ABACO && credit.getProduct().getId() == Product.AGREEMENT) {
            // do nothing
        } else {
            creditDao.updateDisbursementDate(creditId, new Date());
        }

        credit = creditDao.getCreditByID(creditId, Configuration.getDefaultLocale(), false, Credit.class);
        if (credit.getProduct().getId() == Product.AGREEMENT && credit.getEntity().getId() == Entity.ABACO) {
            // If it's Abaco and Agreement, do this because it was an old implementation (SOAP implementation)
            agreementService.confirmDisbursement(creditId, request, response, Configuration.getDefaultLocale(), templateEngine);
        } else {

            PersonContactInformation personContact = personDao.getPersonContactInformation(locale, credit.getPersonId());
            EntityProductParams entityProductParams = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());

            if (entityProductParams.getDisbursementInteractionIds() != null && !entityProductParams.getDisbursementInteractionIds().isEmpty()) {

                for (Integer interactionId : entityProductParams.getDisbursementInteractionIds()) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);

                    PersonInteraction interaction = new PersonInteraction();
                    interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    interaction.setInteractionContent(catalogService.getInteractionContent(interactionId, loanApplication.getCountryId()));
                    interaction.setDestination(personContact.getEmail());
                    interaction.setCreditId(credit.getId());
                    interaction.setPersonId(credit.getPersonId());

                    JSONObject jsonVars = new JSONObject();
                    Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), false);
                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("ENTITY", credit.getEntity().getFullName());
                    jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
                    jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);

                    byte[] contract = creditService.createContract(credit.getId(), request, response, locale, templateEngine, "Contrato-".concat(credit.getEntity().getFullName()).concat(".pdf"), false);
                    PersonInteractionAttachment atachment = new PersonInteractionAttachment();
                    atachment.setBytes(contract);
                    atachment.setFilename("Contrato-".concat(credit.getEntity().getFullName()).concat(".pdf"));
                    interaction.setAttachments(Arrays.asList(atachment));

                    interactionService.sendPersonInteraction(interaction, jsonVars, null);
                }

            } else if (entityProductParams.getDisbursementType().equals(EntityProductParams.DISBURSEMENT_TYPE_DEPOSIT)
                    && entityProductParams.getSignatureType().equals(EntityProductParams.CONTRACT_TYPE_DIGITAL)) {

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);

                PersonInteraction interaction = new PersonInteraction();
                interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.DISBURSEMENT_MAIL_NO_DOCUMENT, loanApplication.getCountryId()));
                interaction.setDestination(personContact.getEmail());
                interaction.setCreditId(credit.getId());
                interaction.setPersonId(credit.getPersonId());

                JSONObject jsonVars = new JSONObject();
                Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), false);
                jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                jsonVars.put("ENTITY", credit.getEntity().getFullName());
                jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
                jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);

                byte[] contract = creditService.createContract(credit.getId(), request, response, locale, templateEngine, "Contrato-".concat(credit.getEntity().getFullName()).concat(".pdf"), false);
                PersonInteractionAttachment atachment = new PersonInteractionAttachment();
                atachment.setBytes(contract);
                atachment.setFilename("Contrato-".concat(credit.getEntity().getFullName()).concat(".pdf"));
                interaction.setAttachments(Arrays.asList(atachment));

                interactionService.sendPersonInteraction(interaction, jsonVars, null);
            }
        }

        loanNotifierService.notifyDisbursement(credit.getLoanApplicationId(), Configuration.getDefaultLocale());
        offlineConversionService.sendOfflineConversion(credit);
    }

    @Override
    public void generateCreditTcea(Credit credit) throws Exception{

        EntityProductParams entityProductParam = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());

        List<Double> paymentsList = new ArrayList<>();
        List<Date> dateList = new ArrayList<>();

        paymentsList.add(((credit.getAmount()) * -1));
        dateList.add(DateUtils.truncate(new Date(), Calendar.DATE));

        List<OriginalSchedule> scheduleForTcea = credit.getOriginalSchedule().stream().sequential().filter(o -> o.getInstallmentId() != 0).collect(Collectors.toList());
        double hidenCommission = (credit.getHiddenCommission() != null ? credit.getHiddenCommission() : 0);
        for (int i = 0; i < scheduleForTcea.size(); i++) {
            double installmentAmount = scheduleForTcea.get(i).getInstallmentAmount();
            if(credit.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_FINANSOL_CONSUMO || credit.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_FINANSOL_CONSUMO_BASE){
                installmentAmount = scheduleForTcea.get(i).getInstallmentAmount() - scheduleForTcea.get(i).getCollectionCommission();
            } else if(credit.getEntity().getId() == Entity.PRISMA){
                installmentAmount = scheduleForTcea.get(i).getInstallmentAmount() - scheduleForTcea.get(i).getCollectionCommission() - hidenCommission;
            }
            if (i == 0) {
                paymentsList.add(installmentAmount + hidenCommission + (credit.getCost() != null ? credit.getCost() : 0));
                dateList.add(scheduleForTcea.get(i).getBillingDate());
            } else {
                paymentsList.add(installmentAmount + hidenCommission);
                dateList.add(scheduleForTcea.get(i).getBillingDate());
            }

        }
        // When Azteca, the first date is the first payment date - 1 month, also the days beetween dates should be 30
//        if(credit.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA){
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(dateList.get(1));
//            cal.add(Calendar.MONTH, -1);
//            dateList.set(0, cal.getTime());
//
//            for (int i = 1; i <= credit.getInstallments(); i++) {
//                cal.setTime(dateList.get(i - 1));
//                cal.add(Calendar.DATE, 30);
//                dateList.set(i, cal.getTime());
//            }
//        }

        // Exclusive validation for salary advance
        // If the first date is the same as the last, make the first date with time in the las second of the day
        if (entityProductParam.getProduct().getId() == Product.SALARY_ADVANCE) {
            Date firstInArray = dateList.get(0);
            Date lastInArray = dateList.get(dateList.size() - 1);
            if (!firstInArray.before(lastInArray) && !firstInArray.after(lastInArray)) {
                Calendar c = Calendar.getInstance();
                c.setTime(firstInArray);
                c.add(Calendar.DATE, 1);
                c.add(Calendar.SECOND, -1);
                dateList.set(0, c.getTime());
            }
        }

        Double[] payments = paymentsList.toArray(new Double[paymentsList.size()]);
        Date[] dates = dateList.toArray(new Date[dateList.size()]);

        // If the entity needs a custom tcea, calculate it
        double tcea = XirrDate.Newtons_method(0.1, payments, dates, credit.getEntity().getAnnualizaXirr());
        double customEntityTcea = tcea;
        if (credit.getEntity().getCustomEffectiveAnnualCostRate() != null && credit.getEntity().getCustomEffectiveAnnualCostRate()) {
            switch (credit.getEntity().getId()) {
                case Entity.RIPLEY:
                    if (!entityProductParam.getId().equals(EntityProductParams.ENT_PROD_PARAM_RIPLEY))
                        break;
                    customEntityTcea = (Math.pow(1 + Irr.irr(ArrayUtils.toPrimitive(payments)), 12) - 1) * 100.0;
                    break;
                case Entity.COMPARTAMOS:
                    customEntityTcea = (Math.pow(1 + Irr.irr(ArrayUtils.toPrimitive(payments)), 12) - 1) * 100.0;
                    break;
            }
        }

        // If its azteca consumo, the tcea should be equal to the tea
//        if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE, EntityProductParams.ENT_PROD_PARAM_AZTECA).contains(credit.getEntityProductParameterId())){
//            tcea = credit.getEffectiveAnnualRate();
//            customEntityTcea = credit.getEffectiveAnnualRate();
//        }

        if (credit.getEffectiveAnnualRate() != null && customEntityTcea < credit.getEffectiveAnnualRate()) {
            loanApplicationService.sendBadTceaAlertEmail(null, credit.getId(), customEntityTcea, credit.getEffectiveAnnualRate());
            customEntityTcea = credit.getEffectiveAnnualRate();
            tcea = credit.getEffectiveAnnualRate();
        }

        credit.setEffectiveAnnualCostRate(customEntityTcea);

        // done in the database
        creditDao.updateTCEA(credit.getId(), customEntityTcea);
        creditDao.updateSolvenTCEA(credit.getId(), tcea);
    }

    @Override
    public double generateCreditTceaWithoutIva(Credit credit) throws Exception{

        EntityProductParams entityProductParam = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());

        List<Double> paymentsList = new ArrayList<>();
        List<Date> dateList = new ArrayList<>();

        if(credit.getStampTax() != null)
            paymentsList.add(((credit.getAmount() - credit.getStampTax()) * -1));
        else
            paymentsList.add(((credit.getAmount()) * -1));

        dateList.add(DateUtils.truncate(new Date(), Calendar.DATE));

        List<OriginalSchedule> scheduleForTcea = credit.getOriginalSchedule().stream().sequential().filter(o -> o.getInstallmentId() != 0).collect(Collectors.toList());
        double hidenCommission = (credit.getHiddenCommission() != null ? credit.getHiddenCommission() : 0);
        for (int i = 0; i < scheduleForTcea.size(); i++) {
            if (i == 0) {
                paymentsList.add(scheduleForTcea.get(i).getInstallmentAmount() - scheduleForTcea.get(i).getInterestTax() + hidenCommission + (credit.getCost() != null ? credit.getCost() : 0));
                dateList.add(scheduleForTcea.get(i).getBillingDate());
            } else {
                paymentsList.add(scheduleForTcea.get(i).getInstallmentAmount() - scheduleForTcea.get(i).getInterestTax() + hidenCommission);
                dateList.add(scheduleForTcea.get(i).getBillingDate());
            }

        }

        // Exclusive validation for salary advance
        // If the first date is the same as the last, make the first date with time in the las second of the day
        if (entityProductParam.getProduct().getId() == Product.SALARY_ADVANCE) {
            Date firstInArray = dateList.get(0);
            Date lastInArray = dateList.get(dateList.size() - 1);
            if (!firstInArray.before(lastInArray) && !firstInArray.after(lastInArray)) {
                Calendar c = Calendar.getInstance();
                c.setTime(firstInArray);
                c.add(Calendar.DATE, 1);
                c.add(Calendar.SECOND, -1);
                dateList.set(0, c.getTime());
            }
        }

        Double[] payments = paymentsList.toArray(new Double[paymentsList.size()]);
        Date[] dates = dateList.toArray(new Date[dateList.size()]);

        // If the entity needs a custom tcea, calculate it
//        double tcea = XirrDate.Newtons_method(0.1, payments, dates, credit.getEntity().getAnnualizaXirr());
        double tcea = XirrDate.Newtons_method(0.1, payments, dates, false);
        double customEntityTcea = tcea;
        if (credit.getEntity().getCustomEffectiveAnnualCostRate() != null && credit.getEntity().getCustomEffectiveAnnualCostRate()) {
            switch (credit.getEntity().getId()) {
                case Entity.RIPLEY:
                    if (!entityProductParam.getId().equals(EntityProductParams.ENT_PROD_PARAM_RIPLEY))
                        break;
                    customEntityTcea = (Math.pow(1 + Irr.irr(ArrayUtils.toPrimitive(payments)), 12) - 1) * 100.0;
                    break;
                case Entity.COMPARTAMOS:
                    customEntityTcea = (Math.pow(1 + Irr.irr(ArrayUtils.toPrimitive(payments)), 12) - 1) * 100.0;
                    break;
            }
        }

        return customEntityTcea;
    }

    private double calculateTotal(double amountParam, int intallmentsParam) {
        BigDecimal amount = new BigDecimal(amountParam);
        BigDecimal installments = new BigDecimal(intallmentsParam);

        return amount.multiply(installments).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}

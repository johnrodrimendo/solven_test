package com.affirm.strategy.contracts;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.WebServiceDAO;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.strategy.ContractStrategy;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.context.MessageSource;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public abstract class BaseContract implements ContractStrategy {

    protected PDDocument pdfDoc;
    protected PDAcroForm acroForm;
    protected CatalogService catalogService;
    protected PersonDAO personDAO;
    protected COSName handWritingFont;
    protected COSName defaultFont;
    protected UtilService utilService;
    protected CreditDAO creditDAO;
    protected LoanApplicationDAO loanApplicationDAO;
    protected WebServiceDAO webServiceDao;
    protected MessageSource messageSource;
    private static final String PDF_FILL_DEFAULT_NULL_VALUE = " ----";

    public BaseContract(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO, WebServiceDAO webServiceDao) throws Exception{
        pdfDoc = PDDocument.load(contract);
        acroForm = pdfDoc.getDocumentCatalog().getAcroForm();
        this.catalogService = catalogService;
        this.personDAO = personDAO;
        this.utilService = utilService;
        this.creditDAO = creditDAO;
        this.loanApplicationDAO = loanApplicationDAO;
        this.webServiceDao = webServiceDao;

        if (pdfDoc != null) {
            this.handWritingFont = prepareFont(pdfDoc, "/PhontPhreak-sHandwriting.ttf");
            this.defaultFont = prepareFont(pdfDoc, "/Calibri.ttf");
        }
    }

    public BaseContract(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO, WebServiceDAO webServiceDao, Object obj, MessageSource messageSource) throws Exception{
        pdfDoc = PDDocument.load(contract);
        acroForm = pdfDoc.getDocumentCatalog().getAcroForm();
        this.catalogService = catalogService;
        this.personDAO = personDAO;
        this.utilService = utilService;
        this.creditDAO = creditDAO;
        this.loanApplicationDAO = loanApplicationDAO;
        this.webServiceDao = webServiceDao;
        this.messageSource = messageSource;

        if (pdfDoc != null) {
            this.handWritingFont = prepareFont(pdfDoc, "/PhontPhreak-sHandwriting.ttf");
            this.defaultFont = prepareFont(pdfDoc, "/Calibri.ttf");
        }
    }

    protected COSName prepareFont(PDDocument _pdfDocument, String path) throws IOException {
        PDDocumentCatalog docCatalog = _pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();

        PDResources res = acroForm.getDefaultResources();

        if (res == null) res = new PDResources();

        InputStream fontStream = getClass().getResourceAsStream(path);
        PDType0Font font = PDType0Font.load(_pdfDocument, fontStream);
        COSName fontName = res.add(font);
        acroForm.setDefaultResources(res);

        return fontName;
    }

    protected void fillCheckBox(String fieldName, boolean value) {
        try {
            if (value)
                ((PDCheckBox) acroForm.getField(fieldName)).check();
            else
                ((PDCheckBox) acroForm.getField(fieldName)).unCheck();
        } catch (Exception ex) {
//            logger.error("Error setting value to pdfBox", ex);
        }
    }

    protected void fillText(String fieldName, Object value) {
        fillText(fieldName, value, null, null);
    }

    protected void fillText(String fieldName, Object value, Integer fontSize) {
        fillText(fieldName, value, null, fontSize);
    }

    protected void fillTextWithColor(String fieldName, Object value, Integer fontSize, String color) {
        fillText(fieldName, value, null, fontSize,color);
    }

    protected void fillText(String fieldName, Object value, Integer fontSize, boolean printEmptyTextOnNull) {
        fillText(fieldName, printEmptyTextOnNull ? value != null ? value + "" : ""/*EMPTY TEXT*/ : value, null, fontSize);
    }

    protected void fillText(String fieldName, Object value, COSName font) {
        fillText(fieldName, value, font, null);
    }

    protected void fillText(String fieldName, Object value, COSName font, Integer fontSize) {
        fillText(fieldName, value, font, fontSize,null);
    }

    protected void fillText(String fieldName, Object value, COSName font, Integer fontSize, String color) {
        try {
            PDField field = acroForm.getField(fieldName);

            COSDictionary dict = field.getCOSObject();
            COSString defaultAppearance = (COSString) dict.getDictionaryObject(COSName.DA);
            if (defaultAppearance != null) {
                String[] daString = defaultAppearance.getASCII().split(" ", 2);
                String[] fontSizeSplit = daString[1].split(" ", 2);
                dict.setString(COSName.DA, String.format("/%s %s %s %s rgb",
                        (font != null ? font.getName() : defaultFont.getName()),
                        (fontSize != null ? fontSize : fontSizeSplit[0]),
                        fontSizeSplit[1],
                        color == null ? "0 0 0" :
                                String.format("%s %s %s",Color.decode(color).getRed(),Color.decode(color).getGreen(),Color.decode(color).getBlue())
                        )); // Force black font color, maybe customize in the future
            }
            ((PDTextField) acroForm.getField(fieldName)).setValue(value != null ? value + "" : PDF_FILL_DEFAULT_NULL_VALUE);
        } catch (Exception ex) {
//            logger.error("Error setting value to pdfBox" + fieldName, ex);
        }
    }

    public byte[] renderPdf(Person person, LoanApplication loanApplication, Credit credit, LoanOffer loanOffer, EntityProductParams params, User user, String signature, Locale locale) throws Exception {
        Person partner = person.getPartner();
        if (partner == null) partner = personDAO.getPerson(catalogService, locale, person.getId(), true).getPartner();

        PersonContactInformation contactInfo = personDAO.getPersonContactInformation(locale, person.getId());
        List<PersonOcupationalInformation> ocupations = personDAO.getPersonOcupationalInformation(locale, person.getId());
        PersonOcupationalInformation principalOcupation = ocupations != null ?
                ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null) : null;
        PersonBankAccountInformation bankInfo = personDAO.getPersonBankAccountInformation(locale, person.getId());
        SunatResult sunatResult = personDAO.getSunatResult(person.getId());
        fillPdf(person, credit, loanOffer, params, user, signature, partner, contactInfo, ocupations, principalOcupation, bankInfo, loanApplication, locale, sunatResult);

        return render();
    }

    protected byte[] render() throws Exception {
        // Flat the pdf (not editable)
        acroForm.flatten();
        // Save it to byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pdfDoc.save(out);
        pdfDoc.close();

        return out.toByteArray();
    }
}

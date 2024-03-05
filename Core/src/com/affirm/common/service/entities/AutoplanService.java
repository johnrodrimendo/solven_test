package com.affirm.common.service.entities;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.transactional.AutoplanCreditActivation;
import com.affirm.common.service.FileService;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service("autoplanService")
public class AutoplanService {

    private static final Logger logger = Logger.getLogger(AutoplanService.class);
    private static final String DEFAULT_LEAD_STATUS = "Envio Lead";

    private static final String SPREAD_SHEET_FILE = "Formato de Activación-Autoplan.xls";

    private final FileService fileService;
    private final CreditDAO creditDAO;

    @Autowired
    public AutoplanService(FileService fileService, CreditDAO creditDAO) {
        this.fileService = fileService;
        this.creditDAO = creditDAO;
    }


    public byte[] createLeadsSpreadSheet() throws Exception {

        List<AutoplanCreditActivation> autoplanCreditActivationList = creditDAO.getAutoplanReports();

        Workbook workbook = null;
        Sheet sheet = null;
        ByteArrayInputStream inputStream = null;
        CellStyle valueStyle = null;
        Font valueStyleFont = null;

        inputStream = new ByteArrayInputStream(fileService.getAssociatedFile(SPREAD_SHEET_FILE));
        workbook = WorkbookFactory.create(inputStream);
        sheet = workbook.getSheetAt(0);
        valueStyle = workbook.createCellStyle();
        valueStyleFont = workbook.createFont();
        valueStyleFont.setFontHeightInPoints((short) 9);
        valueStyle.setFont(valueStyleFont);
        valueStyle.setAlignment(HorizontalAlignment.LEFT);
        valueStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("@"));

        if (null != autoplanCreditActivationList) {
            for (AutoplanCreditActivation autoplan : autoplanCreditActivationList) {
                Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());

                if (IdentityDocumentType.DNI == autoplan.getDocumentId()) {
                    fillSheet(sheet, row, 0, "DNI");
                } else if (IdentityDocumentType.CE == autoplan.getDocumentId()) {
                    fillSheet(sheet, row, 0, "CE");
                }

                fillSheet(sheet, row, 1, autoplan.getDocumentNumber());
                fillSheet(sheet, row, 2, autoplan.getFirstSurname());
                fillSheet(sheet, row, 3, autoplan.getLastSurname());
                fillSheet(sheet, row, 4, autoplan.getName());
                fillSheet(sheet, row, 5, autoplan.getBirthday() != null ? new SimpleDateFormat("dd/MM/yyyy").format(autoplan.getBirthday()) : null);
                fillSheet(sheet, row, 6, autoplan.getPhoneNumber());
                fillSheet(sheet, row, 7, autoplan.getEmail());
                fillSheet(sheet, row, 8, autoplan.getRegisterDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(autoplan.getRegisterDate()) : null);
                fillSheet(sheet, row, 9, DEFAULT_LEAD_STATUS);
                fillSheet(sheet, row, 10, null);
            }
        }

        try {
            inputStream.close();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();
            return outStream.toByteArray();
        } catch (IOException io) {
            logger.error("Error flushing streams in document generation", io);
            return null;
        }
    }

    private static void fillSheet(Sheet sheet, Row row, int column, Object val) {
        row.createCell(column).setCellValue(String.valueOf(null != val ? val : ""));
    }
}

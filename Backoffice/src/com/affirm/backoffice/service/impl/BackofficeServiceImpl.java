package com.affirm.backoffice.service.impl;

import com.affirm.backoffice.dao.BackofficeDAO;
import com.affirm.backoffice.model.*;
import com.affirm.backoffice.service.BackofficeService;
import com.affirm.backoffice.service.ReportsBoService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.model.ReportLoans;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.ProcessQuestionCategory;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jarmando on 27/02/17.
 */
@Service("backofficeService")
public class BackofficeServiceImpl implements BackofficeService {

    @Autowired
    private CatalogService catalogService;

    @Override
    public SysUser getLoggedSysuser() throws Exception {
        return (SysUser) SecurityUtils.getSubject().getPrincipal();
    }

    @Override
    public void setCountryActiveSysuser(Integer countryId, String cssClass) {
        ((SysUser) SecurityUtils.getSubject().getPrincipal()).getCountries().entrySet()
                .stream()
                .filter(e -> e.getKey().equals(countryId))
                .forEach(e -> e.setValue(!cssClass.contains("active")));
    }

    @Override
    public String getCountryActiveSysuser() {
        List<Integer> countriesId = ((SysUser) SecurityUtils.getSubject().getPrincipal()).getActiveCountries();
        return new Gson().toJson(countriesId);
    }

    @Override
    public String getCurrencySymbol() {
        Integer activeCountry = ((SysUser) SecurityUtils.getSubject().getPrincipal()).getActiveCountries().get(0);
        return catalogService.getCountryParam(activeCountry).getCurrency().getSymbol();
    }

    @Override
    public String getSeparator() {
        Integer activeCountry = ((SysUser) SecurityUtils.getSubject().getPrincipal()).getActiveCountries().get(0);
        return catalogService.getCountryParam(activeCountry).getSeparator();
    }

    @Override
    public boolean isCountryUnlocked() {
        return !Configuration.BLOCK_COUNTRY_BO() || (boolean) SecurityUtils.getSubject().getSession().getAttribute("countryUnlocked");
    }

    @Override
    public void createImportImputationOpenMarketExcelTemplate(OutputStream outputStream) throws Exception {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pagos a Imputar");

        // Cell Styles
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 10);
        headStyleFont.setBold(true);
        headStyle.setFont(headStyleFont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 9);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle valueStyle = workbook.createCellStyle();
        Font valueStyleFont = workbook.createFont();
        valueStyleFont.setFontHeightInPoints((short) 9);
        valueStyle.setFont(valueStyleFont);
        valueStyle.setAlignment(HorizontalAlignment.LEFT);
        valueStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("@"));

        // Setting widths
        for (int i = 0; i < 8; i++) {
            sheet.setColumnWidth(i, 35 * 256);
        }

        // Paint head row
        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "Perú (51) - Argentina (54)", sheet, workbook).setCellValue("País*");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "DNI - CE", sheet, workbook).setCellValue("Tipo de Documento*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Nro. Documento*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Código de Crédito*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Código de Depositante*");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "Formato DD/MM/YYYY", sheet, workbook).setCellValue("Fecha de Pago*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Monto de Pago*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Número de Operación del Banco*");

        // Print demo values
        for (int i = 0; i < 2; i++) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "51" : "54");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "DNI" : "DNI");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "12345678" : "123456789");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "C2505031034-01" : "C1405031201-01");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "XXXXX" : "YYYYY");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "10/05/2018" : "20/05/2018");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle, CellType.NUMERIC).setCellValue(i == 0 ? "1500" : "800");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "AAAAA" : "BBBBB");
        }

        workbook.write(outputStream);
        workbook.close();
    }

    @Override
    public void createImportImputationCloseMarketExcelTemplate(OutputStream outputStream) throws Exception {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pagos a Imputar");

        // Cell Styles
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 10);
        headStyleFont.setBold(true);
        headStyle.setFont(headStyleFont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 9);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle valueStyle = workbook.createCellStyle();
        Font valueStyleFont = workbook.createFont();
        valueStyleFont.setFontHeightInPoints((short) 9);
        valueStyle.setFont(valueStyleFont);
        valueStyle.setAlignment(HorizontalAlignment.LEFT);
        valueStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("@"));

        // Setting widths
        for (int i = 0; i < 8; i++) {
            sheet.setColumnWidth(i, 35 * 256);
        }

        // Paint head row
        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "Perú (51) - Argentina (54)", sheet, workbook).setCellValue("País*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("RUC de la Empresa*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Nombre de la Empresa*");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "DNI - CE", sheet, workbook).setCellValue("Tipo de Documento*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Número de Documento*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Código de Crédito*");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "Formato DD/MM/YYYY", sheet, workbook).setCellValue("Fecha de Pago*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Monto de Pago*");

        // Print demo values
        for (int i = 0; i < 2; i++) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "51" : "54");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "20601444764" : "20601554787");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "Solven Funding S.A.C." : "Empresa Ficticia");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "DNI" : "DNI");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "45671564" : "78451547");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "C2505031034-01" : "C1405031201-01");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "10/05/2018" : "20/05/2018");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle, CellType.NUMERIC).setCellValue(i == 0 ? "1500" : "800");
        }

        workbook.write(outputStream);
        workbook.close();
    }

    private Cell createCellWithComment(Row row, int column, CellStyle style, String comment, Sheet sheet, Workbook workbook) {
        Cell cell = row.createCell(column);
        if (style != null)
            cell.setCellStyle(style);

        if (comment != null) {
            Drawing drawing = sheet.createDrawingPatriarch();
            CreationHelper factory = workbook.getCreationHelper();

            ClientAnchor anchor = factory.createClientAnchor();
            anchor.setCol1(cell.getColumnIndex());
            anchor.setCol2(cell.getColumnIndex());
            anchor.setRow1(row.getRowNum());
            anchor.setRow2(row.getRowNum());

            // Create the comment and set the text+author
            Comment commentElement = drawing.createCellComment(anchor);
            RichTextString str = factory.createRichTextString(comment);
            commentElement.setString(str);

            // Assign the comment to the cell
            cell.setCellComment(commentElement);
        }
        return cell;
    }

    private Cell createCell(Row row, int column, CellStyle style) {
        return createCell(row, column, style, null);
    }

    private Cell createCell(Row row, int column, CellStyle style, CellType cellType) {
        Cell cell = row.createCell(column);
        if (style != null)
            cell.setCellStyle(style);
        if (cellType != null)
            cell.setCellType(cellType);
        return cell;
    }
}
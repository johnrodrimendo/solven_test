package com.affirm.backoffice.service.impl;

import com.affirm.backoffice.dao.BackofficeDAO;
import com.affirm.backoffice.model.*;
import com.affirm.backoffice.service.ReportsBoService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.model.ReportLoans;
import com.affirm.common.model.RowGoogleAnalytics;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.GoogleAnalyticsReportingService;
import com.affirm.common.service.UtilService;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.model.*;
import com.google.api.services.analyticsreporting.v4.model.Report;
import org.apache.commons.lang3.tuple.Pair;
import com.affirm.iovation.ws.transaction.check.CheckTransactionDetails;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jarmando on 27/02/17.
 */
@Service
public class ReportsBoServiceImpl implements ReportsBoService {

    @Autowired
    private BackofficeDAO backofficeDAO;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;
    @Autowired
    MessageSource messageSource;

    private HashMap<String, Integer> mapPageSalaryAdvance;
    private HashMap<String, Integer> mapPageSalaryConsumer;

    @Override
    public byte[] createOriginationReportEntityGrouped(String country, String symbol, String separator) throws Exception {

        List<OriginationReportPeriod> periods = backofficeDAO.getOriginationEntityProductReportPeriod(country, catalogService);
        OriginationReportEntityProductPainter report = new OriginationReportEntityProductPainter(periods);
        report.processReport();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Originación reporte");

        // Cell Styles
        CellStyle periodHeadStyle = workbook.createCellStyle();
        periodHeadStyle.setAlignment(HorizontalAlignment.CENTER);
        periodHeadStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
        periodHeadStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font periodHeadStyleFont = workbook.createFont();
        periodHeadStyleFont.setFontHeightInPoints((short) 8);
        periodHeadStyleFont.setBold(true);
        periodHeadStyleFont.setColor(IndexedColors.WHITE.getIndex());
        periodHeadStyle.setFont(periodHeadStyleFont);

        CellStyle reportTypeStyle = workbook.createCellStyle();
        reportTypeStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        reportTypeStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font reportTypeStyleFont = workbook.createFont();
        reportTypeStyleFont.setFontHeightInPoints((short) 8);
        reportTypeStyleFont.setBold(true);
        reportTypeStyleFont.setColor(IndexedColors.WHITE.getIndex());
        reportTypeStyle.setFont(reportTypeStyleFont);

        CellStyle entityNameStyle = workbook.createCellStyle();
        Font entityNameStyleFont = workbook.createFont();
        entityNameStyleFont.setFontHeightInPoints((short) 8);
        entityNameStyleFont.setBold(true);
        entityNameStyle.setFont(entityNameStyleFont);

        CellStyle originationTypeNameStyle = workbook.createCellStyle();
        Font originationTypeNameStylefont = workbook.createFont();
        originationTypeNameStylefont.setFontHeightInPoints((short) 8);
        originationTypeNameStylefont.setBold(true);
        originationTypeNameStyle.setFont(originationTypeNameStylefont);

        CellStyle productNameStyle = workbook.createCellStyle();
        Font productNameStylefont = workbook.createFont();
        productNameStylefont.setFontHeightInPoints((short) 8);
        productNameStyle.setFont(productNameStylefont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 8);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle loanCapitalStyle = workbook.createCellStyle();
        Font loanCapitalFont = workbook.createFont();
        loanCapitalFont.setFontHeightInPoints((short) 8);
        loanCapitalStyle.setFont(loanCapitalFont);
        loanCapitalStyle.setAlignment(HorizontalAlignment.RIGHT);
        loanCapitalStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00")); // Money format

        CellStyle commissionStyle = workbook.createCellStyle();
        Font commissionFont = workbook.createFont();
        commissionFont.setFontHeightInPoints((short) 8);
        commissionStyle.setFont(commissionFont);
        commissionStyle.setAlignment(HorizontalAlignment.RIGHT);
        commissionStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00")); // Money format

        // Setting widths
        sheet.setColumnWidth(0, 40 * 256);
        for (int i = 0; i < periods.size(); i++) {
            sheet.setColumnWidth(i + 1, 10 * 256);
        }

        // Paint head period rows
        Row headRow = sheet.createRow(0);
        headRow.createCell(0).setCellValue("");
        for (OriginationReportPeriod period : periods) {
            Cell periodCell = headRow.createCell(headRow.getPhysicalNumberOfCells());
            periodCell.setCellValue(period.getPeriod());
            periodCell.setCellStyle(periodHeadStyle);
        }

        // Paint originated quantity report
        Row originationQuantiyReportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        Cell originationQuantiyReportCell = originationQuantiyReportRow.createCell(0);
        originationQuantiyReportCell.setCellValue("Originación (#)");
        originationQuantiyReportCell.setCellStyle(reportTypeStyle);
        sheet.addMergedRegion(new CellRangeAddress(originationQuantiyReportRow.getRowNum(), originationQuantiyReportRow.getRowNum(), 0, periods.size()));
        for (OriginationReportEntityGroup entityGroup : report.getEntities()) {
            paintOriginationReportEntityGroup("quantity", entityGroup, periods, sheet, entityNameStyle, quantityStyle, originationTypeNameStyle, productNameStyle);
        }
        sheet.createRow(sheet.getPhysicalNumberOfRows());
        paintOriginationReportEntityGroup("quantity", report.getTotalEntities(), periods, sheet, entityNameStyle, quantityStyle, originationTypeNameStyle, productNameStyle);
        sheet.createRow(sheet.getPhysicalNumberOfRows());
        paintOriginationReportOriginationTypeGroup("quantity", report.getTotalOriginations(), periods, sheet, quantityStyle, originationTypeNameStyle, productNameStyle, "", "    ");
        sheet.createRow(sheet.getPhysicalNumberOfRows());

        // Paint originated loan capital report
        Row originationLoanCapitalReportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        Cell originationLoanCapitalReportCell = originationLoanCapitalReportRow.createCell(0);
        originationLoanCapitalReportCell.setCellValue("Originación ".concat(symbol));
        originationLoanCapitalReportCell.setCellStyle(reportTypeStyle);
        sheet.addMergedRegion(new CellRangeAddress(originationLoanCapitalReportRow.getRowNum(), originationLoanCapitalReportRow.getRowNum(), 0, periods.size()));
        for (OriginationReportEntityGroup entityGroup : report.getEntities()) {
            paintOriginationReportEntityGroup("loanCapital", entityGroup, periods, sheet, entityNameStyle, loanCapitalStyle, originationTypeNameStyle, productNameStyle);
        }
        sheet.createRow(sheet.getPhysicalNumberOfRows());
        paintOriginationReportEntityGroup("loanCapital", report.getTotalEntities(), periods, sheet, entityNameStyle, loanCapitalStyle, originationTypeNameStyle, productNameStyle);
        sheet.createRow(sheet.getPhysicalNumberOfRows());
        paintOriginationReportOriginationTypeGroup("loanCapital", report.getTotalOriginations(), periods, sheet, loanCapitalStyle, originationTypeNameStyle, productNameStyle, "", "    ");
        sheet.createRow(sheet.getPhysicalNumberOfRows());

        // Paint originated entity commission report
        Row originationEntityCommissionReportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        Cell originationEntityCommissionReportCell = originationEntityCommissionReportRow.createCell(0);
        originationEntityCommissionReportCell.setCellValue("Comisión ".concat(symbol));
        originationEntityCommissionReportCell.setCellStyle(reportTypeStyle);
        sheet.addMergedRegion(new CellRangeAddress(originationEntityCommissionReportRow.getRowNum(), originationEntityCommissionReportRow.getRowNum(), 0, periods.size()));
        for (OriginationReportEntityGroup entityGroup : report.getEntities()) {
            paintOriginationReportEntityGroup("entityCommission", entityGroup, periods, sheet, entityNameStyle, commissionStyle, originationTypeNameStyle, productNameStyle);
        }
        sheet.createRow(sheet.getPhysicalNumberOfRows());
        paintOriginationReportEntityGroup("entityCommission", report.getTotalEntities(), periods, sheet, entityNameStyle, commissionStyle, originationTypeNameStyle, productNameStyle);
        sheet.createRow(sheet.getPhysicalNumberOfRows());
        paintOriginationReportOriginationTypeGroup("entityCommission", report.getTotalOriginations(), periods, sheet, commissionStyle, originationTypeNameStyle, productNameStyle, "", "    ");


        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    private void paintOriginationReportEntityGroup(
            String fieldToPaint,
            OriginationReportEntityGroup entityGroup,
            List<OriginationReportPeriod> periods,
            Sheet sheet, CellStyle entityNameStyle,
            CellStyle quantityStyle, CellStyle originationTypeNameStyle,
            CellStyle productNameStyle) {

        Row totalEntityRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        Cell entityNameCell = totalEntityRow.createCell(totalEntityRow.getPhysicalNumberOfCells());
        entityNameCell.setCellValue(entityGroup.getEntityName());
        entityNameCell.setCellStyle(entityNameStyle);
        for (OriginationReportPeriod period : periods) {
            Cell valueCell = totalEntityRow.createCell(totalEntityRow.getPhysicalNumberOfCells());
            if (fieldToPaint.equalsIgnoreCase("quantity"))
                valueCell.setCellValue(entityGroup.getTotalDetailQuantityByPeriod(period.getPeriod()));
            else if (fieldToPaint.equalsIgnoreCase("loanCapital"))
                valueCell.setCellValue(entityGroup.getTotalDetailLoanCapitalByPeriod(period.getPeriod()));
            else if (fieldToPaint.equalsIgnoreCase("entityCommission"))
                valueCell.setCellValue(entityGroup.getTotalDetailEntityCommissionByPeriod(period.getPeriod()));
            valueCell.setCellStyle(quantityStyle);
        }

        //entity origination types rows
        for (OriginationReportOriginationTypeGroup originationTypeGroup : entityGroup.getOriginationTypes()) {
            paintOriginationReportOriginationTypeGroup(fieldToPaint, originationTypeGroup, periods, sheet, quantityStyle, originationTypeNameStyle, productNameStyle, "    ", "        ");
        }
    }

    private void paintOriginationReportOriginationTypeGroup(
            String fieldToPaint,
            OriginationReportOriginationTypeGroup originationTypeGroup,
            List<OriginationReportPeriod> periods,
            Sheet sheet, CellStyle quantityStyle,
            CellStyle originationTypeNameStyle,
            CellStyle productNameStyle, String originationTypeNamePrepend, String productNamePrepend) {

        Row originationTypeRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        Cell originationTypeCell = originationTypeRow.createCell(originationTypeRow.getPhysicalNumberOfCells());
        originationTypeCell.setCellValue(originationTypeNamePrepend + originationTypeGroup.getOriginationName());
        originationTypeCell.setCellStyle(originationTypeNameStyle);
        for (OriginationReportPeriod period : periods) {
            Cell valueCell = originationTypeRow.createCell(originationTypeRow.getPhysicalNumberOfCells());
            if (fieldToPaint.equalsIgnoreCase("quantity"))
                valueCell.setCellValue(originationTypeGroup.getTotalDetailQuantityByPeriod(period.getPeriod()));
            else if (fieldToPaint.equalsIgnoreCase("loanCapital"))
                valueCell.setCellValue(originationTypeGroup.getTotalDetailLoanCapitalByPeriod(period.getPeriod()));
            else if (fieldToPaint.equalsIgnoreCase("entityCommission"))
                valueCell.setCellValue(originationTypeGroup.getTotalDetailEntityCommissionByPeriod(period.getPeriod()));
            valueCell.setCellStyle(quantityStyle);
        }

        //entity product rows
        for (OriginationReportProductGroup productGroup : originationTypeGroup.getProducts()) {
            Row productEntityRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            Cell productNameCell = productEntityRow.createCell(productEntityRow.getPhysicalNumberOfCells());
            productNameCell.setCellValue(productNamePrepend + productGroup.getProduct().getName());
            productNameCell.setCellStyle(productNameStyle);
            for (OriginationReportPeriod period : periods) {
                Cell valueCell = productEntityRow.createCell(productEntityRow.getPhysicalNumberOfCells());
                if (fieldToPaint.equalsIgnoreCase("quantity"))
                    valueCell.setCellValue(productGroup.getDetailByPeriod(period.getPeriod()).getQuantity());
                else if (fieldToPaint.equalsIgnoreCase("loanCapital"))
                    valueCell.setCellValue(productGroup.getDetailByPeriod(period.getPeriod()).getLoanCapital());
                else if (fieldToPaint.equalsIgnoreCase("entityCommission"))
                    valueCell.setCellValue(productGroup.getDetailByPeriod(period.getPeriod()).getEntityCommission());
                valueCell.setCellStyle(quantityStyle);
            }
        }
    }

    private Cell createCell(Row row, int column, CellStyle style) {
        Cell cell = row.createCell(column);
        if (style != null)
            cell.setCellStyle(style);
        return cell;
    }

    private void validateRow() {

    }
}
package com.affirm.common.service.impl;

import com.affirm.acceso.model.Direccion;
import com.affirm.bancodelsol.model.DisburseCreditReport;
import com.affirm.bancodelsol.model.DisbursedCreditReport;
import com.affirm.bancodelsol.model.LoanApplicationInProcessReport;
import com.affirm.bancodelsol.service.BancoDelSolService;
import com.affirm.common.dao.*;
import com.affirm.common.model.*;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.Report;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.question.Question119Service;
import com.affirm.common.service.question.Question149Service;
import com.affirm.common.util.CSVutils;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.model.*;
import com.google.gson.Gson;
import jxl.CellView;
import jxl.format.Alignment;
import jxl.write.Number;
import jxl.write.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.lambda.tuple.Tuple3;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Boolean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.poi.util.LocaleUtil.setUserTimeZone;

@Service("reportsService")
public class ReportsServiceImpl implements ReportsService {

    private static Logger logger = Logger.getLogger(ReportsServiceImpl.class);

    private static final String RIPLEY_REPORT_SEF_ABONO_EXCEL_URL = "RIPLEY_MODELO_DE_REPORTE_SEF_ABONO.XLSX";

    private static final String APPROVE_CONSOLIDATION_CREDITS_REPORT = "Consolidado_para_Aprobacion_de_creditos.xls";

    private static final SimpleDateFormat CUSTOM_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static final int MAX_HARD_FILTERS_AND_POLICIES = 25;

    private static final String CREDITO_DE_CONSUMO = "Crédito de Consumo";

    private static final String CONSUMOS_VARIABLES = "Consumos variables";

    private static final String ADELANTO_DE_SUELDO = "Adelanto de sueldo";

    private static final String EMPTY = "";

    public static final String FUNNEL_REPORT_DATETYPE_1 = "Por fecha de inicio de solicitud";

    public static final String FUNNEL_REPORT_DATETYPE_2 = "Por fecha de cambio de estado";

    public static final String INTERNAL_STATUS_DEFAULT = "Doc. por enviar";

    @Autowired
    private ReportsDAO reportsDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private FileService fileService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private SysUserDAO sysuserDao;
    @Autowired
    private GoogleAnalyticsReportingService googleAnalyticsReportingService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private TokyService tokyService;
    @Autowired
    private BancoDelSolService bancoDelSolService;
    @Autowired
    private Question119Service question119Service;
    @Autowired
    private Question149Service question149Service;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private BrandingService brandingService;
    @Autowired
    private CatalogDAO catalogDAO;
    @Autowired
    private ErrorService errorService;

    private GetReportsResponse responseGoogleAnalytics;
    private HashMap<String, Double> totalGAResults;

    @Override
    public byte[] createRipleyReportExcel() throws Exception {
        // Get the data to paint
        List<RipleySefReport> reports = reportsDao.getRipleyReport(1);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileService.getAssociatedFile(RIPLEY_REPORT_SEF_ABONO_EXCEL_URL));
        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(0);

        int fileCount = 4;
        Row row = sheet.getRow(fileCount);

        if (reports == null) {
            row.getCell(0).setCellValue("No existe registros a mostrar");
        } else {
            for (RipleySefReport report : reports) {
                row.getCell(0).setCellValue(utilService.dateFormat(report.getOriginated()));
                if (report.getDocNumber() != null)
                    row.getCell(1).setCellValue(report.getDocNumber().toUpperCase());
                if (report.getFullName() != null)
                    row.getCell(2).setCellValue(report.getFullName().toUpperCase());
                if (report.getCardType() != null)
                    row.getCell(3).setCellValue(report.getCardType().toUpperCase());
                if (report.getContract() != null)
                    row.getCell(4).setCellValue(report.getContract());
                if (report.getCardNumber() != null)
                    row.getCell(5).setCellValue(report.getCardNumber().toUpperCase());
                row.getCell(6).setCellValue(report.getLoanCapital());
                row.getCell(7).setCellValue(report.getInstallments());
                String rate = utilService.doubleFormat(report.getEffectiveMonthlyRate());
                row.getCell(8).setCellValue(rate);
                row.getCell(9).setCellValue(report.getInsurance() ? "SI" : "NO");
                if (report.getPhoneNumber() != null)
                    row.getCell(10).setCellValue(report.getPhoneNumber().toUpperCase());
                row.getCell(11).setCellValue("ZLOPEZ");
                row.getCell(13).setCellValue("SOLVEN");
                if (report.getBank() != null)
                    row.getCell(15).setCellValue(report.getBank().toUpperCase());
                if (report.getBankAccount() != null)
                    row.getCell(16).setCellValue(report.getBankAccount().toUpperCase());
                if (report.getCciCode() != null)
                    row.getCell(17).setCellValue(report.getCciCode().toUpperCase());
                row.getCell(19).setCellValue("VIRTUAL");
                if (report.getStreetName() != null)
                    row.getCell(20).setCellValue(report.getStreetName().toUpperCase());
                if (report.getUbigeo() != null) {
                    if (report.getUbigeo().getDistrict() != null && report.getUbigeo().getDistrict().getName() != null)
                        row.getCell(21).setCellValue(report.getUbigeo().getDistrict().getName().toUpperCase());
                    if (report.getUbigeo().getProvince() != null && report.getUbigeo().getProvince().getName() != null)
                        row.getCell(22).setCellValue(report.getUbigeo().getProvince().getName().toUpperCase());
                    if (report.getUbigeo().getDepartment() != null && report.getUbigeo().getDepartment().getName() != null)
                        row.getCell(23).setCellValue(report.getUbigeo().getDepartment().getName().toUpperCase());
                }

                fileCount++;
                row = sheet.getRow(fileCount);
            }

        }
        inputStream.close();

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] endMonthCompanyResume(int employerOrGroupId, boolean isGroup) throws Exception {

        Workbook workbook = new XSSFWorkbook();

        // Cell Styles
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 8);
        headStyleFont.setBold(true);
        headStyleFont.setColor(IndexedColors.WHITE.getIndex());
        headStyle.setFont(headStyleFont);

        CellStyle textStyle = workbook.createCellStyle();
        Font textStyleFont = workbook.createFont();
        textStyleFont.setFontHeightInPoints((short) 8);
        textStyle.setFont(textStyleFont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 8);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle moneyStyle = workbook.createCellStyle();
        Font moneyStyleFont = workbook.createFont();
        moneyStyleFont.setFontHeightInPoints((short) 8);
        moneyStyle.setFont(moneyStyleFont);
        moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
        moneyStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00")); // Money format

        CellStyle moneyStyleBold = workbook.createCellStyle();
        Font moneyStyleBoldFont = workbook.createFont();
        moneyStyleBoldFont.setBold(true);
        moneyStyleBoldFont.setFontHeightInPoints((short) 8);
        moneyStyleBold.setFont(moneyStyleBoldFont);
        moneyStyleBold.setAlignment(HorizontalAlignment.RIGHT);
        moneyStyleBold.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00")); // Money format

        CellStyle totalRowStyle = workbook.createCellStyle();
        totalRowStyle.setAlignment(HorizontalAlignment.CENTER);
        totalRowStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        totalRowStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font totalRowStyleFont = workbook.createFont();
        totalRowStyleFont.setFontHeightInPoints((short) 8);
        totalRowStyleFont.setBold(true);
        totalRowStyleFont.setColor(IndexedColors.WHITE.getIndex());
        totalRowStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalRowStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00")); // Money format
        totalRowStyle.setFont(totalRowStyleFont);

        CellStyle totalRowLightStyle = workbook.createCellStyle();
        totalRowLightStyle.setAlignment(HorizontalAlignment.CENTER);
        totalRowLightStyle.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        totalRowLightStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font totalRowLightStyleFont = workbook.createFont();
        totalRowLightStyleFont.setFontHeightInPoints((short) 8);
        totalRowLightStyleFont.setBold(true);
        totalRowLightStyleFont.setColor(IndexedColors.WHITE.getIndex());
        totalRowLightStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalRowLightStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00")); // Money format
        totalRowLightStyle.setFont(totalRowLightStyleFont);


        // Resumen sheet
        {
            Sheet sheet = workbook.createSheet("Resumen");
            Pair<List<EmployerCreditsGatewayReport>, List<EmployerCreditsGatewayReport>> reportResume = reportsDao.getEmployerCreditCollectinReport(employerOrGroupId, isGroup);

            // Setting widths
            sheet.setColumnWidth(0, 25 * 256);
            sheet.setColumnWidth(1, 15 * 256);
            sheet.setColumnWidth(2, 15 * 256);
            sheet.setColumnWidth(3, 15 * 256);
            sheet.setColumnWidth(4, 15 * 256);

            // Paint head rows - adelanto
            Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            headRow.setHeightInPoints(22);
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Empresa");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Monto Adelanto");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Comisiones con IGV");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Comisión sin IGV");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Monto Adelanto \n Total (inc. IGV) S/");

            for (EmployerCreditsGatewayReport report : reportResume.getLeft()) {
                Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getEmployer().getName());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(report.getAmount());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(report.getLoanCommission());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(report.getLoanCommissionWithoutIgv());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(report.getTotalAmount());
            }
            Row totalRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowStyle).setCellValue("Total");
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowStyle).setCellValue(reportResume.getLeft().stream().mapToDouble(r -> r.getAmount()).sum());
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowStyle).setCellValue(reportResume.getLeft().stream().mapToDouble(r -> r.getLoanCommission()).sum());
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowStyle).setCellValue(reportResume.getLeft().stream().mapToDouble(r -> r.getLoanCommissionWithoutIgv()).sum());
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowStyle).setCellValue(reportResume.getLeft().stream().mapToDouble(r -> r.getTotalAmount()).sum());

            totalRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowLightStyle).setCellValue("Total Desembolsado");
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowLightStyle).setCellValue("");
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowLightStyle).setCellValue("");
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowLightStyle).setCellValue("");
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowLightStyle).setCellValue(reportResume.getLeft().stream().mapToDouble(r -> r.getTotalAmount()).sum());

            // Paint head rows - otros
            sheet.createRow(sheet.getPhysicalNumberOfRows());
            sheet.createRow(sheet.getPhysicalNumberOfRows());
            headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            headRow.setHeightInPoints(22);
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Empresa");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Cuota Crédito\n(inc. IGV)");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("IGV");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Cuota sin IGV");

            for (EmployerCreditsGatewayReport report : reportResume.getRight()) {
                Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getEmployer().getName());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(report.getPendingInstallmentAmount());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(report.getIgv());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(report.getPendingInstallmentAmountWithoutIgv());
            }
            totalRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowStyle).setCellValue("Total");
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowStyle).setCellValue(reportResume.getRight().stream().mapToDouble(r -> r.getPendingInstallmentAmount()).sum());
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowStyle).setCellValue(reportResume.getRight().stream().mapToDouble(r -> r.getIgv()).sum());
            createCell(totalRow, totalRow.getPhysicalNumberOfCells(), totalRowStyle).setCellValue(reportResume.getRight().stream().mapToDouble(r -> r.getPendingInstallmentAmountWithoutIgv()).sum());
        }


        // Descuento Planilla sheet
        {
            Sheet sheet = workbook.createSheet("Descuento Planilla");
            List<EmployerCreditsGatewayDetailReport> reports = reportsDao.getEmployerCreditCollectinDetailReport(employerOrGroupId, isGroup);

            // Setting widths
            sheet.setColumnWidth(0, 20 * 256);
            sheet.setColumnWidth(1, 20 * 256);
            sheet.setColumnWidth(2, 20 * 256);
            sheet.setColumnWidth(3, 20 * 256);
            sheet.setColumnWidth(4, 15 * 256);
            sheet.setColumnWidth(5, 15 * 256);
            sheet.setColumnWidth(6, 15 * 256);
            sheet.setColumnWidth(7, 20 * 256);
            sheet.setColumnWidth(8, 15 * 256);
            sheet.setColumnWidth(9, 20 * 256);

            // Paint head rows
            Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            headRow.setHeightInPoints(22);
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Periodo");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Tipo de Documento");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Número de Documento");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Nombre");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("# Creditos / Cuotas");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Monto Adelanto \n inc comision");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Monto Crédito");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Monto Descuento \n Planilla");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Producto");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Empresa (Convenio)");

            // Paint data rows
            if (reports != null)
                for (EmployerCreditsGatewayDetailReport report : reports) {
                    Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue("" + utilService.dateCustomFormat(report.getPeriod(), "MM / yyyy", Configuration.getDefaultLocale()));
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getDocumentType().getName());
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getDocumentNumber());
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getFullName());
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getCredits());
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(report.getSalaryAdvanceAmount());
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(report.getCreditAmount());
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(report.getDiscountAmount());
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getProductIds().stream().map(p -> p.getName()).collect(Collectors.joining(" / ")));
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getEmployerIds().stream().map(p -> p.getName()).collect(Collectors.joining(" / ")));
                }
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createProsegurPendingDisbursementConsolidationReport(Locale locale) throws Exception {

        List<PendingDisbursementConsolidationReportDetail> reports = reportsDao.getPendingDisbursementConsolidationReport();
        if (reports == null)
            reports = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Serv Regular");

        // Cell Styles
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headStyle.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 8);
        headStyleFont.setBold(true);
        headStyleFont.setColor(IndexedColors.BLACK.getIndex());
        headStyle.setFont(headStyleFont);

        CellStyle textStyle = workbook.createCellStyle();
        Font textStyleFont = workbook.createFont();
        textStyleFont.setFontHeightInPoints((short) 8);
        textStyle.setFont(textStyleFont);

        CellStyle resumeStyle = workbook.createCellStyle();
        Font resumeStyleFont = workbook.createFont();
        resumeStyleFont.setBold(true);
        resumeStyleFont.setFontHeightInPoints((short) 8);
        resumeStyle.setFont(resumeStyleFont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 8);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle moneyStyle = workbook.createCellStyle();
        Font moneyStyleFont = workbook.createFont();
        moneyStyleFont.setFontHeightInPoints((short) 8);
        moneyStyle.setFont(moneyStyleFont);
        moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
        moneyStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00")); // Money format

        // Setting widths
        sheet.setColumnWidth(0, 5 * 256);
        sheet.setColumnWidth(1, 5 * 256);
        sheet.setColumnWidth(2, 25 * 256);
        sheet.setColumnWidth(3, 20 * 256);
        sheet.setColumnWidth(4, 30 * 256);
        sheet.setColumnWidth(5, 30 * 256);
        sheet.setColumnWidth(6, 30 * 256);
        sheet.setColumnWidth(7, 10 * 256);
        sheet.setColumnWidth(8, 10 * 256);
        sheet.setColumnWidth(9, 10 * 256);
        sheet.setColumnWidth(10, 15 * 256);
        sheet.setColumnWidth(11, 15 * 256);
        sheet.setColumnWidth(12, 20 * 256);

        // Paint resume section
        sheet.createRow(sheet.getPhysicalNumberOfRows());
        sheet.createRow(sheet.getPhysicalNumberOfRows());
        sheet.createRow(sheet.getPhysicalNumberOfRows());

        Row resumeRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(resumeRow, 2, resumeStyle).setCellValue("Lima,");
        createCell(resumeRow, 4, resumeStyle).setCellValue(utilService.dateCustomFormat(new Date(), "dd 'de' MMMM 'de' yyyy", Configuration.getDefaultLocale()));
        sheet.createRow(sheet.getPhysicalNumberOfRows());

        resumeRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(resumeRow, 2, resumeStyle).setCellValue("Señores");

        resumeRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(resumeRow, 2, resumeStyle).setCellValue("CIA. DE SEGURIDAD PROSEGUR S.A");

        resumeRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(resumeRow, 2, resumeStyle).setCellValue("Presente.");

        sheet.createRow(sheet.getPhysicalNumberOfRows());
        sheet.createRow(sheet.getPhysicalNumberOfRows());
        resumeRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(resumeRow, 2, resumeStyle).setCellValue("Estimados Señores:");

        sheet.createRow(sheet.getPhysicalNumberOfRows());
        resumeRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(resumeRow, 2, resumeStyle).setCellValue("Solicitamos a Uds. Se sirvan realizar los siguientes pagos de tarjetas de créditos y préstamos personales al día siguiente útil de enviado el cuadro de la referencia, por lo cual autorizamos retirar de nuestra bóveda la cantidad de:");
        sheet.addMergedRegion(new CellRangeAddress(resumeRow.getRowNum(), resumeRow.getRowNum(), 2, 12));

        sheet.createRow(sheet.getPhysicalNumberOfRows());
        sheet.createRow(sheet.getPhysicalNumberOfRows());
        resumeRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(resumeRow, 2, resumeStyle).setCellValue("NUEVOS SOLES:");
        createCell(resumeRow, 5, resumeStyle).setCellValue(utilService.doubleMoneyFormat(reports.stream().mapToDouble(r -> r.getBalance()).sum(), Currency.PEN_SYMBOL));

        resumeRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(resumeRow, 2, resumeStyle).setCellValue("DOLARES AMERICANOS:");
        createCell(resumeRow, 5, resumeStyle).setCellValue("$0.00");

        sheet.createRow(sheet.getPhysicalNumberOfRows());
        sheet.createRow(sheet.getPhysicalNumberOfRows());

        // Paint head row
        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        headRow.setHeightInPoints(30);
        headRow.createCell(headRow.getPhysicalNumberOfCells());
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Nro");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("FECHA");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("DNI");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("APELLIDOS Y NOMBRE");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("INSTITUCION FINANCIERA");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("NRO DE TARJETA/CREDITO");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("TIPO");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("TIT");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("ADIC");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("MONTO A PAGAR\n S/");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("MONTO A PAGAR\n US$");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("TIPO DE SERVICIO");

        // Paint data rows
        int counter = 1;
        for (PendingDisbursementConsolidationReportDetail report : reports) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            reportRow.createCell(reportRow.getPhysicalNumberOfCells());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(counter);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateCustomFormat(new Date(), "dd/MM/yyyy", Configuration.getDefaultLocale()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getDocumentNumber());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getFullName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getRccEntity() != null ? report.getRccEntity().getFullName() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getCardNumber());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getConsolidationTypeShort());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue("SI");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue("NO");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(report.getBalance());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue("");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getConsolidationTypeLong());
            counter++;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createOriginationReport(Date startDate, Date endDate, String country, String symbol, boolean internationalCurrency) throws Exception {
        List<ReportOrigination> result = creditDao.getOriginationReport(country, catalogService, Configuration.getDefaultLocale(), startDate, endDate);

        Locale locale = Configuration.getDefaultLocale();

        WritableWorkbook paysheetBook = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();


        try {

            paysheetBook = jxl.Workbook.createWorkbook(outStream);

            WritableSheet excelSheet = paysheetBook.createSheet("Reporte de Originación", 0);


            // Format for Head

            // Fonts
            WritableFont headFontFormat = new WritableFont(WritableFont.ARIAL, 12);
            headFontFormat.setBoldStyle(WritableFont.BOLD);

            WritableFont bodyFontFormat = new WritableFont(WritableFont.ARIAL, 11);


            // Cells
            WritableCellFormat headCellFormat = new WritableCellFormat(headFontFormat);
            headCellFormat.setWrap(true);
            headCellFormat.setAlignment(Alignment.CENTRE);

            WritableCellFormat leftCellFormat = new WritableCellFormat(bodyFontFormat);
            leftCellFormat.setAlignment(Alignment.LEFT);

            WritableCellFormat rightCellFormat = new WritableCellFormat(bodyFontFormat);
            rightCellFormat.setAlignment(Alignment.RIGHT);

            WritableCellFormat centerCellFormat = new WritableCellFormat(bodyFontFormat);
            centerCellFormat.setAlignment(Alignment.CENTRE);

            CellView headCellView = new CellView();
            headCellView.setAutosize(true);

            // Add labels to sheet
            Label label = new Label(0, 0, "#", headCellFormat);
            excelSheet.setColumnView(0, headCellView);
            excelSheet.addCell(label);

            label = new Label(1, 0, "País", headCellFormat);
            excelSheet.setColumnView(1, headCellView);
            excelSheet.addCell(label);

            label = new Label(2, 0, "Fecha de Originación", headCellFormat);
            excelSheet.setColumnView(2, headCellView);
            excelSheet.addCell(label);

            label = new Label(3, 0, "Loan Application", headCellFormat);
            excelSheet.setColumnView(3, headCellView);
            excelSheet.addCell(label);

            label = new Label(4, 0, "Crédito", headCellFormat);
            excelSheet.setColumnView(4, headCellView);
            excelSheet.addCell(label);

            label = new Label(5, 0, "Person Id", headCellFormat);
            excelSheet.setColumnView(5, headCellView);
            excelSheet.addCell(label);

            label = new Label(6, 0, "Tipo de Documento", headCellFormat);
            excelSheet.setColumnView(6, headCellView);
            excelSheet.addCell(label);

            label = new Label(7, 0, "Número de Documento", headCellFormat);
            excelSheet.setColumnView(7, headCellView);
            excelSheet.addCell(label);

            label = new Label(8, 0, "R.U.C.", headCellFormat);
            excelSheet.setColumnView(8, headCellView);
            excelSheet.addCell(label);

            label = new Label(9, 0, "Apellidos", headCellFormat);
            excelSheet.setColumnView(9, headCellView);
            excelSheet.addCell(label);

            label = new Label(10, 0, "Nombres", headCellFormat);
            excelSheet.setColumnView(10, headCellView);
            excelSheet.addCell(label);

            label = new Label(11, 0, "Nacionalidad", headCellFormat);
            excelSheet.setColumnView(11, headCellView);
            excelSheet.addCell(label);

            label = new Label(12, 0, "Monto (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(12, headCellView);
            excelSheet.addCell(label);

            label = new Label(13, 0, "Plazo", headCellFormat);
            excelSheet.setColumnView(13, headCellView);
            excelSheet.addCell(label);

            label = new Label(14, 0, "TEA", headCellFormat);
            excelSheet.setColumnView(14, headCellView);
            excelSheet.addCell(label);

            label = new Label(15, 0, "TCEA", headCellFormat);
            excelSheet.setColumnView(15, headCellView);
            excelSheet.addCell(label);

            label = new Label(16, 0, "TNA", headCellFormat);
            excelSheet.setColumnView(16, headCellView);
            excelSheet.addCell(label);

            label = new Label(17, 0, "Cuota Promedio (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(17, headCellView);
            excelSheet.addCell(label);

            label = new Label(18, 0, "Producto", headCellFormat);
            excelSheet.setColumnView(18, headCellView);
            excelSheet.addCell(label);

            label = new Label(19, 0, "Financiador", headCellFormat);
            excelSheet.setColumnView(19, headCellView);
            excelSheet.addCell(label);

            label = new Label(20, 0, "Empresa (Convenio)", headCellFormat);
            excelSheet.setColumnView(20, headCellView);
            excelSheet.addCell(label);

            label = new Label(21, 0, "Comisión de Entidad", headCellFormat);
            excelSheet.setColumnView(21, headCellView);
            excelSheet.addCell(label);

            label = new Label(22, 0, "Entidades Evaluadas", headCellFormat);
            excelSheet.setColumnView(22, headCellView);
            excelSheet.addCell(label);

            label = new Label(23, 0, "Ofertas de Entidades", headCellFormat);
            excelSheet.setColumnView(23, headCellView);
            excelSheet.addCell(label);

            label = new Label(24, 0, "Tipo de Empleo", headCellFormat);
            excelSheet.setColumnView(24, headCellView);
            excelSheet.addCell(label);

            label = new Label(25, 0, "Empleo", headCellFormat);
            excelSheet.setColumnView(25, headCellView);
            excelSheet.addCell(label);

            label = new Label(26, 0, "Antigüedad (meses)", headCellFormat);
            excelSheet.setColumnView(26, headCellView);
            excelSheet.addCell(label);

            label = new Label(27, 0, "Ingresos Bruto (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(27, headCellView);
            excelSheet.addCell(label);

            label = new Label(28, 0, "Destino del Crédito", headCellFormat);
            excelSheet.setColumnView(28, headCellView);
            excelSheet.addCell(label);

            label = new Label(29, 0, "Sexo", headCellFormat);
            excelSheet.setColumnView(29, headCellView);
            excelSheet.addCell(label);

            label = new Label(30, 0, "Fecha de Nacimiento", headCellFormat);
            excelSheet.setColumnView(30, headCellView);
            excelSheet.addCell(label);

            label = new Label(31, 0, "Departamento", headCellFormat);
            excelSheet.setColumnView(31, headCellView);
            excelSheet.addCell(label);

            label = new Label(32, 0, "Provincia", headCellFormat);
            excelSheet.setColumnView(32, headCellView);
            excelSheet.addCell(label);

            label = new Label(33, 0, "Distrito", headCellFormat);
            excelSheet.setColumnView(33, headCellView);
            excelSheet.addCell(label);

            label = new Label(34, 0, "Estado Civil", headCellFormat);
            excelSheet.setColumnView(34, headCellView);
            excelSheet.addCell(label);

            label = new Label(35, 0, "Nivel de Estudios", headCellFormat);
            excelSheet.setColumnView(35, headCellView);
            excelSheet.addCell(label);

            label = new Label(36, 0, "Score Buró", headCellFormat);
            excelSheet.setColumnView(36, headCellView);
            excelSheet.addCell(label);

            label = new Label(37, 0, "Realizado por", headCellFormat);
            excelSheet.setColumnView(37, headCellView);
            excelSheet.addCell(label);

            label = new Label(38, 0, "UTM Source", headCellFormat);
            excelSheet.setColumnView(38, headCellView);
            excelSheet.addCell(label);

            label = new Label(39, 0, "UTM Medium", headCellFormat);
            excelSheet.setColumnView(39, headCellView);
            excelSheet.addCell(label);

            label = new Label(40, 0, "UTM Campaign", headCellFormat);
            excelSheet.setColumnView(40, headCellView);
            excelSheet.addCell(label);

            label = new Label(41, 0, "UTM Term", headCellFormat);
            excelSheet.setColumnView(42, headCellView);
            excelSheet.addCell(label);

            label = new Label(42, 0, "UTM Content", headCellFormat);
            excelSheet.setColumnView(42, headCellView);
            excelSheet.addCell(label);

            label = new Label(43, 0, "¿glcId?", headCellFormat);
            excelSheet.setColumnView(43, headCellView);
            excelSheet.addCell(label);


            label = new Label(44, 0, "¿Proceso Asistido?", headCellFormat);
            excelSheet.setColumnView(44, headCellView);
            excelSheet.addCell(label);

            label = new Label(45, 0, "Tiempo de desembolso (hh)", headCellFormat);
            excelSheet.setColumnView(45, headCellView);
            excelSheet.addCell(label);

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            if (result != null && result.size() > 0) {
                Double exchangeRate = 1.0;

                for (int i = 0; i < result.size(); i++) {

                    ReportOrigination origination = result.get(i);

                    if (internationalCurrency) {
                        exchangeRate = exchangeRateService.getExchangeRate(origination.getCountry().getId());
                    }

                    label = new Label(0, i + 1, String.valueOf(i + 1), leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(1, i + 1, origination.getCountry() != null ? origination.getCountry().getName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(2, i + 1, origination.getOriginationDate() != null ? df.format(origination.getOriginationDate()) : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(3, i + 1, origination.getLoanApplicationCode() != null ? origination.getLoanApplicationCode() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(4, i + 1, origination.getCreditCode() != null ? origination.getCreditCode() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(5, i + 1, origination.getPersonId() != null ? origination.getPersonId().toString() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(6, i + 1, origination.getIdentityDocumentType() != null ? origination.getIdentityDocumentType().getName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(7, i + 1, origination.getDocumentNumber() != null ? origination.getDocumentNumber() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(8, i + 1, origination.getRuc() != null ? origination.getRuc() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(9, i + 1, origination.getSurname() != null ? origination.getSurname() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(10, i + 1, origination.getPersonName() != null ? origination.getPersonName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(11, i + 1, origination.getNationality() != null ? messageSource.getMessage(origination.getNationality().getName(), null, locale) : "", leftCellFormat);
                    excelSheet.addCell(label);

                    Number number = new Number(12, i + 1, origination.getLoanCapital() != null ? origination.getLoanCapital() * exchangeRate : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(13, i + 1, origination.getInstallments() != null ? origination.getInstallments() : 0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(14, i + 1, origination.getEffectiveAnualRate() != null ? origination.getEffectiveAnualRate() / 100 : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(15, i + 1, origination.getEffectiveAnualCostRate() != null ? origination.getEffectiveAnualCostRate() / 100 : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(16, i + 1, origination.getNominalAnualRate() != null ? origination.getNominalAnualRate() / 100 : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(17, i + 1, origination.getInstallmentAmountAvg() != null ? origination.getInstallmentAmountAvg() * exchangeRate : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    label = new Label(18, i + 1, origination.getProduct() != null ? origination.getProduct().getName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(19, i + 1, origination.getEntity() != null ? origination.getEntity().getShortName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(20, i + 1, origination.getEmployer() != null ? origination.getEmployer().getName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    number = new Number(21, i + 1, origination.getEntityCommission() != null ? origination.getEntityCommission() * exchangeRate : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    label = new Label(22, i + 1, origination.getPreliminaryEvaluationEntities() != null ? origination.getPreliminaryEvaluationEntities() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(23, i + 1, origination.getOfferEntities() != null ? origination.getOfferEntities() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(24, i + 1, origination.getActivityType() != null ? messageSource.getMessage(origination.getActivityType().getType(), null, locale) : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(25, i + 1, origination.getCompanyName() != null ? origination.getCompanyName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    number = new Number(26, i + 1, origination.getEmploymentTime() != null ? origination.getEmploymentTime() : 0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(27, i + 1, origination.getIncome() != null ? origination.getIncome() * exchangeRate : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    label = new Label(28, i + 1, origination.getLoanApplicationReason() != null ? origination.getLoanApplicationReason().getReason() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(29, i + 1, origination.getGender() != null ? origination.getGender() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(30, i + 1, origination.getBirthday() != null ? df.format(origination.getBirthday()) : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(31, i + 1, origination.getUbigeo() != null ? origination.getUbigeo().getDepartment().getName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(32, i + 1, origination.getUbigeo() != null ? origination.getUbigeo().getProvince().getName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(33, i + 1, origination.getUbigeo() != null ? origination.getUbigeo().getDistrict().getName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(34, i + 1, origination.getMaritalStatus() != null ? messageSource.getMessage(origination.getMaritalStatus().getStatus(), null, locale) : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(35, i + 1, origination.getStudyLevel() != null ? messageSource.getMessage(origination.getStudyLevel().getLevel(), null, locale) : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(36, i + 1, origination.getScore() != null ? origination.getScore() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(37, i + 1, "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(37, i + 1, origination.getRealizedBy(), leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(38, i + 1, origination.getSource(), leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(39, i + 1, origination.getMedium(), leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(40, i + 1, origination.getCampaign(), leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(41, i + 1, origination.getTerm(), leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(42, i + 1, origination.getContent(), leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(43, i + 1, origination.getGclid() != null ? "Si" : "No", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(44, i + 1, origination.getAssistedProcess() ? "Si" : "No", leftCellFormat);
                    excelSheet.addCell(label);

                    number = new Number(45, i + 1, origination.getDisbursementTime(), leftCellFormat);
                    excelSheet.addCell(number);
                }
            }

            paysheetBook.write();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {

            if (paysheetBook != null) {
                try {
                    paysheetBook.close();
                    outStream.close();
                    return outStream.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public byte[] createLoanReport(Date startDate, Date endDate, String countries) throws Exception {
        List<ReportLoans> loans = creditDao.getLoansReport(startDate, endDate, Configuration.getDefaultLocale(), countries);
        setUserTimeZone(TimeZone.getTimeZone("GMT/UTC"));

        if (loans != null) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Reporte de Solicitudes");

            List<Entity> entities = catalogService.getEntities();
            Integer entitiesSize = entities.size();
            int START_PRE_EVALUATION = 113;
            int END_PRE_EVALUATION = START_PRE_EVALUATION + entitiesSize - 1;
            int START_EVALUATION = START_PRE_EVALUATION + entitiesSize;
            int END_EVALUATION = START_EVALUATION + entitiesSize - 1;

            sheet.addMergedRegion(new CellRangeAddress(0, 0, START_PRE_EVALUATION, END_PRE_EVALUATION));
            Row headerRow = sheet.createRow(0);
            Cell cell = headerRow.createCell(START_PRE_EVALUATION);
            cell.setCellValue("Pre Evaluación");
            CellUtil.setAlignment(cell, workbook, CellStyle.ALIGN_CENTER);

            sheet.addMergedRegion(new CellRangeAddress(0, 0, START_EVALUATION, END_EVALUATION));
            cell = headerRow.createCell(START_EVALUATION);
            cell.setCellValue("Evaluación");
            CellUtil.setAlignment(cell, workbook, CellStyle.ALIGN_CENTER);

            sheet.setColumnWidth(3, 14 * 256);
            sheet.setColumnWidth(11, 14 * 256);
            sheet.setColumnWidth(28, 14 * 256);

            int rowNum = 1;
            headerRow = sheet.createRow(rowNum++);
            cell = headerRow.createCell(0);
            cell.setCellValue("#");
            cell = headerRow.createCell(1);
            cell.setCellValue("País");
            cell = headerRow.createCell(2);
            cell.setCellValue("Loan Application");
            cell = headerRow.createCell(3);
            cell.setCellValue("Fecha y hora");
            cell = headerRow.createCell(4);
            cell.setCellValue("Tipo de Documento");
            cell = headerRow.createCell(5);
            cell.setCellValue("Número de Documento");
            cell = headerRow.createCell(6);
            cell.setCellValue("Apellidos");
            cell = headerRow.createCell(7);
            cell.setCellValue("Nombres");
            cell = headerRow.createCell(8);
            cell.setCellValue("Producto");
            cell = headerRow.createCell(9);
            cell.setCellValue("Monto de Crédito");
            cell = headerRow.createCell(10);
            cell.setCellValue("Estado de la Solicitud");
            cell = headerRow.createCell(11);
            cell.setCellValue("Fecha ultimo estado");
            cell = headerRow.createCell(12);
            cell.setCellValue("Estado del Crédito");
            cell = headerRow.createCell(13);
            cell.setCellValue("Tipo de Proceso");
            cell = headerRow.createCell(14);
            cell.setCellValue("Esperando Pre Evaluación");
            cell = headerRow.createCell(15);
            cell.setCellValue("Esperando Evaluación");
            cell = headerRow.createCell(16);
            cell.setCellValue("Estado de la Pre-Evaluación");
            cell = headerRow.createCell(17);
            cell.setCellValue("Estado de la Evaluación");
            cell = headerRow.createCell(18);
            cell.setCellValue("Pre-evaluación ejecutada");
            cell = headerRow.createCell(19);
            cell.setCellValue("Pre-evaluación aprobada");
            cell = headerRow.createCell(20);
            cell.setCellValue("Solicitud completa");
            cell = headerRow.createCell(21);
            cell.setCellValue("Solicitud con ofertas");
            cell = headerRow.createCell(22);
            cell.setCellValue("Oferta aceptada");
            cell = headerRow.createCell(23);
            cell.setCellValue("Proceso de validación completo");
            cell = headerRow.createCell(24);
            cell.setCellValue("Solicitud aprobada y verificada");
            cell = headerRow.createCell(25);
            cell.setCellValue("Solicitud aceptada / firmada");
            cell = headerRow.createCell(26);
            cell.setCellValue("Solicitud desembolsada");
            cell = headerRow.createCell(27);
            cell.setCellValue("Oferta de Cross Selling");
            cell = headerRow.createCell(28);
            cell.setCellValue("Lead Referido");
            cell = headerRow.createCell(29);
            cell.setCellValue("Tipo de Desembolso");
            cell = headerRow.createCell(30);
            cell.setCellValue("Fecha de desembolso");
            cell = headerRow.createCell(31);
            cell.setCellValue("1 - Datos Personales / Prevalidación");
            cell = headerRow.createCell(32);
            cell.setCellValue("2 - Ingresos / Solicitud");
            cell = headerRow.createCell(33);
            cell.setCellValue("3 - Oferta");
            cell = headerRow.createCell(34);
            cell.setCellValue("4 - Verifiación");
            cell = headerRow.createCell(35);
            cell.setCellValue("5 - Contrato");
            cell = headerRow.createCell(36);
            cell.setCellValue("Resultado Final (Créditos)");
            cell = headerRow.createCell(37);
            cell.setCellValue("Instancia de Rechazo");
            cell = headerRow.createCell(38);
            cell.setCellValue("Tipo de rechazo");
            cell = headerRow.createCell(39);
            cell.setCellValue("IDs Motivos de Rechazo");
            cell = headerRow.createCell(40);
            cell.setCellValue("Motivos de Rechazo");
            cell = headerRow.createCell(41);
            cell.setCellValue("Mensaje de rechazo");
            cell = headerRow.createCell(42);
            cell.setCellValue("Política de rechazo");
            cell = headerRow.createCell(43);
            cell.setCellValue("Mensaje de Política de rechazo");
            cell = headerRow.createCell(44);
            cell.setCellValue("Motivo de rechazo por defecto");
            cell = headerRow.createCell(45);
            cell.setCellValue("Mensaje de rechazo por defecto");
            cell = headerRow.createCell(46);
            cell.setCellValue("Política de rechazo por defecto");
            cell = headerRow.createCell(47);
            cell.setCellValue("Mensaje de Política de rechazo por defecto");
            cell = headerRow.createCell(48);
            cell.setCellValue("PEP (Sí/No)");
            cell = headerRow.createCell(49);
            cell.setCellValue("Motivo del Préstamo");
            cell = headerRow.createCell(50);
            cell.setCellValue("Monto Solicitado");
            cell = headerRow.createCell(51);
            cell.setCellValue("Plazo Solicitado");
            cell = headerRow.createCell(52);
            cell.setCellValue("Estado Civil");
            cell = headerRow.createCell(53);
            cell.setCellValue("Dependientes");
            cell = headerRow.createCell(54);
            cell.setCellValue("Vivienda - Departamento");
            cell = headerRow.createCell(55);
            cell.setCellValue("Vivienda - Provincia");
            cell = headerRow.createCell(56);
            cell.setCellValue("Vivienda - Distrito");
            cell = headerRow.createCell(57);
            cell.setCellValue("Condición de la vivienda");
            cell = headerRow.createCell(58);
            cell.setCellValue("Nivel de estudios");
            cell = headerRow.createCell(59);
            cell.setCellValue("Profesión");
            cell = headerRow.createCell(60);
            cell.setCellValue("Tipo de Empleo");
            cell = headerRow.createCell(61);
            cell.setCellValue("Antigüedad");
            cell = headerRow.createCell(62);
            cell.setCellValue("Ingresos");
            cell = headerRow.createCell(63);
            cell.setCellValue("Ingreso Variable");
            cell = headerRow.createCell(64);
            cell.setCellValue("RUC Empleador");
            cell = headerRow.createCell(65);
            cell.setCellValue("Ocupación");
            cell = headerRow.createCell(66);
            cell.setCellValue("Compañía Telefónica");
            cell = headerRow.createCell(67);
            cell.setCellValue("Edad");
            cell = headerRow.createCell(68);
            cell.setCellValue("Sexo");
            cell = headerRow.createCell(69);
            cell.setCellValue("Nacionalidad");
            cell = headerRow.createCell(70);
            cell.setCellValue("Red Social Vinculada");
            cell = headerRow.createCell(71);
            cell.setCellValue("Referencia 1");
            cell = headerRow.createCell(72);
            cell.setCellValue("Referencia 2");
            cell = headerRow.createCell(73);
            cell.setCellValue("Banco - Cuenta declarada");
            cell = headerRow.createCell(74);
            cell.setCellValue("Compromiso Mensual");
            cell = headerRow.createCell(75);
            cell.setCellValue("RCI");
            cell = headerRow.createCell(76);
            cell.setCellValue("DTI");
            cell = headerRow.createCell(77);
            cell.setCellValue("Equifax Score");
            cell = headerRow.createCell(78);
            cell.setCellValue("Cantidad de Entidades evaluadas");
            cell = headerRow.createCell(79);
            cell.setCellValue("Cantidad de Entidades con oferta");
            cell = headerRow.createCell(80);
            cell.setCellValue("Mejor Oferta - Entidad");
            cell = headerRow.createCell(81);
            cell.setCellValue("Mejor Oferta - Monto");
            cell = headerRow.createCell(82);
            cell.setCellValue("Mejor Oferta - Plazo");
            cell = headerRow.createCell(83);
            cell.setCellValue("Mejor Oferta - TEA");
            cell = headerRow.createCell(84);
            cell.setCellValue("Mejor Oferta - TCEA");
            cell = headerRow.createCell(85);
            cell.setCellValue("Oferta Máxima Mismo Plazo");
            cell = headerRow.createCell(86);
            cell.setCellValue("Oferta Máxima Global (sin considerar plazo)");
            cell = headerRow.createCell(87);
            cell.setCellValue("EFL Process");
            cell = headerRow.createCell(88);
            cell.setCellValue("Score EFL");
            cell = headerRow.createCell(89);
            cell.setCellValue("Tiene crédito hipotecario");
            cell = headerRow.createCell(90);
            cell.setCellValue("Device Type");
            cell = headerRow.createCell(91);
            cell.setCellValue("OS");
            cell = headerRow.createCell(92);
            cell.setCellValue("Browser");
            cell = headerRow.createCell(93);
            cell.setCellValue("Origen");
            cell = headerRow.createCell(94);
            cell.setCellValue("¿gclId?");
            cell = headerRow.createCell(95);
            cell.setCellValue("UTM Source");
            cell = headerRow.createCell(96);
            cell.setCellValue("UTM Medium");
            cell = headerRow.createCell(97);
            cell.setCellValue("UTM Campaign");
            cell = headerRow.createCell(98);
            cell.setCellValue("UTM Term");
            cell = headerRow.createCell(99);
            cell.setCellValue("UTM Content");
            cell = headerRow.createCell(100);
            cell.setCellValue("Fuente");
            cell = headerRow.createCell(101);
            cell.setCellValue("Lead-Entidad");
            cell = headerRow.createCell(102);
            cell.setCellValue("Entidad brandeada");
            cell = headerRow.createCell(103);
            cell.setCellValue("Alerta de fraudes");
            cell = headerRow.createCell(104);
            cell.setCellValue("Proceso Asistido");
            cell = headerRow.createCell(105);
            cell.setCellValue("Compromiso mensual");
            cell = headerRow.createCell(106);
            cell.setCellValue("Endeudamiento total");
            cell = headerRow.createCell(107);
            cell.setCellValue("Nivel Socio Económico");
            cell = headerRow.createCell(108);
            cell.setCellValue("Peor Calificación - Actual");
            cell = headerRow.createCell(109);
            cell.setCellValue("Peor Calificación - ultimos 6 m");
            cell = headerRow.createCell(110);
            cell.setCellValue("Peor Calificación - 7 a 12 m");
            cell = headerRow.createCell(111);
            cell.setCellValue("Peor Calificación - 13 a 24 m");
            cell = headerRow.createCell(112);
            cell.setCellValue("Flujo de preguntas");
            for (int i = 0; i < entitiesSize; i++) {
                cell = headerRow.createCell(START_PRE_EVALUATION + i);
                cell.setCellValue(entities.get(i).getShortName());
            }

            for (int i = 0; i < entitiesSize; i++) {
                cell = headerRow.createCell(START_EVALUATION + i);
                cell.setCellValue(entities.get(i).getShortName());
            }

            //format to dates
            CellStyle cellStyleDate1 = workbook.createCellStyle();
            CreationHelper createHelperDate1 = workbook.getCreationHelper();
            cellStyleDate1.setDataFormat(
                    createHelperDate1.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

            CellStyle cellStyleDate2 = workbook.createCellStyle();
            CreationHelper createHelperDate2 = workbook.getCreationHelper();
            cellStyleDate2.setDataFormat(
                    createHelperDate2.createDataFormat().getFormat("dd/MM/yyyy"));

            int index = 1;
            for (ReportLoans loan : loans) {
                Row row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(index);

                cell = row.createCell(1);
                cell.setCellValue(loan.getCountry());

                cell = row.createCell(2);
                cell.setCellValue(loan.getLoanApplicationCode());
                cell = row.createCell(3);
                cell.setCellValue(loan.getRegisterDate());
                cell.setCellStyle(cellStyleDate1);
                cell = row.createCell(4);
                cell.setCellValue(loan.getIdentityDocumentType() != null ? loan.getIdentityDocumentType().getName() : null);
                cell = row.createCell(5);
                cell.setCellValue(loan.getDocumentNumber());
                cell = row.createCell(6);
                cell.setCellValue(loan.getSurname());
                cell = row.createCell(7);
                cell.setCellValue(loan.getPersonName());
                cell = row.createCell(8);
                cell.setCellValue(loan.getProduct() != null ? loan.getProduct().getName() : null);
                cell = row.createCell(9);
                if (loan.getLoanCapital() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getLoanCapital());
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(10);
                cell.setCellValue(loan.getLoanApplicationStatus() != null ? loan.getLoanApplicationStatus().getStatus() : null);
                cell = row.createCell(11);
                cell.setCellValue(loan.getLastStatusUpdate());
                cell.setCellStyle(cellStyleDate1);
                cell = row.createCell(12);
                cell.setCellValue(loan.getCreditStatus() != null ? loan.getCreditStatus().getStatus() : null);
                cell = row.createCell(13);
                cell.setCellType(CellType.BLANK);
                cell = row.createCell(14);
                cell.setCellValue(loan.getWaitingPreEvaluation());
                cell = row.createCell(15);
                cell.setCellValue(loan.getWaitingEvaluation());
                cell = row.createCell(16);
                cell.setCellValue(loan.getPreEvaluationStatus());
                cell = row.createCell(17);
                cell.setCellValue(loan.getEvaluationStatus());
                cell = row.createCell(18);
                if (loan.getPreEvaluationExecuted() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getPreEvaluationExecuted());
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(19);
                if (loan.getPreEvaluationApproved() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getPreEvaluationApproved());
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(20);
                if (loan.getApplicationCompleted() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getApplicationCompleted());
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(21);
                if (loan.getHasOffers() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getHasOffers());
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(22);
                if (loan.getHasSelectedOffer() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getHasSelectedOffer());
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(23);
                if (loan.getVerificationCompleted() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getVerificationCompleted());
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(24);
                if (loan.getApplicationApproved() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getApplicationApproved());
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(25);
                if (loan.getApplicationApprovedAndSigned() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getApplicationApprovedAndSigned());
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(26);
                if (loan.getIsDisbursed() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getIsDisbursed());
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(27);
                if (loan.getIsDisbursed() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getLoanApplicationStatus().getId() == LoanApplicationStatus.CROSS_SELLING_OFFER ||
                            loan.getLoanApplicationStatus().getId() == LoanApplicationStatus.LEAD_REFERRED ||
                            (loan.getProduct() != null && loan.getProduct().getId() == Product.PREPAY_CARD) ? 1 : 0);
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(28);
                if (loan.getIsDisbursed() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getLoanApplicationStatus().getId() == LoanApplicationStatus.LEAD_REFERRED ? 1 : 0);
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(29);
                if (loan.getDisbursementType() != null)
                    cell.setCellValue(loan.getDisbursementType().getDisbursementType());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(30);
                cell.setCellValue(loan.getDisbursementDate());
                cell.setCellStyle(cellStyleDate2);
                cell = row.createCell(31);
                if (loan.getPassedPersonalInformation())
                    cell.setCellValue("Superó");
                else
                    cell.setCellValue("Última instancia");
                cell = row.createCell(32);
                if (loan.getPassedIncomeInformation())
                    cell.setCellValue("Superó");
                else if (loan.getPassedPersonalInformation())
                    cell.setCellValue("Última instancia");
                else
                    cell.setCellValue("No alcanzó");
                cell = row.createCell(33);
                if (loan.getPassedOfferScreen())
                    cell.setCellValue("Superó");
                else if (loan.getPassedIncomeInformation())
                    cell.setCellValue("Última instancia");
                else
                    cell.setCellValue("No alcanzó");
                cell = row.createCell(34);
                if (loan.getPassedVerificationScreen())
                    cell.setCellValue("Superó");
                else if (loan.getPassedOfferScreen())
                    cell.setCellValue("Última instancia");
                else
                    cell.setCellValue("No alcanzó");
                cell = row.createCell(35);
                if (loan.getPassedContract())
                    cell.setCellValue("Superó");
                else if (loan.getPassedVerificationScreen())
                    cell.setCellValue("Última instancia");
                else
                    cell.setCellValue("No alcanzó");
                cell = row.createCell(36);
                cell.setCellValue(loan.getFinalResult());
                cell = row.createCell(37);
                cell.setCellValue(loan.getRejectionInstance());
                cell = row.createCell(38);
                cell.setCellValue(loan.getRejectionType());
                cell = row.createCell(39);
                String hardFilter = loan.getHardFilterId() != null ? loan.getHardFilterId() : "";
                String policyFilter = loan.getPolicyId() != null ? loan.getPolicyId() : "";
                cell.setCellValue(hardFilter.concat(policyFilter));
                cell = row.createCell(40);
                String hardFilterValues = loan.getHardFilterValues() != null ? loan.getHardFilterValues() : "";
                String policyFilterValues = loan.getPolicyValues() != null ? loan.getPolicyValues() : "";
                if (hardFilterValues.length() > 0 && policyFilterValues.length() > 0)
                    cell.setCellValue(hardFilterValues.concat(" - " + policyFilterValues));
                else
                    cell.setCellValue(hardFilterValues.concat(policyFilterValues));

                cell = row.createCell(41);
                if (loan.getHardFilterMessages() != null) {
                    List<String> messageI18n = new ArrayList<>();
                    for (int i = 0; i < loan.getHardFilterMessages().size(); i++) {
                        messageI18n.add(messageSource.getMessage(loan.getHardFilterMessages().get(i), null, Configuration.getDefaultLocale()));
                    }
                    String hardFilterMessage = String.join(", ", messageI18n);
                    cell.setCellValue(hardFilterMessage);

                }

                cell = row.createCell(42);
                cell.setCellValue(loan.getPolicyString() != null ? loan.getPolicyString() : null);
                cell = row.createCell(43);
                cell.setCellValue(loan.getPolicyMessageString() != null ? loan.getPolicyMessageString() : null);


                cell = row.createCell(44);
                cell.setCellValue(loan.getDefaultHardFilterString() != null ? loan.getDefaultHardFilterString() : null);
                cell = row.createCell(45);
                if (loan.getDefaultHardFilter() != null) {
                    List<String> messageI18n = new ArrayList<>();
                    for (int i = 0; i < loan.getDefaultHardFilter().size(); i++) {
                        messageI18n.add(messageSource.getMessage(loan.getDefaultHardFilter().get(i).getHardFilterMessage().trim(), null, Configuration.getDefaultLocale()));
                    }
                    String defaultMessage = String.join(", ", messageI18n);
                    cell.setCellValue(defaultMessage);

                }

                cell = row.createCell(46);
                cell.setCellValue(loan.getDefaultPolicyString() != null ? loan.getDefaultPolicyString() : null);
                cell = row.createCell(47);
                cell.setCellValue(loan.getDefaultPolicyMessageString() != null ? loan.getDefaultPolicyMessageString() : null);


                cell = row.createCell(48);
                cell.setCellValue(loan.getPep());
                cell = row.createCell(49);
                cell.setCellValue(loan.getReason() != null ? loan.getReason().getReason() : null);
                cell = row.createCell(50);
                if (loan.getAmount() != null)
                    cell.setCellValue(loan.getAmount());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(51);
                if (loan.getInstallments() != null)
                    cell.setCellValue(loan.getInstallments());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(52);
                cell.setCellValue(loan.getMaritalStatus() != null ? loan.getMaritalStatus().getStatus() : null);
                cell = row.createCell(53);
                if (loan.getDependents() != null)
                    cell.setCellValue(loan.getDependents());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(54);
                cell.setCellValue(loan.getUbigeo() != null ? loan.getUbigeo().getDepartment().getName() : null);
                cell = row.createCell(55);
                cell.setCellValue(loan.getUbigeo() != null ? loan.getUbigeo().getProvince().getName() : null);
                cell = row.createCell(56);
                cell.setCellValue(loan.getUbigeo() != null ? loan.getUbigeo().getDistrict().getName() : null);
                cell = row.createCell(57);
                cell.setCellValue(loan.getHousingType() != null ? loan.getHousingType().getType() : null);
                cell = row.createCell(58);
                cell.setCellValue(loan.getStudyLevel() != null ? loan.getStudyLevel().getLevel() : null);
                cell = row.createCell(59);
                cell.setCellValue(loan.getProfession() != null ? loan.getProfession().getProfession() : null);
                cell = row.createCell(60);
                cell.setCellValue(loan.getActivityType() != null ? loan.getActivityType().getType() : null);
                cell = row.createCell(61);
                if (loan.getEmploymentTime() != null)
                    cell.setCellValue(loan.getEmploymentTime());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(62);
                if (loan.getIncome() != null)
                    cell.setCellValue(loan.getIncome());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(63);
                if (loan.getVariableGrossIncome() != null)
                    cell.setCellValue(loan.getVariableGrossIncome());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(64);
                cell.setCellValue(loan.getRuc());
                cell = row.createCell(65);
                cell.setCellValue(loan.getOcupation() != null ? loan.getOcupation().getOcupation() : null);
                cell = row.createCell(66);
                if (loan.getCompanyName() != null)
                    cell.setCellValue(loan.getOperadoresTelefonicos());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(67);
                cell.setCellValue(utilService.yearsBetween(loan.getBirthday(), new Date()));
                cell = row.createCell(68);
                cell.setCellValue(loan.getGender());
                cell = row.createCell(69);
                cell.setCellValue(loan.getNationality() != null ? loan.getNationality().getName() : null);
                cell = row.createCell(70);
                cell.setCellValue(loan.getSocialNetworks());

                if (loan.getReferrals() != null && loan.getReferrals().size() > 0) {
                    cell = row.createCell(71);
                    if (loan.getReferrals().get(0) != null)
                        cell.setCellValue(loan.getReferrals().get(0).getRelationship().getRelationship().concat(" - ").concat(loan.getReferrals().get(0).getPhoneNumber()));
                    else
                        cell.setCellType(CellType.BLANK);
                    cell = row.createCell(72);
                    if (loan.getReferrals().size() > 1) {
                        if (loan.getReferrals().get(1) != null)
                            cell.setCellValue(loan.getReferrals().get(1).getRelationship().getRelationship().concat(" - ").concat(loan.getReferrals().get(1).getPhoneNumber()));
                        else
                            cell.setCellType(CellType.BLANK);
                    }
                } else {
                    cell = row.createCell(71);
                    cell.setCellType(CellType.BLANK);
                    cell = row.createCell(72);
                    cell.setCellType(CellType.BLANK);
                }
                cell = row.createCell(73);
                String bank = "";
                if (loan.getBank() != null)
                    bank = loan.getBank().getName().concat(" - ").concat(loan.getBankAccount() != null ? loan.getBankAccount() : "");
                cell.setCellValue(bank);
                cell = row.createCell(74);
                if (loan.getSbsMonthlyInstallment() != null)
                    cell.setCellValue(loan.getSbsMonthlyInstallment());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(75);
                if (loan.getLoanApplicationRCI() != null)
                    cell.setCellValue(loan.getLoanApplicationRCI());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(76);
                if (loan.getCreditRCI() != null)
                    cell.setCellValue(loan.getCreditRCI());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(77);
                if (loan.getScore() != null)
                    cell.setCellValue(loan.getScore());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(78);
                if (loan.getEvaluations() != null)
                    cell.setCellValue(loan.getEvaluations());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(79);
                if (loan.getApprovedEntities() != null)
                    cell.setCellValue(loan.getApprovedEntities());
                else
                    cell.setCellType(CellType.BLANK);

                cell = row.createCell(80);
                if (loan.getSelectedEntity() != null)
                    cell.setCellValue(loan.getSelectedEntity().getFullName());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(81);
                cell.setCellValue(loan.getOfferAmount());
                cell = row.createCell(82);
                cell.setCellValue(loan.getOfferInstallments());
                cell = row.createCell(83);
                cell.setCellValue(loan.getOfferTEAS());
                cell = row.createCell(84);
                cell.setCellValue(loan.getOfferTCEAS());
                cell = row.createCell(85);
                if (loan.getMaxAmountOffer() != null)
                    cell.setCellValue(loan.getMaxAmountOffer());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(86);
                if (loan.getMaxAmountSameInstallments() != null)
                    cell.setCellValue(loan.getMaxAmountSameInstallments());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(87);
                if (loan.getEflProccess() != null)
                    cell.setCellValue(loan.getEflProccess());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(88);
                if (loan.getEflScore() != null)
                    cell.setCellValue(loan.getEflScore());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(89);
                if (loan.getMortgageAmount() != null && loan.getMortgageAmount() > 0)
                    cell.setCellValue("Sí");
                else
                    cell.setCellValue("No");
                cell = row.createCell(90);
                cell.setCellValue(loan.getIovationType());
                cell = row.createCell(91);
                cell.setCellValue(loan.getIovationOs());
                cell = row.createCell(92);
                cell.setCellValue(loan.getIovationBrowser());
                cell = row.createCell(93);
                cell.setCellValue(loan.getOrigin());
                cell = row.createCell(94);
                cell.setCellValue(loan.getWithGclid());
                cell = row.createCell(95);
                cell.setCellValue(loan.getSource());
                cell = row.createCell(96);
                cell.setCellValue(loan.getMedium());
                cell = row.createCell(97);
                cell.setCellValue(loan.getCampaign());
                cell = row.createCell(98);
                cell.setCellValue(loan.getTerm());
                cell = row.createCell(99);
                cell.setCellValue(loan.getContent());
                cell = row.createCell(100);
                cell.setCellValue(loan.getApplicationSource());
                cell = row.createCell(101);
                cell.setCellValue(loan.getLoanApplicationStatus().getId() == LoanApplicationStatus.LEAD_REFERRED ||
                        loan.getLoanApplicationStatus().getId() == LoanApplicationStatus.CROSS_SELLING_OFFER ? loan.getReferredLeads() : null);
                cell = row.createCell(102);
                cell.setCellValue(loan.getEntityBranding() != null ? loan.getEntityBranding().getShortName() : null);
                cell = row.createCell(103);
                cell.setCellValue(loan.getHasFraudAlerts());
                cell = row.createCell(104);
                cell.setCellValue(loan.getAssistedProcess() ? "Asistido" : "Web");
                cell = row.createCell(105);
                if (loan.getMonthlyInstallment() != null) cell.setCellValue(loan.getMonthlyInstallment());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(106);
                if (loan.getTotalDebt() != null) cell.setCellValue(loan.getTotalDebt());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(107);
                cell.setCellValue(loan.getSocioeconomicLevel() != null ? loan.getSocioeconomicLevel() : "");
                cell = row.createCell(108);
                cell.setCellValue(loan.getWorstCalificationCurrent() != null ? loan.getWorstCalificationCurrent() : "");
                cell = row.createCell(109);
                cell.setCellValue(loan.getWorstCalificationU6M() != null ? loan.getWorstCalificationU6M() : "");
                cell = row.createCell(110);
                cell.setCellValue(loan.getWorstCalification7to12() != null ? loan.getWorstCalification7to12() : "");
                cell = row.createCell(111);
                cell.setCellValue(loan.getWorstCalification13to24() != null ? loan.getWorstCalification13to24() : "");
                cell = row.createCell(112);
                if (loan.getQuestionFlow() != null) {
                    cell.setCellValue(loan.getQuestionFlow() == LoanApplication.QUESTION_FLOW_GROUPED ? "Agrupado" : "Individual");
                } else {
                    cell.setCellValue("");
                }

                for (int i = 0; i < entitiesSize; i++) {
                    cell = row.createCell(START_PRE_EVALUATION + i);
                    if (loan.getPreapprovedEntitiesName() != null) {
                        String[] entitiesArr = loan.getPreapprovedEntitiesName().split(",");
                        for (int j = 0; j < entitiesArr.length; j++) {
                            if (entities.get(i).getShortName().equals(entitiesArr[j].trim())) {
                                cell.setCellValue("Aprobó");
                                break;
                            }
                        }
                    }

                    if (loan.getPrerejectedEntitiesName() != null) {
                        String[] entitiesArr = loan.getPrerejectedEntitiesName().split(",");
                        for (int j = 0; j < entitiesArr.length; j++) {
                            if (entities.get(i).getShortName().equals(entitiesArr[j].trim())) {
                                cell.setCellValue("No Aprobó");
                                break;
                            }
                        }
                    }
                }

                for (int i = 0; i < entitiesSize; i++) {
                    cell = row.createCell(START_EVALUATION + i);
                    if (loan.getApprovedEntitiesName() != null) {
                        String[] entitiesArr = loan.getApprovedEntitiesName().split(",");
                        for (int j = 0; j < entitiesArr.length; j++) {
                            if (entities.get(i).getShortName().equals(entitiesArr[j].trim())) {
                                cell.setCellValue("Aprobó");
                                break;
                            }
                        }
                    }

                    if (loan.getRejectedEntitiesName() != null) {
                        String[] entitiesArr = loan.getRejectedEntitiesName().split(",");
                        for (int j = 0; j < entitiesArr.length; j++) {
                            if (entities.get(i).getShortName().equals(entitiesArr[j].trim())) {
                                cell.setCellValue("No Aprobó");
                                break;
                            }
                        }
                    }
                }

                index++;
            }

            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                workbook.write(outStream);
                workbook.close();
                outStream.close();
                return outStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    @Override
    public byte[] createLoanLightReport(Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate, Date startDate2, Date endDate2, Integer[] countryIds, Integer[] entityIds, Integer[] products) throws Exception {
        List<ReportLoans> loans = creditDao.getLoansLightReport(minAge, maxAge, requestType, cardType, startDate, endDate, startDate2, endDate2, Configuration.getDefaultLocale(), countryIds, entityIds, products);
        return createLoanLightReport(loans, true);
    }

    @Override
    public byte[] createLoanLightReport(Date startDate, Date endDate, Integer[] countryIds, Integer[] entityIds) throws Exception {
        List<ReportLoans> loans = creditDao.getLoansLightReport(startDate, endDate, Configuration.getDefaultLocale(), countryIds, entityIds);
        return createLoanLightReport(loans, false);
    }

    private byte[] createLoanLightReport(List<ReportLoans> loans, boolean isBanBif) {
        setUserTimeZone(TimeZone.getTimeZone("America/Lima"));

        if(loans != null && !loans.isEmpty() && (loans.get(0).getSelectedEntity() != null || loans.get(0).getEntityBranding() != null)){
            if((loans.get(0).getSelectedEntity() != null && loans.get(0).getSelectedEntity().getId() == Entity.BANCO_DEL_SOL) || (loans.get(0).getEntityBranding() != null && loans.get(0).getEntityBranding().getId() == Entity.BANCO_DEL_SOL)){
                setUserTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
            }
        }


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Reporte Consolidado de Solicitudes");

        //format to dates
        CellStyle cellStyleDate1 = workbook.createCellStyle();
        CreationHelper createHelperDate1 = workbook.getCreationHelper();
        cellStyleDate1.setDataFormat(
                createHelperDate1.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

        CellStyle cellBgColorStyle = workbook.createCellStyle();
        cellBgColorStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());

        Row headerRow;
        Cell cell;

        int rowNum = 0;
        headerRow = sheet.createRow(rowNum++);

        if (isBanBif) {
            cell = headerRow.createCell(0);
            cell.setCellValue("#");
            cell = headerRow.createCell(1);
            cell.setCellValue("País");
            cell = headerRow.createCell(2);
            cell.setCellValue("Application Number");
            cell = headerRow.createCell(3);
            cell.setCellValue("Fecha y hora");
            cell = headerRow.createCell(4);
            cell.setCellValue("Tipo de Documento");
            cell = headerRow.createCell(5);
            cell.setCellValue("Número de Documento");
            cell = headerRow.createCell(6);
            cell.setCellValue("Apellidos");
            cell = headerRow.createCell(7);
            cell.setCellValue("Nombres");
            cell = headerRow.createCell(8);
            cell.setCellValue("Edad");
            cell = headerRow.createCell(9);
            cell.setCellValue("Teléfono");
            cell = headerRow.createCell(10);
            cell.setCellValue("Correo");
            cell = headerRow.createCell(11);
            cell.setCellValue("Producto");
            cell = headerRow.createCell(12);
            cell.setCellValue("Tipo");
            cell = headerRow.createCell(13);
            cell.setCellValue("Medio");
            cell = headerRow.createCell(14);
            cell.setCellValue("Estado de la Solicitud");
            cell = headerRow.createCell(15);
            cell.setCellValue("Fecha último estado");
            cell = headerRow.createCell(16);
            cell.setCellValue("Estado");
            cell = headerRow.createCell(17);
            cell.setCellValue("Línea de Crédito Máxima");
            cell = headerRow.createCell(18);
            cell.setCellValue("Registro"); // Pre-evaluación ejecutada
            cell = headerRow.createCell(19);
            cell.setCellValue("Pre-evaluación aprobada");
            cell = headerRow.createCell(20);
            cell.setCellValue("Validación completa"); // Solicitud completa
            cell = headerRow.createCell(21);
            cell.setCellValue("Oferta aceptada"); // Solicitud con ofertas
            cell = headerRow.createCell(22);
            cell.setCellValue("Solicitud completa"); // Oferta aceptada
            cell = headerRow.createCell(23);
            cell.setCellValue("Proceso de validación completo");
            cell.setCellStyle(cellBgColorStyle);
            cell = headerRow.createCell(24);
            cell.setCellValue("Solicitud aprobada y verificada");
            cell.setCellStyle(cellBgColorStyle);
            cell = headerRow.createCell(25);
            cell.setCellValue("Solicitud aceptada / firmada");
            cell.setCellStyle(cellBgColorStyle);
            cell = headerRow.createCell(26);
            cell.setCellValue("Solicitud desembolsada");
            cell.setCellStyle(cellBgColorStyle);
            cell = headerRow.createCell(27);
            cell.setCellValue("Tarjeta entregada");
            cell = headerRow.createCell(28);
            cell.setCellValue("Resultado Final");
            cell = headerRow.createCell(29);
            cell.setCellValue("Instancia de Rechazo");
            cell = headerRow.createCell(30);
            cell.setCellValue("Motivos de Rechazo");
            cell = headerRow.createCell(31);
            cell.setCellValue("Motivo del Préstamo");
            cell.setCellStyle(cellBgColorStyle);
            cell = headerRow.createCell(32);
            cell.setCellValue("Monto Solicitado");
            cell.setCellStyle(cellBgColorStyle);
            cell = headerRow.createCell(33);
            cell.setCellValue("Plazo Solicitado");
            cell.setCellStyle(cellBgColorStyle);
            cell = headerRow.createCell(34);
            cell.setCellValue("Proceso A/B Testing");
            cell = headerRow.createCell(35);
            cell.setCellValue("Motivo - No la quiero");
            cell = headerRow.createCell(36);
            cell.setCellValue("Empresa");
            cell = headerRow.createCell(37);
            cell.setCellValue("UTM Source");
            cell = headerRow.createCell(38);
            cell.setCellValue("UTM Medium");
            cell = headerRow.createCell(39);
            cell.setCellValue("UTM Campaign");
            int startStepsData = 40;
            int endStepsData = 40;
            int countSteps = 0;
            fillInHeaderPreApprovedBase(Entity.BANBIF,endStepsData,headerRow,cell);
        } else {
            cell = headerRow.createCell(0);
            cell.setCellValue("#");
            cell = headerRow.createCell(1);
            cell.setCellValue("País");
            cell = headerRow.createCell(2);
            cell.setCellValue("Loan Application");
            cell = headerRow.createCell(3);
            cell.setCellValue("Fecha y hora");
            cell = headerRow.createCell(4);
            cell.setCellValue("Tipo de Documento");
            cell = headerRow.createCell(5);
            cell.setCellValue("Número de Documento");
            cell = headerRow.createCell(6);
            cell.setCellValue("Apellidos");
            cell = headerRow.createCell(7);
            cell.setCellValue("Nombres");
            cell = headerRow.createCell(8);
            cell.setCellValue("Producto");
            cell = headerRow.createCell(9);
            cell.setCellValue("Estado de la Solicitud");
            cell = headerRow.createCell(10);
            cell.setCellValue("Fecha último estado");
            cell = headerRow.createCell(11);
            cell.setCellValue("Estado del Crédito");
            cell = headerRow.createCell(12);
            cell.setCellValue("Monto de Crédito");
            cell = headerRow.createCell(13);
            cell.setCellValue("Pre-evaluación ejecutada");
            cell = headerRow.createCell(14);
            cell.setCellValue("Pre-evaluación aprobada");
            cell = headerRow.createCell(15);
            cell.setCellValue("Solicitud completa");
            cell = headerRow.createCell(16);
            cell.setCellValue("Solicitud con ofertas");
            cell = headerRow.createCell(17);
            cell.setCellValue("Oferta aceptada");
            cell = headerRow.createCell(18);
            cell.setCellValue("Proceso de validación completo");
            cell = headerRow.createCell(19);
            cell.setCellValue("Solicitud aprobada y verificada");
            cell = headerRow.createCell(20);
            cell.setCellValue("Solicitud aceptada / firmada");
            cell = headerRow.createCell(21);
            cell.setCellValue("Solicitud desembolsada");
            cell = headerRow.createCell(22);
            cell.setCellValue("Resultado Final (Créditos)");
            cell = headerRow.createCell(23);
            cell.setCellValue("Instancia de Rechazo");
            cell = headerRow.createCell(24);
            cell.setCellValue("Motivos de Rechazo");
            cell = headerRow.createCell(25);
            cell.setCellValue("Motivo del Préstamo");
            cell = headerRow.createCell(26);
            cell.setCellValue("Monto Solicitado");
            cell = headerRow.createCell(27);
            cell.setCellValue("Plazo Solicitado");
            cell = headerRow.createCell(28);
            cell.setCellValue("Estado Civil");
            cell = headerRow.createCell(29);
            cell.setCellValue("Dependientes");
            cell = headerRow.createCell(30);
            cell.setCellValue("Vivienda - Departamento");
            cell = headerRow.createCell(31);
            cell.setCellValue("Vivienda - Provincia");
            cell = headerRow.createCell(32);
            cell.setCellValue("Vivienda - Distrito");
            cell = headerRow.createCell(33);
            cell.setCellValue("Condición de la vivienda");
            cell = headerRow.createCell(34);
            cell.setCellValue("Nivel de estudios");
            cell = headerRow.createCell(35);
            cell.setCellValue("Profesión");
            cell = headerRow.createCell(36);
            cell.setCellValue("Tipo de Empleo");
            cell = headerRow.createCell(37);
            cell.setCellValue("Antigüedad");
            cell = headerRow.createCell(38);
            cell.setCellValue("Ingresos");
            cell = headerRow.createCell(39);
            cell.setCellValue("Ingreso Variable");
            cell = headerRow.createCell(40);
            cell.setCellValue("RUC Empleador");
            cell = headerRow.createCell(41);
            cell.setCellValue("Ocupación");
            cell = headerRow.createCell(42);
            cell.setCellValue("Compañía Telefónica");
            cell = headerRow.createCell(43);
            cell.setCellValue("Edad");
            cell = headerRow.createCell(44);
            cell.setCellValue("Celular");
            cell = headerRow.createCell(45);
            cell.setCellValue("Correo");
            cell = headerRow.createCell(46);
            cell.setCellValue("Sexo");
            cell = headerRow.createCell(47);
            cell.setCellValue("Nacionalidad");
            cell = headerRow.createCell(48);
            cell.setCellValue("Red Social Vinculada");
            cell = headerRow.createCell(49);
            cell.setCellValue("Referencia 1");
            cell = headerRow.createCell(50);
            cell.setCellValue("Referencia 2");
            cell = headerRow.createCell(51);
            cell.setCellValue("Banco - Cuenta declarada");
            cell = headerRow.createCell(52);
            cell.setCellValue("Compromiso Mensual");
            cell = headerRow.createCell(53);
            cell.setCellValue("RCI");
            cell = headerRow.createCell(54);
            cell.setCellValue("DTI");
            cell = headerRow.createCell(55);
            cell.setCellValue("Compromiso mensual");
            cell = headerRow.createCell(56);
            cell.setCellValue("Endeudamiento total");
        }

        if (loans != null) {
            int index = 1;
            for (ReportLoans loan : loans) {
                Row row = sheet.createRow(rowNum++);

                if (isBanBif) {
                    cell = row.createCell(0);
                    cell.setCellValue(index);
                    cell = row.createCell(1);
                    cell.setCellValue(loan.getCountry());
                    cell = row.createCell(2);
                    cell.setCellValue(loan.getLoanApplicationCode());
                    cell = row.createCell(3);
                    cell.setCellValue(loan.getRegisterDate());
                    cell.setCellStyle(cellStyleDate1);
                    cell = row.createCell(4);
                    cell.setCellValue(loan.getIdentityDocumentType() != null ? loan.getIdentityDocumentType().getName() : null);
                    cell = row.createCell(5);
                    cell.setCellValue(loan.getDocumentNumber());
                    cell = row.createCell(6);
                    cell.setCellValue(loan.getSurname());
                    cell = row.createCell(7);
                    cell.setCellValue(loan.getPersonName());
                    cell = row.createCell(8);
                    cell.setCellValue(utilService.yearsBetween(loan.getBirthday(), new Date()));
                    cell = row.createCell(9);
                    cell.setCellValue(loan.getPhoneNumber());
                    cell = row.createCell(10);
                    cell.setCellValue(loan.getEmail());
                    cell = row.createCell(11);
                    cell.setCellValue(loan.getProduct() != null ? loan.getProduct().getName() : null);
                    cell = row.createCell(12);
                    if(loan.getEntityProductParam() == null) cell.setCellValue("");
                    else {
                        cell.setCellValue(loan.getEntityProductParam().getId().intValue() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA ? "Visa Cero Membresía" : loan.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANBIF_TIPO_TARJETA_DE_BASE.getKey()));
                    }

                    cell = row.createCell(13);
                    cell.setCellValue(loan.getBanbifRequestType());
                    cell = row.createCell(14);
                    cell.setCellValue(loan.getLoanApplicationStatus() != null ? loan.getLoanApplicationStatus().getStatus() : null);
                    cell = row.createCell(15);
                    cell.setCellValue(loan.getLastStatusUpdate());
                    cell.setCellStyle(cellStyleDate1);

                    cell = row.createCell(16);
                    if (loan.getCreditStatus() != null) {
                        if (loan.getCreditStatus().getId() == CreditStatus.ORIGINATED)
                            cell.setCellValue("Por entregar");
                        else if (loan.getCreditStatus().getId() == CreditStatus.ORIGINATED_DISBURSED)
                            cell.setCellValue("Entregado");
                        else if (loan.getCreditStatus().getId() == CreditStatus.REJECTED)
                            cell.setCellValue("Cancelado");
                        else
                            cell.setCellType(CellType.BLANK);
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }

                    cell = row.createCell(17);
                    if (loan.getBanbifBaseDataAsInteger("linea") != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getBanbifBaseDataAsInteger("linea"));
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(18);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(1); // All LAs

                    cell = row.createCell(19);
                    if (loan.getPreEvaluationApproved() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getPreEvaluationApproved());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(20);
                    cell.setCellType(CellType.NUMERIC);
                    if (loan.getLogApplicationStatusIds().contains(LoanApplicationStatus.EVAL_APPROVED) || loan.getLogApplicationStatusIds().contains(LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION)) {
                        cell.setCellValue(1);
                    } else {
                        cell.setCellValue(0);
                    }
                    cell = row.createCell(21);
                    if (loan.getHasSelectedOffer() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getHasSelectedOffer());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(22);
                    cell.setCellType(CellType.NUMERIC);
                    if (loan.getLogCreditStatusIds() != null && loan.getLogCreditStatusIds().contains(CreditStatus.ORIGINATED)) {
                        cell.setCellValue(1);
                    } else {
                        cell.setCellValue(0);
                    }
                    cell = row.createCell(23);
                    cell.setCellType(CellType.BLANK);

                    cell = row.createCell(24);
                    cell.setCellType(CellType.BLANK);

                    cell = row.createCell(25);
                    cell.setCellType(CellType.BLANK);

                    cell = row.createCell(26);
                    cell.setCellType(CellType.BLANK);

                    cell = row.createCell(27);
                    cell.setCellType(CellType.NUMERIC);
                    if ((loan.getCreditStatus() != null && loan.getCreditStatus().getId() == CreditStatus.ORIGINATED_DISBURSED)
                    || (loan.getLoanApplicationStatus().getId() == LoanApplicationStatus.LEAD_CONVERTED)) {
                        cell.setCellValue(1);
                    } else {
                        cell.setCellValue(0);
                    }

                    cell = row.createCell(28);
                    cell.setCellValue(loan.getFinalResult());

                    cell = row.createCell(29);
                    cell.setCellValue(loan.getRejectionInstance());

                    cell = row.createCell(30);
                    if (loan.getLoanApplicationStatus() != null) {
                        if (loan.getLoanApplicationStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC) {
                            String hardFilterValues = loan.getHardFilterValues() != null ? loan.getHardFilterValues() : "";
                            if (hardFilterValues.length() > 0)
                                cell.setCellValue(hardFilterValues.split(",")[0]);

                        } else if (loan.getLoanApplicationStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION) {
                            String policyFilterValues = loan.getPolicyValues() != null ? loan.getPolicyValues() : "";
                            if (policyFilterValues.length() > 0)
                                cell.setCellValue(policyFilterValues.split(",")[0]);
                        } else {
                            cell.setCellType(CellType.BLANK);
                        }
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }

                    cell = row.createCell(31);
                    cell.setCellType(CellType.BLANK);

                    cell = row.createCell(32);
                    cell.setCellType(CellType.BLANK);

                    cell = row.createCell(33);
                    cell.setCellType(CellType.BLANK);

                    cell = row.createCell(34);
                    cell.setCellValue(loan.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANBIF_LANDING_AB_TESTING.getKey()));

                    cell = row.createCell(35);
                    if (loan.getOfferRejectionReasonKey() != null)
                        cell.setCellValue(messageSource.getMessage(loan.getOfferRejectionReasonKey(), null, Configuration.getDefaultLocale()));
                    else
                        cell.setCellType(CellType.BLANK);

                    cell = row.createCell(36);
                    if (loan.getCompanyName() != null)
                        cell.setCellValue(loan.getCompanyName());
                    else
                        cell.setCellType(CellType.BLANK);

                    cell = row.createCell(37);
                    cell.setCellValue(loan.getSource());
                    cell = row.createCell(38);
                    cell.setCellValue(loan.getMedium());
                    cell = row.createCell(39);
                    cell.setCellValue(loan.getCampaign());

                    fillInBodyPreApprovedBase(Entity.BANBIF,40,row,cell,loan.getEntityCustomData());

                } else {
                    cell = row.createCell(0);
                    cell.setCellValue(index);

                    cell = row.createCell(1);
                    cell.setCellValue(loan.getCountry());

                    cell = row.createCell(2);
                    cell.setCellValue(loan.getLoanApplicationCode());
                    cell = row.createCell(3);
                    cell.setCellValue(loan.getRegisterDate());
                    cell.setCellStyle(cellStyleDate1);
                    cell = row.createCell(4);
                    cell.setCellValue(loan.getIdentityDocumentType() != null ? loan.getIdentityDocumentType().getName() : null);
                    cell = row.createCell(5);
                    cell.setCellValue(loan.getDocumentNumber());
                    cell = row.createCell(6);
                    cell.setCellValue(loan.getSurname());
                    cell = row.createCell(7);
                    cell.setCellValue(loan.getPersonName());
                    cell = row.createCell(8);
                    cell.setCellValue(loan.getProduct() != null ? loan.getProduct().getName() : null);
                    cell = row.createCell(9);
                    cell.setCellValue(loan.getLoanApplicationStatus() != null ? loan.getLoanApplicationStatus().getStatus() : null);
                    cell = row.createCell(10);
                    cell.setCellValue(loan.getLastStatusUpdate());
                    cell.setCellStyle(cellStyleDate1);
                    cell = row.createCell(11);
                    cell.setCellValue(loan.getCreditStatus() != null ? loan.getCreditStatus().getStatus() : null);
                    cell = row.createCell(12);
                    if (loan.getLoanCapital() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getLoanCapital());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(13);
                    if (loan.getPreEvaluationExecuted() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getPreEvaluationExecuted());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(14);
                    if (loan.getPreEvaluationApproved() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getPreEvaluationApproved());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(15);
                    if (loan.getApplicationCompleted() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getApplicationCompleted());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(16);
                    if (loan.getHasOffers() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getHasOffers());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(17);
                    if (loan.getHasSelectedOffer() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getHasSelectedOffer());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(18);
                    if (loan.getVerificationCompleted() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getVerificationCompleted());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(19);
                    if (loan.getApplicationApproved() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getApplicationApproved());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(20);
                    if (loan.getApplicationApprovedAndSigned() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getApplicationApprovedAndSigned());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(21);
                    if (loan.getIsDisbursed() != null) {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(loan.getIsDisbursed());
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(22);
                    cell.setCellValue(loan.getFinalResult());
                    cell = row.createCell(23);
                    cell.setCellValue(loan.getRejectionInstance());
                    cell = row.createCell(24);
                    String hardFilterValues = loan.getHardFilterValues() != null ? loan.getHardFilterValues() : "";
                    String policyFilterValues = loan.getPolicyValues() != null ? loan.getPolicyValues() : "";
                    if (hardFilterValues.length() > 0 && policyFilterValues.length() > 0)
                        cell.setCellValue(hardFilterValues.concat(" - " + policyFilterValues));
                    else
                        cell.setCellValue(hardFilterValues.concat(policyFilterValues));

                    cell = row.createCell(25);
                    cell.setCellValue(loan.getReason() != null ? loan.getReason().getReason() : null);
                    cell = row.createCell(26);
                    if (loan.getAmount() != null)
                        cell.setCellValue(loan.getAmount());
                    else
                        cell.setCellType(CellType.BLANK);
                    cell = row.createCell(27);
                    if (loan.getInstallments() != null)
                        cell.setCellValue(loan.getInstallments());
                    else
                        cell.setCellType(CellType.BLANK);
                    cell = row.createCell(28);
                    cell.setCellValue(loan.getMaritalStatus() != null ? loan.getMaritalStatus().getStatus() : null);
                    cell = row.createCell(29);
                    if (loan.getDependents() != null)
                        cell.setCellValue(loan.getDependents());
                    else
                        cell.setCellType(CellType.BLANK);
                    cell = row.createCell(30);
                    cell.setCellValue(loan.getUbigeo() != null ? loan.getUbigeo().getDepartment().getName() : null);
                    cell = row.createCell(31);
                    cell.setCellValue(loan.getUbigeo() != null ? loan.getUbigeo().getProvince().getName() : null);
                    cell = row.createCell(32);
                    cell.setCellValue(loan.getUbigeo() != null ? loan.getUbigeo().getDistrict().getName() : null);
                    cell = row.createCell(33);
                    cell.setCellValue(loan.getHousingType() != null ? loan.getHousingType().getType() : null);
                    cell = row.createCell(34);
                    cell.setCellValue(loan.getStudyLevel() != null ? loan.getStudyLevel().getLevel() : null);
                    cell = row.createCell(35);
                    cell.setCellValue(loan.getProfession() != null ? loan.getProfession().getProfession() : null);
                    cell = row.createCell(36);
                    cell.setCellValue(loan.getActivityType() != null ? loan.getActivityType().getType() : null);
                    cell = row.createCell(37);
                    if (loan.getEmploymentTime() != null)
                        cell.setCellValue(loan.getEmploymentTime());
                    else
                        cell.setCellType(CellType.BLANK);
                    cell = row.createCell(38);
                    if (loan.getIncome() != null)
                        cell.setCellValue(loan.getIncome());
                    else
                        cell.setCellType(CellType.BLANK);
                    cell = row.createCell(39);
                    if (loan.getVariableGrossIncome() != null)
                        cell.setCellValue(loan.getVariableGrossIncome());
                    else
                        cell.setCellType(CellType.BLANK);
                    cell = row.createCell(40);
                    cell.setCellValue(loan.getRuc());
                    cell = row.createCell(41);
                    cell.setCellValue(loan.getOcupation() != null ? loan.getOcupation().getOcupation() : null);
                    cell = row.createCell(42);
                    if (loan.getCompanyName() != null)
                        cell.setCellValue(loan.getOperadoresTelefonicos());
                    else
                        cell.setCellType(CellType.BLANK);
                    cell = row.createCell(43);
                    cell.setCellValue(utilService.yearsBetween(loan.getBirthday(), new Date()));
                    cell = row.createCell(44);
                    cell.setCellValue(loan.getPhoneNumber());
                    cell = row.createCell(45);
                    cell.setCellValue(loan.getEmail());
                    cell = row.createCell(46);
                    cell.setCellValue(loan.getGender());
                    cell = row.createCell(47);
                    cell.setCellValue(loan.getNationality() != null ? loan.getNationality().getName() : null);
                    cell = row.createCell(48);
                    cell.setCellValue(loan.getSocialNetworks());

                    if (loan.getReferrals() != null && loan.getReferrals().size() > 0) {
                        cell = row.createCell(49);
                        if (loan.getReferrals().get(0) != null)
                            cell.setCellValue(loan.getReferrals().get(0).getRelationship().getRelationship().concat(" - ").concat(loan.getReferrals().get(0).getPhoneNumber()));
                        else
                            cell.setCellType(CellType.BLANK);
                        cell = row.createCell(50);
                        if (loan.getReferrals().size() > 1) {
                            if (loan.getReferrals().get(1) != null)
                                cell.setCellValue(loan.getReferrals().get(1).getRelationship().getRelationship().concat(" - ").concat(loan.getReferrals().get(1).getPhoneNumber()));
                            else
                                cell.setCellType(CellType.BLANK);
                        }
                    } else {
                        cell = row.createCell(49);
                        cell.setCellType(CellType.BLANK);
                        cell = row.createCell(50);
                        cell.setCellType(CellType.BLANK);
                    }
                    cell = row.createCell(51);
                    String bank = "";
                    if (loan.getBank() != null)
                        bank = loan.getBank().getName().concat(" - ").concat(loan.getBankAccount() != null ? loan.getBankAccount() : "");
                    cell.setCellValue(bank);
                    cell = row.createCell(52);
                    if (loan.getSbsMonthlyInstallment() != null)
                        cell.setCellValue(loan.getSbsMonthlyInstallment());
                    else
                        cell.setCellType(CellType.BLANK);
                    cell = row.createCell(53);
                    if (loan.getLoanApplicationRCI() != null)
                        cell.setCellValue(loan.getLoanApplicationRCI());
                    else
                        cell.setCellType(CellType.BLANK);
                    cell = row.createCell(54);
                    if (loan.getCreditRCI() != null)
                        cell.setCellValue(loan.getCreditRCI());
                    else
                        cell.setCellType(CellType.BLANK);
                    cell = row.createCell(55);
                    if (loan.getMonthlyInstallment() != null) cell.setCellValue(loan.getMonthlyInstallment());
                    else cell.setCellType(CellType.BLANK);
                    cell = row.createCell(56);
                    if (loan.getTotalDebt() != null) cell.setCellValue(loan.getTotalDebt());
                    else cell.setCellType(CellType.BLANK);
                }
                index++;
            }
        }

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();
            return outStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] createFunnelV3Report(Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate, Date startDate2, Date endDate2, Integer[] countryIds, Integer[] entityIds, Integer[] products,Integer[] steps, List<String> utmSources,List<String> utmMedium,List<String> utmCampaign,List<String> utmContent,List<Integer> entityProductParams) throws Exception {
        List<ReportLoans> loans = creditDao.getLoansLightReportFunnel(minAge, maxAge, requestType, cardType, startDate, endDate, startDate2, endDate2, Configuration.getDefaultLocale(), countryIds, entityIds, products, steps,
                utmSources,
                utmMedium,
                utmCampaign,
                utmContent,
                entityProductParams
                );
        Product product = products.length > 0 ? catalogService.getProduct(products[0]) : null;
        return createFunnelV3Report(loans, entityIds.length > 0 ? entityIds[0] : null, product);
    }

    @Override
    public ReportProces createReport(Integer[] loanIds, Integer entityId, int userId, Integer report, Integer tray, Date startDate, Date endDate, String search,Integer[] products,Integer[] steps, Boolean isPaymentCommitment, Boolean isRejectedTrayReport ) throws Exception {
        JSONObject params = new JSONObject();
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        params.put("query", search);
        params.put("entity", entityId);
        params.put("tray", tray);
        params.put("products", products);
        params.put("steps", steps);
        params.put("loanIds", loanIds);
        params.put("isPaymentCommitment", isPaymentCommitment);
        params.put("isRejectedTrayReport", isRejectedTrayReport);

        Integer reportProcessId = reportsDao.registerReportProces(report, userId, params);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcessId);
        reportProces.setReportId(report);
        reportProces.setUserId(userId);
        reportProces.setParams(params);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    private byte[] createFunnelV3Report(List<ReportLoans> loans, Integer entityId, Product product) throws Exception {

        if(entityId != null && entityId == Entity.BANCO_DEL_SOL){
            setUserTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        }else setUserTimeZone(TimeZone.getTimeZone("America/Lima"));

        EntityExtranetConfiguration extranetConfiguration = entityId != null ? brandingService.getExtranetBrandingAsJson(entityId) : null;
        EntityExtranetConfiguration.FunnelConfiguration funnelConfiguration = null;
        if(extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && product != null && product.getProductCategoryId() != null) funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId() == product.getProductCategoryId()).findFirst().orElse(null);
        List<FunnelStep> funnelSteps = catalogDAO.getFunnelSteps();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Reporte Funnel");

        //format to dates
        CellStyle cellStyleDate1 = workbook.createCellStyle();
        CreationHelper createHelperDate1 = workbook.getCreationHelper();
        cellStyleDate1.setDataFormat(
                createHelperDate1.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

        CellStyle cellBgColorStyle = workbook.createCellStyle();
        cellBgColorStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());

        Row headerRow;
        Cell cell;

        int rowNum = 0;
        headerRow = sheet.createRow(rowNum++);
        cell = headerRow.createCell(0);
        cell.setCellValue("#");
        cell = headerRow.createCell(1);
        cell.setCellValue("País");
        cell = headerRow.createCell(2);
        cell.setCellValue("Application Number");
        cell = headerRow.createCell(3);
        cell.setCellValue("Fecha y hora");
        cell = headerRow.createCell(4);
        cell.setCellValue("Tipo de Documento");
        cell = headerRow.createCell(5);
        cell.setCellValue("Número de Documento");
        cell = headerRow.createCell(6);
        cell.setCellValue("Apellidos");
        cell = headerRow.createCell(7);
        cell.setCellValue("Nombres");
        cell = headerRow.createCell(8);
        cell.setCellValue("Edad");

        cell = headerRow.createCell(9);
        cell.setCellValue("Celular");
        cell = headerRow.createCell(10);
        cell.setCellValue("Correo");

        cell = headerRow.createCell(11);
        cell.setCellValue("Producto");
        cell = headerRow.createCell(12);
        cell.setCellValue("Sub-producto");
        cell = headerRow.createCell(13);
        cell.setCellValue("Tipo");
        cell = headerRow.createCell(14);
        cell.setCellValue("Departamento");
        cell = headerRow.createCell(15);
        cell.setCellValue("Provincia");
        cell = headerRow.createCell(16);
        cell.setCellValue("Distrito");
        cell = headerRow.createCell(17);
        cell.setCellValue("Estado de la solicitud");
        cell = headerRow.createCell(18);
        cell.setCellValue("Fecha último estado");
        cell = headerRow.createCell(19);
        cell.setCellValue("Estado");
        cell = headerRow.createCell(20);
        cell.setCellValue("Monto");

        int startStepsData = 21;
        int endStepsData = 21;

        //
        //STEPS
        //
        int countSteps = 0;
        if(funnelConfiguration != null && funnelConfiguration.getSteps() != null && !funnelConfiguration.getSteps().isEmpty()){
            for (EntityExtranetConfiguration.FunnelStep step : funnelConfiguration.getSteps()) {
                if(step.getStepId() == null) continue;
                FunnelStep stepConfiguration = funnelSteps.stream().filter(e -> e.getId() == step.getStepId()).findFirst().orElse(null);
                if(stepConfiguration == null) continue;
                countSteps++;
                cell = headerRow.createCell(endStepsData);
                cell.setCellValue(String.format("%s - %s", countSteps , step.getName() != null ? step.getName() : stepConfiguration.getName()));
                endStepsData++;
            }
        }
        //
        // END STEPS
        //
        cell = headerRow.createCell(endStepsData);
        cell.setCellValue("Resultado Final");
        cell = headerRow.createCell(endStepsData+1);
        cell.setCellValue("Instancia de Rechazo");
        cell = headerRow.createCell(endStepsData+2);
        cell.setCellValue("Motivos de Rechazo");
        cell = headerRow.createCell(endStepsData+3);
        cell.setCellValue("Motivo del Préstamo");
        cell = headerRow.createCell(endStepsData+4);
        cell.setCellValue("Monto Solicitado");
        cell = headerRow.createCell(endStepsData+5);
        cell.setCellValue("Plazo Solicitado");
        cell = headerRow.createCell(endStepsData+6);
        cell.setCellValue("Proceso A/B Testing");
        cell = headerRow.createCell(endStepsData+7);
        cell.setCellValue("Motivo - No la quiero");
        cell = headerRow.createCell(endStepsData+8);
        cell.setCellValue("UTM Source");
        cell = headerRow.createCell(endStepsData+9);
        cell.setCellValue("UTM Medium");
        cell = headerRow.createCell(endStepsData+10);
        cell.setCellValue("UTM Campaign");


        if (loans != null) {
            int index = 1;
            for (ReportLoans loan : loans) {
                Row row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(index);
                cell = row.createCell(1);
                cell.setCellValue(loan.getCountry());
                cell = row.createCell(2);
                cell.setCellValue(loan.getLoanApplicationCode());
                cell = row.createCell(3);
                cell.setCellValue(loan.getRegisterDate());
                cell.setCellStyle(cellStyleDate1);
                cell = row.createCell(4);
                cell.setCellValue(loan.getIdentityDocumentType() != null ? loan.getIdentityDocumentType().getName() : null);
                cell = row.createCell(5);
                cell.setCellValue(loan.getDocumentNumber());
                cell = row.createCell(6);
                cell.setCellValue(loan.getSurname());
                cell = row.createCell(7);
                cell.setCellValue(loan.getPersonName());
                cell = row.createCell(8);
                cell.setCellValue(utilService.yearsBetween(loan.getBirthday(), new Date()));
                cell = row.createCell(9);
                cell.setCellValue(loan.getPhoneNumber());
                cell = row.createCell(10);
                cell.setCellValue(loan.getEmail());
//                cell.setCellValue("Producto");
                cell = row.createCell(11);
                cell.setCellValue(loan.getProduct() != null ? loan.getProduct().getName() : null);
                //SUBPRODUCTO
                cell = row.createCell(12);
                if(loan.getEntityProductParam() != null && Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(loan.getEntityProductParam().getId())){
                    if(loan.getSelectedOfferBankAccountDataType() != null)
                        if(loan.getSelectedOfferBankAccountDataType() == BankAccountOfferData.TRADITIONAL_TYPE)
                            cell.setCellValue(BankAccountOfferData.TRADITIONAL_NAME);
                        else if(loan.getSelectedOfferBankAccountDataType() == BankAccountOfferData.HIGH_PROFITABILITY_TYPE)
                            cell.setCellValue(BankAccountOfferData.HIGH_PROFITABILITY_NAME);
                    else
                        cell.setCellValue((String) null);
                }else{
                    cell.setCellValue(loan.getEntityProductParam() != null ? loan.getEntityProductParam().getEntityProduct() : null);
                }
//                cell.setCellValue("Tipo");
                cell = row.createCell(13);
                cell.setCellValue(loan.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANBIF_TIPO_TARJETA_DE_BASE.getKey()));
//                cell.setCellValue("Departamento");
                cell = row.createCell(14);
                cell.setCellValue(loan.getUbigeo() != null ? loan.getUbigeo().getDepartment().getName() : null);
//                cell.setCellValue("Provincia");
                cell = row.createCell(15);
                cell.setCellValue(loan.getUbigeo() != null ? loan.getUbigeo().getProvince().getName() : null);
//                cell.setCellValue("Distrito");
                cell = row.createCell(16);
                cell.setCellValue(loan.getUbigeo() != null ? loan.getUbigeo().getDistrict().getName() : null);
                //Fecha último estado
                cell = row.createCell(17);
                cell.setCellValue(loan.getLoanApplicationStatus() != null ? loan.getLoanApplicationStatus().getStatus() : null);
                //Estado de la solicitud"
                cell = row.createCell(18);
                cell.setCellValue(loan.getLastStatusUpdate());
                cell.setCellStyle(cellStyleDate1);
                //Estado
                cell = row.createCell(19);
                if (loan.getCreditStatus() != null) {
                    if (loan.getCreditStatus().getId().intValue() == CreditStatus.ORIGINATED) {
                        if (loan.getProduct() != null && Arrays.asList(Product.TARJETA_CREDITO).contains(loan.getProduct().getId()))
                            cell.setCellValue("Por entregar");
                        else
                            cell.setCellValue("Originado (inactivo)");
                    }else if (loan.getCreditStatus().getId().intValue() == CreditStatus.ORIGINATED_DISBURSED) {
                        if (loan.getProduct() != null && Arrays.asList(Product.TARJETA_CREDITO).contains(loan.getProduct().getId()))
                            cell.setCellValue("Entregado");
                        else
                            cell.setCellValue("Originado (Activo)");
                    }else if (loan.getCreditStatus().getId().intValue() == CreditStatus.REJECTED)
                        cell.setCellValue("Cancelado");
                    else if (loan.getCreditStatus().getId().intValue() == CreditStatus.PENDING_PAYMENT)
                        cell.setCellValue("Pendiente de pago");
                    else if (loan.getCreditStatus().getId().intValue() == CreditStatus.PAYED)
                        cell.setCellValue("Pagado");
                    else if (loan.getCreditStatus().getId().intValue() == CreditStatus.PENDING_CONFIRMATION_BT)
                        cell.setCellValue("Pendiente de confirmación");
                    else if (loan.getCreditStatus().getId().intValue() == CreditStatus.PAYED_INFORMED)
                        cell.setCellValue("Pago informado");
                    else
                        cell.setCellType(CellType.BLANK);
                } else {
                    cell.setCellType(CellType.BLANK);
                }
                //LINEA MAXIMA
                cell = row.createCell(20);
                if(entityId == Entity.BANBIF && loan.getBanbifBaseDataAsInteger("linea") != null){
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getBanbifBaseDataAsInteger("linea"));
                }
                else if(entityId == Entity.AZTECA && loan.getAztecaCobranzaRecoveryDataAsDouble("montoCampania") != null){
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getAztecaCobranzaRecoveryDataAsDouble("montoCampania"));
                }
                else if(entityId == Entity.AZTECA && loan.getAztecaCobranzaVigenteDataAsDouble("monto") != null){
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getAztecaCobranzaVigenteDataAsDouble("monto"));
                }
                else {
                    cell.setCellType(CellType.BLANK);
                }

                if(countSteps >0){
                    Integer starStepsCells = startStepsData;
                    for (EntityExtranetConfiguration.FunnelStep step : funnelConfiguration.getSteps()) {
                        if(step.getStepId() == null) continue;
                        FunnelStep stepConfiguration = funnelSteps.stream().filter(e -> e.getId() == step.getStepId()).findFirst().orElse(null);
                        if(stepConfiguration == null) continue;
                        cell = row.createCell(starStepsCells);
                        switch (step.getStepId()){
                            case FunnelStep.REGISTERED:
                                if(loan.getStep1() != null) cell.setCellValue(loan.getStep1());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.PRE_EVALUATION_APPROVED:
                                if(loan.getStep2() != null)cell.setCellValue(loan.getStep2());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.PIN_VALIDATED:
                                if(loan.getStep3() != null) cell.setCellValue(loan.getStep3());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.APPROVED_VALIDATION:
                                if(loan.getStep4() != null) cell.setCellValue(loan.getStep4());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.REQUEST_COMPLETE:
                                if(loan.getStep5() != null) cell.setCellValue(loan.getStep5());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.REQUEST_WITH_OFFER:
                                if(loan.getStep6() != null) cell.setCellValue(loan.getStep6());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.ACCEPTED_OFFER:
                                if(loan.getStep7() != null) cell.setCellValue(loan.getStep7());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.VALIDATION:
                                if(loan.getStep8() != null) cell.setCellValue(loan.getStep8());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.SIGNATURE:
                                if(loan.getStep9() != null) cell.setCellValue(loan.getStep9());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.VERIFICATION:
                                if(loan.getStep10() != null) cell.setCellValue(loan.getStep10());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.APPROBATION:
                                if(loan.getStep11() != null) cell.setCellValue(loan.getStep11());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.DISBURSEMENT:
                                if(loan.getStep12() != null) cell.setCellValue(loan.getStep12());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.DISBURSED:
                                if(loan.getStep13() != null) cell.setCellValue(loan.getStep13());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.HOUSING_ADDRESS:
                                if(loan.getStep14() != null) cell.setCellValue(loan.getStep14());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.REQUEST_FINALIZED:
                                if(loan.getStep15() != null) cell.setCellValue(loan.getStep15());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.LEAD_REERRED:
                                if(loan.getStep16() != null) cell.setCellValue(loan.getStep16());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.COMMITMENT_GENERATED:
                                if(loan.getStep17() != null) cell.setCellValue(loan.getStep17());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.COMMITMENT_PAID:
                                if(loan.getStep18() != null) cell.setCellValue(loan.getStep18());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.COMMITMENT_PENDING_CONFIRMATION:
                                if(loan.getStep19() != null) cell.setCellValue(loan.getStep19());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.COMMITMENT_PAYMENT_INFORMED:
                                if(loan.getStep20() != null) cell.setCellValue(loan.getStep20());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.PRE_LOAN_APPLICATION_REGISTER:
                                if(loan.getStep21() != null) cell.setCellValue(loan.getStep21());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.REGISTERED_ALFIN:
                                if(loan.getStep22() != null) cell.setCellValue(loan.getStep22());
                                else cell.setCellType(CellType.BLANK);
                                break;
                        }
                        starStepsCells++;
                    }
                }
                //
                // END STEPS
                //
//              Resultado Final
                cell = row.createCell(endStepsData);
                cell.setCellValue(loan.getFinalResult());
//              Instancia de Rechazo
                cell = row.createCell(endStepsData+1);
                cell.setCellValue(loan.getRejectionInstance());
//              Motivos de Rechazo
                cell = row.createCell(endStepsData+2);
                if(entityId != null && entityId == Entity.BANBIF){
                    if (loan.getLoanApplicationStatus() != null) {
                        if (loan.getLoanApplicationStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC) {
                            String hardFilterValues = loan.getHardFilterValues() != null ? loan.getHardFilterValues() : "";
                            if (hardFilterValues.length() > 0)
                                cell.setCellValue(hardFilterValues.split(",")[0]);

                        } else if (loan.getLoanApplicationStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION) {
                            String policyFilterValues = loan.getPolicyValues() != null ? loan.getPolicyValues() : "";
                            if (policyFilterValues.length() > 0)
                                cell.setCellValue(policyFilterValues.split(",")[0]);
                        } else {
                            cell.setCellType(CellType.BLANK);
                        }
                    } else {
                        cell.setCellType(CellType.BLANK);
                    }
                }
                else{
                    if(loan.getIsCredit() != null && !loan.getIsCredit()){
                        if(Arrays.asList(LoanApplicationStatus.REJECTED_AUTOMATIC,LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION,LoanApplicationStatus.REJECTED).contains(loan.getLoanApplicationStatus().getId()))
                            cell.setCellValue(loan.getApplicationRejectionReason() != null ? loan.getApplicationRejectionReason().getReason() : (loan.getHardFilterOrPolicyMessageToShow() != null ? loan.getHardFilterOrPolicyMessageToShow() : (loan.getApplicationRejectionReason() != null ? loan.getApplicationRejectionReason().getReason() : "")));
                    }
                    else if(loan.getIsCredit() == null || loan.getIsCredit()){
                        if(loan.getIsCredit() == null){
                            String hardFilterValues = loan.getHardFilterValues() != null ? loan.getHardFilterValues() : "";
                            String policyFilterValues = loan.getPolicyValues() != null ? loan.getPolicyValues() : "";
                            if (hardFilterValues.length() > 0 && policyFilterValues.length() > 0)
                                cell.setCellValue(hardFilterValues.concat(" - " + policyFilterValues));
                            else
                                cell.setCellValue(hardFilterValues.concat(policyFilterValues));
                        }
                        else{
                            cell.setCellValue(loan.getRejectionReason() != null ? loan.getRejectionReason().getReason() : "");
                        }
                    }
                }
//              Motivo del Préstamo
                cell = row.createCell(endStepsData+3);
                if(entityId != null && entityId == Entity.BANBIF) cell.setCellType(CellType.BLANK);
                else cell.setCellValue(loan.getReason() != null ? loan.getReason().getReason() : null);
//              Monto Solicitado
                cell = row.createCell(endStepsData+4);
                if (loan.getAmount() != null)
                    cell.setCellValue(loan.getAmount());
                else
                    cell.setCellType(CellType.BLANK);
//                Plazo Solicitado
                cell = row.createCell(endStepsData+5);
                if (loan.getInstallments() != null)
                    cell.setCellValue(loan.getInstallments());
                else
                    cell.setCellType(CellType.BLANK);
                //"Proceso A/B Testing"
                cell = row.createCell(endStepsData+6);
                if(entityId != null && entityId == Entity.BANBIF) cell.setCellValue(loan.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANBIF_LANDING_AB_TESTING.getKey()));
                else if(entityId != null && entityId == Entity.AZTECA) cell.setCellValue(loan.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_LANDING_AB_TESTING.getKey()));
                else cell.setCellType(CellType.BLANK);
                //REJECT REASON
                cell = row.createCell(endStepsData+7);
                if (loan.getOfferRejectionReasonKey() != null)
                    cell.setCellValue(messageSource.getMessage(loan.getOfferRejectionReasonKey(), null, Configuration.getDefaultLocale()));
                else
                    cell.setCellType(CellType.BLANK);
                //SOURCE
                cell = row.createCell(endStepsData+8);
                cell.setCellValue(loan.getSource());
                //MEDIUM
                cell = row.createCell(endStepsData+9);
                cell.setCellValue(loan.getMedium());
                //CAMPAIGN
                cell = row.createCell(endStepsData+10);
                cell.setCellValue(loan.getCampaign());

                index++;
            }
        }

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();
            return outStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public byte[] createRejectedTrayReport(Integer entity,Integer[] loanIds, Integer[] products, Integer[] steps) throws Exception {
        List<ReportEntityExtranetTrayReport> loans = creditDao.getExtranetEntityLoansForReport(loanIds, Configuration.getDefaultLocale());
        Product product = products !=null && products.length > 0 ? catalogService.getProduct(products[0]) : null;
        return createRejectedTrayReport(loans, entity, product);
    }

    private byte[] createRejectedTrayReport(List<ReportEntityExtranetTrayReport> loans, Integer entityId, Product product) throws Exception {
//        setUserTimeZone(TimeZone.getTimeZone("GMT/UTC"));

        if(entityId != null && entityId == Entity.BANCO_DEL_SOL){
            setUserTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        }else setUserTimeZone(TimeZone.getTimeZone("America/Lima"));

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Reporte");

        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId);
        EntityExtranetConfiguration.FunnelConfiguration funnelConfiguration = null;
        if(extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && product != null && product.getProductCategoryId() != null) funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId() == product.getProductCategoryId()).findFirst().orElse(null);
        List<FunnelStep> funnelSteps = catalogDAO.getFunnelSteps();

        //format to dates
        CellStyle cellStyleDate1 = workbook.createCellStyle();
        CreationHelper createHelperDate1 = workbook.getCreationHelper();
        cellStyleDate1.setDataFormat(
                createHelperDate1.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

        CellStyle cellBgColorStyle = workbook.createCellStyle();

        Row headerRow;
        Cell cell;

        int rowNum = 0;
        headerRow = sheet.createRow(rowNum++);
        cell = headerRow.createCell(0);
        cell.setCellValue("#");
        cell = headerRow.createCell(1);
        cell.setCellValue("País");
        cell = headerRow.createCell(2);
        cell.setCellValue("Loan application");
        cell = headerRow.createCell(3);
        cell.setCellValue("Fecha de registro");
        cell = headerRow.createCell(4);
        cell.setCellValue("Tipo");
        cell = headerRow.createCell(5);
        cell.setCellValue("Documento");
        cell = headerRow.createCell(6);
        cell.setCellValue("Apellidos");
        cell = headerRow.createCell(7);
        cell.setCellValue("Nombres");
        cell = headerRow.createCell(8);
        cell.setCellValue("Fecha de nacimiento");
        cell = headerRow.createCell(9);
        cell.setCellValue("Edad");
        cell = headerRow.createCell(10);
        cell.setCellValue("Género");
        cell = headerRow.createCell(11);
        cell.setCellValue("Nacionalidad");
        cell = headerRow.createCell(12);
        cell.setCellValue("Estado civil");
        cell = headerRow.createCell(13);
        cell.setCellValue("Dependientes");
        cell = headerRow.createCell(14);
        cell.setCellValue("Nivel de estudios");
        cell = headerRow.createCell(15);
        cell.setCellValue("Profesión");

        cell = headerRow.createCell(16);
        cell.setCellValue("Sector Económico");
        cell = headerRow.createCell(17);
        cell.setCellValue("Cargo");

        cell = headerRow.createCell(18);
        cell.setCellValue("Banco Cta");
        cell = headerRow.createCell(19);
        cell.setCellValue("Nro Cta Bria");
        cell = headerRow.createCell(20);
        cell.setCellValue("CCI");

        cell = headerRow.createCell(21);
        cell.setCellValue("Condición de la vivienda");
        cell = headerRow.createCell(22);
        cell.setCellValue("Vivienda - Domicilio");
        cell = headerRow.createCell(23);
        cell.setCellValue("Vivienda - Departamento");
        cell = headerRow.createCell(24);
        cell.setCellValue("Vivienda - Provincia");
        cell = headerRow.createCell(25);
        cell.setCellValue("Vivienda - Distrito");
        cell = headerRow.createCell(26);
        cell.setCellValue("Latitud navegador");
        cell = headerRow.createCell(27);
        cell.setCellValue("Longitud navegador");
        cell = headerRow.createCell(28);
        cell.setCellValue("Tipo de Empleo");
        cell = headerRow.createCell(29);
        cell.setCellValue("Ocupación");
        cell = headerRow.createCell(30);
        cell.setCellValue("Antigüedad");
        cell = headerRow.createCell(31);
        cell.setCellValue("RUC empleador");
        cell = headerRow.createCell(32);
        cell.setCellValue("Razón social empleador");
        cell = headerRow.createCell(33);
        cell.setCellValue("Dirección empleador");
        cell = headerRow.createCell(34);
        cell.setCellValue("Ingresos");
        cell = headerRow.createCell(35);
        cell.setCellValue("PEP (Sí/No)");
        cell = headerRow.createCell(36);
        cell.setCellValue("Tipo Doc Cónyuge");
        cell = headerRow.createCell(37);
        cell.setCellValue("DNI cónyuge");
        cell = headerRow.createCell(38);
        cell.setCellValue("Nombre cónyuge");
        cell = headerRow.createCell(39);
        cell.setCellValue("Referencia 1 - Nombre");
        cell = headerRow.createCell(40);
        cell.setCellValue("Referencia 1 - Parentezco");
        cell = headerRow.createCell(41);
        cell.setCellValue("Referencia 1 - Teléfono");
        cell = headerRow.createCell(42);
        cell.setCellValue("Referencia 2 - Nombre");
        cell = headerRow.createCell(43);
        cell.setCellValue("Referencia 2 - Parentezco");
        cell = headerRow.createCell(44);
        cell.setCellValue("Referencia 2 - Teléfono");
        cell = headerRow.createCell(45);
        cell.setCellValue("Motivo del préstamo");
        cell = headerRow.createCell(46);
        cell.setCellValue("Monto solicitado");
        cell = headerRow.createCell(47);
        cell.setCellValue("Plazo solicitado");
        cell = headerRow.createCell(48);
        cell.setCellValue("Producto");
        cell = headerRow.createCell(49);
        cell.setCellValue("Monto seleccionado");
        cell = headerRow.createCell(50);
        cell.setCellValue("Plazo seleccionado");
        cell = headerRow.createCell(51);
        cell.setCellValue("Tasa seleccionada");
        cell = headerRow.createCell(52);
        cell.setCellValue("No le interesa");
        cell = headerRow.createCell(53);
        cell.setCellValue("Agencia seleccionada");
        cell = headerRow.createCell(54);
        cell.setCellValue("Último estado");
        cell = headerRow.createCell(55);
        cell.setCellValue("Estado de la solicitud");
        cell = headerRow.createCell(56);
        cell.setCellValue("Estado del crédito");
        cell = headerRow.createCell(57);
        cell.setCellValue("Tipo de desembolso");
        cell = headerRow.createCell(58);
        cell.setCellValue("Fecha de desembolso");
        cell = headerRow.createCell(59);
        cell.setCellValue("Motivo de Rechazo");

        int startStepsData = 60;
        int endStepsData = 60;
        //
        //STEPS
        //
        int countSteps = 0;
        if(funnelConfiguration != null && funnelConfiguration.getSteps() != null && !funnelConfiguration.getSteps().isEmpty()){
            for (EntityExtranetConfiguration.FunnelStep step : funnelConfiguration.getSteps()) {
                if(step.getStepId() == null) continue;
                FunnelStep stepConfiguration = funnelSteps.stream().filter(e -> e.getId() == step.getStepId()).findFirst().orElse(null);
                if(stepConfiguration == null) continue;
                countSteps++;
                cell = headerRow.createCell(endStepsData);
                cell.setCellValue(String.format("%s - %s", countSteps , step.getName() != null ? step.getName() : stepConfiguration.getName()));
                endStepsData++;
            }
        }
        //
        // END STEPS
        //
        cell = headerRow.createCell(endStepsData);
        cell.setCellValue("Teléfono");
        cell = headerRow.createCell(endStepsData+1);
        cell.setCellValue("Correo");

        //CABECERAS DE BASE PREAPROBADA
        fillInHeaderPreApprovedBase(entityId,endStepsData+2,headerRow,cell);
        SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHour =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (loans != null) {
            int index = 1;
            for (ReportEntityExtranetTrayReport loan : loans) {
                Row row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(index);
                //PARTE FIJA
                cell = row.createCell(1);
                if (loan.getCountry() != null)
                    cell.setCellValue(loan.getCountry().getName());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(2);
                cell.setCellValue(loan.getLoanApplicationCode());
                cell = row.createCell(3);
                cell.setCellValue(sdfHour.format(loan.getRegisterDate()));
                cell = row.createCell(4);
                cell.setCellValue(loan.getIdentityDocumentType() != null ? loan.getIdentityDocumentType().getName() : "");
                cell = row.createCell(5);
                cell.setCellValue(loan.getDocumentNumber());
                cell = row.createCell(6);
                cell.setCellValue(loan.getSurname());
                cell = row.createCell(7);
                cell.setCellValue(loan.getPersonName());
                cell = row.createCell(8);
                cell.setCellValue(loan.getBirthday() != null ? sdf.format(loan.getBirthday()) : "");
                cell = row.createCell(9);
                cell.setCellValue(utilService.yearsBetween(loan.getBirthday(), new Date()));
                cell = row.createCell(10);
                cell.setCellValue(loan.getGender());
                cell = row.createCell(11);
                cell.setCellValue(loan.getNationality() != null ? loan.getNationality().getName() : "");
                cell = row.createCell(12);
                cell.setCellValue(loan.getMaritalStatus() != null ? loan.getMaritalStatus().getStatus() : "");
                cell = row.createCell(13);
                if (loan.getDependents() != null)
                    cell.setCellValue(loan.getDependents());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(14);
                if (loan.getStudyLevel() != null)
                    cell.setCellValue(loan.getStudyLevel().getLevel());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(15);
                if (loan.getProfession() != null)
                    cell.setCellValue(loan.getProfession().getProfession());
                else
                    cell.setCellType(CellType.BLANK);

                cell = row.createCell(16);
                if(loan.getProfession() != null) cell.setCellValue(loan.getProfession().getProfession() != null ? loan.getProfession().getProfession() : "");
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(17);
                if(loan.getOcupation() != null) cell.setCellValue(loan.getOcupation().getOcupation() != null ? loan.getOcupation().getOcupation() : "");
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(18);
                if(loan.getBank() != null) cell.setCellValue(loan.getBank().getName() != null ? loan.getBank().getName() : "");
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(19);
                cell.setCellValue(loan.getBankAccountNUmber() != null ? loan.getBankAccountNUmber() : "");
                cell = row.createCell(20);
                cell.setCellValue(loan.getCci() != null ? loan.getCci() : "");

                cell = row.createCell(21);
                if (loan.getHousingType() != null)
                    cell.setCellValue(loan.getHousingType().getType());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(22);
                cell.setCellType(CellType.BLANK);
                if (loan.getStreetName() != null) cell.setCellValue(loan.getStreetName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(23);
                if (loan.getUbigeo() != null)
                    cell.setCellValue(loan.getUbigeo().getDepartment() != null ? loan.getUbigeo().getDepartment().getName() : "");
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(24);
                if (loan.getUbigeo() != null)
                    cell.setCellValue(loan.getUbigeo().getProvince() != null ? loan.getUbigeo().getProvince().getName() : "");
                else
                    cell.setCellType(CellType.BLANK);

                cell = row.createCell(25);
                if (loan.getUbigeo() != null)
                    cell.setCellValue(loan.getUbigeo().getDistrict() != null ? loan.getUbigeo().getDistrict().getName() : "");
                else
                    cell.setCellType(CellType.BLANK);

                cell = row.createCell(26);
                if (loan.getNavLatitude() != null) cell.setCellValue(loan.getNavLatitude());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(27);
                if (loan.getNavLongitude() != null) cell.setCellValue(loan.getNavLongitude());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(28);
                if (loan.getActivityType() != null)
                    cell.setCellValue(loan.getActivityType().getType());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(29);
                if (loan.getOcupation() != null)
                    cell.setCellValue(loan.getOcupation().getOcupation());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(30);
                if (loan.getEmploymentTime() != null){
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getEmploymentTime());
                }
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(31);
                if (loan.getRuc() != null) cell.setCellValue(loan.getRuc());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(32);
                if (loan.getCompanyName() != null) cell.setCellValue(loan.getCompanyName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(33);
                cell.setCellType(CellType.BLANK);
                cell = row.createCell(34);
                if (loan.getIncome() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getIncome());
                }
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(35);
                if (loan.getPep() != null) cell.setCellValue(loan.getPep());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(36);
                if (loan.getPartnerIdentityDocumentType() != null) cell.setCellValue(loan.getPartnerIdentityDocumentType().getName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(37);
                if (loan.getPartnerDocumentNumber() != null) cell.setCellValue(loan.getPartnerDocumentNumber());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(38);
                if (loan.getPartnerPersonName() != null) cell.setCellValue(loan.getPartnerPersonName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(39);
                if (loan.getReferrals() != null && loan.getReferrals().get(0) != null)
                    cell.setCellValue(loan.getReferrals().get(0).getFullName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(40);
                if (loan.getReferrals() != null && loan.getReferrals().get(0) != null)
                    cell.setCellValue(loan.getReferrals().get(0).getRelationship() != null ? loan.getReferrals().get(0).getRelationship().getRelationship() : "");
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(41);
                if (loan.getReferrals() != null && loan.getReferrals().get(0) != null && loan.getReferrals().get(0).getPhoneNumber() != null)
                    cell.setCellValue(loan.getReferrals().get(0).getPhoneNumber());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(42);
                if (loan.getReferrals() != null && loan.getReferrals().size() > 1 && loan.getReferrals().get(1) != null)
                    cell.setCellValue(loan.getReferrals().get(1).getFullName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(43);
                if (loan.getReferrals() != null && loan.getReferrals().size() > 1 && loan.getReferrals().get(1) != null)
                    cell.setCellValue(loan.getReferrals().get(1).getRelationship() != null ? loan.getReferrals().get(1).getRelationship().getRelationship() : "");
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(44);
                if (loan.getReferrals() != null && loan.getReferrals().size() > 1 && loan.getReferrals().get(1) != null && loan.getReferrals().get(1).getPhoneNumber() != null)
                    cell.setCellValue(loan.getReferrals().get(1).getPhoneNumber());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(45);
                if (loan.getReason() != null && loan.getReason().getReason() != null) cell.setCellValue(loan.getReason().getReason());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(46);
                if (loan.getAmount() != null) cell.setCellValue(loan.getAmount());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(47);
                if (loan.getInstallments() != null) cell.setCellValue(loan.getInstallments());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(48);
                if (loan.getProduct() != null) cell.setCellValue(loan.getProduct().getName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(49);
                if (loan.getSelectedAmount() != null) cell.setCellValue(loan.getSelectedAmount());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(50);
                if (loan.getSelectedInstallments() != null) cell.setCellValue(loan.getSelectedInstallments());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(51);
                if (loan.getSelectedEffectiveAnnualRate() != null) cell.setCellValue(loan.getSelectedEffectiveAnnualRate());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(52);
                cell.setCellType(CellType.BLANK);
                if (loan.getOfferRejectionReason() != null) cell.setCellValue(loan.getOfferRejectionReason().getReason());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(53);
                if (loan.getAgencyName() != null) cell.setCellValue(loan.getAgencyName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(54);
                if (loan.getLastStatusUpdate() != null) cell.setCellValue(sdfHour.format(loan.getLastStatusUpdate()));
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(55);
                if (loan.getApplicationStatus() != null) cell.setCellValue(loan.getApplicationStatus().getStatus());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(56);
                if (loan.getCreditStatus() != null) cell.setCellValue(loan.getCreditStatus().getStatus());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(57);
                if (loan.getDisbursementType() != null) cell.setCellValue(loan.getDisbursementType().getDisbursementType());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(58);
                if (loan.getDisbursementDate() != null) cell.setCellValue(sdfHour.format(loan.getDisbursementDate()));
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(59);
                cell.setCellValue((loan.getCreditCode() == null) ? (loan.getLoanApplicationRejectionReason() == null ? loan.getHardFilterOrPolicyMessageToShow() : loan.getLoanApplicationRejectionReason().getReason()) : (loan.getRejectionReason() != null ? loan.getRejectionReason().getReason() : (loan.getLoanApplicationRejectionReason() != null ? loan.getLoanApplicationRejectionReason().getReason() : "")));

                if(countSteps >0){
                    Integer starStepsCells = startStepsData;
                    for (EntityExtranetConfiguration.FunnelStep step : funnelConfiguration.getSteps()) {
                        if(step.getStepId() == null) continue;
                        FunnelStep stepConfiguration = funnelSteps.stream().filter(e -> e.getId() == step.getStepId()).findFirst().orElse(null);
                        if(stepConfiguration == null) continue;
                        cell = row.createCell(starStepsCells);
                        switch (step.getStepId()){
                            case FunnelStep.REGISTERED:
                                if(loan.getStep1() != null) cell.setCellValue(loan.getStep1());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.PRE_EVALUATION_APPROVED:
                                if(loan.getStep2() != null)cell.setCellValue(loan.getStep2());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.PIN_VALIDATED:
                                if(loan.getStep3() != null) cell.setCellValue(loan.getStep3());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.APPROVED_VALIDATION:
                                if(loan.getStep4() != null) cell.setCellValue(loan.getStep4());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.REQUEST_COMPLETE:
                                if(loan.getStep5() != null) cell.setCellValue(loan.getStep5());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.REQUEST_WITH_OFFER:
                                if(loan.getStep6() != null) cell.setCellValue(loan.getStep6());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.ACCEPTED_OFFER:
                                if(loan.getStep7() != null) cell.setCellValue(loan.getStep7());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.VALIDATION:
                                if(loan.getStep8() != null) cell.setCellValue(loan.getStep8());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.SIGNATURE:
                                if(loan.getStep9() != null) cell.setCellValue(loan.getStep9());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.VERIFICATION:
                                if(loan.getStep10() != null) cell.setCellValue(loan.getStep10());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.APPROBATION:
                                if(loan.getStep11() != null) cell.setCellValue(loan.getStep11());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.DISBURSEMENT:
                                if(loan.getStep12() != null) cell.setCellValue(loan.getStep12());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.DISBURSED:
                                if(loan.getStep13() != null) cell.setCellValue(loan.getStep13());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.HOUSING_ADDRESS:
                                if(loan.getStep14() != null) cell.setCellValue(loan.getStep14());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.REQUEST_FINALIZED:
                                if(loan.getStep15() != null) cell.setCellValue(loan.getStep15());
                                else cell.setCellType(CellType.BLANK);
                                break;
                        }
                        starStepsCells++;
                    }
                }
                cell = row.createCell(endStepsData);
                if (loan.getPhoneNumber() != null) cell.setCellValue(loan.getPhoneNumber());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(endStepsData+1);
                if (loan.getEmail() != null) cell.setCellValue(loan.getEmail());
                else cell.setCellType(CellType.BLANK);

                fillInBodyPreApprovedBase(entityId,endStepsData+2,row,cell,loan.getEntityCustomData());
                index++;
            }
        }

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();
            return outStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] createTrayExtranetReport(Integer entity,Integer[] loanIds, Integer[] products, Integer[] steps) throws Exception {
        List<ReportEntityExtranetTrayReport> loans = creditDao.getExtranetEntityLoansForReport(loanIds, Configuration.getDefaultLocale());
        Product product = products !=null && products.length > 0 ? catalogService.getProduct(products[0]) : null;
        return createTrayExtranetReport(loans, entity, product);
    }

    private byte[] createTrayExtranetReport(List<ReportEntityExtranetTrayReport> loans, Integer entityId, Product product) throws Exception {
//        setUserTimeZone(TimeZone.getTimeZone("GMT/UTC"));

        if(entityId != null && entityId == Entity.BANCO_DEL_SOL){
            setUserTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        }else setUserTimeZone(TimeZone.getTimeZone("America/Lima"));

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Reporte");

        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId);
        EntityExtranetConfiguration.FunnelConfiguration funnelConfiguration = null;
        if(extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && product != null && product.getProductCategoryId() != null) funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId() == product.getProductCategoryId()).findFirst().orElse(null);
        List<FunnelStep> funnelSteps = catalogDAO.getFunnelSteps();

        //format to dates
        CellStyle cellStyleDate1 = workbook.createCellStyle();
        CreationHelper createHelperDate1 = workbook.getCreationHelper();
        cellStyleDate1.setDataFormat(
                createHelperDate1.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

        CellStyle cellBgColorStyle = workbook.createCellStyle();

        Row headerRow;
        Cell cell;

        int rowNum = 0;
        headerRow = sheet.createRow(rowNum++);
        cell = headerRow.createCell(0);
        cell.setCellValue("#");
        cell = headerRow.createCell(1);
        cell.setCellValue("País");
        cell = headerRow.createCell(2);
        cell.setCellValue("Loan application");
        cell = headerRow.createCell(3);
        cell.setCellValue("Fecha de registro");
        cell = headerRow.createCell(4);
        cell.setCellValue("Tipo");
        cell = headerRow.createCell(5);
        cell.setCellValue("Documento");
        cell = headerRow.createCell(6);
        cell.setCellValue("Apellidos");
        cell = headerRow.createCell(7);
        cell.setCellValue("Nombres");
        cell = headerRow.createCell(8);
        cell.setCellValue("Fecha de nacimiento");
        cell = headerRow.createCell(9);
        cell.setCellValue("Edad");
        cell = headerRow.createCell(10);
        cell.setCellValue("Género");
        cell = headerRow.createCell(11);
        cell.setCellValue("Nacionalidad");
        cell = headerRow.createCell(12);
        cell.setCellValue("Estado civil");
        cell = headerRow.createCell(13);
        cell.setCellValue("Dependientes");
        cell = headerRow.createCell(14);
        cell.setCellValue("Nivel de estudios");
        cell = headerRow.createCell(15);
        cell.setCellValue("Profesión");

        cell = headerRow.createCell(16);
        cell.setCellValue("Sector Económico");
        cell = headerRow.createCell(17);
        cell.setCellValue("Cargo");

        cell = headerRow.createCell(18);
        cell.setCellValue("Banco Cta");
        cell = headerRow.createCell(19);
        cell.setCellValue("Nro Cta Bria");
        cell = headerRow.createCell(20);
        cell.setCellValue("CCI");

        cell = headerRow.createCell(21);
        cell.setCellValue("Condición de la vivienda");
        cell = headerRow.createCell(22);
        cell.setCellValue("Vivienda - Domicilio");
        cell = headerRow.createCell(23);
        cell.setCellValue("Vivienda - Departamento");
        cell = headerRow.createCell(24);
        cell.setCellValue("Vivienda - Provincia");
        cell = headerRow.createCell(25);
        cell.setCellValue("Vivienda - Distrito");
        cell = headerRow.createCell(26);
        cell.setCellValue("Latitud navegador");
        cell = headerRow.createCell(27);
        cell.setCellValue("Longitud navegador");
        cell = headerRow.createCell(28);
        cell.setCellValue("Tipo de Empleo");
        cell = headerRow.createCell(29);
        cell.setCellValue("Ocupación");
        cell = headerRow.createCell(30);
        cell.setCellValue("Antigüedad");
        cell = headerRow.createCell(31);
        cell.setCellValue("RUC empleador");
        cell = headerRow.createCell(32);
        cell.setCellValue("Razón social empleador");
        cell = headerRow.createCell(33);
        cell.setCellValue("Dirección empleador");
        cell = headerRow.createCell(34);
        cell.setCellValue("Ingresos");
        cell = headerRow.createCell(35);
        cell.setCellValue("PEP (Sí/No)");
        cell = headerRow.createCell(36);
        cell.setCellValue("Tipo Doc Cónyuge");
        cell = headerRow.createCell(37);
        cell.setCellValue("DNI cónyuge");
        cell = headerRow.createCell(38);
        cell.setCellValue("Nombre cónyuge");
        cell = headerRow.createCell(39);
        cell.setCellValue("Referencia 1 - Nombre");
        cell = headerRow.createCell(40);
        cell.setCellValue("Referencia 1 - Parentezco");
        cell = headerRow.createCell(41);
        cell.setCellValue("Referencia 1 - Teléfono");
        cell = headerRow.createCell(42);
        cell.setCellValue("Referencia 2 - Nombre");
        cell = headerRow.createCell(43);
        cell.setCellValue("Referencia 2 - Parentezco");
        cell = headerRow.createCell(44);
        cell.setCellValue("Referencia 2 - Teléfono");
        cell = headerRow.createCell(45);
        cell.setCellValue("Motivo del préstamo");
        cell = headerRow.createCell(46);
        cell.setCellValue("Monto solicitado");
        cell = headerRow.createCell(47);
        cell.setCellValue("Plazo solicitado");
        cell = headerRow.createCell(48);
        cell.setCellValue("Producto");
        cell = headerRow.createCell(49);
        cell.setCellValue("Monto seleccionado");
        cell = headerRow.createCell(50);
        cell.setCellValue("Plazo seleccionado");
        cell = headerRow.createCell(51);
        cell.setCellValue("Tasa seleccionada");
        cell = headerRow.createCell(52);
        cell.setCellValue("No le interesa");
        cell = headerRow.createCell(53);
        cell.setCellValue("Agencia seleccionada");
        cell = headerRow.createCell(54);
        cell.setCellValue("Último estado");
        cell = headerRow.createCell(55);
        cell.setCellValue("Estado de la solicitud");
        cell = headerRow.createCell(56);
        cell.setCellValue("Estado del crédito");
        cell = headerRow.createCell(57);
        cell.setCellValue("Tipo de desembolso");
        cell = headerRow.createCell(58);
        cell.setCellValue("Fecha de desembolso");

        int startStepsData = 58;
        int endStepsData = 58;
        //
        //STEPS
        //
        int countSteps = 0;
        if(funnelConfiguration != null && funnelConfiguration.getSteps() != null && !funnelConfiguration.getSteps().isEmpty()){
            for (EntityExtranetConfiguration.FunnelStep step : funnelConfiguration.getSteps()) {
                if(step.getStepId() == null) continue;
                FunnelStep stepConfiguration = funnelSteps.stream().filter(e -> e.getId() == step.getStepId()).findFirst().orElse(null);
                if(stepConfiguration == null) continue;
                countSteps++;
                cell = headerRow.createCell(endStepsData);
                cell.setCellValue(String.format("%s - %s", countSteps , step.getName() != null ? step.getName() : stepConfiguration.getName()));
                endStepsData++;
            }
        }
        //
        // END STEPS
        //
        cell = headerRow.createCell(endStepsData);
        cell.setCellValue("Teléfono");
        cell = headerRow.createCell(endStepsData+1);
        cell.setCellValue("Correo");

        //CABECERAS DE BASE PREAPROBADA
        fillInHeaderPreApprovedBase(entityId,endStepsData+2,headerRow,cell);
        SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHour =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (loans != null) {
            int index = 1;
            for (ReportEntityExtranetTrayReport loan : loans) {
                Row row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(index);
                //PARTE FIJA
                cell = row.createCell(1);
                if (loan.getCountry() != null)
                    cell.setCellValue(loan.getCountry().getName());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(2);
                cell.setCellValue(loan.getLoanApplicationCode());
                cell = row.createCell(3);
                cell.setCellValue(sdfHour.format(loan.getRegisterDate()));
                cell = row.createCell(4);
                cell.setCellValue(loan.getIdentityDocumentType() != null ? loan.getIdentityDocumentType().getName() : "");
                cell = row.createCell(5);
                cell.setCellValue(loan.getDocumentNumber());
                cell = row.createCell(6);
                cell.setCellValue(loan.getSurname());
                cell = row.createCell(7);
                cell.setCellValue(loan.getPersonName());
                cell = row.createCell(8);
                cell.setCellValue(loan.getBirthday() != null ? sdf.format(loan.getBirthday()) : "");
                cell = row.createCell(9);
                cell.setCellValue(utilService.yearsBetween(loan.getBirthday(), new Date()));
                cell = row.createCell(10);
                cell.setCellValue(loan.getGender());
                cell = row.createCell(11);
                cell.setCellValue(loan.getNationality() != null ? loan.getNationality().getName() : "");
                cell = row.createCell(12);
                cell.setCellValue(loan.getMaritalStatus() != null ? loan.getMaritalStatus().getStatus() : "");
                cell = row.createCell(13);
                if (loan.getDependents() != null)
                    cell.setCellValue(loan.getDependents());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(14);
                if (loan.getStudyLevel() != null)
                    cell.setCellValue(loan.getStudyLevel().getLevel());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(15);
                if (loan.getProfession() != null)
                    cell.setCellValue(loan.getProfession().getProfession());
                else
                    cell.setCellType(CellType.BLANK);

                cell = row.createCell(16);
                if(loan.getProfession() != null) cell.setCellValue(loan.getProfession().getProfession() != null ? loan.getProfession().getProfession() : "");
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(17);
                if(loan.getOcupation() != null) cell.setCellValue(loan.getOcupation().getOcupation() != null ? loan.getOcupation().getOcupation() : "");
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(18);
                if(loan.getBank() != null) cell.setCellValue(loan.getBank().getName() != null ? loan.getBank().getName() : "");
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(19);
                cell.setCellValue(loan.getBankAccountNUmber() != null ? loan.getBankAccountNUmber() : "");
                cell = row.createCell(20);
                cell.setCellValue(loan.getCci() != null ? loan.getCci() : "");

                cell = row.createCell(21);
                if (loan.getHousingType() != null)
                    cell.setCellValue(loan.getHousingType().getType());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(22);
                cell.setCellType(CellType.BLANK);
                if (loan.getStreetName() != null) cell.setCellValue(loan.getStreetName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(23);
                if (loan.getUbigeo() != null)
                    cell.setCellValue(loan.getUbigeo().getDepartment() != null ? loan.getUbigeo().getDepartment().getName() : "");
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(24);
                if (loan.getUbigeo() != null)
                    cell.setCellValue(loan.getUbigeo().getProvince() != null ? loan.getUbigeo().getProvince().getName() : "");
                else
                    cell.setCellType(CellType.BLANK);

                cell = row.createCell(25);
                if (loan.getUbigeo() != null)
                    cell.setCellValue(loan.getUbigeo().getDistrict() != null ? loan.getUbigeo().getDistrict().getName() : "");
                else
                    cell.setCellType(CellType.BLANK);

                cell = row.createCell(26);
                if (loan.getNavLatitude() != null) cell.setCellValue(loan.getNavLatitude());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(27);
                if (loan.getNavLongitude() != null) cell.setCellValue(loan.getNavLongitude());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(28);
                if (loan.getActivityType() != null)
                    cell.setCellValue(loan.getActivityType().getType());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(29);
                if (loan.getOcupation() != null)
                    cell.setCellValue(loan.getOcupation().getOcupation());
                else
                    cell.setCellType(CellType.BLANK);
                cell = row.createCell(30);
                if (loan.getEmploymentTime() != null){
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getEmploymentTime());
                }
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(31);
                if (loan.getRuc() != null) cell.setCellValue(loan.getRuc());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(32);
                if (loan.getCompanyName() != null) cell.setCellValue(loan.getCompanyName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(33);
                cell.setCellType(CellType.BLANK);
                cell = row.createCell(34);
                if (loan.getIncome() != null) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(loan.getIncome());
                }
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(35);
                if (loan.getPep() != null) cell.setCellValue(loan.getPep());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(36);
                if (loan.getPartnerIdentityDocumentType() != null) cell.setCellValue(loan.getPartnerIdentityDocumentType().getName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(37);
                if (loan.getPartnerDocumentNumber() != null) cell.setCellValue(loan.getPartnerDocumentNumber());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(38);
                if (loan.getPartnerPersonName() != null) cell.setCellValue(loan.getPartnerPersonName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(39);
                if (loan.getReferrals() != null && loan.getReferrals().get(0) != null)
                    cell.setCellValue(loan.getReferrals().get(0).getFullName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(40);
                if (loan.getReferrals() != null && loan.getReferrals().get(0) != null)
                    cell.setCellValue(loan.getReferrals().get(0).getRelationship() != null ? loan.getReferrals().get(0).getRelationship().getRelationship() : "");
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(41);
                if (loan.getReferrals() != null && loan.getReferrals().get(0) != null && loan.getReferrals().get(0).getPhoneNumber() != null)
                    cell.setCellValue(loan.getReferrals().get(0).getPhoneNumber());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(42);
                if (loan.getReferrals() != null && loan.getReferrals().size() > 1 && loan.getReferrals().get(1) != null)
                    cell.setCellValue(loan.getReferrals().get(1).getFullName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(43);
                if (loan.getReferrals() != null && loan.getReferrals().size() > 1 && loan.getReferrals().get(1) != null)
                    cell.setCellValue(loan.getReferrals().get(1).getRelationship() != null ? loan.getReferrals().get(1).getRelationship().getRelationship() : "");
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(44);
                if (loan.getReferrals() != null && loan.getReferrals().size() > 1 && loan.getReferrals().get(1) != null && loan.getReferrals().get(1).getPhoneNumber() != null)
                    cell.setCellValue(loan.getReferrals().get(1).getPhoneNumber());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(45);
                if (loan.getReason() != null && loan.getReason().getReason() != null) cell.setCellValue(loan.getReason().getReason());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(46);
                if (loan.getAmount() != null) cell.setCellValue(loan.getAmount());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(47);
                if (loan.getInstallments() != null) cell.setCellValue(loan.getInstallments());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(48);
                if (loan.getProduct() != null) cell.setCellValue(loan.getProduct().getName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(49);
                if (loan.getSelectedAmount() != null) cell.setCellValue(loan.getSelectedAmount());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(50);
                if (loan.getSelectedInstallments() != null) cell.setCellValue(loan.getSelectedInstallments());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(51);
                if (loan.getSelectedEffectiveAnnualRate() != null) cell.setCellValue(loan.getSelectedEffectiveAnnualRate());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(52);
                cell.setCellType(CellType.BLANK);
                if (loan.getOfferRejectionReason() != null) cell.setCellValue(loan.getOfferRejectionReason().getReason());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(53);
                if (loan.getAgencyName() != null) cell.setCellValue(loan.getAgencyName());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(54);
                if (loan.getLastStatusUpdate() != null) cell.setCellValue(sdfHour.format(loan.getLastStatusUpdate()));
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(55);
                if (loan.getApplicationStatus() != null) cell.setCellValue(loan.getApplicationStatus().getStatus());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(56);
                if (loan.getCreditStatus() != null) cell.setCellValue(loan.getCreditStatus().getStatus());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(57);
                if (loan.getDisbursementType() != null) cell.setCellValue(loan.getDisbursementType().getDisbursementType());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(58);
                if (loan.getDisbursementDate() != null) cell.setCellValue(sdfHour.format(loan.getDisbursementDate()));
                else cell.setCellType(CellType.BLANK);
                if(countSteps >0){
                    Integer starStepsCells = startStepsData;
                    for (EntityExtranetConfiguration.FunnelStep step : funnelConfiguration.getSteps()) {
                        if(step.getStepId() == null) continue;
                        FunnelStep stepConfiguration = funnelSteps.stream().filter(e -> e.getId() == step.getStepId()).findFirst().orElse(null);
                        if(stepConfiguration == null) continue;
                        cell = row.createCell(starStepsCells);
                        switch (step.getStepId()){
                            case FunnelStep.REGISTERED:
                                if(loan.getStep1() != null) cell.setCellValue(loan.getStep1());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.PRE_EVALUATION_APPROVED:
                                if(loan.getStep2() != null)cell.setCellValue(loan.getStep2());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.PIN_VALIDATED:
                                if(loan.getStep3() != null) cell.setCellValue(loan.getStep3());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.APPROVED_VALIDATION:
                                if(loan.getStep4() != null) cell.setCellValue(loan.getStep4());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.REQUEST_COMPLETE:
                                if(loan.getStep5() != null) cell.setCellValue(loan.getStep5());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.REQUEST_WITH_OFFER:
                                if(loan.getStep6() != null) cell.setCellValue(loan.getStep6());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.ACCEPTED_OFFER:
                                if(loan.getStep7() != null) cell.setCellValue(loan.getStep7());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.VALIDATION:
                                if(loan.getStep8() != null) cell.setCellValue(loan.getStep8());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.SIGNATURE:
                                if(loan.getStep9() != null) cell.setCellValue(loan.getStep9());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.VERIFICATION:
                                if(loan.getStep10() != null) cell.setCellValue(loan.getStep10());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.APPROBATION:
                                if(loan.getStep11() != null) cell.setCellValue(loan.getStep11());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.DISBURSEMENT:
                                if(loan.getStep12() != null) cell.setCellValue(loan.getStep12());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.DISBURSED:
                                if(loan.getStep13() != null) cell.setCellValue(loan.getStep13());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.HOUSING_ADDRESS:
                                if(loan.getStep14() != null) cell.setCellValue(loan.getStep14());
                                else cell.setCellType(CellType.BLANK);
                                break;
                            case FunnelStep.REQUEST_FINALIZED:
                                if(loan.getStep15() != null) cell.setCellValue(loan.getStep15());
                                else cell.setCellType(CellType.BLANK);
                                break;
                        }
                        starStepsCells++;
                    }
                }
                cell = row.createCell(endStepsData);
                if (loan.getPhoneNumber() != null) cell.setCellValue(loan.getPhoneNumber());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(endStepsData+1);
                if (loan.getEmail() != null) cell.setCellValue(loan.getEmail());
                else cell.setCellType(CellType.BLANK);

                fillInBodyPreApprovedBase(entityId,endStepsData+2,row,cell,loan.getEntityCustomData());
                index++;
            }
        }

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();
            return outStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] createTrayExtranetReportPaymentCommitment(Integer entity,Integer[] loanIds, Integer[] products, Integer[] steps) throws Exception {
        List<ReportEntityExtranetTrayReport> loans = creditDao.getExtranetEntityLoansForReport(loanIds, Configuration.getDefaultLocale());
        Product product = products !=null && products.length > 0 ? catalogService.getProduct(products[0]) : null;
        return createTrayExtranetReportPaymentCommitment(loans, entity, product);
    }

    private byte[] createTrayExtranetReportPaymentCommitment(List<ReportEntityExtranetTrayReport> loans, Integer entityId, Product product) throws Exception {
        setUserTimeZone(TimeZone.getTimeZone("GMT/UTC"));

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Reporte");

        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(entityId);
        EntityExtranetConfiguration.FunnelConfiguration funnelConfiguration = null;
        if(extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && product != null && product.getProductCategoryId() != null) funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId() == product.getProductCategoryId()).findFirst().orElse(null);
        List<FunnelStep> funnelSteps = catalogDAO.getFunnelSteps();

        //format to dates
        CellStyle cellStyleDate1 = workbook.createCellStyle();
        CreationHelper createHelperDate1 = workbook.getCreationHelper();
        cellStyleDate1.setDataFormat(
                createHelperDate1.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

        CellStyle cellBgColorStyle = workbook.createCellStyle();

        Row headerRow;
        Cell cell;

        int rowNum = 0;
        headerRow = sheet.createRow(rowNum++);
        cell = headerRow.createCell(0);
        cell.setCellValue("#");
        cell = headerRow.createCell(1);
        cell.setCellValue("Fecha registro");
        cell = headerRow.createCell(2);
        cell.setCellValue("Compromiso nro");
        cell = headerRow.createCell(3);
        cell.setCellValue("Tipo Doc.");
        cell = headerRow.createCell(4);
        cell.setCellValue("Documento");
        cell = headerRow.createCell(5);
        cell.setCellValue("Nombres");
        cell = headerRow.createCell(6);
        cell.setCellValue("Apellidos");
        cell = headerRow.createCell(7);
        cell.setCellValue("Celular");
        cell = headerRow.createCell(8);
        cell.setCellValue("Correo");
        cell = headerRow.createCell(9);
        cell.setCellValue("Monto Campaña");
        cell = headerRow.createCell(10);
        cell.setCellValue("Estado");
        cell = headerRow.createCell(11);
        cell.setCellValue("Medio de Pago");
        cell = headerRow.createCell(12);
        cell.setCellValue("Fecha de expiración Compromiso");
        cell = headerRow.createCell(13);
        cell.setCellValue("Código de Identificación de Pago");

        int startStepsData = 13;
        int endStepsData = 13;
        int countSteps = 0;

        //CABECERAS DE BASE
        fillInHeaderCollectionPreApprovedBase(entityId,endStepsData+1,headerRow,cell);
        SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHour =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (loans != null) {
            int index = 1;
            for (ReportEntityExtranetTrayReport loan : loans) {
                Row row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(index);
                cell = row.createCell(1);
                cell.setCellValue(sdfHour.format(loan.getRegisterDate()));
                cell = row.createCell(2);
                cell.setCellValue(loan.getCreditCode() != null ? loan.getCreditCode() : loan.getLoanApplicationCode());
                cell = row.createCell(3);
                cell.setCellValue(loan.getIdentityDocumentType() != null ? loan.getIdentityDocumentType().getName() : "");
                cell = row.createCell(4);
                cell.setCellValue(loan.getDocumentNumber());
                cell = row.createCell(5);
                cell.setCellValue(loan.getPersonName());
                cell = row.createCell(6);
                cell.setCellValue(loan.getSurname());
                cell = row.createCell(7);
                if (loan.getPhoneNumber() != null) cell.setCellValue(loan.getPhoneNumber());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(8);
                if (loan.getEmail() != null) cell.setCellValue(loan.getEmail());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(9);
                if (loan.getSelectedAmount() != null) cell.setCellValue(loan.getSelectedAmount());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(10);
                cell.setCellValue(loan.getCreditStatus() != null ? loan.getCreditStatus().getStatus() : (loan.getLoanApplicationStatus() != null ? loan.getLoanApplicationStatus().getStatus() : null));
                cell = row.createCell(11);
                cell.setCellType(CellType.BLANK);
                cell = row.createCell(12);
                cell.setCellValue(loan.getPaidCollectionPaymentMethod() != null && loan.getPaidCollectionPaymentMethod().getExpirationDate() != null ? sdfHour.format(loan.getPaidCollectionPaymentMethod().getExpirationDate()) : "");
                cell = row.createCell(13);
                if (loan.getPaidCollectionPaymentMethod() != null) cell.setCellValue(loan.getPaidCollectionPaymentMethod().getCip());
                else cell.setCellType(CellType.BLANK);

                fillInBodyCollectionPreApprovedBase(entityId,endStepsData+1,row,cell,loan);
                index++;
            }
        }

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();
            return outStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void fillInHeaderPreApprovedBase(Integer  entityId, Integer startIn,Row row, Cell cell){
        if(entityId == null) return;
        switch (entityId){
            case Entity.BANBIF:
                cell = row.createCell(startIn);
                cell.setCellValue("base_tipo_doc");
                cell = row.createCell(startIn+1);
                cell.setCellValue("base_numero_doc");
                cell = row.createCell(startIn+2);
                cell.setCellValue("base_plastico");
                cell = row.createCell(startIn+3);
                cell.setCellValue("base_linea");
                cell = row.createCell(startIn+4);
                cell.setCellValue("base_tipo_base");
                cell = row.createCell(startIn+5);
                cell.setCellValue("base_nombres");
                cell = row.createCell(startIn+6);
                cell.setCellValue("base_apellidos");
                cell = row.createCell(startIn+7);
                cell.setCellValue("base_canal");
                cell = row.createCell(startIn+8);
                cell.setCellValue("base_promocion_uso");
                cell = row.createCell(startIn+9);
                cell.setCellValue("base_promocion_aceptacion");
                break;
            case Entity.PRISMA:
                cell = row.createCell(startIn);
                cell.setCellValue("base_document_number");
                cell = row.createCell(startIn+1);
                cell.setCellValue("base_document_type");
                cell = row.createCell(startIn+2);
                cell.setCellValue("base_names");
                cell = row.createCell(startIn+3);
                cell.setCellValue("base_last_name");
                cell = row.createCell(startIn+4);
                cell.setCellValue("base_max_installments");
                cell = row.createCell(startIn+5);
                cell.setCellValue("base_max_amount");
                cell = row.createCell(startIn+6);
                cell.setCellValue("base_tea");
                break;
            case Entity.AZTECA:
                cell = row.createCell(startIn);
                cell.setCellValue("base_dni");
                cell = row.createCell(startIn+1);
                cell.setCellValue("base_x_appaterno");
                cell = row.createCell(startIn+2);
                cell.setCellValue("base_x_apmaterno");
                cell = row.createCell(startIn+3);
                cell.setCellValue("base_x_nombre");
                cell = row.createCell(startIn+4);
                cell.setCellValue("base_capacidad");
                cell = row.createCell(startIn+5);
                cell.setCellValue("base_oferta_max");
                cell = row.createCell(startIn+6);
                cell.setCellValue("base_plazo");
                cell = row.createCell(startIn+7);
                cell.setCellValue("base_idcampania");
                cell = row.createCell(startIn+8);
                cell.setCellValue("base_landing_pp");
                cell = row.createCell(startIn+9);
                cell.setCellValue("base_landing_cc");
                cell = row.createCell(startIn+10);
                cell.setCellValue("base_landing_moto");
                cell = row.createCell(startIn+11);
                cell.setCellValue("base_tipo_verificacion");
                cell = row.createCell(startIn+12);
                cell.setCellValue("base_grupo_riesgo");
                cell = row.createCell(startIn+13);
                cell.setCellValue("base_tipovisita");
                cell = row.createCell(startIn+14);
                cell.setCellValue("base_tasa_2999");
                cell = row.createCell(startIn+15);
                cell.setCellValue("base_tasa_5999");
                cell = row.createCell(startIn+16);
                cell.setCellValue("base_tasa_9999");
                cell = row.createCell(startIn+17);
                cell.setCellValue("base_tasa_19999");
                cell = row.createCell(startIn+18);
                cell.setCellValue("base_tasa_mas");
                cell = row.createCell(startIn+19);
                cell.setCellValue("base_oferta_12meses");
                cell = row.createCell(startIn+20);
                cell.setCellValue("base_oferta_18meses");
                cell = row.createCell(startIn+21);
                cell.setCellValue("base_oferta_24meses");
                cell = row.createCell(startIn+22);
                cell.setCellValue("base_oferta_36meses");
                cell = row.createCell(startIn+23);
                cell.setCellValue("base_oferta_mas");
                break;
        }
    }

    private void fillInBodyPreApprovedBase(Integer  entityId, Integer startIn,Row row, Cell cell, JSONObject entityCustomData){
        if(entityId == null) return;
        switch (entityId){
            case Entity.BANBIF:
                BanbifPreApprovedBase banbifPreApprovedBase = new BanbifPreApprovedBase();
                if(entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey())){
                    banbifPreApprovedBase = new Gson().fromJson(entityCustomData.getJSONObject(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey()).toString(),BanbifPreApprovedBase.class);
                }
                cell = row.createCell(startIn);
                if(banbifPreApprovedBase != null && banbifPreApprovedBase.getTipoDoc() != null) cell.setCellValue(banbifPreApprovedBase.getTipoDoc());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(startIn+1);
                if(banbifPreApprovedBase != null && banbifPreApprovedBase.getNumeroDoc() != null) cell.setCellValue(banbifPreApprovedBase.getNumeroDoc());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(startIn+2);
                if(banbifPreApprovedBase != null && banbifPreApprovedBase.getPlastico() != null) cell.setCellValue(banbifPreApprovedBase.getPlastico());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(startIn+3);
                if(banbifPreApprovedBase != null && banbifPreApprovedBase.getLinea() != null) cell.setCellValue(banbifPreApprovedBase.getLinea());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(startIn+4);
                if(banbifPreApprovedBase != null && banbifPreApprovedBase.getTipoBase() != null) cell.setCellValue(banbifPreApprovedBase.getTipoBase());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(startIn+5);
                if(banbifPreApprovedBase != null && banbifPreApprovedBase.getNombres() != null) cell.setCellValue(banbifPreApprovedBase.getNombres());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(startIn+6);
                if(banbifPreApprovedBase != null && banbifPreApprovedBase.getApellidos() != null) cell.setCellValue(banbifPreApprovedBase.getApellidos());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(startIn+7);
                if(banbifPreApprovedBase != null && banbifPreApprovedBase.getCanal() != null) cell.setCellValue(banbifPreApprovedBase.getCanal());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(startIn+8);
                if(banbifPreApprovedBase != null && banbifPreApprovedBase.getPromocionUso() != null) cell.setCellValue(banbifPreApprovedBase.getPromocionUso());
                else cell.setCellType(CellType.BLANK);
                cell = row.createCell(startIn+9);
                if(banbifPreApprovedBase != null && banbifPreApprovedBase.getPromocionAceptacion() != null) cell.setCellValue(banbifPreApprovedBase.getPromocionAceptacion());
                else cell.setCellType(CellType.BLANK);
                break;
            case Entity.PRISMA:
                PrismaPreApprovedBase prismaPreApprovedBase = new PrismaPreApprovedBase();
                if(entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.PRISMA_BASE_PREAPROBADA.getKey())){
                    prismaPreApprovedBase = new Gson().fromJson(entityCustomData.getJSONObject(LoanApplication.EntityCustomDataKeys.PRISMA_BASE_PREAPROBADA.getKey()).toString(),PrismaPreApprovedBase.class);
                }

                cell = row.createCell(startIn);
                if(prismaPreApprovedBase != null && prismaPreApprovedBase.getDocumentNumber() != null) cell.setCellValue(prismaPreApprovedBase.getDocumentNumber());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+1);
                if(prismaPreApprovedBase != null && prismaPreApprovedBase.getDocumentType() != null) cell.setCellValue(prismaPreApprovedBase.getDocumentType());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+2);
                if(prismaPreApprovedBase != null && prismaPreApprovedBase.getNames() != null) cell.setCellValue(prismaPreApprovedBase.getNames());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+3);
                if(prismaPreApprovedBase != null && prismaPreApprovedBase.getLastName() != null) cell.setCellValue(prismaPreApprovedBase.getLastName());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+4);
                if(prismaPreApprovedBase != null && prismaPreApprovedBase.getMaxInstallments() != null) cell.setCellValue(prismaPreApprovedBase.getMaxInstallments());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+5);
                if(prismaPreApprovedBase != null && prismaPreApprovedBase.getMaxAmount() != null) cell.setCellValue(prismaPreApprovedBase.getMaxAmount());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+6);
                if(prismaPreApprovedBase != null && prismaPreApprovedBase.getTea() != null) cell.setCellValue(prismaPreApprovedBase.getTea());
                else cell.setCellType(CellType.BLANK);

                break;
            case Entity.AZTECA:
                AztecaPreApprovedBase aztecaPreApprovedBase = new AztecaPreApprovedBase();
                if(entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_PREAPROBADA.getKey())){
                    aztecaPreApprovedBase = new Gson().fromJson(entityCustomData.getJSONObject(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_PREAPROBADA.getKey()).toString(),AztecaPreApprovedBase.class);
                }
                cell = row.createCell(startIn);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getDni() != null) cell.setCellValue(aztecaPreApprovedBase.getDni());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+1);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getApPaterno() != null) cell.setCellValue(aztecaPreApprovedBase.getApPaterno());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+2);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getApMaterno() != null) cell.setCellValue(aztecaPreApprovedBase.getApMaterno());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+3);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getNombre() != null) cell.setCellValue(aztecaPreApprovedBase.getNombre());
                else cell.setCellType(CellType.BLANK);


                cell = row.createCell(startIn+4);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getCapacidad() != null) cell.setCellValue(aztecaPreApprovedBase.getCapacidad());
                else cell.setCellType(CellType.BLANK);


                cell = row.createCell(startIn+5);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getOfertaMax() != null) cell.setCellValue(aztecaPreApprovedBase.getOfertaMax());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+6);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getPlazo() != null) cell.setCellValue(aztecaPreApprovedBase.getPlazo());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+7);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getIdCampania() != null) cell.setCellValue(aztecaPreApprovedBase.getIdCampania());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+8);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getLandingPp() != null) cell.setCellValue(aztecaPreApprovedBase.getLandingPp());
                else cell.setCellType(CellType.BLANK);


                cell = row.createCell(startIn+9);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getLandingCc() != null) cell.setCellValue(aztecaPreApprovedBase.getLandingCc());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+10);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getLandingMoto() != null) cell.setCellValue(aztecaPreApprovedBase.getLandingMoto());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+11);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getTipoVerificacion() != null) cell.setCellValue(aztecaPreApprovedBase.getTipoVerificacion());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+12);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getGrupoRiesgo() != null) cell.setCellValue(aztecaPreApprovedBase.getGrupoRiesgo());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+13);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getTipovisita() != null) cell.setCellValue(aztecaPreApprovedBase.getTipovisita());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+14);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getTasa2999() != null) cell.setCellValue(aztecaPreApprovedBase.getTasa2999());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+15);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getTasa5999() != null) cell.setCellValue(aztecaPreApprovedBase.getTasa5999());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+16);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getTasa9999() != null) cell.setCellValue(aztecaPreApprovedBase.getTasa9999());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+17);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getTasa19999() != null) cell.setCellValue(aztecaPreApprovedBase.getTasa19999());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+18);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getTasaMas() != null) cell.setCellValue(aztecaPreApprovedBase.getTasaMas());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+19);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getOferta12Meses() != null) cell.setCellValue(aztecaPreApprovedBase.getOferta12Meses());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+20);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getOferta18Meses() != null) cell.setCellValue(aztecaPreApprovedBase.getOferta18Meses());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+21);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getOferta24Meses() != null) cell.setCellValue(aztecaPreApprovedBase.getOferta24Meses());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+22);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getOferta36Meses() != null) cell.setCellValue(aztecaPreApprovedBase.getOferta36Meses());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+23);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getOfertaMas() != null) cell.setCellValue(aztecaPreApprovedBase.getOfertaMas());
                else cell.setCellType(CellType.BLANK);
                break;
        }
    }

    private void fillInHeaderCollectionPreApprovedBase(Integer  entityId, Integer startIn,Row row, Cell cell){
        if(entityId == null) return;
        switch (entityId){
            case Entity.AZTECA:
                cell = row.createCell(startIn);
                cell.setCellValue("base_pais");
                cell = row.createCell(startIn+1);
                cell.setCellValue("base_tipo_documento");
                cell = row.createCell(startIn+2);
                cell.setCellValue("base_numero_documento");
                cell = row.createCell(startIn+3);
                cell.setCellValue("base_nombre");
                cell = row.createCell(startIn+4);
                cell.setCellValue("base_ap_paterno");
                cell = row.createCell(startIn+5);
                cell.setCellValue("base_ap_materno");
                cell = row.createCell(startIn+6);
                cell.setCellValue("base_celular_1");
                cell = row.createCell(startIn+7);
                cell.setCellValue("base_celular_2");
                cell = row.createCell(startIn+8);
                cell.setCellValue("base_celular_3");
                cell = row.createCell(startIn+9);
                cell.setCellValue("base_celular_4");
                cell = row.createCell(startIn+10);
                cell.setCellValue("base_celular_5");
                cell = row.createCell(startIn+11);
                cell.setCellValue("base_saldo_capital");
                cell = row.createCell(startIn+12);
                cell.setCellValue("base_saldo_interes");
                cell = row.createCell(startIn+13);
                cell.setCellValue("base_saldo_moratorio");
                cell = row.createCell(startIn+14);
                cell.setCellValue("base_saldo_total");
                cell = row.createCell(startIn+15);
                cell.setCellValue("base_dias_atraso");
                cell = row.createCell(startIn+16);
                cell.setCellValue("base_monto_campania");
                cell = row.createCell(startIn+17);
                cell.setCellValue("base_vencimiento_campania");
                cell = row.createCell(startIn+18);
                cell.setCellValue("base_domicilio");
                cell = row.createCell(startIn+19);
                cell.setCellValue("base_departamento");
                cell = row.createCell(startIn+20);
                cell.setCellValue("base_provincia");
                cell = row.createCell(startIn+21);
                cell.setCellValue("base_distrito");
                cell = row.createCell(startIn+22);
                cell.setCellValue("base_codigo_cliente_externo");
                break;
        }
    }

    private void fillInBodyCollectionPreApprovedBase(Integer  entityId, Integer startIn,Row row, Cell cell, ReportEntityExtranetTrayReport loan){
        if(entityId == null) return;
        switch (entityId){
            case Entity.AZTECA:
                AztecaGetawayBase aztecaPreApprovedBase = new AztecaGetawayBase();
                if(loan.getEntityCustomData() != null && loan.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_GATEWAY.getKey())){
                    aztecaPreApprovedBase = new Gson().fromJson(loan.getEntityCustomData().getJSONObject(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_GATEWAY.getKey()).toString(), AztecaGetawayBase.class);
                }

                cell = row.createCell(startIn);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getPais() != null) cell.setCellValue(aztecaPreApprovedBase.getPais());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+1);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getTipoDocumento() != null) cell.setCellValue(aztecaPreApprovedBase.getTipoDocumento());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+2);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getNumeroDocumento() != null) cell.setCellValue(aztecaPreApprovedBase.getNumeroDocumento());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+3);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getNombre() != null) cell.setCellValue(aztecaPreApprovedBase.getNombre());
                else cell.setCellType(CellType.BLANK);


                cell = row.createCell(startIn+4);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getApPaterno() != null) cell.setCellValue(aztecaPreApprovedBase.getApPaterno());
                else cell.setCellType(CellType.BLANK);


                cell = row.createCell(startIn+5);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getApMaterno() != null) cell.setCellValue(aztecaPreApprovedBase.getApMaterno());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+6);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getCelular1() != null) cell.setCellValue(aztecaPreApprovedBase.getCelular1());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+7);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getCelular2() != null) cell.setCellValue(aztecaPreApprovedBase.getCelular2());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+8);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getCelular3() != null) cell.setCellValue(aztecaPreApprovedBase.getCelular3());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+9);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getCelular4() != null) cell.setCellValue(aztecaPreApprovedBase.getCelular4());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+10);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getCelular5() != null) cell.setCellValue(aztecaPreApprovedBase.getCelular5());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+11);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getSaldoCapital() != null) cell.setCellValue(aztecaPreApprovedBase.getSaldoCapital());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+12);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getSaldoInteres() != null) cell.setCellValue(aztecaPreApprovedBase.getSaldoInteres());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+13);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getSaldoMoratorio() != null) cell.setCellValue(aztecaPreApprovedBase.getSaldoMoratorio());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+14);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getSaldoTotal() != null) cell.setCellValue(aztecaPreApprovedBase.getSaldoTotal());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+15);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getDiasAtrazo() != null) cell.setCellValue(aztecaPreApprovedBase.getDiasAtrazo());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+16);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getMontoCampania() != null) cell.setCellValue(aztecaPreApprovedBase.getMontoCampania());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+17);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getVencimientoCampania() != null) cell.setCellValue(aztecaPreApprovedBase.getVencimientoCampania());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+18);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getDomicilio() != null) cell.setCellValue(aztecaPreApprovedBase.getDomicilio());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+19);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getDepartamento() != null) cell.setCellValue(aztecaPreApprovedBase.getDepartamento());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+20);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getProvincia() != null) cell.setCellValue(aztecaPreApprovedBase.getProvincia());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+21);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getDistrito() != null) cell.setCellValue(aztecaPreApprovedBase.getDistrito());
                else cell.setCellType(CellType.BLANK);

                cell = row.createCell(startIn+22);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getCodigoClienteExterno() != null) cell.setCellValue(aztecaPreApprovedBase.getCodigoClienteExterno());
                else cell.setCellType(CellType.BLANK);

                break;
        }
    }

    @Override
    public byte[] createBanBifLeadsReport(List<BanbifTcLeadLoan> data, String sheetname) {
        setUserTimeZone(TimeZone.getTimeZone("America/Lima"));

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetname);

        Row headerRow;
        Cell cell;

        int rowNum = 0;
        headerRow = sheet.createRow(rowNum++);
        cell = headerRow.createCell(0);
        cell.setCellValue("#");
        cell = headerRow.createCell(1);
        cell.setCellValue("Fecha Generación");
        cell = headerRow.createCell(2);
        cell.setCellValue("Tipo Doc.");
        cell = headerRow.createCell(3);
        cell.setCellValue("Número Doc");
        cell = headerRow.createCell(4);
        cell.setCellValue("Nombres");
        cell = headerRow.createCell(5);
        cell.setCellValue("Apellido Paterno");
        cell = headerRow.createCell(6);
        cell.setCellValue("Apellido Materno");
        cell = headerRow.createCell(7);
        cell.setCellValue("Email");
        cell = headerRow.createCell(8);
        cell.setCellValue("Teléfono");
        cell = headerRow.createCell(9);
        cell.setCellValue("Tipo de Base");
        cell = headerRow.createCell(10);
        cell.setCellValue("Canal de atención");

        //format to dates
        CellStyle cellStyleDate = workbook.createCellStyle();
        CreationHelper createHelperDate = workbook.getCreationHelper();
        cellStyleDate.setDataFormat(
                createHelperDate.createDataFormat().getFormat("dd/MM/yyyy"));

        if (data != null) {
            int index = 1;
            for (BanbifTcLeadLoan lead : data) {
                Row row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(index);
                cell = row.createCell(1);
                cell.setCellValue(utilService.dateFormat(lead.getRegisterDate()));
                cell.setCellStyle(cellStyleDate);
                cell = row.createCell(2);
                cell.setCellValue(lead.getDocumentType().getName());
                cell = row.createCell(3);
                cell.setCellValue(lead.getDocumentNumber());
                cell = row.createCell(4);
                cell.setCellValue(lead.getName());
                cell = row.createCell(5);
                cell.setCellValue(lead.getLastName());
                cell = row.createCell(6);
                cell.setCellValue(lead.getLastSurname());
                cell = row.createCell(7);
                cell.setCellValue(lead.getEmail());
                cell = row.createCell(8);
                cell.setCellValue(lead.getCellphone());
                cell = row.createCell(9);
                cell.setCellValue(lead.getBanbifBaseType());
                cell = row.createCell(10);
                cell.setCellValue(lead.getBanbifChannel());

                index++;
            }
        }

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();
            return outStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] createEntityErrorNotificationDetail(EntityErrorExtranetPainter entityError, EntityWebServiceLog entityWebServiceLog) throws Exception {

        List<String> data = new ArrayList<>();
        SimpleDateFormat sdfHour =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        data.add("Servicio: "+ entityError.getEntityWebService().getWbeserviceName());
        data.add("Fecha de registro: "+sdfHour.format(entityError.getRegisterDate()));
        if(entityError.getLoanApplicationId() != null){
            data.add("Solicitud: "+entityError.getLoanApplicationCode());
            data.add("Documento: "+String.format("%s - %s", entityError.getIdentityDocumentType().getName(), entityError.getDocumentNumber()));
        }
        data.add("Request:");
        data.add(entityWebServiceLog.getRequest());
        data.add("Response:");
        data.add(entityWebServiceLog.getResponse());

        StringBuilder sb = new StringBuilder();
        for (String datum : data) {
            sb.append(CSVutils.writeLine(Arrays.asList(datum,"\n")));
        }
        String da = sb.toString();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        outStream.write(da.getBytes());
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createCreditsReport(List<CreditEntityExtranetPainter> credits, String sheetname) throws Exception {
//        setUserTimeZone(TimeZone.getTimeZone("GMT/UTC"));

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetname);

        Row headerRow;
        Cell cell;
        int rowNum = 0;
        headerRow = sheet.createRow(rowNum++);
        cell = headerRow.createCell(0);
        cell.setCellValue("#");
        cell = headerRow.createCell(1);
        cell.setCellValue("Fecha Generación");
        cell = headerRow.createCell(2);
        cell.setCellValue("Crédito");
        cell = headerRow.createCell(3);
        cell.setCellValue("Producto");
        cell = headerRow.createCell(4);
        cell.setCellValue("Tipo Doc.");
        cell = headerRow.createCell(5);
        cell.setCellValue("Número Doc.");
        cell = headerRow.createCell(6);
        cell.setCellValue("Nombres");
        cell = headerRow.createCell(7);
        cell.setCellValue("Apellido Paterno");
        cell = headerRow.createCell(8);
        cell.setCellValue("Apellido Materno");
        cell = headerRow.createCell(9);
        cell.setCellValue("Correo");
        cell = headerRow.createCell(10);
        cell.setCellValue("Teléfono");
        cell = headerRow.createCell(11);
        cell.setCellValue("Línea");
        cell = headerRow.createCell(12);
        cell.setCellValue("Tipo de tarjeta");
        cell = headerRow.createCell(13);
        cell.setCellValue("Bono de bienvenida");
        // TODO: BanBif doesn't show these columns
        /*cell = headerRow.createCell(10);
        cell.setCellValue("Cuotas");
        cell = headerRow.createCell(11);
        cell.setCellValue("Tasa");
        cell = headerRow.createCell(12);
        cell.setCellValue("Documentos");*/
        cell = headerRow.createCell(14);
        cell.setCellValue("Dir. Vivienda Departamento");
        cell = headerRow.createCell(15);
        cell.setCellValue("Dir. Vivienda Provincia");
        cell = headerRow.createCell(16);
        cell.setCellValue("Dir. Vivienda Distrito");
        cell = headerRow.createCell(17);
        cell.setCellValue("Dir. Vivienda Zona");
        cell = headerRow.createCell(18);
        cell.setCellValue("Dir. Vivienda Vía");
        cell = headerRow.createCell(19);
        cell.setCellValue("Dir. Vivienda");
        cell = headerRow.createCell(20);
        cell.setCellValue("Dir. Vivienda Numero");
        cell = headerRow.createCell(21);
        cell.setCellValue("Dir. Vivienda Interior");
        cell = headerRow.createCell(22);
        cell.setCellValue("Dir. Vivienda Referencia");

        cell = headerRow.createCell(23);
        cell.setCellValue("Dir. Laboral Departamento");
        cell = headerRow.createCell(24);
        cell.setCellValue("Dir. Laboral Provincia");
        cell = headerRow.createCell(25);
        cell.setCellValue("Dir. Laboral Distrito");
        cell = headerRow.createCell(26);
        cell.setCellValue("Dir. Laboral Zona");
        cell = headerRow.createCell(27);
        cell.setCellValue("Dir. Laboral Vía");
        cell = headerRow.createCell(28);
        cell.setCellValue("Dir. Laboral");
        cell = headerRow.createCell(29);
        cell.setCellValue("Dir. Laboral Numero");
        cell = headerRow.createCell(30);
        cell.setCellValue("Dir. Laboral Interior");
        cell = headerRow.createCell(31);
        cell.setCellValue("Dir. Laboral Referencia");
        cell = headerRow.createCell(32);
        cell.setCellValue("Punto de entrega");
        cell = headerRow.createCell(33);
        cell.setCellValue("Nombre de la empresa");
        //format to dates

        CellStyle cellStyleDate = workbook.createCellStyle();
        CreationHelper createHelperDate = workbook.getCreationHelper();
        cellStyleDate.setDataFormat(
                createHelperDate.createDataFormat().getFormat("dd/MM/yyyy"));

        if (credits != null) {
            int index = 1;
            for (CreditEntityExtranetPainter credit : credits) {

                Row row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(index);
                cell = row.createCell(1);
                cell.setCellValue(utilService.dateFormat(credit.getInactiveWOScheduleDate()));
                cell.setCellStyle(cellStyleDate);
                cell = row.createCell(2);
                cell.setCellValue(credit.getCode());
                cell = row.createCell(3);
                cell.setCellValue(credit.getEntityProductParams().getEntityProduct());
                cell = row.createCell(4);
                cell.setCellValue(credit.getPersonDocumentType().getName());
                cell = row.createCell(5);
                cell.setCellValue(credit.getPersonDocumentNumber());
                cell = row.createCell(6);
                cell.setCellValue(credit.getPersonName());
                cell = row.createCell(7);
                cell.setCellValue(credit.getPersonFirstSurname());
                cell = row.createCell(8);
                cell.setCellValue(credit.getPersonLastSurname());
                cell = row.createCell(9);
                cell.setCellValue(credit.getEmail());
                cell = row.createCell(10);
                cell.setCellValue(credit.getPhoneNumber());
                cell = row.createCell(11);
                cell.setCellValue(utilService.doubleMoneyFormat(credit.getBanbifBaseDataAsDouble("linea"), "$"));
                cell = row.createCell(12);
                cell.setCellValue(credit.getBanbifBaseDataAsString("plastico"));
                cell = row.createCell(13);
                cell.setCellValue(credit.getBanbifBaseDataWelcomeBonusAsString());
                DisggregatedAddress disagregatedHomeAddress = credit.getHomeAddress();
                if(disagregatedHomeAddress != null){
                    if(disagregatedHomeAddress.getUbigeo() != null){
                        cell = row.createCell(14);
                        cell.setCellValue(disagregatedHomeAddress.getUbigeo().getDepartment() != null ? disagregatedHomeAddress.getUbigeo().getDepartment().getName() : "");
                        cell = row.createCell(15);
                        cell.setCellValue(disagregatedHomeAddress.getUbigeo().getProvince() != null ? disagregatedHomeAddress.getUbigeo().getProvince().getName() : "");
                        cell = row.createCell(16);
                        cell.setCellValue(disagregatedHomeAddress.getUbigeo().getDistrict() != null ? disagregatedHomeAddress.getUbigeo().getDistrict().getName() : "");
                    }
                    else{
                        cell = row.createCell(14);
                        cell.setCellValue("");
                        cell = row.createCell(15);
                        cell.setCellValue("");
                        cell = row.createCell(16);
                        cell.setCellValue("");
                    }
                    cell = row.createCell(17);
                    cell.setCellValue(disagregatedHomeAddress.getAreaType() != null && disagregatedHomeAddress.getAreaType().getName() != null? disagregatedHomeAddress.getAreaType().getName() : "");
                    cell = row.createCell(18);
                    cell.setCellValue(disagregatedHomeAddress.getStreetType() != null && disagregatedHomeAddress.getStreetType().getType() != null ? disagregatedHomeAddress.getStreetType().getType() : "-");
                    cell = row.createCell(19);
                    cell.setCellValue(disagregatedHomeAddress.getStreetName() != null ? disagregatedHomeAddress.getStreetName()  : "");
                    cell = row.createCell(20);
                    cell.setCellValue(disagregatedHomeAddress.getStreetnumber() != null ? disagregatedHomeAddress.getStreetnumber() : "");
                    cell = row.createCell(21);
                    cell.setCellValue(disagregatedHomeAddress.getInteriorNumber() != null ? disagregatedHomeAddress.getInteriorNumber().toString() : "");
                    cell = row.createCell(22);
                    cell.setCellValue(disagregatedHomeAddress.getReference() != null ? disagregatedHomeAddress.getReference() : "");
                }
                else{
                    for (int i = 0; i < 9; i++) {
                        cell = row.createCell(14+i);
                        cell.setCellValue("");
                    }
                }
                DisggregatedAddress disagregatedWorkAddress = credit.getWorkplaceAddress();
                if(disagregatedWorkAddress != null){
                    if(disagregatedWorkAddress.getUbigeo() != null){
                        cell = row.createCell(23);
                        cell.setCellValue(disagregatedWorkAddress.getUbigeo().getDepartment() != null ? disagregatedWorkAddress.getUbigeo().getDepartment().getName() : "");
                        cell = row.createCell(24);
                        cell.setCellValue(disagregatedWorkAddress.getUbigeo().getProvince() != null ? disagregatedWorkAddress.getUbigeo().getProvince().getName() : "");
                        cell = row.createCell(25);
                        cell.setCellValue(disagregatedWorkAddress.getUbigeo().getDistrict() != null ? disagregatedWorkAddress.getUbigeo().getDistrict().getName() : "");
                    }
                    else{
                        cell = row.createCell(23);
                        cell.setCellValue("");
                        cell = row.createCell(24);
                        cell.setCellValue("");
                        cell = row.createCell(25);
                        cell.setCellValue("");
                    }
                    cell = row.createCell(26);
                    cell.setCellValue(disagregatedWorkAddress.getAreaType() != null && disagregatedWorkAddress.getAreaType().getName() != null? disagregatedWorkAddress.getAreaType().getName() : "");
                    cell = row.createCell(27);
                    cell.setCellValue(disagregatedWorkAddress.getStreetType() != null && disagregatedWorkAddress.getStreetType().getType() != null ? disagregatedWorkAddress.getStreetType().getType() : "");
                    cell = row.createCell(28);
                    cell.setCellValue(disagregatedWorkAddress.getStreetName() != null ? disagregatedWorkAddress.getStreetName() : "");
                    cell = row.createCell(29);
                    cell.setCellValue(disagregatedWorkAddress.getStreetnumber() != null ? disagregatedWorkAddress.getStreetnumber() : "");
                    cell = row.createCell(30);
                    cell.setCellValue(disagregatedWorkAddress.getInteriorNumber() != null ? disagregatedWorkAddress.getInteriorNumber().toString() : "");
                    cell = row.createCell(31);
                    cell.setCellValue(disagregatedWorkAddress.getReference() != null ? disagregatedWorkAddress.getReference() : "");
                }
                else{
                    for (int i = 0; i < 9; i++) {
                        cell = row.createCell(23+i);
                        cell.setCellValue("");
                    }
                }
                cell = row.createCell(32);
                cell.setCellValue("H".equals(credit.getDeliveryPoint()) ? "Vivienda" : "Laboral");
                cell = row.createCell(33);
                cell.setCellValue(credit.getCompanyName() != null ? credit.getCompanyName() : "");
                index++;
            }
        }

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();
            return outStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Pair<byte[], String> processReport(ReportProces reportProces) throws Exception {
        switch (reportProces.getReportId()) {
            case Report.REPORTE_SOLICITUDES_BO: {
                Date startDate = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null));
                Date endDate = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null));
                String countries = JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "countries", null).toString();
                return Pair.of(createLoanReport(startDate, endDate, countries), "Reporte_de_Solicitudes_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_BANDEJAS_EXTRANET: {
                Date startDate = JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null) != null ? CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null)) : null;
                Date endDate = JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null) != null ? CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null)) : null;
                Integer entity = JsonUtil.getIntFromJson(reportProces.getParams(), "entity", null);
                String query = JsonUtil.getStringFromJson(reportProces.getParams(), "query", null);

                JSONArray productsJson = JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "products", null);
                Integer[] products = JsonUtil.getIntegerArrayFromJson(productsJson.toString(), null);
                if(products == null) products = new Integer[0];

                JSONArray stepsJson = JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "steps", null);
                Integer[] steps = JsonUtil.getIntegerArrayFromJson(stepsJson.toString(), null);
                if(steps == null) steps = new Integer[0];

                JSONArray loanIdsJson = JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "loanIds", null);
                Integer[] loanIds = JsonUtil.getIntegerArrayFromJson(loanIdsJson.toString(), null);
                if(loanIds == null) loanIds = new Integer[0];

                Boolean isPaymentCommitment = JsonUtil.getBooleanFromJson(reportProces.getParams(), "isPaymentCommitment", false);

                Boolean isRejectedTrayReport  = JsonUtil.getBooleanFromJson(reportProces.getParams(), "isRejectedTrayReport", false);

                if(isRejectedTrayReport) return Pair.of(createRejectedTrayReport(entity, loanIds, products, steps),"Reporte_Solicitudes_Rechazadas_" + reportProces.getId() + ".xls");

                if(!isPaymentCommitment) return Pair.of(createTrayExtranetReport(entity, loanIds, products, steps),"Reporte_" + reportProces.getId() + ".xls");
                else return Pair.of(createTrayExtranetReportPaymentCommitment(entity, loanIds, products, steps),"Reporte_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_FUNNEL_COLLECTION:
            case Report.REPORTE_FUNNELV3: {
                Date startDate = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null));
                Date endDate = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null));

                Date startDate2 = null;
                Date endDate2 = null;

                if (JsonUtil.getStringFromJson(reportProces.getParams(), "startDate2", null) != null)
                    startDate2 = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "startDate2", null));

                if (JsonUtil.getStringFromJson(reportProces.getParams(), "endDate2", null) != null)
                    endDate2 = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "endDate2", null));

                String countriesJson = JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "countries", null).toString();
                Integer[] countryIds = JsonUtil.getIntegerArrayFromJson(countriesJson, new Integer[0]);

                String entitiesJson = JsonUtil.getStringFromJson(reportProces.getParams(), "entities", null);
                Integer[] entityIds = JsonUtil.getIntegerArrayFromJson( entitiesJson, new Integer[0]);

                String productsJson = JsonUtil.getStringFromJson(reportProces.getParams(), "products", null);
                Integer[] products = JsonUtil.getIntegerArrayFromJson(productsJson, new Integer[0]);

                Integer minAge = JsonUtil.getIntFromJson(reportProces.getParams(), "minAge", null);
                Integer maxAge = JsonUtil.getIntFromJson(reportProces.getParams(), "maxAge", null);
                String requestType = JsonUtil.getStringFromJson(reportProces.getParams(), "requestType", null);
                String cardType = JsonUtil.getStringFromJson(reportProces.getParams(), "cardType", null);
                boolean isBanBif = JsonUtil.getBooleanFromJson(reportProces.getParams(), "isBanBif", false);

                List<String> utmSource = JsonUtil.getListFromJsonArray(JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "utmSource" , new JSONArray()), (arr, i) -> arr.getString(i));
                List<String> utmCampaign = JsonUtil.getListFromJsonArray(JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "utmCampaign" , new JSONArray()), (arr, i) -> arr.getString(i));
                List<String> utmContent = JsonUtil.getListFromJsonArray(JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "utmContent" , new JSONArray()), (arr, i) -> arr.getString(i));
                List<String> utmMedium = JsonUtil.getListFromJsonArray(JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "utmMedium" , new JSONArray()), (arr, i) -> arr.getString(i));
                List<Integer> entityProductParams = JsonUtil.getListFromJsonArray(JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "entityProductParams" , new JSONArray()), (arr, i) -> arr.getInt(i));

                String stepsJson = JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "steps", null).toString();
                Integer[] steps = JsonUtil.getIntegerArrayFromJson(stepsJson, null);

                return Pair.of(createFunnelV3Report(minAge, maxAge, requestType, cardType, startDate, endDate, startDate2, endDate2, countryIds, entityIds, products,steps,
                        utmSource,
                        utmCampaign,
                        utmContent,
                        utmMedium,
                        entityProductParams
                        ), "Reporte_funnel_" + reportProces.getId() + ".xls");

            }
            case Report.REPORTE_SOLICITUDES_LIGHT: {
                Date startDate = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null));
                Date endDate = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null));

                Date startDate2 = null;
                Date endDate2 = null;

                if (JsonUtil.getStringFromJson(reportProces.getParams(), "startDate2", null) != null)
                    startDate2 = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "startDate2", null));

                if (JsonUtil.getStringFromJson(reportProces.getParams(), "endDate2", null) != null)
                    endDate2 = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "endDate2", null));

                String countriesJson = JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "countries", null).toString();
                Integer[] countryIds = JsonUtil.getIntegerArrayFromJson(countriesJson, null);

                String entitiesJson = JsonUtil.getStringFromJson(reportProces.getParams(), "entities", null);
                Integer[] entityIds = JsonUtil.getIntegerArrayFromJson(entitiesJson, null);

                String productsJson = JsonUtil.getStringFromJson(reportProces.getParams(), "products", null);
                Integer[] products = JsonUtil.getIntegerArrayFromJson(productsJson, null);

                Integer minAge = JsonUtil.getIntFromJson(reportProces.getParams(), "minAge", null);
                Integer maxAge = JsonUtil.getIntFromJson(reportProces.getParams(), "maxAge", null);
                String requestType = JsonUtil.getStringFromJson(reportProces.getParams(), "requestType", null);
                String cardType = JsonUtil.getStringFromJson(reportProces.getParams(), "cardType", null);
                boolean isBanBif = JsonUtil.getBooleanFromJson(reportProces.getParams(), "isBanBif", false);

                if (isBanBif)
                    return Pair.of(createLoanLightReport(minAge, maxAge, requestType, cardType, startDate, endDate, startDate2, endDate2, countryIds, entityIds, products), "Reporte_consolidado_solicitudes_" + reportProces.getId() + ".xls");
                else
                    return Pair.of(createLoanLightReport(startDate, endDate, countryIds, entityIds), "Reporte_consolidado_solicitudes_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_ORIGINACIONES_BO: {
                Date startDate = new Date(JsonUtil.getLongFromJson(reportProces.getParams(), "startDate", null));
                Date endDate = new Date(JsonUtil.getLongFromJson(reportProces.getParams(), "endDate", null));
                String countries = JsonUtil.getStringFromJson(reportProces.getParams(), "countries", null);
                String symbol = JsonUtil.getStringFromJson(reportProces.getParams(), "symbol", null);
                boolean internationalCurrency = JsonUtil.getBooleanFromJson(reportProces.getParams(), "internationalCurrency", null);
                return Pair.of(createOriginationReport(startDate, endDate, countries, symbol, internationalCurrency), "Reporte_de_Originaciones_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_FUNNEL: {
                Date startDate = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null));
                Date endDate = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null));
                Integer dateType = JsonUtil.getIntFromJson(reportProces.getParams(), "dateType", null);
                String entities = JsonUtil.getStringFromJson(reportProces.getParams(), "entities", null);
                String products = JsonUtil.getStringFromJson(reportProces.getParams(), "products", null);
                String source = JsonUtil.getStringFromJson(reportProces.getParams(), "source", null);
                String medium = JsonUtil.getStringFromJson(reportProces.getParams(), "medium", null);
                String campaign = JsonUtil.getStringFromJson(reportProces.getParams(), "campaign", null);
                Integer origin = JsonUtil.getIntFromJson(reportProces.getParams(), "origin", null);
                String countries = JsonUtil.getStringFromJson(reportProces.getParams(), "countries", null);
                return Pair.of(createFunnelReport(startDate, endDate, dateType, entities, products, source, medium, campaign, origin, countries), "Reporte_funnel_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_GESTION_OPERADORES: {
                String period1FromFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "period1From", null);
                Date period1From = period1FromFilter != null ? CUSTOM_FORMAT.parse(period1FromFilter) : null;

                String period1ToFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "period1To", null);
                Date period1To = period1ToFilter != null ? CUSTOM_FORMAT.parse(period1ToFilter) : null;

                String period2FromFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "period2From", null);
                Date period2From = period2FromFilter != null ? CUSTOM_FORMAT.parse(period2FromFilter) : null;

                String period2ToFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "period2To", null);
                Date period2To = period2ToFilter != null ? CUSTOM_FORMAT.parse(period2ToFilter) : null;

                String countryId = JsonUtil.getStringFromJson(reportProces.getParams(), "countryId", null);
                Integer sysUserId = JsonUtil.getIntFromJson(reportProces.getParams(), "sysUserId", null);
                Integer entityId = JsonUtil.getIntFromJson(reportProces.getParams(), "entityId", null);
                Integer productId = JsonUtil.getIntFromJson(reportProces.getParams(), "productId", null);
                String symbol = JsonUtil.getStringFromJson(reportProces.getParams(), "symbol", null);
                return Pair.of(createOperatorsManagementsReport(countryId, period1From, period1To, period2From, period2To, sysUserId, entityId, productId, symbol), "Reporte_gestion_operadores_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_CONSOLIDADO_DEUDORES: {
                return Pair.of(createDebtConsolidationReport(), "Reporte_consolidacion_deudas_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_FUNNEL_DASHBOARD_BO: {
                Date period1From = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "period1From", null));
                Date period1To = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "period1To", null));
                Date period2From = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "period2From", null));
                Date period2To = CUSTOM_FORMAT.parse(JsonUtil.getStringFromJson(reportProces.getParams(), "period2To", null));

                String countryId = JsonUtil.getStringFromJson(reportProces.getParams(), "countryId", null);
                Integer entityId = JsonUtil.getIntFromJson(reportProces.getParams(), "entityId", null);
                String disbursementType = JsonUtil.getStringFromJson(reportProces.getParams(), "disbursementType", null);
                Integer dateType = JsonUtil.getIntFromJson(reportProces.getParams(), "dateType", null);

                return Pair.of(createReporteFunnelDashBoardBo(period1From, period1To, period2From, period2To, entityId, disbursementType, dateType, countryId), "Reporte_Dashboard_de_Funnel_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_DE_PANTALLAS: {
                String countryId = JsonUtil.getStringFromJson(reportProces.getParams(), "countryId", null);

                String startDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null);
                Date startDate = startDateFilter != null ? CUSTOM_FORMAT.parse(startDateFilter) : null;

                String endDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null);
                Date endDate = endDateFilter != null ? CUSTOM_FORMAT.parse(endDateFilter) : null;

                JSONArray products = JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "products", null);
                Integer[] productsArray = null;
                if (products != null) {
                    productsArray = new Integer[products.length()];
                    for (int i = 0; i < products.length(); i++) {
                        productsArray[i] = products.getInt(i);
                    }
                }

                JSONArray statuses = JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "statuses", null);
                Integer[] statusArray = null;
                if (statuses != null) {
                    statusArray = new Integer[statuses.length()];
                    for (int i = 0; i < statuses.length(); i++) {
                        statusArray[i] = statuses.getInt(i);
                    }
                }

                return Pair.of(createScreenReport(countryId, startDate, endDate, productsArray, statusArray, Configuration.getDefaultLocale()), "Reporte_pantallas_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_DE_PANTALLAS_RECORRIDAS: {
                String countryId = JsonUtil.getStringFromJson(reportProces.getParams(), "countryId", null);

                String startDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null);
                Date startDate = startDateFilter != null ? CUSTOM_FORMAT.parse(startDateFilter) : null;

                String endDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null);
                Date endDate = endDateFilter != null ? CUSTOM_FORMAT.parse(endDateFilter) : null;
                return Pair.of(createTrackScreenReport(countryId, startDate, endDate, Configuration.getDefaultLocale()), "Reporte_pantallas_recorridas_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_SOLICITUDES_EN_PROCESO_EXT_BDS: {
                JSONObject params = reportProces.getParams();
                String countryId = JsonUtil.getStringFromJson(params, "countryId", null);
                Integer documentType = JsonUtil.getIntFromJson(params, "documentType", null);
                String documentNumber = JsonUtil.getStringFromJson(params, "documentNumber", null);

                String producersIdsJson = JsonUtil.getStringFromJson(params, "producers", null);
                Integer[] producersIds = JsonUtil.getIntegerArrayFromJson(producersIdsJson, null);

                String startDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null);
                Date startDate = startDateFilter != null ? CUSTOM_FORMAT.parse(startDateFilter) : null;

                String endDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null);
                Date endDate = endDateFilter != null ? CUSTOM_FORMAT.parse(endDateFilter) : null;

                String applicationStatusesJson = JsonUtil.getStringFromJson(reportProces.getParams(), "loanStatuses", null);
                Integer[] applicationStatusesArray = JsonUtil.getIntegerArrayFromJson(applicationStatusesJson, null);

                String producersNameConcat = JsonUtil.getStringFromJson(params, "producersNameConcat", null);

                return Pair.of(createLoanInProcessReport(reportProces.getRegisterDate(), reportProces.getProcessDate(), countryId, documentType, documentNumber, producersIds, producersNameConcat, startDate, endDate, applicationStatusesArray), "Reporte_solicitudes_en_proceso_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_CREDITOS_A_DESEMBOLSAR_EXT_BDS: {
                JSONObject params = reportProces.getParams();
                String countryId = JsonUtil.getStringFromJson(params, "countryId", null);
                Integer documentType = JsonUtil.getIntFromJson(params, "documentType", null);
                String documentNumber = JsonUtil.getStringFromJson(params, "documentNumber", null);

                String producersIdsJson = JsonUtil.getStringFromJson(params, "producers", null);
                Integer[] producersIds = JsonUtil.getIntegerArrayFromJson(producersIdsJson, null);

                String startDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null);
                Date startDate = startDateFilter != null ? CUSTOM_FORMAT.parse(startDateFilter) : null;

                String endDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null);
                Date endDate = endDateFilter != null ? CUSTOM_FORMAT.parse(endDateFilter) : null;

                String producersNameConcat = JsonUtil.getStringFromJson(params, "producersNameConcat", null);

                return Pair.of(createCreditToDisburseReport(reportProces.getRegisterDate(), reportProces.getProcessDate(), countryId, documentType, documentNumber, producersIds, producersNameConcat, startDate, endDate), "Reporte_creditos_a_desembolsar_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_CREDITOS_DESEMBOLSADOS_EXT_BDS: {
                JSONObject params = reportProces.getParams();
                String countryId = JsonUtil.getStringFromJson(params, "countryId", null);
                Integer documentType = JsonUtil.getIntFromJson(params, "documentType", null);
                String documentNumber = JsonUtil.getStringFromJson(params, "documentNumber", null);

                String producersIdsJson = JsonUtil.getStringFromJson(params, "producers", null);
                Integer[] producersIds = JsonUtil.getIntegerArrayFromJson(producersIdsJson, null);

                String startDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null);
                Date startDate = startDateFilter != null ? CUSTOM_FORMAT.parse(startDateFilter) : null;

                String endDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null);
                Date endDate = endDateFilter != null ? CUSTOM_FORMAT.parse(endDateFilter) : null;

                JSONArray internalStatusesArray = JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "internalStatuses", null);
                if (internalStatusesArray.length() == 0)
                    internalStatusesArray = null;

                String disbursementStartDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "disbursementStartDate", null);
                Date disbursementStartDate = disbursementStartDateFilter != null ? CUSTOM_FORMAT.parse(disbursementStartDateFilter) : null;

                String disbursementEndDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "disbursementEndDate", null);
                Date disbursementEndDate = disbursementEndDateFilter != null ? CUSTOM_FORMAT.parse(disbursementEndDateFilter) : null;

                String producersNameConcat = JsonUtil.getStringFromJson(params, "producersNameConcat", null);

                return Pair.of(createDisbursedCreditReport(reportProces.getRegisterDate(), reportProces.getProcessDate(), countryId, documentType, documentNumber, producersIds, producersNameConcat, startDate, endDate, internalStatusesArray, disbursementStartDate, disbursementEndDate), "Reporte_creditos_desembolsados_" + reportProces.getId() + ".xls");
            }
            case Report.REPORTE_RIESGOS_EXT_BDS: {
                JSONObject params = reportProces.getParams();
                Integer documentType = JsonUtil.getIntFromJson(params, "documentType", null);
                String documentNumber = JsonUtil.getStringFromJson(params, "documentNumber", null);

                String producersJson = JsonUtil.getStringFromJson(params, "producers", null);
                Integer[] producersArray = JsonUtil.getIntegerArrayFromJson(producersJson, null);

                String startDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null);
                Date startDate = startDateFilter != null ? CUSTOM_FORMAT.parse(startDateFilter) : null;

                String endDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null);
                Date endDate = endDateFilter != null ? CUSTOM_FORMAT.parse(endDateFilter) : null;

                String creditSubStatusesJson = JsonUtil.getStringFromJson(reportProces.getParams(), "creditStatuses", null);
                Integer[] creditSubStatusesArray = JsonUtil.getIntegerArrayFromJson(creditSubStatusesJson, null);

                JSONArray internalStatusesArray = JsonUtil.getJsonArrayFromJson(reportProces.getParams(), "internalStatuses", null);
                if (internalStatusesArray.length() == 0)
                    internalStatusesArray = null;

                String applicationStatusesJson = JsonUtil.getStringFromJson(reportProces.getParams(), "loanStatuses", null);
                Integer[] applicationStatusesArray = JsonUtil.getIntegerArrayFromJson(applicationStatusesJson, null);

                String lastExecutionStartDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "lastExecutionStartDate", null);
                Date lastExecutionStartDate = lastExecutionStartDateFilter != null ? CUSTOM_FORMAT.parse(lastExecutionStartDateFilter) : null;

                String lastExecutionEndDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "lastExecutionEndDate", null);
                Date lastExecutionEndDate = lastExecutionEndDateFilter != null ? CUSTOM_FORMAT.parse(lastExecutionEndDateFilter) : null;

                return Pair.of(createRiskReport(documentType, documentNumber, producersArray, startDate, endDate, creditSubStatusesArray, internalStatusesArray,
                        applicationStatusesArray, lastExecutionStartDate, lastExecutionEndDate), "Reporte_riesgos_" + reportProces.getId() + ".csv");
            }
            case Report.REPORTE_SOLICITUDES_EN_PROCESO_EXT_FDLM: {
                JSONObject params = reportProces.getParams();
                String countryId = JsonUtil.getStringFromJson(params, "countryId", null);
                Integer documentType = JsonUtil.getIntFromJson(params, "documentType", null);
                String documentNumber = JsonUtil.getStringFromJson(params, "documentNumber", null);
                String lastname = JsonUtil.getStringFromJson(params, "lastname", null);

                String startDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "startDate", null);
                Date startDate = startDateFilter != null ? CUSTOM_FORMAT.parse(startDateFilter) : null;

                String endDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "endDate", null);
                Date endDate = endDateFilter != null ? CUSTOM_FORMAT.parse(endDateFilter) : null;

                String updatedStartDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "updatedStartDate", null);
                Date updatedStartDate = updatedStartDateFilter != null ? CUSTOM_FORMAT.parse(updatedStartDateFilter) : null;

                String updatedEndDateFilter = JsonUtil.getStringFromJson(reportProces.getParams(), "updatedEndDate", null);
                Date updatedEndDate = updatedEndDateFilter != null ? CUSTOM_FORMAT.parse(updatedEndDateFilter) : null;

                String applicationStatusesJson = JsonUtil.getStringFromJson(reportProces.getParams(), "loanStatuses", null);
                Integer[] applicationStatusesArray = JsonUtil.getIntegerArrayFromJson(applicationStatusesJson, null);

                return Pair.of(createLoanInProcessReportFDLM(reportProces.getRegisterDate(), reportProces.getProcessDate(), countryId, documentType, documentNumber, lastname, startDate, endDate, updatedStartDate, updatedEndDate, applicationStatusesArray), "Reporte_solicitudes_en_proceso_" + reportProces.getId() + ".xls");
            }
        }
        return null;
    }

    @Override
    public ReportProces createReporteSolicitudesBo(Integer userId, Date startDate, Date endDate, String country) throws Exception {
        JSONObject params = new JSONObject();
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        List<Integer> arr = new ArrayList<>();
        if (Integer.valueOf(country).equals(0)) {
            for (CountryParam countryParam : catalogService.getCountryParams()) {
                arr.add(countryParam.getId());
            }
        } else {
            arr.add(Integer.valueOf(country));
        }
        params.put("countries", arr);
        Integer reportProcesId = reportsDao.registerReportProces(Report.REPORTE_SOLICITUDES_BO, userId, params);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcesId);
        reportProces.setReportId(Report.REPORTE_SOLICITUDES_BO);
        reportProces.setUserId(userId);
        reportProces.setParams(params);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    @Override
    public ReportProces createReporteSolicitudesLight(Integer userId, Date startDate, Date endDate, Integer[] countries, Integer[] entities, int origin) throws Exception {
        JSONObject params = new JSONObject();
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        List<Integer> arr = new ArrayList<>();
        if (countries == null || countries.length == 0) {
            for (CountryParam countryParam : catalogService.getCountryParams()) {
                arr.add(countryParam.getId());
            }
        } else {
            arr.addAll(Arrays.asList(countries));
        }
        params.put("countries", arr);
        params.put("entities", entities != null ? new Gson().toJson(entities) : null);
        params.put("origin", origin);
        Integer reportProcesId = reportsDao.registerReportProces(Report.REPORTE_SOLICITUDES_LIGHT, userId, params);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcesId);
        reportProces.setReportId(Report.REPORTE_SOLICITUDES_LIGHT);
        reportProces.setUserId(userId);
        reportProces.setParams(params);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    @Override
    public ReportProces createReporteSolicitudesLight(Integer userId, Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate, Date startDate2, Date endDate2, Integer[] countries, Integer[] entities, Integer[] products, int origin) throws Exception {
        JSONObject params = new JSONObject();
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        params.put("startDate2", startDate2 != null ? CUSTOM_FORMAT.format(startDate2) : null);
        params.put("endDate2", endDate2 != null ? CUSTOM_FORMAT.format(endDate2) : null);
        List<Integer> arr = new ArrayList<>();
        if (countries == null || countries.length == 0) {
            for (CountryParam countryParam : catalogService.getCountryParams()) {
                arr.add(countryParam.getId());
            }
        } else {
            arr.addAll(Arrays.asList(countries));
        }
        params.put("countries", arr);
        params.put("entities", entities != null ? new Gson().toJson(entities) : null);
        params.put("products", products != null ? new Gson().toJson(products) : null);
        params.put("origin", origin);
        params.put("minAge", minAge);
        params.put("maxAge", maxAge);
        params.put("requestType", requestType);
        params.put("cardType", cardType);
        params.put("isBanBif", true);
        Integer reportProcesId = reportsDao.registerReportProces(Report.REPORTE_SOLICITUDES_LIGHT, userId, params);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcesId);
        reportProces.setReportId(Report.REPORTE_SOLICITUDES_LIGHT);
        reportProces.setUserId(userId);
        reportProces.setParams(params);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    @Override
    public ReportProces createReporteFunnelV3(Integer userId, Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate, Date startDate2, Date endDate2, Integer[] countries, Integer[] entities, Integer[] products,Integer[] steps, Integer base, int origin) throws Exception {
        return createReporteFunnelV3(userId, minAge, maxAge, requestType, cardType, startDate, endDate, startDate2, endDate2, countries, entities, products, steps, base, origin, null,null,null,null, null, null);
    }

    @Override
    public ReportProces createReporteFunnelV3(Integer userId, Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate, Date startDate2, Date endDate2, Integer[] countries, Integer[] entities, Integer[] products,Integer[] steps, Integer base, int origin, Integer customReportId ,List<String> utmSources,List<String> utmMedium,List<String> utmCampaign,List<String> utmContent,List<Integer> entityProductParams) throws Exception {
        JSONObject params = new JSONObject();
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        params.put("startDate2", startDate2 != null ? CUSTOM_FORMAT.format(startDate2) : null);
        params.put("endDate2", endDate2 != null ? CUSTOM_FORMAT.format(endDate2) : null);
        List<Integer> arr = new ArrayList<>();
        if (countries == null || countries.length == 0) {
            for (CountryParam countryParam : catalogService.getCountryParams()) {
                arr.add(countryParam.getId());
            }
        } else {
            arr.addAll(Arrays.asList(countries));
        }
        params.put("countries", arr);
        params.put("entities", entities != null ? new Gson().toJson(entities) : null);
        params.put("products", products != null ? new Gson().toJson(products) : null);
        params.put("origin", origin);
        params.put("minAge", minAge);
        params.put("maxAge", maxAge);
        params.put("requestType", requestType);
        params.put("cardType", cardType);
        params.put("isBanBif", false);
        params.put("steps", steps);
        params.put("utmSources", utmSources);
        params.put("utmMedium", utmMedium);
        params.put("utmCampaign", utmCampaign);
        params.put("utmContent", utmContent);
        params.put("entityProductParams", entityProductParams);

        Integer reportProcessId = reportsDao.registerReportProces(customReportId != null ? customReportId : Report.REPORTE_FUNNELV3, userId, params);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcessId);
        reportProces.setReportId(customReportId != null ? customReportId : Report.REPORTE_FUNNELV3);
        reportProces.setUserId(userId);
        reportProces.setParams(params);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    @Override
    public ReportProces createReporteOriginacionesBo(Integer userId, Date startDate, Date endDate, String countries, String symbol, boolean internationalCurrency) throws Exception {
        JSONObject params = new JSONObject();
        params.put("startDate", startDate != null ? startDate.getTime() : null);
        params.put("endDate", endDate != null ? endDate.getTime() : null);
        params.put("countries", countries);
        params.put("symbol", symbol);
        params.put("internationalCurrency", internationalCurrency);
        Integer reportProcesId = reportsDao.registerReportProces(Report.REPORTE_ORIGINACIONES_BO, userId, params);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcesId);
        reportProces.setReportId(Report.REPORTE_ORIGINACIONES_BO);
        reportProces.setUserId(userId);
        reportProces.setParams(params);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    @Override
    public String createReportDownloadUrl(int reportProcesId) throws Exception {
        JSONObject params = new JSONObject();
        params.put("reportProcesId", reportProcesId);

        return Configuration.getClientDomain() + "/public/report/download/" + CryptoUtil.encrypt(params.toString());
    }

    private Cell createCell(Row row, int column, CellStyle style) {
        Cell cell = row.createCell(column);
        if (style != null)
            cell.setCellStyle(style);
        return cell;
    }

    @Override
    public byte[] createLoanDetailReport() throws Exception {
        List<LoanDetailsReport> loans = creditDao.generateLoanDetailsReport(catalogService);

        if (loans != null) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Reporte de Créditos");

            // Cell Styles
            CellStyle headStyle = workbook.createCellStyle();
            headStyle.setAlignment(HorizontalAlignment.CENTER);
            headStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
            headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
            Font headStyleFont = workbook.createFont();
            headStyleFont.setFontHeightInPoints((short) 8);
            headStyleFont.setBold(true);
            headStyleFont.setColor(IndexedColors.WHITE.getIndex());
            headStyle.setFont(headStyleFont);

            CellStyle textStyle = workbook.createCellStyle();
            Font textStyleFont = workbook.createFont();
            textStyleFont.setFontHeightInPoints((short) 8);
            textStyle.setFont(textStyleFont);

            CellStyle quantityStyle = workbook.createCellStyle();
            Font quantityStyleFont = workbook.createFont();
            quantityStyleFont.setFontHeightInPoints((short) 8);
            quantityStyle.setFont(quantityStyleFont);
            quantityStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle filterStyle = workbook.createCellStyle();
            Font filterStyleFont = workbook.createFont();
            filterStyleFont.setFontHeightInPoints((short) 8);
            filterStyle.setFont(filterStyleFont);

            CellStyle valueStyle = workbook.createCellStyle();
            Font valueStyleFont = workbook.createFont();
            valueStyleFont.setFontHeightInPoints((short) 9);
            valueStyle.setFont(valueStyleFont);
            valueStyle.setAlignment(HorizontalAlignment.LEFT);

            // Paint head period row
            Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            headRow.setHeightInPoints(22);
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Código de Crédito");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Id de Crédito");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Producto");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Id de Cuota");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Fecha de Vencimiento");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Fecha de Pago");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Status");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Capital");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Capital Pendiente");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Interés");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Interés Pendiente");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Interés I.G.V.");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Interés Pendiente I.G.V.");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Interés Moratorio Pendiente");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Interés Moratorio Pendiente I.G.V.");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Cargos Moratorios");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Cargos Moratorios Pendientes");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Cargos Moratorios I.G.V.");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Cargos Moratorios Pendientes I.G.V.");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Monto de Cuota");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Cuota Pendiente");
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Capital Remanente");

            for (LoanDetailsReport loan : loans) {
                Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getCreditCode() != null ? loan.getCreditCode() : "");
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getCreditId());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getProduct().getName());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getInstallmentId());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getDueDate());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getPaymentDate());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(messageSource.getMessage(catalogService.getInstallmentStatusesById(loan.getInstallmentStatus()).getStatusKey(), null, Configuration.getDefaultLocale()));

                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getInstallmentCapital());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getPendingInstallmentCapital());

                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getInterest());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getPendingInterest());

                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getInterestTax());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getPendingInterestTax());

                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getPendingMoratoriumInterest());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getPendingMoratoriumInterestTax());

                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getMoratoriumCharge());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getPendingMoratoriumCharge());

                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getMoratoriumChargeTax());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getPendingMoratoriumChargeTax());

                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getInstallmentAmount());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getPendingInstallmentAmount());
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(loan.getRemainingCapital());
            }

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();

            return outStream.toByteArray();
        }

        return null;
    }

    @Override
    public byte[] createApproveCreditsReport(List<Credit> credits) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileService.getAssociatedFile(APPROVE_CONSOLIDATION_CREDITS_REPORT));
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        int fileCount = 7;
        Row row = null;

        // Paint head row
        CellStyle valueStyle = workbook.createCellStyle();
        Font valueStyleFont = workbook.createFont();
        valueStyleFont.setFontHeightInPoints((short) 9);
        valueStyle.setFont(valueStyleFont);
        valueStyle.setAlignment(HorizontalAlignment.LEFT);
        valueStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("@"));

        if (credits == null || credits.size() == 0) {
            row = sheet.getRow(fileCount);
            createCell(row, 0, valueStyle).setCellValue("No existe registros a mostrar");
        } else {
            for (Credit credit : credits) {

                row = sheet.getRow(fileCount);


                String documentNumber = credit.getPersonDocumentNumber();
                String contactDate = "---";
                if (credit.getRegisterDate() != null) {
                    contactDate = utilService.dateFormat(credit.getRegisterDate());
                }
                EntityProductParams entityProductParams = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());

                String clientName = credit.getFullName();
                String ammount = String.valueOf(credit.getAmount());
                String term = credit.getInstallments() + "";
                String rate = String.valueOf(credit.getEffectiveAnnualRate());
                String payDay = "---";
                if (credit.getDueDate() != null) {
                    payDay = utilService.dateFormat(credit.getDueDate());
                }
                String callCenter = "SOLVEN";
                String callCenterPerson = "kpena";

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), Configuration.getDefaultLocale());

                if (loanApplication != null && loanApplication.getCreditAnalystSysUserId() != null) {
                    Integer analystId = loanApplication.getCreditAnalystSysUserId();
                    SysUser sysUser = sysuserDao.getSysUserById(analystId);
                    if (sysUser != null) {
                        callCenterPerson = sysUser.getUserName();
                    }
                }

                String procedureType = "---";
                PreApprovedInfo preApprovedInfo = loanApplicationDao.getApprovedLoanApplication(credit.getLoanApplicationId(), Configuration.getDefaultLocale())
                        .stream().filter(p -> p.getEntity().getId().equals(credit.getEntity().getId())).findFirst().orElse(null);
                if (preApprovedInfo != null && preApprovedInfo.getCluster() != null) {
                    List<String> possibleValues = Arrays.asList("REG", "SEN", "TN1", "TN2", "TN3");
                    procedureType = possibleValues.stream().filter(pv -> preApprovedInfo.getCluster().toLowerCase().contains(pv.toLowerCase())).findFirst().orElse(null);
                    if (procedureType == null)
                        procedureType = preApprovedInfo.getCluster();
                }

                String disbursement = "---";
                if (entityProductParams.getDisbursementType() != null) {
                    switch (entityProductParams.getDisbursementType()) {
                        case EntityProductParams.DISBURSEMENT_TYPE_DEPOSIT:
                            disbursement = "TRANSFERENCIA";
                            break;
                        case EntityProductParams.DISBURSEMENT_TYPE_RETIREMNT:
                            disbursement = "PRESENCIAL";
                            break;
                    }
                }

                String sendType = "FISICO";

                String address = null;
                Direccion direccion = personDao.getDisggregatedAddress(credit.getPersonId(), "H");
                PersonContactInformation contactInfo = personDao.getPersonContactInformation(Configuration.getDefaultLocale(), credit.getPersonId());
                if (direccion != null) {
                    address = direccion.getDireccionCompleta();
                } else if (contactInfo != null) {
                    address = contactInfo.getFullAddress();
                }

                String department = null;
                String province = null;
                String district = null;
                Ubigeo ubigeo;
                if (direccion != null && direccion.getUbigeo() != null && (ubigeo = catalogService.getUbigeo(direccion.getUbigeo())) != null) {
                    department = ubigeo.getDepartment().getName();
                    province = ubigeo.getProvince().getName();
                    district = ubigeo.getDistrict().getName();
                } else if (contactInfo != null && contactInfo.getAddressUbigeo() != null) {
                    department = contactInfo.getAddressUbigeo().getDepartment().getName();
                    province = contactInfo.getAddressUbigeo().getProvince().getName();
                    district = contactInfo.getAddressUbigeo().getDistrict().getName();
                }

                String phoneNumber = null;
                String email = null;
                User user = userDao.getUser(credit.getUserId());
                phoneNumber = user.getPhoneNumber();
                email = user.getEmail();

                String bank = null;
                String bankAccount = null;
                String bankAccountCci = null;
                PersonBankAccountInformation bankAccountInfo = personDao.getPersonBankAccountInformationByCredit(Configuration.getDefaultLocale(), credit.getPersonId(), credit.getId());
                if (bankAccountInfo != null) {
                    bank = bankAccountInfo.getBank() != null ? bankAccountInfo.getBank().getName() : null;
                    bankAccount = bankAccountInfo.getBankAccount();
                    bankAccountCci = bankAccountInfo.getCciCode();
                }

                //CLIENTE_DNI
                if (documentNumber != null)
                    createCell(row, 0, valueStyle).setCellValue(documentNumber.toUpperCase());
                //FECHA DE CONTACTO
                if (contactDate != null)
                    createCell(row, 1, valueStyle).setCellValue(contactDate);
                //NOMBRE CLIENTE
                if (clientName != null)
                    createCell(row, 2, valueStyle).setCellValue(clientName.toUpperCase());
                //MONTO
                if (ammount != null)
                    createCell(row, 3, valueStyle).setCellValue(ammount);
                //PLAZO
                if (term != null)
                    createCell(row, 4, valueStyle).setCellValue(term);
                //TASA
                if (rate != null)
                    createCell(row, 5, valueStyle).setCellValue(rate);
                //DIA DE PAGO
                if (payDay != null)
                    createCell(row, 6, valueStyle).setCellValue(payDay.toUpperCase());
                //CALL CENTER
                if (callCenter != null)
                    createCell(row, 7, valueStyle).setCellValue(callCenter);
                //EJECUTIVO CALL CENTER
                if (callCenterPerson != null)
                    createCell(row, 8, valueStyle).setCellValue(callCenterPerson);
                //TIPO DE TRAMITE
                createCell(row, 9, valueStyle).setCellValue(procedureType);
                //TIPO DE DESEMBOLSO
                if (disbursement != null)
                    createCell(row, 10, valueStyle).setCellValue(disbursement.toUpperCase());
                //BANCO
                if (bank != null)
                    createCell(row, 11, valueStyle).setCellValue(bank);
                //CUENTA BANCARIA
                if (bankAccount != null)
                    createCell(row, 12, valueStyle).setCellValue(bankAccount);
                //CUENTA INTERBANCARIA
                if (bankAccountCci != null)
                    createCell(row, 13, valueStyle).setCellValue(bankAccountCci);
                //TIPO DEENVIO
                if (sendType != null)
                    createCell(row, 32, valueStyle).setCellValue(sendType);
                //DIRECCION
                if (address != null)
                    createCell(row, 33, valueStyle).setCellValue(address);
                //DEPARTAMENTO
                if (department != null)
                    createCell(row, 34, valueStyle).setCellValue(department);
                //PROVINCIA
                if (province != null)
                    createCell(row, 35, valueStyle).setCellValue(province);
                //DISTRITO
                if (district != null)
                    createCell(row, 36, valueStyle).setCellValue(district);
                //TELEFONO
                if (phoneNumber != null)
                    createCell(row, 37, valueStyle).setCellValue(phoneNumber);
                //EMAIL
                if (email != null)
                    createCell(row, 38, valueStyle).setCellValue(email);

                fileCount++;
            }
        }

        inputStream.close();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createFunnelReport(Date startDate, Date endDate, Integer dateType, String entities, String products, String source, String medium, String campaign, Integer origin, String countries) throws Exception {

        AnalyticsReporting service = googleAnalyticsReportingService.initializeAnalyticsReporting();
        List<Pair<Date, Date>> periodDates = new ArrayList<>();
        List<String> strPeriods = new ArrayList<>();

        periodDates.add(Pair.of(startDate, endDate));
        Calendar present = Calendar.getInstance();
        String presentDate = new SimpleDateFormat("MMM yyyy").format(present.getTime()).substring(0, 1).toUpperCase() + new SimpleDateFormat("MMM yyyy").format(present.getTime()).substring(1);
        strPeriods.add(presentDate);

        XSSFWorkbook workbook = new XSSFWorkbook();

        JSONArray jsonEntities = new JSONArray(entities);
        JSONArray jsonCountries = new JSONArray(countries);

        if (jsonEntities.length() > 0) {

            Entity entity = null;
            if (jsonEntities.length() == 1)
                entity = catalogService.getEntity(jsonEntities.getInt(0));

            List<String> gaViews = new ArrayList<>();
            for (Object jsonEntity : jsonEntities) {

                switch ((int) jsonEntity) {
                    case Entity.AFFIRM:
                        gaViews.add(Configuration.GA_LOCAL);
                        break;
                    case Entity.ABACO:
                        gaViews.add(Configuration.GA_ABACO);
                        break;
                    case Entity.RIPLEY:
                        gaViews.add(Configuration.GA_RIPLEY);
                        break;
                    case Entity.ACCESO:
                        gaViews.add(Configuration.GA_ACCESO);
                        break;
                    case Entity.EFL:
                        gaViews.add(Configuration.GA_EFL);
                        break;
                    case Entity.BF:
                        gaViews.add(Configuration.GA_BF);
                        break;
                    case Entity.EFECTIVA:
                        gaViews.add(Configuration.GA_EFECTIVA);
                        break;
                    case Entity.COMPARTAMOS:
                        gaViews.add(Configuration.GA_COMPARTAMOS);
                        break;
                    case Entity.CAJASULLANA:
                        gaViews.add(Configuration.GA_CAJA_SULLANA);
                        break;
                    case Entity.CAJA_LOS_ANDES:
                        gaViews.add(Configuration.GA_CAJA_LOS_ANDES);
                        break;
                    case Entity.MULTIFINANZAS:
                        gaViews.add(Configuration.GA_MULTIFINANZAS);
                        break;
                    case Entity.WENANCE:
                        gaViews.add(Configuration.GA_WENANCE);
                        break;
                    case Entity.AELU:
                        gaViews.add(Configuration.GA_AELU);
                        break;
                    default:
                        gaViews.add(Configuration.GA_LOCAL);
                        break;
                }

            }

            responseGoogleAnalytics = googleAnalyticsReportingService.getReport(gaViews, service, periodDates);
            processGoogleAnalyticsReport(strPeriods);

            Currency currency = jsonCountries.length() > 1 ? catalogService.getCurrency(Currency.USD) : catalogService.getCountryParam(jsonCountries.getInt(0)).getCurrency();

            List<FunnelReportSection> marketplaceSheet = reportsDao.getFunnelReport(startDate, endDate, dateType, jsonEntities, new JSONArray(products), source, medium, campaign, jsonCountries);
            multiSheetFunnelReport(workbook, currency, marketplaceSheet, String.format("%s Brandeado", entity == null ? "" : entity.getShortName() + " - "));

            List<FunnelReportSection> brandingSheet = reportsDao.getFunnelMarketplaceBrandedReport(startDate, endDate, dateType, jsonEntities, new JSONArray(products), source, medium, campaign, jsonCountries);
            multiSheetFunnelReport(workbook, currency, brandingSheet, String.format("%s Marketplace", entity == null ? "" : entity.getShortName() + " - "));

        } else {

            responseGoogleAnalytics = googleAnalyticsReportingService.getReport(Configuration.GA_MARKETPLACE, service, periodDates);
            processGoogleAnalyticsReport(strPeriods);

            Currency currency = jsonCountries.length() > 1 ? catalogService.getCurrency(Currency.USD) : catalogService.getCountryParam(jsonCountries.getInt(0)).getCurrency();

            List<FunnelReportSection> marketplaceSheet = reportsDao.getFunnelReport(startDate, endDate, dateType, jsonEntities, new JSONArray(products), source, medium, campaign, jsonCountries);
            multiSheetFunnelReport(workbook, currency, marketplaceSheet, "Marketplace");

        }

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();
            return outStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }

        return null;

    }


    private void multiSheetFunnelReport(XSSFWorkbook workbook, Currency currency, List<FunnelReportSection> sections, String sheetTitle) throws Exception {

        Calendar present = Calendar.getInstance();
//        String presentDate = new SimpleDateFormat("MMM yyyy").format(present.getTime()).substring(0, 1).toUpperCase() + new SimpleDateFormat("MMM yyyy").format(present.getTime()).substring(1);

        final List<Integer> statusIndexs = Arrays.asList(0, 2, 5, 8, 11, 14);
        Double sumPercentage;

        XSSFSheet sheet = workbook.createSheet(sheetTitle);
        boolean isMarketplaceSheet = workbook.getSheetIndex(sheet) == 0;
        boolean isBrandingSheet = workbook.getSheetIndex(sheet) == 1;
        logger.debug(String.format("isMarketplaceSheet: %s. isBrandingSheet: %s", isMarketplaceSheet, isBrandingSheet));

        sheet.setZoom(2, 3);
        int columnSpace = (isMarketplaceSheet) ? 13 : 3;

        for (int i = 0; i < columnSpace; ++i) {
            sheet.setColumnWidth(i, 20 * 128);
        }

        for (int i = columnSpace; i <= 20; ++i) {
            sheet.setColumnWidth(i, 20 * 256);
        }

        /*Styles for column of loan status*/
        CellStyle statusStyle = workbook.createCellStyle();
        statusStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        statusStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        statusStyle.setBorderRight(BorderStyle.THIN);
        statusStyle.setBorderLeft(BorderStyle.THIN);
        statusStyle.setBorderTop(BorderStyle.THIN);
        statusStyle.setBorderBottom(BorderStyle.THIN);
        statusStyle.setAlignment(HorizontalAlignment.CENTER);

        Font headStatusFont = workbook.createFont();
        headStatusFont.setFontHeightInPoints((short) 11);
        headStatusFont.setBold(true);
        headStatusFont.setColor(IndexedColors.WHITE.getIndex());
        statusStyle.setFont(headStatusFont);

        /*Styles for column Incomplete status */
        CellStyle incompleteStyle = workbook.createCellStyle();
        incompleteStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        incompleteStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        incompleteStyle.setBorderRight(BorderStyle.THIN);
        incompleteStyle.setBorderLeft(BorderStyle.THIN);
        incompleteStyle.setBorderTop(BorderStyle.THIN);
        incompleteStyle.setBorderBottom(BorderStyle.THIN);
        incompleteStyle.setAlignment(HorizontalAlignment.CENTER);

        Font imcompleteFont = workbook.createFont();
        imcompleteFont.setFontHeightInPoints((short) 11);
        imcompleteFont.setBold(true);
        incompleteStyle.setFont(imcompleteFont);

        /*Styles for column Reject status */
        CellStyle rejectStyle = workbook.createCellStyle();
        rejectStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        rejectStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        rejectStyle.setBorderRight(BorderStyle.THIN);
        rejectStyle.setBorderLeft(BorderStyle.THIN);
        rejectStyle.setBorderTop(BorderStyle.THIN);
        rejectStyle.setBorderBottom(BorderStyle.THIN);
        rejectStyle.setAlignment(HorizontalAlignment.CENTER);

        Font rejectFont = workbook.createFont();
        rejectFont.setFontHeightInPoints((short) 11);
        rejectFont.setBold(true);
        rejectFont.setColor(IndexedColors.WHITE.getIndex());
        rejectStyle.setFont(rejectFont);

        /*Styles for content cells*/
        CellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setBorderRight(BorderStyle.THIN);
        contentStyle.setBorderLeft(BorderStyle.THIN);
        contentStyle.setBorderTop(BorderStyle.THIN);
        contentStyle.setBorderBottom(BorderStyle.THIN);
        contentStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle percentagePreviousStyle = workbook.createCellStyle();
        percentagePreviousStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        percentagePreviousStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        percentagePreviousStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        percentagePreviousStyle.setBorderRight(BorderStyle.THIN);
        percentagePreviousStyle.setBorderLeft(BorderStyle.THIN);
        percentagePreviousStyle.setBorderTop(BorderStyle.THIN);
        percentagePreviousStyle.setBorderBottom(BorderStyle.THIN);
        percentagePreviousStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle percentageNewStyle = workbook.createCellStyle();
        percentageNewStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        percentageNewStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        percentageNewStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        percentageNewStyle.setBorderRight(BorderStyle.THIN);
        percentageNewStyle.setBorderLeft(BorderStyle.THIN);
        percentageNewStyle.setBorderTop(BorderStyle.THIN);
        percentageNewStyle.setBorderBottom(BorderStyle.THIN);
        percentageNewStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle simplePercentageStyle = workbook.createCellStyle();
        simplePercentageStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        Font percentageFont = workbook.createFont();
        percentageFont.setFontHeightInPoints((short) 8);
        simplePercentageStyle.setFont(percentageFont);


        /*Add styles for cells used like lines*/
        CellStyle verticalLineStyle = workbook.createCellStyle();
        verticalLineStyle.setBorderRight(BorderStyle.MEDIUM);

        CellStyle horizonLineStyle = workbook.createCellStyle();
        horizonLineStyle.setBorderTop(BorderStyle.MEDIUM);

        CellStyle cornerRightStyle = workbook.createCellStyle();
        cornerRightStyle.setBorderTop(BorderStyle.MEDIUM);
        cornerRightStyle.setBorderRight(BorderStyle.MEDIUM);

        CellStyle cornerLefttStyle = workbook.createCellStyle();
        cornerLefttStyle.setBorderTop(BorderStyle.MEDIUM);
        cornerLefttStyle.setBorderLeft(BorderStyle.MEDIUM);

        Row row1 = sheet.createRow(0);
        Row row2 = sheet.createRow(1);

        createCell(row1, 0, percentageNewStyle).setCellValue("");
        createCell(row2, 0, percentagePreviousStyle).setCellValue("");

        if (isMarketplaceSheet) {
            createCell(row1, 1, null).setCellValue("% s/ solicitudes iniciadas");
        } else if (isBrandingSheet) {
            createCell(row1, 1, null).setCellValue("% s/ solicitudes pre-evaluadas");
        }
//        createCell(row1, 3, null).setCellValue(presentDate);
        createCell(row2, 1, null).setCellValue("% s/ estado anterior");

        Integer rowNumber = 2;
        int statusColumn = (isMarketplaceSheet) ? 15 : 6;

        if (isMarketplaceSheet) {
            createCell(row2, statusColumn, statusStyle).setCellValue("Visitas");
            sheet.addMergedRegion(new CellRangeAddress(row2.getRowNum(), row2.getRowNum(), statusColumn, statusColumn + 1));

            Row rowGA = sheet.createRow(rowNumber++);
            createCell(rowGA, statusColumn, contentStyle).setCellValue(totalGAResults.get("Usuarios (visitas)").intValue());
            sheet.addMergedRegion(new CellRangeAddress(rowGA.getRowNum(), rowGA.getRowNum(), statusColumn, statusColumn + 1));

            Row visitSpace = sheet.createRow(rowNumber++);
            createCell(visitSpace, statusColumn, verticalLineStyle).setCellValue("");
        }

        int maxSpaces = 2;
        List<Integer> skippedBrandingTables = Arrays.asList(3);//comenzando de 0

        for (int i = 0; i < statusIndexs.size(); i++) {
            FunnelReportSection section = sections.get(statusIndexs.get(i));

            Row rowSectionStatus = sheet.createRow(rowNumber++);
            createCell(rowSectionStatus, statusColumn, statusStyle).setCellValue(section.getTitle());
            sheet.addMergedRegion(new CellRangeAddress(rowSectionStatus.getRowNum(), rowSectionStatus.getRowNum(), statusColumn, statusColumn + 1));


            switch (statusIndexs.get(i)) {
                case 2:
                    if (isMarketplaceSheet) {
                        createCell(rowSectionStatus, statusColumn + 3, incompleteStyle).setCellValue(sections.get(3).getTitle() == null ? "" : sections.get(3).getTitle());
                    }
                    createCell(rowSectionStatus, statusColumn - 3, rejectStyle).setCellValue(sections.get(1).getTitle() == null ? "" : sections.get(1).getTitle());
                    break;
                case 5:
                    createCell(rowSectionStatus, statusColumn + 3, incompleteStyle).setCellValue(sections.get(6).getTitle() == null ? "" : sections.get(6).getTitle());
                    createCell(rowSectionStatus, statusColumn - 3, rejectStyle).setCellValue(sections.get(4).getTitle() == null ? "" : sections.get(4).getTitle());
                    break;
                case 8:
                    createCell(rowSectionStatus, statusColumn + 3, incompleteStyle).setCellValue(sections.get(9).getTitle() == null ? "" : sections.get(9).getTitle());
                    createCell(rowSectionStatus, statusColumn - 3, rejectStyle).setCellValue(sections.get(7).getTitle() == null ? "" : sections.get(7).getTitle());
                    maxSpaces = 2;
                    break;
                case 11:
                    createCell(rowSectionStatus, statusColumn + 3, incompleteStyle).setCellValue(sections.get(12).getTitle() == null ? "" : sections.get(12).getTitle());
                    createCell(rowSectionStatus, statusColumn - 3, rejectStyle).setCellValue(sections.get(10).getTitle() == null ? "" : sections.get(10).getTitle());
                    maxSpaces = 2;
                    break;
                case 14:
                    createCell(rowSectionStatus, statusColumn + 3, incompleteStyle).setCellValue(sections.get(15).getTitle() == null ? "" : sections.get(15).getTitle());
                    createCell(rowSectionStatus, statusColumn - 3, rejectStyle).setCellValue(sections.get(13).getTitle() == null ? "" : sections.get(13).getTitle());
                    maxSpaces = 2;
                    break;
            }

            if (statusIndexs.get(i) != 0) {
                CellRangeAddress incompleteRange = new CellRangeAddress(rowSectionStatus.getRowNum(), rowSectionStatus.getRowNum(), statusColumn + 3, statusColumn + 4);
                sheet.addMergedRegion(incompleteRange);
                RegionUtil.setBorderRight(BorderStyle.THIN, incompleteRange, sheet);

                CellRangeAddress rejectRange = new CellRangeAddress(rowSectionStatus.getRowNum(), rowSectionStatus.getRowNum(), statusColumn - 3, statusColumn - 2);
                sheet.addMergedRegion(rejectRange);
                RegionUtil.setBorderRight(BorderStyle.THIN, rejectRange, sheet);
            }

            Row rowCount = sheet.createRow(rowNumber++);
            createCell(rowCount, statusColumn, contentStyle).setCellValue(section.getCount());
            if (statusIndexs.get(i) == 0) {
                CellRangeAddress cellRangeAddress = new CellRangeAddress(rowCount.getRowNum(), rowCount.getRowNum(), statusColumn, statusColumn + 1);
                sheet.addMergedRegion(cellRangeAddress);
                RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
            } else {
                if (section.getAmount() != null) {
                    createCell(rowCount, statusColumn + 1, contentStyle).setCellValue(utilService.integerMoneyFormat(section.getAmount().intValue(), currency));
                } else {
                    Cell cell = rowCount.createCell(statusColumn - 2);
                    cell.setCellType(CellType.BLANK);
                }

                if ((!skippedBrandingTables.contains(statusIndexs.get(i) - 1) && isBrandingSheet) || isMarketplaceSheet) {
                    createCell(rowCount, statusColumn - 3, contentStyle).setCellValue(sections.get(statusIndexs.get(i) - 1).getCount() == null ? 0 : sections.get(statusIndexs.get(i) - 1).getCount());
                    if (sections.get(statusIndexs.get(i) - 1).getAmount() != null) {
                        createCell(rowCount, statusColumn - 2, contentStyle).setCellValue(utilService.integerMoneyFormat(sections.get(statusIndexs.get(i) - 1).getAmount().intValue(), currency));
                    } else {
                        Cell cell = rowCount.createCell(statusColumn - 2);
                        cell.setCellType(CellType.BLANK);
                    }
                }

                if ((!skippedBrandingTables.contains(statusIndexs.get(i) + 1) && isBrandingSheet) || isMarketplaceSheet) {
                    createCell(rowCount, statusColumn + 3, contentStyle).setCellValue(sections.get(statusIndexs.get(i) + 1).getCount() == null ? 0 : sections.get(statusIndexs.get(i) + 1).getCount());
                    if (sections.get(statusIndexs.get(i) + 1).getAmount() != null) {
                        createCell(rowCount, statusColumn + 4, contentStyle).setCellValue(utilService.integerMoneyFormat(sections.get(statusIndexs.get(i) + 1).getAmount() == null ? 0 : sections.get(statusIndexs.get(i) + 1).getAmount().intValue(), currency));
                    } else {
                        Cell cell = rowCount.createCell(statusColumn + 4);
                        cell.setCellType(CellType.BLANK);
                    }
                }
            }

            Row rowPercentage = sheet.createRow(rowNumber++);
            if (i == 0) {
                int countOfVisits;
                if (isMarketplaceSheet) {
                    countOfVisits = totalGAResults.get("Usuarios (visitas)").intValue();
                } else {
                    countOfVisits = section.getCount();
                }
                createCell(rowPercentage, statusColumn, percentagePreviousStyle).setCellValue(section.getCount() * 1.0 / countOfVisits);
                CellRangeAddress cellRangeAddress = new CellRangeAddress(rowPercentage.getRowNum(), rowPercentage.getRowNum(), statusColumn, statusColumn + 1);
                sheet.addMergedRegion(cellRangeAddress);
                RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
            } else if (i == 1) {
                Integer statusPrevious = sections.get(0).getCount();
                createCell(rowPercentage, statusColumn, percentagePreviousStyle).setCellValue(section.getCount() * 1.0 / statusPrevious);
                CellRangeAddress cellRangeAddress = new CellRangeAddress(rowPercentage.getRowNum(), rowPercentage.getRowNum(), statusColumn, statusColumn + 1);
                sheet.addMergedRegion(cellRangeAddress);
                RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);

                if ((!skippedBrandingTables.contains(statusIndexs.get(i) - 1) && isBrandingSheet) || isMarketplaceSheet) {
                    createCell(rowPercentage, statusColumn - 3, percentagePreviousStyle).setCellValue(sections.get(statusIndexs.get(i) - 1).getCount() == null ? 0 : sections.get(statusIndexs.get(i) - 1).getCount() * 1.0 / statusPrevious);
                    CellRangeAddress rejectRange = new CellRangeAddress(rowPercentage.getRowNum(), rowPercentage.getRowNum(), statusColumn - 3, statusColumn - 2);
                    sheet.addMergedRegion(rejectRange);
                    RegionUtil.setBorderRight(BorderStyle.THIN, rejectRange, sheet);
                }

                if ((!skippedBrandingTables.contains(statusIndexs.get(i) + 1) && isBrandingSheet) || isMarketplaceSheet) {
                    createCell(rowPercentage, statusColumn + 3, percentagePreviousStyle).setCellValue(sections.get(statusIndexs.get(i) + 1).getCount() == null ? 0 : sections.get(statusIndexs.get(i) + 1).getCount() * 1.0 / statusPrevious);
                    CellRangeAddress incompleteRange = new CellRangeAddress(rowPercentage.getRowNum(), rowPercentage.getRowNum(), statusColumn + 3, statusColumn + 4);
                    sheet.addMergedRegion(incompleteRange);
                    RegionUtil.setBorderRight(BorderStyle.THIN, incompleteRange, sheet);
                }
            } else {
                Integer statusPrevious = sections.get(statusIndexs.get(i) - 3).getCount();
                Integer statusNew = sections.get(0).getCount();

                createCell(rowPercentage, statusColumn, percentageNewStyle).setCellValue(section.getCount() * 1.0 / statusNew);
                createCell(rowPercentage, statusColumn + 1, percentagePreviousStyle).setCellValue(section.getCount() * 1.0 / statusPrevious);

                if ((!skippedBrandingTables.contains(statusIndexs.get(i) - 1) && isBrandingSheet) || isMarketplaceSheet) {
                    createCell(rowPercentage, statusColumn - 3, percentageNewStyle).setCellValue(sections.get(statusIndexs.get(i) - 1).getCount() == null ? 0 : sections.get(statusIndexs.get(i) - 1).getCount() * 1.0 / statusNew);
                    createCell(rowPercentage, statusColumn - 2, percentagePreviousStyle).setCellValue(sections.get(statusIndexs.get(i) - 1).getCount() == null ? 0 : sections.get(statusIndexs.get(i) - 1).getCount() * 1.0 / statusPrevious);
                }
                if ((!skippedBrandingTables.contains(statusIndexs.get(i) + 1) && isBrandingSheet) || isMarketplaceSheet) {
                    createCell(rowPercentage, statusColumn + 3, percentageNewStyle).setCellValue(sections.get(statusIndexs.get(i) + 1).getCount() == null ? 0 : sections.get(statusIndexs.get(i) + 1).getCount() * 1.0 / statusNew);
                    createCell(rowPercentage, statusColumn + 4, percentagePreviousStyle).setCellValue(sections.get(statusIndexs.get(i) + 1).getCount() == null ? 0 : sections.get(statusIndexs.get(i) + 1).getCount() * 1.0 / statusPrevious);
                }
            }

            int rejectSize = 0;
            sumPercentage = 0.0;
            switch (statusIndexs.get(i)) {
                case 2:
                case 5:
                case 8:
                case 11:
                    Row rowRejectTitle = sheet.createRow(rowNumber++);
                    createCell(rowRejectTitle, statusColumn - 3, contentStyle).setCellValue("Motivos de Rechazo");
                    CellRangeAddress cellRangeAddress = new CellRangeAddress(rowRejectTitle.getRowNum(), rowRejectTitle.getRowNum(), statusColumn - 3, statusColumn - 2);
                    sheet.addMergedRegion(cellRangeAddress);
                    RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
                    createCell(rowRejectTitle, statusColumn, verticalLineStyle).setCellValue("");

                    if (sections.get(statusIndexs.get(i) - 1).getTableDetail() != null) {
                        for (Tuple3<String, Integer, Double> hardFilter : sections.get(statusIndexs.get(i) - 1).getTableDetail()) {
                            Double percentage = hardFilter.v3;
                            if (percentage >= 0.05) {
                                Row rowRejectReason = sheet.createRow(rowNumber++);
                                if ("hard_filter_id".equals(hardFilter.v1)) {
                                    createCell(rowRejectReason, statusColumn - 3, simplePercentageStyle).setCellValue(catalogService.getHardFilterById(hardFilter.v2).getHardFilterMessage());
                                } else if ("policy_id".equals(hardFilter.v1)) {
                                    createCell(rowRejectReason, statusColumn - 3, simplePercentageStyle).setCellValue(catalogService.getPolicyById(hardFilter.v2).getPolicy());
                                } else if ("offer_rejection_id".equals(hardFilter.v1)) {
                                    createCell(rowRejectReason, statusColumn - 3, simplePercentageStyle).setCellValue(catalogService.getOfferRejectionReason(hardFilter.v2).getReason());
                                } else if ("la_rejection_reason_id".equals(hardFilter.v1)) {
                                    createCell(rowRejectReason, statusColumn - 3, simplePercentageStyle).setCellValue(catalogService.getApplicationRejectionReasons().stream().filter(f -> f.getId().intValue() == hardFilter.v2.intValue()).findFirst().orElse(null).getReason());
                                } else {
                                    throw new Exception("Not mapped filter_id");
                                }
                                createCell(rowRejectReason, statusColumn - 2, simplePercentageStyle).setCellValue(hardFilter.v3);
                                createCell(rowRejectReason, statusColumn, verticalLineStyle).setCellValue("");
                                sumPercentage = sumPercentage + percentage;
                                rejectSize++;
                            }
                        }

                        if (sumPercentage < 1) {
                            Row rowOtherRejection = sheet.createRow(rowNumber++);
                            createCell(rowOtherRejection, statusColumn - 3, simplePercentageStyle).setCellValue("Otros");
                            createCell(rowOtherRejection, statusColumn - 2, simplePercentageStyle).setCellValue(1 - sumPercentage);
                            createCell(rowOtherRejection, statusColumn, verticalLineStyle).setCellValue("");
                            rejectSize++;
                        }

                    }

                    maxSpaces = (rejectSize > MAX_HARD_FILTERS_AND_POLICIES) ? MAX_HARD_FILTERS_AND_POLICIES : rejectSize;
                    break;
            }

            //draw lines vertical and horizontal in diagram
            if (statusIndexs.get(i) != 14) {
                maxSpaces = maxSpaces > 1 ? maxSpaces : 2;
                for (int j = 0; j < maxSpaces; j++) {
                    Row space = sheet.createRow(rowNumber++);
                    if (j < maxSpaces - 1) {
                        createCell(space, statusColumn, verticalLineStyle).setCellValue("");
                    } else {
//                        LEFT LINES
                        createCell(space, statusColumn, cornerRightStyle).setCellValue("");
                        createCell(space, statusColumn - 1, horizonLineStyle).setCellValue("");
                        createCell(space, statusColumn - 2, cornerLefttStyle).setCellValue("");
//                        RIGHT LINES
                        if ((i != 0 && isBrandingSheet) || isMarketplaceSheet) {// i == 0 LA PRIMERA FILA
                            createCell(space, statusColumn + 1, horizonLineStyle).setCellValue("");
                            createCell(space, statusColumn + 2, horizonLineStyle).setCellValue("");
                            createCell(space, statusColumn + 3, cornerRightStyle).setCellValue("");
                        }
                    }

                }
            }

        }
    }

    //    TODO REFACTOR
    private void processGoogleAnalyticsReport(List<String> periods) {
//        HashMap<String, Double> totalGAResults = new HashMap<>();
        totalGAResults = new HashMap<>();

        for (com.google.api.services.analyticsreporting.v4.model.Report report : responseGoogleAnalytics.getReports()) {
            ColumnHeader header = report.getColumnHeader();
            List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
            List<ReportRow> rows = report.getData().getRows();

            if (rows != null) {
                for (ReportRow row : rows) {

                    for (DateRangeValues dateRangeValues : row.getMetrics()) {

                        for (int i = 0; i < dateRangeValues.getValues().size(); i++) {

                            Double newValue = Double.parseDouble(dateRangeValues.getValues().get(i));
                            totalGAResults.computeIfPresent(metricHeaders.get(i).getName(), (k, v) -> (v + newValue));
                            totalGAResults.putIfAbsent(metricHeaders.get(i).getName(), newValue);

                        }

                    }

                }
            } else {

                for (MetricHeaderEntry metricHeaderEntry : metricHeaders) {
                    totalGAResults.put(metricHeaderEntry.getName(), 0.0);
                }

            }
        }

        logger.debug(totalGAResults.toString());
    }


    @Override
    public ReportProces createReporteFunnelBo(Integer userId, Date startDate, Date endDate, Integer dateType, Integer[] entities, Integer[] products, String source, String medium, String campaign, int origin, Integer[] countries) throws Exception {
        Gson gson = new Gson();
        JSONObject params = new JSONObject();
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        params.put("dateType", dateType);
        params.put("entities", entities != null ? new Gson().toJson(entities) : null);
        params.put("products", products != null ? new Gson().toJson(products) : null);
        params.put("source", source);
        params.put("medium", medium);
        params.put("campaign", campaign);
        params.put("origin", origin);
        params.put("countries", countries != null ? new Gson().toJson(countries) : null);
        Integer reportProcesId = reportsDao.registerReportProces(Report.REPORTE_FUNNEL, userId, params);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcesId);
        reportProces.setReportId(Report.REPORTE_FUNNEL);
        reportProces.setUserId(userId);
        reportProces.setParams(params);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    @Override
    public ReportProces createReporteGestionOperadoresBO(Integer userId, String countryId, Date period1From, Date period1To, Date period2From, Date period2To, Integer sysUserId, Integer entityId, Integer productId, String symbol) throws Exception {
        JSONObject params = new JSONObject();
        params.put("countryId", countryId);
        params.put("period1From", period1From != null ? CUSTOM_FORMAT.format(period1From) : null);
        params.put("period1To", period1To != null ? CUSTOM_FORMAT.format(period1To) : null);
        params.put("period2From", period2From != null ? CUSTOM_FORMAT.format(period2From) : null);
        params.put("period2To", period2To != null ? CUSTOM_FORMAT.format(period2To) : null);
        params.put("sysUserId", sysUserId);
        params.put("entityId", entityId);
        params.put("productId", productId);
        params.put("symbol", symbol);
        Integer reportProcesId = reportsDao.registerReportProces(Report.REPORTE_GESTION_OPERADORES, userId, params);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcesId);
        reportProces.setReportId(Report.REPORTE_GESTION_OPERADORES);
        reportProces.setUserId(userId);
        reportProces.setParams(params);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    @Override
    public ReportProces createReportDebtorConsolidation(Integer userId) throws Exception {
        Integer reportProcesId = reportsDao.registerReportProces(Report.REPORTE_CONSOLIDADO_DEUDORES, userId, null);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcesId);
        reportProces.setReportId(Report.REPORTE_CONSOLIDADO_DEUDORES);
        reportProces.setUserId(userId);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    @Override
    public ReportProces createReportPantallas(Integer userId, String countryId, Date startDate, Date endDate, Integer[] products, Integer[] statuses) throws Exception {
        JSONObject params = new JSONObject();
        params.put("countryId", countryId);
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        params.put("products", products != null ? new Gson().toJson(products) : null);
        params.put("statuses", statuses != null ? new Gson().toJson(statuses) : null);
        Integer reportProcesId = reportsDao.registerReportProces(Report.REPORTE_DE_PANTALLAS, userId, params);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcesId);
        reportProces.setReportId(Report.REPORTE_DE_PANTALLAS);
        reportProces.setUserId(userId);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    @Override
    public ReportProces createReportPantallasRecorridas(Integer userId, String countryId, Date startDate, Date endDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("countryId", countryId);
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        Integer reportProcesId = reportsDao.registerReportProces(Report.REPORTE_DE_PANTALLAS_RECORRIDAS, userId, params);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcesId);
        reportProces.setReportId(Report.REPORTE_DE_PANTALLAS_RECORRIDAS);
        reportProces.setUserId(userId);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    @Override
    public ReportProces createReportSolicitudesEnProceso(Integer userId, String countryId, Integer documentType, String documentNumber, Integer[] producers, Date startDate, Date endDate, Integer[] loanStatuses) throws Exception {
        JSONObject params = new JSONObject();
        params.put("countryId", countryId);
        params.put("documentType", documentType);
        params.put("documentNumber", documentNumber);
        params.put("producers", producers != null ? new Gson().toJson(producers) : null);
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        params.put("loanStatuses", loanStatuses != null ? new Gson().toJson(loanStatuses) : null);

        concatProducersNameIfExist(params, producers);

        Integer reportProcessId = reportsDao.registerReportProces(Report.REPORTE_SOLICITUDES_EN_PROCESO_EXT_BDS, userId, params);

        ReportProces reportProcess = new ReportProces();
        reportProcess.setId(reportProcessId);
        reportProcess.setReportId(Report.REPORTE_SOLICITUDES_EN_PROCESO_EXT_BDS);
        reportProcess.setUserId(userId);

        webscrapperService.callReportBot(reportProcess.getId());

        return reportProcess;
    }

    @Override
    public ReportProces createReportSolicitudesEnProcesoFDLM(Integer userId, String countryId, Integer documentType, String documentNumber, String lastname, Date startDate, Date endDate, Date updatedStartDate, Date updatedEndDate, Integer[] loanStatuses) throws Exception {
        JSONObject params = new JSONObject();
        params.put("countryId", countryId);
        params.put("documentType", documentType);
        params.put("documentNumber", documentNumber);
        params.put("lastname", lastname);
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        params.put("updatedStartDate", updatedStartDate != null ? CUSTOM_FORMAT.format(updatedStartDate) : null);
        params.put("updatedEndDate", updatedEndDate != null ? CUSTOM_FORMAT.format(updatedEndDate) : null);
        params.put("loanStatuses", loanStatuses != null ? new Gson().toJson(loanStatuses) : null);

        Integer reportProcessId = reportsDao.registerReportProces(Report.REPORTE_SOLICITUDES_EN_PROCESO_EXT_FDLM, userId, params);

        ReportProces reportProcess = new ReportProces();
        reportProcess.setId(reportProcessId);
        reportProcess.setReportId(Report.REPORTE_SOLICITUDES_EN_PROCESO_EXT_FDLM);
        reportProcess.setUserId(userId);

        webscrapperService.callReportBot(reportProcess.getId());

        return reportProcess;
    }

    private void concatProducersNameIfExist(JSONObject params, Integer[] producersParam) throws Exception{

        if (producersParam != null && producersParam.length > 0) {
            List<UserOfHierarchy> organizersDB = userDao.getUsersOfHierarchy();
            List<UserOfHierarchy> producersDB = new ArrayList<>();

            organizersDB.stream().map(UserOfHierarchy::getProducers).filter(Objects::nonNull).forEach(producersDB::addAll);
            producersDB.addAll(organizersDB);

            String producersNameConcat = "";
            for (Integer id : producersParam) {
                for (UserOfHierarchy user : producersDB) {
                    if (user.getEntityUserId().equals(id)) {
                        producersNameConcat += user.getFirstSurname() + " " + user.getPersonName() + ", ";
                        break;
                    }
                }
            }

            if (!producersNameConcat.isEmpty())
                producersNameConcat = producersNameConcat.substring(0, producersNameConcat.length() - 2);
            
            params.put("producersNameConcat", producersNameConcat);
        }
    }

    @Override
    public ReportProces createReportCreditosADesembolsar(Integer userId, String countryId, Integer documentType, String documentNumber, Integer[] producers, Date startDate, Date endDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("countryId", countryId);
        params.put("documentType", documentType);
        params.put("documentNumber", documentNumber);
        params.put("producers", producers != null ? new Gson().toJson(producers) : null);
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);

        concatProducersNameIfExist(params, producers);

        Integer reportProcessId = reportsDao.registerReportProces(Report.REPORTE_CREDITOS_A_DESEMBOLSAR_EXT_BDS, userId, params);

        ReportProces reportProcess = new ReportProces();
        reportProcess.setId(reportProcessId);
        reportProcess.setReportId(Report.REPORTE_CREDITOS_A_DESEMBOLSAR_EXT_BDS);
        reportProcess.setUserId(userId);

        webscrapperService.callReportBot(reportProcess.getId());

        return reportProcess;
    }

    @Override
    public ReportProces createReportCreditosDesembolsados(Integer userId, String countryId, Integer documentType, String documentNumber, Integer[] producers,
                                                          Date startDate, Date endDate, String[] internalStatuses, Date disbursementStartDate, Date disbursementEndDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("countryId", countryId);
        params.put("documentType", documentType);
        params.put("documentNumber", documentNumber);
        params.put("producers", producers != null ? new Gson().toJson(producers) : null);
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        params.put("internalStatuses", internalStatuses != null ? internalStatuses : null);
        params.put("disbursementStartDate", disbursementStartDate != null ? CUSTOM_FORMAT.format(disbursementStartDate) : null);
        params.put("disbursementEndDate", disbursementEndDate != null ? CUSTOM_FORMAT.format(disbursementEndDate) : null);

        concatProducersNameIfExist(params, producers);

        Integer reportProcessId = reportsDao.registerReportProces(Report.REPORTE_CREDITOS_DESEMBOLSADOS_EXT_BDS, userId, params);

        ReportProces reportProcess = new ReportProces();
        reportProcess.setId(reportProcessId);
        reportProcess.setReportId(Report.REPORTE_CREDITOS_DESEMBOLSADOS_EXT_BDS);
        reportProcess.setUserId(userId);

        webscrapperService.callReportBot(reportProcess.getId());

        return reportProcess;
    }

    @Override
    public ReportProces createReportRiesgos(Integer userId, String countryId, Integer documentType, String documentNumber, Integer[] producers, Date startDate, Date endDate, Integer[] loanStatuses,
                                            String[] internalStatuses, Integer[] creditStatuses, Date lastExecutionStartDate, Date lastExecutionEndDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("countryId", countryId);
        params.put("documentType", documentType);
        params.put("documentNumber", documentNumber);
        params.put("producers", producers != null ? new Gson().toJson(producers) : null);
        params.put("startDate", startDate != null ? CUSTOM_FORMAT.format(startDate) : null);
        params.put("endDate", endDate != null ? CUSTOM_FORMAT.format(endDate) : null);
        params.put("loanStatuses", loanStatuses != null ? new Gson().toJson(loanStatuses) : null);
        params.put("internalStatuses", internalStatuses != null ? internalStatuses : null);
        params.put("creditStatuses", creditStatuses != null ? new Gson().toJson(creditStatuses) : null);
        params.put("lastExecutionStartDate", lastExecutionStartDate != null ? CUSTOM_FORMAT.format(lastExecutionStartDate) : null);
        params.put("lastExecutionEndDate", lastExecutionEndDate != null ? CUSTOM_FORMAT.format(lastExecutionEndDate) : null);

        concatProducersNameIfExist(params, producers);

        Integer reportProcessId = reportsDao.registerReportProces(Report.REPORTE_RIESGOS_EXT_BDS, userId, params);

        ReportProces reportProcess = new ReportProces();
        reportProcess.setId(reportProcessId);
        reportProcess.setReportId(Report.REPORTE_RIESGOS_EXT_BDS);
        reportProcess.setUserId(userId);

        webscrapperService.callReportBot(reportProcess.getId());

        return reportProcess;
    }

    @Override
    public byte[] createOperatorsManagementsReport(String countryId, Date period1From, Date period1To, Date period2From, Date period2To, Integer sysUserId, Integer entityId, Integer productId, String symbol) throws Exception {

        OperatorManagementReport report = reportsDao.getOperatorManagementReport(countryId, period1From, period1To, period2From, period2To, sysUserId, entityId, productId);
        SysUser sysUser = null;
        if (sysUserId != null)
            sysUser = sysuserDao.getSysUserById(sysUserId);
        if (period1From != null && period1To != null) {
            Triple<Integer, Integer, Integer> tokyResult = tokyService.getCallsAndMinutes(period1From, period1To, sysUser != null ? sysUser.getEmail() : null);
            OperatorManagementReportDetail reportDetail = report.getDetailByType(OperatorManagementReportDetail.TYPE_PERIOD_1);
            if (reportDetail != null) {
                reportDetail.setCallings(tokyResult.getLeft() + tokyResult.getMiddle());
                reportDetail.setCallingsContact(tokyResult.getLeft());
                reportDetail.setCallingsNoContact(tokyResult.getMiddle());
                reportDetail.setCallingMinutes(tokyResult.getRight());
            }
        }
        if (period2From != null && period2To != null) {
            Triple<Integer, Integer, Integer> tokyResult = tokyService.getCallsAndMinutes(period2From, period2To, sysUser != null ? sysUser.getEmail() : null);
            OperatorManagementReportDetail reportDetail = report.getDetailByType(OperatorManagementReportDetail.TYPE_PERIOD_2);
            if (reportDetail != null) {
                reportDetail.setCallings(tokyResult.getLeft() + tokyResult.getMiddle());
                reportDetail.setCallingsContact(tokyResult.getLeft());
                reportDetail.setCallingsNoContact(tokyResult.getMiddle());
                reportDetail.setCallingMinutes(tokyResult.getRight());
            }
        }


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Gestión de Operadores");

        // Cell Styles
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 8);
        headStyleFont.setBold(true);
        headStyleFont.setColor(IndexedColors.WHITE.getIndex());
        headStyle.setFont(headStyleFont);

        CellStyle textStyle = workbook.createCellStyle();
        Font textStyleFont = workbook.createFont();
        textStyleFont.setFontHeightInPoints((short) 8);
        textStyle.setFont(textStyleFont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 8);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle filterStyle = workbook.createCellStyle();
        Font filterStyleFont = workbook.createFont();
        filterStyleFont.setFontHeightInPoints((short) 8);
        filterStyle.setFont(filterStyleFont);

        CellStyle operatorHeadStyle = workbook.createCellStyle();
        operatorHeadStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        operatorHeadStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font operatorHeadStyleFont = workbook.createFont();
        operatorHeadStyleFont.setFontHeightInPoints((short) 8);
        operatorHeadStyleFont.setBold(true);
        operatorHeadStyleFont.setColor(IndexedColors.WHITE.getIndex());
        operatorHeadStyle.setFont(operatorHeadStyleFont);

        // Setting widths
        sheet.setColumnWidth(0, 3 * 256);
        sheet.setColumnWidth(1, 30 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);

        // Paint filter row
        Row filterRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        filterRow.setHeightInPoints(50 * 2);
        StringBuilder filter = new StringBuilder("FILTROS:\n");
        filter.append("\tPeriodo 1:\t\t\t");
        filter.append("\n");
        filter.append("\tDesde:\t\t\t");
        filter.append(period1From == null ? "" : new SimpleDateFormat("dd-MM-yyyy").format(period1From));
        filter.append("\tHasta:\t\t\t");
        filter.append(period1To == null ? "" : new SimpleDateFormat("dd-MM-yyyy").format(period1To));
        filter.append("\n");
        filter.append("\tPeriodo 2:\t\t\t");
        filter.append("\n");
        filter.append("\tDesde:\t\t\t");
        filter.append(period2From == null ? "" : new SimpleDateFormat("dd-MM-yyyy").format(period2From));
        filter.append("\tHasta:\t\t\t");
        filter.append(period2To == null ? "" : new SimpleDateFormat("dd-MM-yyyy").format(period2To));
        filter.append("\n");
        filter.append("\tAnalista:\t\t\t");
        filter.append(sysUserId != null ? catalogService.getManagementAnalystSysusers().stream().filter(s -> s.getId() == sysUserId.intValue()).findFirst().get().getFullName() : "Todos");
        filter.append("\n");
        filter.append("\tProducto:\t\t\t");
        filter.append(productId != null ? catalogService.getActiveProducts().stream().filter(product -> product.getId() == productId.intValue()).findFirst().get().getName() : "Todos");
        filter.append("\n");
        filter.append("\tFinanciador:\t\t");
        filter.append(entityId != null ? catalogService.getEntities().stream().filter(entity -> entity.getId() == entityId.intValue()).findFirst().get().getShortName() : "Todos");
        filter.append("\n");

        createCell(filterRow, 0, filterStyle).setCellValue(filter.toString());

        // Paint head period rows
        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
//        headRow.setHeightInPoints(22);
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Indicadores");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Periodo 1");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Periodo 2");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("MTD");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("U6M");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("YTD");

        // Merged cells of filter data
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        // Paint indicator columns
        CellStyle indicatorStyle = workbook.createCellStyle();
        indicatorStyle.cloneStyleFrom(headStyle);
        indicatorStyle.setFillPattern(FillPatternType.FINE_DOTS);
        indicatorStyle.setAlignment(HorizontalAlignment.LEFT);

        int indicatorRow = 0;
//        LLAMADAS
        indicatorRow++;
        Row numberOfCalls = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(numberOfCalls, numberOfCalls.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(numberOfCalls, numberOfCalls.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("Llamadas #");
//        - CONTACTO
        indicatorRow++;
        Row numberOfCallsDetailContact = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(numberOfCallsDetailContact, numberOfCallsDetailContact.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(numberOfCallsDetailContact, numberOfCallsDetailContact.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("        Contacto");
//        - NO CONTACTO
        indicatorRow++;
        Row numberOfCallsDetailNoContact = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(numberOfCallsDetailNoContact, numberOfCallsDetailNoContact.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(numberOfCallsDetailNoContact, numberOfCallsDetailNoContact.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("        No Contacto");
//        LLAMADAS MIN
        indicatorRow++;
        Row minCalls = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(minCalls, minCalls.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(minCalls, minCalls.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("Llamadas min.");
//        GESTIONES ASISTIDAS
        indicatorRow++;
        Row assistedManagement = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(assistedManagement, assistedManagement.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(assistedManagement, assistedManagement.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("Gestiones asistidas (total)");
//        - CONTACTO
        Row assistedManagementDetailContact = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(assistedManagementDetailContact, assistedManagementDetailContact.getPhysicalNumberOfCells(), headStyle).setCellValue("");
        createCell(assistedManagementDetailContact, assistedManagementDetailContact.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("      Contacto");
//        - NO CONTACTO
        Row assistedManagementDetailNoContact = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(assistedManagementDetailNoContact, assistedManagementDetailNoContact.getPhysicalNumberOfCells(), headStyle).setCellValue("");
        createCell(assistedManagementDetailNoContact, assistedManagementDetailNoContact.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("      No Contacto");
//        GESTIONES MANUAL
        indicatorRow++;
        Row manualManagement = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(manualManagement, manualManagement.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(manualManagement, manualManagement.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("Gestiones manual (total)");
//        - CONTACTO
        Row manualManagementDetailContact = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(manualManagementDetailContact, manualManagementDetailContact.getPhysicalNumberOfCells(), headStyle).setCellValue("");
        createCell(manualManagementDetailContact, manualManagementDetailContact.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("      Contacto");
//        - NO CONTACTO
        Row manualManagementDetailNoContact = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(manualManagementDetailNoContact, manualManagementDetailNoContact.getPhysicalNumberOfCells(), headStyle).setCellValue("");
        createCell(manualManagementDetailNoContact, manualManagementDetailNoContact.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("      No Contacto");
//        SEGUIMIENTO
        indicatorRow++;
        Row tracing = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(tracing, tracing.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(tracing, tracing.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("Seguimiento (prox. contacto)");
//        - 1 DIA
        Row tracingOneDay = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(tracingOneDay, tracingOneDay.getPhysicalNumberOfCells(), headStyle).setCellValue("");
        createCell(tracingOneDay, tracingOneDay.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("      1 día");
//        - 2 DIAS
        Row tracingTwoDays = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(tracingTwoDays, tracingTwoDays.getPhysicalNumberOfCells(), headStyle).setCellValue("");
        createCell(tracingTwoDays, tracingTwoDays.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("      2 días");
//        - >2 DIAS
        Row tracingMoreThanTwo = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(tracingMoreThanTwo, tracingMoreThanTwo.getPhysicalNumberOfCells(), headStyle).setCellValue("");
        createCell(tracingMoreThanTwo, tracingMoreThanTwo.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("        > 2 días");
//        DESEMBOLSOS
        indicatorRow++;
        Row numberOfDisbursement = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(numberOfDisbursement, numberOfDisbursement.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(numberOfDisbursement, numberOfDisbursement.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("Desembolsos (#)");
//        DESEMBOLSOS(MONEDA)
        indicatorRow++;
        Row paymentDisbursement = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(paymentDisbursement, paymentDisbursement.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(paymentDisbursement, paymentDisbursement.getPhysicalNumberOfCells(), indicatorStyle).setCellValue(String.format("Desembolsos (%s)", symbol));
//        GESTIONES ASISTIDAS NO ASIGNADAS
        indicatorRow++;
        Row notAssignedAssistedManagement = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(notAssignedAssistedManagement, notAssignedAssistedManagement.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(notAssignedAssistedManagement, notAssignedAssistedManagement.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("Gestiones asistidas no asignadas");
//        GESTIONES MANUALES NO ASIGNADAS
        indicatorRow++;
        Row notAssignedManualManagement = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(notAssignedManualManagement, notAssignedManualManagement.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(notAssignedManualManagement, notAssignedManualManagement.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("Gestiones manuales no asignadas");
//        EFECTIVIDAD
        indicatorRow++;
        Row effectivity = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(effectivity, effectivity.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(effectivity, effectivity.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("Efectividad");
//        TIEMPO PROM. LLAMADAS
        indicatorRow++;
        Row avgCallDuration = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(avgCallDuration, avgCallDuration.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(avgCallDuration, avgCallDuration.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("Tiempo prom. llamadas");
//        % GESTION
        indicatorRow++;
        Row noManagement = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(noManagement, noManagement.getPhysicalNumberOfCells(), headStyle).setCellValue(String.valueOf(indicatorRow));
        createCell(noManagement, noManagement.getPhysicalNumberOfCells(), indicatorStyle).setCellValue("% No gestión");

        int tableDetailDataColumn = 2;
        for (OperatorManagementReportDetail detail : report.getDetails()) {
//            PERIODO 1 - PERIODO 2 - MTD - U6M - YTD
            createCell(numberOfCalls, tableDetailDataColumn, quantityStyle).setCellValue(detail.getCallings() == null ? "" : detail.getCallings().toString());
            createCell(numberOfCallsDetailContact, tableDetailDataColumn, quantityStyle).setCellValue(detail.getCallingsContact() == null ? "" : detail.getCallingsContact().toString());
            createCell(numberOfCallsDetailNoContact, tableDetailDataColumn, quantityStyle).setCellValue(detail.getCallingsNoContact() == null ? "" : detail.getCallingsNoContact().toString());
            createCell(minCalls, tableDetailDataColumn, quantityStyle).setCellValue(detail.getCallingMinutes() == null ? "" : detail.getCallingMinutes().toString());
            createCell(assistedManagement, tableDetailDataColumn, quantityStyle).setCellValue(detail.getAssistedProcess() == null ? "" : detail.getAssistedProcess().toString());
            createCell(assistedManagementDetailContact, tableDetailDataColumn, quantityStyle).setCellValue(detail.getAssistedProcessContact() == null ? "" : detail.getAssistedProcessContact().toString());
            createCell(assistedManagementDetailNoContact, tableDetailDataColumn, quantityStyle).setCellValue(detail.getAssistedProcessNoContact() == null ? "" : detail.getAssistedProcessNoContact().toString());
            createCell(manualManagement, tableDetailDataColumn, quantityStyle).setCellValue(detail.getNonAssistedProcess() == null ? "" : detail.getNonAssistedProcess().toString());
            createCell(manualManagementDetailContact, tableDetailDataColumn, quantityStyle).setCellValue(detail.getNonAssistedProcessContact() == null ? "" : detail.getNonAssistedProcessContact().toString());
            createCell(manualManagementDetailNoContact, tableDetailDataColumn, quantityStyle).setCellValue(detail.getNonAssistedProcessNoContact() == null ? "" : detail.getNonAssistedProcessNoContact().toString());
            createCell(tracing, tableDetailDataColumn, quantityStyle).setCellValue("");// FILA VACIA
            createCell(tracingOneDay, tableDetailDataColumn, quantityStyle).setCellValue(detail.getScheduledDate1() == null ? "" : detail.getScheduledDate1().toString());
            createCell(tracingTwoDays, tableDetailDataColumn, quantityStyle).setCellValue(detail.getScheduledDate2() == null ? "" : detail.getScheduledDate2().toString());
            createCell(tracingMoreThanTwo, tableDetailDataColumn, quantityStyle).setCellValue(detail.getScheduledDateMore() == null ? "" : detail.getScheduledDateMore().toString());
            createCell(numberOfDisbursement, tableDetailDataColumn, quantityStyle).setCellValue(detail.getDisbursements() == null ? "" : detail.getDisbursements().toString());
            createCell(paymentDisbursement, tableDetailDataColumn, quantityStyle).setCellValue(utilService.doubleMoneyFormat(detail.getDisbursementsAmount(), ""));
            createCell(notAssignedAssistedManagement, tableDetailDataColumn, quantityStyle).setCellValue(detail.getNonAssignedAssistedProcess() == null ? "" : detail.getNonAssignedAssistedProcess().toString());
            createCell(notAssignedManualManagement, tableDetailDataColumn, quantityStyle).setCellValue(detail.getNonAssignedManualProcess() == null ? "" : detail.getNonAssignedManualProcess().toString());
//            RATIOS
            createCell(effectivity, tableDetailDataColumn, quantityStyle).setCellValue(detail.getEffectiveness() == null ? "" : utilService.doubleFormat(detail.getEffectiveness()));
            createCell(avgCallDuration, tableDetailDataColumn, quantityStyle).setCellValue(detail.getAverageCallTime() == null ? "" : utilService.doubleFormat(detail.getAverageCallTime()));
            createCell(noManagement, tableDetailDataColumn, quantityStyle).setCellValue(detail.getNoManagementPercentage() == null ? "" : utilService.doubleFormat(detail.getNoManagementPercentage()));

            tableDetailDataColumn++;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createDebtConsolidationReport() throws Exception {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte RFC  " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

        List<DebtorsReport> debtors = reportsDao.getDebtorsReport();
        int dataRows = debtors != null ? debtors.size() : 0;

        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 11);
        headStyleFont.setBold(true);
        headStyle.setFont(headStyleFont);

        Row headerRow = sheet.createRow(0);

        for (int x = 0; x <= 14; x++)
            sheet.setColumnWidth(x, 20 * 350);
        sheet.setColumnWidth(5, 20 * 800);


        createCell(headerRow, 0, headStyle).setCellValue("Tipo de Documento");
        createCell(headerRow, 1, headStyle).setCellValue("Documento Identidad (*)");
        createCell(headerRow, 2, headStyle).setCellValue("Apellido Paterno");
        createCell(headerRow, 3, headStyle).setCellValue("Apellido Materno");
        createCell(headerRow, 4, headStyle).setCellValue("Nombres");
        createCell(headerRow, 5, headStyle).setCellValue("Dirección (*)");
        createCell(headerRow, 6, headStyle).setCellValue("Teléfono Celular");
        createCell(headerRow, 7, headStyle).setCellValue("Email");
        createCell(headerRow, 8, headStyle).setCellValue("Nombre del Distrito (*)");
        createCell(headerRow, 9, headStyle).setCellValue("Nombre de Provincia (*)");
        createCell(headerRow, 10, headStyle).setCellValue("Nombre del Departamento (*)");
        createCell(headerRow, 11, headStyle).setCellValue("Número de cuenta por cobrar");
        createCell(headerRow, 12, headStyle).setCellValue("Fecha de Vencimiento (*)");
        createCell(headerRow, 13, headStyle).setCellValue("Monto de la deuda (*)");
        createCell(headerRow, 14, headStyle).setCellValue("Dias de Vencimiento (DIFF)");


        for (int i = 0; i < dataRows; i++) {
            DebtorsReport dr = debtors.get(i);
            Row row = sheet.createRow((short) i + 1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date dueDate = null != dr.getDueDate() ? sdf.parse(dr.getDueDate().replaceAll("-", "")) : null;
            Date currentDate = new Date();

            createCell(row, 0, getCenterStyle(workbook)).setCellValue(dr.getDocumentId());
            createCell(row, 1, getCenterStyle(workbook)).setCellValue(dr.getDocNumber());
            createCell(row, 2, getLeftStyle(workbook)).setCellValue(dr.getFirstSurname());
            createCell(row, 3, getLeftStyle(workbook)).setCellValue(dr.getLastSurname());
            createCell(row, 4, getLeftStyle(workbook)).setCellValue(dr.getName());
            createCell(row, 5, getLeftStyle(workbook)).setCellValue(dr.getStreet());
            createCell(row, 6, getCenterStyle(workbook)).setCellValue(dr.getPhoneNumber());
            createCell(row, 7, getLeftStyle(workbook)).setCellValue(dr.getEmail());
            createCell(row, 8, getCenterStyle(workbook)).setCellValue(dr.getDistrict());
            createCell(row, 9, getCenterStyle(workbook)).setCellValue(dr.getProvince());
            createCell(row, 10, getCenterStyle(workbook)).setCellValue(dr.getDepartment());
            createCell(row, 11, getCenterStyle(workbook)).setCellValue(dr.getCreditCode());
            createCell(row, 13, getRightStyle(workbook)).setCellValue(Double.valueOf(dr.getDebtAmount().toString()));

            if (null != dueDate) {
                createCell(row, 12, getRightDateStyle(workbook)).setCellValue(dueDate);
                long diffInMillies = currentDate.getTime() - dueDate.getTime();
                long diffDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                createCell(row, 14, getRightStyle(workbook)).setCellValue(String.valueOf(diffDays));
            }
        }

        try {
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

    @Override
    public ReportProces createReporteFunnelDashboardBo(Integer userId, Integer dateType, Date period1From, Date period1To, Date period2From, Date period2To, Integer[] countryId,
                                                       Integer entityId, Integer[] disbursementType) throws Exception {
        Gson gson = new Gson();
        JSONObject params = new JSONObject();

        params.put("dateType", dateType != null ? dateType : null);
        params.put("period1From", period1From != null ? CUSTOM_FORMAT.format(period1From) : null);
        params.put("period1To", period1To != null ? CUSTOM_FORMAT.format(period1To) : null);
        params.put("period2From", period2From != null ? CUSTOM_FORMAT.format(period2From) : null);
        params.put("period2To", period2To != null ? CUSTOM_FORMAT.format(period2To) : null);
        params.put("countryId", countryId != null ? gson.toJson(countryId) : null);
        params.put("entityId", entityId != null ? entityId : null);
        params.put("disbursementType", disbursementType != null ? gson.toJson(disbursementType) : null);

        Integer reportProcesId = reportsDao.registerReportProces(Report.REPORTE_FUNNEL_DASHBOARD_BO, userId, params);

        ReportProces reportProces = new ReportProces();
        reportProces.setId(reportProcesId);
        reportProces.setReportId(Report.REPORTE_FUNNEL_DASHBOARD_BO);
        reportProces.setUserId(userId);
        reportProces.setParams(params);

        // Send query to worker
        webscrapperService.callReportBot(reportProces.getId());

        return reportProces;
    }

    @Override
    public byte[] createReporteFunnelDashBoardBo(Date period1FromParam, Date period1ToParam, Date period2FromParam, Date period2ToParam,
                                                 Integer entityId, String disbursementTypes, Integer dateType, String countryId) throws Exception {

        Calendar period1From = Calendar.getInstance();
        period1From.setTime(period1FromParam);

        Calendar period2From = Calendar.getInstance();
        period2From.setTime(period2FromParam);

        Calendar period1To = Calendar.getInstance();
        period1To.setTime(period1ToParam);

        Calendar period2To = Calendar.getInstance();
        period2To.setTime(period2ToParam);

        AnalyticsReporting service = googleAnalyticsReportingService.initializeAnalyticsReporting();
        GetReportsResponse responseGoogleAnalytics = googleAnalyticsReportingService.getReport(service, period1From, period1To, period2From, period2To);

        List<String> paths = new ArrayList<>();

        paths.add("/adelanto-de-sueldo");

        GetReportsResponse pageViewsSalaryAdvance = googleAnalyticsReportingService.getPageViews(service, period1From, period1To, period2From, period2To, paths);

        paths = new ArrayList<>();

        paths.add("/credito-de-consumo/evaluacion");
        GetReportsResponse pageViewsConsumer = googleAnalyticsReportingService.getPageViews(service, period1From, period1To, period2From, period2To, paths);

        List<FunnelDashboardSection> sections = reportsDao.getFunnelDashboard(period1From.getTime(), period1To.getTime(), period2From.getTime(), period2To.getTime(), entityId, disbursementTypes, dateType, countryId);

        FunnelReportDashboardPainter report = new FunnelReportDashboardPainter(responseGoogleAnalytics, sections, utilService);
        report.setPageViewsConsumer(pageViewsConsumer);
        report.setPageViewsSalaryAdvance(pageViewsSalaryAdvance);
        report.processViews();

        if(entityId != null && entityId == Entity.BANCO_DEL_SOL){
            setUserTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        }else setUserTimeZone(TimeZone.getTimeZone("America/Lima"));

        if (report != null) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Reporte Dashboard de Funnel");

            XSSFColor sectionNameColor = new XSSFColor(new java.awt.Color(36, 140, 142));
            XSSFColor boldColor = new XSSFColor(new java.awt.Color(23, 26, 52));

            BorderStyle borderStyle = BorderStyle.THIN;

            XSSFFont sectionNameFont = workbook.createFont();
            sectionNameFont.setBold(true);
            sectionNameFont.setFontName("Liberation Sans");
            sectionNameFont.setFontHeightInPoints((short) 11);
            sectionNameFont.setColor(sectionNameColor);

            CellStyle sectionNameStyle = workbook.createCellStyle();
            sectionNameStyle.setFont(sectionNameFont);

            XSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldFont.setFontName("Liberation Sans");
            boldFont.setFontHeightInPoints((short) 10);
            boldFont.setColor(boldColor);

            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);
            boldStyle.setBorderTop(borderStyle);
            boldStyle.setBorderRight(borderStyle);
            boldStyle.setBorderLeft(borderStyle);
            boldStyle.setBorderBottom(borderStyle);

            CellStyle contentStyle = workbook.createCellStyle();
            contentStyle.setAlignment(HorizontalAlignment.CENTER);
            contentStyle.setBorderTop(borderStyle);
            contentStyle.setBorderRight(borderStyle);
            contentStyle.setBorderLeft(borderStyle);
            contentStyle.setBorderBottom(borderStyle);

            Row headerRow = sheet.createRow(0);
            Cell cell = headerRow.createCell(0);
            cell.setCellValue("Vistas");
            cell.setCellStyle(sectionNameStyle);

            int rowNum = 1;
            headerRow = sheet.createRow(rowNum++);

            List<String> periods = report.getPeriods();

            cell = headerRow.createCell(0);
            cell.setCellValue(EMPTY);

            int column = 1;

            for (int i = 0; i < periods.size(); i++) {

                sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                cell = headerRow.createCell(column);
                cell.setCellValue(periods.get(i));
                cell.setCellStyle(boldStyle);

                column += 2;
            }

            List<RowGoogleAnalytics> gaRows = report.getGaRows();

            for (int i = 0; i < gaRows.size(); i++) {
                headerRow = sheet.createRow(rowNum++);

                cell = headerRow.createCell(0);
                cell.setCellValue(gaRows.get(i).getName());
                cell.setCellStyle(boldStyle);

                column = 1;

                for (int j = 0; j < periods.size(); j++) {

                    sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                    cell = headerRow.createCell(column);
                    cell.setCellValue(gaRows.get(i).getValues().get(periods.get(j)).getQuantity());
                    cell.setCellStyle(contentStyle);

                    column += 2;
                }
            }

            for (FunnelDashboardSection section : report.getSections()) {
                sheet.createRow(rowNum++);
                headerRow = sheet.createRow(rowNum++);
                cell = headerRow.createCell(0);
                cell.setCellValue(section.getName());
                cell.setCellStyle(sectionNameStyle);

                headerRow = sheet.createRow(rowNum++);
                cell = headerRow.createCell(0);
                cell.setCellValue(EMPTY);

                column = 1;

                for (int i = 0; i < periods.size(); i++) {

                    int nextColumn = column + section.getValues();

                    sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, nextColumn - 1));

                    cell = headerRow.createCell(column);
                    cell.setCellValue(periods.get(i));
                    cell.setCellStyle(boldStyle);

                    column = column + section.getValues();
                }

                column = 0;

                if (!CONSUMOS_VARIABLES.equals(section.getName())) {

                    headerRow = sheet.createRow(rowNum++);

                    cell = headerRow.createCell(column++);
                    cell.setCellValue("Visitas atribuibles");
                    cell.setCellStyle(boldStyle);

                    for (int i = 0; i < periods.size(); i++) {
                        if (ADELANTO_DE_SUELDO.equals(section.getName())) {
                            sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                            cell = headerRow.createCell(column);
                            cell.setCellValue(report.getMapPageSalaryAdvance().get(periods.get(i)));
                            cell.setCellStyle(contentStyle);

                            column += 2;
                        }

                        if (CREDITO_DE_CONSUMO.equals(section.getName())) {
                            sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                            cell = headerRow.createCell(column);
                            cell.setCellValue(report.getMapPageSalaryConsumer().get(periods.get(i)));
                            cell.setCellStyle(contentStyle);

                            column += 2;
                        }
                    }
                }

                for (int i = 0; i < section.getRows().size(); i++) {

                    String row = section.getRows().get(i);

                    headerRow = sheet.createRow(rowNum++);

                    cell = headerRow.createCell(0);
                    cell.setCellValue(row);
                    cell.setCellStyle(boldStyle);

                    int counter = 1;

                    for (int j = 0; j < periods.size(); j++) {

                        String period = periods.get(j);
                        HashMap<String, List<String>> mapDetail = section.getMapDetail().get(period);
                        List<String> cells = mapDetail.get(row);

                        for (int k = 0; k < cells.size(); k++) {
                            String cellValue = cells.get(k);

                            cell = headerRow.createCell(counter++);
                            cell.setCellValue(cellValue);
                            cell.setCellStyle(contentStyle);
                        }
                    }
                }

                column = 0;

                if (!CONSUMOS_VARIABLES.equals(section.getName())) {
                    headerRow = sheet.createRow(rowNum++);

                    cell = headerRow.createCell(column++);
                    cell.setCellValue("Total desembolsado S/");
                    cell.setCellStyle(boldStyle);

                    for (int i = 0; i < periods.size(); i++) {
                        String period = periods.get(i);

                        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                        cell = headerRow.createCell(column);
                        cell.setCellValue(utilService.doubleMoneyFormat(section.getCalculatedTotal().get(period) != null ? section.getCalculatedTotal().get(period) : 0.00));
                        cell.setCellStyle(contentStyle);

                        column += 2;
                    }
                }

                column = 0;

                if (!CONSUMOS_VARIABLES.equals(section.getName())) {
                    headerRow = sheet.createRow(rowNum++);

                    cell = headerRow.createCell(column++);
                    cell.setCellValue("Monto promedio");
                    cell.setCellStyle(boldStyle);

                    for (int i = 0; i < periods.size(); i++) {
                        String period = periods.get(i);

                        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                        cell = headerRow.createCell(column);
                        cell.setCellValue(utilService.doubleMoneyFormat(section.getCalculatedAvg().get(period) != null ? section.getCalculatedAvg().get(period) : 0.00));
                        cell.setCellStyle(contentStyle);

                        column += 2;
                    }
                }

                if (!CONSUMOS_VARIABLES.equals(section.getName())) {
                    sheet.createRow(rowNum++);
                    headerRow = sheet.createRow(rowNum++);
                    cell = headerRow.createCell(0);
                    cell.setCellValue(EMPTY);

                    column = 1;

                    for (int i = 0; i < periods.size(); i++) {
                        String period = periods.get(i);

                        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                        cell = headerRow.createCell(column);
                        cell.setCellValue(period);
                        cell.setCellStyle(boldStyle);

                        column += 2;
                    }

                    if (!CONSUMOS_VARIABLES.equals(section.getName())) {
                        headerRow = sheet.createRow(rowNum++);
                        cell = headerRow.createCell(0);
                        cell.setCellValue("Inicios de registración / Visitas atribuibles");
                        cell.setCellStyle(boldStyle);

                        column = 1;

                        for (int i = 0; i < periods.size(); i++) {
                            String period = periods.get(i);

                            sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                            if (CREDITO_DE_CONSUMO.equals(section.getName())) {
                                cell = headerRow.createCell(column);
                                cell.setCellValue(utilService.dividePercentage(section.getMapDetail().get(period).get(section.getRows().get(0)).get(0),
                                        report.getMapPageSalaryConsumer().get(period).toString()));
                                cell.setCellStyle(contentStyle);
                            }

                            if (ADELANTO_DE_SUELDO.equals(section.getName())) {
                                cell = headerRow.createCell(column);
                                cell.setCellValue(utilService.dividePercentage(section.getMapDetail().get(period).get(section.getRows().get(0)).get(0),
                                        report.getMapPageSalaryAdvance().get(period).toString()));
                                cell.setCellStyle(contentStyle);
                            }

                            column += 2;
                        }
                    }

                    if (!CONSUMOS_VARIABLES.equals(section.getName())) {
                        headerRow = sheet.createRow(rowNum++);
                        cell = headerRow.createCell(0);
                        cell.setCellValue("Inicios de registración / Visitas");
                        cell.setCellStyle(boldStyle);

                        column = 1;

                        for (int i = 0; i < periods.size(); i++) {
                            String period = periods.get(i);

                            sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                            cell = headerRow.createCell(column);
                            cell.setCellValue(utilService.dividePercentage(section.getMapDetail().get(period).get(section.getRows().get(0)).get(0),
                                    report.getGaRows().get(0).getValues().get(period).getQuantity()));
                            cell.setCellStyle(contentStyle);

                            column += 2;
                        }
                    }

                    if (CREDITO_DE_CONSUMO.equals(section.getName()) && section.getRows().size() > 1) {
                        headerRow = sheet.createRow(rowNum++);
                        cell = headerRow.createCell(0);
                        cell.setCellValue("Rechazo en prevalidación");
                        cell.setCellStyle(boldStyle);

                        column = 1;

                        for (int i = 0; i < periods.size(); i++) {
                            String period = periods.get(i);

                            sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                            cell = headerRow.createCell(column);
                            cell.setCellValue(utilService.dividePercentage(section.getMapDetail().get(period).get(section.getRows().get(1)).get(0),
                                    section.getMapDetail().get(period).get(section.getRows().get(0)).get(0)));
                            cell.setCellStyle(contentStyle);

                            column += 2;
                        }
                    }

                    if (CREDITO_DE_CONSUMO.equals(section.getName()) && section.getRows().size() > 2) {
                        headerRow = sheet.createRow(rowNum++);
                        cell = headerRow.createCell(0);
                        cell.setCellValue("Tasa de aprobación Financiadores");
                        cell.setCellStyle(boldStyle);

                        column = 1;

                        for (int i = 0; i < periods.size(); i++) {
                            String period = periods.get(i);

                            sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                            cell = headerRow.createCell(column);
                            cell.setCellValue(utilService.dividePercentage(section.getMapDetail().get(period).get(section.getRows().get(2)).get(0),
                                    section.getMapDetail().get(period).get(section.getRows().get(1)).get(0)));
                            cell.setCellStyle(contentStyle);

                            column += 2;
                        }
                    }

                    if (CREDITO_DE_CONSUMO.equals(section.getName()) && section.getRows().size() > 3) {
                        headerRow = sheet.createRow(rowNum++);
                        cell = headerRow.createCell(0);
                        cell.setCellValue("Tasa de aceptación de Ofertas");
                        cell.setCellStyle(boldStyle);

                        column = 1;

                        for (int i = 0; i < periods.size(); i++) {
                            String period = periods.get(i);

                            sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                            cell = headerRow.createCell(column);
                            cell.setCellValue(utilService.dividePercentage(section.getMapDetail().get(period).get(section.getRows().get(3)).get(0),
                                    section.getMapDetail().get(period).get(section.getRows().get(2)).get(0)));
                            cell.setCellStyle(contentStyle);

                            column += 2;
                        }
                    }

                    if (CREDITO_DE_CONSUMO.equals(section.getName()) && section.getRows().size() > 4) {
                        headerRow = sheet.createRow(rowNum++);
                        cell = headerRow.createCell(0);
                        cell.setCellValue("Tasa de Firma");
                        cell.setCellStyle(boldStyle);

                        column = 1;

                        for (int i = 0; i < periods.size(); i++) {
                            String period = periods.get(i);

                            sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                            cell = headerRow.createCell(column);
                            cell.setCellValue(utilService.dividePercentage(section.getMapDetail().get(period).get(section.getRows().get(4)).get(0),
                                    section.getMapDetail().get(period).get(section.getRows().get(3)).get(0)));
                            cell.setCellStyle(contentStyle);

                            column += 2;
                        }
                    }

                    if (CREDITO_DE_CONSUMO.equals(section.getName()) && section.getRows().size() > 6) {
                        headerRow = sheet.createRow(rowNum++);
                        cell = headerRow.createCell(0);
                        cell.setCellValue("Desembolsos / Contrato Firmado");
                        cell.setCellStyle(boldStyle);

                        column = 1;

                        for (int i = 0; i < periods.size(); i++) {
                            String period = periods.get(i);

                            sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                            cell = headerRow.createCell(column);
                            cell.setCellValue(utilService.dividePercentage(section.getMapDetail().get(period).get(section.getRows().get(6)).get(0),
                                    section.getMapDetail().get(period).get(section.getRows().get(3)).get(0)));
                            cell.setCellStyle(contentStyle);

                            column += 2;
                        }
                    }

                    if (CREDITO_DE_CONSUMO.equals(section.getName()) && section.getRows().size() > 6) {
                        headerRow = sheet.createRow(rowNum++);
                        cell = headerRow.createCell(0);
                        cell.setCellValue("Desembolsos / Ofertas Aceptadas");
                        cell.setCellStyle(boldStyle);

                        column = 1;

                        for (int i = 0; i < periods.size(); i++) {
                            String period = periods.get(i);

                            sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                            cell = headerRow.createCell(column);
                            cell.setCellValue(utilService.dividePercentage(section.getMapDetail().get(period).get(section.getRows().get(6)).get(0),
                                    section.getMapDetail().get(period).get(section.getRows().get(5)).get(0)));
                            cell.setCellStyle(contentStyle);

                            column += 2;
                        }
                    }

                    if (CREDITO_DE_CONSUMO.equals(section.getName()) && section.getRows().size() > 6) {
                        headerRow = sheet.createRow(rowNum++);
                        cell = headerRow.createCell(0);
                        cell.setCellValue("Desembolsos / Inicios de Registración");
                        cell.setCellStyle(boldStyle);

                        column = 1;

                        for (int i = 0; i < periods.size(); i++) {
                            String period = periods.get(i);

                            sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                            cell = headerRow.createCell(column);
                            cell.setCellValue(utilService.dividePercentage(section.getMapDetail().get(period).get(section.getRows().get(6)).get(0),
                                    section.getMapDetail().get(period).get(section.getRows().get(0)).get(0)));
                            cell.setCellStyle(contentStyle);

                            column += 2;
                        }
                    }
                }

                if (CONSUMOS_VARIABLES.equals(section.getName())) {
                    sheet.createRow(rowNum++);
                    headerRow = sheet.createRow(rowNum++);
                    cell = headerRow.createCell(0);
                    cell.setCellValue(EMPTY);

                    column = 1;

                    for (int i = 0; i < periods.size(); i++) {
                        String period = periods.get(i);

                        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), column, column + 1));

                        cell = headerRow.createCell(column);
                        cell.setCellValue(period);
                        cell.setCellStyle(boldStyle);

                        column += 2;
                    }

                    List<String> rows = section.getRows();

                    for (int i = 0; i < rows.size(); i++) {
                        headerRow = sheet.createRow(rowNum++);

                        column = 0;

                        String row = rows.get(i);
                        cell = headerRow.createCell(column++);
                        cell.setCellValue(row + " / Solicitud Originada");
                        cell.setCellStyle(boldStyle);

                        for (int j = 0; j < periods.size(); j++) {
                            String period = periods.get(j);

                            cell = headerRow.createCell(column++);

                            if (report.getSections().get(0).getRows().size() > 6) {
                                cell.setCellValue("USD " + utilService.divide(section.getMapDetail().get(period).get(row).get(0),
                                        report.getSections().get(0).getMapDetail().get(period).get(report.getSections().get(0).getRows().get(6)).get(0)));
                                cell.setCellStyle(contentStyle);
                            } else {
                                cell.setCellValue("USD 0.00");
                                cell.setCellStyle(contentStyle);
                            }

                            cell = headerRow.createCell(column++);

                            if (report.getSections().get(0).getRows().size() > 6) {
                                cell.setCellValue(utilService.divide(section.getMapDetail().get(period).get(row).get(1),
                                        report.getSections().get(0).getMapDetail().get(period).get(report.getSections().get(0).getRows().get(6)).get(0)));
                                cell.setCellStyle(contentStyle);
                            } else {
                                cell.setCellValue("0.00");
                                cell.setCellStyle(contentStyle);
                            }
                        }
                    }
                }
            }

            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (CellRangeAddress rangeAddress : mergedRegions) {
                RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangeAddress, sheet, workbook);
                RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangeAddress, sheet, workbook);
                RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangeAddress, sheet, workbook);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangeAddress, sheet, workbook);
            }

            sheet.autoSizeColumn(0);

            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                workbook.write(outStream);
                workbook.close();
                outStream.close();
                return outStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public byte[] createScreenReport(String countryId, Date startDate, Date endDate, Integer[] products, Integer[] statuses, Locale locale) throws Exception {
        List<ScreenReport> paths = reportsDao.getApplicationProcessReport(countryId, startDate, endDate, statuses, products, locale);
        if (paths == null)
            paths = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte de Pantallas");

        // Cell Styles
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 8);
        headStyleFont.setBold(true);
        headStyleFont.setColor(IndexedColors.WHITE.getIndex());
        headStyle.setFont(headStyleFont);

        CellStyle textStyle = workbook.createCellStyle();
        Font textStyleFont = workbook.createFont();
        textStyleFont.setFontHeightInPoints((short) 8);
        textStyle.setFont(textStyleFont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 8);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle filterStyle = workbook.createCellStyle();
        Font filterStyleFont = workbook.createFont();
        filterStyleFont.setFontHeightInPoints((short) 8);
        filterStyle.setFont(filterStyleFont);

        // Setting widths
        sheet.setColumnWidth(0, 10 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 20 * 256);
        sheet.setColumnWidth(3, 10 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(8, 15 * 256);

        // Paint filter row
        Row filterRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        filterRow.setHeightInPoints(70);
        StringBuffer filter = new StringBuffer("FILTROS:\n");
        filter.append("\tDesde:\t\t\t" + new SimpleDateFormat("dd/MM/yyyy").format(startDate) + "\n");
        filter.append("\tHasta:\t\t\t" + new SimpleDateFormat("dd/MM/yyyy").format(endDate) + "\n");
        if (statuses != null && statuses.length > 0) {
            filter.append("\tEstados:\t\t\t");
            for (int i = 0; i < statuses.length; i++) {
                LoanApplicationStatus status = catalogService.getLoanApplicationStatus(locale, statuses[i]);
                if (status != null)
                    filter.append(status.getStatus() + ", ");
            }
            filter.append("\n");
        }
        if (products != null && products.length > 0) {
            filter.append("\tCat. productos:\t");
            for (int i = 0; i < products.length; i++) {
                ProductCategory category = catalogService.getCatalogById(ProductCategory.class, products[i], locale);
                if (category != null)
                    filter.append(category.getCategory() + ", ");
            }
            filter.append("\n");
        }
        filter.append("\nFECHA CREACIÓN:\t\t" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        createCell(filterRow, 0, filterStyle).setCellValue(filter.toString());

        // Paint head period row
        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        headRow.setHeightInPoints(22);
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Cod. Pantalla");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Nombre Pantalla");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Sección");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("# LA");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Tiempo en Pantalla");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Avanzaron");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Desistieron/\nEn Proceso");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Rechazados");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Fec. Última Visita");

        // Merged cells of filter data
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        // Paint originated quantity report
        int counter = 0;
        for (ScreenReport path : paths) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue("" + path.getProcessQuestion().getId());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getProcessQuestion().getQuestion());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getProcessQuestion().getCategory() != null ? path.getProcessQuestion().getCategory().getCategory() : null);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getLoanApplicationsQuantity() != null ? path.getLoanApplicationsQuantity() : 0);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getTotalTime() != null ? path.getTotalTime() : 0));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getProceeded() != null ? path.getProceeded() : 0);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getAbandoned() != null ? path.getAbandoned() : 0);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getRejected() != null ? path.getRejected() : 0);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getLastVisit() != null ? utilService.dateFormat(path.getLastVisit()) : "");
            counter++;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createTrackScreenReport(String countryId, Date startDate, Date endDate, Locale locale) throws Exception {
        List<ScreenTrackReport> paths = reportsDao.getApplicationProcessPathOrderReport(countryId, startDate, endDate, locale);
        if (paths == null)
            paths = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Recorrido Usuarios - orden");

        // Cell Styles
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 8);
        headStyleFont.setBold(true);
        headStyleFont.setColor(IndexedColors.WHITE.getIndex());
        headStyle.setFont(headStyleFont);

        CellStyle textStyle = workbook.createCellStyle();
        Font textStyleFont = workbook.createFont();
        textStyleFont.setFontHeightInPoints((short) 8);
        textStyle.setFont(textStyleFont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 8);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle filterStyle = workbook.createCellStyle();
        Font filterStyleFont = workbook.createFont();
        filterStyleFont.setFontHeightInPoints((short) 8);
        filterStyle.setFont(filterStyleFont);

        // Calculate the max screen size
        int maxScreenSize = paths.stream().mapToInt(p -> p.getQuestionSequence() != null ? p.getQuestionSequence().size() : 0).max().orElse(0);

        // Setting widths
        sheet.setColumnWidth(0, 5 * 256);
        sheet.setColumnWidth(1, 15 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(8, 15 * 256);
        sheet.setColumnWidth(9, 15 * 256);
        sheet.setColumnWidth(10, 15 * 256);
        sheet.setColumnWidth(11, 15 * 256);
        sheet.setColumnWidth(12, 15 * 256);
        sheet.setColumnWidth(13, 15 * 256);
        sheet.setColumnWidth(14, 15 * 256);
        sheet.setColumnWidth(15, 15 * 256);
        sheet.setColumnWidth(16, 15 * 256);
        sheet.setColumnWidth(17, 15 * 256);
        sheet.setColumnWidth(18, 15 * 256);
        for (int i = 0; i < maxScreenSize; i++) {
            sheet.setColumnWidth(i + 19, 10 * 256);
        }

        // Paint filter row
        Row filterRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        filterRow.setHeightInPoints(55);
        StringBuffer filter = new StringBuffer("FILTROS:\n");
        filter.append("\tDesde:\t\t\t" + new SimpleDateFormat("dd/MM/yyyy").format(startDate) + "\n");
        filter.append("\tHasta:\t\t\t" + new SimpleDateFormat("dd/MM/yyyy").format(endDate) + "\n");
        filter.append("\nFECHA CREACIÓN:\t\t" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        createCell(filterRow, 0, filterStyle).setCellValue(filter.toString());

        // Paint head period rows
        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        headRow.setHeightInPoints(22);
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("#");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Loan Application");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Fecha y hora");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Tipo Documento");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Nro. Documento");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Categoría Producto");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Estado");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Fec. Último Estado");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Tiempo Total");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Tiempo Total \nh/ desembolso");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("1 - Inf. Preliminar");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("2 - Datos Personales");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("3 - Ingresos");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("4 - Oferta");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("5 - Verificaión");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("6 - Esperando Aprobación");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("7 - Resultados");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Cant. Pantallas");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Última pantalla");
        for (int i = 0; i < maxScreenSize; i++) {
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Pantalla " + (i + 1));
        }
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Medium");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Source");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Campaign");

        // Merged cells of filter data
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        // Paint originated quantity report
        int counter = 0;
        for (ScreenTrackReport path : paths) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue("" + (counter + 1));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getLoanApplicationCode());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.datetimeShortFormat(path.getNewLoanStatusDate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getDocType() != null ? path.getDocType().getName() : null);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getDocNumber());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getProductCategory().getCategory());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getLoanApplicationStatus().getStatus());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.datetimeShortFormat(path.getMaxStatusDate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getProccesDuration() != null ? path.getProccesDuration() : 0));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getDisbursementTime() != null ? path.getDisbursementTime() : 0));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getScreensByCategory(ProcessQuestionCategory.PRE_INFORMATION));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getScreensByCategory(ProcessQuestionCategory.PERSONAL_INFORMATION));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getScreensByCategory(ProcessQuestionCategory.INCOME));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getScreensByCategory(ProcessQuestionCategory.OFFER));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getScreensByCategory(ProcessQuestionCategory.VERIFICATION));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getScreensByCategory(ProcessQuestionCategory.RESULT));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getScreensByCategory(ProcessQuestionCategory.WAITING_APPROVAL));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getQuestionSequence() != null ? path.getQuestionSequence().size() : 0);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getCurrentQuestionId() != null ? path.getCurrentQuestionId() : 0);
            for (ProcessQuestionSequence question : path.getQuestionSequence()) {
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(question.getId());
            }
            counter++;
        }

        // Second report
        paths = reportsDao.getApplicationProcessPathTimeReport(startDate, endDate, Configuration.getDefaultLocale());
        if (paths == null)
            paths = new ArrayList<>();

        Sheet sheet2 = workbook.createSheet("Recorrido Usuarios - Tiempo");

        // Setting widths
        sheet2.setColumnWidth(0, 5 * 256);
        sheet2.setColumnWidth(1, 15 * 256);
        sheet2.setColumnWidth(2, 15 * 256);
        sheet2.setColumnWidth(3, 15 * 256);
        sheet2.setColumnWidth(4, 15 * 256);
        sheet2.setColumnWidth(5, 15 * 256);
        sheet2.setColumnWidth(6, 15 * 256);
        sheet2.setColumnWidth(7, 15 * 256);
        sheet2.setColumnWidth(8, 15 * 256);
        sheet2.setColumnWidth(9, 15 * 256);
        sheet2.setColumnWidth(10, 15 * 256);
        sheet2.setColumnWidth(11, 15 * 256);
        sheet2.setColumnWidth(12, 15 * 256);
        sheet2.setColumnWidth(13, 15 * 256);
        sheet2.setColumnWidth(14, 15 * 256);
        sheet2.setColumnWidth(15, 15 * 256);
        sheet2.setColumnWidth(16, 15 * 256);
        sheet2.setColumnWidth(17, 15 * 256);
        sheet2.setColumnWidth(18, 15 * 256);
        for (int i = 0; i < maxScreenSize; i++) {
            sheet2.setColumnWidth(i + 19, 10 * 256);
        }

        // Paint filter row
        filterRow = sheet2.createRow(sheet2.getPhysicalNumberOfRows());
        filterRow.setHeightInPoints(55);
        filter = new StringBuffer("FILTROS:\n");
        filter.append("\tDesde:\t\t\t" + new SimpleDateFormat("dd/MM/yyyy").format(startDate) + "\n");
        filter.append("\tHasta:\t\t\t" + new SimpleDateFormat("dd/MM/yyyy").format(endDate) + "\n");
        filter.append("\nFECHA CREACIÓN:\t\t" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        createCell(filterRow, 0, filterStyle).setCellValue(filter.toString());

        // Paint head period rows
        headRow = sheet2.createRow(sheet2.getPhysicalNumberOfRows());
        headRow.setHeightInPoints(22);
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("#");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Loan Application");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Fecha y hora");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Tipo Documento");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Nro. Documento");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Categoría Producto");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Estado");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Fec. Último Estado");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Tiempo Total");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Tiempo Total \nh/ desembolso");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("1 - Inf. Preliminar");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("2 - Datos Personales");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("3 - Ingresos");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("4 - Oferta");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("5 - Verificaión");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("6 - Esperando Aprobación");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("7 - Resultados");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Cant. Pantallas");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Última pantalla");
        for (int i = 0; i < maxScreenSize; i++) {
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Pantalla " + (i + 1));
        }
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Medium");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Source");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Campaign");

        // Merged cells of filter data
        sheet2.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        // Paint originated quantity report
        counter = 0;
        for (ScreenTrackReport path : paths) {
            Row reportRow = sheet2.createRow(sheet2.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue("" + (counter + 1));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getLoanApplicationCode());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.datetimeShortFormat(path.getNewLoanStatusDate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getDocType() != null ? path.getDocType().getName() : null);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getDocNumber());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getProductCategory().getCategory());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getLoanApplicationStatus().getStatus());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.datetimeShortFormat(path.getMaxStatusDate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getProccesDuration() != null ? path.getProccesDuration() : 0));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getDisbursementTime() != null ? path.getDisbursementTime() : 0));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getScreensTimeByCategory(ProcessQuestionCategory.PRE_INFORMATION)));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getScreensTimeByCategory(ProcessQuestionCategory.PERSONAL_INFORMATION)));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getScreensTimeByCategory(ProcessQuestionCategory.INCOME)));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getScreensTimeByCategory(ProcessQuestionCategory.OFFER)));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getScreensTimeByCategory(ProcessQuestionCategory.VERIFICATION)));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getScreensTimeByCategory(ProcessQuestionCategory.RESULT)));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(path.getScreensTimeByCategory(ProcessQuestionCategory.WAITING_APPROVAL)));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getQuestionSequence() != null ? path.getQuestionSequence().size() : 0);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getCurrentQuestionId() != null ? path.getCurrentQuestionId() : 0);
            for (ProcessQuestionSequence question : path.getQuestionSequence()) {
                long timeInSeconds = 0;
                if (question.getDate() != null && question.getFinishDate() != null)
                    timeInSeconds = (question.getFinishDate().getTime() - question.getDate().getTime()) / 1000;
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(formatInMinuteSeconds(timeInSeconds));
            }
            counter++;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createLoanInProcessReport(Date registerDateReport, Date processDateReport, String countryId, Integer documentType, String documentNumber, Integer[] producersIds, String producersNameConcat, Date startDate, Date endDate, Integer[] applicationStatuses) throws Exception {
        List<LoanApplicationInProcessReport> paths = reportsDao.getLoanApplicationInProcessReportBDS(documentType, documentNumber, producersIds, startDate, endDate, applicationStatuses);
        if (paths == null)
            paths = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Solicitudes en proceso");

        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 8);
        headStyleFont.setBold(true);
        headStyleFont.setColor(IndexedColors.WHITE.getIndex());
        headStyle.setFont(headStyleFont);

        CellStyle textStyle = workbook.createCellStyle();
        Font textStyleFont = workbook.createFont();
        textStyleFont.setFontHeightInPoints((short) 8);
        textStyle.setFont(textStyleFont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 8);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle filterLabelStyle = workbook.createCellStyle();
        Font filterLabelFont = workbook.createFont();
        filterLabelFont.setFontHeightInPoints((short) 8);
        filterLabelFont.setBold(true);
        filterLabelStyle.setFont(filterLabelFont);

        String filterLoanRegisterDate = "";
        if (startDate != null && endDate != null)
            filterLoanRegisterDate = utilService.dateFormat(startDate) + " - " + utilService.dateFormat(endDate);

        String filterApplicationStatuses = "";
        if (applicationStatuses != null)
            filterApplicationStatuses = getLoanStatusesByIds(applicationStatuses);
        else
            filterApplicationStatuses = "Todos";

        int columnProducers = 7;

        Row filterRow1 = sheet.createRow(sheet.getPhysicalNumberOfRows());

        sheet.addMergedRegion(new CellRangeAddress(filterRow1.getRowNum(), filterRow1.getRowNum(), columnProducers, 100));

        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Fec. Generación reporte");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(registerDateReport));
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Cuit solicitante");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue(documentNumber);
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Productor");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue(producersNameConcat == null ? "Todos" : producersNameConcat);

        Row filterRow2 = sheet.createRow(sheet.getPhysicalNumberOfRows());

        sheet.addMergedRegion(new CellRangeAddress(filterRow2.getRowNum(), filterRow2.getRowNum(), columnProducers, 20));

        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Fec. Solicitud");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue(filterLoanRegisterDate);
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Estado de solicitud");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue(filterApplicationStatuses);

        sheet.createRow(sheet.getPhysicalNumberOfRows());

        List<String> headers = Arrays.asList("Fecha de solicitud", "Fecha de última modificación", "N° de solicitud", "Canal", "Nombre", "CUIT",
                "Monto", "Tasa", "Plazo", "CBU", "Organizador", "Productor", "Usuario", "Estado");

        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        headRow.setHeightInPoints(22);

        for (String header : headers) {
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue(header);
        }

        Currency argCurrency = catalogService.getCurrency(Currency.ARS);

        for (LoanApplicationInProcessReport path : paths) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(path.getRegisterDate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(path.getUpdatedTime()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getLoanApplicationCode());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getEntityUserChannelId() != null ? catalogService.getEntityAcquisitionChannelById(path.getEntityUserChannelId()).getEntityAcquisitionChannel() : null);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getClientFullName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getDocumentNumber());

            if (path.getAmount() != null)
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(utilService.doubleMoneyFormat(path.getAmount(), argCurrency));
            else
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue("");

            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(utilService.percentFormat(path.getRate()));

            if (path.getInstallments() != null)
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getInstallments());
            else
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue("");

            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getCciCode() != null ? path.getCciCode() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getOrganizerFullName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getProductorFullName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getProductorUsername());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getLoanApplicationStatus() != null ? path.getLoanApplicationStatus().getStatus() : null);
        }

        try{
            IntStream.range(0, headers.size()).filter(i -> i != columnProducers).forEach(sheet::autoSizeColumn);
        }
        catch (NullPointerException e){
            errorService.sendGeneralErrorSlack(e.getMessage());
        }
        sheet.setColumnWidth(columnProducers, 2_000);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createCreditToDisburseReport(Date registerDateReport, Date processDateReport, String countryId, Integer documentType, String documentNumber, Integer[] producers, String producersNameConcat, Date startDate, Date endDate) throws Exception {
        List<DisburseCreditReport> paths = reportsDao.getDisburseCreditReportBDS(documentType, documentNumber, producers, startDate, endDate);
        if (paths == null)
            paths = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Créditos para desembolsar");

        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 8);
        headStyleFont.setBold(true);
        headStyleFont.setColor(IndexedColors.WHITE.getIndex());
        headStyle.setFont(headStyleFont);

        CellStyle textStyle = workbook.createCellStyle();
        Font textStyleFont = workbook.createFont();
        textStyleFont.setFontHeightInPoints((short) 8);
        textStyle.setFont(textStyleFont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 8);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle moneyStyle = workbook.createCellStyle();
        Font moneyStyleFont = workbook.createFont();
        moneyStyleFont.setFontHeightInPoints((short) 8);
        moneyStyle.setFont(moneyStyleFont);
        moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
        moneyStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00")); // Money format

        CellStyle filterLabelStyle = workbook.createCellStyle();
        Font filterLabelFont = workbook.createFont();
        filterLabelFont.setFontHeightInPoints((short) 8);
        filterLabelFont.setBold(true);
        filterLabelStyle.setFont(filterLabelFont);

        String filterLoanRegisterDate = "";
        if (startDate != null && endDate != null)
            filterLoanRegisterDate = utilService.dateFormat(startDate) + " - " + utilService.dateFormat(endDate);

        int columnProducers = 7;

        Row filterRow1 = sheet.createRow(sheet.getPhysicalNumberOfRows());

        sheet.addMergedRegion(new CellRangeAddress(filterRow1.getRowNum(), filterRow1.getRowNum(), columnProducers, 100));

        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Fec. Generación reporte");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(registerDateReport));
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Cuit solicitante");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue(documentNumber);
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Productor");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue(producersNameConcat == null ? "Todos" : producersNameConcat);

        Row filterRow2 = sheet.createRow(sheet.getPhysicalNumberOfRows());

        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Fec. Solicitud");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue(filterLoanRegisterDate);

        sheet.createRow(sheet.getPhysicalNumberOfRows());

        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        headRow.setHeightInPoints(22);

        List<String> headers = Arrays.asList("Fecha de solicitud", "Fecha de confirmación del crédito", "N° de crédito", "Canal", "Nombre", "CUIT", "Monto",
                "Tasa", "Plazo", "CBU", "Organizador", "Productor", "Usuario", "Estado", "Estado de documentación");

        for (String header : headers) {
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue(header);
        }

        Currency argCurrency = catalogService.getCurrency(Currency.ARS);

        for (DisburseCreditReport path : paths) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(path.getLoanRegisterDate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(path.getCreditOriginatedDate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getCreditCode());

            Integer entityUserChannelId = JsonUtil.getIntFromJson(path.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ENTITY_USER_CHANNEL_ID.getKey(), null);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(entityUserChannelId != null ? catalogService.getEntityAcquisitionChannelById(entityUserChannelId).getEntityAcquisitionChannel() : null);

            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getClientFullName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getDocumentNumber());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(utilService.doubleMoneyFormat(path.getLoanCapital(), argCurrency));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.percentFormat(path.getRate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getInstallments());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getCciCode());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getOrganizerFullName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getProductorFullName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getProductorUsername());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getCreditSubStatus() != null ? path.getCreditSubStatus().getSubStatus() : null);

            String internalStatus = JsonUtil.getStringFromJson(path.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_INTERNAL_CREDIT_STATUS.getKey(), null);
            if (internalStatus == null)
                internalStatus = INTERNAL_STATUS_DEFAULT;

            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(internalStatus);
        }

        try{
            IntStream.range(0, headers.size()).filter(i -> i != columnProducers).forEach(sheet::autoSizeColumn);
        }
        catch (NullPointerException e){
            errorService.sendGeneralErrorSlack(e.getMessage());
        }
        sheet.setColumnWidth(columnProducers, 2_000);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createDisbursedCreditReport(Date registerDateReport, Date processDateReport, String countryId, Integer documentType, String documentNumber, Integer[] producers, String producersNameConcat, Date startDate, Date endDate,
                                              JSONArray internalStatuses, Date disbursementStartDate, Date disbursementEndDate) throws Exception {
        List<DisbursedCreditReport> paths = reportsDao.getDisbursedCreditReportBDS(documentType, documentNumber, producers, startDate, endDate, internalStatuses, disbursementStartDate, disbursementEndDate);
        if (paths == null)
            paths = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Créditos desembolsados");

        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 8);
        headStyleFont.setBold(true);
        headStyleFont.setColor(IndexedColors.WHITE.getIndex());
        headStyle.setFont(headStyleFont);

        CellStyle textStyle = workbook.createCellStyle();
        Font textStyleFont = workbook.createFont();
        textStyleFont.setFontHeightInPoints((short) 8);
        textStyle.setFont(textStyleFont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 8);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle moneyStyle = workbook.createCellStyle();
        Font moneyStyleFont = workbook.createFont();
        moneyStyleFont.setFontHeightInPoints((short) 8);
        moneyStyle.setFont(moneyStyleFont);
        moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
        moneyStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00")); // Money format

        CellStyle filterLabelStyle = workbook.createCellStyle();
        Font filterLabelFont = workbook.createFont();
        filterLabelFont.setFontHeightInPoints((short) 8);
        filterLabelFont.setBold(true);
        filterLabelStyle.setFont(filterLabelFont);

        CellStyle filterValueStyle = workbook.createCellStyle();
        textStyleFont.setFontHeightInPoints((short) 8);
        filterValueStyle.setFont(textStyleFont);
        filterValueStyle.setWrapText(false);

        String filterLoanRegisterDate = "";
        if (startDate != null && endDate != null)
            filterLoanRegisterDate = utilService.dateFormat(startDate) + " - " + utilService.dateFormat(endDate);

        String filterDisbursementDate = "";
        if (startDate != null && endDate != null)
            filterDisbursementDate = utilService.dateFormat(disbursementStartDate) + " - " + utilService.dateFormat(disbursementEndDate);

        String filterInternalStatuses = "";
        if (internalStatuses != null)
            filterInternalStatuses = getInternalStatusesConcat(internalStatuses);
        else
            filterInternalStatuses = "Todos";

        int columnProducers = 7;

        Row filterRow1 = sheet.createRow(sheet.getPhysicalNumberOfRows());

        sheet.addMergedRegion(new CellRangeAddress(filterRow1.getRowNum(), filterRow1.getRowNum(), columnProducers, 100));

        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Fec. Generación reporte");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(registerDateReport));
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Cuit solicitante");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue(documentNumber);
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Productor");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterValueStyle).setCellValue(producersNameConcat == null ? "Todos" : producersNameConcat);

        Row filterRow2 = sheet.createRow(sheet.getPhysicalNumberOfRows());

        sheet.addMergedRegion(new CellRangeAddress(filterRow2.getRowNum(), filterRow2.getRowNum(), columnProducers, 20));

        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Fec. Solicitud");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue(filterLoanRegisterDate);
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Fec. Desembolso");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue(filterDisbursementDate);
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Estado de solicitud");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue(filterInternalStatuses);

        sheet.createRow(sheet.getPhysicalNumberOfRows());

        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        headRow.setHeightInPoints(22);

        List<String> headers = Arrays.asList("Fecha de solicitud", "Fecha de desembolso", "N° de crédito", "Canal", "Nombre", "CUIT", "Correo electrónico", "Monto desembolsado",
                "Monto del crédito", "Tasa", "Plazo", "Organizador", "Productor", "Usuario", "Estado de documentación");

        for (String header : headers) {
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue(header);
        }

        Currency argCurrency = catalogService.getCurrency(Currency.ARS);

        for (DisbursedCreditReport path : paths) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(path.getRegisterDate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(path.getCreditDisbursementDate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getCreditCode());

            Integer entityUserChannelId = JsonUtil.getIntFromJson(path.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ENTITY_USER_CHANNEL_ID.getKey(), null);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(entityUserChannelId != null ? catalogService.getEntityAcquisitionChannelById(entityUserChannelId).getEntityAcquisitionChannel() : null);

            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getClientFullName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getDocumentNumber());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getEmail());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(utilService.doubleMoneyFormat(path.getAmount(), argCurrency));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), moneyStyle).setCellValue(utilService.doubleMoneyFormat(path.getLoanCapital(), argCurrency));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.percentFormat(path.getRate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getInstallments());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getOrganizerFullName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getProductorFullName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getProductorUsername());

            String internalStatus = JsonUtil.getStringFromJson(path.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_INTERNAL_CREDIT_STATUS.getKey(), null);
            if (internalStatus == null)
                internalStatus = INTERNAL_STATUS_DEFAULT;

            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(internalStatus);
        }

        try{
            IntStream.range(0, headers.size()).filter(i -> i != columnProducers).forEach(sheet::autoSizeColumn);
        }
        catch (NullPointerException e){
            errorService.sendGeneralErrorSlack(e.getMessage());
        }
        sheet.setColumnWidth(columnProducers, 4_000);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createRiskReport(Integer documentType, String documentNumber, Integer[] producers, Date startDate, Date endDate, Integer[] creditStatuses,
                                   JSONArray internalStatuses, Integer[] applicationsStatuses, Date lastExecutionStartDate, Date lastExecutionEndDate) throws Exception {

        JSONArray arrayRisk = reportsDao.getRiskReportBDS(documentType, documentNumber, producers, startDate, endDate, creditStatuses, internalStatuses,
                applicationsStatuses, lastExecutionStartDate, lastExecutionEndDate);

        String risk = bancoDelSolService.generarReportCSVRisk(arrayRisk, true);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        outStream.write(risk.getBytes());
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createPersoninteractionFollowUpReports(String event, JSONArray personInteractionIds) throws Exception {

        JSONObject json = new JSONObject();
        json.put("event", event);
        json.put("person_interaction_id", personInteractionIds);
        List<PersonInteractionFollowUpReport> reports = reportsDao.getPersoninteractionFollowUpReports(json.toString());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Interacciones");

        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 8);
        headStyleFont.setBold(true);
        headStyleFont.setColor(IndexedColors.WHITE.getIndex());
        headStyle.setFont(headStyleFont);

        CellStyle textStyle = workbook.createCellStyle();
        Font textStyleFont = workbook.createFont();
        textStyleFont.setFontHeightInPoints((short) 8);
        textStyle.setFont(textStyleFont);

        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        headRow.setHeightInPoints(22);

        List<String> headers = Arrays.asList("Categoria", "Email", "Nombre", "Ape. paterno", "Ape. materno", "DNI",
                "Loan Id", "Credit Id", "Evento");

        for (String header : headers) {
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue(header);
        }

        for (PersonInteractionFollowUpReport report : reports) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getInitcap());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getEmail());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getFirstSurname());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getLastSurname());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getDocumentNumber());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getLoanApplicationId());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getCreditId());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(report.getEvent());
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }

    @Override
    public byte[] createLoanInProcessReportFDLM(Date registerDateReport, Date processDateReport, String countryId, Integer documentType, String documentNumber, String lastname, Date startDate, Date endDate, Date updatedStartDate, Date updatedEndDate, Integer[] applicationStatuses) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Solicitudes en proceso");

        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 8);
        headStyleFont.setBold(true);
        headStyleFont.setColor(IndexedColors.WHITE.getIndex());
        headStyle.setFont(headStyleFont);

        CellStyle textStyle = workbook.createCellStyle();
        Font textStyleFont = workbook.createFont();
        textStyleFont.setFontHeightInPoints((short) 8);
        textStyle.setFont(textStyleFont);

        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 8);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle filterLabelStyle = workbook.createCellStyle();
        Font filterLabelFont = workbook.createFont();
        filterLabelFont.setFontHeightInPoints((short) 8);
        filterLabelFont.setBold(true);
        filterLabelStyle.setFont(filterLabelFont);

        String filterLoanRegisterDate = "";
        if (startDate != null && endDate != null)
            filterLoanRegisterDate = utilService.dateFormat(startDate) + " - " + utilService.dateFormat(endDate);

        String filterLoanUpdatedDate = "";
        if (startDate != null && endDate != null)
            filterLoanUpdatedDate = utilService.dateFormat(updatedStartDate) + " - " + utilService.dateFormat(updatedEndDate);

        String filterApplicationStatuses = "";
        if (applicationStatuses != null)
            filterApplicationStatuses = getLoanStatusesByIds(applicationStatuses);
        else
            filterApplicationStatuses = "Todos";

        List<com.affirm.fdlm.report.LoanApplicationInProcessReport> paths = reportsDao.getLoanApplicationInProcessReportFDLM(documentType, documentNumber, lastname, startDate, endDate, updatedStartDate, updatedEndDate, applicationStatuses);
        if (paths == null)
            paths = new ArrayList<>();

        Row filterRow1 = sheet.createRow(sheet.getPhysicalNumberOfRows());

        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Fec. Generación reporte");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(registerDateReport));
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue(catalogService.getIdentityDocumentType(documentType).getName() + " solicitante");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue(documentNumber);
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Apellido solicitante");
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue(lastname);
        createCell(filterRow1, filterRow1.getPhysicalNumberOfCells(), textStyle).setCellValue("");

        Row filterRow2 = sheet.createRow(sheet.getPhysicalNumberOfRows());

        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Fec. Solicitud");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue(filterLoanRegisterDate);
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Fec. Actualizacion");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue(filterLoanUpdatedDate);
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue("");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), filterLabelStyle).setCellValue("Estado de solicitud");
        createCell(filterRow2, filterRow2.getPhysicalNumberOfCells(), textStyle).setCellValue(filterApplicationStatuses);

        sheet.createRow(sheet.getPhysicalNumberOfRows());

        List<String> headers = Arrays.asList(
                "Fecha de solicitud",
                "Fecha de última modificación",
                "N° de solicitud",
                "Monto solicitado",
                "Plazo",
                "Documento",
                "N° de documento",
                "Estado de la solicitud",
                "Motivo de rechazo",
                "Destino del crédito",
                "Teléfono",
                "Correo electrónico",
                "Nombres",
                "Primer Apellido",
                "Segundo Apellido",
                "Fecha de nacimiento",
                "Procedencia de solicitud",
                "Estado civil",
                "Dependientes",
                "Dirección de cliente",
                "Tipo de vivienda",
                "Localidad",
                "Nivel de estudios",
                "Actividad Principal",
                "Nombre de empresa donde labora",
                "Teléfono",
                "Fecha que inicio a laborar",
                "Dirección de empresa",
                "Localidad",
                "Profesión",
                "Sector",
                "Cargo",
                "Ingreso neto",
                "Tipo de contrato");

        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        headRow.setHeightInPoints(22);

        for (String header : headers) {
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue(header);
        }

        Currency currency = catalogService.getCurrency(Currency.COP);

        for (com.affirm.fdlm.report.LoanApplicationInProcessReport path : paths) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(path.getRegisterDate()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(utilService.dateFormat(path.getUpdatedTime()));
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getLoanApplicationCode());

            if (path.getAmount() != null)
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(utilService.doubleMoneyFormat(path.getAmount(), currency));
            else
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue("");

            if (path.getInstallments() != null)
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getInstallments());
            else
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue("");

            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getClientDocument() != null ? path.getClientDocument().getName() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getClientDocumentNumber());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getLoanApplicationStatus() != null ? path.getLoanApplicationStatus().getStatus() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getRejectionMessage() != null && path.getRejectionMessage().split("\\.").length >= 3 ?
                    messageSource.getMessage(path.getRejectionMessage(), null, new Locale("es", "CO")) :
                    path.getRejectionMessage() != null && path.getRejectionMessage().split("\\.").length < 3 ? path.getRejectionMessage() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(path.getLoanApplicationReason() != null ? path.getLoanApplicationReason().getReason() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getClientPhoneNumber());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getClientEmail());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getClientName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getClientFirstSurname());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getClientLastSurname());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getBirthday() != null ? utilService.dateFormat(path.getBirthday()) : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getLoanOriginId() != null ? question119Service.getFDLMLoanOrigins().get(path.getLoanOriginId()) : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getMaritalStatus() != null ? path.getMaritalStatus().getStatus() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getDependents());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getClientAddress());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getClientHousingType() != null ? path.getClientHousingType().getType() : "");

            if (path.getClientLocalityId() != null) {
                Department department = catalogService.getGeneralDepartment(CountryParam.COUNTRY_COLOMBIA).stream()
                        .filter(d -> d.getDepartmentId().toString().equals(path.getClientLocalityId().toString().substring(0, 4)))
                        .findFirst().orElse(null);
                District generalDistrict = catalogService.getGeneralDistrictById(path.getClientLocalityId());

                if (generalDistrict != null) {
                    String province = generalDistrict.getProvince().getName();
                    String district = generalDistrict.getName();

                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(String.format("%s, %s, %s", department != null ? department.getName() : "", province, district));
                } else {
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue("");
                }
            } else {
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue("");
            }

            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getStudyLevel() != null ? path.getStudyLevel().getLevel() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getActivityType() != null ? path.getActivityType().getType() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getCompanyName());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getCompanyPhoneNumber());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getClientCompanyStartDate() != null ? utilService.dateFormat(path.getClientCompanyStartDate()) : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getCompanyAddress());

            if (path.getCompanyLocalityId() != null) {
                Department department = catalogService.getGeneralDepartment(CountryParam.COUNTRY_COLOMBIA).stream()
                        .filter(d -> d.getDepartmentId().toString().equals(path.getClientLocalityId().toString().substring(0, 4)))
                        .findFirst().orElse(null);
                District generalDistrict = catalogService.getGeneralDistrictById(path.getCompanyLocalityId());

                if (generalDistrict != null) {
                    String province = generalDistrict.getProvince().getName();
                    String district = generalDistrict.getName();

                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(String.format("%s, %s, %s", department != null ? department.getName() : "", province, district));
                } else {
                    createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue("");
                }
            } else {
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue("");
            }

            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getProfessionOccupationId() != null ? catalogService.getProfessionOccupation(path.getProfessionOccupationId()).getOccupation() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getProfessionId() != null ? catalogService.getProfession(new Locale("es", "CO"), path.getProfessionId()).getProfession() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getOcupationId() != null ? catalogService.getOcupation(new Locale("es", "CO"), path.getOcupationId()).getOcupation() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getFixedGrossIncome() != null ? utilService.doubleMoneyFormat(path.getFixedGrossIncome(), currency) : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), textStyle).setCellValue(path.getContractType() != null ? question149Service.getContractKindByKey(path.getContractType()) : "");
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        workbook.close();
        outStream.close();

        return outStream.toByteArray();
    }



    private CellStyle getLeftStyle(Workbook workbook) {
        CellStyle textStyle = workbook.createCellStyle();
        DataFormat fmt = workbook.createDataFormat();
        textStyle.setAlignment(HorizontalAlignment.LEFT);
        textStyle.setDataFormat(fmt.getFormat("@"));
        return textStyle;
    }

    private CellStyle getRightStyle(Workbook workbook) {
        CellStyle textStyle = workbook.createCellStyle();
        DataFormat fmt = workbook.createDataFormat();
        textStyle.setAlignment(HorizontalAlignment.RIGHT);
        textStyle.setDataFormat(fmt.getFormat("@"));
        return textStyle;
    }

    private CellStyle getCenterStyle(Workbook workbook) {
        CellStyle textStyle = workbook.createCellStyle();
        DataFormat fmt = workbook.createDataFormat();
        textStyle.setAlignment(HorizontalAlignment.CENTER);
        textStyle.setDataFormat(fmt.getFormat("@"));
        return textStyle;
    }

    private CellStyle getRightDateStyle(Workbook workbook) {
        CellStyle cellStyleDate = workbook.createCellStyle();
        CreationHelper createHelperDate = workbook.getCreationHelper();
        cellStyleDate.setDataFormat(createHelperDate.createDataFormat().getFormat("yyyymmdd"));
        cellStyleDate.setAlignment(HorizontalAlignment.CENTER);
        return cellStyleDate;
    }

    private String formatInMinuteSeconds(long seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    private String getLoanStatusesByIds(Integer[] loanStatuses) {
        StringBuffer internalStatusesConcat = new StringBuffer();
        IntStream.range(0, loanStatuses.length).forEach(i -> internalStatusesConcat.append(StatusExtranetReport.getLoanStatuses().get(loanStatuses[i]) + ", "));

        String result = internalStatusesConcat.toString();

        if (result.length() > 0) {
            result = result.substring(0, result.length() - 2);
        }

        return result;
    }

    private String getInternalStatusesConcat(JSONArray internalStatusesArray) {
        String internalStatusesConcat = "";
        for (int i = 0; i < internalStatusesArray.length(); i++) {
            internalStatusesConcat += internalStatusesArray.getString(i) + ", ";
        }

        if (internalStatusesConcat.length() > 0) {
            internalStatusesConcat = internalStatusesConcat.substring(0, internalStatusesConcat.length() - 2);
        }

        return internalStatusesConcat;
    }
}



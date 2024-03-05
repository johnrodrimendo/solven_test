package com.affirm.client.service.impl;

import com.affirm.client.dao.EmployerCLDAO;
import com.affirm.client.model.EmployeeCompanyExtranetPainter;
import com.affirm.client.model.LoggedUserEmployer;
import com.affirm.client.service.ExtranetCompanyService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.InteractionDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.Employee;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by jrodriguez on 27/09/16.
 */

@Service("extranetCompanyService")
public class ExtranetCompanyServiceImpl implements ExtranetCompanyService {

    private static Logger logger = Logger.getLogger(ExtranetCompanyServiceImpl.class);

    @Autowired
    private UserDAO userDao;
    @Autowired
    private InteractionDAO interactionDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private EmployerCLDAO employerClDao;
    @Autowired
    private CreditDAO creditDao;

    @Override
    public void login(AuthenticationToken token, HttpServletRequest request) throws Exception {

        // Log in and sets the session timeout to 5 min
        SecurityUtils.getSubject().login(token);
        SecurityUtils.getSubject().getSession().setTimeout(Configuration.getExtranetTimeoutMinutes() * 60 * 1000);
        SecurityUtils.getSubject().getSession().setAttribute("extranetCompanyLoginId", ((LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal()).getSessionId());
    }

    @Override
    public void onLogout(int sessionId, Date logoutDate) throws Exception {
        userDao.registerSessionLogout(sessionId, logoutDate);
    }

    @Override
    public LoggedUserEmployer getLoggedUserEmployer() {
        return ((LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal());
    }

    @Override
    public Integer getLoggedUserActiveEntity() throws Exception{
        LoggedUserEmployer loggedUserEmployer = ((LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal());
        List<EntityProduct> entityProducts = catalogService.getEntityProducts();
        EntityProduct entity = entityProducts.stream().filter(e->e.getEmployer() != null && e.getEmployer().getId().equals(loggedUserEmployer.getActiveCompany().getId())).findFirst().orElse(null);
        if(entity != null)
            return entity.getEntityId();
        return null;
    }

    public Boolean validateAgreementProduct(int employerId) throws Exception {
        List<EntityProduct> entityProducts = catalogService.getEntityProducts();
        List<EntityProduct> finalEntityProducts = new ArrayList<>();
        for (EntityProduct entityProduct : entityProducts) {
            if (entityProduct.getEmployer() != null && entityProduct.getEmployer().getId() == employerId && entityProduct.getProduct().getId() != Product.AGREEMENT) {
                finalEntityProducts.add(entityProduct);
            }
        }
        if (finalEntityProducts.size() == 0)
            return false;
        return true;
    }

    @Override
    public Boolean validateProduct(int employerId, int productId) throws Exception {
        List<EntityProduct> entityProducts = catalogService.getEntityProducts();
        List<EntityProduct> finalEntityProducts = new ArrayList<>();
        for (EntityProduct entityProduct : entityProducts) {
            if (entityProduct.getEmployer() != null && entityProduct.getEmployer().getId() == employerId && entityProduct.getProduct().getId() == productId) {
                finalEntityProducts.add(entityProduct);
            }
        }
        if (finalEntityProducts.size() == 0)
            return false;
        return true;
    }

    @Override
    public boolean isCustomMaxAmountActivated() throws Exception {
        List<EntityProduct> entitiesOfEmployer = catalogService.getEntityProducts().stream()
                .filter(ep -> ep.getEmployer() != null && ep.getEmployer().getId().intValue() == getLoggedUserEmployer().getActiveCompany().getId()).collect(Collectors.toList());
        return entitiesOfEmployer != null && entitiesOfEmployer.stream().anyMatch(e -> e.getCustomMaxAmount() != null && e.getCustomMaxAmount());
    }

    @Override
    public void createEmployeesExcel(List<Employee> employees, OutputStream outputStream) throws Exception {

        boolean customMaxAmountActivated = isCustomMaxAmountActivated();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Planilla de colaboradores");

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

        // Setting widths
        for (int i = 0; i < 22; i++) {
            sheet.setColumnWidth(i, 15 * 256);
        }
        sheet.setColumnWidth(8, 20 * 256);
        sheet.setColumnWidth(9, 25 * 256);
        sheet.setColumnWidth(10, 20 * 256);
        sheet.setColumnWidth(11, 20 * 256);
        sheet.setColumnWidth(14, 20 * 256);
        sheet.setColumnWidth(15, 20 * 256);
        sheet.setColumnWidth(16, 20 * 256);

        // Paint head row
        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Tipo Doc.");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Número Doc");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Nombres");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Apellido Paterno");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Apellido Materno");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Fecha de Ingreso Laboral");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Tipo de Contrato");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Fin del Contrato");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Email");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Dirección");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Ingreso Bruto Fijo");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Ingreso Bruto Variable");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Descuentos");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Teléfono Móvil");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Banco");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Número de Cuenta");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("CCI");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Salario con Embargo");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Licencia s/ goce de haberes");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("RUC empresa");
        if (customMaxAmountActivated)
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Monto Máximo Prestamo");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Fecha de Registro");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Estado");

        // Paint employees
        for (Employee employee : employees) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getDocType().getName() != null ? employee.getDocType().getName() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getDocNumber() != null ? employee.getDocNumber() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getName() != null ? employee.getName() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getFirstSurname() != null ? employee.getFirstSurname() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getLastSurname() != null ? employee.getLastSurname() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getEmploymentStartDateDate() != null ? utilService.dateFormat(employee.getEmploymentStartDateDate()) : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(employee.getContractType() != null ? employee.getContractType().toString() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getContractEndDate() != null ? utilService.dateFormat(employee.getContractEndDate()) : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getWorkEmail() != null ? employee.getWorkEmail() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getAddress() != null ? employee.getAddress() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(employee.getFixedGrossIncome() != null ? employee.getFixedGrossIncome() : 0);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(employee.getVariableGrossIncome() != null ? employee.getVariableGrossIncome() : 0);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(employee.getMonthlyDeduction() != null ? employee.getMonthlyDeduction() : 0);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getPhoneNumber() != null ? employee.getPhoneNumber() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getBank() != null ? employee.getBank() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getAccountNumber() != null ? employee.getAccountNumber() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getAccountNumberCci() != null ? employee.getAccountNumberCci() : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(
                    employee.getSalaryGarnishment() != null ? (employee.getSalaryGarnishment() ? "1" : "0") : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle).setCellValue(
                    employee.getUnpaidLeave() != null ? (employee.getUnpaidLeave() ? "1" : "0") : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(
                    employee.getEmployer() != null ? (employee.getEmployer().getRuc() != null ? employee.getEmployer().getRuc() : "") : "");
            if (customMaxAmountActivated)
                createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getCustomMaxAmount() != null ? employee.getCustomMaxAmount() : 0);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getRegisterDate() != null ? utilService.dateFormat(employee.getRegisterDate()) : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle).setCellValue(employee.getActive() != null && employee.getActive() ? "Activo" : "Inactivo");
        }

        workbook.write(outputStream);
        workbook.close();
    }

    @Override
    public void createImportEmployeesExcelTemplate(OutputStream outputStream) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();
        boolean customMaxAmountActivated = isCustomMaxAmountActivated();
        boolean validateAgreementProduct = validateAgreementProduct(user.getActiveCompany().getId());

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Planilla de colaboradores");

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
        for (int i = 0; i < 20; i++) {
            sheet.setColumnWidth(i, 20 * 256);
        }
        sheet.setColumnWidth(5, 25 * 256);
        sheet.setColumnWidth(18, 25 * 256);

        // Paint head row
        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "Solo se permite DNI o CEX.", sheet, workbook).setCellValue("Tipo Documento*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Nro. Documento*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Nombres*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Apellido Paterno*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Apellido Materno");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "Formato DD/MM/YYYY", sheet, workbook).setCellValue("Fecha de Ingreso Laboral*");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "En caso de contrato indefinido, poner I. En caso de contrato definido, poner D.", sheet, workbook).setCellValue("Tipo de Contrato");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "En caso de Tipo de Contrato \"definido\" (valor D), debe consignarse la fecha de fin del contrato en formato DD/MM/YYYY.", sheet, workbook).setCellValue("Fin del Contrato");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "Ingresar correo electrónico laboral.", sheet, workbook).setCellValue("Email");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "Dirección de residencia del colaborador.", sheet, workbook).setCellValue("Dirección*");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "En Nuevos Soles. Consignar sólo el valor, sin el signo de moneda.", sheet, workbook).setCellValue("Ingreso Bruto Fijo*");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "Promedio de los últimos 3 meses. En Nuevos Soles. Consignar sólo el valor, sin el signode moneda.", sheet, workbook).setCellValue("Ingreso Bruto Variable");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "Descuentos mensual por créditos o otros.", sheet, workbook).setCellValue("Descuentos");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Teléfono Móvil");
        if (validateAgreementProduct)
            createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "Debe ingresarse Banco y Cuenta o CCI.", sheet, workbook).setCellValue("Banco");
        else
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Banco*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue(validateAgreementProduct ? "Número de Cuenta" : "Número de Cuenta*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("CCI");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "En caso de tener un embargo sobre el salario, de cualquier tipo, indicar 1, sino nulo ó 0.", sheet, workbook).setCellValue("Salario con Embargo*");
        createCellWithComment(headRow, headRow.getPhysicalNumberOfCells(), headStyle, "En caso de licencia sin goce de haberes o licencias pagadas por Essalud, indicar 1; sino nulo ó 0.", sheet, workbook).setCellValue("Licencia s/ goce de haberes*");
        createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("RUC empresa");
        if (customMaxAmountActivated)
            createCell(headRow, headRow.getPhysicalNumberOfCells(), headStyle).setCellValue("Monto máximo prestamo");

        // Print demo values
        for (int i = 0; i < 2; i++) {
            Row reportRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "dni" : "ce");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "12345678" : "123456789");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "José" : "Juan");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "Peña" : "Nieto");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "Rodriguez" : "Diaz");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "14/01/2017" : "14/01/2017");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle, CellType.STRING).setCellValue(i == 0 ? "D" : "I");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "14/05/2017" : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "jose@dominio.com" : "juan@dominio.com");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "Av. Los Delfines – Miraflores" : "Av. Canevaro - Lince");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle, CellType.NUMERIC).setCellValue(i == 0 ? 5000 : 3400);
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle, CellType.NUMERIC).setCellValue(i == 0 ? "" : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle, CellType.STRING).setCellValue(i == 0 ? "" : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "987654321" : "");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "Interbank" : "BCP");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "1234567890" : "951984984199");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "" : "12345678912345678912");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle, CellType.STRING).setCellValue(i == 0 ? "1" : "0");
            createCell(reportRow, reportRow.getPhysicalNumberOfCells(), quantityStyle, CellType.STRING).setCellValue(i == 0 ? "0" : "1");
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

    @Override
    public List<EmployeeCompanyExtranetPainter> getPendingAuthorizationEmployees(Locale locale) throws Exception{
        Employer activeCompany = getLoggedUserEmployer().getActiveCompany();
        List<EmployeeCompanyExtranetPainter> employees = employerClDao.getEmployerEmployeesPendingAuthorization(activeCompany.getId(), 0, 1000, locale);
        if (employees == null)
            employees = new ArrayList<>();

        for(EmployeeCompanyExtranetPainter employee : employees){
            if(employee.getActiveCreditId() != null){
                Credit credit = creditDao.getCreditByID(employee.getActiveCreditId(), locale, false, Credit.class);

                // Set if Aelu buttons are shown
                if (credit.getEntityProductParameterId().equals(EntityProductParams.ENT_PROD_PARAM_AELU_CONVENIO)) {
                    switch (credit.getSubStatus().getId()){
                        case CreditSubStatus.AELU_PENDING_PRELIMINARY_DOCUMENTATION:
                            employee.setShowAeluRecivePreliminaryDocButton(true);
                            break;
                        case CreditSubStatus.AELU_PENDING_PROMISORY_NOTE:
                            employee.setShowAeluRecivePromisoryNoteButton(true);
                            break;
                    }
                }
            }
        }

        return employees;
    }

}

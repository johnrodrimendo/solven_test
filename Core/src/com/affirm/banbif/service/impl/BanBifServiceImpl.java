package com.affirm.banbif.service.impl;

import com.affirm.acceso.model.Direccion;
import com.affirm.banbif.model.BanbifFileData;
import com.affirm.banbif.model.KonectaLeadResponse;
import com.affirm.banbif.service.BanBifService;
import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.FileService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.SftpUtil;
import com.affirm.sentinel.rest.BanbifServiceCall;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.vfs2.FileObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("banBifService")
public class BanBifServiceImpl implements BanBifService {

    public static final String GA_TRACKING_ID = "UA-xxxxxxxxxxxxxxxxxxxxx";

    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private FileService fileService;
    @Autowired
    private RccDAO rccDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private BanbifServiceCall banbifServiceCall;

    private final String FTP_HOST = "xxxxxxxxxxxxxxxxxxxxx";
    private final String FTP_USER = "xxxxxxxxxxxxxxxxxxxxx";
    private final String FTP_PWD = "xxxxxxxxxxxxxxxxxxxxx";
    private final String FTP_FILE_PATH = "/bases";
    private final String FTP_FILENAME = "base_conversiones.csv";
    private final List<String> FILE_HEADERS = java.util.Arrays.asList("DNI","CONVERSION_DATE","STATUS","VALUE");
    private final List<String> validDateFormats = Arrays.asList("dd/MM/yyyy", "dd-MM-yyyy");
    private final String FTP_TARGET_PATH_BACKUP = "/backups/";

    SftpUtil sftpUtil;

    public void readBaseFile() throws Exception {
        boolean fileExists = SftpUtil.exist(FTP_HOST, FTP_USER,FTP_PWD, FTP_FILE_PATH+ "/" +FTP_FILENAME);
        if(fileExists){
            String localFilePath = "/tmp/"+FTP_FILENAME;
            SftpUtil.download(FTP_HOST, FTP_USER,FTP_PWD,localFilePath, FTP_FILE_PATH+ "/" +FTP_FILENAME);
            List<BanbifFileData> data = getFileData(localFilePath);
            if(!data.stream().anyMatch(e -> !e.getErrors().isEmpty())){
                updateDataConversionDate(data);
                uploadFileBackup(localFilePath);
                SftpUtil.delete(FTP_HOST, FTP_USER,FTP_PWD, FTP_FILE_PATH+ "/" +FTP_FILENAME);
            }
            else sendErrorEmailProcessFile(data);
        }
        else{
            this.sendEmailNotification("ERROR_FILE", "No se encontró archivo para procesar. <br>");
        }
    }

    private void sendErrorEmailProcessFile(List<BanbifFileData> list) throws Exception {
        String errorMessage = "";
        for (int i = 0; i < list.size(); i++) {
            if(!list.get(i).getErrors().isEmpty()) errorMessage += String.format("Fila %s: %s <br>", i+1, list.get(i).getErrors().toString());
        }
        this.sendEmailNotification("ERROR_FILE", errorMessage);
    }

    private void updateDataConversionDate(List<BanbifFileData> list) throws Exception {
        String additionalMessage = "";
        int errorsCount = 0;
        if(!list.stream().anyMatch(e -> !e.getErrors().isEmpty())){
            int position = -1;
            for (BanbifFileData data : list) {
                position++;
                Integer personId = personDAO.getPersonIdByDocument(IdentityDocumentType.DNI,data.getDocumentNumber());
                if(personId != null){
                    Integer[] creditIds = creditDAO.getActiveCreditIdsByPerson(Configuration.getDefaultLocale(), personId, Entity.BANBIF);
                    if(creditIds == null || creditIds.length == 0) {
                        LoanApplication loan = loanApplicationDao.getActiveLoanApplicationByPerson(Configuration.getDefaultLocale(), personId, ProductCategory.TARJETA_CREDITO, Entity.BANBIF);
                        if(loan != null && loan.getStatus() != null && loan.getStatus().getId() == LoanApplicationStatus.LEAD_REFERRED){
                            if(data.getValue() != null){
                                loanApplicationDao.updateLoanApplicationStatus(loan.getId(), LoanApplicationStatus.LEAD_CONVERTED, null);
                                loan.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_CONVERSION_VALUE.getKey(), data.getValue());
                                loanApplicationDao.updateEntityCustomData(loan.getId(), loan.getEntityCustomData());
                            }else{
                                errorsCount++;
                                additionalMessage += String.format("Fila %s - Documento: %s Error: No han enviado el valor de conversion.<br>", position + 1, data.getDocumentNumber());
                            }
                        }
                        else{
                            errorsCount++;
                            additionalMessage += String.format("Fila %s - Documento: %s Error: No se ha encontrado crédito para la persona indicada. <br>", position + 1, data.getDocumentNumber());
                        }
                    }
                    else{
                        for (Integer creditId : creditIds) {
                            Credit credit = creditDAO.getCreditByID(creditId, Configuration.getDefaultLocale(), false, Credit.class);
                            if(credit.getStatus().getId() == CreditStatus.ORIGINATED){
                                if(data.getStatus().equals(BanbifFileData.APPROVED_STATUS)){
                                    creditDAO.updateCreditStatus(creditId, CreditStatus.ORIGINATED_DISBURSED, null);
                                }else{
                                    creditDAO.updateCreditStatus(creditId, CreditStatus.REJECTED, null);
                                }
                            }
                        }
                    }
                }
                else {
                    errorsCount++;
                    additionalMessage += String.format("Fila %s - Documento: %s Error: No se ha encontrado información de la persona en nuestros registros. <br>", position+1, data.getDocumentNumber());
                }
            }
            if(!additionalMessage.isEmpty()) {
                additionalMessage += String.format("<b>Total registros:</b> %s <br> <b>Total procesados:</b> %s <br> <b>Total errores:</b> %s <br>", list.size(), list.size() - errorsCount, errorsCount);
                this.sendEmailNotification("PROCESS_ALERT",additionalMessage);
            }
        }
        else sendErrorEmailProcessFile(list);
    }


    public void sendEmailNotification(String type, String additionalText) throws Exception {
        if(type == null) return;
        String bodyMessage = "";
        String subject = "";
        switch (type){
            case "ERROR_FILE":
                subject = "Solven - Banbif | Error en el procesamiento del archivo de conversiones";
                bodyMessage = "Hola,  ocurrió un error al procesar el archivo: <br> <br> {{additionalText}} <br> Saludos <br> ".replace("{{additionalText}}", additionalText == null ? "Sin especificar excepciones" : additionalText);
                break;
            case "PROCESS_ALERT":
                subject = "Solven - Banbif | Procesamiento del archivo de conversiones con excepciones";
                bodyMessage = "Hola,  se procesó el archivo pero se detectaron los siguientes inconvenientes: <br>  <br> {{additionalText}} <br> Saludos <br> ".replace("{{additionalText}}", additionalText == null ? "Sin especificar excepciones" : additionalText);
                break;
        }

        awsSesEmailService.sendRawEmail(
                null,
                "procesos@solven.pe",
                null,
                "dev@solven.pe",
                null,
                subject,
                bodyMessage,
                bodyMessage,
                null,
                null, null, null, null);
    }


    public List<BanbifFileData> getFileData(String filePath) throws FileNotFoundException {
        List<BanbifFileData> dataList = new ArrayList<>();
        File csvFile = new File(filePath);
        if (csvFile.isFile()) {
            InputStream inputStream = new FileInputStream(csvFile);
            if(filePath.contains(".csv")) inputStream = this.csvToXlsx(inputStream,",");
            try (org.apache.poi.ss.usermodel.Workbook workbook = new XSSFWorkbook(inputStream)) {
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rows = sheet.rowIterator();
                if(!hasValidHeaders(rows)){
                    this.sendEmailNotification("ERROR_FILE","FILE HAS INVALID HEADERS");
                    return dataList;
                }
                return convertFileDataToList(rows);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        return dataList;
    }

    private List<BanbifFileData> convertFileDataToList(Iterator<Row> rows){
        List<BanbifFileData> dataList = new ArrayList<>();
        int rowIdx = 0;
        while (rows.hasNext()) {
            if (rowIdx == 0) {
                rowIdx++;
                continue;
            }

            Row currentRow = rows.next();
            BanbifFileData data = new BanbifFileData();

            for (int i = 0; i < FILE_HEADERS.size(); i++) {
                Cell cell = currentRow.getCell(i);

                if (cell != null) {
                    cell.setCellType(CellType.STRING);
                }

                switch (i) {
                    case 0:
                        if(cell != null){
                            data.setDocumentNumber(cell.getStringCellValue());
                            if (!data.getDocumentNumber().matches("^[0-9]{5,8}$")) {
                                data.getErrors().add("INVALID DOCUMENT NUMBER");
                            }
                        }
                        else data.getErrors().add("DOCUMENT NUMBER IS REQUIRED");
                        break;
                    case 1:
                        if(cell != null){
                            try{
                                if(cell.getStringCellValue().contains("/")){
                                    data.setConversionDate(new SimpleDateFormat(this.validDateFormats.get(0)).parse(cell.getStringCellValue()));
                                }
                                else if(cell.getStringCellValue().contains("-")){
                                    data.setConversionDate(new SimpleDateFormat(this.validDateFormats.get(0)).parse(cell.getStringCellValue()));
                                }
                                else data.getErrors().add("CONVERSION DATE INVALID FORMAT");
                            }
                            catch (ParseException e){
                                data.getErrors().add("CONVERSION DATE INVALID FORMAT");
                            }
                        }
                        else data.getErrors().add("CONVERSION DATE IS REQUIRED");
                        break;
                    case 2:
                        if(cell != null){
                            if(cell.getStringCellValue().equals(BanbifFileData.APPROVED_STATUS) || cell.getStringCellValue().equals(BanbifFileData.REJECTED_STATUS)){
                                data.setStatus(cell.getStringCellValue());
                            }
                            else data.getErrors().add("INVALID STATUS FORMAT");
                        }
                        else data.getErrors().add("STATUS IS REQUIRED");
                        break;
                    case 3:
                        if(cell != null && cell.getStringCellValue() != null){
                            if (cell.getStringCellValue().matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                                data.setValue(Double.valueOf(cell.getStringCellValue()));
                            }
                            else data.getErrors().add("INVALID VALUE FORMAT");
                        }
                        break;
                }
            }
            dataList.add(data);
        }
        return dataList;
    }

    @Override
    public InputStream csvToXlsx(InputStream is, String delimiter) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            org.apache.poi.ss.usermodel.Workbook workbook = new XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet();
            String line = br.readLine();

            for (int rows = 0; line != null; rows++) {
                Row row = sheet.createRow(rows);
                String[] items = line.split(delimiter);

                for (int i = 0, col = 0; i < items.length; i++) {
                    String item = items[i];
                    Cell cell = row.createCell(col++);
                    cell.setCellValue(item);
                }

                line = br.readLine();
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            return new ByteArrayInputStream(barray);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean hasValidHeaders(Iterator<Row> rows) {
        List<String> headers = new ArrayList<>();
        Row currentRow = rows.next();
        Iterator<Cell> cells = currentRow.cellIterator();

        while (cells.hasNext()) {
            Cell cell = cells.next();

            if (FILE_HEADERS.size() == headers.size()) {
                break;
            }

            String headerName = cell.getStringCellValue();

            if (headerName.contains(" ")) {
                headerName = headerName.replaceAll(" ", "_");
            }

            headers.add(headerName);
        }

        for (int i = 0; i < headers.size(); i++) {
            if (!headers.get(i).toUpperCase().contains(FILE_HEADERS.get(i).toUpperCase())) {
                return false;
            }
        }

        return true;
    }

    private void uploadFileBackup(String filePath) throws Exception {
        File file = new File(filePath);
        if (file.exists()){
            String filenameBackup = FTP_FILENAME;
            filenameBackup = filenameBackup.replace(".", "-"+ new SimpleDateFormat("dd-MM-yyy-HH:mm").format(new Date())+".");
            Map<String, InputStream> map = new HashMap<>();
            map.put(filenameBackup, new FileInputStream(file));
            SftpUtil.upload(FTP_HOST, FTP_USER, FTP_PWD, map, FTP_TARGET_PATH_BACKUP, "");
            fileService.bucketOverrideCustomBucket("banbif-bases", "backup/",filenameBackup, Files.readAllBytes(file.toPath()),false);
            file.delete();
        }
    }

    @Override
    public void updateBaseValuesInLoan(LoanApplication loanApplication, String documentNumber) throws Exception {
        BanbifPreApprovedBase banbifPreApprovedBase =  getBanbifPreApprovedBase("DNI", documentNumber, loanApplication);
        if(banbifPreApprovedBase != null){
            Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
            StaticDBInfo staticDBInfo = personDAO.getInfoFromStaticDB(loanApplication.getPersonId());
            if(staticDBInfo != null && staticDBInfo.getRuc() != null){
                personDAO.cleanOcupationalInformation(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL);
                personDAO.updateOcupationalActivityType(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, ActivityType.DEPENDENT);
                personDAO.updateOcupationalRuc(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, staticDBInfo.getRuc());
                personDAO.updateOcupationalCompany(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, staticDBInfo.getRazonSocial());
            }
            if(banbifPreApprovedBase.getNombres() != null && !banbifPreApprovedBase.getNombres().trim().isEmpty() && (person.getName() == null || person.getName().isEmpty())) personDAO.updateName(loanApplication.getPersonId(), banbifPreApprovedBase.getNombres().trim());
            if(banbifPreApprovedBase.getApellidos() != null && !banbifPreApprovedBase.getApellidos().trim().isEmpty() && (person.getFirstSurname() == null || person.getFirstSurname().isEmpty())) personDAO.updateFirstSurname(loanApplication.getPersonId(), banbifPreApprovedBase.getApellidos().trim());
            loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_TIPO_TARJETA_DE_BASE.getKey(), banbifPreApprovedBase.getPlastico());
            loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey(), new JSONObject(new Gson().toJson(banbifPreApprovedBase)));
            loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
        }
    }

    @Override
    public BanbifPreApprovedBase getBanbifPreApprovedBase(String documentType, String documentNumber, LoanApplication loanApplication) {
        Boolean landingMasEfectivo = JsonUtil.getBooleanFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANBIF_TC_MASEFECTIVO_LANDING.getKey(), null);

        List<BanbifPreApprovedBase> bases = rccDao.getBanbifPreApprovedBase(documentType, documentNumber);
        if(bases.size() == 1)
            return bases.get(0);

        BanbifPreApprovedBase baseToReturn = null;
        if(landingMasEfectivo != null) {
            if(landingMasEfectivo)
                baseToReturn = bases.stream().filter(b -> b.getTipoBase().equalsIgnoreCase(BanbifPreApprovedBase.BANBIF_MAS_EFECTIVO_TIPO_BASE)).findFirst().orElse(null);
            else
                baseToReturn = bases.stream().filter(b -> !b.getTipoBase().equalsIgnoreCase(BanbifPreApprovedBase.BANBIF_MAS_EFECTIVO_TIPO_BASE)).findFirst().orElse(null);
        }
        if (baseToReturn == null)
            return bases.stream().findFirst().orElse(null);
        else
            return baseToReturn;
    }

    @Override
    public void callKonectaLead(Integer loanApplicationId) throws Exception{
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        Person person = personDAO.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale());
        User user = userDAO.getUser(loanApplication.getUserId());
        KonectaLeadResponse response = banbifServiceCall.callKonectaLead(loanApplication,person, user);

        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_CALL_KONECTA_LEAD.getKey(), true);
        loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
    }
}

package com.affirm.preapprovedbase.service.impl;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PreApprovedDAO;
import com.affirm.common.dao.RccDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Report;
import com.affirm.common.model.transactional.ReportProces;
import com.affirm.common.service.FileService;
import com.affirm.common.service.WebscrapperService;
import com.affirm.preapprovedbase.model.PreApprovedBaseProcessed;
import com.affirm.preapprovedbase.service.PreApprovedBaseService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.*;

@Service("preApprovedBaseService")
public class PreApprovedBaseServiceImpl implements PreApprovedBaseService {

    @Autowired
    private FileService fileService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private RccDAO rccDAO;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private PreApprovedDAO preApprovedDAO;

    private final List<String> validHeadersPrisma = Arrays.asList("DOCUMENT_NUMBER",	"DOCUMENT_TYPE",	"NAMES",	"LAST_NAME", "MAX_INSTALLMENTS", "MAX_AMOUNT",	"TEA");

    private final String BUCKET = "pre-approved-bases";
    private final boolean IS_LOCAL = Configuration.hostEnvIsLocal();
    private final boolean IS_STG = Configuration.hostEnvIsStage();
    private final boolean IS_PRD = Configuration.hostEnvIsProduction();
    private final Integer DEFAULT_LENGTH = 8;

    private final String PRISMA_PRE_APPROVED_BASE_TABLE_TEMP = "bases.tb_prisma_pre_approved_temp";
    private final String PRISMA_PRE_APPROVED_BASE_TABLE = "bases.tb_prisma_pre_approved";

    private final String AZTECA_PRE_APPROVED_BASE_TABLE_TEMP = "bases.tb_azteca_pre_approved_temp";
    private final String AZTECA_PRE_APPROVED_BASE_TABLE = "bases.tb_azteca_pre_approved";

    @Override
    public void uploadFileToBucket(MultipartFile file, Integer entityId, Integer entityUserId) throws Exception {
        String principalFolder = IS_LOCAL ? "local" : ( IS_STG ? "stg" : (IS_PRD ? "prd" : "other"));
        String filename = Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("/ +/","").replaceAll(" ","_").replaceAll("\\s+","");
        String fileUrl =  fileService.uploadFileToCustomBucket(BUCKET, principalFolder+"/"+entityId+"/", new Date().getTime()+"_"+filename, file.getBytes(), false);
        createUploadProcessPreApprovedBase(fileUrl,entityId,entityUserId);
    }

    @Override
    public void uploadData(String url, Integer entityId) throws IOException, SQLException {
        byte[] fileBytes = fileService.downloadFile(url);
        if(fileBytes == null || fileBytes.length == 0) throw new RuntimeException("File doesnt exists");
        switch (entityId){
            case Entity.PRISMA:
                File csvFile = fileService.convertByteArrayToFile(fileBytes, new Date().getTime()+".csv");
                rccDAO.copyCSVToPrismaTable(csvFile, PRISMA_PRE_APPROVED_BASE_TABLE_TEMP);
                rccDAO.truncateTable(PRISMA_PRE_APPROVED_BASE_TABLE);
                rccDAO.copyTableToAnother(PRISMA_PRE_APPROVED_BASE_TABLE_TEMP,PRISMA_PRE_APPROVED_BASE_TABLE);
                break;
            case Entity.AZTECA:
                File csvFileAzteca = fileService.convertByteArrayToFile(fileBytes, new Date().getTime()+".csv");
                formattingCSVFile(csvFileAzteca,Arrays.asList(4,5,8,9,10,14,15,16,17,18,19,20,21,22,23),Arrays.asList(0), DEFAULT_LENGTH, ',');
                rccDAO.copyCSVToTable(csvFileAzteca, AZTECA_PRE_APPROVED_BASE_TABLE_TEMP, true,"FORCE_NULL(capacidad,oferta_max,landing_pp,landing_cc,landing_moto,tasa_2999,tasa_5999,tasa_9999,tasa_19999,tasa_mas,oferta_12Meses,oferta_18Meses,oferta_24Meses,oferta_36Meses,oferta_mas)");
                rccDAO.truncateTable(AZTECA_PRE_APPROVED_BASE_TABLE);
                rccDAO.copyTableToAnother(AZTECA_PRE_APPROVED_BASE_TABLE_TEMP,AZTECA_PRE_APPROVED_BASE_TABLE);
                break;
        }
    }

    @Override
    public PreApprovedBaseProcessed createUploadProcessPreApprovedBase(String url, Integer entityId, Integer entityUserId) throws Exception {
        Integer processId = preApprovedDAO.registerUploadPreApprovedProcess(entityId,url,entityUserId);

        PreApprovedBaseProcessed preApprovedBaseProcessed = new PreApprovedBaseProcessed();
        preApprovedBaseProcessed.setId(processId);
        preApprovedBaseProcessed.setEntityId(entityId);
        preApprovedBaseProcessed.setUrl(url);
        preApprovedBaseProcessed.setEntityUserId(entityUserId);

        webscrapperService.callUploadPreApprovedBaseCSV(processId);

        return preApprovedBaseProcessed;

    }

    private void formattingCSVFile(File file, List<Integer> columns,List<Integer> columnsToFormatNumber,  char delimiter){
        this.formattingCSVFile(file,columns, columnsToFormatNumber, null, delimiter);
    }

    private void formattingCSVFile(File file, List<Integer> columns,  char delimiter){
        this.formattingCSVFile(file,columns, new ArrayList<Integer>(), null, delimiter);
    }

    private void formattingCSVFile(File file, List<Integer> columns, List<Integer> columnsToFormatNumber, Integer lengthDefault, char delimiter){
        if(lengthDefault == null) lengthDefault = DEFAULT_LENGTH;
        try {
            // Read existing file
            CSVReader reader = new CSVReaderBuilder(new FileReader(file)).build();
            List<String[]> csvBody = reader.readAll();
            // get CSV row column  and replace with by using row and column
            for(int i=0; i<csvBody.size(); i++){
                String[] strArray = csvBody.get(i);
                for(int j=0; j<strArray.length; j++){
                    if(csvBody.get(i)[j] != null && (csvBody.get(i)[j].equalsIgnoreCase("NULL") || csvBody.get(i)[j].isEmpty())) csvBody.get(i)[j] = "";
                    if(columns != null && columns.contains(j)){
                        csvBody.get(i)[j] = csvBody.get(i)[j].replace(",", ".");
                    }
                    if(columnsToFormatNumber != null && columnsToFormatNumber.contains(j)){
                        csvBody.get(i)[j] = completeWithZeroLeft(csvBody.get(i)[j],lengthDefault);
                    }
                }
            }
            reader.close();

            CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8),
                    delimiter,
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            writer.writeAll(csvBody);
            writer.flush();
            writer.close();
        } catch(IOException e) {
            System.out.println(" There was an Error Reading the file. " + e.getMessage());
        }
    }

    private String completeWithZeroLeft(String phrase, Integer minLength) {
        if (phrase != null && minLength != null) {
            String filled = StringUtils.repeat("0", Math.abs(phrase.length() - minLength));
            return filled + phrase;
        }
        return phrase;
    }


}

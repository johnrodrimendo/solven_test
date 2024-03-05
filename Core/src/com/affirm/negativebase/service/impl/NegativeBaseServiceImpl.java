package com.affirm.negativebase.service.impl;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.NegativeBaseProcessDAO;
import com.affirm.common.dao.PreApprovedDAO;
import com.affirm.common.dao.RccDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.service.FileService;
import com.affirm.common.service.WebscrapperService;
import com.affirm.negativebase.model.NegativeBaseProcessed;
import com.affirm.negativebase.service.NegativeBaseService;
import com.affirm.preapprovedbase.model.PreApprovedBaseProcessed;
import com.affirm.preapprovedbase.service.PreApprovedBaseService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service("negativeBaseService")
public class NegativeBaseServiceImpl implements NegativeBaseService {

    @Autowired
    private FileService fileService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private RccDAO rccDAO;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private NegativeBaseProcessDAO negativeBaseProcessDAO;


    private final String BUCKET = "negative-bases";
    private final boolean IS_LOCAL = Configuration.hostEnvIsLocal();
    private final boolean IS_STG = Configuration.hostEnvIsStage();
    private final boolean IS_PRD = Configuration.hostEnvIsProduction();

    private final String PRISMA_NEGATIVE_BASE_TABLE_TEMP = "bases.tb_prisma_negative_base_temp";
    private final String PRISMA_NEGATIVE_BASE_TABLE = "bases.tb_prisma_negative_base";

    @Override
    public void uploadFileToBucket(MultipartFile file, Integer entityId, Integer entityUserId, Character type) throws Exception {
        String principalFolder = IS_LOCAL ? "local" : ( IS_STG ? "stg" : (IS_PRD ? "prd" : "other"));
        String filename = Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("/ +/","").replaceAll(" ","_").replaceAll("\\s+","");
        String fileUrl =  fileService.uploadFileToCustomBucket(BUCKET, principalFolder+"/"+entityId+"/", new Date().getTime()+"_"+filename, file.getBytes(), false);
        createUploadProcessNegativeBase(fileUrl,entityId,entityUserId, type);
    }

    @Override
    public void uploadData(String url, Integer entityId, Character type) throws IOException, SQLException {
        byte[] fileBytes = fileService.downloadFile(url);
        if(fileBytes == null || fileBytes.length == 0) throw new RuntimeException("File doesnt exists");
        switch (entityId){
            case Entity.PRISMA:
                File csvFile = fileService.convertByteArrayToFile(fileBytes, new Date().getTime()+".csv");
                rccDAO.copyCSVToPrismaTable(csvFile, PRISMA_NEGATIVE_BASE_TABLE_TEMP);
                if(type != null && type.equals(NegativeBaseProcessed.PROCESS_TYPE_OVERWRITE)) rccDAO.truncateTable(PRISMA_NEGATIVE_BASE_TABLE);
                rccDAO.copyTableToAnother(PRISMA_NEGATIVE_BASE_TABLE_TEMP,PRISMA_NEGATIVE_BASE_TABLE);
                break;
        }
    }

    @Override
    public NegativeBaseProcessed createUploadProcessNegativeBase(String url, Integer entityId, Integer entityUserId, Character type) throws Exception {
        Integer processId = negativeBaseProcessDAO.registerUploadNegativeBaseProcess(entityId,url,entityUserId, type);

        NegativeBaseProcessed negativeBaseProcessed = new NegativeBaseProcessed();
        negativeBaseProcessed.setId(processId);
        negativeBaseProcessed.setEntityId(entityId);
        negativeBaseProcessed.setUrl(url);
        negativeBaseProcessed.setType(type);
        negativeBaseProcessed.setEntityUserId(entityUserId);
        webscrapperService.callUploadNegativeBaseCSV(processId);

        return negativeBaseProcessed;

    }


}

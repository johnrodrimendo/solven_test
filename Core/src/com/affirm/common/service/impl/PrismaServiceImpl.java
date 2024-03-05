package com.affirm.common.service.impl;

import com.affirm.banbif.model.BanbifFileData;
import com.affirm.banbif.service.BanBifService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.RccDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.FileService;
import com.affirm.common.service.PrismaService;
import com.affirm.common.util.SftpUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("prismaService")
public class PrismaServiceImpl implements PrismaService {

    @Autowired
    private PersonDAO personDao;
    @Autowired
    private RccDAO rccDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;

    @Override
    public void updateBaseValuesInLoan(LoanApplication loanApplication, Person person) throws Exception {
        PrismaPreApprovedBase prismaPreApprovedBase =  rccDao.getPrismaPreApprovedBase(person.getDocumentType().getName(), person.getDocumentNumber());
        if(prismaPreApprovedBase != null){
            personDao.registerPreApprovedBaseByEntityProductParameter(Entity.PRISMA, Product.TRADITIONAL, person.getDocumentType().getId(), person.getDocumentNumber(),
                    prismaPreApprovedBase.getMaxAmount() * 1.0, prismaPreApprovedBase.getMaxInstallments(), prismaPreApprovedBase.getTea(), null, null, null, null, null, "{" + EntityProductParams.ENT_PROD_PARAM_PRISMA_SOCIIOS_ACTUALES + "}");

            loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.PRISMA_BASE_PREAPROBADA.getKey(), new JSONObject(new Gson().toJson(prismaPreApprovedBase)));
            loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
        }
    }
}

package com.affirm.common.service;

import com.affirm.banbif.model.BanbifFileData;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public interface PrismaService {

    void updateBaseValuesInLoan(LoanApplication loanApplication, Person person) throws Exception;
}

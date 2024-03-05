package com.affirm.client.service.impl;

import com.affirm.client.dao.PersonCLDAO;
import com.affirm.client.model.form.LoanApplicationStep3Form;
import com.affirm.client.service.PersonCLService;
import com.affirm.common.model.catalog.ActivityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jrodriguez on 27/09/16.
 */

@Service
public class PersonCLServiceImpl implements PersonCLService {

    @Autowired
    private PersonCLDAO personClDao;

    @Override
    public void registerOcupationalInformation(int personId, LoanApplicationStep3Form form) throws Exception {
        // Update the principal ocupational information
        switch (form.getPrincipalActivityType()) {
            case ActivityType.DEPENDENT:
                personClDao.updateOcupationalInformation(personId, 1, form.getPrincipalActivityType(),
                        form.getPrincipalDependentRuc(), form.getPrincipalDependentCompany(), null, null,
                        form.getPrincipalDependentNetIncome(), form.getPrincipalDependentGrossIncome(), null, null,
                        null, form.getPrincipalDependentTime(), form.getPrincipalDependentPhoneNumber()
                );
                break;
            case ActivityType.INDEPENDENT:
                personClDao.updateOcupationalInformation(personId, 1, form.getPrincipalActivityType(),
                        form.getPrincipalIndependentRuc(), null, null, null, form.getPrincipalIndependentNetIncome(),
                        form.getPrincipalIndependentGrossIncome(), form.getPrincipalIndependentVouchers(), null, null,
                        form.getPrincipalIndependentTime(), null);
                break;
            case ActivityType.HOUSEKEEPER:
                personClDao.updateOcupationalInformation(personId, 1, form.getPrincipalActivityType(), null,
                        form.getPrincipalHousekeeperEmployer(), null, null, form.getPrincipalHousekeeperNetIncome(),
                        null, null, null, null, form.getPrincipalHousekeeperTime(), null);
                break;
            case ActivityType.RENTIER:
                personClDao.updateOcupationalInformation(personId, 1, form.getPrincipalActivityType(), null, null, null,
                        null, form.getPrincipalRentierNetIncome(), form.getPrincipalRentierGrossIncome(), null,
                        form.getPrincipalRentierBelonging(), null, form.getPrincipalRentierTime(), null);
                break;
            case ActivityType.STOCKHOLDER:
                personClDao.updateOcupationalInformation(personId, 1, form.getPrincipalActivityType(),
                        form.getPrincipalShareholderRuc(), form.getPrincipalShareholderCompany(),
                        form.getPrincipalShareholderShareholding(),
                        form.getPrincipalShareholderResultU12M() != null ? form.getPrincipalShareholderResultU12M() + "" : null,
                        form.getPrincipalShareholderNetIncome(), null, null, null, null, null, null);
                break;
            case ActivityType.PENSIONER:
                personClDao.updateOcupationalInformation(personId, 1, form.getPrincipalActivityType(), null, null, null,
                        null, form.getPrincipalPensionerNetIncome(), null, null, null, form.getPrincipalPensionerFrom(),
                        null, null);
                break;
            case ActivityType.STUDENT:
                personClDao.updateOcupationalInformation(personId, 1, form.getPrincipalActivityType(), null, null, null,
                        null, 0, 0, null, null, null, null, null);
                break;
            case ActivityType.UNEMPLOYED:
                personClDao.updateOcupationalInformation(personId, 1, form.getPrincipalActivityType(), null, null, null,
                        null, 0, 0, null, null, null, null, null);
                break;
        }

        // Update the secundary ocupational information
        if (form.getSecundaryActivityType() != null) {
            switch (form.getSecundaryActivityType()) {
                case ActivityType.DEPENDENT:
                    personClDao.updateOcupationalInformation(personId, 2, form.getSecundaryActivityType(),
                            form.getSecundaryDependentRuc(), form.getSecundaryDependentCompany(), null, null,
                            form.getSecundaryDependentNetIncome(), form.getSecundaryDependentGrossIncome(), null, null,
                            null, form.getSecundaryDependentTime(), form.getSecundaryDependentPhoneNumber());
                    break;
                case ActivityType.INDEPENDENT:
                    personClDao.updateOcupationalInformation(personId, 2, form.getSecundaryActivityType(),
                            form.getSecundaryIndependentRuc(), null, null, null, form.getSecundaryIndependentNetIncome(),
                            form.getSecundaryIndependentGrossIncome(), form.getSecundaryIndependentVouchers(), null,
                            null, form.getSecundaryIndependentTime(), null);
                    break;
                case ActivityType.HOUSEKEEPER:
                    personClDao.updateOcupationalInformation(personId, 2, form.getSecundaryActivityType(), null,
                            form.getSecundaryHousekeeperEmployer(), null, null, form.getSecundaryHousekeeperNetIncome(),
                            null, null, null, null, form.getSecundaryHousekeeperTime(), null);
                    break;
                case ActivityType.RENTIER:
                    personClDao.updateOcupationalInformation(personId, 2, form.getSecundaryActivityType(), null, null, null,
                            null, form.getSecundaryRentierNetIncome(), form.getSecundaryRentierGrossIncome(), null,
                            form.getSecundaryRentierBelonging(), null, form.getSecundaryRentierTime(), null);
                    break;
                case ActivityType.STOCKHOLDER:
                    personClDao.updateOcupationalInformation(personId, 2, form.getSecundaryActivityType(),
                            form.getSecundaryShareholderRuc(), form.getSecundaryShareholderCompany(),
                            form.getSecundaryShareholderShareholding(),
                            form.getSecundaryShareholderResultU12M() != null ? form.getSecundaryShareholderResultU12M() + "" : null,
                            form.getSecundaryShareholderNetIncome(), null, null, null, null, null, null);
                    break;
                case ActivityType.PENSIONER:
                    personClDao.updateOcupationalInformation(personId, 2, form.getSecundaryActivityType(), null, null, null,
                            null, form.getSecundaryPensionerNetIncome(), null, null, null, form.getSecundaryPensionerFrom(),
                            null, null);
                    break;
                case ActivityType.STUDENT:
                    personClDao.updateOcupationalInformation(personId, 2, form.getSecundaryActivityType(), null, null, null,
                            null, 0, 0, null, null, null, null, null);
                    break;
                case ActivityType.UNEMPLOYED:
                    personClDao.updateOcupationalInformation(personId, 2, form.getSecundaryActivityType(), null, null, null,
                            null, 0, 0, null, null, null, null, null);
                    break;
            }
        }

        // Update the other incomes
        if (form.getOtherIncomes() != null) {
            personClDao.updateOcupationalInformation(personId, 0, null, null, null, null, null, form.getOtherIncomes(),
                    null, null, null, null, null, null);
        }
    }

}

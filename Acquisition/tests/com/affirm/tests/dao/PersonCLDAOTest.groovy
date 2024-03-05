package com.affirm.tests.dao

import com.affirm.client.dao.PersonCLDAO
import com.affirm.client.model.form.LoanApplicationStep1Form
import com.affirm.client.model.form.LoanApplicationStep2Form
import com.affirm.tests.BaseAcquisitionConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class PersonCLDAOTest extends BaseAcquisitionConfig {

    @Autowired
    PersonCLDAO personCLDAO

    static final Integer PERSON_ID = 1531
    static final LoanApplicationStep1Form FORM = getLoanApplicationStep1Form()
    static final LoanApplicationStep2Form FORM2 = getLoanApplicationStep2Form()

    static LoanApplicationStep2Form getLoanApplicationStep2Form() {
        LoanApplicationStep2Form form2 = new LoanApplicationStep2Form()
        form2.setDepartment("14")
        form2.setProvince("01")
        form2.setDistrict("04")
        form2.setStreetType(null)
        form2.setStreetName("Jirón Recuay 355, Breña, Perú")
        form2.setStreetNumber("")
        form2.setInterior("103")
        form2.setDetail(null)

        return form2
    }

    static LoanApplicationStep1Form getLoanApplicationStep1Form() {
        LoanApplicationStep1Form form1 = new LoanApplicationStep1Form()
        form1.setName("Ada Bertha")
        form1.setFirstSurname("Arohuanca")
        form1.setLastSurname("Ramos")
        form1.setDocType(1)
        form1.setDocNumber("00441712")
        form1.setBirthday("23/11/1961")
        form1.setGender((Character) "F")
        form1.setCountryCode("51")
        form1.setPhoneNumber("987654888")
        form1.setEmail("ada@gmail.com")
        form1.setMaritalStatus(2)
        form1.setCompanionDocType(1)
        form1.setCompanionDocNumber("333333333")
        form1.setCompanionName("Companio Name")
        form1.setCompanionFirstSurname("Companion First Surname")
        form1.setCompanionLastSurname("Companion Last Surname")
        form1.setNationality(1)
        form1.setCityCode("01")
        form1.setLandline("01")
        form1.setPep(true)

        return form1
    }

    @Test
    void updatePersonalInformationFromPersonCLDAO() {
        personCLDAO.updatePersonalInformation(PERSON_ID, FORM)
    }

    @Test
    void updateAddressInformationFromPersonCLDAO() {
        personCLDAO.updateAddressInformation(PERSON_ID, FORM2)
    }
}

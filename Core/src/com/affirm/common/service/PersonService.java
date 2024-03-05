/**
 *
 */
package com.affirm.common.service;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.form.Question108Form;
import com.affirm.common.model.transactional.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author jrodriguez
 */
public interface PersonService {

    /**
     * Updates the names, sex and birthday of the person with the data in the Reniec DB
     *
     * @param personId
     * @param docNumber
     * @throws Exception
     */
    Reniec updatePersonDataFromReniecBD(int personId, String docNumber) throws Exception;

    boolean isPersonalInformationFilledUp(int personid, Locale locale) throws Exception;

    boolean isAddressInformationFilledUp(int personid, Locale locale) throws Exception;

    boolean isOcupationalInformationFilledUp(int personid, Locale locale) throws Exception;

    PersonOcupationalInformation getPrincipalOcupationalInormation(int personid, Locale locale) throws Exception;

    PersonOcupationalInformation getCurrentOcupationalInformation(int personid, Locale locale) throws Exception;

    PersonContactInformation getContactInformation(int personid) throws Exception;

    PersonOcupationalInformation getOcupationalInformation(int personid, int number, Locale locale) throws Exception;

    void updateOcupationalStartDate(int personid, int ocupationalNumber, Date startDate) throws Exception;

    public boolean isDisggregatedAddress(int personId) throws Exception;

    StaticDBInfo getIncome(Integer dni) throws  Exception;

    RucInfo getRucInfo(String ruc) throws Exception;

    String getPhoneInfoNosis(Integer personId) throws Exception;

    Integer getNosisCommitment(Integer personId) throws Exception;

    String getNosisNSE(Integer personId) throws Exception;

    List<IdentityDocumentType> getValidIdentityDocumentTypes(int countryId, Integer entityBrandingId) throws Exception;

    void asyncPersonPartnerUpdate(Question108Form form,Integer personId)throws Exception;

    List<PersonDisqualifier> getPersonDisqualifiersByPersonId(HttpServletRequest httpServletRequest, Integer personId) throws Exception;

    Map<String,PersonDisqualifier> getPersonDisqualifierMap(HttpServletRequest httpServletRequest, Integer loanAppId) throws Exception ;

    void savePersonDisqualifier(Integer loanAppId, MultipartFile file, String type, boolean isDisquilified, String detail) throws Exception ;

    String getRucPersonOccupationalInformation(Integer personId, Locale locale) throws Exception;

    void updateOcupationalRuc(String ruc, int personId, boolean cleanOcupatinalInfo, LoanApplication loanApplication) throws Exception;

    void updateOcupationalRuc(String ruc, int personId, boolean cleanOcupatinalInfo, String taxRegime, LoanApplication loanApplication) throws Exception;
}

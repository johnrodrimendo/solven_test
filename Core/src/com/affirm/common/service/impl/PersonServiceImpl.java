/**
 *
 */
package com.affirm.common.service.impl;


import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question108Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.Marshall;
import com.affirm.nosis.NosisResult;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author jrodriguez
 */
@Service("personService")
public class PersonServiceImpl implements PersonService {

    private static final Logger logger = Logger.getLogger(PersonServiceImpl.class);

    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private FileService fileService;
    @Autowired
    private WebscrapperService webscrapperService;

    @Override
    public Reniec updatePersonDataFromReniecBD(int personId, String docNumber) throws Exception {
        Reniec reniec = personDao.getReniecDBData(docNumber);
        if (reniec != null) {
            personDao.updateName(personId, reniec.getName());
            personDao.updateFirstSurname(personId, reniec.getFirstSurname());
            personDao.updateLastSurname(personId, reniec.getLastSurname());
            personDao.updateBirthday(personId, reniec.getBirthday());
            personDao.updateGender(personId, reniec.getGender());
        }
        return reniec;
    }

    @Override
    public boolean isPersonalInformationFilledUp(int personid, Locale locale) throws Exception {
        Person personalInformation = personDao.getPerson(catalogService, locale, personid, false);
        return personalInformation.getNationality() != null;
    }

    @Override
    public boolean isAddressInformationFilledUp(int personid, Locale locale) throws Exception {
        PersonContactInformation address = personDao.getPersonContactInformation(locale, personid);
        return address != null && address.getAddressStreetName() != null;
    }

    @Override
    public boolean isOcupationalInformationFilledUp(int personid, Locale locale) throws Exception {
        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, personid);
        return ocupations != null;
    }

    @Override
    public PersonOcupationalInformation getPrincipalOcupationalInormation(int personid, Locale locale) throws Exception {
        return getOcupationalInformation(personid, PersonOcupationalInformation.PRINCIPAL, locale);
    }

    @Override
    public PersonOcupationalInformation getCurrentOcupationalInformation(int personid, Locale locale) throws Exception {
        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, personid);
        if (ocupations != null) {
            return ocupations
                    .stream().max((o1, o2) -> Integer.compare(o1.getNumber(), o2.getNumber()))
                    .orElse(null);
        }
        return null;
    }

    @Override
    public PersonContactInformation getContactInformation(int personid) throws Exception {
        PersonContactInformation contact = personDao.getPersonContactInformation(Configuration.getDefaultLocale(), personid);
        if (contact != null) {
            return contact;
        }
        return null;
    }

    @Override
    public PersonOcupationalInformation getOcupationalInformation(int personid, int number, Locale locale) throws Exception {
        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, personid);
        if (ocupations != null) {
            return ocupations.stream().filter(o -> o.getNumber() == number).findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public void updateOcupationalStartDate(int personid, int ocupationalNumber, Date startDate) throws Exception {
        personDao.updateOcupatinalStartDate(personid, ocupationalNumber, startDate);

        LocalDate startDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        personDao.updateEmploymentTime(personid, ocupationalNumber, ChronoUnit.MONTHS.between(startDateTime, LocalDate.now()) + "");
    }

    @Override
    public boolean isDisggregatedAddress(int personId) throws Exception {
        int howMany = personDao.hasDisggregatedAddress(personId);
        List<PersonOcupationalInformation> personOcupationalInformations = personDao.getPersonOcupationalInformation(Configuration.getDefaultLocale(), personId);
        PersonOcupationalInformation personOcupationalInformation = personOcupationalInformations.stream().filter(e -> e.getNumber().equals(1)).findFirst().orElse(null);
        if (personOcupationalInformation.getActivityType().getId().equals(ActivityType.INDEPENDENT) && Arrays.asList(SubActivityType.PROFESSIONAL_SERVICE, SubActivityType.RENT, SubActivityType.SHAREHOLDER).contains(personOcupationalInformation.getSubActivityType().getId()) && howMany == 1)
            return true;
        else if (personOcupationalInformation.getActivityType().getId().equals(ActivityType.RENTIER) && howMany == 1)
            return true;
        else if (personOcupationalInformation.getActivityType().getId().equals(ActivityType.PENSIONER) && howMany == 1)
            return true;
        else if (personOcupationalInformation.getActivityType().getId().equals(ActivityType.STOCKHOLDER) && howMany == 1)
            return true;
        else if (personOcupationalInformation.getActivityType().getId().equals(ActivityType.HOUSEKEEPER) && howMany == 1)
            return true;
        else if (howMany == 2)
            return true;
        return false;
    }

    @Override
    public StaticDBInfo getIncome(Integer dni) throws Exception {
        StaticDBInfo staticDBInfo = personDao.getInfoFromStaticDB(dni);
        return staticDBInfo;
    }

    @Override
    public RucInfo getRucInfo(String ruc) throws Exception {
        RucInfo rucInfo = personDao.getRucInfo(ruc);
        return rucInfo;
    }

    @Override
    public String getPhoneInfoNosis(Integer personId) throws Exception {
        NosisResult nosisResult = personDao.getBureauResult(personId, NosisResult.class);
        Marshall marshall = new Marshall();
        if (nosisResult != null) {
            if (nosisResult.getParteXML() != null && nosisResult.getParteXML().getDatos().get(0).getDomAlternativos() != null) {
                if (nosisResult.getParteXML().getDatos().get(0).getDomAlternativos().getTels() != null) {
                    if (nosisResult.getParteXML().getDatos().get(0).getDomAlternativos().getTels().getTel() != null) {
                        return nosisResult.getParteXML().getDatos().get(0).getDomAlternativos().getTels().getTel().get(0);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Integer getNosisCommitment(Integer personId) throws Exception {
        NosisResult nosisResult = personDao.getBureauResult(personId, NosisResult.class);
        if (nosisResult != null) {
            return nosisResult.getCommitment();
        }
        return null;
    }

    @Override
    public String getNosisNSE(Integer personId) throws Exception {
        NosisResult nosisResult = personDao.getBureauResult(personId, NosisResult.class);
        if (nosisResult != null) {
            return nosisResult.getNSE();
        }
        return null;
    }

    @Override
    public List<IdentityDocumentType> getValidIdentityDocumentTypes(int countryId, Integer entityBrandingId) throws Exception {
        EntityBranding branding = entityBrandingId != null ? catalogService.getEntityBranding(entityBrandingId) : null;
        if (branding != null && branding.getBranded()) {
            if (branding.getEntity().getDocumentTypes() != null && !branding.getEntity().getDocumentTypes().isEmpty())
                return branding.getEntity().getDocumentTypes();
        }
        return catalogService.getIdentityDocumentTypeByCountry(countryId, countryId == CountryParam.COUNTRY_ARGENTINA);
    }

    @Override
    @Async
    public void asyncPersonPartnerUpdate(Question108Form form, Integer personId) throws Exception {
        updatePersonAuxDataByLoanApplication(form);
        JSONObject jsonPerson = personDao.searchPersonByNames(form.getPartnerName(), form.getPartnerFirstSurname(), form.getPartnerLastSurname(), form.getGender(), form.getPartnerMonth(), form.getPartnerDay());
        Integer partnerPersonId = personDao.createPerson(1, jsonPerson.getString("documento"), Configuration.getDefaultLocale()).getId();

        if (partnerPersonId != null){
            personDao.updatePartner(personId, partnerPersonId);
            webscrapperService.callRunSynthesized(jsonPerson.getString("documento"), null);
        }

    }

    @Override
    public List<PersonDisqualifier> getPersonDisqualifiersByPersonId(HttpServletRequest httpServletRequest, Integer personId) throws Exception {
        List<PersonDisqualifier> personDisqualifiers = personDao.getPersonDisqualifierByPersonId(personId);
        if (personDisqualifiers == null)
            return null;
        for (PersonDisqualifier personDisqualifier : personDisqualifiers) {
            String generatedFileUrl = fileService.generatePublicUserFileUrl(personDisqualifier.getUserFileId(), false);
            personDisqualifier.setFileUrl(generatedFileUrl);
        }
        return personDisqualifiers;
    }

    @Override
    public Map<String, PersonDisqualifier> getPersonDisqualifierMap(HttpServletRequest httpServletRequest, Integer personId) throws Exception {
        List<PersonDisqualifier> personDisqualifiers = getPersonDisqualifiersByPersonId(httpServletRequest, personId);
        Map<String, PersonDisqualifier> map = null;
        if (null != personDisqualifiers) {
            PersonDisqualifier pep = personDisqualifiers.stream().filter(p -> p.getType().equals("P")).findFirst().orElse(null);
            PersonDisqualifier ofac = personDisqualifiers.stream().filter(p -> p.getType().equals("O")).findFirst().orElse(null);
            map = new HashMap<>();
            map.put("pep", pep);
            map.put("ofac", ofac);
        }
        return map;
    }

    @Override
    public void savePersonDisqualifier(Integer loanAppId, MultipartFile file, String type, boolean isDisquilified, String detail) throws Exception {
        LoanApplication loanApplication = loanApplicationService.getLoanApplicationById(loanAppId);
        List<PersonDisqualifier> personDisqualifiers = personDao.getPersonDisqualifierByPersonId(loanApplication.getPersonId());

        if (file != null) {
            int index = file.getOriginalFilename().lastIndexOf(".") + 1;
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(index);

            String generatedFileName = fileService.writeUserFile(file.getBytes(), loanApplication.getUserId(), ("P".equals(type) ? "pep" : "ofac") + "." + extension);
            Integer fileId = userDAO.registerUserFile(loanApplication.getUserId(), loanApplication.getId(), UserFileType.PERSON_DISQUALIFIER, generatedFileName);

            if (personDisqualifiers == null || (personDisqualifiers.size() == 1 && !personDisqualifiers.get(0).getType().equals(type))) {
                personDao.savePersonDisqualifier(loanApplication.getPersonId(), type, isDisquilified, detail, fileId);
            } else {
                for (PersonDisqualifier personDisqualifier : personDisqualifiers) {
                    if (personDisqualifier.getType().equals(type)) {
                        personDao.updatePersonDisqualifier(loanApplication.getPersonId(), isDisquilified, detail, fileId, type);
                    }
                }
            }
        } else {
            for (PersonDisqualifier personDisqualifier : personDisqualifiers) {
                if (personDisqualifier.getType().equals(type)) {
                    personDao.updatePersonDisqualifierNoImage(loanApplication.getPersonId(), isDisquilified, detail, type);
                }
            }
        }
    }

    @Override
    public String getRucPersonOccupationalInformation(Integer personId, Locale locale) throws Exception{
        List<PersonOcupationalInformation> personOcupationalInformations =
                personDao.getPersonOcupationalInformation(Configuration.getDefaultLocale(), personId);

        if (personOcupationalInformations != null &&
                !personOcupationalInformations.isEmpty())
            return personOcupationalInformations.get(0).getRuc();

        return null;
    }

    private void updatePersonAuxDataByLoanApplication(Question108Form form) throws Exception{
        if (form.getLoanApplicationId() == null) return;
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(form.getLoanApplicationId(),Configuration.getDefaultLocale());
        if(loanApplication == null) return;
        if(loanApplication.getAuxData() == null) loanApplication.setAuxData(new LoanApplicationAuxData());
        loanApplication.getAuxData().setPartnetFirstSurname(form.getPartnerFirstSurname());
        loanApplication.getAuxData().setPartnetLastSurname(form.getPartnerLastSurname());
        loanApplication.getAuxData().setPartnetName(form.getPartnerName());
        loanApplication.getAuxData().setPartnerGender(form.getGender());
        loanApplicationDao.updateAuxData(loanApplication.getId(),loanApplication.getAuxData());
    }

    @Override
    public void updateOcupationalRuc(String ruc, int personId, boolean cleanOcupatinalInfo, LoanApplication loanApplication) throws Exception{
        updateOcupationalRuc(ruc, personId, cleanOcupatinalInfo, null, loanApplication);
    }

    @Override
    public void updateOcupationalRuc(String ruc, int personId, boolean cleanOcupatinalInfo, String taxRegime, LoanApplication loanApplication) throws Exception{
        if(cleanOcupatinalInfo){
            personDao.updateOcupatinalPhoneNumber(personId, null, PersonOcupationalInformation.PRINCIPAL, null);
            personDao.updateOcupationalAddress(personId,PersonOcupationalInformation.PRINCIPAL, null, null, null, null, null);
        }
        PersonOcupationalInformation ocupation = getCurrentOcupationalInformation(personId, Configuration.getDefaultLocale());
        if(taxRegime != null)
            personDao.updateOcupationalRuc(personId, ocupation.getNumber(), ruc, taxRegime);
        else
            personDao.updateOcupationalRuc(personId, ocupation.getNumber(), ruc);
        // Call to Universidad Peru scrapper to get the info of the ruc
        webscrapperService.callUniversidadPeruBot(loanApplication, ruc, userDAO.getUserIdByPersonId(personId));
    }
}
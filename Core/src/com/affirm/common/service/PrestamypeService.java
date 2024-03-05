package com.affirm.common.service;

import com.affirm.common.dao.EvaluationDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.Address;
import com.affirm.common.model.catalog.Ubigeo;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.model.transactional.User;
import com.affirm.system.configuration.Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("prestamypeService")
public class PrestamypeService {

    public static final List<String> UBIGEO_LIMA_TOP = Arrays.asList("150131", "150114", "150140", "150122", "150130", "150104");
    public static final List<String> UBIGEO_LIMA_MODERNA = Arrays.asList("150113", "150116", "150120", "150141", "150121", "150136");
    public static final List<String> UBIGEO_LIMA_CENTRO = Arrays.asList("150101", "150105", "150115", "150128", "150134");
    public static final List<String> UBIGEO_LIMA_ESTE = Arrays.asList("150132", "150137", "150103", "150107", "150118", "150109", "150123");
    public static final List<String> UBIGEO_LIMA_NORTE = Arrays.asList("150117", "150106", "150135");
    public static final List<String> UBIGEO_LIMA_SUR = Arrays.asList("150133", "150119", "150142", "150108", "150129", "150126", "150127", "150502", "150138");
    public static final List<String> UBIGEO_CALLAO = Arrays.asList("070101", "070102", "070104", "070103");
    public static final List<String> UBIGEO_AREQUIPA = Arrays.asList("040104", "040112", "040103", "040126", "040110", "040129", "040101");

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private EvaluationDAO evaluationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private UtilService utilService;

    public boolean isApproved(LoanApplication loanApplication) throws Exception {
        if (loanApplication.getEntityCustomData() != null && loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.PRESTAMYPE_DATA.getKey())) {
            JSONObject data = loanApplication.getEntityCustomData().getJSONObject(LoanApplication.EntityCustomDataKeys.PRESTAMYPE_DATA.getKey());
            Boolean propertyGuarantee = data.getBoolean("propertyGuarantee");
            Boolean propertySunarp = data.getBoolean("propertySunarp");
            Boolean propertyNearHill = data.getBoolean("propertyNearHill");
            Boolean propertyHasSidewalk = data.getBoolean("propertyHasSidewalk");
            if (propertyGuarantee && propertySunarp && !propertyNearHill && propertyHasSidewalk) {
                return isApprovedGeolocation(loanApplication.getPersonId());
            }
        }
        return false;
    }

    private boolean isApprovedGeolocation(int personId) throws Exception {
        Address address = evaluationDao.getAddressByPersonId(personId);
        if (address != null && address.getUbigeoId() != null) {
            Ubigeo ubigeo = catalogService.getUbigeo(address.getUbigeoId());
            String ubigeoIneiId = ubigeo.getIneiUbigeoId();
            List<String> allUbigeos = new ArrayList<>();
            allUbigeos.addAll(UBIGEO_LIMA_TOP);
            allUbigeos.addAll(UBIGEO_LIMA_MODERNA);
            allUbigeos.addAll(UBIGEO_LIMA_CENTRO);
            allUbigeos.addAll(UBIGEO_LIMA_ESTE);
            allUbigeos.addAll(UBIGEO_LIMA_NORTE);
            allUbigeos.addAll(UBIGEO_LIMA_SUR);
            allUbigeos.addAll(UBIGEO_CALLAO);
            allUbigeos.addAll(UBIGEO_AREQUIPA);
            return allUbigeos.contains(ubigeoIneiId);
        }
        return false;
    }

    public void sendLeadEmail(LoanApplication loanApplication) throws Exception{
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        User user = userDao.getUser(loanApplication.getUserId());
        PersonContactInformation personContactInfo = personDao.getPersonContactInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
        String address = personContactInfo.getAddressStreetName()+ ", " + personContactInfo.getAddressUbigeo().getDepartment().getName() +
                ", " + personContactInfo.getAddressUbigeo().getProvince().getName() +
                ", " + personContactInfo.getAddressUbigeo().getDistrict().getName();

        String body = "";
        body += "Hola, <br>Se generó el siguiente Lead desde la plataforma solven:<br><br>" +
                "Fecha de ingreso: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(loanApplication.getRegisterDate()) + "<br>" +
                "Tipo de Documento: " + person.getDocumentType().getName()+ "<br>" +
                "Nro. de documento: " + person.getDocumentNumber()+ "<br>" +
                "Nombres y apellidos: " + person.getFullName()+ "<br>" +
                "Tel&eacute;fono: " + user.getPhoneNumber()+ "<br>" +
                "Email: " + user.getEmail()+ "<br>" +
                "Fecha de nacimiento: " + (person.getBirthday() != null ? new SimpleDateFormat("dd/MM/yyyy").format(person.getBirthday()) : "") + "<br>" +
                "Monto: " + utilService.integerMoneyFormat(loanApplication.getAmount() < 15000 ? 15000 : loanApplication.getAmount()) + "<br>"+
                "Dirección: " + address;

        awsSesEmailService.sendRawEmail(
                null,
                "procesos@solven.pe",
                null,
                "comercial@prestamype.com",
                null,
                "Nuevo Lead Solven",
                body,
                body,
                null,
                null, null, null, null);
    }
}

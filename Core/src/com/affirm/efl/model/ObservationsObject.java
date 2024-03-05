package com.affirm.efl.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.transactional.EntityConsolidableDebt;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.model.transactional.PersonOcupationalInformation;

import java.util.List;

/**
 * Created by dev5 on 04/07/17.
 */
public class ObservationsObject {

    private Observation prim_phon_num;
    private Observation isCellPhone;
    private Observation cellPhone;
    private Observation resident_stat;
    private Observation edu;
    private Observation bnk_relt_oth;
    private Observation business_age_cns;
    private ObservationList what_prod_have;

    public ObservationsObject(int entityId, TranslatorDAO translatorDAO, Person person, PersonContactInformation contactInfo, PersonOcupationalInformation ocupationalInfo, List<EntityConsolidableDebt> products) throws Exception{
        //this.setPrim_phon_num(new Observation(contactInfo.getPhoneNumber()));
        //this.setIsCellPhone(new Observation("1", "Si"));
        //this.setCellPhone(new Observation(contactInfo.getPhoneNumber()));
        this.setResident_stat(new Observation(translatorDAO.translate(entityId, 17, contactInfo.getHousingType().getId().toString(), null), contactInfo.getHousingType().getType()));
        this.setEdu(new Observation(translatorDAO.translate(entityId, 18, person.getStudyLevel().getId().toString(), null), person.getStudyLevel().getLevel()));
        this.setBnk_relt_oth(new Observation("1", "Yes"));
        if(ocupationalInfo != null){
            Integer months = Integer.valueOf(ocupationalInfo.getEmploymentTime());
            if(months >= 24){
                String years = String.valueOf(Math.floor(months / 12));
                this.setBusiness_age_cns(new Observation(years, years + " años"));
            }
        }
        ObservationList productsList = new ObservationList(entityId, translatorDAO, products);
        this.setWhat_prod_have(productsList);
    }

    public Observation getPrim_phon_num() {
        return prim_phon_num;
    }

    public void setPrim_phon_num(Observation prim_phon_num) {
        this.prim_phon_num = prim_phon_num;
    }

    public Observation getIsCellPhone() {
        return isCellPhone;
    }

    public void setIsCellPhone(Observation isCellPhone) {
        this.isCellPhone = isCellPhone;
    }

    public Observation getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(Observation cellPhone) {
        this.cellPhone = cellPhone;
    }

    public Observation getResident_stat() {
        return resident_stat;
    }

    public void setResident_stat(Observation resident_stat) {
        this.resident_stat = resident_stat;
    }

    public Observation getEdu() {
        return edu;
    }

    public void setEdu(Observation edu) {
        this.edu = edu;
    }

    public Observation getBnk_relt_oth() {
        return bnk_relt_oth;
    }

    public void setBnk_relt_oth(Observation bnk_relt_oth) {
        this.bnk_relt_oth = bnk_relt_oth;
    }

    public Observation getBusiness_age_cns() {
        return business_age_cns;
    }

    public void setBusiness_age_cns(Observation business_age_cns) {
        this.business_age_cns = business_age_cns;
    }

    public ObservationList getWhat_prod_have() {
        return what_prod_have;
    }

    public void setWhat_prod_have(ObservationList what_prod_have) {
        this.what_prod_have = what_prod_have;
    }
}

package com.affirm.efl.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.transactional.*;

import java.util.List;

/**
 * Created by dev5 on 04/07/17.
 */
public class Data {

    private ApplicantObject applicant;
    private ObservationsObject observations;
    private String application;

    public Data(String application, int entityId, TranslatorDAO translatorDAO, int loanApplicationId, Person person, User user, PersonContactInformation contactInfo, PersonOcupationalInformation ocupationalInfo, List<EntityConsolidableDebt> products) throws Exception{
        this.setApplicant(new ApplicantObject(entityId, translatorDAO, loanApplicationId, person, user, contactInfo, ocupationalInfo));
        this.setObservations(new ObservationsObject(entityId, translatorDAO, person, contactInfo, ocupationalInfo, products));
        setApplication(application);
    }

    public ApplicantObject getApplicant() {
        return applicant;
    }

    public void setApplicant(ApplicantObject applicant) {
        this.applicant = applicant;
    }

    public ObservationsObject getObservations() {
        return observations;
    }

    public void setObservations(ObservationsObject observations) {
        this.observations = observations;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }
}

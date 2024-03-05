package com.affirm.entityExt.models;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.TrackingAction;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.model.transactional.Referral;
import com.affirm.common.model.transactional.UserFile;

import java.util.List;

public class PhoneVerificationLoanExtranetPainter {

    private Integer loanId;
    private String name;
    private String phone;
    private String documentNumber;
    private IdentityDocumentType documentType;
    private List<ReferralPainterExtranet> referrals;
    private TrackingAction action;
    private Integer countryId;
    private UserFile userFile;

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(IdentityDocumentType documentType) {
        this.documentType = documentType;
    }

    public TrackingAction getAction() {
        return action;
    }

    public void setAction(TrackingAction action) {
        this.action = action;
    }

    public List<ReferralPainterExtranet> getReferrals() {
        return referrals;
    }

    public void setReferrals(List<ReferralPainterExtranet> referrals) {
        this.referrals = referrals;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public UserFile getUserFile() {
        return userFile;
    }

    public void setUserFile(UserFile userFile) {
        this.userFile = userFile;
    }
}

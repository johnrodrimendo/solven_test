/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.model.transactional.PersonLinkedIn;
import com.affirm.common.model.transactional.UserFacebook;
import com.affirm.common.util.Util;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationStep4Form implements Serializable {

    private String linkedinId;
    private String linkedinFirstName;
    private String linkedinLastName;
    private String linkedinMaidenName;
    private String linkedinFormattedName;
    private String linkedinPhoneticFirstName;
    private String linkedinPhoneticLastName;
    private String linkedinFormattedPhoneticName;
    private String linkedinHeadline;
    private String linkedinLocation;
    private String linkedinIndustry;
    private Integer linkedinNumConnections;
    private Boolean linkedinNumConnectionsCapped;
    private String linkedinSummary;
    private String linkedinSpecialties;
    private String linkedinPositions;
    private String linkedinPictureUrl;
    private String linkedinPictureUrlsOriginal;
    private String linkedinPublicProfileUrl;
    private String linkedinEmailAddress;
    private String facebookId;
    private String facebookEmail;
    private String facebookName;
    private String facebookFirstName;
    private String facebookLastName;
    private Integer facebookAgeMax;
    private Integer facebookAgeMin;
    private String facebookLink;
    private String facebookGender;
    private String facebookLocale;
    private String facebookPicture;
    private Integer facebookTimeZone;
    private String facebookUpdatedTime;
    private String facebookVerified;
    private String facebookBirthday;
    private String facebookLocation;

    public LoanApplicationStep4Form() {
    }

//	public LoanApplicationStep4Form(HttpServletRequest request) {
//		if (request.getParameter("linkedinId") != null)
//			linkedinId = Integer.parseInt(request.getParameter("linkedinId"));
//		linkedinFirstName = request.getParameter("linkedinFirstName");
//		linkedinLastName = request.getParameter("linkedinLastName");
//		linkedinMaidenName = request.getParameter("linkedinMaidenName");
//		linkedinFormattedName = request.getParameter("linkedinFormattedName");
//		linkedinPhoneticFirstName = request.getParameter("linkedinPhoneticFirstName");
//		linkedinPhoneticLastName = request.getParameter("linkedinPhoneticLastName");
//		linkedinHeadline = request.getParameter("linkedinHeadline");
//		linkedinLocation = request.getParameter("linkedinLocation");
//		linkedinIndustry = request.getParameter("linkedinIndustry");
//		if (request.getParameter("linkedinNumConnections") != null)
//			linkedinNumConnections = Integer.parseInt(request.getParameter("linkedinNumConnections"));
//		if (request.getParameter("linkedinNumConnectionsCapped") != null)
//			linkedinNumConnectionsCapped = Boolean.parseBoolean(request.getParameter("linkedinNumConnectionsCapped"));
//		linkedinSummary = request.getParameter("linkedinSummary");
//		linkedinSpecialties = request.getParameter("linkedinSpecialties");
//		linkedinPositions = request.getParameter("linkedinPositions");
//		linkedinPictureUrl = request.getParameter("linkedinPictureUrl");
//		linkedinPictureUrlsOriginal = request.getParameter("linkedinPictureUrlsOriginal");
//		linkedinPublicProfileUrl = request.getParameter("linkedinPublicProfileUrl");
//		linkedinEmailAddress = request.getParameter("linkedinEmailAddress");
//	}

    public PersonLinkedIn getUserLinkedin() {
        if (linkedinId != null) {
            PersonLinkedIn linkedin = new PersonLinkedIn();
            linkedin.setLinkedinId(linkedinId);
            linkedin.setLinkedinFirstName(linkedinFirstName);
            linkedin.setLinkedinLastName(linkedinLastName);
            linkedin.setLinkedinMaidenName(linkedinMaidenName);
            linkedin.setLinkedinFormattedName(linkedinFormattedName);
            linkedin.setLinkedinPhoneticFirstName(linkedinPhoneticFirstName);
            linkedin.setLinkedinPhoneticLastName(linkedinPhoneticLastName);
            linkedin.setLinkedinFormattedPhoneticName(linkedinFormattedPhoneticName);
            linkedin.setLinkedinHeadline(linkedinHeadline);
            linkedin.setLinkedinLocation(linkedinLocation != null ? new JSONObject(linkedinLocation) : null);
            linkedin.setLinkedinIndustry(linkedinIndustry);
            linkedin.setLinkedinNumConnections(linkedinNumConnections);
            linkedin.setLinkedinNumConnectionsCapped(linkedinNumConnectionsCapped);
            linkedin.setLinkedinSummary(linkedinSummary);
            linkedin.setLinkedinSpecialties(linkedinSpecialties);
            linkedin.setLinkedinPositions(linkedinPositions != null ? new JSONObject(linkedinPositions) : null);
            linkedin.setLinkedinPictureUrl(linkedinPictureUrl);
            linkedin.setLinkedinPictureUrlsOriginal(linkedinPictureUrlsOriginal);
            linkedin.setLinkedinPublicProfileUrl(linkedinPublicProfileUrl);
            linkedin.setLinkedinEmailAddress(linkedinEmailAddress);
            return linkedin;
        }
        return null;
    }

    public UserFacebook getUserFacebook() {
        if (facebookId != null) {
            UserFacebook facebook = new UserFacebook();
            facebook.setFacebookId(facebookId);
            facebook.setFacebookEmail(facebookEmail);
            facebook.setFacebookName(facebookName);
            facebook.setFacebookFirstName(facebookFirstName);
            facebook.setFacebookLastName(facebookLastName);
            facebook.setFacebookAgeMax(facebookAgeMax);
            facebook.setFacebookAgeMin(facebookAgeMin);
            facebook.setFacebookLink(facebookLink);
            facebook.setFacebookGender(facebookGender);
            facebook.setFacebookLocale(facebookLocale);
            facebook.setFacebookPicture(facebookPicture);
            facebook.setFacebookTimeZone(facebookTimeZone);
            if (facebookUpdatedTime != null) {
                try {
                    facebook.setFacebookUpdatedTime(Util.FACEBOOK_DATE_FORMATTER.parse(facebookUpdatedTime));
                } catch (Exception ex) {
                }
            }
            facebook.setFacebookVerified(facebookVerified);
            facebook.setFacebookBirthday(facebookBirthday);
            facebook.setFacebookLocation(facebookLocation);
            return facebook;
        }
        return null;
    }

    public String getLinkedinId() {
        return linkedinId;
    }

    public void setLinkedinId(String linkedinId) {
        this.linkedinId = linkedinId;
    }

    public String getLinkedinFirstName() {
        return linkedinFirstName;
    }

    public void setLinkedinFirstName(String linkedinFirstName) {
        this.linkedinFirstName = linkedinFirstName;
    }

    public String getLinkedinLastName() {
        return linkedinLastName;
    }

    public void setLinkedinLastName(String linkedinLastName) {
        this.linkedinLastName = linkedinLastName;
    }

    public String getLinkedinMaidenName() {
        return linkedinMaidenName;
    }

    public void setLinkedinMaidenName(String linkedinMaidenName) {
        this.linkedinMaidenName = linkedinMaidenName;
    }

    public String getLinkedinFormattedName() {
        return linkedinFormattedName;
    }

    public void setLinkedinFormattedName(String linkedinFormattedName) {
        this.linkedinFormattedName = linkedinFormattedName;
    }

    public String getLinkedinPhoneticFirstName() {
        return linkedinPhoneticFirstName;
    }

    public void setLinkedinPhoneticFirstName(String linkedinPhoneticFirstName) {
        this.linkedinPhoneticFirstName = linkedinPhoneticFirstName;
    }

    public String getLinkedinPhoneticLastName() {
        return linkedinPhoneticLastName;
    }

    public void setLinkedinPhoneticLastName(String linkedinPhoneticLastName) {
        this.linkedinPhoneticLastName = linkedinPhoneticLastName;
    }

    public String getLinkedinFormattedPhoneticName() {
        return linkedinFormattedPhoneticName;
    }

    public void setLinkedinFormattedPhoneticName(String linkedinFormattedPhoneticName) {
        this.linkedinFormattedPhoneticName = linkedinFormattedPhoneticName;
    }

    public String getLinkedinHeadline() {
        return linkedinHeadline;
    }

    public void setLinkedinHeadline(String linkedinHeadline) {
        this.linkedinHeadline = linkedinHeadline;
    }

    public String getLinkedinLocation() {
        return linkedinLocation;
    }

    public void setLinkedinLocation(String linkedinLocation) {
        this.linkedinLocation = linkedinLocation;
    }

    public String getLinkedinIndustry() {
        return linkedinIndustry;
    }

    public void setLinkedinIndustry(String linkedinIndustry) {
        this.linkedinIndustry = linkedinIndustry;
    }

    public Integer getLinkedinNumConnections() {
        return linkedinNumConnections;
    }

    public void setLinkedinNumConnections(Integer linkedinNumConnections) {
        this.linkedinNumConnections = linkedinNumConnections;
    }

    public Boolean getLinkedinNumConnectionsCapped() {
        return linkedinNumConnectionsCapped;
    }

    public void setLinkedinNumConnectionsCapped(Boolean linkedinNumConnectionsCapped) {
        this.linkedinNumConnectionsCapped = linkedinNumConnectionsCapped;
    }

    public String getLinkedinSummary() {
        return linkedinSummary;
    }

    public void setLinkedinSummary(String linkedinSummary) {
        this.linkedinSummary = linkedinSummary;
    }

    public String getLinkedinSpecialties() {
        return linkedinSpecialties;
    }

    public void setLinkedinSpecialties(String linkedinSpecialties) {
        this.linkedinSpecialties = linkedinSpecialties;
    }

    public String getLinkedinPositions() {
        return linkedinPositions;
    }

    public void setLinkedinPositions(String linkedinPositions) {
        this.linkedinPositions = linkedinPositions;
    }

    public String getLinkedinPictureUrl() {
        return linkedinPictureUrl;
    }

    public void setLinkedinPictureUrl(String linkedinPictureUrl) {
        this.linkedinPictureUrl = linkedinPictureUrl;
    }

    public String getLinkedinPictureUrlsOriginal() {
        return linkedinPictureUrlsOriginal;
    }

    public void setLinkedinPictureUrlsOriginal(String linkedinPictureUrlsOriginal) {
        this.linkedinPictureUrlsOriginal = linkedinPictureUrlsOriginal;
    }

    public String getLinkedinPublicProfileUrl() {
        return linkedinPublicProfileUrl;
    }

    public void setLinkedinPublicProfileUrl(String linkedinPublicProfileUrl) {
        this.linkedinPublicProfileUrl = linkedinPublicProfileUrl;
    }

    public String getLinkedinEmailAddress() {
        return linkedinEmailAddress;
    }

    public void setLinkedinEmailAddress(String linkedinEmailAddress) {
        this.linkedinEmailAddress = linkedinEmailAddress;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookEmail() {
        return facebookEmail;
    }

    public void setFacebookEmail(String facebookEmail) {
        this.facebookEmail = facebookEmail;
    }

    public String getFacebookName() {
        return facebookName;
    }

    public void setFacebookName(String facebookName) {
        this.facebookName = facebookName;
    }

    public String getFacebookFirstName() {
        return facebookFirstName;
    }

    public void setFacebookFirstName(String facebookFirstName) {
        this.facebookFirstName = facebookFirstName;
    }

    public String getFacebookLastName() {
        return facebookLastName;
    }

    public void setFacebookLastName(String facebookLastName) {
        this.facebookLastName = facebookLastName;
    }

    public Integer getFacebookAgeMax() {
        return facebookAgeMax;
    }

    public void setFacebookAgeMax(Integer facebookAgeMax) {
        this.facebookAgeMax = facebookAgeMax;
    }

    public Integer getFacebookAgeMin() {
        return facebookAgeMin;
    }

    public void setFacebookAgeMin(Integer facebookAgeMin) {
        this.facebookAgeMin = facebookAgeMin;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getFacebookGender() {
        return facebookGender;
    }

    public void setFacebookGender(String facebookGender) {
        this.facebookGender = facebookGender;
    }

    public String getFacebookLocale() {
        return facebookLocale;
    }

    public void setFacebookLocale(String facebookLocale) {
        this.facebookLocale = facebookLocale;
    }

    public String getFacebookPicture() {
        return facebookPicture;
    }

    public void setFacebookPicture(String facebookPicture) {
        this.facebookPicture = facebookPicture;
    }

    public Integer getFacebookTimeZone() {
        return facebookTimeZone;
    }

    public void setFacebookTimeZone(Integer facebookTimeZone) {
        this.facebookTimeZone = facebookTimeZone;
    }

    public String getFacebookUpdatedTime() {
        return facebookUpdatedTime;
    }

    public void setFacebookUpdatedTime(String facebookUpdatedTime) {
        this.facebookUpdatedTime = facebookUpdatedTime;
    }

    public String getFacebookVerified() {
        return facebookVerified;
    }

    public void setFacebookVerified(String facebookVerified) {
        this.facebookVerified = facebookVerified;
    }

    public String getFacebookBirthday() {
        return facebookBirthday;
    }

    public void setFacebookBirthday(String facebookBirthday) {
        this.facebookBirthday = facebookBirthday;
    }

    public String getFacebookLocation() {
        return facebookLocation;
    }

    public void setFacebookLocation(String facebookLocation) {
        this.facebookLocation = facebookLocation;
    }

    @Override
    public String toString() {
        return "LoanApplicationStep4Form{" +
                "linkedinId='" + linkedinId + '\'' +
                ", linkedinFirstName='" + linkedinFirstName + '\'' +
                ", linkedinLastName='" + linkedinLastName + '\'' +
                ", linkedinMaidenName='" + linkedinMaidenName + '\'' +
                ", linkedinFormattedName='" + linkedinFormattedName + '\'' +
                ", linkedinPhoneticFirstName='" + linkedinPhoneticFirstName + '\'' +
                ", linkedinPhoneticLastName='" + linkedinPhoneticLastName + '\'' +
                ", linkedinFormattedPhoneticName='" + linkedinFormattedPhoneticName + '\'' +
                ", linkedinHeadline='" + linkedinHeadline + '\'' +
                ", linkedinLocation='" + linkedinLocation + '\'' +
                ", linkedinIndustry='" + linkedinIndustry + '\'' +
                ", linkedinNumConnections=" + linkedinNumConnections +
                ", linkedinNumConnectionsCapped=" + linkedinNumConnectionsCapped +
                ", linkedinSummary='" + linkedinSummary + '\'' +
                ", linkedinSpecialties='" + linkedinSpecialties + '\'' +
                ", linkedinPositions='" + linkedinPositions + '\'' +
                ", linkedinPictureUrl='" + linkedinPictureUrl + '\'' +
                ", linkedinPictureUrlsOriginal='" + linkedinPictureUrlsOriginal + '\'' +
                ", linkedinPublicProfileUrl='" + linkedinPublicProfileUrl + '\'' +
                ", linkedinEmailAddress='" + linkedinEmailAddress + '\'' +
                ", facebookId='" + facebookId + '\'' +
                ", facebookEmail='" + facebookEmail + '\'' +
                ", facebookName='" + facebookName + '\'' +
                ", facebookFirstName='" + facebookFirstName + '\'' +
                ", facebookLastName='" + facebookLastName + '\'' +
                ", facebookAgeMax=" + facebookAgeMax +
                ", facebookAgeMin=" + facebookAgeMin +
                ", facebookLink='" + facebookLink + '\'' +
                ", facebookGender='" + facebookGender + '\'' +
                ", facebookLocale='" + facebookLocale + '\'' +
                ", facebookPicture='" + facebookPicture + '\'' +
                ", facebookTimeZone=" + facebookTimeZone +
                ", facebookUpdatedTime='" + facebookUpdatedTime + '\'' +
                ", facebookVerified='" + facebookVerified + '\'' +
                ", facebookBirthday='" + facebookBirthday + '\'' +
                ", facebookLocation='" + facebookLocation + '\'' +
                '}';
    }
}

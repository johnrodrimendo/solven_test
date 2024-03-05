/**
 *
 */
package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jrodriguez
 */
public class PersonLinkedIn implements Serializable {

    private String linkedinId;
    private String linkedinFirstName;
    private String linkedinLastName;
    private String linkedinMaidenName;
    private String linkedinFormattedName;
    private String linkedinPhoneticFirstName;
    private String linkedinPhoneticLastName;
    private String linkedinFormattedPhoneticName;
    private String linkedinHeadline;
    private JSONObject linkedinLocation;
    private String linkedinIndustry;
    private Integer linkedinNumConnections;
    private Boolean linkedinNumConnectionsCapped;
    private String linkedinSummary;
    private String linkedinSpecialties;
    private JSONObject linkedinPositions;
    private String linkedinPictureUrl;
    private String linkedinPictureUrlsOriginal;
    private String linkedinPublicProfileUrl;
    private String linkedinEmailAddress;
    private Date linkedinLastModified;
    private String linkedinProposalComments;
    private String linkedinAssociations;
    private String linkedinInterests;
    private JSONObject linkedinLanguages;
    private JSONObject linkedinSkills;
    private JSONObject linkedinCertifications;
    private JSONObject linkedinEducations;
    private JSONObject linkedinCourses;
    private JSONObject linkedinVolunteer;
    private JSONObject linkedinThreeCurrentPositions;
    private JSONObject linkedinThreePastPositions;
    private Integer linkedinNumRecomenders;
    private JSONObject linkedinRecommendationsRecived;
    private JSONObject linkedinSuggestions;
    private String linkedinDateOfBirth;
    private JSONObject linkedinHonorsAwards;

    public void fillFromDb(JSONObject json) throws Exception {
        setLinkedinId(JsonUtil.getStringFromJson(json, "linkedin_id", null));
        setLinkedinFirstName(JsonUtil.getStringFromJson(json, "first_name", null));
        setLinkedinLastName(JsonUtil.getStringFromJson(json, "last_name", null));
        setLinkedinMaidenName(JsonUtil.getStringFromJson(json, "maiden_name", null));
        setLinkedinFormattedName(JsonUtil.getStringFromJson(json, "formatted_name", null));
        setLinkedinPhoneticFirstName(JsonUtil.getStringFromJson(json, "phonetic_first_name", null));
        setLinkedinPhoneticLastName(JsonUtil.getStringFromJson(json, "phonetic_last_name", null));
        setLinkedinFormattedPhoneticName(JsonUtil.getStringFromJson(json, "formatted_phonetic_name", null));
        setLinkedinHeadline(JsonUtil.getStringFromJson(json, "headline", null));
        setLinkedinLocation(JsonUtil.getJsonObjectFromJson(json, "location", null));
        setLinkedinIndustry(JsonUtil.getStringFromJson(json, "industry", null));
        setLinkedinNumConnections(JsonUtil.getIntFromJson(json, "num_connections", null));
        setLinkedinNumConnectionsCapped(JsonUtil.getBooleanFromJson(json, "num_connections_capped", null));
        setLinkedinSummary(JsonUtil.getStringFromJson(json, "summary", null));
        setLinkedinSpecialties(JsonUtil.getStringFromJson(json, "specialties", null));
        setLinkedinPositions(JsonUtil.getJsonObjectFromJson(json, "positions", null));
        setLinkedinPictureUrl(JsonUtil.getStringFromJson(json, "picture_url", null));
        setLinkedinPictureUrlsOriginal(JsonUtil.getStringFromJson(json, "picture_urls_original", null));
        setLinkedinPublicProfileUrl(JsonUtil.getStringFromJson(json, "public_profile_url", null));
        setLinkedinEmailAddress(JsonUtil.getStringFromJson(json, "email_address", null));
        setLinkedinLastModified(JsonUtil.getPostgresDateFromJson(json, "last_modified_timestamp", null));
        setLinkedinProposalComments(JsonUtil.getStringFromJson(json, "proposal_comments", null));
        setLinkedinAssociations(JsonUtil.getStringFromJson(json, "associations", null));
        setLinkedinInterests(JsonUtil.getStringFromJson(json, "interests", null));
        setLinkedinLanguages(JsonUtil.getJsonObjectFromJson(json, "languages", null));
        setLinkedinSkills(JsonUtil.getJsonObjectFromJson(json, "skills", null));
        setLinkedinCertifications(JsonUtil.getJsonObjectFromJson(json, "certifications", null));
        setLinkedinEducations(JsonUtil.getJsonObjectFromJson(json, "educations", null));
        setLinkedinCourses(JsonUtil.getJsonObjectFromJson(json, "courses", null));
        setLinkedinVolunteer(JsonUtil.getJsonObjectFromJson(json, "volunteer", null));
        setLinkedinThreeCurrentPositions(JsonUtil.getJsonObjectFromJson(json, "three_current_positions", null));
        setLinkedinThreePastPositions(JsonUtil.getJsonObjectFromJson(json, "three_past_positions", null));
        setLinkedinNumRecomenders(JsonUtil.getIntFromJson(json, "num_recommenders", null));
        setLinkedinRecommendationsRecived(JsonUtil.getJsonObjectFromJson(json, "recommendations_received", null));
        setLinkedinSuggestions(JsonUtil.getJsonObjectFromJson(json, "suggestions", null));
        setLinkedinDateOfBirth(JsonUtil.getStringFromJson(json, "date_of_birth", null));
        setLinkedinHonorsAwards(JsonUtil.getJsonObjectFromJson(json, "honors_awards", null));
    }

    public void fillFromApi(JSONObject json) throws Exception {
        setLinkedinId(JsonUtil.getStringFromJson(json, "id", null));
        setLinkedinFirstName(JsonUtil.getStringFromJson(json, "firstName", null));
        setLinkedinLastName(JsonUtil.getStringFromJson(json, "lastName", null));
        setLinkedinMaidenName(JsonUtil.getStringFromJson(json, "maidenName", null));
        setLinkedinFormattedName(JsonUtil.getStringFromJson(json, "formattedName", null));
        setLinkedinPhoneticFirstName(JsonUtil.getStringFromJson(json, "phoneticFirstName", null));
        setLinkedinPhoneticLastName(JsonUtil.getStringFromJson(json, "phoneticLastName", null));
        setLinkedinFormattedPhoneticName(JsonUtil.getStringFromJson(json, "formattedPhoneticName", null));
        setLinkedinHeadline(JsonUtil.getStringFromJson(json, "headline", null));
        setLinkedinLocation(JsonUtil.getJsonObjectFromJson(json, "location", null));
        setLinkedinIndustry(JsonUtil.getStringFromJson(json, "industry", null));
        setLinkedinNumConnections(JsonUtil.getIntFromJson(json, "numConnections", null));
        setLinkedinNumConnectionsCapped(JsonUtil.getBooleanFromJson(json, "numConnectionsCapped", null));
        setLinkedinSummary(JsonUtil.getStringFromJson(json, "summary", null));
        setLinkedinSpecialties(JsonUtil.getStringFromJson(json, "specialties", null));
        setLinkedinPositions(JsonUtil.getJsonObjectFromJson(json, "positions", null));
        setLinkedinPictureUrl(JsonUtil.getStringFromJson(json, "pictureUrl", null));
        setLinkedinPictureUrlsOriginal(JsonUtil.getStringFromJson(json, "pictureUrls", null));
        setLinkedinPublicProfileUrl(JsonUtil.getStringFromJson(json, "publicProfileUrl", null));
        setLinkedinEmailAddress(JsonUtil.getStringFromJson(json, "emailAddress", null));
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

    public JSONObject getLinkedinLocation() {
        return linkedinLocation;
    }

    public void setLinkedinLocation(JSONObject linkedinLocation) {
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

    public JSONObject getLinkedinPositions() {
        return linkedinPositions;
    }

    public void setLinkedinPositions(JSONObject linkedinPositions) {
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

    public Date getLinkedinLastModified() {
        return linkedinLastModified;
    }

    public void setLinkedinLastModified(Date linkedinLastModified) {
        this.linkedinLastModified = linkedinLastModified;
    }

    public String getLinkedinProposalComments() {
        return linkedinProposalComments;
    }

    public void setLinkedinProposalComments(String linkedinProposalComments) {
        this.linkedinProposalComments = linkedinProposalComments;
    }

    public String getLinkedinAssociations() {
        return linkedinAssociations;
    }

    public void setLinkedinAssociations(String linkedinAssociations) {
        this.linkedinAssociations = linkedinAssociations;
    }

    public String getLinkedinInterests() {
        return linkedinInterests;
    }

    public void setLinkedinInterests(String linkedinInterests) {
        this.linkedinInterests = linkedinInterests;
    }

    public JSONObject getLinkedinLanguages() {
        return linkedinLanguages;
    }

    public void setLinkedinLanguages(JSONObject linkedinLanguages) {
        this.linkedinLanguages = linkedinLanguages;
    }

    public JSONObject getLinkedinSkills() {
        return linkedinSkills;
    }

    public void setLinkedinSkills(JSONObject linkedinSkills) {
        this.linkedinSkills = linkedinSkills;
    }

    public JSONObject getLinkedinCertifications() {
        return linkedinCertifications;
    }

    public void setLinkedinCertifications(JSONObject linkedinCertifications) {
        this.linkedinCertifications = linkedinCertifications;
    }

    public JSONObject getLinkedinEducations() {
        return linkedinEducations;
    }

    public void setLinkedinEducations(JSONObject linkedinEducations) {
        this.linkedinEducations = linkedinEducations;
    }

    public JSONObject getLinkedinCourses() {
        return linkedinCourses;
    }

    public void setLinkedinCourses(JSONObject linkedinCourses) {
        this.linkedinCourses = linkedinCourses;
    }

    public JSONObject getLinkedinVolunteer() {
        return linkedinVolunteer;
    }

    public void setLinkedinVolunteer(JSONObject linkedinVolunteer) {
        this.linkedinVolunteer = linkedinVolunteer;
    }

    public JSONObject getLinkedinThreeCurrentPositions() {
        return linkedinThreeCurrentPositions;
    }

    public void setLinkedinThreeCurrentPositions(JSONObject linkedinThreeCurrentPositions) {
        this.linkedinThreeCurrentPositions = linkedinThreeCurrentPositions;
    }

    public JSONObject getLinkedinThreePastPositions() {
        return linkedinThreePastPositions;
    }

    public void setLinkedinThreePastPositions(JSONObject linkedinThreePastPositions) {
        this.linkedinThreePastPositions = linkedinThreePastPositions;
    }

    public Integer getLinkedinNumRecomenders() {
        return linkedinNumRecomenders;
    }

    public void setLinkedinNumRecomenders(Integer linkedinNumRecomenders) {
        this.linkedinNumRecomenders = linkedinNumRecomenders;
    }

    public JSONObject getLinkedinRecommendationsRecived() {
        return linkedinRecommendationsRecived;
    }

    public void setLinkedinRecommendationsRecived(JSONObject linkedinRecommendationsRecived) {
        this.linkedinRecommendationsRecived = linkedinRecommendationsRecived;
    }

    public JSONObject getLinkedinSuggestions() {
        return linkedinSuggestions;
    }

    public void setLinkedinSuggestions(JSONObject linkedinSuggestions) {
        this.linkedinSuggestions = linkedinSuggestions;
    }

    public String getLinkedinDateOfBirth() {
        return linkedinDateOfBirth;
    }

    public void setLinkedinDateOfBirth(String linkedinDateOfBirth) {
        this.linkedinDateOfBirth = linkedinDateOfBirth;
    }

    public JSONObject getLinkedinHonorsAwards() {
        return linkedinHonorsAwards;
    }

    public void setLinkedinHonorsAwards(JSONObject linkedinHonorsAwards) {
        this.linkedinHonorsAwards = linkedinHonorsAwards;
    }
}

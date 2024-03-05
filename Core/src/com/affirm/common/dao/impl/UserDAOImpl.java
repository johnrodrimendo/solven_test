/**
 *
 */
package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.UserOfHierarchy;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.security.OldPassword;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */

@Repository
public class UserDAOImpl extends JsonResolverDAO implements UserDAO {


    @Autowired
    private CatalogService catalogService;

    @Override
    public User registerUser(String name, String firstSurname, String lastSurname, Integer docType, String docNumber, Date birthDate) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.ins_user(?, ?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, name),
                new SqlParameterValue(Types.VARCHAR, firstSurname),
                new SqlParameterValue(Types.VARCHAR, lastSurname),
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber),
                new SqlParameterValue(Types.DATE, birthDate));

        User user = new User();
        user.fillFromDb(dbJson);
        return user;
    }

    @Override
    public String registerPhoneNumber(int userId, String countryCode, String phoneNumber) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.ins_phone_number(?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, countryCode),
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.BOOLEAN, false));
        return dbJson.getString("sms_token");
    }

    @Override
    public String registerPhoneNumber(int userId, String countryCode, String phoneNumber, boolean forceInsert) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.ins_phone_number(?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, countryCode),
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.BOOLEAN, false),
                new SqlParameterValue(Types.BOOLEAN, forceInsert));
        return dbJson.getString("sms_token");
    }

    @Override
    public String getNewCellphoneAuthToken(int userId, String phoneNumber) throws Exception {
        return queryForObjectTrx("select * from users.get_new_token(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, phoneNumber));
    }

    @Override
    public void registerFacebook(int userId, UserFacebook facebook) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.ins_facebook_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId + ""),
                new SqlParameterValue(Types.VARCHAR, facebook.getFacebookId()),
                new SqlParameterValue(Types.VARCHAR, facebook.getFacebookEmail()),
                new SqlParameterValue(Types.VARCHAR, facebook.getFacebookName()),
                new SqlParameterValue(Types.VARCHAR, facebook.getFacebookFirstName()),
                new SqlParameterValue(Types.VARCHAR, facebook.getFacebookLastName()),
                new SqlParameterValue(Types.INTEGER, facebook.getFacebookAgeMin()),
                new SqlParameterValue(Types.INTEGER, facebook.getFacebookAgeMax()),
                new SqlParameterValue(Types.VARCHAR, facebook.getFacebookLink()),
                new SqlParameterValue(Types.VARCHAR, facebook.getFacebookGender()),
                new SqlParameterValue(Types.VARCHAR, facebook.getFacebookLocale()),
                new SqlParameterValue(Types.VARCHAR, facebook.getFacebookPicture()),
                new SqlParameterValue(Types.INTEGER, facebook.getFacebookTimeZone()),
                new SqlParameterValue(Types.DATE, facebook.getFacebookUpdatedTime()),
                new SqlParameterValue(Types.VARCHAR, facebook.getFacebookVerified()),
                new SqlParameterValue(Types.DATE, facebook.getFacebookBirthday() != null ? new SimpleDateFormat("MM/dd/YYYY").parse(facebook.getFacebookBirthday()) : null),
                new SqlParameterValue(Types.VARCHAR, facebook.getFacebookLocation()),
                new SqlParameterValue(Types.INTEGER, facebook.getFacebookFriends()));
        if (dbJson != null && dbJson.has("message")) {
            throw new SqlErrorMessageException(dbJson.getString("message"), null);
        }
    }

    @Override
    public boolean registerFacebookMessengerId(int userId, String messengerId) throws Exception {
        return queryForObjectTrx("select * from users.upd_messenger_id(?, ?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, messengerId));
    }

    @Override
    public User getUser(Integer userId) {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_user(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId));
        if (dbjson == null) {
            return null;
        }

        User user = new User();
        user.fillFromDb(dbjson);
        return user;
    }


    @Override
    public JSONObject validateCellphone(int userId, String authToken) throws Exception {
        return queryForObjectTrx("select * from users.validate_phone_number(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, authToken));
    }

    @Override
    public List<ExternalUserInfoState> getUserExternalInfoState(int userId) throws Exception {
        JSONArray jarray = queryForObject("select * from external.get_user_info_state(?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, userId));
        if (jarray == null) {
            return null;
        }

        List<ExternalUserInfoState> infoStates = new ArrayList<>();
        for (int i = 0; i < jarray.length(); i++) {
            infoStates.add(new ExternalUserInfoState(jarray.getJSONObject(i)));
        }
        return infoStates;
    }

    @Override
    public UserPreliminaryCredibilityEvaluation getLastPreliminaryCredibilityEvaluation(int userId) throws Exception {
        JSONObject dbjson = queryForObjectEvaluation("select * from evaluation.get_last_preliminary_credibility_evaluation(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId));
        if (dbjson == null) {
            return null;
        }

        UserPreliminaryCredibilityEvaluation validation = new UserPreliminaryCredibilityEvaluation();
        validation.fillFromDb(dbjson);
        return validation;
    }

    @Override
    public JSONArray getUserNamesExternalSources(int docType, String docNumber) throws Exception {
        return queryForObjectExternal("select * from external.get_user_names(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }

    @Override
    public UserFacebook getUserFacebook(Integer userId) throws Exception {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_user_facebook(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId));
        if (dbjson == null) {
            return null;
        }

        UserFacebook userFacebook = new UserFacebook();
        userFacebook.fillFromDb(dbjson);
        return userFacebook;
    }

    // Reemplazado por personDao.getPersonContactInformation!!
//    @Override
//    public PersonAddress getAddressInformation(int personId) throws Exception {
//        StringBuffer query = new StringBuffer("select * from person.get_address(p_person_id)");
//        PGQeuryUtil.replaceInt(query, "p_person_id", personId);
//
//        JSONObject dbjson = executeForJson(query.toString());
//        if (dbjson == null) {
//            return null;
//        }
//
//        PersonAddress address = new PersonAddress();
//        address.setId(JsonUtil.getIntFromJson(dbjson, "person_id", null));
//        address.setUbigeo(JsonUtil.getStringFromJson(dbjson, "ubigeo_id", null));
//        address.setStreetTypeId(JsonUtil.getIntFromJson(dbjson, "street_type_id", null));
//        address.setStreetName(JsonUtil.getStringFromJson(dbjson, "street_name", null));
//        address.setStreetNumber(JsonUtil.getStringFromJson(dbjson, "street_number", null));
//        address.setInterior(JsonUtil.getStringFromJson(dbjson, "interior", null));
//        address.setDetail(JsonUtil.getStringFromJson(dbjson, "detail", null));
//
//        return address;
//    }


//
//    @Override
//    public List<PersonOcupationalInformation> getOcupationalInformation(Integer personId, Locale locale) throws Exception {
//        StringBuffer query = new StringBuffer("select * from person.get_ocupational_information(p_person_id)");
//        PGQeuryUtil.replaceInt(query, "p_person_id", personId);
//
//        JSONArray dbarray = executeForArray(query.toString());
//        if (dbarray == null) {
//            return null;
//        }
//
//        List<PersonOcupationalInformation> ocupationalInformations = new ArrayList<>();
//        for (int i = 0; i < dbarray.length(); i++) {
//            JSONObject json = dbarray.getJSONObject(i);
//            PersonOcupationalInformation info = new PersonOcupationalInformation();
//            info.fillFromDb(json, catalogService, locale);
//            ocupationalInformations.add(info);
//        }
//        return ocupationalInformations;
//    }


    @Override
    public Integer registerUserFile(Integer userId, Integer loanApplicationid, Integer userDocumentTypeId, String filename) throws Exception {
        return queryForObjectTrx("select * from users.ins_user_file(?, ?, ?, ?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, loanApplicationid),
                new SqlParameterValue(Types.INTEGER, userDocumentTypeId),
                new SqlParameterValue(Types.VARCHAR, filename));
    }

    @Override
    public void updateReferalPhones(Integer personId, String referalPhone1, Integer relative1, String referalPhone2, Integer relative2) throws Exception {
        queryForObjectTrx("select * from person.upd_referral_phones(?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.VARCHAR, "51"),
                new SqlParameterValue(Types.VARCHAR, referalPhone1),
                new SqlParameterValue(Types.INTEGER, relative1),
                new SqlParameterValue(Types.VARCHAR, "51"),
                new SqlParameterValue(Types.VARCHAR, referalPhone2),
                new SqlParameterValue(Types.INTEGER, relative2));
    }

    @Override
    public void updateLoanApplicationLocation(Integer loanApplicationId, String ipAddress, String ipCountryCode,
                                              String ipCountryName, String ipRegionCode, String ipRegionName, Double ipLatitude, Double ipLongitude,
                                              Double navLatitude, Double navLongitude, String navFormattedAddress) {
        queryForObjectTrx("select * from credit.upd_loan_application_location_info(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.VARCHAR, ipAddress),
                new SqlParameterValue(Types.VARCHAR, ipCountryCode),
                new SqlParameterValue(Types.VARCHAR, ipCountryName),
                new SqlParameterValue(Types.VARCHAR, ipRegionCode),
                new SqlParameterValue(Types.VARCHAR, ipRegionName),
                new SqlParameterValue(Types.NUMERIC, ipLatitude),
                new SqlParameterValue(Types.NUMERIC, ipLongitude),
                new SqlParameterValue(Types.NUMERIC, navLatitude),
                new SqlParameterValue(Types.NUMERIC, navLongitude),
                new SqlParameterValue(Types.VARCHAR, navFormattedAddress));
    }

    @Override
    public void registerLinkedin(Integer personId, PersonLinkedIn linkedin) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.ins_linkedin(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinId()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinFirstName()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinLastName()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinMaidenName()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinFormattedName()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinPhoneticFirstName()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinPhoneticLastName()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinFormattedPhoneticName()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinHeadline()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinLocation()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinIndustry()),
                new SqlParameterValue(Types.INTEGER, linkedin.getLinkedinNumConnections()),
                new SqlParameterValue(Types.BOOLEAN, linkedin.getLinkedinNumConnectionsCapped()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinSummary()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinSpecialties()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinPositions()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinPictureUrl()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinPictureUrlsOriginal()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinPublicProfileUrl()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinEmailAddress()),
                new SqlParameterValue(Types.DATE, linkedin.getLinkedinLastModified()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinProposalComments()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinAssociations()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinInterests()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinLanguages()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinSkills()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinCertifications()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinEducations()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinCourses()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinVolunteer()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinThreeCurrentPositions()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinThreePastPositions()),
                new SqlParameterValue(Types.INTEGER, linkedin.getLinkedinNumRecomenders()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinRecommendationsRecived()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinSuggestions()),
                new SqlParameterValue(Types.VARCHAR, linkedin.getLinkedinDateOfBirth()),
                new SqlParameterValue(Types.OTHER, linkedin.getLinkedinHonorsAwards()));
        if (dbJson != null && dbJson.has("message")) {
            throw new SqlErrorMessageException(dbJson.getString("message"), null);
        }
    }

    @Override
    public PersonLinkedIn getLinkedin(Integer personId) throws Exception {
        JSONObject dbjson = queryForObjectTrx("select * from person.get_linkedin(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbjson == null) {
            return null;
        }

        PersonLinkedIn linkedin = new PersonLinkedIn();
        linkedin.fillFromDb(dbjson);
        return linkedin;
    }

    @Override
    public void validateUserFile(
            int userFileId, boolean validated) throws Exception {
        queryForObjectTrx("select * from users.bo_validate_user_file(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, userFileId),
                new SqlParameterValue(Types.BOOLEAN, validated));
    }

    @Override
    public void validateAllUserFile(
            int userId, int loanApplicationId) {
        queryForObjectTrx("select * from users.bo_validate_user_files_all(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
    }

    @Override
    public void updateUserFileType(int userFileId, int fileTypeId) throws Exception {
        String query = "UPDATE users.tb_user_files set filetype_id = ? where user_files_id = ?";
        updateTrx(query,
                new SqlParameterValue(Types.INTEGER, fileTypeId),
                new SqlParameterValue(Types.INTEGER, userFileId));
    }

    @Override
    public void registerEarlyAccess(Integer docType, String docNumber, String email, Integer loanReasonId, Integer productId) throws Exception {
        queryForObjectTrx("select * from support.insert_pre_register(?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber),
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.INTEGER, loanReasonId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public void registerSubscritor(String email, boolean isActive) throws Exception {
        queryForObjectTrx("select * from users.manage_subscriptors(?, ?)", String.class,
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.BOOLEAN, isActive));
    }

    @Override
    public UserNetworkToken registerNetworkAccessToken(Integer userId, char network, String accessToken, String refreshToken, String email, String networkId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.register_network_token(?, ?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.CHAR, network),
                new SqlParameterValue(Types.VARCHAR, accessToken),
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.VARCHAR, refreshToken),
                new SqlParameterValue(Types.VARCHAR, networkId));
        if (dbJson == null) {
            return null;
        } else if (dbJson != null && dbJson.has("message")) {
            throw new SqlErrorMessageException(dbJson.getString("message"), null);
        }

        UserNetworkToken userNetworkToken = new UserNetworkToken();
        userNetworkToken.fillFromDb(dbJson);
        return userNetworkToken;
    }

    @Override
    public Integer getUserIdByPersonId(int personId) throws Exception {
        return queryForObjectTrx("select user_id from users.tb_user where person_id = ?", Integer.class,
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public UserFile getUserFile(int userFileId) throws Exception {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_user_file(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userFileId));
        if (dbjson == null) {
            return null;
        }

        UserFile userFile = new UserFile();
        userFile.fillFromDb(dbjson, catalogService);
        return userFile;
    }

    @Override
    public User getUserByDocument(int docType, String docNumber) throws Exception {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_user_by_document(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber));
        if (dbjson == null) {
            return null;
        }

        User user = new User();
        user.fillFromDb(dbjson);
        return user;
    }

    @Override
    public User getUserByFacebookId(String facebookId) throws Exception {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_user_by_facebook(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, facebookId));
        if (dbjson == null) {
            return null;
        }

        User user = new User();
        user.fillFromDb(dbjson);
        return user;
    }

    @Override
    public User getUserByLinkedinId(String linkedinId) throws Exception {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_user_by_linkedin(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, linkedinId));
        if (dbjson == null) {
            return null;
        }

        User user = new User();
        user.fillFromDb(dbjson);
        return user;
    }

    @Override
    public User getUserByFacebookMessengerId(String messengerId) throws Exception {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_user_by_messenger(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, messengerId));
        if (dbjson == null) {
            return null;
        }

        User user = new User();
        user.fillFromDb(dbjson);
        return user;
    }

    @Override
    public void registerEmailAge(Integer userId, String result) throws Exception {
        JSONObject dbjson = queryForObjectTrx("select * from users.register_emailage(?, ?::json)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, result));
    }

    @Override
    public void updateNetworkContacts(int networkTokenId, String jsonContacts) throws Exception {
        queryForObjectTrx("select * from users.upd_network_contacts(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, networkTokenId),
                new SqlParameterValue(Types.OTHER, jsonContacts));
    }

    @Override
    public void updateNetworkProfile(int networkTokenId, String jsonProfile) throws Exception {
        queryForObjectTrx("select * from users.upd_network_profile(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, networkTokenId),
                new SqlParameterValue(Types.OTHER, jsonProfile));
    }

    @Override
    public UserNetworkToken getUserNetworkTokenByProvider(Integer userId, char network) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.get_network_token(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.CHAR, network));
        if (dbJson == null) {
            return null;
        }

        UserNetworkToken userMailToken = new UserNetworkToken();
        userMailToken.fillFromDb(dbJson);
        return userMailToken;
    }

    @Override
    public void registerSessionLogout(int extranetSessionId, Date signoutDate) throws Exception {
        queryForObjectTrx("select * from users.sign_out(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, extranetSessionId),
                new SqlParameterValue(Types.TIMESTAMP, signoutDate));
    }

    @Override
    public UserSessionLog getLastSessionLog(int userId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.get_set_session(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId));
        if (dbJson == null)
            return null;

        UserSessionLog userSession = new UserSessionLog();
        userSession.fillFromDb(dbJson);
        return userSession;
    }

    @Override
    public void registerNoAuthExtranetLinkExpiration(int userId, Integer seconds) throws Exception {
        queryForObjectTrx("select * from users.upd_auth_link_expiration_(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, seconds));
    }

    @Override
    public User getUserByYahoo(String yahooId) throws Exception {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_user_by_yahoo(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, yahooId));
        if (dbjson == null) {
            return null;
        }
        User user = new User();
        user.fillFromDb(dbjson);
        return user;
    }

    @Override
    public User getUserByGoogle(String googleId) throws Exception {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_user_by_google(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, googleId));
        if (dbjson == null) {
            return null;
        }
        User user = new User();
        user.fillFromDb(dbjson);
        return user;
    }

    @Override
    public User getUserByWindows(String windowsId) throws Exception {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_user_by_windows(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, windowsId));
        if (dbjson == null) {
            return null;
        }
        User user = new User();
        user.fillFromDb(dbjson);
        return user;
    }

    @Override
    public boolean existsFacebookMessengerId(String messengerId) throws Exception {
        return queryForObjectTrx("select * from users.exists_messenger_id(?)", Boolean.class,
                new SqlParameterValue(Types.VARCHAR, messengerId));
    }

    @Override
    public Integer registerEmailChange(int userId, String email) throws Exception {
        return queryForObjectTrx("select * from users.ins_email(?, ?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, email));
    }

    @Override
    public void validateEmailChange(int userId, int emailId) throws Exception {
        queryForObjectTrx("select * from users.validate_email_change(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, emailId));
    }

    @Override
    public List<UserEmail> getUserEmails(int userId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from users.get_emails(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, userId));
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<UserEmail> emails = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            UserEmail email = new UserEmail();
            email.fillFromDb(dbArray.getJSONObject(i));
            emails.add(email);
        }
        return emails;
    }

    @Override
    public void verifyEmail(int userId, int emailId, boolean isVerified) throws Exception {
        updateTrx("UPDATE users.tb_email set is_verified = ? where user_id = ? and email_id = ? and is_active; " +
                        "UPDATE users.tb_user set email_verified = ? WHERE user_id = ?;",
                new SqlParameterValue(Types.BOOLEAN, isVerified),
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, emailId),
                new SqlParameterValue(Types.BOOLEAN, isVerified),
                new SqlParameterValue(Types.INTEGER, userId)
        );
    }

    @Override
    public void updatePassword(int userId, String password) throws Exception {
        updateTrx("UPDATE users.tb_user set password = ? where user_id = ?",
                new SqlParameterValue(Types.VARCHAR, password),
                new SqlParameterValue(Types.INTEGER, userId));
    }

    @Override
    public User getUserByEmail(String email) {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_user_by_email(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, email));
        if (dbjson == null) {
            return null;
        }
        User user = new User();
        user.fillFromDb(dbjson);
        return user;
    }

    @Override
    public UserEntity getUserEntityById(int id, Locale locale) {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_entity_user_by_id(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, id));
        if (dbjson == null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.fillFromDb(dbjson, catalogService, locale);
        return user;
    }

    @Override
    public UserEntity getUserEntityByEmail(String email, Locale locale, Integer entityId) {
        JSONObject dbjson = queryForObjectTrx("select * from users.get_entity_user_by_email(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.INTEGER, entityId));
        if (dbjson == null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.fillFromDb(dbjson, catalogService, locale);
        return user;
    }

    @Override
    public void registerResetToken(String email, String token) throws Exception {
        queryForObjectTrx("select * from users.register_entity_user_password_token(?, ?)", String.class,
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.VARCHAR, token));
    }

    @Override
    public Boolean updateResetPassword(String token, String email, String password) throws Exception {
        return queryForObjectTrx("select * from users.update_entity_user_password_by_token(?, ?, ?)", Boolean.class,
                new SqlParameterValue(Types.VARCHAR, token),
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.VARCHAR, password));
    }


    @Override
    public List<OldPassword> getUserEntityPasswords(Integer entityUserId, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from users.get_entity_users_password(?,?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, Configuration.OLD_PASSWORDS));
        if (dbArray == null) {
            return null;
        }

        List<OldPassword> passwords = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            OldPassword password = new OldPassword();
            password.fillFromDb(dbArray.getJSONObject(i));
            passwords.add(password);
        }

        return passwords;
    }


    @Override
    public Boolean isResetPasswordTokenUsed(String token) throws Exception {
        return queryForObjectTrx("select is_used from users.tb_entity_users_password_tokens where token = ? order by register_date desc limit 1", Boolean.class,
                new SqlParameterValue(Types.VARCHAR, token));
    }

    @Override
    public boolean mustCallEmailage(int userId, String email) throws Exception {
        return queryForObjectTrx("select * from users.must_run_emailage(?, ?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, email));
    }

    @Override
    public List<UserEntity> getUserEntityPendingsToActivateTfa(int entityId, Locale locale) {
        JSONArray dbArray = queryForObjectTrx("select * from users.get_entity_users_pending_tfa(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId));
        if (dbArray == null) {
            return null;
        }

        List<UserEntity> entityUsers = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            UserEntity entityUser = new UserEntity();
            entityUser.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            entityUsers.add(entityUser);
        }

        return entityUsers;
    }


    @Override
    public List<EntityExtranetUserActionLog> getUserActionLog(Integer entityId, Integer entityUserId, Integer offset, Integer limit) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from security.get_lg_entity_user_actions(?, ?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit));
        if (dbArray == null) {
            return null;
        }

        List<EntityExtranetUserActionLog> userActionLogs = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EntityExtranetUserActionLog userActionLog = new EntityExtranetUserActionLog();
            userActionLog.fillFromDb(dbArray.getJSONObject(i));
            userActionLogs.add(userActionLog);
        }
        return userActionLogs;
    }

    @Override
    public List<EntityExtranetUser> getEntityExtranetUsers(Integer entityId, Integer offset, Integer limit) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from security.get_entity_users_by_entity(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit));
        if (dbArray == null) {
            return null;
        }

        List<EntityExtranetUser> userActionLogs = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EntityExtranetUser user = new EntityExtranetUser();
            user.fillFromDb(dbArray.getJSONObject(i),catalogService);
            userActionLogs.add(user);
        }
        return userActionLogs;
    }

    public void activateExtranetEntityUser(Integer entityUserId, Boolean valueToUpdate) throws Exception {
        updateTrx("UPDATE users.tb_entity_users set is_active = ? where entity_user_id= ? ",
                new SqlParameterValue(Types.BOOLEAN, valueToUpdate),
                new SqlParameterValue(Types.INTEGER, entityUserId));
    }

    @Override
    public PhoneNumber getPhoneNumberByUser(Integer userId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.get_user_phone_number(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId));
        if (dbJson == null) {
            return null;
        }

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.fillFromDb(dbJson);
        return phoneNumber;
    }

    @Override
    public List<PhoneNumber> getAllPhoneNumbersByUser(Integer userId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from users.get_user_phone_numbers(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, userId));
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<PhoneNumber> phoneNumberList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.fillFromDb(dbArray.getJSONObject(i));
            phoneNumberList.add(phoneNumber);
        }

        return phoneNumberList;
    }

    @Override
    public void updatePhoneNumberJsInteraction(Integer phoneNumberId, String json) throws Exception {
        updateTrx("UPDATE users.tb_phone_number set js_person_interaction_id = ? where phone_number_id = ?",
                new SqlParameterValue(Types.OTHER, json),
                new SqlParameterValue(Types.INTEGER, phoneNumberId));
    }

    @Override
    public List<UserOfHierarchy> getUsersOfHierarchy() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from security.get_users_of_hierarchy()", JSONArray.class);

        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<UserOfHierarchy> userOfHierarchyList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            UserOfHierarchy userOfHierarchy = new UserOfHierarchy();
            userOfHierarchy.fillFromDb(dbArray.getJSONObject(i));
            userOfHierarchyList.add(userOfHierarchy);
        }

        return userOfHierarchyList;
    }

    @Override
    public List<UserEntity> getEntityUsers(int entityId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from users.get_entity_users(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId));

        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<UserEntity> userOfHierarchyList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            UserEntity userEntity = new UserEntity();
            userEntity.fillFromDb(dbArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            userOfHierarchyList.add(userEntity);
        }

        return userOfHierarchyList;
    }

    @Override
    public JSONObject registerBDSUserEntity(Integer userEntityId, Integer entityId, String email, String personName, String firstSurname, String password, Integer entityUserIdFromEntity, Integer entityUserType, Integer entityUserIdProductor, Integer entityUserIdOrganizador, Integer[] countries, Integer channelId) throws Exception {
        return queryForObjectTrx("select users.register_entity_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userEntityId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.VARCHAR, personName),
                new SqlParameterValue(Types.VARCHAR, firstSurname),
                new SqlParameterValue(Types.VARCHAR, password),
                new SqlParameterValue(Types.INTEGER, entityUserIdFromEntity),
                new SqlParameterValue(Types.INTEGER, entityUserType),
                new SqlParameterValue(Types.INTEGER, entityUserIdProductor),
                new SqlParameterValue(Types.INTEGER, entityUserIdOrganizador),
                new SqlParameterValue(Types.OTHER, countries != null ? new Gson().toJson(countries) : null),
                new SqlParameterValue(Types.INTEGER, channelId)
        );

    }

    @Override
    public UserEntity getEntityUserById(int entityUserId, Locale locale) {
        JSONObject dbjson = queryForObjectTrx("select users.get_entity_user_by_entity_user_id(?);", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityUserId));
        if (dbjson == null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.fillFromDb(dbjson, catalogService, locale);
        return user;
    }

    @Override
    public List<Affiliator> getAffiliators() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_tb_affiliator()", JSONArray.class);

        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<Affiliator> affiliators = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Affiliator affiliator = new Affiliator();
            affiliator.fillFromDb(dbArray.getJSONObject(i));
            affiliators.add(affiliator);
        }

        return affiliators;
    }

    @Override
    public Affiliator getAffiliator(Integer affiliatorId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from originator.get_tb_affiliator(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, affiliatorId));
        if (dbJson == null) {
            return null;
        }

        Affiliator affiliator = new Affiliator();
        affiliator.fillFromDb(dbJson);
        return affiliator;
    }

    @Override
    public List<Product> getProductsFromUserAndEntity(int entityUserId, int entityId, Locale locale) {
        JSONArray dbArray = queryForObjectTrx("SELECT json_agg(DISTINCT product_id) FROM users.tb_entity_users_entity where entity_user_id = ? and entity_id = ?", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, entityId)
                );
        if (dbArray == null) {
            return null;
        }
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < dbArray.length(); i++) {
            products.add(catalogService.getProduct(dbArray.getInt(i)));
        }

        return products;
    }

    @Override
    public void deleteAndCreateEntityUserRelationship(int entityUserId, int entityId, int productId) {
        updateTrx("DELETE from users.tb_entity_users_entity where entity_user_id = ? and entity_id = ?; INSERT INTO users.tb_entity_users_entity(entity_user_id, entity_id, product_id) VALUES (?,?,?);",
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId)
        );
    }

    @Override
    public boolean existsOtherUsersByEmail(String email, int userId) {
        Boolean exists = queryForObjectTrx("select * from users.exists_other_users_by_email(?,?)", Boolean.class,
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.BIGINT, userId));
        if (exists == null) {
            return false;
        }
        return exists;
    }

    @Override
    public boolean existsOtherUsersByPhoneNumber(String phoneNumber, String countryCode, int userId) {
        Boolean exists = queryForObjectTrx("select * from users.exists_other_users_by_phone_number(?,?,?)", Boolean.class,
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.VARCHAR, countryCode),
                new SqlParameterValue(Types.BIGINT, userId));
        if (exists == null) {
            return false;
        }
        return exists;
    }

    @Override
    public void updateUserEmailJsToken(int userEmailId, List<UserEmail.EmailTokens> emailToken) throws Exception {
        updateTrx("UPDATE users.tb_email set js_tokens = ? where email_id = ?;",
                new SqlParameterValue(Types.OTHER, new Gson().toJson(emailToken)),
                new SqlParameterValue(Types.INTEGER, userEmailId)
        );
    }

    @Override
    public UserEmail getUserEmailById(int userEmailId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.get_user_email_by_id(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userEmailId));
        if (dbJson == null) {
            return null;
        }

        UserEmail userEmail = new UserEmail();
        userEmail.fillFromDb(dbJson);
        return userEmail;
    }

    @Override
    public void enableOrDisableEmailByUserEmail(int userId, int emailId, boolean isVerified) throws Exception {
        updateTrx("UPDATE users.tb_email set is_verified = ? where user_id = ? and email_id = ?; ",
                new SqlParameterValue(Types.BOOLEAN, isVerified),
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, emailId)
        );
    }

    @Override
    public Boolean isVerifiedEmail(int email_id) throws Exception {
        return queryForObjectTrx("select is_verified from users.tb_email where email_id = ?", Boolean.class,
                new SqlParameterValue(Types.INTEGER, email_id));
    }

    @Override
    public Boolean isVerifiedPhoneNumber(int phone_number_id) throws Exception {
        return queryForObjectTrx("select is_verified from users.tb_phone_number where phone_number_id = ?", Boolean.class,
                new SqlParameterValue(Types.INTEGER, phone_number_id));
    }

    @Override
    public Integer insertEmail(Integer userId, String email, boolean active, boolean verified) {
        Integer emailId = queryForObjectTrx("INSERT INTO users.tb_email (user_id, email, is_active, register_date, is_verified) VALUES (?, ?, ?, ?, ?) RETURNING email_id;", Integer.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.BOOLEAN, active),
                new SqlParameterValue(Types.TIMESTAMP, new Date()),
                new SqlParameterValue(Types.BOOLEAN, verified));
        return emailId;
    }

    @Override
    public Integer insertPhoneNumber(Integer userId, String countryCode, String phoneNumber, String smsToken, boolean active, boolean verified) {
        Integer emailId = queryForObjectTrx("INSERT INTO users.tb_phone_number (user_id, country_code, phone_number, sms_token, is_active, is_verified) VALUES (?, ?, ?, ?, ?, ?) returning phone_number_id;", Integer.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, countryCode),
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.VARCHAR, smsToken),
                new SqlParameterValue(Types.BOOLEAN, active),
                new SqlParameterValue(Types.BOOLEAN, verified));
        return emailId;
    }

    @Override
    public void updateActiveEmail(int emailId, boolean active) throws Exception {
        updateTrx("UPDATE users.tb_email set is_active = ? where email_id = ?",
                new SqlParameterValue(Types.BOOLEAN, active),
                new SqlParameterValue(Types.INTEGER, emailId));
    }

    @Override
    public void updateActivePhoneNumber(int phoneNumberId, boolean active) throws Exception {
        updateTrx("UPDATE users.tb_phone_number set is_active = ? where phone_number_id = ?",
                new SqlParameterValue(Types.BOOLEAN, active),
                new SqlParameterValue(Types.INTEGER, phoneNumberId));
    }

    @Override
    public void updateUserPhoneNumber(int userId, String phoneNumber, Boolean verified) throws Exception {
        updateTrx("UPDATE users.tb_user set phone_number = ?, phone_verified = ? WHERE user_id = ?",
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.BOOLEAN, verified),
                new SqlParameterValue(Types.INTEGER, userId));
    }

    @Override
    public void updateUserEmail(int userId, String email, Boolean verified) throws Exception {
        updateTrx("UPDATE users.tb_user set email = ?, email_verified = ? WHERE user_id = ?",
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.BOOLEAN, verified),
                new SqlParameterValue(Types.INTEGER, userId));
    }

    @Override
    public PhoneNumber getUserPhoneNumberById(int userPhoneNumberId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.get_user_phone_number_by_id(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userPhoneNumberId));
        if (dbJson == null) {
            return null;
        }

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.fillFromDb(dbJson);
        return phoneNumber;
    }


    @Override
    public String registerPhoneNumberExcludeVerification(int userId, String countryCode, String phoneNumber) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.ins_phone_number_exclude_verification(?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, countryCode),
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.BOOLEAN, false));
        return dbJson.getString("sms_token");
    }

    @Override
    public Integer registerEmailChangeExcludeVerification(int userId, String email) throws Exception {
        return queryForObjectTrx("select * from users.ins_email_exclude_verification(?, ?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, email));
    }

    @Override
    public void removeUserFileFromTypeAndLoanId(int userId, int loanId, int userFileTypeId) throws Exception {
        updateTrx("UPDATE users.tb_user_files set filetype_id = 18, is_active = false where user_id = ? and loan_application_id = ? and filetype_id = ?; ",
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, loanId),
                new SqlParameterValue(Types.INTEGER, userFileTypeId)
        );
    }

}

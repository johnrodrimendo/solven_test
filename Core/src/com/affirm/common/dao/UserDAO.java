package com.affirm.common.dao;

import com.affirm.common.model.UserOfHierarchy;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.security.OldPassword;
import com.affirm.common.model.transactional.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface UserDAO {

    User registerUser(String name, String firstSurname, String lastSurname, Integer docType, String docNumber, Date birthDate) throws Exception;

    String registerPhoneNumber(int userId, String countryCode, String phoneNumber) throws Exception;

    String registerPhoneNumber(int userId, String countryCode, String phoneNumber, boolean forceInsert) throws Exception;

    String getNewCellphoneAuthToken(int userId, String phoneNumber) throws Exception;

    void registerFacebook(int userId, UserFacebook facebook) throws Exception;

    boolean registerFacebookMessengerId(int userId, String messengerId) throws Exception;

    User getUser(Integer userId);

    JSONObject validateCellphone(int userId, String authToken) throws Exception;

    List<ExternalUserInfoState> getUserExternalInfoState(int userId) throws Exception;

    UserPreliminaryCredibilityEvaluation getLastPreliminaryCredibilityEvaluation(int userId) throws Exception;

    JSONArray getUserNamesExternalSources(int docType, String docNumber) throws Exception;

    UserFacebook getUserFacebook(Integer userId) throws Exception;

    Integer registerUserFile(Integer userId, Integer loanApplicationid, Integer userDocumentTypeId, String filename) throws Exception;

    void updateReferalPhones(Integer personId, String referalPhone1, Integer relative1, String referalPhone2, Integer relative2) throws Exception;

    void updateLoanApplicationLocation(Integer loanApplicationId, String ipAddress, String ipCountryCode,
                                       String ipCountryName, String ipRegionCode, String ipRegionName, Double ipLatitude, Double ipLongitude,
                                       Double navLatitude, Double navLongitude, String navFormattedAddress) ;

    void registerLinkedin(Integer personId, PersonLinkedIn linkedin) throws Exception;

    PersonLinkedIn getLinkedin(Integer personId) throws Exception;

    void validateUserFile(int userFileId, boolean validated) throws Exception;

    void validateAllUserFile(int userId, int validateAllUserFile);

    void updateUserFileType(int userFileId, int fileTypeId) throws Exception;

    void registerEarlyAccess(Integer docType, String docNumber, String email, Integer loanReasonId, Integer productId) throws Exception;

    void registerSubscritor(String email, boolean isActive) throws Exception;

    UserNetworkToken registerNetworkAccessToken(Integer userId, char network, String accessToken, String refreshToken, String email, String networkId) throws Exception;

    Integer getUserIdByPersonId(int PersonId) throws Exception;

    UserFile getUserFile(int userFileId) throws Exception;

    User getUserByDocument(int docType, String docNumber) throws Exception;

    User getUserByFacebookId(String facebookId) throws Exception;

    User getUserByLinkedinId(String linkedinId) throws Exception;

    User getUserByFacebookMessengerId(String messengerId) throws Exception;

    void registerEmailAge(Integer userId, String result) throws Exception;

    void updateNetworkContacts(int networkTokenId, String jsonContacts) throws Exception;

    void updateNetworkProfile(int networkTokenId, String jsonProfile) throws Exception;

    UserNetworkToken getUserNetworkTokenByProvider(Integer userId, char network) throws Exception;

    void registerSessionLogout(int extranetSessionId, Date signoutDate) throws Exception;

    UserSessionLog getLastSessionLog(int userId) throws Exception;

    void registerNoAuthExtranetLinkExpiration(int userId, Integer seconds) throws Exception;

    User getUserByYahoo(String id) throws Exception;

    User getUserByGoogle(String id) throws Exception;

    User getUserByWindows(String id) throws Exception;

    boolean existsFacebookMessengerId(String messengerId) throws Exception;

    Integer registerEmailChange(int userId, String email) throws Exception;

    void validateEmailChange(int userId, int emailId) throws Exception;

    void verifyEmail(int userId, int emailId, boolean isVerified) throws Exception;

    List<UserEmail> getUserEmails(int userId) throws Exception;

    void updatePassword(int userId, String password) throws Exception;

    User getUserByEmail(String email) ;

    UserEntity getUserEntityById(int id, Locale locale) ;

    UserEntity getUserEntityByEmail(String email, Locale locale, Integer entityId);

    void registerResetToken(String email, String token) throws Exception;

    Boolean updateResetPassword(String token, String email, String password) throws Exception;

    List<OldPassword> getUserEntityPasswords(Integer entityUserId, Locale locale) throws Exception;

    Boolean isResetPasswordTokenUsed(String token) throws Exception;

    boolean mustCallEmailage(int userId, String email) throws Exception;

    List <EntityExtranetUserActionLog> getUserActionLog(Integer entityId, Integer entityUserId, Integer offset, Integer limit) throws Exception;

    List <EntityExtranetUser> getEntityExtranetUsers(Integer entityId,Integer offset, Integer limit) throws Exception;

    List<UserEntity> getUserEntityPendingsToActivateTfa(int entityId, Locale locale) ;

    void activateExtranetEntityUser(Integer entityUserId, Boolean valueToUpdate) throws Exception;

    PhoneNumber getPhoneNumberByUser(Integer userId) throws Exception;

    List<PhoneNumber> getAllPhoneNumbersByUser(Integer userId) throws Exception;

    void updatePhoneNumberJsInteraction(Integer phoneNumberId, String json) throws Exception;

    List<UserOfHierarchy> getUsersOfHierarchy() throws Exception;

    List<UserEntity> getEntityUsers(int entityId) throws Exception;

    JSONObject registerBDSUserEntity(Integer userEntityId, Integer entityId, String email, String personName, String firstSurname, String password, Integer entityUserIdFromEntity, Integer entityUserType, Integer entityUserIdProductor, Integer entityUserIdOrganizador, Integer[] countries, Integer channelId) throws Exception;

    UserEntity getEntityUserById(int entityUserId, Locale locale);

    List<Affiliator> getAffiliators() throws Exception;

    Affiliator getAffiliator(Integer affiliatorId) throws Exception;

    List<Product> getProductsFromUserAndEntity(int entityUserId, int entityId, Locale locale);

    void deleteAndCreateEntityUserRelationship(int entityUserId, int entityId, int productId);

    boolean existsOtherUsersByEmail(String email, int userId);

    boolean existsOtherUsersByPhoneNumber(String phoneNumber, String countryCode, int userId);

    void updateUserEmailJsToken(int userEmailId, List<UserEmail.EmailTokens> emailToken) throws Exception;

    UserEmail getUserEmailById(int userEmailId) throws Exception;

    void enableOrDisableEmailByUserEmail(int userId, int emailId, boolean isVerified) throws Exception;

    Boolean isVerifiedEmail(int email_id) throws Exception;

    Boolean isVerifiedPhoneNumber(int phone_number_id) throws Exception;

    Integer insertEmail(Integer userId, String email, boolean active, boolean verified);

    Integer insertPhoneNumber(Integer userId, String countryCode, String phoneNumber, String smsToken, boolean active, boolean verified);

    void updateActiveEmail(int emailId, boolean active) throws Exception;

    void updateActivePhoneNumber(int phoneNumberId, boolean active) throws Exception;

    void updateUserPhoneNumber(int userId, String phoneNumber, Boolean verified) throws Exception;

    void updateUserEmail(int userId, String email, Boolean verified) throws Exception;

    PhoneNumber getUserPhoneNumberById(int userPhoneNumberId) throws Exception;

    String registerPhoneNumberExcludeVerification(int userId, String countryCode, String phoneNumber) throws Exception;

    Integer registerEmailChangeExcludeVerification(int userId, String email) throws Exception;

    void removeUserFileFromTypeAndLoanId(int userId, int loanId, int userFileTypeId) throws Exception;
}
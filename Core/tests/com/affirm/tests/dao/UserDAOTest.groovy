package com.affirm.tests.dao

import com.affirm.common.dao.UserDAO
import com.affirm.common.model.security.OldPassword
import com.affirm.common.model.transactional.*
import com.affirm.tests.BaseConfig
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserDAOTest extends BaseConfig {

    @Autowired
    UserDAO userDAO

    static final String NAME = "Omar"
    static final String FIRST_SURNAME = "Ccoa"
    static final String LAST_SURNAME = "Heredia"
    static final Integer DOC_TYPE = 1
    static final String DOC_NUMBER = "45454545"
    static final Date BIRTHDAY = new Date()
    static final int USER_ID = 1633
    static final String COUNTRY_CODE = "51"
    static final String PHONE_NUMBER = "987456123"
    static final UserFacebook USER_FACEBOOK = getUserFacebook()
    static final int MESSENGER_ID = 12356
    static final String AUTH_TOKEN = "tokenx"
    static final Integer LOAN_APPLICATION_ID = 1697
    static final Integer USER_DOCUMENT_TYPE_ID = 13
    static final String FILENAME = "filename"
    static final Integer PERSON_ID = 2007
    static final String REFERAL_PHONE1 = "988888888"
    static final Integer RELATIVE1 = 10
    static final String REFERAL_PHONE2 = "977777777"
    static final Integer RELATIVE2 = 10
    static final String IP_ADDRESS = "127.0.0.1"
    static final String IP_COUNTRY_CODE = "127.0.0.1"
    static final String IP_COUNTRY_NAME = "PERU"
    static final String IP_REGION_CODE = "US-EAST-2"
    static final String IP_REGION_NAME = "OHIO"
    static final Double IP_LATITUDE = -12.087796
    static final Double IP_LONGITUDE = -77.013877
    static final Double NAV_LATITUDE = 20
    static final Double NAV_LONGITUD = 30
    static final String NAV_FORMATTED_ADDRESS = ""
    static final PersonLinkedIn PERSON_LINKEDIN = getPersonLinkedIn()
    static final int USER_FILE_ID = 200
    static final boolean VALIDATED = true
    static final int FILE_TYPE_ID = 1
    static final String EMAIL = "occoa@solven.pe"
    static final Integer LOAN_REASON_ID = 18
    static final Integer PRODUCT_ID = 11
    static final boolean IS_ACTIVE = true
    static final char NETWORK = '1'
    static final String ACCESS_TOKEN = ""
    static final String REFRESH_TOKEN = ""
    static final String NETWORK_ID = "1122"
    static final String FACEBOOK_ID = "9999"
    static final String LINKEDIN_ID = "8888"
    static final String RESULT_JSON = '["result"]'
    static final int NETWORK_TOKEN_ID = 13456
    static final String JSON_CONTACTS = '"[]"'
    static final String JSON_PROFILE = '"[]"'
    static final int EXTRANET_SESSION_ID = 654
    static final Date CURRENT_DATE = new Date()
    static final Date SIGNOUT_DATE = CURRENT_DATE
    static final Integer SECONDS = 3000
    static final String YAHOO_ID = "2211"
    static final String GOOGLE_ID = "2233"
    static final String WINDOWS_ID = "1133"
    static final int EMAIL_ID = 6644
    static final String PASSWORD = "pass"
    static final int USER_ENTITY_ID = 331
    static final Locale LOCALE = Locale.US
    static final int ENTITY_ID = 12
    static final Integer OFFSET = 10
    static final Integer LIMIT = 10
    static final Boolean VALUE_TO_UPDATE = true
    static final Integer PHONE_NUMBER_ID = 98745
    static final String JSON_PERSON_INTERACTION_IDS = '["6541"]'

    static UserFacebook getUserFacebook() {
        UserFacebook userFacebook = new UserFacebook()
        userFacebook.setFacebookId("6547889")

        return userFacebook
    }

    static PersonLinkedIn getPersonLinkedIn() {
        PersonLinkedIn personLinkedIn = new PersonLinkedIn()
        personLinkedIn.setLinkedinId("123456")

        return personLinkedIn
    }

    @Test
    void registerUserFromUserDAO() {
        User result = userDAO.registerUser(NAME, FIRST_SURNAME, LAST_SURNAME, DOC_TYPE, DOC_NUMBER, BIRTHDAY)
        Assert.assertNotNull(result)
    }

    @Test
    void registerPhoneNumberFromUserDAO() {
        String result = userDAO.registerPhoneNumber(USER_ID, COUNTRY_CODE, PHONE_NUMBER)
        Assert.assertNotNull(result)
    }

    @Test
    void getNewCellphoneAuthTokenFromUserDAO() {
        String result = userDAO.getNewCellphoneAuthToken(USER_ID, PHONE_NUMBER)
        Assert.assertNotNull(result)
    }

    @Test
    void registerFacebookFromUserDAO() {
        userDAO.registerFacebook(USER_ID, USER_FACEBOOK)
    }

    @Test
    void registerFacebookMessengerIdFromUserDAO() {
        boolean result = userDAO.registerFacebookMessengerId(USER_ID, MESSENGER_ID.toString())
        Assert.assertNotNull(result)
    }

    @Test
    void getUserFromUserDAO() {
        User result = userDAO.getUser(USER_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void validateCellphoneFromUserDAO() {
        JSONObject result = userDAO.validateCellphone(USER_ID, AUTH_TOKEN)
        Assert.assertNotNull(result)
    }

    @Test
    void getUserExternalInfoStateFromUserDAO() {
        List<ExternalUserInfoState> result = userDAO.getUserExternalInfoState(USER_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getLastPreliminaryCredibilityEvaluationFromUserDAO() {
        UserPreliminaryCredibilityEvaluation result = userDAO.getLastPreliminaryCredibilityEvaluation(USER_ID)
        Assert.assertNull(result)
    }

    @Test
    void getUserNamesExternalSourcesFromUserDAO() {
        JSONArray result = userDAO.getUserNamesExternalSources(DOC_TYPE, DOC_NUMBER)
        Assert.assertNull(result)
    }

    @Test
    void getUserFacebookFromUserDAO() {
        UserFacebook result = userDAO.getUserFacebook(USER_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void registerUserFileFromUserDAO() {
        Integer result = userDAO.registerUserFile(USER_ID, LOAN_APPLICATION_ID, USER_DOCUMENT_TYPE_ID, FILENAME)
        Assert.assertNotNull(result)
    }

    @Test
    void updateReferalPhonesFromUserDAO() {
        userDAO.updateReferalPhones(PERSON_ID, REFERAL_PHONE1, RELATIVE1, REFERAL_PHONE2, RELATIVE2)
    }

    @Test
    void updateLoanApplicationLocationFromUserDAO() {
        userDAO.updateLoanApplicationLocation(LOAN_APPLICATION_ID, IP_ADDRESS, IP_COUNTRY_CODE, IP_COUNTRY_NAME, IP_REGION_CODE,
                IP_REGION_NAME, IP_LATITUDE, IP_LONGITUDE, NAV_LATITUDE, NAV_LONGITUD, NAV_FORMATTED_ADDRESS)
    }

    @Test
    void registerLinkedinFromUserDAO() {
        userDAO.registerLinkedin(PERSON_ID, PERSON_LINKEDIN)
    }

    @Test
    void getLinkedinFromUserDAO() {
        PersonLinkedIn result = userDAO.getLinkedin(PERSON_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void validateUserFileFromUserDAO() {
        userDAO.validateUserFile(USER_FILE_ID, VALIDATED)
    }

    @Test
    void validateAllUserFileFromUserDAO() {
        userDAO.validateAllUserFile(USER_ID, LOAN_APPLICATION_ID)
    }

    @Test
    void updateUserFileTypeFromUserDAO() {
        userDAO.updateUserFileType(USER_FILE_ID, FILE_TYPE_ID)
    }

    @Test
    void registerEarlyAccessFromUserDAO() {
        userDAO.registerEarlyAccess(DOC_TYPE, DOC_NUMBER, EMAIL, LOAN_REASON_ID, PRODUCT_ID)
    }

    @Test
    void registerSubscritorFromUserDAO() {
        userDAO.registerSubscritor(EMAIL, IS_ACTIVE)
    }

    @Test
    void registerNetworkAccessTokenFromUserDAO() {
        UserNetworkToken result = userDAO.registerNetworkAccessToken(USER_ID, NETWORK, ACCESS_TOKEN, REFRESH_TOKEN,
                EMAIL, NETWORK_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getUserIdByPersonIdFromUserDAO() {
        Integer result = userDAO.getUserIdByPersonId(PERSON_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getUserFileFromUserDAO() {
        UserFile result = userDAO.getUserFile(USER_FILE_ID)
        Assert.assertNull(result)
    }

    @Test
    void getUserByDocumentFromUserDAO() {
        User result = userDAO.getUserByDocument(DOC_TYPE, DOC_NUMBER)
        Assert.assertNotNull(result)
    }

    @Test
    void getUserByFacebookIdFromUserDAO() {
        User result = userDAO.getUserByFacebookId(FACEBOOK_ID)
        Assert.assertNull(result)
    }

    @Test
    void getUserByLinkedinIdFromUserDAO() {
        User result = userDAO.getUserByLinkedinId(LINKEDIN_ID)
        Assert.assertNull(result)
    }

    @Test
    void getUserByFacebookMessengerIdFromUserDAO() {
        User result = userDAO.getUserByFacebookMessengerId(MESSENGER_ID.toString())
        Assert.assertNotNull(result)
    }

    @Test
    void registerEmailAgeFromUserDAO() {
        userDAO.registerEmailAge(USER_ID, RESULT_JSON)
    }

    @Test
    void updateNetworkContactsFromUserDAO() {
        userDAO.updateNetworkContacts(NETWORK_TOKEN_ID, JSON_CONTACTS)
    }

    @Test
    void updateNetworkProfileFromUserDAO() {
        userDAO.updateNetworkProfile(NETWORK_TOKEN_ID, JSON_PROFILE)
    }

    @Test
    void getUserNetworkTokenByProviderFromUserDAO() {
        UserNetworkToken result = userDAO.getUserNetworkTokenByProvider(USER_ID, NETWORK)
        Assert.assertNotNull(result)
    }

    @Test
    void registerSessionLogoutFromUserDAO() {
        userDAO.registerSessionLogout(EXTRANET_SESSION_ID, SIGNOUT_DATE)
    }

    @Test
    void getLastSessionLogFromUserDAO() {
        UserSessionLog result = userDAO.getLastSessionLog(USER_ID)
        Assert.assertNull(result)
    }

    @Test
    void registerNoAuthExtranetLinkExpirationFromUserDAO() {
        userDAO.registerNoAuthExtranetLinkExpiration(USER_ID, SECONDS)
    }

    @Test
    void getUserByYahooFromUserDAO() {
        User result = userDAO.getUserByYahoo(YAHOO_ID)
        Assert.assertNull(result)
    }

    @Test
    void getUserByGoogleFromUserDAO() {
        User result = userDAO.getUserByGoogle(GOOGLE_ID)
        Assert.assertNull(result)
    }

    @Test
    void getUserByWindowsFromUserDAO() {
        User result = userDAO.getUserByWindows(WINDOWS_ID)
        Assert.assertNull(result)
    }

    @Test
    void existsFacebookMessengerIdFromUserDAO() {
        boolean result = userDAO.existsFacebookMessengerId(MESSENGER_ID.toString())
        Assert.assertNotNull(result)
    }

    @Test
    void registerEmailChangeFromUserDAO() {
        Integer result = userDAO.registerEmailChange(USER_ID, EMAIL)
        Assert.assertNotNull(result)
    }

    @Test
    void validateEmailChangeFromUserDAO() {
        userDAO.validateEmailChange(USER_ID, EMAIL_ID)
    }

    @Test
    void getUserEmailsFromUserDAO() {
        List<UserEmail> result = userDAO.getUserEmails(USER_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void updatePasswordFromUserDAO() {
        userDAO.updatePassword(USER_ID, PASSWORD)
    }

    @Test
    void getUserByEmailFromUserDAO() {
        User result = userDAO.getUserByEmail(EMAIL)
        Assert.assertNotNull(result)
    }

    @Test
    void getUserEntityByIdFromUserDAO() {
        UserEntity result = userDAO.getUserEntityById(USER_ENTITY_ID, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void getUserEntityByEmailFromUserDAO() {
        UserEntity result = userDAO.getUserEntityByEmail(EMAIL, LOCALE, null)
        Assert.assertNull(result)
    }

    @Test
    void registerResetTokenFromUserDAO() {
        userDAO.registerResetToken(EMAIL, AUTH_TOKEN)
    }

    @Test
    void updateResetPasswordFromUserDAO() {
        Boolean result = userDAO.updateResetPassword(AUTH_TOKEN, EMAIL, PASSWORD)
        Assert.assertNotNull(result)
    }

    @Test
    void getUserEntityPasswordsFromUserDAO() {
        List<OldPassword> result = userDAO.getUserEntityPasswords(USER_ENTITY_ID, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void isResetPasswordTokenUsedFromUserDAO() {
        Boolean result = userDAO.isResetPasswordTokenUsed(AUTH_TOKEN)
        Assert.assertNotNull(result)
    }

    @Test
    void mustCallEmailageFromUserDAO() {
        boolean result = userDAO.mustCallEmailage(USER_ID, EMAIL)
        Assert.assertNotNull(result)
    }

    @Test
    void getUserEntityPendingsToActivateTfaFromUserDAO() {
        List<UserEntity> result = userDAO.getUserEntityPendingsToActivateTfa(ENTITY_ID, LOCALE)
        Assert.assertNotNull(result)
    }

    @Test
    void getUserActionLogFromUserDAO() {
        List<EntityExtranetUserActionLog> result = userDAO.getUserActionLog(ENTITY_ID, USER_ENTITY_ID, OFFSET, LIMIT)
        Assert.assertNull(result)
    }

    @Test
    void getEntityExtranetUsersFromUserDAO() {
        List<EntityExtranetUser> result = userDAO.getEntityExtranetUsers(ENTITY_ID, OFFSET, LIMIT)
        Assert.assertNull(result)
    }

    @Test
    void activateExtranetEntityUserFromUserDAO() {
        userDAO.activateExtranetEntityUser(USER_ENTITY_ID, VALUE_TO_UPDATE)
    }

    @Test
    void getPhoneNumberByUserFromUserDAO() {
        PhoneNumber result = userDAO.getPhoneNumberByUser(USER_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void updatePhoneNumberJsInteractionFromUserDAO() {
        userDAO.updatePhoneNumberJsInteraction(PHONE_NUMBER_ID, JSON_PERSON_INTERACTION_IDS)
    }

}

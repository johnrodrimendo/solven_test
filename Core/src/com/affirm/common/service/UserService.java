/**
 *
 */
package com.affirm.common.service;

import com.affirm.common.model.transactional.*;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface UserService {

    User getOrRegisterUser(int docType, String docNumber, Date birthday, String name, String firstSurname) throws Exception;

    public boolean validateUserAuthenticity(String userName, String userFirstSurname, String userLastSurnam,
                                            Integer userDocType, String userDocNumber) throws Exception;

    /**
     * Get the location of the ip and registers in the DB
     *
     * @param ip
     * @param loanApplicationId
     * @throws Exception
     */
    void registerIpUbication(String ip, int loanApplicationId) throws Exception;

    /**
     * Get the reverse geocodification of Google Map and registers in the DB
     *
     * @param loanApplicationId
     * @param latitude
     * @param longitude
     * @throws Exception
     */
    void registerBrowserUbication(int loanApplicationId, Double latitude, Double longitude) throws Exception;

    List<UserFile> getUserFileByType(List<UserFile> files, Integer... userFileTYpe) throws Exception;

//    void callUserExternalDataBots(int userId, int docType, String docNumber, Date birthday, String plate) throws Exception;

    List<Integer> registerUserFiles(MultipartFile[] files, int loanApplicationId, int userId, int userDocumentTypeId)
            throws Exception;

    List<Integer> registerUserFilesBase64(String[] files, int loanApplicationId, int userId, int userDocumentTypeId, LoanApplication loanApplication)
            throws Exception;

    String generateUSerToken(int userId) throws Exception;

    boolean validateUserPasswordIfExists(User user, String email, String password) throws Exception;

    UserEntity getUserEntityById(int id, Locale locale) throws Exception;

    User getUser(Integer userId);

    void sendAuthTokenSms(int userId, int personId, String countryCode, String phoneNumber, String personName, Integer loanApplicationId, String entityShort, int countryId) throws Exception;

    String validateSmsAuthToken(int userId, String authToken, Locale locale) throws Exception;

    JSONObject validateSmsContractToken(int loanOfferId, String authToken) throws Exception;

    void registerUserFileByte(byte[] file, int loanApplicationId, int userId, int userDocumentTypeId, String extension)
            throws Exception;

    void sendAuthTokenInteractionWithProvider(int userId, int personId, String countryCode, String phoneNumber, String personName, Integer loanApplicationId, String entityShort, int countryId, int interactionTypeId, Integer interactionProviderId) throws Exception;

    void sendCallRequest(Integer loanApplicationId, String countryCode, String phoneNumber) throws Exception;

    void sendContractToken(String pin, User user, Integer loanApplicationId, Integer personId, String personName, Integer countryId, String entityShortName, int interactionTypeId, Integer interactionProviderId) throws Exception;

    PhoneNumber getUserPhoneNumberToVerify(List<PhoneNumber> userPhoneNumbers);

    String getProducersByIds(Object producerId) throws Exception;

    String getLoanStatusesByIds(Object userId) throws Exception;

    String getFDLMLoanStatusesByIds(Object userId) throws Exception;

    String formattedInternalStatuses(Object statusId) throws Exception;

    String getCreditStatusesByIds(Object statusId) throws Exception;

    List<UserFile> getUserFileByTypes(List<UserFile> files, Integer... userFileTYpe);

    void createEmailPassword(String email, Integer userId) throws Exception;

    void activateUserEmailPhoneNumber(int phoneNumberId, int emailId, int userId) throws Exception;

    void createEmailPasswordExcludeValidation(String email, Integer userId) throws Exception;

    String generatePresignedVideoUrl(Integer loanId) throws Exception;

    String generatePresignedVideoUrlToUserFolder(Integer loanId, Integer userId, String filename) throws Exception;

    String registerRecordingFile(Integer loanId, Integer userId, String filename) throws Exception;

    void registerIpUbicationFromMaxMind(String ip, int loanApplicationId) throws Exception;
}

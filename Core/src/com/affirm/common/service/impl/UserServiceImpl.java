/**
 *
 */
package com.affirm.common.service.impl;


import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.StatusExtranetReport;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.*;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDAO userDao;

    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private FileService fileService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ErrorService errorService;

    private static final String LOAN_VIDEO_FOLDER = "loan_application_recording/";

    @Override
    public User getOrRegisterUser(int docType, String docNumber, Date birthday, String name, String firstSurname) throws Exception {

        // Return user if exist
        User user = userDao.getUserByDocument(docType, docNumber);
        if (user != null)
            return user;

        // Otherwise, create it
        return userDao.registerUser(name, firstSurname, null, docType, docNumber, birthday);
    }

    @Override
    public boolean validateUserAuthenticity(String userName, String userFirstSurname, String userLastSurnam,
                                            Integer userDocType, String userDocNumber) throws Exception {
        JSONArray array = userDao.getUserNamesExternalSources(userDocType, userDocNumber);
        if (array != null) {
            String fullName = Util.removeWhiteSpaces(Util.replaceSpecialChars(userName)).toLowerCase() + " "
                    + Util.removeWhiteSpaces(Util.replaceSpecialChars(userFirstSurname)).toLowerCase() + " "
                    + Util.removeWhiteSpaces(Util.replaceSpecialChars(userLastSurnam)).toLowerCase();
            for (int i = 0; i < array.length(); i++) {
                JSONObject externalUser = array.getJSONObject(i);
                String firstName = Util
                        .removeWhiteSpaces(
                                Util.replaceSpecialChars(JsonUtil.getStringFromJson(externalUser, "first_name", "")))
                        .toLowerCase();
                String secondName = Util
                        .removeWhiteSpaces(
                                Util.replaceSpecialChars(JsonUtil.getStringFromJson(externalUser, "second_name", "")))
                        .toLowerCase();
                String firstSurname = Util
                        .removeWhiteSpaces(
                                Util.replaceSpecialChars(JsonUtil.getStringFromJson(externalUser, "first_surname", "")))
                        .toLowerCase();
                String lastSurname = Util
                        .removeWhiteSpaces(
                                Util.replaceSpecialChars(JsonUtil.getStringFromJson(externalUser, "last_surname", "")))
                        .toLowerCase();
                String marriedSurname = Util
                        .removeWhiteSpaces(Util
                                .replaceSpecialChars(JsonUtil.getStringFromJson(externalUser, "married_surname", "")))
                        .toLowerCase();

                if (!(fullName
                        .equals(firstName + " " + secondName + " " + firstSurname + " " + lastSurname + " de "
                                + marriedSurname)
                        || fullName.equals(firstName + " " + firstSurname + " " + lastSurname + " de " + marriedSurname)
                        || fullName
                        .equals(secondName + " " + firstSurname + " " + lastSurname + " de " + marriedSurname)
                        || fullName.equals(firstName + " " + secondName + " " + firstSurname + " " + lastSurname + " "
                        + marriedSurname)
                        || fullName.equals(firstName + " " + firstSurname + " " + lastSurname + " " + marriedSurname)
                        || fullName.equals(secondName + " " + firstSurname + " " + lastSurname + " " + marriedSurname)
                        || fullName.equals(firstName + " " + secondName + " " + firstSurname + " de " + marriedSurname)
                        || fullName.equals(firstName + " " + firstSurname + " de " + marriedSurname)
                        || fullName.equals(secondName + " " + firstSurname + " de " + marriedSurname)
                        || fullName.equals(firstName + " " + secondName + " " + firstSurname + " " + marriedSurname)
                        || fullName.equals(firstName + " " + firstSurname + " " + marriedSurname)
                        || fullName.equals(secondName + " " + firstSurname + " " + marriedSurname)
                        || fullName.equals(firstName + " " + secondName + " " + firstSurname + " " + lastSurname)
                        || fullName.equals(firstName + " " + firstSurname + " " + lastSurname)
                        || fullName.equals(secondName + " " + firstSurname + " " + lastSurname))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void registerIpUbication(String ip, int loanApplicationId) throws Exception {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject jsonResponse = utilService.getIpGeolocation(ip, UtilServiceImpl.GeoIpServices.FREGEOIP);// new JSONObject(res);
                    userDao.updateLoanApplicationLocation(loanApplicationId, ip,
                            JsonUtil.getStringFromJson(jsonResponse, "country_code", null),
                            JsonUtil.getStringFromJson(jsonResponse, "country_name", null),
                            JsonUtil.getStringFromJson(jsonResponse, "region_code", null),
                            JsonUtil.getStringFromJson(jsonResponse, "region_name", null),
                            JsonUtil.getDoubleFromJson(jsonResponse, "latitude", null),
                            JsonUtil.getDoubleFromJson(jsonResponse, "longitude", null), null, null, null);
                } catch (Exception ex) {
                    logger.error("Error while updating geolocation LoanApp", ex);
                }

            }
        }).start();
    }

    @Override
    public void registerIpUbicationFromMaxMind(String ip, int loanApplicationId) throws Exception {
        try {
            JSONObject jsonResponse = utilService.getIpGeolocationMaxMind(loanApplicationId, ip, UtilServiceImpl.GeoIpServices.MAXMIND_CITY);
            JSONObject country = jsonResponse == null ? new JSONObject() : jsonResponse.optJSONObject("country");
            if(JsonUtil.getStringFromJson(country, "iso_code", null) == null){
                jsonResponse = utilService.getIpGeolocationMaxMind(loanApplicationId, ip, UtilServiceImpl.GeoIpServices.MAXMIND_CITY);
                country = jsonResponse == null ? new JSONObject() : jsonResponse.optJSONObject("country");
                if(JsonUtil.getStringFromJson(country, "iso_code", null) == null)  errorService.sendErrorCriticSlack(String.format("MAXMIND_CITY not found IP: %s, Loan: %s", ip, loanApplicationId));
            }
            userDao.updateLoanApplicationLocation(loanApplicationId, ip,
                    JsonUtil.getStringFromJson(country, "iso_code", null),
                    null,
                    null,
                    null,
                    null,
                    null, null, null, null);
            if(JsonUtil.getStringFromJson(country, "iso_code", null) != null){
                JSONObject traits = jsonResponse == null ? new JSONObject() : jsonResponse.optJSONObject("traits");
                if(traits != null && JsonUtil.getStringFromJson(traits, "organization", null) != null){
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId,Configuration.getDefaultLocale());
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.GEOLOCATION_IP_ORGANIZATION.getKey(),  JsonUtil.getStringFromJson(traits, "organization", null));
                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                }
            }
        } catch (Exception ex) {
            logger.error("Error while updating geolocation LoanApp", ex);
            errorService.onError(ex);
        }
    }

    @Override
    public void registerBrowserUbication(int loanApplicationId, Double latitude, Double longitude) throws Exception {
        loanApplicationDao.updateConsentNavGeolocation(loanApplicationId, true);
        utilService.processGoogleGeodecoding(latitude, longitude, null, args -> {
            if (args != null && args.length > 0)
                userDao.updateLoanApplicationLocation(loanApplicationId, null, null, null, null, null, null, null,
                        latitude, longitude, args[0]);
            return null;
        });
    }

    @Override
    public List<UserFile> getUserFileByType(List<UserFile> files, Integer... userFileTYpe) throws Exception {
        if (files == null || userFileTYpe == null)
            return null;

        return files.stream().filter(f -> Arrays.asList(userFileTYpe).contains(f.getFileType().getId())).collect(Collectors.toList());
    }

    /*@Override
    public void callUserExternalDataBots(int userId, int docType, String docNumber, Date birthday, String plate) throws Exception {
        // Get the bots to execute
        List<ExternalUserInfoState> infoStates = userDao.getUserExternalInfoState(userId);
        Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), userDao.getUser(userId).getPersonId(), false);
        webscrapperService.setCountry(person.getCountry());

        if (infoStates != null) {
            for (ExternalUserInfoState state : infoStates) {
                if (!state.getResult()) {
                    switch (state.getBotId()) {
                        case Bot.SUNAT_BOT:
                            webscrapperService.callSunatDniBot(docNumber, userId);
                            break;
//                    case Bot.RENIEC_BOT:
//                        webscrapperService.callReniecBot(docNumber, userId);
//                        break;
//                    case Bot.ESSALUD_BOT:
//                        webscrapperService.callEssaludBot(docType, docNumber, userId);
//                        break;
//                    case Bot.REDAM_BOT:
//                        webscrapperService.callRedamBot(docType, docNumber, userId);
//                        break;
                        case Bot.CLARO:
                            webscrapperService.callClaroBot(docType, docNumber, userId);
                            break;
                        case Bot.MOVISTAR:
                            webscrapperService.callMovistarBot(docType, docNumber, userId);
                            break;
                        case Bot.BITEL:
                            webscrapperService.callBitelBot(docType, docNumber, userId);
                            break;
                        case Bot.ENTEL:
                            webscrapperService.callEntelBot(docType, docNumber, userId);
                            break;
//                    case Bot.SAT:
//                        webscrapperService.callSatBot(docType, docNumber, userId);
//                        break;
//                    case Bot.SIS:
//                        webscrapperService.callSisBot(docType, docNumber, userId);
//                        break;
                        case Bot.MIGRACIONES:
                            if (docType == IdentityDocumentType.CE)
                                webscrapperService.callMigracionesBot(docNumber, birthday, userId);
                            break;
                        case Bot.VIRGIN:
                            webscrapperService.callVirginBot(docType, docNumber, userId);
                            break;
                        case Bot.ONPE:
                            webscrapperService.callONPEBot(docNumber, userId);
                            break;
//                    case Bot.SAT_PLATE:
//                        webscrapperService.callSatPlateBot(userId, plate);
                    }
                }
            }
        }
    }*/

    @Override
    public List<Integer> registerUserFiles(MultipartFile[] files, int loanApplicationId, int userId, int userDocumentTypeId)
            throws Exception {

        if (files == null)
            return null;

        // Validate the size and the type
        for (int i = 0; i < files.length; i++) {
            if (!Arrays.asList(Configuration.ALLOW_USER_FILE_TYPE).contains(files[i].getContentType()) ||
                    files[i].getSize() > Configuration.MAX_UPLOAD_FILE_SIZE()) {
                throw new Exception("El archivo excede el tamaño máximo");
            }
        }

        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            // Save the file in s3
            String fileName = fileService.writeUserFile(files[i].getBytes(), userId, files[i].getOriginalFilename(), false);

            // Register the file
            ids.add(userDao.registerUserFile(userId, loanApplicationId, userDocumentTypeId, fileName));
        }
        return ids;
    }

    @Override
    public List<Integer> registerUserFilesBase64(String[] files, int loanApplicationId, int userId, int userDocumentTypeId, LoanApplication loanApplication)
            throws Exception {

        if (files == null)
            return null;

        boolean validateImageDimensions = false;

        if (loanApplication == null) {
            loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        }

        EntityProductParams entityProductParams = null;
        if (loanApplication.getSelectedEntityProductParameterId() != null) {
            entityProductParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());
        } else {
            // If its validacion de identidad, get the entity product params from the pre evaluations
            if (loanApplication.getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD) {
                List<LoanApplicationPreliminaryEvaluation> preevals = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale());
                Integer entityProductParamId = preevals.stream().filter(p -> p.getStatus() == 'S' && p.getApproved() != null && p.getApproved()).map(p -> p.getEntityProductParameterId()).findFirst().orElse(null);
                if (entityProductParamId != null)
                    entityProductParams = catalogService.getEntityProductParamById(entityProductParamId);
            }
        }

        if (entityProductParams != null && entityProductParams.getEntityProductParamIdentityValidationConfig() != null) {
            if ((entityProductParams.getEntityProductParamIdentityValidationConfig().getRunMati() != null && entityProductParams.getEntityProductParamIdentityValidationConfig().getRunMati()) || (entityProductParams.getEntityProductParamIdentityValidationConfig().getRunRekognitionReniec() != null && entityProductParams.getEntityProductParamIdentityValidationConfig().getRunRekognitionReniec()))
                validateImageDimensions = true;
        }

        List<Integer> ids = new ArrayList<>();
        boolean allFilesAreValid = true;

        for (int i = 0; i < files.length; i = i + 2) {
            byte[] imageByte = DatatypeConverter.parseBase64Binary(files[i + 1]);
            // read the filetype

            String extension = files[0].split("/")[1].split(";")[0];
            String base64String = files[i + 1];

            // read the image data
            ImageUtil iu = new ImageUtil();
            String newbase64String = iu.processFile(base64String, extension, validateImageDimensions);
/*            if(newbase64String == null && validateImageDimensions){
                allFilesAreValid = false;
                continue;
            }*/

            imageByte = DatatypeConverter.parseBase64Binary(newbase64String);

            // Validate the size and the type
            if (imageByte.length > Configuration.MAX_UPLOAD_FILE_SIZE()) {
                throw new Exception("El archivo excede el tamaño máximo");
            }

            // Save the file in s3

            String fileName = fileService.writeUserFile(imageByte, userId, catalogService.getUserFileType(userDocumentTypeId).getType() + "." + extension);

            // Register the file
            ids.add(userDao.registerUserFile(userId, loanApplicationId, userDocumentTypeId, fileName));
        }


        return ids;
    }

    @Override
    public void registerUserFileByte(byte[] file, int loanApplicationId, int userId, int userDocumentTypeId, String extension)
            throws Exception {

        if (file != null) {

            if (file.length > Configuration.MAX_UPLOAD_FILE_SIZE()) {
                throw new Exception("El archivo excede el tamaño máximo");
            }
            // Save the file in s3

            String fileName = fileService.writeUserFile(file, userId, catalogService.getUserFileType(userDocumentTypeId).getType() + "." + extension);

            // Register the file
            userDao.registerUserFile(userId, loanApplicationId, userDocumentTypeId, fileName);
        }

    }

    @Override
    public String generateUSerToken(int userId) throws Exception {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("user", userId);
        return CryptoUtil.encrypt(jsonParam.toString());
    }

    @Override
    public boolean validateUserPasswordIfExists(User user, String email, String password) throws Exception {
        if (user.getEmail() != null && user.getPassword() != null) {
            if (!user.getEmail().toLowerCase().equals(email) || !CryptoUtil.validatePassword(password, user.getPassword())) {
                return false;
            }
        }
        return true;
    }

    public UserEntity getUserEntityById(int id, Locale locale) throws Exception {
        return userDao.getUserEntityById(id, locale);
    }

    public String tpHost() {
        return Configuration.hostEnvIsProduction() ? "https://mpsnare.iesnare.com" : "https://ci-mpsnare.iovation.com";
    }

    @Override
    public User getUser(Integer userId) {
        return userDao.getUser(userId);
    }

    @Override
    public void sendAuthTokenSms(int userId, int personId, String countryCode, String phoneNumber, String personName, Integer loanApplicationId, String entityShort, int countryId) throws Exception {
        String authToken = userDao.getNewCellphoneAuthToken(userId, phoneNumber);

        if (Configuration.IS_AUTHORIZED_PHONENUMBER(phoneNumber)) {
            JSONObject jsonVars = new JSONObject();
            jsonVars.put("AUTH_SMS_TOKEN", authToken);
            if (personName == null) {
                jsonVars.put("CLIENT_NAME", personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), personId, false).getFirstName());
            } else {
                jsonVars.put("CLIENT_NAME", personName);
            }
            if (entityShort == null)
                jsonVars.put("ENTITY_SHORT", catalogService.getEntity(Entity.AFFIRM).getShortName());
            else
                jsonVars.put("ENTITY_SHORT", entityShort);

            PersonInteraction interaction = new PersonInteraction();
            interaction.setLoanApplicationId(loanApplicationId);
            interaction.setPersonId(personId);
            interaction.setDestination("+" + countryCode + phoneNumber);
            interaction.setInteractionType(catalogService.getInteractionType(InteractionType.SMS));
            interaction.setInteractionProvider(catalogService.getInteractionProvider(InteractionProvider.INFOBIP));
            interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.REGISTRO_AUTHTOKEN_SMS, countryId));

            interactionService.sendPersonInteraction(interaction, jsonVars, null);
        }
    }

    @Override
    public String validateSmsAuthToken(int userId, String authToken, Locale locale) throws Exception {
        if (Configuration.SMS_AUTHORIZATION_ACTIVATED()) {
            JSONObject jsonResponse = userDao.validateCellphone(userId, authToken);
            if (!jsonResponse.getBoolean("result")) {
                return messageSource.getMessage(jsonResponse.getString("message"), null, locale);
            }
        } else {
            // If not SMS auth activated, then activated by getting a new authToken
            User user = userDao.getUser(userId);
            List<PhoneNumber> userPhoneNumbers = userDao.getAllPhoneNumbersByUser(user.getId());
            PhoneNumber userPhoneNumber = getUserPhoneNumberToVerify(userPhoneNumbers);
            String newAuthToken = userDao.getNewCellphoneAuthToken(userId, userPhoneNumber.getPhoneNumber());
            userDao.validateCellphone(userId, newAuthToken);
        }
        return null;
    }

    @Override
    public JSONObject validateSmsContractToken(int loanOfferId, String authToken) throws Exception {
        logger.debug(String.format("OfferId %s, Token: %s", String.valueOf(loanOfferId), authToken));
        if (Configuration.SMS_AUTHORIZATION_ACTIVATED()) {
            return loanApplicationDao.validateSignaturePin(loanOfferId, authToken);
        } else {
            return new JSONObject().put(LoanOffer.SIGNATURE_PIN_VALIDATED_KEY, true);
        }
    }

    @Override
    public void sendAuthTokenInteractionWithProvider(int userId, int personId, String countryCode, String phoneNumber, String personName, Integer loanApplicationId, String entityShort, int countryId, int interactionTypeId, Integer interactionProviderId) throws Exception {
        String authToken = userDao.getNewCellphoneAuthToken(userId, phoneNumber);

        if (Configuration.IS_AUTHORIZED_PHONENUMBER(phoneNumber)) {
            JSONObject jsonVars = new JSONObject();
            if (interactionTypeId == InteractionType.CALL) {
                jsonVars.put("AUTH_SMS_TOKEN", String.join(" ", authToken.split("")));
            } else {
                jsonVars.put("AUTH_SMS_TOKEN", authToken);
            }

            if (personName == null) {
                jsonVars.put("CLIENT_NAME", personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), personId, false).getFirstName());
            } else {
                jsonVars.put("CLIENT_NAME", personName);
            }
            if (entityShort == null)
                jsonVars.put("ENTITY_SHORT", catalogService.getEntity(Entity.AFFIRM).getShortName());
            else
                jsonVars.put("ENTITY_SHORT", entityShort);

            PersonInteraction interaction = new PersonInteraction();
            interaction.setLoanApplicationId(loanApplicationId);
            interaction.setPersonId(personId);
            interaction.setDestination("+" + countryCode + phoneNumber);
            interaction.setInteractionType(catalogService.getInteractionType(interactionTypeId));
            interaction.setInteractionProvider(interactionProviderId == null ? null : catalogService.getInteractionProvider(interactionProviderId));
            if (interactionTypeId == InteractionType.SMS) {
                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.REGISTRO_AUTHTOKEN_SMS, countryId));
            } else if (interactionTypeId == InteractionType.CALL) {
                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.REGISTRO_AUTHTOKEN_CALL, countryId));
            }

            interactionService.sendPersonInteraction(interaction, jsonVars, null);

            // Add to the pin interactions
            List<PhoneNumber> userPhoneNumbers = userDao.getAllPhoneNumbersByUser(userId);
            PhoneNumber userPhoneNumber = getUserPhoneNumberToVerify(userPhoneNumbers);
            if (interactionTypeId == InteractionType.SMS) {
                userPhoneNumber.addSmsInteractionSend(interaction.getId(), new Date());
            } else if (interactionTypeId == InteractionType.CALL) {
                userPhoneNumber.addCallInteractionSend(interaction.getId(), new Date());
            }
            userDao.updatePhoneNumberJsInteraction(userPhoneNumber.getPhoneNumberId(), new Gson().toJson(userPhoneNumber.getPinInteractions()));
        }
    }

    @Override
    public void sendCallRequest(Integer loanApplicationId, String countryCode, String phoneNumber) throws Exception {
        interactionService.registerCallRequestPersonInteraction(loanApplicationId, countryCode, phoneNumber);
    }

    @Override
    public void sendContractToken(String pin, User user, Integer loanApplicationId, Integer personId, String personName, Integer countryId, String entityShortName, int interactionTypeId, Integer interactionProviderId) throws Exception {
        if (Configuration.SMS_AUTHORIZATION_ACTIVATED()) {
            JSONObject jsonVars = new JSONObject();
            if (interactionTypeId == InteractionType.CALL) {
                jsonVars.put("AUTH_SMS_TOKEN", String.join(" ", pin.split("")));
            } else {
                jsonVars.put("AUTH_SMS_TOKEN", pin);
            }

            jsonVars.put("CLIENT_NAME", personName != null ? personName : "");

            if (entityShortName == null)
                jsonVars.put("ENTITY_SHORT", catalogService.getEntity(Entity.AFFIRM).getShortName());
            else
                jsonVars.put("ENTITY_SHORT", entityShortName);

            PersonInteraction interaction = new PersonInteraction();
            interaction.setLoanApplicationId(loanApplicationId);
            interaction.setPersonId(personId);
            interaction.setDestination("+" + user.getCountryCode() + user.getPhoneNumber());
            interaction.setInteractionType(catalogService.getInteractionType(interactionTypeId));
            interaction.setInteractionProvider(catalogService.getInteractionProvider(interactionProviderId));
            if (interactionTypeId == InteractionType.SMS) {
                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.REGISTRO_CONTRACTTOKEN_SMS, countryId));
            } else if (interactionTypeId == InteractionType.CALL) {
                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.REGISTRO_CONTRACTTOKEN_CALL, countryId));
            }

            interactionService.sendPersonInteraction(interaction, jsonVars, null);
        }
    }

    @Override
    public PhoneNumber getUserPhoneNumberToVerify(List<PhoneNumber> userPhoneNumbers) {
        return userPhoneNumbers.stream().filter(PhoneNumber::isToVerify).findFirst().orElse(null);
    }

    @Override
    public String getProducersByIds(Object userId) throws Exception {
        String producersConcat = "";
        String[] producersArray = userId.toString().split("\\[");
        String[] producersArray2 = producersArray[1].split("\\]");
        if (producersArray2.length > 0) {
            String[] producers = producersArray2[0].split(",");
            String separator = ",";
            for (int i = 0; i < producers.length; i++) {
                UserEntity userEntity = userDao.getUserEntityById(Integer.parseInt(producers[i]), Configuration.getDefaultLocale());

                if (userEntity != null) {
                    producersConcat = producersConcat + userEntity.getFirstSurname() + " " + userEntity.getName();
                }

                if (i < producers.length - 1)
                    producersConcat = producersConcat + separator + " ";
            }
        }
        return producersConcat;
    }

    @Override
    public String getLoanStatusesByIds(Object statusId) throws Exception {
        String statusConcat = getStatusesByIds(statusId, StatusExtranetReport.getLoanStatuses());
        return statusConcat;
    }

    @Override
    public String getFDLMLoanStatusesByIds(Object statusId) throws Exception {
        return getStatusesByIds(statusId, StatusExtranetReport.getLoanStatusesFDLM());
    }

    @Override
    public String formattedInternalStatuses(Object statusId) throws Exception {
        List<String> statuses = new Gson().fromJson(statusId.toString(), new TypeToken<ArrayList<String>>() {
        }.getType());
        String statusConcat = "";
        if (statuses != null) {
            for (String status : statuses) {
                statusConcat += status + ", ";
            }

            statusConcat = statusConcat.substring(0, statusConcat.length() - 2);
        }
        return statusConcat;
    }

    @Override
    public String getCreditStatusesByIds(Object statusId) throws Exception {
        String statusConcat = getStatusesByIds(statusId, StatusExtranetReport.getCreditStatuses());
        return statusConcat;
    }

    private String getStatusesByIds(Object statusId, Map<Integer, String> statuses) throws Exception {
        String statusConcat = "";
        String[] statusArray = statusId.toString().split("\\[");
        String[] statusArray2 = statusArray[1].split("\\]");
        if (statusArray2.length > 0) {
            String[] status = statusArray2[0].split(",");
            String separator = ",";
            for (int i = 0; i < status.length; i++) {

                statusConcat += statuses.get(Integer.parseInt(status[i]));

                if (i < status.length - 1)
                    statusConcat = statusConcat + separator + " ";
            }
        }
        return statusConcat;
    }

    @Override
    public List<UserFile> getUserFileByTypes(List<UserFile> files, Integer... userFileTYpe) {
        if (files == null || userFileTYpe == null)
            return null;

        return files.stream().filter(f -> Arrays.asList(userFileTYpe).contains(f.getFileType().getId())).collect(Collectors.toList());
    }

    @Override
    public void createEmailPassword(String email, Integer userId) throws Exception {
        User user = userDao.getUser(userId);
        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(email)) {
            throw new SqlErrorMessageException(null, "El  email no coincide con el registrado");
        } else if (user.getEmail() == null) {
            int emailId = userDao.registerEmailChange(userId, email.toLowerCase());
            userDao.validateEmailChange(userId, emailId);
        }
    }

    @Override
    public void activateUserEmailPhoneNumber(int phoneNumberId, int emailId, int userId) throws Exception {
        UserEmail userEmail = userDao.getUserEmailById(emailId);
        userDao.updateActiveEmail(emailId, true);
        userDao.updateUserEmail(userId, userEmail.getEmail(), true);

        PhoneNumber phoneNumber = userDao.getUserPhoneNumberById(phoneNumberId);
        userDao.updateActivePhoneNumber(phoneNumberId, true);
        userDao.updateUserPhoneNumber(userId, phoneNumber.getPhoneNumber(), true);
    }

    @Override
    public void createEmailPasswordExcludeValidation(String email, Integer userId) throws Exception {
        User user = userDao.getUser(userId);
        if (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(email)) {
            int emailId = userDao.registerEmailChangeExcludeVerification(userId, email.toLowerCase());
            userDao.validateEmailChange(userId, emailId);
        }
    }

    @Override
    public String generatePresignedVideoUrl(Integer loanId) throws Exception {
        return fileService.generatePresignedUrl(LOAN_VIDEO_FOLDER, "binary/octet-stream", String.format("%s_%s.webm", new Date().getTime(), loanId), 1000 * 60 * 10);
    }

    @Override
    public String generatePresignedVideoUrlToUserFolder(Integer loanId, Integer userId, String filename) throws Exception {
        return fileService.generatePresignedUrl("user/" + userId + "/", "binary/octet-stream", filename, 1000 * 60 * 10);
    }

    @Override
    public String registerRecordingFile(Integer loanId, Integer userId, String filename) throws Exception {
        userDao.removeUserFileFromTypeAndLoanId(userId, loanId, UserFileType.SELFIE_RECORDING);
        userDao.registerUserFile(userId, loanId, UserFileType.SELFIE_RECORDING, filename);

        return filename;
    }


}

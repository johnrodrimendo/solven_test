package com.affirm.client.service.impl;

import com.affirm.client.dao.EntityCLDAO;
import com.affirm.client.model.LoanApplicationExtranetRequestPainter;
import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.model.ResetPassword;
import com.affirm.client.model.form.RegisterEntityUserRolesForm;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.*;
import com.affirm.common.model.UTMValue;
import com.affirm.common.model.transactional.ExtranetMenuEntity;
import com.affirm.common.model.ExtranetNote;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.ExtranetRoleActionForm;
import com.affirm.common.model.security.OldPassword;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.service.TokenAuthService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jrodriguez on 27/09/16.
 */

@Service("entityExtranetService")
public class EntityExtranetServiceImpl implements EntityExtranetService {

    private static Logger logger = Logger.getLogger(EntityExtranetServiceImpl.class);

    public static final int ROLE_VISIBLE = 1;
    public static final int ROLE_EJECUCION = 2;
    public static final int ROLE_NO_VISIBLE = 3;

    public static final int BANDEJA_PENDING = 1;
    public static final int BANDEJA_GENERATED = 2;
    public static final int BANDEJA_TERMINATED = 3;
    public static final int BANDEJA_TOUPLOAD = 4;
    public static final int BANDEJA_BEING_PROCESSED = 5;
    public static final int BANDEJA_GENERATED_TC = 6;
    public static final int BANDEJA_TERMINATED_TC = 7;
    public static final int BANDEJA_CALL_CENTER = 8;
    public static final int BANDEJA_REJECTED = 9;

    @Autowired
    private TokenAuthService tokenAuthService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EntityCLDAO entityClDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private SecurityDAO securityDAO;
    @Autowired
    private EntityExtranetService entityExtranetService;
    @Autowired
    private BrandingService brandingService;
    @Autowired
    private ExtranetNoteDAO extranetNoteDAO;
    @Autowired
    LoanApplicationDAO loanApplicationDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private EntityExtranetDAO entityExtranetDAO;

    @Override
    public void login(AuthenticationToken token, HttpServletRequest request) throws Exception {

        // Log in and sets the session timeout to 5 min

        SecurityUtils.getSubject().login(token);
        SecurityUtils.getSubject().getSession().setTimeout(Configuration.getEntityExtranetTimeoutMinutes() * 60 * 1000);
        SecurityUtils.getSubject().getSession().setAttribute("entityExtranetLoginId", ((LoggedUserEntity) SecurityUtils.getSubject().getPrincipal()).getSessionId());

    }

    @Override
    public void onLogout(int sessionId, Date logoutDate) throws Exception {
        entityClDAO.registerSessionLogout(sessionId, logoutDate);
    }

    @Override
    public LoggedUserEntity getLoggedUserEntity() throws Exception {
        return ((LoggedUserEntity) SecurityUtils.getSubject().getPrincipal());
    }

    @Override
    public String generateResetPassword(String email) throws Exception {
        return generateResetPassword(email, 24);
    }

    @Override
    public String generateResetPassword(String email, Integer timeToExpireInHours) throws Exception {
        ResetPassword resetPassword;

        if (timeToExpireInHours != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            resetPassword = new ResetPassword(email, calendar.getTime().toString());
        } else {
            resetPassword = new ResetPassword(email);
        }

        Gson gson = new Gson();
        String token = CryptoUtil.encrypt(gson.toJson(resetPassword));
        userDAO.registerResetToken(email, token);
        return token;
    }

    @Override
    public String generateResetLink(String email) throws Exception {
        return generateResetLink(email, null, 24);
    }

    @Override
    public String generateResetLink(String email, Integer entityBrandingId, Integer timeToExpireInHours) throws Exception {
        if (entityBrandingId != null && Arrays.asList(Entity.BANCO_DEL_SOL,Entity.BANBIF,Entity.PRISMA,Entity.AZTECA).contains(entityBrandingId)) {
            EntityBranding entityBranding = catalogService.getEntityBranding(entityBrandingId);
            CountryParam countryParam = catalogService.getCountryParam(entityBranding.getEntity().getCountryId());
            String baseUrl;
            if (Configuration.hostEnvIsLocal()) {

                String countryDomain = countryParam.getDomains().get(0);
                baseUrl = "http://" + entityBranding.getSubdomain() + "." + countryDomain + ":8080/";
            } else if (Configuration.hostEnvIsProduction()) {
                String countryDomain = countryParam.getDomains().get(0);
                baseUrl = "http://" + entityBranding.getSubdomain() + "." + countryDomain + "/";
            } else {
                String countryDomain = countryParam.getDomains().get(0);
                baseUrl = "https://" + entityBranding.getSubdomain() + "." + countryDomain + "/";
            }
            return baseUrl.concat("funcionarios/extranetEntityReset/").concat(generateResetPassword(email, timeToExpireInHours));
        } else {
            return Configuration.getClientDomain().concat("/funcionarios/extranetEntityReset/").concat(generateResetPassword(email));
        }
    }

    @Override
    public Boolean validPassword(Integer userEntityId, String newPassword) throws Exception {
        List<OldPassword> passwords = userDAO.getUserEntityPasswords(userEntityId, Configuration.getDefaultLocale());

        if (passwords == null || passwords.isEmpty())
            return true;

        for (OldPassword password : passwords) {
            if (CryptoUtil.validatePassword(newPassword, password.getPassword())) return false;
        }
        return true;
    }

    @Override
    public JSONArray getFullCreditInfo(int entityId) throws Exception {
        return entityClDAO.getCreditFullInfo(entityId);
    }

    @Override
    public boolean showVehicleLoad() throws Exception {
        LoggedUserEntity user = getLoggedUserEntity();
        for (Entity entity : user.getEntities()) {
            if (entity.getId() == Entity.ACCESO) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<EntityExtranetUserActionLog> getUserActionsLog(Integer entityId, Integer entityUserId, Integer offset, Integer limit) throws Exception {
        return userDAO.getUserActionLog(entityId, entityUserId, offset, limit);
    }

    @Override
    public void activateTfaLogin(int entityId, boolean activate, Locale locale) throws Exception {
        entityClDAO.activateTfaLogin(entityId, activate);
        System.out.println(activate == true ? "se activo" : "se desactivo");
        if (activate == true) {
            List<UserEntity> userEntities = userDAO.getUserEntityPendingsToActivateTfa(entityId, locale);
            if (userEntities != null) {
                for (int i = 0; i < userEntities.size(); i++) {
                    Map.Entry<String, JSONArray> credentials = tokenAuthService.newTokenCredentials();
                    vinculate(userEntities.get(i), credentials.getKey(), credentials.getValue());
                    String url = utilService.qrImageUrl(userEntities.get(i).getEmail(), credentials.getKey());

                    InteractionContent interactionContent = catalogService.getInteractionContent(InteractionContent.TWO_FACTOR_AUTHENTICATION_INSTRUCTIONS, catalogService.getEntity(entityId).getCountryId());

                    String to = userEntities.get(i).getEmail();
                    String[] cc = null;

                    String subject = interactionContent.getSubject();
                    String message = interactionContent.getBody();
                    subject = subject
                            .replace("%ENTITY_USER_NAME%", userEntities.get(i).getName());
                    message = message
                            .replace("%QRIMAGEURL%", url);

                    awsSesEmailService.sendEmail(interactionContent.getFromMail(), to, cc, subject, null, message, null);
                }
            }
        }
    }

    @Override
    public void vinculate(UserEntity userEntity, String secret, JSONArray scratchs) throws Exception {
        //encrypt
        String hashSecret = CryptoUtil.encryptAES(secret, System.getenv("SECRET_ENCRYPT_KEY"));
        //persist
        entityClDAO.updateSharedSecret(userEntity.getId(), scratchs, hashSecret);
    }

    @Override
    public String registerUserEntity(int entityId, String name, String firstSurname, String email, Locale locale) throws Exception {
        String message = entityClDAO.registerEntityUser(entityId, name, firstSurname, email);
        if (message.equals("entity_user.registered")) {
            if (!catalogService.getEntity(entityId).isTfaLogin()) {
                sendResetPasswordEmail(email, locale, entityId);
            } else {
                UserEntity userEntity = userDAO.getUserEntityByEmail(email, locale, entityId);
                Map.Entry<String, JSONArray> credentials = tokenAuthService.newTokenCredentials();
                vinculate(userDAO.getUserEntityByEmail(email, locale, entityId), credentials.getKey(), credentials.getValue());
                String url = utilService.qrImageUrl(userEntity.getEmail(), credentials.getKey());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonVars = new JSONObject();
                            jsonVars.put("USER_NAME", userDAO.getUserEntityByEmail(email, locale, entityId).getName());
                            jsonVars.put("RESET_LINK", generateResetLink(email));
                            jsonVars.put("AGENT_IMAGE_URL", Configuration.AGENT_IMAGE_URL_COLLECTION);
                            jsonVars.put("AGENT_FULLNAME", Configuration.AGENT_FULLNAME_COLLECTION);
                            jsonVars.put("QRIMAGEURL", url);

                            PersonInteraction interaction = new PersonInteraction();
                            interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                            interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.RESET_PASSWORD_WITH_TFA_INSTRUCTIONS, catalogService.getEntity(entityId).getCountryId()));
                            interaction.setDestination(email);
                            interactionService.sendPersonInteraction(interaction, jsonVars, null);
                        } catch (Exception ex) {
                            logger.error("Error sending email", ex);
                        }
                    }
                }).start();
            }
        }
        return message;
    }

    @Override
    public Integer createOrUpdateEntityuser(RegisterEntityUserRolesForm userForm) throws Exception {

        Entity loggedEntity = getPrincipalEntity();
        JSONObject result = null;

        if (userForm.getId() == null) {
            // Call the create query
            result = userDAO.registerBDSUserEntity(
                    null,
                    loggedEntity.getId(),
                    userForm.getEmail(),
                    userForm.getName(),
                    userForm.getFirstSurname(),
                    CryptoUtil.hashPassword(RandomStringUtils.random(8, true, true)),
                    userForm.getIdInEntity(),
                    userForm.getRoleId(),
                    userForm.getProducerId(),
                    userForm.getOrganizerId(), null,
                    userForm.getChannelId());
        } else {
            // Call the edit query
            result = userDAO.registerBDSUserEntity(
                    userForm.getId(),
                    loggedEntity.getId(),
                    userForm.getEmail(),
                    userForm.getName(),
                    userForm.getFirstSurname(),
                    null,
                    userForm.getIdInEntity(),
                    userForm.getRoleId(),
                    userForm.getProducerId(),
                    userForm.getOrganizerId(), null,
                    userForm.getChannelId());
        }

        if (result != null && result.has("message")) {
            String errorMessage = result.getString("message");
            if (errorMessage.equalsIgnoreCase("entity_user.exists"))
                throw new SqlErrorMessageException(null, "El email ya está registrado.");
            if (errorMessage.equalsIgnoreCase("entity_user_id_from_entity.exists"))
                throw new SqlErrorMessageException(null, "El Id de usuario en la entidad ya está registrado.");
            throw new SqlErrorMessageException(errorMessage, null);
        }
        Integer userId = JsonUtil.getIntFromJson(result, "entity_user_id", null);

        if (userForm.getId() == null) {
            JSONObject jsonVars = new JSONObject();
            jsonVars.put("USER_NAME", userForm.getName());
            jsonVars.put("RESET_LINK", generateResetLink(userForm.getEmail(), loggedEntity.getId(), null));
            jsonVars.put("AGENT_IMAGE_URL", Configuration.AGENT_IMAGE_URL_COLLECTION);
            jsonVars.put("AGENT_FULLNAME", Configuration.AGENT_FULLNAME_COLLECTION);
            if (loggedEntity.getId() == Entity.BANCO_DEL_SOL) {
                jsonVars.put("ENTITY", loggedEntity.getShortName());
                jsonVars.put("ENTITY_SHORT", loggedEntity.getShortName());
            }

            PersonInteraction interaction = new PersonInteraction();
            interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
            interaction.setDestination(userForm.getEmail());
            if (loggedEntity.getId().equals(Entity.BANCO_DEL_SOL)) {
                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.CREATE_USER_EXTRANET_ENTITY_BRANDING, catalogService.getEntity(loggedEntity.getId()).getCountryId()));
                Map<String, String> templateVars = new HashMap<>();
                templateVars.put("banner_image_url", "https://solven-public.s3.amazonaws.com/img/bancodelsol/banco-del-sol_reset_password_banner.png");
                interactionService.sendPersonInteraction(interaction, jsonVars, null, templateVars);
            } else {
                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.CREATE_USER_EXTRANET_ENTITY, catalogService.getEntity(loggedEntity.getId()).getCountryId()));
                interactionService.sendPersonInteraction(interaction, jsonVars, null);
            }
        }

        return userId;
    }

    @Override
    public void sendResetPasswordEmail(String email, Locale locale, Integer entityId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Entity entity = getPrincipalEntity();
                    EntityExtranetConfiguration entityExtranetConfiguration = null;
                    if (entity == null && entityId != null){
                        entity = catalogService.getEntity(entityId);
                    }
                    if(entity != null){
                        entityExtranetConfiguration = brandingService.getExtranetBrandingAsJson(entity.getId());
                    }
                    JSONObject jsonVars = new JSONObject();
                    jsonVars.put("USER_NAME", userDAO.getUserEntityByEmail(email, locale, entityId).getName());
                    jsonVars.put("RESET_LINK", generateResetLink(email, entity != null ? entity.getId() : null, 24));
                    jsonVars.put("AGENT_IMAGE_URL", Configuration.AGENT_IMAGE_URL_COLLECTION);
                    jsonVars.put("AGENT_FULLNAME", Configuration.AGENT_FULLNAME_COLLECTION);
                    if (entity != null) {
                        jsonVars.put("ENTITY", entity.getShortName());
                        jsonVars.put("ENTITY_SHORT", entity.getShortName());
                    }

                    PersonInteraction interaction = new PersonInteraction();
                    interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    interaction.setDestination(email);

                    if (entity != null && entity.getId().equals(Entity.BANCO_DEL_SOL)) {
                        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.RESET_PASSWORD_BANCO_DEL_SOL, entity.getCountryId()));
                        Map<String, String> templateVars = new HashMap<>();
                        templateVars.put("banner_image_url", "https://solven-public.s3.amazonaws.com/img/bancodelsol/banco-del-sol_reset_password_banner.png");
                        interactionService.sendPersonInteraction(interaction, jsonVars, null, templateVars);
                    }
                    else if(entity != null && entityExtranetConfiguration != null && entityExtranetConfiguration.getBackgroundEmailUrl() != null) {
                        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.RESET_PASSWORD_BRANDING, CountryParam.COUNTRY_PERU));
                        Map<String, String> templateVars = new HashMap<>();
                        templateVars.put("banner_image_url", entityExtranetConfiguration.getBackgroundEmailUrl());
                        String body = interaction.getInteractionContent().getBody();
                        String text = entityExtranetConfiguration.getResetPasswordTextEmail() == null ? "Por favor sigue el siguiente link para cambiar tu contraseña." : entityExtranetConfiguration.getResetPasswordTextEmail();
                        String subject = interaction.getInteractionContent().getSubject();
                        subject = subject.replace("%ENTITY%", entity.getShortName() != null ? entity.getShortName() : "");
                        body = body.replace("%BODY_MESSAGE%", text);
                        interaction.setSubject(subject);
                        if(entityExtranetConfiguration.getAgentImgEmailUrl() != null) jsonVars.put("AGENT_IMAGE_URL", entityExtranetConfiguration.getAgentImgEmailUrl());
                        if(entityExtranetConfiguration.getAgentEmailName() != null) jsonVars.put("AGENT_FULLNAME", entityExtranetConfiguration.getAgentEmailName());
                        if(entityExtranetConfiguration.getBackgroundColorButtonEmail() != null) body = body.replace("%BUTTON_COLOR%", entityExtranetConfiguration.getBackgroundColorButtonEmail());
                        interaction.setBody(body);
                        interactionService.sendPersonInteraction(interaction, jsonVars, null, templateVars);
                    }
                    else{
                        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.RESET_PASSWORD, CountryParam.COUNTRY_PERU));
                        interactionService.sendPersonInteraction(interaction, jsonVars, null, null);
                    }

                } catch (Exception ex) {
                    logger.error("Error sending email", ex);
                }
            }
        }).start();
    }

    public void activateExtranetEntityUser(Integer entityUserId, Boolean valueToUpdate) throws Exception {
        userDAO.activateExtranetEntityUser(entityUserId, valueToUpdate);
    }

    @Override
    public List<Integer> getBandejaModifiablePermission(int bandeja) throws Exception {
        Entity entity = getLoggedUserEntity().getPrincipalEntity();

        List<Integer> modifiablePermisions = new ArrayList<>();
        switch (bandeja) {
            case 1:
                if (entity.getExtranetRoles().contains(Role.BANDEJA_1_VISIBLE))
                    modifiablePermisions.add(ROLE_VISIBLE);
                if (entity.getExtranetRoles().contains(Role.BANDEJA_1_EJECUCION))
                    modifiablePermisions.add(ROLE_EJECUCION);
                break;
            case 2:
                if (entity.getExtranetRoles().contains(Role.BANDEJA_2_VISIBLE))
                    modifiablePermisions.add(ROLE_VISIBLE);
                if (entity.getExtranetRoles().contains(Role.BANDEJA_2_EJECUCION))
                    modifiablePermisions.add(ROLE_EJECUCION);
                break;
            case 3:
                if (entity.getExtranetRoles().contains(Role.BANDEJA_3_VISIBLE))
                    modifiablePermisions.add(ROLE_VISIBLE);
                if (entity.getExtranetRoles().contains(Role.BANDEJA_3_EJECUCION))
                    modifiablePermisions.add(ROLE_EJECUCION);
                break;
            case 4:
                if (entity.getExtranetRoles().contains(Role.BANDEJA_4_VISIBLE))
                    modifiablePermisions.add(ROLE_VISIBLE);
                break;
            case 5:
                if (entity.getExtranetRoles().contains(Role.BANDEJA_5_VISIBLE))
                    modifiablePermisions.add(ROLE_VISIBLE);
                break;
            case 6:
                if (entity.getExtranetRoles().contains(Role.BANDEJA_6_VISIBLE))
                    modifiablePermisions.add(ROLE_VISIBLE);
                if (entity.getExtranetRoles().contains(Role.BANDEJA_6_EJECUCION))
                    modifiablePermisions.add(ROLE_EJECUCION);
                break;
            case 7:
                if (entity.getExtranetRoles().contains(Role.BANDEJA_7_VISIBLE))
                    modifiablePermisions.add(ROLE_VISIBLE);
                if (entity.getExtranetRoles().contains(Role.BANDEJA_7_EJECUCION))
                    modifiablePermisions.add(ROLE_EJECUCION);
                break;
        }

        if (!modifiablePermisions.isEmpty())
            modifiablePermisions.add(ROLE_NO_VISIBLE);

        return modifiablePermisions;
    }

    @Override
    public List<EntityExtranetUser> getEntityUsersForPermissionModification() throws Exception {
        Entity entity = getLoggedUserEntity().getPrincipalEntity();
        List<ExtranetMenuEntity> extranetMenuEntities = securityDAO.getExtranetMenuEntities(entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId());
        List<EntityExtranetUser> users = userDAO.getEntityExtranetUsers(entity.getId(), 0, null);
        if (users != null && users.size() > 0) {
            for (EntityExtranetUser u : users) {
                    ExtranetRoleActionForm form = new ExtranetRoleActionForm();
                    extranetMenuEntities.forEach(extranetMenuEntity -> {
                        ExtranetRoleActionForm.Value value = new ExtranetRoleActionForm.Value();
                        value.setExtranetMenuId(extranetMenuEntity.getExtranetMenu().getId());
                        if(u.getMenuEntityProductCategories() != null){
                            value.setMenuEntityProductCategories(new ArrayList<>());
                            EntityExtranetUser.MenuEntityProductCategory entityProductCategory = u.getMenuEntityProductCategories().stream().filter(e -> e.getMenuEntityId().equals(value.getExtranetMenuId())).findFirst().orElse(null);
                            if(entityProductCategory != null && entityProductCategory.getProductCategories() != null) value.setMenuEntityProductCategories(entityProductCategory.getProductCategories());
                        }
                        for(ExtranetMenuRoleGroup roleGroup : extranetMenuEntity.getroleGroupToShow()){
                            if(u.containsExtranetMenuRoleGroup(roleGroup))
                                value.getRoleGroups().add(roleGroup);
                        }
                        form.addValue(value);
                    });
                    u.setForm(form);

            }
        }

        return users;
    }

    @Override
    public List<CreditEntityExtranetPainter> getCreditsToShow(int bandeja, Date startDate, Date endDate, Locale locale, Integer offset, Integer limit, String search) throws Exception {
        return getCreditsToShow(bandeja, startDate, endDate, locale, offset, limit, search, false, null,null);
    }

    @Override
    public List<CreditEntityExtranetPainter> getCreditsToShow(int bandeja, Date startDate, Date endDate, Locale locale, Integer offset, Integer limit, String search, boolean onlyIds) throws Exception {
        return getCreditsToShow(bandeja, startDate, endDate, locale, offset, limit, search, onlyIds, null,null);
    }

    @Override
    public List<CreditEntityExtranetPainter> getCreditsToShow(int bandeja, Date startDate, Date endDate, Locale locale, Integer offset, Integer limit, String search, boolean onlyIds, Integer[] entityProductsParam, List<Integer> products) throws Exception {

        LoggedUserEntity loggedUserEntity = getLoggedUserEntity();

        int entityId = getLoggedUserEntity().getPrincipalEntity().getId();
        int entityUserId = getLoggedUserEntity().getId();
        List<EntityExtranetUser> entityExtranetUsers = userDAO.getEntityExtranetUsers(entityId, null, null);
        List<CreditEntityExtranetPainter> credits = creditDao.getEntityCredits(entityId, bandeja, startDate, endDate, entityUserId, locale, offset, limit, search, entityProductsParam, onlyIds,products);
        if (credits == null)
            credits = new ArrayList<>();

        for (CreditEntityExtranetPainter credit : credits) {
            applyValidationsToCreditToShow(bandeja, credit);

            if (credit.getEntityUserId() != null)
                credit.setEntityExtranetUser(entityExtranetUsers.stream().filter(e -> e.getUserId().equals(credit.getEntityUserId())).findAny().orElse(null));
        }

        return credits;
    }

    @Override
    public Pair<Integer, Double> getCreditsToShowCount(int bandeja, Date startDate, Date endDate, String search, Locale locale) throws Exception {
        return getCreditsToShowCount(bandeja, startDate, endDate, search, locale, null,null);
    }

    @Override
    public Pair<Integer, Double> getCreditsToShowCount(int bandeja, Date startDate, Date endDate, String search, Locale locale, Integer[] entityProductsParam, List<Integer> products) throws Exception {

        int entityId = getLoggedUserEntity().getPrincipalEntity().getId();
        int entityUserId = getLoggedUserEntity().getId();
        return creditDao.getEntityCreditsCount(entityId, bandeja, startDate, endDate, entityUserId, locale,search,entityProductsParam, products);

    }

    @Override
    public CreditEntityExtranetPainter getCreditToShowById(int creditId, int bandeja, Locale locale) throws Exception {

        CreditEntityExtranetPainter credit = creditDao.getCreditByID(creditId, locale, false, CreditEntityExtranetPainter.class);
        if (credit != null)
            applyValidationsToCreditToShow(bandeja, credit);

        return credit;
    }

    @Override
    public List<CreditBancoDelSolExtranetPainter> getCreditsBeingProcessedByLoggedUserId(int userId, Date startDate, Date endDate, String query, Integer offset, Integer limit, Locale locale) throws Exception {
        return getCreditsBeingProcessedByLoggedUserId(userId, startDate, endDate, query, offset, limit, locale, false, null, null, null);
    }

    @Override
    public List<CreditBancoDelSolExtranetPainter> getCreditsBeingProcessedByLoggedUserId(int userId, Date startDate, Date endDate, String query, Integer offset, Integer limit, Locale locale, boolean onlyIds,List<Integer> productsId, Integer minProgress, Integer maxProgress) throws Exception {
        List<CreditBancoDelSolExtranetPainter> credits = creditDao.getEntityCreditsByLoggedUserId(userId, startDate, endDate, query,offset, limit, locale, onlyIds, productsId, minProgress,  maxProgress);
        ;

        if (credits == null)
            credits = new ArrayList<>();

//        for (CreditBancoDelSolExtranetPainter credit : credits) {
//            applyValidationsToCreditToShow(BANDEJA_BEING_PROCESSED, credit);
//        }
        return credits;
    }

    private void applyValidationsToCreditToShow(int bandeja, CreditEntityExtranetPainter credit) throws Exception {
        if (bandeja == ExtranetMenu.TO_VERIFY_CREDITS_MENU) {
            // General configuration for buttons
            EntityProductParams epp = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
            if (epp.getShowGeneration()) {
                if (SecurityUtils.getSubject().isPermitted("credit:pending:validate"))
                    credit.setShowPendingToValidateButton(true);
                if (SecurityUtils.getSubject().isPermitted("credit:pending:reject"))
                    credit.setShowPendingRejectButton(true);
            }

            // Configurations for Acceso - credito garantizado
            if (EntityProductParams.ENT_PROD_PARAM_ACCESO_GARANTIZADO.contains(credit.getEntityProductParameterId())) {
                switch (credit.getSubStatus().getId()) {
                    case CreditSubStatus.ACCESO_WAITING_FOR_APPOINTMENT:
                        if (SecurityUtils.getSubject().isPermitted("credit:pending:accesoScheduleAppointment"))
                            credit.setShowAccesoScheduleAppointmentButton(true);
                        credit.setShowPendingToValidateButton(false);
                        break;
                    case CreditSubStatus.ACCESO_APPOINTMENT_REGISTERED:
                        if (SecurityUtils.getSubject().isPermitted("credit:pending:accesoScheduleAppointment"))
                            credit.setShowAccesoReScheduleAppointmentButton(true);
                        break;
                }
            }
        } else if (bandeja == ExtranetMenu.TO_UPLOAD_CREDITS_MENU) {

        } else if (bandeja == ExtranetMenu.TO_DISBURSE_CREDITS_MENU || bandeja == ExtranetMenu.CALL_CENTER_MENU) {
            // General configuration for buttons
            EntityProductParams epp = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
            if (epp.getShowDisbursement()) {
                if (SecurityUtils.getSubject().isPermitted("credit:generated:disbursement"))
                    credit.setShowGeneratedDisbursementButton(true);
                if (SecurityUtils.getSubject().isPermitted("credit:generated:reject"))
                    credit.setShowGeneratedRejectButton(true);
            }

            // Configuration for Ripley - prestamo ya
            if (EntityProductParams.ENT_PROD_PARAM_RIPLEY_PRESTAMOYA.contains(credit.getEntityProductParameterId())) {
                if (SecurityUtils.getSubject().isPermitted("credit:generated:uploadRipleyFinalSchedule"))
                    credit.setShowUploadRipleyFinalSchedule(credit.getRipleyFinalScheduleFileId() == null);
                if (SecurityUtils.getSubject().isPermitted("credit:generated:deleteRipleyFinalSchedule"))
                    credit.setShowRemoveRipleyFinalSchedule(credit.getRipleyFinalScheduleFileId() != null);
            }

            // Configurations for Aelu - convenio
            if (credit.getEntityProductParameterId().equals(EntityProductParams.ENT_PROD_PARAM_AELU_CONVENIO)) {
                switch (credit.getSubStatus().getId()) {
                    case CreditSubStatus.AELU_PENDING_INTERNAL_DISBURSEMENT:
                        if (SecurityUtils.getSubject().isPermitted("credit:generated:aeluRegisterInternalDisbursemet"))
                            credit.setShowAeluInternalDisbursementButton(true);
                        credit.setShowGeneratedDisbursementButton(false);
                        break;
                    case CreditSubStatus.PENDING_FINAL_DOCUMENTATION:
                        if (SecurityUtils.getSubject().isPermitted("credit:generated:aeluUploadFinalDocumentation"))
                            credit.setShowAeluUploadFinalDocumentationButton(true);
                        credit.setShowGeneratedDisbursementButton(false);
                        break;
                    case CreditSubStatus.PENDING_ENTIY_DISBURSEMENT:
                        // Keep it as it is
                        break;
                    default:
                        credit.setShowGeneratedDisbursementButton(false);
                        break;
                }
            }

            // Configurations for Acceso - credito garantizado
            if (EntityProductParams.ENT_PROD_PARAM_ACCESO_GARANTIZADO.contains(credit.getEntityProductParameterId())) {
                switch (credit.getSubStatus().getId()) {
                    case CreditSubStatus.PENDING_FINAL_DOCUMENTATION:
                        if (SecurityUtils.getSubject().isPermitted("credit:generated:accesoUploadDocumentation"))
                            credit.setShowAccesoUploadFinalDocumentationButton(true);
                        credit.setShowGeneratedDisbursementButton(false);
                        break;
                }
            }
        } else if (bandeja == ExtranetMenu.TO_DELIVER_TC_MENU) {
            credit.setShowGeneratedDisbursementButton(true);
            credit.setShowGeneratedRejectButton(true);
        }
    }

    @Override
    public Currency getActiveEntityCurrency() throws Exception {
        Entity entity = getLoggedUserEntity().getPrincipalEntity();
        return catalogService.getCountryParam(entity.getCountryId()).getCurrency();
    }

    @Override
    public void sendRateCommissionChangeMail(Entity entity, UserEntity userEntity, List<RateCommissionProduct> paramsBeforeChange, List<RateCommissionProduct> paramsAfterChange) throws Exception {

        Currency currency = catalogService.getCountryParam(entity.getCountryId()).getCurrency();

        // Create html of the params before chamge
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<p>Se han realizado cambios en los parámetros de configuración.<br>" +
                "Usuario: " + userEntity.getFullName() + "<br>" +
                "Fecha: " + new SimpleDateFormat("dd/MM/yy HH:mm").format(new Date()) + "</p>");

        for (int i = 0; i < 2; i++) {

            List<RateCommissionProduct> paramsToProces;
            if (i == 0) {
                htmlBuilder.append("<b>Parámetros antes del cambio:</b><br>");
                paramsToProces = paramsBeforeChange;
            } else {
                htmlBuilder.append("<b>Parámetros despues del cambio:</b><br>");
                paramsToProces = paramsAfterChange;
            }

            for (RateCommissionProduct product : paramsToProces) {
                if (paramsToProces.size() > 1) {
                    htmlBuilder.append("<div class=\"inner\" style=\"padding: 15px 0;\"><span class=\"title\" style=\"color: #32c5d2; font-size: 16px; font-weight: 700; padding: 10px 0; text-transform: uppercase;\">" + product.getProduct() + "</span></div>");
                }
                for (RateCommissionPrice price : product.getPrices()) {
                    if (product.getPrices().size() > 1) {
                        htmlBuilder.append("<div class=\"inner\" style=\"padding: 15px 0;\"><span class=\"title\" style=\"color: #32c5d2; font-size: 16px; font-weight: 700; padding: 10px 0; text-transform: uppercase;\">" + price.getPrice() + "</span></div>");
                    }
                    for (RateCommissionCluster cluster : price.getClusters()) {
                        htmlBuilder.append("<div class=\"inner\" style=\"padding: 15px 0;\"><span class=\"title\" style=\"color: #32c5d2; font-size: 16px; font-weight: 700; padding: 10px 0; text-transform: uppercase;\">" + cluster.getCluster() + "</span></div>");
                        htmlBuilder.append("<table style=\"background-color: #fff; border: 0; border-collapse: collapse; border-left: 1px solid #BABBD4; border-right: 1px solid #BABBD4; border-spacing: 0; font-size: 13px; margin-bottom: 2rem; max-width: 650px; position: relative; white-space: nowrap; width: 100%;\">\n" +
                                "      <thead style=\"background: #34465d; color: #FFF;\">\n" +
                                "        <tr>" +
                                "          <th style=\"-webkit-box-sizing: border-box; border: 0; border-top: 0; box-sizing: border-box; color: white; display: table-cell; font-size: 12px; font-weight: 600; height: 48px; line-height: 1.42857; padding: 8px; position: relative; text-align: center; vertical-align: middle;\">Plazo desde (meses)</th>\n" +
                                "          <th style=\"-webkit-box-sizing: border-box; border: 0; border-top: 0; box-sizing: border-box; color: white; display: table-cell; font-size: 12px; font-weight: 600; height: 48px; line-height: 1.42857; padding: 8px; position: relative; text-align: center; vertical-align: middle;\">Plazo hasta (meses)</th>\n" +
                                "          <th style=\"-webkit-box-sizing: border-box; border: 0; border-top: 0; box-sizing: border-box; color: white; display: table-cell; font-size: 12px; font-weight: 600; height: 48px; line-height: 1.42857; padding: 8px; position: relative; text-align: center; vertical-align: middle;\">Monto min.</th>\n" +
                                "          <th style=\"-webkit-box-sizing: border-box; border: 0; border-top: 0; box-sizing: border-box; color: white; display: table-cell; font-size: 12px; font-weight: 600; height: 48px; line-height: 1.42857; padding: 8px; position: relative; text-align: center; vertical-align: middle;\">Monto max.</th>\n" +
                                "          <th style=\"-webkit-box-sizing: border-box; border: 0; border-top: 0; box-sizing: border-box; color: white; display: table-cell; font-size: 12px; font-weight: 600; height: 48px; line-height: 1.42857; padding: 8px; position: relative; text-align: center; vertical-align: middle;\">" + (entity.getCountryId() == CountryParam.COUNTRY_ARGENTINA ? "T.N.A." : "T.E.A.") + "</th>\n" +
                                "        </tr>\n" +
                                "      </thead>\n" +
                                "      <tbody>");
                        for (RateCommission rateCommission : cluster.getRateCommissions()) {
                            htmlBuilder.append(
                                    "        <tr>\n" +
                                            "          <td style=\"border: 1px solid #e7ecf1; line-height: 1.42857; padding: 8px; text-align: center; vertical-align: middle;\"><span>" + rateCommission.getMinInstallments() + "</span></td>\n" +
                                            "          <td style=\"border: 1px solid #e7ecf1; line-height: 1.42857; padding: 8px; text-align: center; vertical-align: middle;\">\n" +
                                            "            <div><span>" + rateCommission.getInstallments() + "</span></div>\n" +
                                            "          </td>\n" +
                                            "          <td style=\"border: 1px solid #e7ecf1; line-height: 1.42857; padding: 8px; text-align: center; vertical-align: middle;\">\n" +
                                            "            <div><span>" + utilService.integerOnlyMoney(rateCommission.getMinAmount(), currency) + "</span></div>\n" +
                                            "          </td>\n" +
                                            "          <td style=\"border: 1px solid #e7ecf1; line-height: 1.42857; padding: 8px; text-align: center; vertical-align: middle;\">\n" +
                                            "            <div><span>" + utilService.integerOnlyMoney(rateCommission.getMaxAmountCommission(), currency) + "</span></div>\n" +
                                            "          </td>\n" +
                                            "          <td style=\"border: 1px solid #e7ecf1; line-height: 1.42857; padding: 8px; text-align: center; vertical-align: middle;\">\n" +
                                            "            <div><span>" + utilService.percentFormat(rateCommission.getEffectiveAnualRate()) + "</span></div>\n" +
                                            "          </td>\n" +
                                            "        </tr>");
                        }
                        htmlBuilder.append("</tbody></table>");
                    }
                }
            }
        }

        // Send the email
        awsSesEmailService.sendEmail(
                Configuration.EMAIL_PROCESS_FROM(),//from
                Configuration.EMAIL_CONTACT_TO(),//to
                null,
                "Cambio en el modulo comercial",//subject
                null,
                htmlBuilder.toString(),
                null
        );
    }

    @Override
    public boolean shouldRegisterEntityApplicationCode() throws Exception {
        return getLoggedUserEntity().getPrincipalEntity().getId() == Entity.ACCESO;
    }

    @Override
    public Entity getPrincipalEntity() throws Exception {
        return getLoggedUserEntity() != null ? getLoggedUserEntity().getPrincipalEntity() : null;
    }

    @Override
    public void sendInteractionProcessLink(LoanApplication loanApplication, String destination, JSONObject parameters) throws Exception {
        InteractionContent interactionContent = catalogService.getInteractionContent(InteractionContent.SEND_NEW_LOAN_LINK_ENTITY_EXTRANET, loanApplication.getCountryId());

        PersonInteraction personInteraction = new PersonInteraction();
        personInteraction.setInteractionType(interactionContent.getType());
        personInteraction.setDestination(destination);
        personInteraction.setInteractionContent(interactionContent);
        personInteraction.setPersonId(loanApplication.getPersonId());
        personInteraction.setLoanApplicationId(loanApplication.getId());

        interactionService.sendPersonInteraction(personInteraction, parameters, null);
    }

    @Override
    public String generateUserFilesLink(int creditId) throws Exception {
        JSONObject json = new JSONObject();
        json.put("creditId", creditId);
        String jsonEncrypt = CryptoUtil.encrypt(json.toString());

        String baseUrl = null;
        if (Configuration.hostEnvIsLocal()) {
            baseUrl = "http://files.solven-test.pe:8080/";
        } else if (Configuration.hostEnvIsDev()) {
            baseUrl = "https://filesdev.solven.pe/";
        } else if (Configuration.hostEnvIsStage()) {
            baseUrl = "https://filesstg.solven.pe/";
        } else if (Configuration.hostEnvIsProduction()) {
            baseUrl = "https://files.solven.pe/";
        }

        return baseUrl + "public/zipAllLoanDocuments/" + jsonEncrypt + "/download";
    }

    @Override
    public String generateUserLoanFilesLink(int loanId) throws Exception {
        JSONObject json = new JSONObject();
        json.put("loanId", loanId);
        String jsonEncrypt = CryptoUtil.encrypt(json.toString());

        String baseUrl = null;
        if (Configuration.hostEnvIsLocal()) {
            baseUrl = "http://files.solven-test.pe:8080/";
        } else if (Configuration.hostEnvIsDev()) {
            baseUrl = "https://filesdev.solven.pe/";
        } else if (Configuration.hostEnvIsStage()) {
            baseUrl = "https://filesstg.solven.pe/";
        } else if (Configuration.hostEnvIsProduction()) {
            baseUrl = "https://files.solven.pe/";
        }

        return baseUrl + "public/zipAllLoanApplicationDocuments/" + jsonEncrypt + "/download";
    }

    @Override
    public Integer canShowDownloadTrayReport(Integer tray, HttpServletRequest request, Locale locale) throws Exception {
        EntityBranding entityBranding = brandingService.getEntityBranding(request);
        if (entityBranding != null && entityBranding.getBranded() && entityBranding.getEntityExtranetConfiguration() != null  && tray != null) {
            switch (tray){
                case ExtranetMenu.TO_DISBURSE_CREDITS_MENU:
                    if(entityBranding.getEntityExtranetConfiguration().getDisburseCreditPageConfiguration() != null) return entityBranding.getEntityExtranetConfiguration().getDisburseCreditPageConfiguration().getReportId();
                    break;
                case ExtranetMenu.DISBURSEMENT_CREDITS_MENU:
                    if(entityBranding.getEntityExtranetConfiguration().getDisbursedCreditPageConfiguration() != null) return entityBranding.getEntityExtranetConfiguration().getDisbursedCreditPageConfiguration().getReportId();
                    break;
                case ExtranetMenu.BEING_PROCESSED_MENU:
                    if(entityBranding.getEntityExtranetConfiguration().getInProcessCreditPageConfiguration() != null) return entityBranding.getEntityExtranetConfiguration().getInProcessCreditPageConfiguration().getReportId();
                    break;
                case ExtranetMenu.CALL_CENTER_MENU:
                    if(entityBranding.getEntityExtranetConfiguration().getCallCenterPageConfiguration() != null) return entityBranding.getEntityExtranetConfiguration().getCallCenterPageConfiguration().getReportId();
                    break;
                case ExtranetMenu.TO_VERIFY_CREDITS_MENU:
                    if(entityBranding.getEntityExtranetConfiguration().getToVerifyPageConfiguration() != null) return entityBranding.getEntityExtranetConfiguration().getToVerifyPageConfiguration().getReportId();
                    break;
                case ExtranetMenu.TO_UPLOAD_CREDITS_MENU:
                    if(entityBranding.getEntityExtranetConfiguration().getCreateCreditPageConfiguration() != null) return entityBranding.getEntityExtranetConfiguration().getCreateCreditPageConfiguration().getReportId();
                    break;
                case ExtranetMenu.PAYMENT_COMMITMENT_MENU:
                    if(entityBranding.getEntityExtranetConfiguration().getPaymentCommitmentPageConfiguration() != null) return entityBranding.getEntityExtranetConfiguration().getPaymentCommitmentPageConfiguration().getReportId();
                    break;
                case ExtranetMenu.REJECTED_MENU:
                    if(entityBranding.getEntityExtranetConfiguration().getRejectedPageConfiguration() != null) return entityBranding.getEntityExtranetConfiguration().getRejectedPageConfiguration().getReportId();
                    break;
            }
        }
        return null;
    }

    @Override
    public List<ExtranetNote> getNotesFromMenu(Integer menuId, Integer entityId,Integer offset, Integer limit){
        if(menuId == null || entityId == null) return new ArrayList<>();
        return extranetNoteDAO.getExtranetMenuNotes(entityId,menuId,offset,limit);
    }

    @Override
    public void insExtranetNote(Integer menuId, String type, String note) throws Exception {
        Entity entity = getPrincipalEntity();
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        ExtranetNote extranetNote = new ExtranetNote();
        extranetNote.setExtranetMenuId(menuId);
        extranetNote.setType(type);
        extranetNote.setNote(note);
        extranetNote.setEntityId(entity != null ? entity.getId() : null);
        extranetNote.setRegisterDate(new Date());
        extranetNote.setEntityUserId(loggedUserEntity != null ? loggedUserEntity.getId() : null);
        extranetNoteDAO.insExtranetMenuNote(extranetNote);
    }

    @Override
    public ExtranetNote editExtranetNote(Integer menuId, Integer noteId, String type, String note) throws Exception {
        Entity entity = getPrincipalEntity();
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        ExtranetNote extranetNote = extranetNoteDAO.getExtranetMenuNote(noteId);
        if(extranetNote == null) return null;
        if(!extranetNote.getEntityId().equals(entity.getId())) return null;
        extranetNote.setNote(note);
        extranetNote.setType(type);
        extranetNote.setExtranetMenuId(menuId);
        extranetNoteDAO.editExtranetMenuNote(extranetNote);
        return extranetNote;
    }

    @Override
    public Pair<Integer, Double> getNotesCount(Integer menuId, Locale locale) throws Exception {
        int entityId = getLoggedUserEntity().getPrincipalEntity().getId();
        return extranetNoteDAO.getNotesCount(entityId,menuId);
    }

    @Override
    public List<CreditBancoDelSolExtranetPainter> getRejectedLoanApplications(Integer entityId, Date startDate, Date endDate, Locale locale, String query, Integer offset, Integer limit) throws Exception {
        return getRejectedLoanApplications(entityId, startDate, endDate, locale, query,offset,limit, false, null,null);
    }

    @Override
    public List<CreditBancoDelSolExtranetPainter> getRejectedLoanApplications(Integer entityId, Date startDate, Date endDate, Locale locale, String query, Integer offset, Integer limit, boolean onlyIds, List<Integer> productIds, List<String> rejectedReasonIds) throws Exception {
        List<CreditBancoDelSolExtranetPainter> credits = creditDao.getEntityRejectedLoanApplications(entityId, startDate, endDate, query,offset, limit, locale, onlyIds, productIds, rejectedReasonIds);

        if (credits == null)
            credits = new ArrayList<>();
        return credits;
    }

    @Override
    public LoanApplicationExtranetRequestPainter getApplicationById(int loanApplicationId, Locale locale, HttpServletRequest request) throws Exception {
        LoanApplicationExtranetRequestPainter application = loanApplicationDao.getLoanApplication(loanApplicationId, locale, LoanApplicationExtranetRequestPainter.class);

        application.setOffers(loanApplicationDao.getLoanOffersAll(application.getId()));
        application.setConsolidableDebts(loanApplicationDao.getConsolidationAccounts(application.getId()));

        List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(application.getId());
        if (userFiles != null && userFiles.size() > 0) {
            UserFile userFile = userFiles.stream().filter(e -> e.getFileType().getId() == UserFileType.CONTRACT_CALL).findFirst().orElse(null);
            if (userFile != null) {
                String tokyCallURL = fileService.generateUserFileUrl(userFile.getId(), request, false);
                application.setTokyCall(tokyCallURL);
            }
        }

        return application;
    }

    @Override
    public EntityExtranetConfiguration.EditableFieldConfiguration getFragmentDetailConfiguration(EntityExtranetConfiguration configuration, Integer tray, String parentFragment, Integer productCategoryId) throws Exception{
        return getFragmentDetailConfiguration(configuration, tray, parentFragment, null,productCategoryId);
    }

    @Override
    public UTMValue getUTMValuesFromEntity(Integer entityId) {
        return creditDao.getUTMValuesFromEntity(entityId);
    }

    @Override
    public EntityExtranetConfiguration.EditableFieldConfiguration getFragmentDetailConfiguration(EntityExtranetConfiguration configuration, Integer tray, String parentFragment, String childFragment, Integer productCategoryId) throws Exception {
        if(configuration == null || tray == null || parentFragment == null) return null;
        EntityExtranetConfiguration.PageDetailConfiguration pageDetailConfiguration = null;
        LoggedUserEntity loggedUserEntity = getLoggedUserEntity();
        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(),tray);
        EntityExtranetConfiguration.EditableFieldConfiguration editableFieldConfigurationAux = new EntityExtranetConfiguration.EditableFieldConfiguration();
        editableFieldConfigurationAux.setVisible(false);
        editableFieldConfigurationAux.setEditable(false);

        if(menuEntityProductCategory == null
                || menuEntityProductCategory.getProductCategories() == null
                || !menuEntityProductCategory.getProductCategories().contains(productCategoryId)
                || menuEntityProductCategory.getExtranetMenu() == null
                || menuEntityProductCategory.getExtranetMenu().getMenuEntityProductCategories() == null
        ) return editableFieldConfigurationAux;

        ExtranetMenu.MenuEntityProductCategoryConfiguration entityProductCategoryConfiguration = menuEntityProductCategory.getExtranetMenu().getMenuEntityProductCategories().stream().filter(e -> e.getProductCategoryId().equals(productCategoryId) && e.getConfiguration() != null).findFirst().orElse(null).getConfiguration();

        if(entityProductCategoryConfiguration != null) editableFieldConfigurationAux.setVisible(true);

        if(entityProductCategoryConfiguration == null || entityProductCategoryConfiguration.getEditableFields() == null || !entityProductCategoryConfiguration.getEditableFields()) return editableFieldConfigurationAux;

        switch (tray){
            case ExtranetMenu.TO_DISBURSE_CREDITS_MENU:
                pageDetailConfiguration = configuration.getDisburseCreditPageConfiguration();
                break;
            case ExtranetMenu.DISBURSEMENT_CREDITS_MENU:
                pageDetailConfiguration = configuration.getDisbursedCreditPageConfiguration();
                break;
            case ExtranetMenu.EVALUATION_MENU:
                pageDetailConfiguration = configuration.getEvaluationPageConfiguration();
                break;
            case ExtranetMenu.REJECTED_MENU:
                pageDetailConfiguration = configuration.getRejectedPageConfiguration();
                break;
            case ExtranetMenu.BEING_PROCESSED_MENU:
                pageDetailConfiguration = configuration.getInProcessCreditPageConfiguration();
                break;
        }
        if(pageDetailConfiguration == null || pageDetailConfiguration.getDetailConfiguration() == null) return null;
        EntityExtranetConfiguration.EditableFieldConfiguration editableFieldConfiguration = null;
        EntityExtranetConfiguration.RequestDetailConfiguration requestDetailConfiguration = null;
        switch (parentFragment){
            case "summary":
                editableFieldConfiguration = pageDetailConfiguration.getDetailConfiguration().getSummary();
                break;
            case "applicant":
                editableFieldConfiguration = pageDetailConfiguration.getDetailConfiguration().getApplicant();
                break;
            case "incomes":
                editableFieldConfiguration = pageDetailConfiguration.getDetailConfiguration().getIncomes();
                break;
            case "request":
                editableFieldConfiguration = pageDetailConfiguration.getDetailConfiguration().getRequest();
                requestDetailConfiguration = pageDetailConfiguration.getDetailConfiguration().getRequest();
                break;
            case "phoneVerification":
                editableFieldConfiguration = pageDetailConfiguration.getDetailConfiguration().getPhoneVerification();
                break;
            case "addressVerification":
                editableFieldConfiguration = pageDetailConfiguration.getDetailConfiguration().getAddressVerification();
                break;
            case "welcomeCall":
                editableFieldConfiguration = pageDetailConfiguration.getDetailConfiguration().getWelcomeCall();
                break;
        }
        if(childFragment == null) return editableFieldConfiguration;
        if(requestDetailConfiguration != null && editableFieldConfiguration != null){
            switch (childFragment){
                case "generalInformation":
                    return requestDetailConfiguration.getGeneralInformation();
                case "evaluationResult":
                    return requestDetailConfiguration.getEvaluationResult();
                case "documentation":
                    return requestDetailConfiguration.getDocumentation();
                case "identityValidation":
                    return requestDetailConfiguration.getIdentityValidation();
                case "fraudAlert":
                    return requestDetailConfiguration.getFraudAlert();
                case "bankAccount":
                    return requestDetailConfiguration.getBankAccount();
                case "notes":
                    return requestDetailConfiguration.getNotes();
                case "interaction":
                    return requestDetailConfiguration.getInteraction();
                case "sbs":
                    return requestDetailConfiguration.getSbs();
            }
        }
        return null;
    }

    @Override
    public EntityExtranetUser.MenuEntityProductCategory getMenuEntityProductCategoryByTray(List<EntityExtranetUser.MenuEntityProductCategory> menuEntityProductCategories, Integer menuEntityId) throws Exception{
        if(menuEntityProductCategories != null && !menuEntityProductCategories.isEmpty() && menuEntityId != null){
            return menuEntityProductCategories.stream().filter(e -> e.getMenuEntityId() != null && e.getMenuEntityId().equals(menuEntityId)).findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public List<Product> getProductsByCategory(List<Integer> productCategoriesId, Integer entityId ) throws Exception {
        if(productCategoriesId == null) return new ArrayList<>();
        List<Product> products = catalogService.getEntityProductsByEntity(entityId).stream().map(EntityProduct::getProduct).collect(Collectors.toList());
        products = products.stream().distinct().collect(Collectors.toList());
        return products.stream().filter(e -> productCategoriesId.contains(e.getProductCategoryId())).collect(Collectors.toList());
    }

    @Override
    public void migrateUserData(List<Integer> entitiesId) throws Exception {
        if(Configuration.hostEnvIsProduction()) return;
        for (Integer entityId : entitiesId) {

            //MENU DE LA ENTIDAD
            List<ExtranetMenuEntity> extranetMenuEntities = securityDAO.getExtranetMenuEntities(entityId);

            //OBTENCION DE LOS PRODUCTOS
            List<Product> products = catalogService.getAllEntityProductsByEntityIncludeInactives(entityId).stream().map(EntityProduct::getProduct).collect(Collectors.toList());

            //CATEGORIAS DE LOS PRODUCTOS
            List<Integer> productCategoriesId = products.stream().map(Product::getProductCategoryId).distinct().collect(Collectors.toList());

            List<EntityExtranetUser> entityExtranetUsers = userDAO.getEntityExtranetUsers(entityId, null, null);

            List<String> otherFixes = new ArrayList<>();


            List<EntityExtranetUser.MenuEntityProductCategory> entityProductCategoriesToUpdate = new ArrayList<>();

            for (ExtranetMenuEntity extranetMenuEntity : extranetMenuEntities) {
                EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = new EntityExtranetUser.MenuEntityProductCategory();
                menuEntityProductCategory.setMenuEntityId(extranetMenuEntity.getExtranetMenu().getId());
                if(extranetMenuEntity.getExtranetMenu().getMenuEntityProductCategories() != null && !extranetMenuEntity.getExtranetMenu().getMenuEntityProductCategories().isEmpty()){
                    menuEntityProductCategory.setProductCategories(extranetMenuEntity.getExtranetMenu().getMenuEntityProductCategories().stream().filter(e -> productCategoriesId.contains(e.getProductCategoryId())).map(ExtranetMenu.MenuEntityProductCategory::getProductCategoryId).collect(Collectors.toList()));
                }
                entityProductCategoriesToUpdate.add(menuEntityProductCategory);
            }

            for (EntityExtranetUser entityExtranetUser : entityExtranetUsers) {

                List<Product> userProducts = userDAO.getProductsFromUserAndEntity(entityExtranetUser.getUserId(), entityId, Configuration.getDefaultLocale());

                List<Integer> productCategoriesIdEntityUser = userProducts.stream().map(Product::getProductCategoryId).distinct().collect(Collectors.toList());

                if (userProducts.size() > 1) {

                    System.out.println(String.format("<-------- INICIO USUARIO %s (%s)-------->", entityExtranetUser.getEmail(), entityExtranetUser.getUserId()));
                    System.out.println(String.format("USUARIO %s posee mas de 1 producto para la entidad %s", entityExtranetUser.getEmail(), entityId));
                    System.out.println("<-------- DETALLE -------->");
                    System.out.println(String.format(new Gson().toJson(entityExtranetUser)));
                    System.out.println("<-------- PRODUCTOS -------->");
                    System.out.println(String.format(new Gson().toJson(userProducts)));
                    System.out.println("<-------- FIN USUARIO-------->");

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("entityExtranetUser", new Gson().toJson(entityExtranetUser));
                    jsonObject.put("products", userProducts);

                    userDAO.deleteAndCreateEntityUserRelationship(entityExtranetUser.getUserId(), entityId, products.get(0).getId());
                    otherFixes.add(jsonObject.toString());


                }

                List<EntityExtranetUser.MenuEntityProductCategory> entityProductCategoriesToUpdateCustomized = new ArrayList<EntityExtranetUser.MenuEntityProductCategory>(entityProductCategoriesToUpdate);

                for (EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory : entityProductCategoriesToUpdateCustomized) {
                    if(menuEntityProductCategory.getProductCategories() != null && !menuEntityProductCategory.getProductCategories().isEmpty()){
                        if(productCategoriesIdEntityUser.isEmpty()) menuEntityProductCategory.setProductCategories(new ArrayList<>());
                        menuEntityProductCategory.setProductCategories(menuEntityProductCategory.getProductCategories().stream().filter(e -> productCategoriesIdEntityUser.contains(e)).collect(Collectors.toList()));
                    }
                }

                entityExtranetDAO.updateEntityUserInformation(entityExtranetUser.getUserId(), entityId, entityProductCategoriesToUpdateCustomized);

            }

            System.out.println("<-------- DETALLE ENTIDAD-------->");
            System.out.println(String.format("PROCESADOS_AUXILIARES: %s ", otherFixes.size()));
            System.out.println(otherFixes.toString());

        }
    }

}

package com.affirm.client.service;

import com.affirm.client.model.LoanApplicationExtranetPainter;
import com.affirm.client.model.LoanApplicationExtranetRequestPainter;
import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.model.form.RegisterEntityUserRolesForm;
import com.affirm.common.model.ExtranetNote;
import com.affirm.common.model.UTMValue;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.authc.AuthenticationToken;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface EntityExtranetService {
    void login(AuthenticationToken token, HttpServletRequest request) throws Exception;

    void onLogout(int sessionId, Date logoutDate) throws Exception;

    LoggedUserEntity getLoggedUserEntity() throws Exception;

    String generateResetLink(String email) throws Exception;

    String generateResetPassword(String email, Integer timeToExpireInHours) throws Exception;

    String generateResetPassword(String email) throws Exception;

    String generateResetLink(String email, Integer entityBrandingId, Integer timeToExpireInHours) throws Exception;

    Boolean validPassword(Integer userEntityId, String newPassword) throws Exception;

    JSONArray getFullCreditInfo(int entityId) throws Exception;

    boolean showVehicleLoad() throws Exception;

    List<EntityExtranetUserActionLog> getUserActionsLog(Integer entityId, Integer entityUserId, Integer offset, Integer limit) throws Exception;

    void activateTfaLogin(int entityId, boolean activate, Locale locale) throws Exception;

    void vinculate(UserEntity userEntity, String secret, JSONArray scratchs) throws Exception;

    String registerUserEntity(int entityId, String name, String firstSurname, String email, Locale locale) throws Exception;

    Integer createOrUpdateEntityuser(RegisterEntityUserRolesForm userForm) throws Exception;

    void sendResetPasswordEmail(String email, Locale locale, Integer entityId);

    void activateExtranetEntityUser(Integer entityUserId, Boolean valueToUpdate) throws Exception;

    List<Integer> getBandejaModifiablePermission(int bandeja) throws Exception;

    List<EntityExtranetUser> getEntityUsersForPermissionModification() throws Exception;

    List<CreditEntityExtranetPainter> getCreditsToShow(int bandeja, Date startDate, Date endDate, Locale locale, Integer offset, Integer limit, String search) throws Exception;

    List<CreditEntityExtranetPainter> getCreditsToShow(int bandeja, Date startDate, Date endDate, Locale locale, Integer offset, Integer limit, String search, boolean onlyIds) throws Exception;

    Pair<Integer, Double> getCreditsToShowCount(int bandeja, Date startDate, Date endDate, String search, Locale locale) throws Exception;

    CreditEntityExtranetPainter getCreditToShowById(int creditId, int bandeja, Locale locale) throws Exception;

    List<CreditBancoDelSolExtranetPainter> getCreditsBeingProcessedByLoggedUserId(int userId, Date startDate, Date endDate,String query, Integer offset, Integer limit, Locale locale) throws Exception;

    Currency getActiveEntityCurrency() throws Exception;

    void sendRateCommissionChangeMail(Entity entity, UserEntity userEntity, List<RateCommissionProduct> paramsBeforeChange, List<RateCommissionProduct> paramsAfterChange) throws Exception;

    boolean shouldRegisterEntityApplicationCode() throws Exception;

    Entity getPrincipalEntity() throws Exception;

    void sendInteractionProcessLink(LoanApplication loanApplication, String destination, JSONObject parameters) throws Exception;

    String generateUserFilesLink(int creditId) throws Exception;

    Integer canShowDownloadTrayReport(Integer tray, HttpServletRequest request, Locale locale) throws Exception;

    List<CreditBancoDelSolExtranetPainter> getCreditsBeingProcessedByLoggedUserId(int userId, Date startDate, Date endDate, String query, Integer offset, Integer limit, Locale locale, boolean onlyIds, List<Integer> productsId,Integer minProgress, Integer maxProgress) throws Exception;

    List<ExtranetNote> getNotesFromMenu(Integer menuId, Integer entityId,Integer offset, Integer limit);

   void insExtranetNote(Integer menuId, String type, String note) throws Exception;

    ExtranetNote editExtranetNote(Integer menuId, Integer noteId, String type, String note) throws Exception;

    Pair<Integer, Double> getNotesCount(Integer menuId, Locale locale) throws Exception;

    List<CreditBancoDelSolExtranetPainter> getRejectedLoanApplications(Integer entityId, Date startDate, Date endDate, Locale locale, String query, Integer offset, Integer limit) throws Exception;

    List<CreditBancoDelSolExtranetPainter> getRejectedLoanApplications(Integer entityId, Date startDate, Date endDate, Locale locale, String query, Integer offset, Integer limit, boolean onlyIds, List<Integer> productIds, List<String> rejectedReason) throws Exception;

    LoanApplicationExtranetRequestPainter getApplicationById(int loanApplicationId, Locale locale, HttpServletRequest request) throws Exception;

    List<CreditEntityExtranetPainter> getCreditsToShow(int bandeja, Date startDate, Date endDate, Locale locale, Integer offset, Integer limit, String search, boolean onlyIds, Integer[] entityProductsParam, List<Integer> products) throws Exception;

    Pair<Integer, Double> getCreditsToShowCount(int bandeja, Date startDate, Date endDate, String search, Locale locale,Integer[] entityProductsParam, List<Integer> products) throws Exception;

    String generateUserLoanFilesLink(int loanId) throws Exception;
    
    EntityExtranetConfiguration.EditableFieldConfiguration getFragmentDetailConfiguration(EntityExtranetConfiguration configuration, Integer tray, String fragmentParent, String fragmentChild,Integer productCategoryId) throws Exception;

    EntityExtranetConfiguration.EditableFieldConfiguration getFragmentDetailConfiguration(EntityExtranetConfiguration configuration, Integer tray, String fragmentParent, Integer productCategoryId) throws Exception;

    UTMValue getUTMValuesFromEntity(Integer entityId);

    EntityExtranetUser.MenuEntityProductCategory getMenuEntityProductCategoryByTray(List<EntityExtranetUser.MenuEntityProductCategory> menuEntityProductCategories, Integer menuEntityId ) throws Exception;

    List<Product> getProductsByCategory(List<Integer> productCategoriesId, Integer entityId) throws Exception;

    void migrateUserData(List<Integer> entitiesId) throws Exception;
}

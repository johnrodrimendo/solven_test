package com.affirm.common.service;

import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.LoanApplication;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface BrandingService {

    EntityBranding getEntityBranding(HttpServletRequest request) throws Exception;

    String getEntityBrandingLogoUrl(HttpServletRequest request, String defaultValue) throws Exception;

    String getEntityBrandingFooterLogoUrl(HttpServletRequest request, String defaultValue) throws Exception;

    String getEntityBrandingPrimaryColor(HttpServletRequest request, String defaultValue) throws Exception;

    String getEntityBrandingSecundaryColor(HttpServletRequest request, String defaultValue) throws Exception;

    String getEntityBrandingLightColor(HttpServletRequest request, String defaultValue) throws Exception;

    String getEntityBrandingAgentPageLogoUrl(HttpServletRequest request, String defaultValue) throws Exception;

    boolean isBranded(HttpServletRequest request) throws Exception;

    String getEntityBrandingIntercomKey(HttpServletRequest request, String defaultValue) throws Exception;

    boolean isNinja(HttpServletRequest request) throws Exception;

    ProductAgeRange getRangeAge(HttpServletRequest request, int defaultMax, int defaultMin) throws Exception;

    String getEntityBrandingAgentBackgroundColor(HttpServletRequest request, String defaultValue) throws Exception;

    String getEntityBrandingAgentTextColor(HttpServletRequest request, String defaultValue) throws Exception;

    String getEntityBrandingLineOne(HttpServletRequest request, String defaultValue) throws Exception;

    String getEntityBrandingLineTwo(HttpServletRequest request, String defaultValue) throws Exception;

    String getEntityBrandingLineThree(HttpServletRequest request, String defaultValue) throws Exception;

    boolean isBrandedByEntityId(HttpServletRequest request, Integer entityId) throws Exception;

    String getEntityBrandingFavIcon(HttpServletRequest request, String defaultValue) throws Exception;

    String getEntityBrandingTertiaryColor(HttpServletRequest request, String defaultValue) throws Exception;

    String getEntityBrandingProcessLogo(HttpServletRequest request, String defaultValue) throws Exception;

    List<String> getEntityBrandingMinRequirements(HttpServletRequest request) throws Exception;

    boolean showMinRequirements(HttpServletRequest request) throws Exception;

    EntityBrandingLoanLandingConfiguration getEntityBrandingLAConfigSettings(HttpServletRequest request, Locale locale) throws Exception;

    List<LoanApplicationReason> getLoanApplicationReasons(HttpServletRequest request, Locale locale) throws Exception;

    String getMetaDescription(HttpServletRequest request, Locale locale) throws Exception;

    String getMetaDescription(HttpServletRequest request, Locale locale, LoanApplication loanApplication) throws Exception;

    String getMetaTittle(HttpServletRequest request, Locale locale, LoanApplication loanApplication) throws Exception;

    String getMetaTittle(HttpServletRequest request, Locale locale) throws Exception;

    EntityExtranetConfiguration getExtranetBrandingAsJson(HttpServletRequest request) throws Exception;

    EntityExtranetConfiguration getExtranetBrandingAsJson(Integer entityId) throws Exception;

    String getExtranetBrandingImageHeader(HttpServletRequest request) throws Exception;

    Boolean canHideBrandingLAElement(HttpServletRequest request, Locale locale, String selector) throws Exception;

    Boolean canShowBrandingAdditionalLAElement(HttpServletRequest request, Locale locale, String selector) throws Exception;

    String getBrandingFavicon(HttpServletRequest request, Locale locale) throws Exception;

    EntityExtranetConfiguration getExtranetBrandingAsJson(Integer id, Boolean noCache) throws  Exception;

}

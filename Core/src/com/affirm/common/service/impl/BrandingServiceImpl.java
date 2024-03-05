package com.affirm.common.service.impl;

import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.BrandingService;
import com.affirm.common.service.CatalogService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jarmando on 26/01/17.
 */
@Service("brandingService")
public class BrandingServiceImpl implements BrandingService {

    public static final String BRANDING_REQUEST_KEY = "BRANDING_PARAM";
    private static final String BRANDING_REQUEST_NULL = "nullBranding";

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private MessageSource messageSource;

    @Override
    public EntityBranding getEntityBranding(HttpServletRequest request) throws Exception {
        if (request.getAttribute(BRANDING_REQUEST_KEY) != null && request.getAttribute(BRANDING_REQUEST_KEY) instanceof EntityBranding)
            return (EntityBranding) request.getAttribute(BRANDING_REQUEST_KEY);
        else if (request.getAttribute(BRANDING_REQUEST_KEY) != null && request.getAttribute(BRANDING_REQUEST_KEY) instanceof String && ((String) request.getAttribute(BRANDING_REQUEST_KEY)).equals(BRANDING_REQUEST_NULL))
            return null;


        String host = null;
        if(Configuration.hostEnvIsLocal()){
            host = new URL(request.getRequestURL().toString()).getHost();
        }else{
            host = request.getHeader("X-Forwarded-Host");
        }

        String subdomain = host != null ? host.trim().split("\\.")[0] : null;
        EntityBranding entityBranding = catalogService.getEntityBranding(subdomain);

        request.setAttribute(BRANDING_REQUEST_KEY, entityBranding != null ? entityBranding : BRANDING_REQUEST_NULL);

        return entityBranding;
    }

    @Override
    public EntityExtranetConfiguration getExtranetBrandingAsJson(HttpServletRequest request) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if(branding != null) return branding.getEntityExtranetConfiguration();
        return null;
    }

    @Override
    public String getExtranetBrandingImageHeader(HttpServletRequest request) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if(branding != null && branding.getEntityExtranetConfiguration() != null && branding.getEntityExtranetConfiguration().getHeaderLogoImg() != null) return branding.getEntityExtranetConfiguration().getHeaderLogoImg();
        return null;
    }

    @Override
    public EntityExtranetConfiguration getExtranetBrandingAsJson(Integer id, Boolean noCache) throws  Exception{
        if(noCache != null && noCache){
            EntityBranding branding = catalogService.getEntityBrandingNoCache(id);
            if(branding != null) return branding.getEntityExtranetConfiguration();
        }else return getExtranetBrandingAsJson(id);
        return null;
    }

    @Override
    public EntityExtranetConfiguration getExtranetBrandingAsJson(Integer id) throws  Exception{
        if(id == null) return  null;
        EntityBranding branding = catalogService.getEntityBranding(id);
        if(branding != null) return branding.getEntityExtranetConfiguration();
        return null;
    }

    @Override
    public String getEntityBrandingLogoUrl(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getEntity().getLogoUrl();
        return defaultValue;
    }

    @Override
    public String getEntityBrandingFooterLogoUrl(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getLogoFooterUrl();
        return defaultValue;
    }

    @Override
    public String getEntityBrandingPrimaryColor(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getEntityPrimaryColor();
        return defaultValue;
    }

    @Override
    public String getEntityBrandingLineOne(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getColorLineOne();
        return defaultValue;
    }

    @Override
    public String getEntityBrandingLineTwo(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getColorLineTwo();
        return defaultValue;
    }

    @Override
    public String getEntityBrandingLineThree(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getColorLineThree();
        return defaultValue;
    }

    @Override
    public String getEntityBrandingSecundaryColor(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getEntitySecundaryColor();
        return defaultValue;
    }


    @Override
    public String getEntityBrandingFavIcon(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getFavicon();
        return defaultValue;
    }

    @Override
    public String getEntityBrandingTertiaryColor(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getTertiaryColor();
        return defaultValue;
    }

    @Override
    public String getEntityBrandingProcessLogo(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getProcessLogo();
        return defaultValue;
    }

    @Override
    public String getEntityBrandingLightColor(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getEntityLightColor();
        return defaultValue;
    }

    @Override
    public String getEntityBrandingAgentPageLogoUrl(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getLogoAgentPageUrl();
        return defaultValue;
    }

    @Override
    public boolean isBranded(HttpServletRequest request) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        return branding != null && branding.getBranded();
    }

    @Override
    public boolean isBrandedByEntityId(HttpServletRequest request, Integer entityId) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null && branding.getEntity().getId().equals(entityId) && branding.getBranded())
            return true;
        else return false;
    }

    @Override
    public String getEntityBrandingIntercomKey(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getIntercomKey();
        return defaultValue;
    }

    @Override
    public boolean isNinja(HttpServletRequest request) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        return branding != null && branding.getNinja();
    }

    @Override
    public ProductAgeRange getRangeAge(HttpServletRequest request, int defaultMax, int defaultMin) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        Integer max = 0;
        Integer min = 200;
        List<EntityProduct> entityProducts = branding.getEntity().getProducts();
        for (EntityProduct entityProduct : entityProducts) {
            ProductAgeRange productAgeRange = catalogService.getProductAgeRange(entityProduct.getProduct().getId(), branding.getEntity().getId());
            if (productAgeRange != null) {
                if (productAgeRange.getMaxAge() != null && productAgeRange.getMaxAge() > max)
                    max = productAgeRange.getMaxAge();
                if (productAgeRange.getMinAge() != null && productAgeRange.getMinAge() < min)
                    min = productAgeRange.getMinAge();
            }
        }

        ProductAgeRange productAgeRange = new ProductAgeRange();
        productAgeRange.setMaxAge(max == 0 ? defaultMax : max);
        productAgeRange.setMinAge(min == 200 ? defaultMin : min);
        return productAgeRange;
    }

    @Override
    public String getEntityBrandingAgentBackgroundColor(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getAgentBackgroundColor();
        return defaultValue;
    }

    @Override
    public String getEntityBrandingAgentTextColor(HttpServletRequest request, String defaultValue) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getAgentTextColor();
        return defaultValue;
    }

    @Override
    public List<String> getEntityBrandingMinRequirements(HttpServletRequest request) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null)
            return branding.getMinRequirements();
        return null;
    }

    @Override
    public boolean showMinRequirements(HttpServletRequest request) throws Exception {
        EntityBranding branding = getEntityBranding(request);
        if (branding != null && branding.getBranded())
            if (getEntityBrandingMinRequirements(request) == null || !getEntityBrandingMinRequirements(request).isEmpty())
                return true;
        return false;
    }

    @Override
    public EntityBrandingLoanLandingConfiguration getEntityBrandingLAConfigSettings(HttpServletRequest request, Locale locale) throws Exception {

        EntityBranding entityBranding = getEntityBranding(request);
        if (entityBranding != null && entityBranding.getBranded()) {
            entityBranding.getLandingConfiguration().fillDefaultValues(messageSource, locale);
            return entityBranding.getLandingConfiguration();
        } else {
            EntityBrandingLoanLandingConfiguration config = new EntityBrandingLoanLandingConfiguration();
            config.fillDefaultValues(messageSource, locale);
            return config;
        }
    }

    @Override
    public List<LoanApplicationReason> getLoanApplicationReasons(HttpServletRequest request, Locale locale) throws Exception {

        EntityBrandingLoanLandingConfiguration entityBrandingLa = getEntityBrandingLAConfigSettings(request, locale);
        List<LoanApplicationReason> reasons = new ArrayList<>();
        if (entityBrandingLa != null && !entityBrandingLa.getLoanReasons().isEmpty()) {
            for(Integer reasonId : entityBrandingLa.getLoanReasons()){
                reasons.add(catalogService.getLoanApplicationReason(locale, reasonId));
            }
        }

        if(reasons.isEmpty()){
            reasons = catalogService.getLoanApplicationReasonsVisible(locale);
        } else {
            for (int i = 0; i < reasons.size(); i++) {
                if (reasons.get(i).getTextInt() != null) {
                    reasons.get(i).setReason(messageSource.getMessage(reasons.get(i).getTextInt(), null, locale));
                }
            }
        }
        return reasons;
    }

    @Override
    public String getMetaDescription(HttpServletRequest request, Locale locale) throws Exception {
        return getMetaDescription(request,locale,null);
    }

    @Override
    public String getMetaDescription(HttpServletRequest request, Locale locale, LoanApplication loanApplication) throws Exception {
        EntityBranding entityBranding = getEntityBranding(request);
        if (entityBranding != null && entityBranding.getBranded()) {
            switch (entityBranding.getEntity().getId()){
                case Entity.CREDIGOB:
                    return "Créditos pre-aprobados en línea. Desembolso en 48 horas.";
                case Entity.FINANSOL:
                    return "Créditos Pre-Aprobados";
                case Entity.ACCESO:
                    return "Créditos Pre-Aprobados";
                case Entity.PRISMA:
                    return "Obtén tu préstamo digital.";
                case Entity.AZTECA:
                    if(loanApplication != null && loanApplication.getProductCategoryId() != null && loanApplication.getProductCategoryId() == ProductCategory.GATEWAY ) return "Cancela tu deuda pagando menos";
                    if(loanApplication != null && loanApplication.getProductCategoryId() != null && loanApplication.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA ) return "Abre tu cuenta de ahorro";
                    if(loanApplication != null && loanApplication.getProductCategoryId() != null && loanApplication.getProductCategoryId() == ProductCategory.CONSEJ0 ) return "Revisa de la mano de los expertos de Alfin cómo está tu salud financiera.";
                    return "Dinero efectivo al instante";
                default:
                    return  entityBranding.getEntity().getFullName() != null ? entityBranding.getEntity().getFullName() : entityBranding.getEntity().getShortName();
            }
        }
        return null;
    }
    @Override
    public String getMetaTittle(HttpServletRequest request, Locale locale) throws Exception {
        return getMetaTittle(request,locale,null);
    }

    @Override
    public String getMetaTittle(HttpServletRequest request, Locale locale, LoanApplication loanApplication) throws Exception {
        EntityBranding entityBranding = getEntityBranding(request);
        if (entityBranding != null && entityBranding.getBranded()) {
            switch (entityBranding.getEntity().getId()){
                case Entity.CREDIGOB:
                    return "Credigob | Crédito para proveedores del estado | Powered by Solven";
                case Entity.FUNDACION_DE_LA_MUJER:
                    return "Fundación delamujer";
                case Entity.FINANSOL:
                    return "Finansol | El crédito que mereces";
                case Entity.ACCESO:
                    return "Acceso | El crédito que mereces";
                case Entity.PRISMA:
                    return "MF PRISMA | Obtén tu préstamo digital";
                case Entity.AZTECA:
                    if(loanApplication != null && loanApplication.getProductCategoryId() != null && loanApplication.getProductCategoryId() == ProductCategory.GATEWAY ) return "Alfin | Cancela tu deuda pagando menos";
                    if(loanApplication != null && loanApplication.getProductCategoryId() != null && loanApplication.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA ) return "Alfin | Abre tu cuenta de ahorro";
                    if(loanApplication != null && loanApplication.getProductCategoryId() != null && loanApplication.getProductCategoryId() == ProductCategory.CONSEJ0 ) return "Alfin| Evalúate con nosotros gratuitamente!";
                    return "Alfin | Dinero efectivo al instante";

            }
        }
        return null;
    }

    @Override
    public Boolean canHideBrandingLAElement(HttpServletRequest request, Locale locale, String selector) throws Exception {
        EntityBranding entityBranding = getEntityBranding(request);
        if (entityBranding != null && entityBranding.getBranded()) {
            entityBranding.getLandingConfiguration().fillDefaultValues(messageSource, locale);
            entityBranding.getLandingConfiguration();
            List<String> elementsToHide = entityBranding.getLandingConfiguration().getHideElements();
            if(elementsToHide != null && !elementsToHide.isEmpty()) return elementsToHide.contains(selector) ? true : false;
        }
        return false;
    }

    @Override
    public Boolean canShowBrandingAdditionalLAElement(HttpServletRequest request, Locale locale, String selector) throws Exception {
        EntityBranding entityBranding = getEntityBranding(request);
        if (entityBranding != null && entityBranding.getBranded()) {
            entityBranding.getLandingConfiguration().fillDefaultValues(messageSource, locale);
            entityBranding.getLandingConfiguration();
            List<String> elementsToShow = entityBranding.getLandingConfiguration().getAdditionalElements();
            if(elementsToShow != null && !elementsToShow.isEmpty()) return elementsToShow.contains(selector) ? true : false;
        }
        return false;
    }

    @Override
    public String getBrandingFavicon(HttpServletRequest request, Locale locale) throws Exception {
        EntityBranding entityBranding = getEntityBranding(request);
        if (entityBranding != null && entityBranding.getBranded()) {
            return  entityBranding.getFavicon();
        }
        return null;
    }

}

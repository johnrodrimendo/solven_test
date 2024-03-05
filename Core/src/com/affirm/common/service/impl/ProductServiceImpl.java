package com.affirm.common.service.impl;

import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ProductService;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jrodriguez
 */

@Service("productService")
public class ProductServiceImpl implements ProductService {

    public static final String PRODUCT_DOMAIN_REQUEST_KEY = "PRODUCT_DOMAIN_PARAM";
    public static final String PRODUCT_DOMAIN_REQUEST_NULL = "productDomainNull";

    private static Logger logger = Logger.getLogger(ProductServiceImpl.class);

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;

    @Override
    public Integer getMaxInstalments(Integer productCategoryId, Integer countryId) throws Exception {
        return getMaxInstalments(productCategoryId, countryId, null);
    }

    @Override
    public Integer getMinInstalments(Integer productCategoryId, Integer countryId) throws Exception {
        return getMinInstalments(productCategoryId, countryId, null);
    }

    @Override
    public Integer getMaxAmount(Integer productCategoryId, Integer countryId) throws Exception {
        return getMaxAmount(productCategoryId, countryId, null);
    }

    @Override
    public Integer getMinAmount(Integer productCategoryId, Integer countryId) throws Exception {
        return getMinAmount(productCategoryId, countryId, null);
    }

    @Override
    public Integer getMaxInstalments(Integer productCategoryId, int countryId, EntityBranding entityBranding) throws Exception {
        if (entityBranding != null) {
            return getMaxInstalmentsEntity(productCategoryId, countryId, entityBranding.getEntity().getId());
        }

        List<Product> activeProductsByCategory = catalogService.getCatalog(
                Product.class, Configuration.getDefaultLocale(), true,
                (c -> productCategoryId == null
                        ? c.getProductCategoryId().equals(ProductCategory.CONSUMO)
                        : c.getProductCategoryId().equals(productCategoryId)));

        return activeProductsByCategory.stream()
                .map(p -> p.getProductParams(countryId))
                .filter(p -> p != null)
                .mapToInt(p -> p.getMaxInstallments())
                .max()
                .orElse(0);
    }

    @Override
    public Integer getMaxInstalmentsEntity(Integer categoryId, int countryId, int entityId) throws Exception {
        List<Product> activeProductsByCategory = catalogService.getProductsEntity();

        return activeProductsByCategory.stream()
                .filter(p -> categoryId == null
                        ? p.getActive()
                        : p.getActive() && p.getProductCategoryId().equals(categoryId))
                .map(p -> p.getProductParams(countryId, entityId))
                .filter(p -> p != null && p.getActive())
                .mapToInt(p -> p.getMaxInstallments())
                .max()
                .orElse(getMaxInstalments(categoryId, countryId, null));
    }

    @Override
    public Integer getMinInstalments(Integer categoryId, int countryId, EntityBranding entityBranding) throws Exception {
        if (entityBranding != null) {
            return getMinInstalmentsEntity(categoryId, countryId, entityBranding.getEntity().getId());
        }

        List<Product> activeProductsByCategory = catalogService.getCatalog(
                Product.class, Configuration.getDefaultLocale(), true,
                (c -> categoryId == null
                        ? c.getProductCategoryId().equals(ProductCategory.CONSUMO)
                        : c.getProductCategoryId().equals(categoryId)));

        return activeProductsByCategory.stream()
                .map(p -> p.getProductParams(countryId))
                .filter(p -> p != null)
                .mapToInt(p -> p.getMinInstallments())
                .min()
                .orElse(0);
    }

    @Override
    public Integer getMinInstalmentsEntity(Integer categoryId, int countryId, int entityId) throws Exception {
        List<Product> activeProductsByCategory = catalogService.getProductsEntity();

        return activeProductsByCategory.stream()
                .filter(p -> categoryId == null
                        ? p.getActive()
                        : p.getActive() && p.getProductCategoryId().equals(categoryId))
                .map(p -> p.getProductParams(countryId, entityId))
                .filter(p -> p != null && p.getActive())
                .mapToInt(p -> p.getMinInstallments())
                .min()
                .orElse(getMinInstalments(categoryId, countryId, null));
    }

    @Override
    public Integer getMaxAmount(Integer categoryId, int countryId, EntityBranding entityBranding) throws Exception {
        if (entityBranding != null) {
            return getMaxAmountEntity(categoryId, countryId, entityBranding.getEntity().getId());
        }

        List<Product> activeProductsByCategory = catalogService.getCatalog(
                Product.class, Configuration.getDefaultLocale(), true,
                (c -> categoryId == null
                        ? c.getProductCategoryId().equals(ProductCategory.CONSUMO)
                        : c.getProductCategoryId().equals(categoryId)));

        return activeProductsByCategory.stream()
                .map(p -> p.getProductParams(countryId))
                .filter(p -> p != null)
                .mapToInt(p -> p.getMaxAmount())
                .max()
                .orElse(0);
    }

    @Override
    public Integer getMaxAmountEntity(Integer categoryId, int countryId, int entityId) throws Exception {
        List<Product> activeProductsByCategory = catalogService.getProductsEntity();

        return activeProductsByCategory.stream()
                .filter(p -> categoryId == null
                        ? p.getActive()
                        : p.getActive() && p.getProductCategoryId().equals(categoryId))
                .map(p -> p.getProductParams(countryId, entityId))
                .filter(p -> p != null && p.getActive())
                .mapToInt(p -> p.getMaxAmount())
                .max()
                .orElse(getMaxAmount(categoryId, countryId, null));
    }

    @Override
    public Integer getMinAmount(Integer categoryId, int countryId, EntityBranding entityBranding) throws Exception {
        if (entityBranding != null) {
            return getMinAmountEntity(categoryId, countryId, entityBranding.getEntity().getId());
        }

        List<Product> activeProductsByCategory = catalogService.getCatalog(
                Product.class, Configuration.getDefaultLocale(), true,
                (c -> categoryId == null
                        ? c.getProductCategoryId().equals(ProductCategory.CONSUMO)
                        : c.getProductCategoryId().equals(categoryId)));

        return activeProductsByCategory.stream()
                .map(p -> p.getProductParams(countryId))
                .filter(p -> p != null)
                .mapToInt(p -> p.getMinAmount())
                .min()
                .orElse(0);
    }

    @Override
    public Integer getMinAmountEntity(Integer categoryId, int countryId, int entityId) throws Exception {
        List<Product> activeProductsByCategory = catalogService.getProductsEntity();

        return activeProductsByCategory.stream()
                .filter(p -> categoryId == null
                        ? p.getActive()
                        : p.getActive() && p.getProductCategoryId().equals(categoryId))
                .map(p -> p.getProductParams(countryId, entityId))
                .filter(p -> p != null && p.getActive())
                .mapToInt(p -> p.getMinAmount())
                .min()
                .orElse(getMinAmount(categoryId, countryId, null));
    }

    @Override
    public ProductCategoryCountry getProductCategoryCountry(int categoryId, int countryId) {
        return catalogService.getCatalogById(ProductCategory.class, categoryId, Configuration.getDefaultLocale()).getCountriesConfig().stream()
                .filter(c -> c.getCountryId().intValue() == countryId).findFirst().orElse(null);
    }

    @Override
    public ProductCountryDomain getProductDomainByRequest(HttpServletRequest request) {
        if (request.getAttribute(PRODUCT_DOMAIN_REQUEST_KEY) != null && request.getAttribute(PRODUCT_DOMAIN_REQUEST_KEY) instanceof ProductCountryDomain)
            return (ProductCountryDomain) request.getAttribute(PRODUCT_DOMAIN_REQUEST_KEY);
        else if (request.getAttribute(PRODUCT_DOMAIN_REQUEST_KEY) != null && request.getAttribute(PRODUCT_DOMAIN_REQUEST_KEY) instanceof String && ((String) request.getAttribute(PRODUCT_DOMAIN_REQUEST_KEY)).equals(PRODUCT_DOMAIN_REQUEST_NULL))
            return null;

        String requestDomain = utilService.getDomainOfRequest(request);
        Product product = catalogService.getProducts()
                .stream()
                .filter(c -> c.getProductCountryDomainByDomain(requestDomain) != null)
                .findFirst().orElse(null);
        ProductCountryDomain productCountryDomain = null;
        if (product != null) {
            productCountryDomain = product.getProductCountryDomainByDomain(requestDomain);
        }

        request.setAttribute(PRODUCT_DOMAIN_REQUEST_KEY, productCountryDomain != null ? productCountryDomain : PRODUCT_DOMAIN_REQUEST_NULL);

        return productCountryDomain;
    }
}

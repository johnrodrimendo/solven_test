package com.affirm.common.service;

import com.affirm.common.model.catalog.EntityBranding;
import com.affirm.common.model.catalog.ProductCategoryCountry;
import com.affirm.common.model.catalog.ProductCountryDomain;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jrodriguez on 11/07/16.
 */
public interface ProductService {
    Integer getMaxInstalments(Integer productCategoryId, int countryId, EntityBranding entityBranding) throws Exception;

    Integer getMinInstalments(Integer productCategoryId, int countryId, EntityBranding entityBranding) throws Exception;

    Integer getMaxAmount(Integer productCategoryId, int countryId, EntityBranding entityBranding) throws Exception;

    Integer getMinAmount(Integer productCategoryId, int countryId, EntityBranding entityBranding) throws Exception;

    Integer getMaxInstalmentsEntity(Integer productCategoryId, int countryId, int entityId) throws Exception;

    Integer getMinInstalmentsEntity(Integer productCategoryId, int countryId, int entityId) throws Exception;

    Integer getMaxAmountEntity(Integer productCategoryId, int countryId, int entityId) throws Exception;

    Integer getMinAmountEntity(Integer productCategoryId, int countryId, int entityId) throws Exception;

    ProductCategoryCountry getProductCategoryCountry(int categoryId, int countryId) throws Exception;

    Integer getMaxInstalments(Integer productCategoryId, Integer countryId) throws Exception;

    Integer getMinInstalments(Integer productCategoryId, Integer countryId) throws Exception;

    Integer getMaxAmount(Integer productCategoryId, Integer countryId) throws Exception;

    Integer getMinAmount(Integer productCategoryId, Integer countryId) throws Exception;

    ProductCountryDomain getProductDomainByRequest(HttpServletRequest request);
}

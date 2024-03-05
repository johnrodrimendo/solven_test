/**
 *
 */
package com.affirm.backoffice.dao;

import com.affirm.backoffice.model.*;
import com.affirm.backoffice.util.PaginationWrapper;
import com.affirm.common.service.CatalogService;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface BackofficeDAO {

    GeneralSearchResult generalSearch(String query, Locale locale) throws Exception;

    String getSharedSecret(String username);

    PaginationWrapper<ReportNoHolding> getNoHoldingReport(String country, Integer offset, Integer limit) throws Exception;

//    PaginationWrapper<ReportOrigination> getOriginationReport(String country, CatalogService catalogService, Locale locale, Date startDate, Date endDate, Integer offset, Integer limit) throws Exception;

//    List<ReportOrigination> getOriginationReport(String country, CatalogService catalogService, Locale locale, Date startDate, Date endDate) throws Exception;

//    List<ReportOrigination> getOriginationReport(String country, CatalogService catalogService, Locale locale, Date startDate, Date endDate) throws Exception;

    List<ReportCreditGateway> getCreditCollectionReport(String country, CatalogService catalogService, Locale locale, Date startDate, Date endDate) throws Exception;

    PaginationWrapper<ReportCreditGateway> getCreditCollectionReport(String country, CatalogService catalogService, Locale locale, Date startDate, Date endDate, Integer offset, Integer limit) throws Exception;

    List<OriginationReportPeriod> getOriginationProductReportPeriod(String country, Date period1From, Date period1To, Date period2From, Date period2To, Integer[] entities) throws Exception;

    List<OriginationReportPeriod> getOriginationEntityProductReportPeriod(String country, CatalogService catalogService) throws Exception;

    void updateSpeech(Integer entityProductParameterId, Integer speechTypeId, String speech);

}

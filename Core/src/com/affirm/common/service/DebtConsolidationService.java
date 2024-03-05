package com.affirm.common.service;


import com.affirm.common.model.transactional.ConsolidableDebt;

import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface DebtConsolidationService {

    List<ConsolidableDebt> getPersonConsolidableDebts(int personId, Locale locale) throws Exception;

    void registerconsolidation(List<ConsolidableDebt> consolidation, int loanApplicationId) ;

    void sendConsolidableAccountsEmail(int creditId, Locale locale, int interactionContentId, byte[] contractBytes) throws Exception;
}

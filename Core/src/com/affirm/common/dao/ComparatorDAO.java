package com.affirm.common.dao;

/**
 * Created by dev5 on 22/06/17.
 */
public interface ComparatorDAO {

    void registerBankProductRates(int bankId, String rates);

    void applyProductRates();

}

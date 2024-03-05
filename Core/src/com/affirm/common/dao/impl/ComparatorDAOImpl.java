package com.affirm.common.dao.impl;

import com.affirm.common.dao.ComparatorDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;

/**
 * Created by dev5 on 22/06/17.
 */
@Repository
public class ComparatorDAOImpl extends JsonResolverDAO implements ComparatorDAO {


    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public void registerBankProductRates(int bankId, String rates){
        queryForObjectTrx("select * from comparator.register_bank_product_rate(?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, bankId),
                new SqlParameterValue(Types.OTHER, rates));
    }

    @Override
    public void applyProductRates(){
        queryForObjectTrx("select * from comparator.apply_bank_product_rates_temp()", String.class);
    }

}

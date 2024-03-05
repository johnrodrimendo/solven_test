package com.affirm.common.dao.impl;

import com.affirm.common.dao.DebtConsolidationDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by dev5 on 22/06/17.
 */
@Repository
public class DebtConsolidationDAOImpl extends JsonResolverDAO implements DebtConsolidationDAO {


    @Autowired
    private CatalogService catalogService;

//    @Override
//    public Integer getConsolidableEntityByPerson(int docType, String docNumber) {
//        return queryForObject("select * from person.get_consosidable_person(?, ?)", Integer.class,
//                new SqlParameterValue(Types.INTEGER, docType),
//                new SqlParameterValue(Types.VARCHAR, docNumber));
//    }
}

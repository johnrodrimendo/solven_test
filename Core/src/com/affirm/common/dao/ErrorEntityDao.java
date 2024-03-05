package com.affirm.common.dao;

import com.affirm.common.model.EntityError;
import com.affirm.common.model.EntityErrorExtranetPainter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.List;

public interface ErrorEntityDao {

    List<EntityErrorExtranetPainter> getEntityErrors(int entityId, Date startDate, Date endDate, String search, Integer offset, Integer limit);

    Pair<Integer, Double> getEntityErrorsCount(Integer entityId, Date startDate, Date endDate,  String search);

    EntityErrorExtranetPainter getEntityErrorsById(int entityExtranetErrorId, int entityId);

    void addEntityError(int loanApplicationId, int entityId, int entityWsId, String error, Integer lgEntityWsId);

}

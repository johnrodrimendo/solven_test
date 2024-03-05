package com.affirm.common.service.impl;

import com.affirm.common.dao.ErrorEntityDao;
import com.affirm.common.model.EntityError;
import com.affirm.common.model.EntityErrorExtranetPainter;
import com.affirm.common.service.ErrorEntityService;
import com.affirm.common.util.JsonUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.Date;
import java.util.List;

@Service
public class ErrorEntityServiceImpl implements ErrorEntityService {

    @Autowired
    private ErrorEntityDao errorEntityDao;

    @Override
    public List<EntityErrorExtranetPainter> getEntityErrors(int entityId, Date startDate, Date endDate, String search, Integer offset, Integer limit) {
        return errorEntityDao.getEntityErrors(entityId, startDate, endDate, search, offset,limit);
    }

    @Override
    public Pair<Integer, Double> getEntityErrorsCount(Integer entityId, Date startDate, Date endDate,  String search) {
        return errorEntityDao.getEntityErrorsCount(entityId, startDate, endDate, search);
    }

    @Override
    public EntityErrorExtranetPainter getEntityErrorsById(int entityExtranetErrorId, int entityId) {
        return errorEntityDao.getEntityErrorsById(entityExtranetErrorId, entityId);
    }
}

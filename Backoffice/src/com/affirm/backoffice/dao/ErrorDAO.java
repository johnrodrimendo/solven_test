package com.affirm.backoffice.dao;

import com.affirm.backoffice.model.*;

import java.util.Date;
import java.util.List;

public interface ErrorDAO {
    List<ExceptionApp> getExceptions(int limit, int offset);

    List<RecurrentException> getRecurrentExceptions(int limit, int offset, Date startDate, Date endDate);

    List<ReportEntityWsStatus> getReportEntityWsStatus(int limit, int offset, Date startDate, Date endDate);

    List<ReportProcessByHour> getReportProcessByHour(Date startDate, Date endDate);

    List<EntityError> getEntityErrorsByEntityId(int entityId);
}

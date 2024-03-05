package com.affirm.tests.system.configuration.dao

import com.affirm.backoffice.dao.ErrorDAO
import com.affirm.backoffice.model.ExceptionApp
import com.affirm.backoffice.model.RecurrentException
import com.affirm.backoffice.model.ReportEntityWsStatus
import com.affirm.backoffice.model.ReportProcessByHour
import com.affirm.tests.BaseBoConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ErrorDAOTest extends BaseBoConfig {

    @Autowired
    ErrorDAO errorDAO

    static final Integer OFFSET = 10
    static final Integer LIMIT = 10
    static final Date START_DATE = new Date()
    static final Date END_DATE = new Date()

    @Test
    void getExceptionsFromErrorDAO() {
        List<ExceptionApp> list = errorDAO.getExceptions(LIMIT, OFFSET)
        Assert.assertNotNull(list)
    }

    @Test
    void getRecurrentExceptionsFromErrorDAO() {
        List<RecurrentException> list = errorDAO.getRecurrentExceptions(LIMIT, OFFSET, START_DATE, END_DATE)
        Assert.assertNotNull(list)
    }

    @Test
    void getReportEntityWsStatusFromErrorDAO() {
        List<ReportEntityWsStatus> list = errorDAO.getReportEntityWsStatus(LIMIT, OFFSET, START_DATE, END_DATE)
        Assert.assertNull(list)
    }

    @Test
    void getReportProcessByHourFromErrorDAO() {
        List<ReportProcessByHour> list = errorDAO.getReportProcessByHour(START_DATE, END_DATE)
        Assert.assertNotNull(list)
    }
}

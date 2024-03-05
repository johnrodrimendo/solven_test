package com.affirm.tests.dao

import com.affirm.common.dao.RccDAO
import com.affirm.common.model.transactional.*
import com.affirm.common.service.UtilService
import com.affirm.tests.BaseConfig
import org.apache.commons.lang.time.DateUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import java.text.SimpleDateFormat

class RccDAOTest extends BaseConfig {

    @Autowired
    private RccDAO rccDAO
    @Autowired
    private UtilService utilService

    private static final String DOCUMENT_NUMBER = '20263292341'

    @Test
    void shouldGetRccSynthesized() {
        List<RccSynthesized> rccSynthesizedList = rccDAO.getRccSynthesized(DOCUMENT_NUMBER)

        Assertions.assertNotNull(rccSynthesizedList)
    }

    @Test
    void shouldGetRccDates() {
        List<RccDate> rccDateList = rccDAO.getRccDates()

        Assertions.assertNotNull(rccDateList)
    }

    @Test
    void shouldGetRccScore() {
        RccScore rccScore = rccDAO.getRccScore(DOCUMENT_NUMBER)

        Assertions.assertNull(rccScore)
    }

    @Test
    void shouldGetRccIdeGrouped() {
        List<RccIdeGrouped> rccIdeGroupedList = rccDAO.getRccIdeGrouped(DOCUMENT_NUMBER)

        Assertions.assertNotNull(rccIdeGroupedList)
    }

    @Test
    void shouldGetCendeuDates() {
        List<CendeuDate> cendeuDateList = rccDAO.getCendeuDates()

        Assertions.assertNotNull(cendeuDateList)
    }

    @Test
    void shouldGetCendeuRejectedChecks() {
        Boolean cendeuRejectedChecks = rccDAO.getCendeuRejectedChecks(DOCUMENT_NUMBER)

        Assertions.assertNotNull(cendeuRejectedChecks)
    }

    @Test
    void shouldGetBancoDelSolBase() {
        List<BDSBase> bancoDeSolBaseList = rccDAO.getBancoDelSolBase(DOCUMENT_NUMBER)

        Assertions.assertNotNull(bancoDeSolBaseList)
    }

    @Test
    void getBanbifBaseValidUntil() {
        Date baseValidUntil = rccDAO.getBanbifBaseValidUntil();
        Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        if(currentDate.after(baseValidUntil) && utilService.daysBetween(baseValidUntil, currentDate) == 1){
            println "se debe limpiar la base"
        }else {
            println "No se debe limpiar la base"
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
        currentDate = DateUtils.truncate(sdf.parse("10/01/2023"), Calendar.DATE);
        if(currentDate.after(baseValidUntil) && utilService.daysBetween(baseValidUntil, currentDate) == 1){
            println "se debe limpiar la base"
        }else {
            println "No se debe limpiar la base"
        }
    }


}

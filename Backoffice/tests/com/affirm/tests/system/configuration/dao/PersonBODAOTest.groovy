package com.affirm.tests.system.configuration.dao

import com.affirm.backoffice.dao.PersonBODAO
import com.affirm.backoffice.model.LineaResultBoPainter
import com.affirm.common.model.transactional.EssaludResult
import com.affirm.common.model.transactional.MigracionesResult
import com.affirm.common.model.transactional.RedamResult
import com.affirm.common.model.transactional.ReniecResult
import com.affirm.common.model.transactional.SatPlateResult
import com.affirm.common.model.transactional.SatResult
import com.affirm.common.model.transactional.SisResult
import com.affirm.common.model.transactional.SoatRecordsResult
import com.affirm.tests.BaseBoConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class PersonBODAOTest extends BaseBoConfig{

    @Autowired
    PersonBODAO personBODAO

    @Test
    void getSoatRecordsFromPersonBoDAO() {
        List<SoatRecordsResult> soatRecordsResults = personBODAO.getSoatRecordsResults(7774)
        Assert.assertNotNull(soatRecordsResults)
    }

    @Test
    void getSatPlatetRecordsFromPersonBODAO() {
        List<SatPlateResult> satPlateResults = personBODAO.getSatPlateResult(7767)
        Assert.assertNotNull(satPlateResults)
    }

    @Test
    void getReniecResultFromPersonBODAO() {
        ReniecResult reniecResult = personBODAO.getReniecResult(2007)
        Assert.assertNotNull(reniecResult)
    }

    @Test
    void getEssaludResultFromPersonBODAO() {
        EssaludResult essaludResult = personBODAO.getEssaludResult(2007)
        Assert.assertNotNull(essaludResult)
    }

    @Test
    void getPhoneContractLineaResultFromPersonBODAO() {
        List<LineaResultBoPainter> lineaResultBoPainters = personBODAO.getPhoneContractLineaResult(2007)
        Assert.assertNotNull(lineaResultBoPainters)
    }

    @Test
    void getSatResultFromPersonBODAO() {
        SatResult satResult = personBODAO.getSatResult(2007)
        Assert.assertNotNull(satResult)
    }

    @Test
    void getSisResultFromPersonBODAO() {
        SisResult sisResult = personBODAO.getSisResult(2007)
        Assert.assertNull(sisResult)
    }

}

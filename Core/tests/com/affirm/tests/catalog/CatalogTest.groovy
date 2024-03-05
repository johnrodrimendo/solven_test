package com.affirm.tests.catalog

import com.affirm.common.service.CatalogService
import com.affirm.tests.BaseConfig
import groovy.transform.CompileStatic
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class CatalogTest extends BaseConfig {

    @Autowired
    CatalogService catalogService

    @Test
    void testProductParamsSuccess() {
        def entityParams = catalogService.getEntityProductParamById(8101)
        Assert.assertEquals "Ripley SEF", entityParams.entityProduct
    }

    @Test
    void testLeadProdSuccess() {
        def leadProductActivities = catalogService.getLeadsProductActivity() as List
        Assert.assertTrue leadProductActivities.size() > 0
        Assert.assertNotNull leadProductActivities
    }

    @Test
    void testLeadActivitySuccess() {
        def leadActivityTypes = catalogService.getLeadActivityTypes() as List
        Assert.assertTrue leadActivityTypes.size() > 0
        Assert.assertNotNull leadActivityTypes
    }

    @Test
    void testExtrantMenusSuccess() {
        def extranetMenus = catalogService.getExtranetMenu(1) as List
        Assert.assertNotNull extranetMenus
    }
}

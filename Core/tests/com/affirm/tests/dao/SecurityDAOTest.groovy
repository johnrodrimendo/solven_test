package com.affirm.tests.dao

import com.affirm.common.model.transactional.MatiResult
import com.affirm.security.dao.SecurityDAO
import com.affirm.security.model.EntityWsResult
import com.affirm.security.model.SysUser
import com.affirm.tests.BaseConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SecurityDAOTest extends BaseConfig {

    @Autowired
    SecurityDAO securityDAO

    static final String USERNAME = "occoa"
    static final String STACKTRACE = "java.lang.NullPointerException"
    static final String ATTACK_TYPE = "DDoS"
    static final String ATTACKER_IP = "10.15.12.250"
    static final Double IP_LATITUDE = -12.087796
    static final Double IP_LONGITUDE = -77.013877
    static final String DETAIL = "fsociety"
    static final Integer LOAN_APPLICATION_ID = 99999
    static final Integer ENTITY_WS = 98765
    static final String PERMISSION = ""

    @Test
    void registerSessionEmployerFromSecurityDAO() {
        SysUser result = securityDAO.getSysUser(USERNAME)
        Assert.assertNotNull(result)
    }

    @Test
    void registerExceptionFromSecurityDAO() {
        securityDAO.registerException(STACKTRACE)
    }

    @Test
    void registerExternalAttackFromSecurityDAO() {
        securityDAO.registerExternalAttack(ATTACK_TYPE, ATTACKER_IP, IP_LATITUDE, IP_LONGITUDE, DETAIL)
    }

    @Test
    void getEntityResultWSFromSecurityDAO() {
        EntityWsResult result = securityDAO.getEntityResultWS(LOAN_APPLICATION_ID, ENTITY_WS)
        Assert.assertNull(result)
    }

    @Test
    void getPermissionIdFromSecurityDAO() {
        Integer result = securityDAO.getPermissionId(PERMISSION)
        Assert.assertNull(result)
    }

    @Test
    void getMatiResultByLoanApp(){
        int loanId = 638339;

        List<MatiResult> matiResultList = securityDAO.getMatiResultsByLoanApplication(loanId);
        for (MatiResult matiResult : matiResultList){
            System.out.println(matiResult.getDocumentNumber());
        }

    }
}

package com.affirm.tests.dao

import com.affirm.common.dao.OriginatorDAO
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

class OriginatorDAOTest extends BaseConfig {

    @Autowired
    private OriginatorDAO originatorDAO

    @Test
    void shouldRegisterAffiliator() {
        String name = 'Daniel'
        String ruc = '10700618019'
        String email = 'dalvarez@solven.pe'
        String phoneNumber = '926105802'
        String ubigeoId = '040101'
        Integer bankId = 7
        String bankAccountNumber = '11111111111111111111'

        Executable executable = {
            originatorDAO.registerAffiliator(name, ruc, email, phoneNumber, ubigeoId, bankId, bankAccountNumber, null, null)
        }

        Assertions.assertDoesNotThrow(executable)
    }
}

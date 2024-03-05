package com.affirm.tests.dao

import com.affirm.common.dao.TranslatorDAO
import com.affirm.common.model.catalog.Entity
import com.affirm.common.model.catalog.IdentityDocumentType
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

class TranslatorDAOTest extends BaseConfig {

    @Autowired
    private TranslatorDAO translatorDAO

    @Test
    void name() {
        Executable executable = {
            translatorDAO.translate(Entity.COMPARTAMOS, 1, IdentityDocumentType.DNI.toString(), null)
        }

        Assertions.assertDoesNotThrow(executable)
    }
}

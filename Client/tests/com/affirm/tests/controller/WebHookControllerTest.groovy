package com.affirm.tests.controller

import com.affirm.common.dao.InteractionDAO
import com.affirm.tests.BaseClientConfig
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import java.sql.Timestamp

class WebHookControllerTest extends BaseClientConfig {

    @Autowired
    InteractionDAO interactionDao


    @Test
    @Disabled
    void interactionDaoInsertCurrentTimeStamp() {
        long timestamp = 1372339860
        interactionDao.insertPersonInteractionStatus(4868, '1372339860@ unit test', new Timestamp(timestamp*1000))
    }

    @Test
    @Disabled
    void interactionDaoInsertTimeStamp() {
        interactionDao.insertPersonInteractionStatus(4868, 'current@ unit test', new Timestamp(System.currentTimeMillis()))
    }
}

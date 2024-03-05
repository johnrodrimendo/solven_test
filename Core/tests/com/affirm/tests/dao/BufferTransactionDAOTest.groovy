package com.affirm.tests.dao

import com.affirm.common.dao.BufferTransactionDAO
import com.affirm.common.model.transactional.BufferTransaction
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

class BufferTransactionDAOTest extends BaseConfig {

    @Autowired
    private BufferTransactionDAO bufferTransactionDAO

    private static final Integer BUFFER_TRANSACTION_ID = 0
    private static final Integer SYS_USER_ID = 0

    @Test
    void shouldPendingBufferTransactions() {
        List<BufferTransaction> bufferTransactionList = bufferTransactionDAO.getPendingBufferTransactions()

        Assertions.assertNull(bufferTransactionList)
    }

    @Test
    void shouldPauseBufferTransactions() {
        boolean pause = true
        Executable pauseExecutable = {
            bufferTransactionDAO.pauseBufferTransactions(BUFFER_TRANSACTION_ID, SYS_USER_ID, pause)
        }

        boolean unpause = true
        Executable unpauseExecutable = {
            bufferTransactionDAO.pauseBufferTransactions(BUFFER_TRANSACTION_ID, SYS_USER_ID, unpause)
        }

        Assertions.assertDoesNotThrow(pauseExecutable)
        Assertions.assertDoesNotThrow(unpauseExecutable)
    }

    @Test
    void shouldDeleteBufferTransactions() {
        Executable executable = { bufferTransactionDAO.deleteBufferTransactions(BUFFER_TRANSACTION_ID) }

        Assertions.assertDoesNotThrow(executable)
    }

}
